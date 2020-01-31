package inter;

import config.IREmit;

public class Break extends Stmt {
    Stmt stmt;

    public Break() {
        if (Stmt.Enclosing == Stmt.Null) {
            error("unenclosed break");
        }
    }

    @Override
    public void gen(int b, int a) {
        IREmit.emit("goto L"+stmt.after);
    }
}
