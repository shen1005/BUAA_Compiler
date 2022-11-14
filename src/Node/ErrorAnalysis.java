package Node;

import WordAnalysis.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ErrorAnalysis {
    private ArrayList<Error> errors = new ArrayList<Error>();
    private HashMap<String, FuncDel> funcTable = new HashMap<String, FuncDel>();
    private SymbolTable rootTable = new SymbolTable();
    private SymbolTable currentTable = rootTable;
    private final static ErrorAnalysis instance = new ErrorAnalysis();
    //属于哪个函数
    private HashMap<String, SymbolTable> func2Symbol = new HashMap<>();

    public void addFunc2Symbol(String funcName, SymbolTable symbolTable) {
        func2Symbol.put(funcName, symbolTable);
    }

    public SymbolTable getRootTable() {
        return rootTable;
    }

    public static ErrorAnalysis getInstance() {
        return instance;
    }

    public void changeTable() {
        SymbolTable temp = new SymbolTable(currentTable);
        currentTable.addSon(temp);
        currentTable = temp;
    }

    public void backTable() {
        currentTable = currentTable.getFather();
    }

    public void addError(int line, String error) {
        errors.add(new Error(line, error));
    }


    public SymbolTable getCurrentTable() {
        return currentTable;
    }

    public HashMap<String, FuncDel> getFuncTable() {
        return funcTable;
    }

    public boolean isInConstTable(String name) {
        boolean flag = false;
        SymbolTable temp = currentTable;
        while (temp != null) {
            if (temp.getConstTable().containsKey(name)) {
                flag = true;
                break;
            }
            temp = temp.getFather();
        }
        return flag;
    }

    public boolean isInTable(String name) {
        boolean flag = false;
        SymbolTable temp = currentTable;
        while (temp != null) {
            if (temp.getTable().containsKey(name)) {
                flag = true;
                break;
            }
            temp = temp.getFather();
        }
        return flag;
    }

    public boolean isInFuncTable(String name) {
        return funcTable.containsKey(name);
    }

    public int getDim(String str) {
        if (isInTable(str)) {
            SymbolTable temp = currentTable;
            while (temp != null) {
                if (temp.getTable().containsKey(str)) {
                    return temp.getTable().get(str);
                }
                temp = temp.getFather();
            }
        }
        else if (isInConstTable(str)) {
            SymbolTable temp = currentTable;
            while (temp != null) {
                if (temp.getConstTable().containsKey(str)) {
                    return temp.getConstTable().get(str);
                }
                temp = temp.getFather();
            }
        }
        return -1;
    }

    public FuncDel getFuncDel(String name) {
        return funcTable.get(name);
    }

    @Override
    public String toString() {
        Collections.sort(errors);
        String str = "";
        for (Error error : errors) {
            str += error.toString();
        }
        return str;
    }

    public boolean isErrorConstAssign(String str) {
        boolean flag = false;
        SymbolTable temp = currentTable;
        while (temp != null) {
            if (temp.getTable().containsKey(str)) {
                break;
            } else if (temp.getConstTable().containsKey(str)) {
                flag = true;
                break;
            }
            temp = temp.getFather();
        }
        return flag;
    }

    public String getFuncType(String funcName) {
        return funcTable.get(funcName).getType();
    }

    public SymbolTable getRoot() {
        return rootTable;
    }

    public SymbolTable getFunc2Symbol(String funcName) {
        return func2Symbol.get(funcName);
    }

    public boolean isFindError() {
        return errors.size() != 0;
    }

    public boolean isHaveFunc(String name) {
        return funcTable.containsKey(name);
    }
}
