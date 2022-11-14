package Node;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    //name 维数
    private final HashMap<String, Integer> table = new HashMap<>();
    private HashMap<String, String> typeTable = new HashMap<>();
    private HashMap<String, Integer> constTable = new HashMap<>();
    private SymbolTable father = null;
    private ArrayList<SymbolTable> sons = new ArrayList<>();
    private ArrayList<SymbolTerm> symbolTerms = new ArrayList<>();
    private int nowSon = 0;
    private static int offset_sp = 4;
    private static int offset_gp = -0x8000;
    private static int stackSize = 0;

    public SymbolTable() {
    }

    public SymbolTable(SymbolTable father) {
        this.father = father;
    }

    public void addSon(SymbolTable son) {
        sons.add(son);
    }

    public void addSymbol(String name, int type) {
        table.put(name, type);
    }

    public HashMap<String, Integer> getConstTable() {
        return constTable;
    }

    public HashMap<String, Integer> getTable() {
        return table;
    }

    public SymbolTable getFather() {
        return father;
    }

    public HashMap<String, String> getTypeTable() {
        return typeTable;
    }

    public void addTerm(SymbolTerm symbolTerm) {
        symbolTerms.add(symbolTerm);
    }

    public int getValue(String name, SymbolTable temp) {
        SymbolTable now = temp;
        while (now != null) {
            for (SymbolTerm symbolTerm : now.symbolTerms) {
                if (symbolTerm.getName().equals(name)) {
                    return symbolTerm.getValue();
                }
            }
            now = now.father;
        }
        return -1;
    }

    public int get1DValue(String name, int index, SymbolTable temp) {
        SymbolTable now = temp;
        while (now != null) {
            for (SymbolTerm symbolTerm : now.symbolTerms) {
                if (symbolTerm.getName().equals(name)) {
                    if (symbolTerm.getLength1Value() == null || symbolTerm.getLength1Value().size() <= index) {
                        return 0;
                    }
                    return symbolTerm.getLength1Value().get(index);
                }
            }
            now = now.father;
        }
        return -1;
    }

    public int get2DValue(String name, int index1, int index2, SymbolTable temp) {
        SymbolTable now = temp;
        while (now != null) {
            for (SymbolTerm symbolTerm : now.symbolTerms) {
                if (symbolTerm.getName().equals(name)) {
                    if (symbolTerm.getLength2Value() == null || symbolTerm.getLength2Value().size() <= index1) {
                        return 0;
                    } else if (symbolTerm.getLength2Value().get(index1).size() <= index2) {
                        return 0;
                    }
                    return symbolTerm.getLength2Value().get(index1).get(index2);
                }
            }
            now = now.father;
        }
        return -1;
    }

    public int getDim(String name) {
        SymbolTable now = this;
        while (now != null) {
            for (SymbolTerm son: now.symbolTerms) {
                if (son.getName().equals(name)) {
                    return son.getDim();
                }
            }
            now = now.father;
        }
        return -1;
    }

    public void setZero() {
        this.nowSon = 0;
        for (SymbolTable son : sons) {
            son.setZero();
        }
    }

    public SymbolTable getNextSon() {
        if (nowSon < sons.size()) {
            return sons.get(nowSon++);
        } else {
            return null;
        }
    }

    public String getNowName(String name) {
        SymbolTable now = this;
        while (now != null) {
            for (SymbolTerm symbolTerm : now.symbolTerms) {
                if (symbolTerm.getName().equals(name)) {
                    if (now.father == null) {
                        return symbolTerm.getName() + "_-1";
                    }
                        return symbolTerm.getName() + "_" + symbolTerm.getLine();
                }
            }
            now = now.father;
        }
        return "114514";
    }

    public ArrayList<Integer> getAllValue(String name) {
        SymbolTable now = this;
        while (now != null) {
            for (SymbolTerm symbolTerm : now.symbolTerms) {
                if (symbolTerm.getName().equals(name)) {
                    int dim = symbolTerm.getDim();
                    if (dim == 0) {
                        return getAll0DValue(name);
                    } else if (dim == 1) {
                        return getAll1DValue(name);
                    } else if (dim == 2) {
                        return getAll2DValue(name);
                    }
                }
            }
            now = now.father;
        }
        return null;
    }

    public ArrayList<Integer> getAll1DValue(String name) {
        SymbolTable now = this;
        while (now != null) {
            for (SymbolTerm symbolTerm : now.symbolTerms) {
                if (symbolTerm.getName().equals(name)) {
                    ArrayList<Integer> ans = new ArrayList<>();
                    for (int i = 0; i < symbolTerm.getLength1(); i++) {
                       if (symbolTerm.getLength1Value().size() > i) {
                           ans.add(symbolTerm.getLength1Value().get(i));
                       } else {
                           ans.add(0);
                       }
                    }
                    return ans;
                }
            }
            now = now.father;
        }
        return null;
    }

    public ArrayList<Integer> getAll2DValue(String name) {
        SymbolTable now = this;
        while (now != null) {
            for (SymbolTerm symbolTerm : now.symbolTerms) {
                if (symbolTerm.getName().equals(name)) {
                    ArrayList<Integer> ans = new ArrayList<>();
                    for (int i = 0; i < symbolTerm.getLength1(); i++) {
                        for (int j = 0; j < symbolTerm.getLength2(); j++) {
                            if (symbolTerm.getLength2Value().size() > i) {
                                if (symbolTerm.getLength2Value().get(i).size() > j) {
                                    ans.add(symbolTerm.getLength2Value().get(i).get(j));
                                } else {
                                    ans.add(0);
                                }
                            } else {
                                ans.add(0);
                            }
                        }
                    }
                    return ans;
                }
            }
            now = now.father;
        }
        return null;
    }

    public ArrayList<Integer> getAll0DValue(String name) {
        SymbolTable now = this;
        while (now != null) {
            for (SymbolTerm symbolTerm : now.symbolTerms) {
                if (symbolTerm.getName().equals(name)) {
                    ArrayList<Integer> ans = new ArrayList<>();
                    ans.add(symbolTerm.getValue());
                    return ans;
                }
            }
            now = now.father;
        }
        return null;
    }

    //实际需要多少空间
    public int getSize() {
        int size = 0;
        SymbolTable now = this;
        for (SymbolTable son : now.sons) {
            size += son.getSize();
        }
        for (SymbolTerm symbolTerm : now.symbolTerms) {
            size += symbolTerm.getSize();
        }
        return size;
    }

    public static int getOffset_sp() {
        return SymbolTable.offset_sp;
    }

    public static void setOffset_sp(int offset_sp) {
        SymbolTable.offset_sp = offset_sp;
    }

    public static void resetSp() {
        SymbolTable.offset_sp = 4;
    }

    public void setAddressSp() {
        for (int i = 0; i < symbolTerms.size(); i++) {
            symbolTerms.get(i).setAddressSp();
        }
        for (SymbolTable symbolTable: sons) {
            symbolTable.setAddressSp();
        }
    }

    public static int getOffset_gp() {
        return SymbolTable.offset_gp;
    }

    public static void setOffset_gp(int offset_gp) {
        SymbolTable.offset_gp = offset_gp;
    }

    public static void resetGp() {
        SymbolTable.offset_gp =-0x8000;
    }

    @Override
    public String toString() {
        String ans = "";
        for (SymbolTerm symbolTerm : symbolTerms) {
            ans += symbolTerm.toString() + "\n";
        }
        for (SymbolTable son : sons) {
            ans += son.toString();
        }
        return ans;
    }

    public int getStackSize() {
        return stackSize;
    }

    public static void addStackSize(int size) {
        SymbolTable.stackSize += size;
    }

    public static void subStackSize(int size) {
        SymbolTable.stackSize -= size;
    }

    public boolean isGlobal(String preName) {
        String name;
        int line = 0;
        boolean isReal = false;
        if (isNum(preName)) {
            name = preName;
        } else if (preName.charAt(preName.length() - 1) != 'A') {
            line = Integer.parseInt(preName.substring(preName.lastIndexOf('_') + 1, preName.length()));
            isReal = true;
            name = preName.substring(0, preName.lastIndexOf('_'));
        } else {
            name = preName;
        }
        if (line == -1) {
            return true;
        }
        return false;
    }

    public SymbolTerm getSymbolTerm(String preName) {
        String name;
        int line = 0;
        boolean isReal = false;
        if (isNum(preName)) {
            name = preName;
        } else if (preName.charAt(preName.length() - 1) != 'A') {
            line = Integer.parseInt(preName.substring(preName.lastIndexOf('_') + 1, preName.length()));
            isReal = true;
            name = preName.substring(0, preName.lastIndexOf('_'));
        } else {
            name = preName;
        }
        if (!isReal) {
            SymbolTable now = this;
            while (now != null) {
                for (SymbolTerm symbolTerm : now.symbolTerms) {
                    if (symbolTerm.getName().equals(name)) {
                        return symbolTerm;
                    }
                }
                now = now.father;
            }
            return null;
        } else {
            SymbolTable now = this;
            while (now != null) {
                for (SymbolTerm symbolTerm : now.symbolTerms) {
                    if (symbolTerm.getName().equals(name) && symbolTerm.getLine() == line) {
                        return symbolTerm;
                    }
                }
                now = now.father;
            }
            return null;
        }
    }

    public boolean isNum(String str) {
        return str.matches("[-+]*[0-9]+");
    }

    public int searchOffsetSp(String preName) {
        String name;
        int line = 0;
        boolean isReal = false;
        if (isNum(preName)) {
            name = preName;
        } else if (preName.charAt(preName.length() - 1) != 'A') {
            line = Integer.parseInt(preName.substring(preName.lastIndexOf('_') + 1, preName.length()));
            isReal = true;
            name = preName.substring(0, preName.lastIndexOf('_'));
        } else {
            name = preName;
        }
        if (!isReal) {
            SymbolTable now = this;
            while (now != null) {
                for (SymbolTerm symbolTerm : now.symbolTerms) {
                    if (symbolTerm.getName().equals(name)) {
                        return symbolTerm.getOffset_sp() + stackSize;
                    }
                }
                now = now.father;
            }
            return -1;
        } else {
            SymbolTable now = this;
            while (now != null) {
                for (SymbolTerm symbolTerm : now.symbolTerms) {
                    if (symbolTerm.getName().equals(name)) {
                        if (symbolTerm.getLine() == line) {
                            return symbolTerm.getOffset_sp() + stackSize;
                        }
                    }
                }
                now = now.father;
            }
            return -1;
        }
    }
}
