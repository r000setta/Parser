package inter;

import lexer.Token;
import symbols.Type;

public class Expr extends Node {
    public Token op;
    public Type type;

    Expr(Token tok, Type p) {
        op = tok;
        type = p;
    }

    //返回一个项，可组成三地址指令的右部
    public Expr gen() {
        return this;
    }

    public Expr reduce() {
        return this;
    }

    /**
     * 布尔表达式代码生成
     *
     * @param t:true出口
     * @param f:false出口
     */
    public void jumping(int t, int f) {
        emitJumps(toString(), t, f);
    }

    public void emitJumps(String test, int t, int f) {
        if (t != 0 && f != 0) {
            emit("if " + test + " goto L" + t);
            emit("goto L" + f);
        } else if (t != 0) {
            emit("if " + test + " goto L" + t);
        } else if (f != 0) {
            emit("ifFalse " + test + " goto L" + f);
        }
    }

    @Override
    public String toString() {
        return op.toString();
    }
}
