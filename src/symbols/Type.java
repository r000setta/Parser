package symbols;

import lexer.Tag;
import lexer.Word;

public class Type extends Word {

    //宽度,用于存储分配
    public int width;

    public Type(String s, int tag, int w) {
        super(s, tag);
        width = w;
    }

    public static final Type
            Int = new Type("int", Tag.BASIC, 4),
            Float = new Type("float", Tag.BASIC, 8),
            Char = new Type("char", Tag.BASIC, 1),
            Bool = new Type("boolean", Tag.BASIC, 1),
            Void = new Type("void", Tag.BASIC, 1);

    public static boolean numeric(Type p) {
        return p == Type.Char || p == Type.Int || p == Type.Float;
    }

    //两个数字单位运算时，可以进行相互转换
    public static Type max(Type p1, Type p2) {
        if (!numeric(p1) || !numeric(p2)) {
            return null;
        } else if (p1 == Type.Float || p2 == Type.Float) {
            return Type.Float;
        } else if (p1 == Type.Int || p2 == Type.Int) {
            return Type.Int;
        }
        return Type.Char;
    }
}
