package parser;

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

    private void emit(String s) {
        System.out.println(s);
    }

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
        s.emitLabel(begin);
        s.gen(begin, after);
        //s.emitLabel(after);
    }

    private Stmt block() throws IOException {
        match('{');
        Env savedEnv = top; //存放最顶层的符号表
        top = new Env(top);
        emit("Declaration:");
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
                    id.emit("decl " + p + " " + id);
                case Tag.EXTERN:
                    Func func = func();
                    if (funcTable.getMap().putIfAbsent(func.getName(), func) != null) {
                        error("The extern function has been defined");
                    }
                    emit("\tdecl extfun " + func.getName());
            }
        }
    }

    private Func func() throws IOException {
        move();
        Type type = type();
        Func func = new Func(Tag.EXTERN, look.toString(), true, type);
        move();
        match('(');
        Arg[] args = args();
        func.setArgNum(args.length);
        func.setArgs(args);
        match(')');
        match(';');
        return func;
    }

    private Arg[] args() throws IOException {
        List<Arg> list = new ArrayList<>();
        while (look.tag == Tag.BASIC) {
            Type p = type();
            Token tok = look;
            match(Tag.ID);
            list.add(new Arg(p, tok.toString()));
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
