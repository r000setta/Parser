package inter;

import config.IREmit;
import lexer.Token;
import symbols.Type;

public class Op extends Expr {
    public Op(Token tok, Type p){
        super(tok, p);
    }

    @Override
    public Expr reduce() {
        Expr x=gen();
        Temp t=new Temp(type);
        IREmit.emit(t.toString()+" = "+x.toString());
        return t;
    }
}
