package inter;

import lexer.Token;
import symbols.Type;

public class Func extends Token {
    private Type ret;

    private String name;

    private boolean isExtern;

    private Arg[] args;

    private int argNum;

    public Func(int tag, String name, boolean isExtern, Type ret) {
        super(tag);
        this.name = name;
        this.isExtern = isExtern;
        this.ret = ret;
    }

    public Type getRet() {
        return ret;
    }

    public void setRet(Type ret) {
        this.ret = ret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExtern() {
        return isExtern;
    }

    public void setExtern(boolean extern) {
        isExtern = extern;
    }

    public Arg[] getArgs() {
        return args;
    }

    public void setArgs(Arg[] args) {
        this.args = args;
    }

    public int getArgNum() {
        return argNum;
    }

    public void setArgNum(int argNum) {
        this.argNum = argNum;
    }
}
