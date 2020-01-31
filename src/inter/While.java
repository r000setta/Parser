package inter;

import config.IREmit;
import symbols.Type;

public class While extends Stmt{
    Expr expr;Stmt stmt;
    public While(){
        expr=null;
        stmt=null;
    }

    public void init(Expr x,Stmt s){
        expr=x;
        stmt=s;
        if (expr.type!= Type.Bool){
            expr.error("boolean required in while");
        }
    }

    @Override
    public void gen(int b, int a) {
        after=a;
        expr.jumping(0,a);
        int label= newLabel();
        emitLabel(label);
        stmt.gen(label,b);
        IREmit.emit("goto L"+b);
    }
}
