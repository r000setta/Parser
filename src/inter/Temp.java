package inter;

import lexer.Word;
import symbols.Type;

public class Temp extends Expr {
    private static int count;
    private int number;

    public Temp(Type p) {
        super(Word.temp, p);
        number = ++count;
    }

    @Override
    public String toString() {
        return "t" + number;
    }
}
