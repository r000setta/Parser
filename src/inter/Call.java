package inter;

import config.IREmit;

/**
 * 处理函数调用
 */
public class Call extends Stmt {

    private Func callee;

    private Func caller;

    private Arg[] args;

    private boolean isExtern;

    public Call(Func callee, Arg[] args, boolean isExtern) {
        this.callee = callee;
        this.args = args;
        this.isExtern = isExtern;
    }

    @Override
    public void gen(int b, int a) {
        if (args.length != 0) {
            for (Arg arg : args) {
                IREmit.emit("param " + arg.getName());
            }
        }
        if (isExtern) {
            IREmit.emit("Call extern func " + callee.getName());
        }
    }
}
