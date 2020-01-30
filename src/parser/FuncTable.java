package parser;

import inter.Func;

import java.util.HashMap;
import java.util.Map;

public class FuncTable {
    private Map<String, Func> map;

    FuncTable() {
        this.map = new HashMap<>();
    }

    public Map<String, Func> getMap() {
        return map;
    }
}
