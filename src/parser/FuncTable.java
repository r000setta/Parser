package parser;

import inter.Func;

import java.util.HashMap;
import java.util.Map;

public class FuncTable {
    private Map<String, Func> map;

    public FuncTable() {
        this.map = new HashMap<>();
    }

    public Map<String, Func> getMap() {
        return map;
    }

    boolean isExist(String name){
        return map.get(name)!=null;
    }

    Func getFunc(String name){
        return map.get(name);
    }
}
