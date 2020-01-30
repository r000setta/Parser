package symbols;

import lexer.Tag;

public class Array extends Type {
    public Type of; //数组元素类型
    public int size = 1;    //默认大小

    public Array(int sz, Type p) {
        super("[]", Tag.INDEX, sz * p.width);
        size = sz;
        of = p;
    }

    @Override
    public String toString() {
        return "[" + size + "]" + of.toString();
    }

}
