package inter;

import config.IREmit;
import lexer.Lexer;

public class Node {
    private int lexLine;

    Node() {
        lexLine = Lexer.line;
    }

    void error(String msg) {
        throw new Error("near line " + lexLine + ": " + msg);
    }

    /**
     * 生成三地址代码
     */
    private static int labels = 0;

    public int newLabel() {
        return ++labels;
    }

    public void emitLabel(int i) {
        IREmit.emit("L" + i + ":");
    }

    public void emit(String s) {
        System.out.println("\t" + s);
    }
}
