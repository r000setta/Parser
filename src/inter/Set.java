package inter;

import config.IREmit;
import symbols.Type;

public class Set extends Stmt {
    public Id id;
    public Expr expr;

    //构造节点
    public Set(Id i, Expr x) {
        id = i;
        expr = x;
        if (check(id.type, expr.type) == null) {
            error("type error");
        }
    }

    //类型检查
    public Type check(Type p1, Type p2) {
        if (Type.numeric(p1) && Type.numeric(p2)) {
            return p2;
        } else if (p1 == Type.Bool && p2 == Type.Bool) {
            return p2;
        } else {
            return null;
        }
    }

    @Override
    public void gen(int b, int a) {
        IREmit.emit(id.toString() + " = " + expr.gen().toString());
    }
}
