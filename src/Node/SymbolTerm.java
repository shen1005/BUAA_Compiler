package Node;

import WordAnalysis.Word;

import java.util.ArrayList;

public class SymbolTerm {
    private String name;
    private String type;
    private int value = 0;
    private int dim;
    private int length1;
    private int length2;
    private ArrayList<Integer> length1Value;
    private ArrayList<ArrayList<Integer>> length2Value;
    private int line = 0;
    private int size;
    private int offset_sp = 4;
    private int offset_gp = 0;
    private boolean isConst = false;
    private boolean isParam = false;

    public SymbolTerm(String name, int dim, int length1, ArrayList<Integer> length1Value) {
        this.name = name;
        this.dim = dim;
        this.length1 = length1;
        this.length1Value = length1Value;
        length2Value = new ArrayList<ArrayList<Integer>>();
    }

    public SymbolTerm(String name, int dim, int length1, int length2, ArrayList<ArrayList<Integer>> length2Value) {
        this.name = name;
        this.dim = dim;
        this.length1 = length1;
        this.length2 = length2;
        this.length2Value = length2Value;
    }

    public SymbolTerm(String name, int dim, int value) {
        this.name = name;
        this.dim = dim;
        this.value = value;
        this.length1 = 0;
        this.length2 = 0;
        length1Value = new ArrayList<Integer>();
        length2Value = new ArrayList<ArrayList<Integer>>();
        this.type = "int";
    }

    public SymbolTerm(String name, int dim, int length1, int length2) {
        this.name = name;
        this.dim = dim;
        this.length1 = length1;
        this.length2 = length2;
        length1Value = new ArrayList<Integer>();
        length2Value = new ArrayList<ArrayList<Integer>>();
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public ArrayList<Integer> getLength1Value() {
        return length1Value;
    }

    public ArrayList<ArrayList<Integer>> getLength2Value() {
        return length2Value;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public void setLength2(int length2) {
        this.length2 = length2;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getLength1() {
        return length1;
    }

    public int getLength2() {
        return length2;
    }

    public int getDim() {
        return dim;
    }

    public int getSize() {
        return size;
    }

    public void setLength1(int length1) {
        this.length1 = length1;
    }

    public void setSize(boolean isParam) {
        if (isParam) {
            size = 4;
        } else {
            if (dim == 0) {
                size = 4;
            } else if (dim == 1) {
                size = 4 * length1;
            } else if (dim == 2) {
                size = 4 * length1 * length2;
            }
        }
    }

    public int getOffset_sp() {
        return offset_sp;
    }

    public void setOffset_sp(int offset_sp) {
        this.offset_sp = offset_sp;
    }

    public void setAddressSp() {
        offset_sp = SymbolTable.getOffset_sp();
        SymbolTable.setOffset_sp(offset_sp + size);
    }

    public void setAddressGp() {
        offset_gp = SymbolTable.getOffset_gp();
        SymbolTable.setOffset_gp(offset_gp + size);
    }

    public int getOffset_gp() {
        return offset_gp;
    }

    @Override
    public String toString() {
        return "SymbolTerm{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", dim=" + dim +
                ", length1=" + length1 +
                ", length2=" + length2 +
                ", length1Value=" + length1Value +
                ", length2Value=" + length2Value +
                ", line=" + line +
                ", size=" + size +
                ", offset_sp=" + offset_sp +
                ", offset_gp=" + offset_gp +
                ", isConst=" + isConst +
                ", isParam=" + isParam +
                '}';
    }

    public void setConst(boolean isConst) {
        this.isConst = isConst;
    }

    public boolean getIsConst() {
        return isConst;
    }

    public void setParam() {
        isParam = true;
    }

    public boolean getIsParam() {
        return isParam;
    }

    public String getRealName() {
        if (name.charAt(name.length() - 1) == 'A') {
            return name;
        } else {
            return name + "_" + line;
        }
    }
}
