package inter;

import symbols.Type;

/**
 * 函数参数
 */
public class Arg {

    private Type type;

    private String name;

    public Arg(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Arg(Type type){
        this(type,"#");
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
