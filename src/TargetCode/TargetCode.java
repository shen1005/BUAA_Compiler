package TargetCode;

import MidCode.CodeManager;
import MidCode.MidCodeTerm;
import Node.ErrorAnalysis;
import Type.OpType;

import java.util.ArrayList;
import java.util.HashMap;

public class TargetCode {
    private CodeManager codeManager = CodeManager.getInstance();
    private String data = ".data 0x10000000\n";
    private String globalStart = "#####global start#####\n";
    private String globalEnd = "#####global end#####\n";
    private String strStart = "#####str start#####\n";
    private String strEnd = "#####str end#####\n";
    private String text = ".text\n";
    private ArrayList<MidCodeTerm> midCodeTerms = codeManager.getMidCodeTerms();
    private ErrorAnalysis errorAnalysis = ErrorAnalysis.getInstance();
    private int pos = 0;

    public TargetCode() {
    }

    public String getCode() {
        String code = "";
        code += data;
        code += globalStart;
        code += getGlobalCode();
        code += globalEnd;
        code += strStart;
        code += getStrCode();
        code += strEnd;
        code += text;
        code += String.format("subiu $sp,$sp,%d\n", errorAnalysis.getFunc2Symbol("main").getSize());
        code += "j main\n";
        for (; pos < midCodeTerms.size(); pos++) {
            code += midCodeTerms.get(pos).getTargetCode();
        }
        return code;
    }

    public String getGlobalCode() {
        String code = "";
        for (; pos < midCodeTerms.size(); pos++) {
            MidCodeTerm midCodeTerm = midCodeTerms.get(pos);
            if (midCodeTerm.getOp().equals(OpType.GLOBAL_DEC)) {
                code += midCodeTerm.getTargetCode();
            } else {
                break;
            }
        }
        return code;
    }

    public String getStrCode() {
        String code = "";
        HashMap<String, String> strMap = codeManager.getStrMap();
        for (String key : strMap.keySet()) {
            code += key + ": .asciiz \"" + strMap.get(key) + "\"\n";
        }
        return code;
    }
}
