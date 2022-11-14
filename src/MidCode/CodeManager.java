package MidCode;

import Node.ErrorAnalysis;
import Node.SymbolTable;
import Node.SymbolTerm;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeManager {
    private static CodeManager codeManager = new CodeManager();
    private ArrayList<MidCodeTerm> midCodeList = new ArrayList<>();
    private HashMap<String, String> stringMap = new HashMap<>();
    private int count = 0;
    private int temp_num = 0;
    private int str_num = 0;
    private int label_num = 0;

    private CodeManager() {}

    public static CodeManager getInstance() {
        return codeManager;
    }

    public void addMidCode(MidCodeTerm midCodeTerm) {
        midCodeList.add(midCodeTerm);
    }

    public int getCount() {
        count++;
        return count;
    }

    public String getTemp(SymbolTable currentTable) {
        temp_num++;
        SymbolTerm symbolTerm = new SymbolTerm("T_" + temp_num + "A", 0, 0);
        symbolTerm.setSize(false);
        currentTable.addTerm(symbolTerm);
        return "T" + "_" + temp_num + "A";
    }

    @Override
    public String toString() {
        String result = "";
        for (MidCodeTerm midCodeTerm : midCodeList) {
            result += midCodeTerm.toString() + '\n';
        }
        return result;
    }

    public String getStr(String str) {
        str_num++;
        stringMap.put("Str" + "_" + str_num, str);
        return "Str" + "_" + str_num;
    }

    public ArrayList<MidCodeTerm> getMidCodeTerms() {
        return midCodeList;
    }

    public HashMap<String, String> getStrMap() {
        return stringMap;
    }

    public String getLabel() {
        label_num++;
        while (ErrorAnalysis.getInstance().getFuncTable().containsKey("Label" + "_" + label_num)) {
            label_num++;
        }
        return "Label" + "_" + label_num;
    }
}
