package lexer;


public class Word extends Token {
    public String lexeme;

    public Word(String s, int tag) {
        super(tag);
        lexeme = s;
    }

    @Override
    public String toString() {
        return lexeme;
    }

    public static final Word
            and = new Word("&&", Tag.AND), or = new Word("||", Tag.OR),
            eq = new Word("==", Tag.EQ), ne = new Word("!=", Tag.NE),
            le = new Word("<=", Tag.LE), ge = new Word(">=", Tag.GE),
            minus = new Word("minus", Tag.MINUS),
            True = new Word("true", Tag.TRUE),
            False = new Word("false", Tag.FALSE),
            temp = new Word("t", Tag.TEMP);

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Word))
            return false;
        Word that = (Word) obj;
        return lexeme.equals(that.lexeme) && tag == that.tag;
    }

    @Override
    public int hashCode() {
        return lexeme.hashCode() + tag;
    }
}
