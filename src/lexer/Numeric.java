package lexer;

public class Numeric extends Token {
    public final int value;

    public Numeric(int value) {
        super(Tag.NUM);
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
