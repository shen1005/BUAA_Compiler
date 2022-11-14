package Node;

import java.util.ArrayList;

public class FuncDel {
    private String name;
    private String type;
    private int paraNum;
    private ArrayList<Integer> paraType;

    public FuncDel(String name, String type, int paraNum, ArrayList<Integer> paraType) {
        this.name = name;
        this.type = type;
        this.paraNum = paraNum;
        this.paraType = paraType;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getParaNum() {
        return paraNum;
    }

    public ArrayList<Integer> getParaType() {
        return paraType;
    }

}
