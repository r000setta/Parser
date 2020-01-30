package symbols;

import inter.Id;
import lexer.Token;

import java.util.Hashtable;
import java.util.Map;

public class Env {
    private Map<Token, Id> table;
    protected Env prev;

    public Env(Env n) {
        table = new Hashtable<>();
        prev = n;
    }

    public Object put(Token w, Id i) {
        return table.putIfAbsent(w,i);
    }

    public Id get(Token w) {
        for (Env e = this; e != null; e = e.prev) {
            Id found = e.table.get(w);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
