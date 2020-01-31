package inter;

import config.IREmit;
import lexer.Numeric;
import lexer.Token;
import lexer.Word;
import symbols.Type;

public class Constant extends Expr {
    public Constant(Token tok, Type p) {
        super(tok, p);
    }

    public Constant(int i) {
        super(new Numeric(i), Type.Int);
    }

    public static final Constant
            True = new Constant(Word.True, Type.Bool),
            False = new Constant(Word.False, Type.Bool);

    @Override
    public void jumping(int t, int f) {
        if (this == True && t != 0) {
            IREmit.emit("goto L" + t);
        } else if (this == False && f != 0) {
            IREmit.emit("goto L" + f);
        }
    }
}
