package parser;

import config.IREmit;
import inter.*;
import lexer.*;
import symbols.Array;
import symbols.Env;
import symbols.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 递归下降。对每个终结符都有一个过程
 */
public class Parser {


    private Lexer lex;  //词法分析器
    private Token look; //前看符号
    private Env top = null; //顶层符号表
    private int used = 0;   //声明变量的存储位置
    private FuncTable funcTable;

    public Parser(Lexer l) throws IOException {
        lex = l;
        move();
        funcTable = new FuncTable();
    }

    void move() throws IOException {
        look = lex.scan();
    }

    private void error(String s) {
        throw new Error("near line " + Lexer.line + ": " + s);
    }

    private void match(int t) throws IOException {
        if (look.tag == t) {
            move();
        } else {
            error("syntax error!");
        }
    }

    public void program() throws IOException {
        Stmt s = block();
        int begin = s.newLabel();
        int after = s.newLabel();
        //s.emitLabel(begin);
        s.gen(begin, after);
        //s.emitLabel(after);
        if (IREmit.isFile)
            IREmit.close();
    }

    private Stmt block() throws IOException {
        match('{');
        Env savedEnv = top; //存放最顶层的符号表
        top = new Env(top);
        decls();    //处理声明
        Stmt s = stmts();
        match('}');
        top = savedEnv;
        return s;
    }

    private void decls() throws IOException {
        while (look.tag == Tag.BASIC || look.tag == Tag.EXTERN) {
            switch (look.tag) {
                case Tag.BASIC:
                    Type p = type();
                    Token tok = look;
                    match(Tag.ID);
                    match(';');
                    Id id = new Id((Word) tok, p, used);
                    if (top.put(tok, id) != null) {
                        error("The variable " + id + " has been declared!");
                    }
                    used = used + p.width;
                    IREmit.emit("decl " + id + ":" + p);
                    break;
                case Tag.EXTERN:
                    Func func = func();
                    if (funcTable.getMap().putIfAbsent(func.getName(), func) == null) {
                        lex.getFuncTable().getMap().put(func.getName(), func);
                    } else {
                        error("The extern function has been defined");
                    }
                    match(';'); //
                    IREmit.emit("extern " + func.getName() + ":" + func.getRet() + " ");
                    if (func.getArgNum() != 0) {
                        for (Arg arg : func.getArgs())
                            IREmit.emit(arg.getName() + ":" + arg.getType());
                    }
            }
        }
    }

    private Func func() throws IOException {
        move();
        Type type = type();
        Func func = new Func(Tag.FUNC, look.toString(), true, type);
        move();
        match('(');
        Arg[] args = defArgs();
        func.setArgNum(args.length);
        func.setArgs(args);
        match(')');
        return func;
    }

    private Arg[] defArgs() throws IOException {
        List<Arg> list = new ArrayList<>();
        while (look.tag == Tag.BASIC) {
            Type p = type();
            Token tok = look;
            //match(Tag.ID);
            try {
                match(',');
            } catch (Error e) {
                if (look.tag == ')') {

                }
            }
            list.add(new Arg(p));
        }
        return list.toArray(new Arg[list.size()]);
    }

    private Type type() throws IOException {
        Type p = (Type) look;
        match(Tag.BASIC);
        if (look.tag != '[') {
            return p;
        } else {
            return dims(p);
        }
    }

    private Type dims(Type p) throws IOException {
        match('[');
        Token tok = look;
        match(Tag.NUM);
        match(']');
        if (look.tag == '[') {
            p = dims(p);
        }
        return new Array(((Numeric) tok).value, p);
    }

    private Stmt stmts() throws IOException {
        if (look.tag == '}') {
            return Stmt.Null;
        } else {
            return new Seq(stmt(), stmts());
        }
    }

    private Stmt call() throws IOException {
        Func func = (Func) look;
        match(Tag.FUNC);
        if (func.getRet() != Type.Void) {
            error("syntax error");
        }
        match('(');
        Arg[] args = args();
        checkArgs(args, func);
        match(')');
        if (func.isExtern()) {
            return new Call(func, args, func.isExtern());
        }
        return null;
    }

    private Arg[] args() throws IOException {
        List<Arg> list = new ArrayList<>();
        while (look.tag == Tag.ID) {
            Word word = (Word) look;
            if (!top.isExist(word.lexeme))
                error("undefined variable " + word.lexeme);
            Type type = top.get(word).type;
            list.add(new Arg(type, word.lexeme));
            move();
            if (look.tag == ',') {
                move();
                continue;
            } else {
                break;
            }
        }
        return list.toArray(new Arg[list.size()]);
    }

    private void checkArgs(Arg[] args, Func func) {
        Arg[] funcArg = func.getArgs();
        if (args.length != funcArg.length)
            error("Argument length error!");
        for (int i = 0; i < args.length; i++) {
            if (funcArg[i].getType() != args[i].getType()) {
                error("Argument type error!");
            }
            if (!top.isExist(args[i].getName())) {
                error("unknown variable " + args[i].getName());
            }
        }
    }

    private Stmt stmt() throws IOException {
        Expr x;
        Stmt s1, s2;
        Stmt savedStmt;
        switch (look.tag) {
            case ';':
                move();
                return Stmt.Null;
            case Tag.IF:
                match(Tag.IF);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                if (look.tag != Tag.ELSE) {
                    return new If(x, s1);
                }
                s2 = stmt();
                return new Else(x, s1, s2);
            case Tag.WHILE:
                While whileNode = new While();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = whileNode;
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                whileNode.init(x, s1);
                Stmt.Enclosing = savedStmt;
                return whileNode;
            case Tag.DO:
                Do doNode = new Do();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = doNode;
                match(Tag.DO);
                s1 = stmt();
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                match(';');
                doNode.init(s1, x);
                Stmt.Enclosing = savedStmt;
                return doNode;
            case Tag.BREAK:
                match(Tag.BREAK);
                match(';');
                return new Break();
            case '{':
                return block();
            case Tag.FUNC:
                return call();
            default:
                return assign();
        }
    }

    private Stmt assign() throws IOException {
        Stmt stmt;
        Token t = look;
        match(Tag.ID);
        Id id = top.get(t);
        if (id == null) {
            error("variable " + t.toString() + " undeclared");
        }
        if (look.tag == '=') {
            move();
            stmt = new Set(id, bool());
        } else {
            Access x = offset(id);
            match('=');
            stmt = new SetElem(x, bool());
        }
        match(';');
        return stmt;
    }

    private Expr bool() throws IOException {
        Expr x = join();
        while (look.tag == Tag.OR) {
            Token tok = look;
            move();
            x = new Or(tok, x, join());
        }
        return x;
    }

    private Expr join() throws IOException {
        Expr x = equality();
        while (look.tag == Tag.AND) {
            Token tok = look;
            move();
            x = new And(tok, x, equality());
        }
        return x;
    }

    private Expr equality() throws IOException {
        Expr x = rel();
        while (look.tag == Tag.EQ || look.tag == Tag.NE) {
            Token tok = look;
            move();
            x = new Rel(tok, x, rel());
        }
        return x;
    }

    private Expr rel() throws IOException {
        Expr x = expr();
        switch (look.tag) {
            case '<':
            case Tag.LE:
            case Tag.GE:
            case '>':
                Token tok = look;
                move();
                return new Rel(tok, x, expr());
            default:
                return x;
        }
    }

    private Expr expr() throws IOException {
        Expr x = term();
        while (look.tag == '+' || look.tag == '-') {
            Token tok = look;
            move();
            x = new Arith(tok, x, term());
        }
        return x;
    }

    private Expr term() throws IOException {
        Expr x = unary();
        while (look.tag == '*' || look.tag == '/') {
            Token tok = look;
            move();
            x = new Arith(tok, x, term());
        }
        return x;
    }

    private Expr unary() throws IOException {
        if (look.tag == '-') {
            move();
            return new Unary(Word.minus, unary());
        } else if (look.tag == '!') {
            Token tok = look;
            move();
            return new Not(tok, unary());
        } else {
            return factor();
        }
    }

    private Expr factor() throws IOException {
        Expr x = null;
        switch (look.tag) {
            case '(':
                move();
                x = bool();
                match(')');
                return x;
            case Tag.NUM:
                x = new Constant(look, Type.Int);
                move();
                return x;
            case Tag.REAL:
                x = new Constant(look, Type.Float);
                move();
                return x;
            case Tag.TRUE:
                x = Constant.True;
                move();
                return x;
            case Tag.FALSE:
                x = Constant.False;
                move();
                return x;
            default:
                error("syntax error");
                return x;
            case Tag.ID:
                String s = look.toString();
                Id id = top.get(look);
                if (id == null) {
                    error(look.toString() + " undeclared");
                }
                move();
                if (look.tag != '[') {
                    return id;
                } else {
                    return offset(id);
                }
        }
    }

    private Access offset(Id a) throws IOException {
        Expr i, w, t1, t2, loc;
        Type type = a.type;
        match('[');
        i = bool();
        match(']');
        type = ((Array) type).of;
        w = new Constant(type.width);
        t1 = new Arith(new Token('*'), i, w);
        loc = t1;
        while (look.tag == '[') {
            match('[');
            i = bool();
            match(']');
            type = ((Array) type).of;
            w = new Constant(type.width);
//            t1 = new Arith(new Token('*'), i, w);
            t2 = new Arith(new Token('+'), i, w);
            loc = t2;
        }
        return new Access(a, loc, type);
    }
}
