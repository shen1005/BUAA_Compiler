package MidCode;

import Node.ErrorAnalysis;
import Node.SymbolTable;
import Node.SymbolTerm;
import Optimize.ConflictGraph;
import Optimize.FuncBlock;
import Optimize.Optimizer;
import Type.OpType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MidCodeTerm {
    private OpType op;
    private String para1;
    private String para2;
    private String para3;
    private String result;
    private final String funcDec = "##################FUNC_DEC##################\n";
    private SymbolTable currentSymbolTable;
    private boolean isEntrance = false;
    private HashSet<String> defSet = new HashSet<>();
    private HashSet<String> useSet = new HashSet<>();
    private final HashSet<String> inSet = new HashSet<>();
    private final HashSet<String> outSet = new HashSet<>();
    private FuncBlock funcBlock;
    private ConflictGraph conflictGraph;

    public MidCodeTerm(OpType op, String para1, String para2, String para3, String result) {
        this.op = op;
        this.para1 = para1;
        this.para2 = para2;
        this.para3 = para3;
        this.result = result;
    }

    public MidCodeTerm(OpType op, String para1, String para2, String para3, String result, SymbolTable currentSymbolTable) {
        this.op = op;
        this.para1 = para1;
        this.para2 = para2;
        this.para3 = para3;
        this.result = result;
        this.currentSymbolTable = currentSymbolTable;
    }

    public MidCodeTerm(OpType op, String para1, String para2, String result, SymbolTable currentSymbolTable) {
        this.op = op;
        this.para1 = para1;
        this.para2 = para2;
        this.para3 = null;
        this.result = result;
        this.currentSymbolTable = currentSymbolTable;
    }

    public MidCodeTerm(OpType op, String para1, String para2, String result) {
        this.op = op;
        this.para1 = para1;
        this.para2 = para2;
        this.para3 = null;
        this.result = result;
    }

    public void setCurrentSymbolTable(SymbolTable currentSymbolTable) {
        this.currentSymbolTable = currentSymbolTable;
    }

    public String getResult() {
        return result;
    }

    public String getPara1() {
        return para1;
    }

    public String getPara2() {
        return para2;
    }

    public String getPara3() {
        return para3;
    }

    public void setPara1(String para1) {
        this.para1 = para1;
    }

    public void setPara2(String para2) {
        this.para2 = para2;
    }

    public void setPara3(String para3) {
        this.para3 = para3;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        switch (op) {
            case ASSIGN:
                return OpType.ASSIGN  + "  " + para1 + " " + this.result;
            case SW_1D:
                return OpType.SW_1D + "  " + para1 + " " + para2 + " " + this.result;
            case SW_2D:
                return OpType.SW_2D + "  " + para1 + " " + para2 + " " + para3 + " " + this.result;
            case ADD:
                return OpType.ADD + "  " + para1 + " " + para2 + " " + this.result;
            case SUB:
                return OpType.SUB + "  " + para1 + " " + para2 + " " + this.result;
            case MUL:
                return OpType.MUL + "  " + para1 + " " + para2 + " " + this.result;
            case DIV:
                return OpType.DIV + "  " + para1 + " " + para2 + " " + this.result;
            case MOD:
                return OpType.MOD + "  " + para1 + " " + para2 + " " + this.result;
            case NEG:
                return OpType.NEG + "  " + para1 + " " + this.result;
            case NOT:
                return OpType.NOT + "  " + para1 + " " + this.result;
            case LOAD_ADDRESS:
                return OpType.LOAD_ADDRESS + "  " + para1 + " " + this.result;
            case LOAD_ARRAY_1D:
                return OpType.LOAD_ARRAY_1D + "  " + para1 + " " + para2 + " " + this.result;
            case LOAD_ARRAY_2D:
                return OpType.LOAD_ARRAY_2D + "  " + para1 + " " + para2 + " " + para3 + " " + this.result;
            case PRINT_INT:
                return OpType.PRINT_INT + "  " + result;
            case PRINT_STRING:
                return OpType.PRINT_STRING + "  " + result;
            case GetInt:
                return OpType.GetInt + "  " + this.result;
            case FUNC_DEC:
                return OpType.FUNC_DEC + "  " + this.result;
            case PRE_CALL:
                return OpType.PRE_CALL + "  " + this.result;
            case PUSH:
                return OpType.PUSH + "  " + para1 + " " + para2 + " " + result;
            case CALL:
                return OpType.CALL +  " " + result;
            case FIN_CALL:
                return OpType.FIN_CALL + " " + result;
            case SW_RET:
                return OpType.SW_RET + " " + result;
            case EXIT:
                return OpType.EXIT + "";
            case RET_VOID:
                return OpType.RET_VOID + " ";
            case RET_VALUE:
                return OpType.RET_VALUE + " " +result;
            case GLOBAL_DEC:
                return OpType.GLOBAL_DEC + " " + result;
            case PUT_LABEL:
                return OpType.PUT_LABEL + " " + result;
            case GOTO:
                return OpType.GOTO + " " + result;
            case BEQZ:
                return OpType.BEQZ + " " + para1 + " " + result;
            case BNEZ:
                return OpType.BNEZ + " " + para1 + " " + result;
            case EQ:
                return OpType.EQ + " " + para1 + " " + para2 + " " + result;
            case NE:
                return OpType.NE + " " + para1 + " " + para2 + " " + result;
            case LT:
                return OpType.LT + " " + para1 + " " + para2 + " " + result;
            case LE:
                return OpType.LE + " " + para1 + " " + para2 + " " + result;
            case GT:
                return OpType.GT + " " + para1 + " " + para2 + " " + result;
            case GE:
                return OpType.GE + " " + para1 + " " + para2 + " " + result;
            default:
                return null;
        }
    }

    public OpType getOp() {
        return op;
    }

    public String getTargetCode() {
        String targetCode = "###### " + this.toString() + " ######\n";
        if (op == OpType.GLOBAL_DEC) {
            targetCode += storeGlobal();
        } else if (op == OpType.FUNC_DEC) {
            SymbolTable.resetSp();
            targetCode += String.format("%s :", result);
            if (Optimizer.getInstance().getOptimizer()) {
                targetCode += initPara();
            }
        } else if (op == OpType.PUSH) {
            targetCode += getPush();
        } else if (op == OpType.PRE_CALL) {
            targetCode += getPreCall();
        } else if (op == OpType.CALL) {
            targetCode += getCall();
        } else if (op == OpType.FIN_CALL) {
            targetCode += getFinCall();
        } else if (op == OpType.EXIT) {
            targetCode += getExit();
        } else if (op == OpType.SW_RET) {
            targetCode += getSwRet();
        } else if (op == OpType.LOAD_ARRAY_1D) {
            targetCode += getLoadArray1D();
        } else if (op == OpType.RET_VOID) {
            targetCode += getRetVoid();
        } else if (op == OpType.RET_VALUE) {
            targetCode += getRetValue();
        } else if (op == OpType.PRINT_STRING) {
            targetCode += getPrintStr();
        } else if (op == OpType.PRINT_INT) {
            targetCode += getPrintInt();
        } else if (op == OpType.ADD) {
            targetCode += getAdd();
        } else if (op == OpType.SUB) {
            targetCode += getSub();
        } else if (op == OpType.MUL) {
            targetCode += getMul();
        } else if (op == OpType.DIV) {
            targetCode += getDiv();
        } else if (op == OpType.MOD) {
            targetCode += getMod();
        } else if (op == OpType.ASSIGN) {
            targetCode += getAssign();
        } else if (op == OpType.GetInt) {
            targetCode += getGetInt();
        } else if (op == OpType.NEG) {
            targetCode += getNeg();
        } else if (op == OpType.NOT) {
            targetCode += getNot();
        } else if (op == OpType.SW_1D) {
            targetCode += getSw1D();
        } else if (op == OpType.LOAD_ADDRESS) {
            targetCode += getLoadAddress();
        } else if (op == OpType.LOAD_ARRAY_2D) {
            targetCode += getLoadArray2D();
        } else if (op == OpType.SW_2D) {
            targetCode += getSw2D();
        } else if (op == OpType.PUT_LABEL) {
            targetCode += getPutLabel();
        } else if (op == OpType.EQ) {
            targetCode += getEQ();
        } else if (op == OpType.NE) {
            targetCode += getNE();
        } else if (op == OpType.BEQZ) {
            targetCode += getBEQZ();
        } else if (op == OpType.BNEZ) {
            targetCode += getBNEZ();
        } else if (op == OpType.GOTO) {
            targetCode += getGoto();
        } else if (op == OpType.LT) {
            targetCode += getLT();
        } else if (op == OpType.LE) {
            targetCode += getLE();
        } else if (op == OpType.GT) {
            targetCode += getGT();
        } else if (op == OpType.GE) {
            targetCode += getGE();
        }
        return targetCode + '\n';
    }

    public String initPara() {
        String code = "\n";
        SymbolTable symbolTable = ErrorAnalysis.getInstance().getFunc2Symbol(result);
        ArrayList<SymbolTerm> paraList = currentSymbolTable.getParaList(result);
        for (SymbolTerm term : paraList) {
            if (conflictGraph.isHaveReg(term.getRealName())) {
                code += String.format("lw %s, %d($sp)\n", conflictGraph.getReg(term.getRealName()), symbolTable.searchOffsetSp(term.getRealName()));
            }
        }
        return code;
    }

    public String storeGlobal() {
        String  realName = getRealName(result);
        SymbolTerm term = currentSymbolTable.getSymbolTerm(realName);
        if (term.getDim() == 1 && (term.getLength1Value() == null || term.getLength1Value().size() == 0)) {
            return String.format(".space %d",term.getSize());
        } else if (term.getDim() == 2 && (term.getLength2Value() == null || term.getLength2Value().size() == 0)) {
            return String.format(".space %d", term.getSize());
        }
        String code = String.format(".word");
        SymbolTable root = ErrorAnalysis.getInstance().getRoot();
        String name = result;
        if (result.charAt(result.length() - 1) != 'A') {
            name = result.substring(0, result.lastIndexOf('_'));
        }
        //把result后面的_ + 数字去掉(本身可能出现_)
        ArrayList<Integer> value = root.getAllValue(name);
        for (int i = 0; i < value.size(); i++) {
            if (i == 0) {
                code += " " + value.get(i);
            } else {
                code += String.format(", %d", value.get(i));
            }
        }
        return code;
    }

    public boolean isNum(String str) {
        return str.matches("[-+]*[0-9]+");
    }

    public String getPush() {
        int arg_offset = Integer.parseInt(para2) * 4 + 4;
        String temp = para1;
        String code = "";
        //如果处在优化状态下
        if (Optimizer.getInstance().getOptimizer()) {
            if (isNum(para1)) {
                code += String.format("li $t0, %s\n", para1);
                code += String.format("sw $t0, %d($sp)\n", arg_offset);
            }  else if (currentSymbolTable.isGlobal(para1)) {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(para1);
                int offset = term.getOffset_gp();
                code += String.format("lw $t0, %d($gp)\n", offset);
                code += String.format("sw $t0, %d($sp)\n", arg_offset);
            } else {
                //如果是局部变量
                if (conflictGraph.isHaveReg(para1)) {
                    code += String.format("sw %s, %d($sp)\n", conflictGraph.getReg(para1), arg_offset);
                } else {
                    int offset = currentSymbolTable.searchOffsetSp(para1);
                    code += String.format("lw $t0, %d($sp)\n", offset);
                    code += String.format("sw $t0, %d($sp)\n", arg_offset);
                }
            }
            return code;
        }
        if (isNum(para1)) {
            code += String.format("li $t0, %s\n", para1);
            code += String.format("sw $t0, %d($sp)\n", arg_offset);
        }
        else {
            String para_temp = para1;
               if (currentSymbolTable.isGlobal(para_temp)) {
                   SymbolTerm term = currentSymbolTable.getSymbolTerm(para_temp);
                   int offset = term.getOffset_gp();
                   code += String.format("lw $t0, %d($gp)\n", offset);
                   code += String.format("sw $t0, %d($sp)\n", arg_offset);
               }
                else {
                     int offset = currentSymbolTable.searchOffsetSp(para_temp);
                     code += String.format("lw $t0, %d($sp)\n", offset);
                     code += String.format("sw $t0, %d($sp)\n", arg_offset);
                }
        }
        return code;
    }

    public String getPreCall() {
        String code = String.format("sw $ra, 0($sp)\n");
        String funcName = result;
        SymbolTable symbolTable = ErrorAnalysis.getInstance().getFunc2Symbol(funcName);
//        if (Optimizer.getInstance().getOptimizer()) {
//            code += saveRegs();
//        }
        int size = symbolTable.getSize() + 4;
        code += String.format("subiu $sp,$sp,%d\n",size);
        SymbolTable.addStackSize(size);
        return code;
    }

    public String saveRegs() {
        HashMap<String, String> saveReg = conflictGraph.getSaveRegs();
        String code = "";
        for (String var : saveReg.keySet()) {
            String reg = saveReg.get(var);
            int offset = currentSymbolTable.searchOffsetSp(var);
            code += String.format("sw %s, %d($sp)\n", reg, offset);
        }
        return code;
    }

    public String getCall() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            code += saveRegs();
        }
        code += String.format("jal %s\n", result);
        return code;
    }

    public String getFinCall() {
        String name = result;
        SymbolTable symbolTable = ErrorAnalysis.getInstance().getFunc2Symbol(name);
        int size = symbolTable.getSize() + 4;
        String code = String.format("addiu $sp,$sp,%d\n", size);
        SymbolTable.addStackSize(-size);
        code += String.format("lw $ra, 0($sp)\n");
        if (Optimizer.getInstance().getOptimizer()) {
            code += restoreRegs();
        }
        return code;
    }

    public String restoreRegs() {
        HashMap<String, String> saveReg = conflictGraph.getSaveRegs();
        String code = "";
        for (String var : saveReg.keySet()) {
            String reg = saveReg.get(var);
            int offset = currentSymbolTable.searchOffsetSp(var);
            code += String.format("lw %s, %d($sp)\n", reg, offset);
        }
        return code;
    }

    public String getExit() {
        String code = String.format("li $v0, 10\n");
        code += String.format("syscall\n");
        return code;
    }

    public String getSwRet() {
        String name = result;
        //一定是A
        int offset = currentSymbolTable.searchOffsetSp(name);
        if (Optimizer.getInstance().getOptimizer()) {
            String code = "";
            if (conflictGraph.isHaveReg(name)) {
                code += String.format("move %s, $v0", conflictGraph.getReg(name));
            } else {
                code += String.format("sw $v0, %d($sp)", offset);
            }
            return code;
        }
        String code = String.format("sw $v0, %d($sp)\n", offset);
        return code;
    }

    //找到name的值存到t1寄存器
    public String loadTValue(String name) {
        String code = "";
        if (isNum(name)) {
            code += String.format("li $t1, %s\n", name);
        } else {
            if (currentSymbolTable.isGlobal(name)) {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
                int offset = term.getOffset_gp();
                code += String.format("lw $t1, %d($gp)\n", offset);
            } else {
                int offset = currentSymbolTable.searchOffsetSp(name);
                code += String.format("lw $t1, %d($sp)\n", offset);
            }
        }
        return code;
    }

    ////找到name的值存到t0寄存器
    public String loadT0Value(String name) {
        String code = "";
        if (isNum(name)) {
            code += String.format("li $t0, %s\n", name);
        } else {
            if (currentSymbolTable.isGlobal(name)) {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
                int offset = term.getOffset_gp();
                code += String.format("lw $t0, %d($gp)\n", offset);
            } else {
                int offset = currentSymbolTable.searchOffsetSp(name);
                code += String.format("lw $t0, %d($sp)\n", offset);
            }
        }
        return code;
    }

    //找到name的值存到t3寄存器
    public String loadT3Value(String name) {
        String code = "";
        if (isNum(name)) {
            code += String.format("li $t3, %s\n", name);
        } else {
            if (currentSymbolTable.isGlobal(name)) {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
                int offset = term.getOffset_gp();
                code += String.format("lw $t3, %d($gp)\n", offset);
            } else {
                int offset = currentSymbolTable.searchOffsetSp(name);
                code += String.format("lw $t3, %d($sp)\n", offset);
            }
        }
        return code;
    }

    //将name值load到指定内存器上(name一定在内存中)
    public String loadValue(String name, String reg) {
        String code = "";
        if (isNum(name)) {
            code += String.format("li %s, %s\n", reg, name);
        } else {
            if (currentSymbolTable.isGlobal(name)) {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
                int offset = term.getOffset_gp();
                code += String.format("lw %s, %d($gp)\n", reg, offset);
            } else {
                int offset = currentSymbolTable.searchOffsetSp(name);
                code += String.format("lw %s, %d($sp)\n", reg, offset);
            }
        }
        return code;
    }

    //找到名字为name，下标为i的一维数组的值存到t0寄存器
    public String load1D(String name, String i) {
        String code = "";
        if (currentSymbolTable.isGlobal(name)) {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
            int offset = term.getOffset_gp();
            code += String.format("addiu $t0,$gp,%d\n", offset);
        } else {
            //如果是形参
            SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
            if (term.getLength1() == 0) {
                int offset = currentSymbolTable.searchOffsetSp(name);
                code += String.format("lw $t0, %d($sp)\n", offset);
            }
            else {
                int offset = currentSymbolTable.searchOffsetSp(name);
                code += String.format("addiu $t0,$sp,%d\n", offset);
            }
        }
        code += loadTValue(i);
        code += String.format("sll $t1, $t1, 2\n");
        code += String.format("addu $t0, $t0, $t1\n");
        code += String.format("lw $t0, 0($t0)\n");
        return code;
    }

    //优化版本，将一个数组的指定下标加载到目标寄存器上
    public String load1D(String name, String index, String reg) {
        String code = "";
        if (currentSymbolTable.isGlobal(name)) {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
            int offset = term.getOffset_gp();
            //t0存放数组的首地址
            code += String.format("addiu $t0, $gp, %d\n", offset);
        } else {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
            if (term.getLength1() == 0) {
                int offset = currentSymbolTable.searchOffsetSp(name);
                code += String.format("lw $t0, %d($sp)\n", offset);
            }
            else {
                int offset = currentSymbolTable.searchOffsetSp(name);
                code += String.format("addiu $t0,$sp,%d\n", offset);
            }
        }
        //之后计算index偏移量加到t0上
        if (isNum(index)) {
            int offset = Integer.parseInt(index) * 4;
            code += String.format("addiu $t0, $t0, %d\n", offset);
        } else if (conflictGraph.isHaveReg(index)) {
            code += String.format("sll $t1, %s, 2\n", conflictGraph.getReg(index));
            code += String.format("addu $t0, $t0, $t1\n");
        } else {
            code += loadTValue(index);
            code += String.format("sll $t1, $t1, 2\n");
            code += String.format("addu $t0, $t0, $t1\n");
        }
        code += String.format("lw %s, 0($t0)\n", reg);
        return code;
    }

    //将t0寄存器的值存到对应的name里
    public String store(String name) {
        String code = "";
        if (currentSymbolTable.isGlobal(name)) {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
            int offset = term.getOffset_gp();
            code += String.format("sw $t0, %d($gp)\n", offset);
        } else {
            int offset = currentSymbolTable.searchOffsetSp(name);
            code += String.format("sw $t0, %d($sp)\n", offset);
        }
        return code;
    }

    //将指定寄存器的值存到对应的name里
    public String store(String name, String reg) {
        String code = "";
        if (currentSymbolTable.isGlobal(name)) {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(name);
            int offset = term.getOffset_gp();
            code += String.format("sw %s, %d($gp)\n", reg, offset);
        } else {
            int offset = currentSymbolTable.searchOffsetSp(name);
            code += String.format("sw %s, %d($sp)\n", reg, offset);
        }
        return code;
    }

    public String getRealName(String name) {
//        if (isNum(name)) {
//            return name;
//        }
//        if (name.charAt(name.length() - 1) != 'A') {
//            name = name.substring(0, name.lastIndexOf('_'));
//        }
        return name;
    }

    public String getLoadArray1D() {
        if (Optimizer.getInstance().getOptimizer()) {
            String code = "";
            String targetReg;
            if (conflictGraph.isHaveReg(result)) {
                targetReg = conflictGraph.getReg(result);
            } else {
                targetReg = "$t0";
            }
            code += load1D(para1, para2, targetReg);
            if (targetReg.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        String para_temp = para1;
        String code = load1D(para_temp, getRealName(para2));
        code += store(result);
        return code;
    }

    public String getRetVoid() {
        return String.format("jr $ra\n");
    }

    public String getRetValue() {
        if (Optimizer.getInstance().getOptimizer()) {
            String code = "";
            if (isNum(result)) {
                code += String.format("li $v0, %s\n", result);
            } else if (conflictGraph.isHaveReg(result)) {
                code += String.format("move $v0, %s\n", conflictGraph.getReg(result));
            } else {
                code += loadTValue(result);
                code += String.format("move $v0, $t1\n");
            }
            code += String.format("jr $ra\n");
            return code;
        }
        String code = loadTValue(getRealName(result));
        code += String.format("move $v0, $t1\n");
        code += String.format("jr $ra\n");
        return code;
    }

    public String getPrintStr() {
        String code = String.format("li $v0, 4\n");
        code += String.format("la $a0, %s\n", result);
        code += String.format("syscall\n");
        return code;
    }

    public String getPrintInt() {
        String code = String.format("li $v0, 1\n");
        if (Optimizer.getInstance().getOptimizer()) {
            if (isNum(result)) {
                code += String.format("li $a0, %s\n", result);
            } else if (conflictGraph.isHaveReg(result)) {
                code += String.format("move $a0, %s\n", conflictGraph.getReg(result));
            } else {
                code += loadTValue(result);
                code += String.format("move $a0, $t1\n");
            }
            code += String.format("syscall\n");
            return code;
        }
        code += loadTValue(getRealName(result));
        code += String.format("move $a0, $t1\n");
        code += String.format("syscall\n");
        return code;
    }

    public String getAdd() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String targetReg;
            if (conflictGraph.isHaveReg(result)) {
                targetReg = conflictGraph.getReg(result);
            } else {
                targetReg = "$t0";
            }
            if (isNum(para1) && isNum(para2)) {
                int value = Integer.parseInt(para1) + Integer.parseInt(para2);
                code += String.format("li %s, %d\n", targetReg, value);
            } else if (isNum(para1)) {
                if (conflictGraph.isHaveReg(para2)) {
                    code += String.format("addiu %s, %s, %s\n", targetReg, conflictGraph.getReg(para2), para1);
                } else {
                    code += loadTValue(para2);
                    code += String.format("addiu %s, $t1, %s\n", targetReg, para1);
                }
            } else if (isNum(para2)) {
                if (conflictGraph.isHaveReg(para1)) {
                    code += String.format("addiu %s, %s, %s\n", targetReg, conflictGraph.getReg(para1), para2);
                } else {
                    code += loadTValue(para1);
                    code += String.format("addiu %s, $t1, %s\n", targetReg, para2);
                }
            } else {
                if (conflictGraph.isHaveReg(para1) && conflictGraph.isHaveReg(para2)) {
                    code += String.format("addu %s, %s, %s\n", targetReg, conflictGraph.getReg(para1), conflictGraph.getReg(para2));
                } else if (conflictGraph.isHaveReg(para1)) {
                    code += loadTValue(para2);
                    code += String.format("addu %s, %s, $t1\n", targetReg, conflictGraph.getReg(para1));
                } else if (conflictGraph.isHaveReg(para2)) {
                    code += loadTValue(para1);
                    code += String.format("addu %s, $t1, %s\n", targetReg, conflictGraph.getReg(para2));
                } else {
                    code += loadTValue(para1);
                    code += loadT3Value(para2);
                    code += String.format("addu %s, $t1, $t3\n", targetReg);
                }
            }
            if (targetReg.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        if (isNum(para1)) {
            code += String.format("li $t0, %s\n", para1);
        } else {
            code += loadT0Value(getRealName(para1));
        }
        if (isNum(para2)) {
            code += String.format("li $t1, %s\n", para2);
        } else {
            code += loadTValue(getRealName(para2));
        }
        code += String.format("addu $t0, $t0, $t1\n");
        code += store(getRealName(result));
        return code;
    }

    public String getSub() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String targetReg;
            if (conflictGraph.isHaveReg(result)) {
                targetReg = conflictGraph.getReg(result);
            } else {
                targetReg = "$t0";
            }
            if (isNum(para1) && isNum(para2)) {
                int value = Integer.parseInt(para1) - Integer.parseInt(para2);
                code += String.format("li %s, %d\n", targetReg, value);
            } else if (isNum(para1)) {
                code += String.format("li $t2, %s\n", para1);
                if (conflictGraph.isHaveReg(para2)) {
                    code += String.format("subu %s, $t2, %s\n", targetReg, conflictGraph.getReg(para2));
                } else {
                    code += loadTValue(para2);
                    code += String.format("subu %s, $t2, $t1\n", targetReg);
                }
            } else if (isNum(para2)) {
                String value2 = String.valueOf(Integer.parseInt(para2) * -1);
                if (conflictGraph.isHaveReg(para1)) {
                    code += String.format("addiu %s, %s, %s\n", targetReg, conflictGraph.getReg(para1), value2);
                } else {
                    code += loadTValue(para1);
                    code += String.format("addiu %s, $t1, %s\n", targetReg, value2);
                }
            } else {
                if (conflictGraph.isHaveReg(para1) && conflictGraph.isHaveReg(para2)) {
                    code += String.format("subu %s, %s, %s\n", targetReg, conflictGraph.getReg(para1), conflictGraph.getReg(para2));
                } else if (conflictGraph.isHaveReg(para1)) {
                    code += loadTValue(para2);
                    code += String.format("subu %s, %s, $t1\n", targetReg, conflictGraph.getReg(para1));
                } else if (conflictGraph.isHaveReg(para2)) {
                    code += loadTValue(para1);
                    code += String.format("subu %s, $t1, %s\n", targetReg, conflictGraph.getReg(para2));
                } else {
                    code += loadTValue(para1);
                    code += loadT3Value(para2);
                    code += String.format("subu %s, $t1, $t3\n", targetReg);
                }
            }
            if (targetReg.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        if (isNum(para1)) {
            code += String.format("li $t0, %s\n", para1);
        } else {
            code += loadT0Value(getRealName(para1));
        }
        if (isNum(para2)) {
            code += String.format("li $t1, %s\n", para2);
        } else {
            code += loadTValue(getRealName(para2));
        }
        code += String.format("subu $t0, $t0, $t1\n");
        code += store(getRealName(result));
        return code;
    }

    public boolean isPower(String num) {
        int value = Integer.parseInt(num);
        if (value < 0) {
            value = value * -1;
        }
        if (value == 0) {
            return false;
        }
        return (value & (value - 1)) == 0;
    }

    public int getPower(String num) {
        int value = Integer.parseInt(num);
        if (value < 0) {
            value = value * -1;
        }
        int power = 0;
        while (value != 1) {
            value = value / 2;
            power++;
        }
        return power;
    }

    public String getMul() {
        String code = "";
        //TODO 优化乘法
        if (Optimizer.getInstance().getOptimizer()) {
            String targetReg;
            if (conflictGraph.isHaveReg(result)) {
                targetReg = conflictGraph.getReg(result);
            } else {
                targetReg = "$t0";
            }
            if (isNum(para1) && isNum(para2)) {
                int value = Integer.parseInt(para1) * Integer.parseInt(para2);
                code += String.format("li %s, %d", targetReg, value);
            } else if (isNum(para1)) {
                if (Integer.parseInt(para1) == 0 || Integer.parseInt(para1) == 1 || Integer.parseInt(para1) == -1) {
                    if (Integer.parseInt(para1) == 0) {
                        code += String.format("li %s, 0", targetReg);
                    } else if (Integer.parseInt(para1) == 1) {
                        if (conflictGraph.isHaveReg(para2)) {
                            code += String.format("move %s, %s", targetReg, conflictGraph.getReg(para2));
                        } else {
                            code += loadTValue(para2);
                            code += String.format("move %s, $t1", targetReg);
                        }
                    } else {
                        if (conflictGraph.isHaveReg(para2)) {
                            code += String.format("neg %s, %s", targetReg, conflictGraph.getReg(para2));
                        } else {
                            code += loadTValue(para2);
                            code += String.format("neg %s, $t1", targetReg);
                        }
                    }
                } else if (isPower(para1)) {
                    int power = getPower(para1);
                    if (conflictGraph.isHaveReg(para2)) {
                        code += String.format("sll %s, %s, %d\n", targetReg, conflictGraph.getReg(para2), power);
                        if (Integer.parseInt(para1) < 0) {
                            code += String.format("neg %s, %s\n", targetReg, targetReg);
                        }
                    } else {
                        code += loadTValue(para2);
                        code += String.format("sll %s, $t1, %d\n", targetReg, power);
                        if (Integer.parseInt(para1) < 0) {
                            code += String.format("neg %s, %s\n", targetReg, targetReg);
                        }
                    }
                } else {
                    code += String.format("li $t2, %s\n", para1);
                    if (conflictGraph.isHaveReg(para2)) {
                        code += String.format("mul %s, $t2, %s\n", targetReg, conflictGraph.getReg(para2));
//                    code += String.format("mult $t2, %s\n", conflictGraph.getReg(para2));
//                    code += String.format("mflo %s\n", targetReg);
                    } else {
                        code += loadTValue(para2);
                        code += String.format("mul %s, $t2, $t1\n", targetReg);
//                    code += String.format("mult $t2, $t1\n");
//                    code += String.format("mflo %s\n", targetReg);
                    }
                }
            } else if (isNum(para2)) {
                if (Integer.parseInt(para2) == 0 || Integer.parseInt(para2) == 1 || Integer.parseInt(para2) == -1) {
                    if (Integer.parseInt(para2) == 0) {
                        code += String.format("li %s, 0", targetReg);
                    } else if (Integer.parseInt(para2) == 1) {
                        if (conflictGraph.isHaveReg(para1)) {
                            code += String.format("move %s, %s\n", targetReg, conflictGraph.getReg(para1));
                        } else {
                            code += loadTValue(para1);
                            code += String.format("move %s, $t1\n", targetReg);
                        }
                    } else {
                        if (conflictGraph.isHaveReg(para1)) {
                            code += String.format("neg %s, %s\n", targetReg, conflictGraph.getReg(para1));
                        } else {
                            code += loadTValue(para1);
                            code += String.format("neg %s, $t1\n", targetReg);
                        }
                    }
                } else if (isPower(para2)) {
                    int power = getPower(para2);
                    if (conflictGraph.isHaveReg(para1)) {
                        code += String.format("sll %s, %s, %d\n", targetReg, conflictGraph.getReg(para1), power);
                        if (Integer.parseInt(para2) < 0) {
                            code += String.format("neg %s, %s\n", targetReg, targetReg);
                        }
                    } else {
                        code += loadTValue(para1);
                        code += String.format("sll %s, $t1, %d\n", targetReg, power);
                        if (Integer.parseInt(para2) < 0) {
                            code += String.format("neg %s, %s\n", targetReg, targetReg);
                        }
                    }
                }
                else {
                    code += String.format("li $t2, %s\n", para2);
                    if (conflictGraph.isHaveReg(para1)) {
                        code += String.format("mul %s, $t2, %s\n", targetReg, conflictGraph.getReg(para1));
//                    code += String.format("mult $t2, %s\n", conflictGraph.getReg(para1));
//                    code += String.format("mflo %s\n", targetReg);
                    } else {
                        code += loadTValue(para1);
                        code += String.format("mul %s, $t2, $t1\n", targetReg);
//                    code += String.format("mult $t2, $t1\n");
//                    code += String.format("mflo %s\n", targetReg);
                    }
                }
//                if (conflictGraph.isHaveReg(para1)) {
//                    code += String.format("mult %s, $t2\n", conflictGraph.getReg(para1));
//                    code += String.format("mflo %s\n", targetReg);
//                } else {
//                    code += loadTValue(para1);
//                    code += String.format("mult $t1, $t2\n");
//                    code += String.format("mflo %s\n", targetReg);
//                }
            } else {
                if (conflictGraph.isHaveReg(para1) && conflictGraph.isHaveReg(para2)) {
                    code += String.format("mul %s, %s, %s\n", targetReg, conflictGraph.getReg(para1), conflictGraph.getReg(para2));
//                    code += String.format("mult %s, %s\n", conflictGraph.getReg(para1), conflictGraph.getReg(para2));
//                    code += String.format("mflo %s\n", targetReg);
                } else if (conflictGraph.isHaveReg(para1)) {
                    code += loadTValue(para2);
                    code += String.format("mul %s, %s, $t1\n", targetReg, conflictGraph.getReg(para1));
//                    code += String.format("mult %s, $t1\n", conflictGraph.getReg(para1));
//                    code += String.format("mflo %s\n", targetReg);
                } else if (conflictGraph.isHaveReg(para2)) {
                    code += loadTValue(para1);
                    code += String.format("mul %s, $t1, %s\n", targetReg, conflictGraph.getReg(para2));
//                    code += String.format("mult $t1, %s\n", conflictGraph.getReg(para2));
//                    code += String.format("mflo %s\n", targetReg);
                } else {
                    code += loadTValue(para1);
                    code += loadT3Value(para2);
                    code += String.format("mul %s, $t1, $t3\n", targetReg);
//                    code += String.format("mult $t1, $t3\n");
//                    code += String.format("mflo %s\n", targetReg);
                }
            }
            if (targetReg.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        if (isNum(para1)) {
            code += String.format("li $t0, %s\n", para1);
        } else {
            code += loadT0Value(getRealName(para1));
        }
        if (isNum(para2)) {
            code += String.format("li $t1, %s\n", para2);
        } else {
            code += loadTValue(getRealName(para2));
        }
        code += String.format("mult $t0, $t1\n");
        code += String.format("mflo $t0\n");
        code += store(getRealName(result));
        return code;
    }

    public String getDiv() {
        String code = "";
        //TODO 优化除法
        if (Optimizer.getInstance().getOptimizer()) {
            String targetReg;
            if (conflictGraph.isHaveReg(result)) {
                targetReg = conflictGraph.getReg(result);
            } else {
                targetReg = "$t0";
            }
//            if (isNum(para1) && isNum(para2)) {
//                int value = Integer.parseInt(para1) / Integer.parseInt(para2);
//                code += String.format("li %s, %d\n", targetReg, value);
//            } else if (isNum(para1)) {
//                code += String.format("li $t2, %s\n", para1);
//                if (conflictGraph.isHaveReg(para2)) {
//                    code += String.format("div $t2, %s\n", conflictGraph.getReg(para2));
//                    code += String.format("mflo %s\n", targetReg);
//                } else {
//                    code += loadTValue(para2);
//                    code += String.format("div $t2, $t1\n");
//                    code += String.format("mflo %s\n", targetReg);
//                }
//            } else if (isNum(para2)) {
//                code += String.format("li $t2, %s\n", para2);
//                if (conflictGraph.isHaveReg(para1)) {
//                    code += String.format("div %s, $t2\n", conflictGraph.getReg(para1));
//                    code += String.format("mflo %s\n", targetReg);
//                } else {
//                    code += loadTValue(para1);
//                    code += String.format("div $t1, $t2\n");
//                    code += String.format("mflo %s\n", targetReg);
//                }
//            } else {
//                if (conflictGraph.isHaveReg(para1) && conflictGraph.isHaveReg(para2)) {
//                    code += String.format("div %s, %s\n", conflictGraph.getReg(para1), conflictGraph.getReg(para2));
//                    code += String.format("mflo %s\n", targetReg);
//                } else if (conflictGraph.isHaveReg(para1)) {
//                    code += loadTValue(para2);
//                    code += String.format("div %s, $t1\n", conflictGraph.getReg(para1));
//                    code += String.format("mflo %s\n", targetReg);
//                } else if (conflictGraph.isHaveReg(para2)) {
//                    code += loadTValue(para1);
//                    code += String.format("div $t1, %s\n", conflictGraph.getReg(para2));
//                    code += String.format("mflo %s\n", targetReg);
//                } else {
//                    code += loadTValue(para1);
//                    code += loadT3Value(para2);
//                    code += String.format("div $t1, $t3\n");
//                    code += String.format("mflo %s\n", targetReg);
//                }
            //在这里做除法优化:将除法结果保存到targetReg中
                code += opDiv(targetReg);
                if (targetReg.equals("$t0")) {
                    code += store(result);
                }
                return code;
            }
        if (isNum(para1)) {
            code += String.format("li $t0, %s\n", para1);
        } else {
            code += loadT0Value(getRealName(para1));
        }
        if (isNum(para2)) {
            code += String.format("li $t1, %s\n", para2);
        } else {
            code += loadTValue(getRealName(para2));
        }
        code += String.format("div $t0, $t1\n");
        code += String.format("mflo $t0\n");
        code += store(getRealName(result));
        return code;
    }

    //注意这里t0可能就是targetReg
    public String opDiv(String targetReg) {
        String code = "";
        if (isNum(para1) && isNum(para2)) {
            int value = Integer.parseInt(para1) / Integer.parseInt(para2);
            code += String.format("li %s, %d\n", targetReg, value);
        } else if (isNum(para1)) {
            if (Integer.parseInt(para1) == 0) {
                code += String.format("li %s, 0\n", targetReg);
            } else {
                code += String.format("li $t2, %s\n", para1);
                if (conflictGraph.isHaveReg(para2)) {
                    code += String.format("div $t2, %s\n", conflictGraph.getReg(para2));
                    code += String.format("mflo %s\n", targetReg);
                } else {
                    code += loadTValue(para2);
                    code += String.format("div $t2, $t1\n");
                    code += String.format("mflo %s\n", targetReg);
                }
            }
        } else if (isNum(para2)) {
            if (Integer.parseInt(para2) == 1 || Integer.parseInt(para2) == -1) {
                if (Integer.parseInt(para2) == 1) {
                    if (conflictGraph.isHaveReg(para1)) {
                        code += String.format("move %s, %s\n", targetReg, conflictGraph.getReg(para1));
                    } else {
                        code += loadTValue(para1);
                        code += String.format("move %s, $t1\n", targetReg);
                    }
                } else {
                    if (conflictGraph.isHaveReg(para1)) {
                        code += String.format("neg %s, %s\n", targetReg, conflictGraph.getReg(para1));
                    } else {
                        code += loadTValue(para1);
                        code += String.format("neg %s, $t1\n", targetReg);
                    }
                }
            } else if (isPower(para2)) {
                //注意这里一种情况是2的整数次幂
                String label1 = CodeManager.getInstance().getLabel();
                String label2 = CodeManager.getInstance().getLabel();
                String reg1;
                if (conflictGraph.isHaveReg(para1)) {
                    reg1 = conflictGraph.getReg(para1);
                } else {
                    code += loadTValue(para1);
                    reg1 = "$t1";
                }
                code += String.format("bgez %s, %s\n",  reg1, label1);
                int value = Integer.parseInt(para2);
                if (value < 0) {
                    value = -value;
                }
                code += String.format("addiu %s, %s, %d\n", targetReg, reg1, value - 1);
                code += String.format("sra %s, %s, %d\n", targetReg, targetReg, getPower(para2));
                code += String.format("j %s\n", label2);
                code += String.format("%s:\n", label1);
                code += String.format("sra %s, %s, %d\n", targetReg, reg1, getPower(para2));
                code += String.format("%s:\n", label2);
                if (Integer.parseInt(para2) < 0) {
                    code += String.format("neg %s, %s\n", targetReg, targetReg);
                }
            }
//            else if (isMagicNum(para2)) {
//                //这里是魔数除法
//                String label = CodeManager.getInstance().getLabel();
//                String reg1;
//                if (conflictGraph.isHaveReg(para1)) {
//                    reg1 = conflictGraph.getReg(para1);
//                } else {
//                    code += loadTValue(para1);
//                    reg1 = "$t1";
//                }
//                if (reg1.equals(targetReg)) {
//                    code += String.format("move $t3, %s\n", reg1);
//                    reg1 = "$t3";
//                }
//                code += String.format("li $t2, %s\n", getMagicNumber(para2));
//                code += String.format("mult %s, $t2\n", reg1);
//                code += String.format("mfhi %s\n", targetReg);
//                if (!getS(para2).equals("0")) {
//                    code += String.format("sra %s, %s, %s\n", targetReg, targetReg, getS(para2));
//                }
//                code += String.format("bgtz %s, %s\n", reg1, label);
//                code += String.format("addiu %s, %s, 1\n", targetReg, targetReg);
//                code += String.format("%s:\n", label);
//            }
              else {
                //para2是正常的常数
                code += String.format("li $t2, %s\n", para2);
                if (conflictGraph.isHaveReg(para1)) {
                    code += String.format("div %s, $t2\n", conflictGraph.getReg(para1));
                    code += String.format("mflo %s\n", targetReg);
                } else {
                    code += loadTValue(para1);
                    code += String.format("div $t1, $t2\n");
                    code += String.format("mflo %s\n", targetReg);
                }
            }
        } else {
            //两个操作数都不是数字
            String reg1;
            if (conflictGraph.isHaveReg(para1)) {
                reg1 = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg1 = "$t1";
            }
            String reg2;
            if (conflictGraph.isHaveReg(para2)) {
                reg2 = conflictGraph.getReg(para2);
            } else {
                code += loadT3Value(para2);
                reg2 = "$t3";
            }
            code += String.format("div %s, %s\n", reg1, reg2);
            code += String.format("mflo %s\n", targetReg);
        }
        return code;
    }

    public boolean isMagicNum(String num) {
        int value = Integer.parseInt(num);
        return value == 3
                || value == 5
                || value == 6
                || value == 9
                || value == 10
                || value == 11
                || value == 12
                || value == 25
                || value == 125
                || value == 625
                ;
    }

    public String getMagicNumber(String r2) {
        int value = Integer.parseInt(r2);
        String magicNumber;
        if (value == 3) {
            magicNumber = "0x55555556";
        } else if (value == 5) {
            magicNumber = "0x66666667";
        } else if (value == 6) {
            magicNumber = "0x2AAAAAAB";
        } else if (value == 9) {
            magicNumber = "0x38E38E39";
        } else if (value == 10) {
            magicNumber = "0x66666667";
        } else if (value == 11) {
            magicNumber = "0x2E8BA2E9";
        } else if (value == 12) {
            magicNumber = "0x2AAAAAAB";
        } else if (value == 25) {
            magicNumber = "0x51EB851F";
        } else if (value == 125) {
            magicNumber = "0x10624DD3";
        } else {
            assert value == 625;
            magicNumber = "0x68DB8BAD";
        }
        return magicNumber;
    }

    public String getS(String para2) {
        int value = Integer.parseInt(para2);
        String s;
        if (value == 3) {
            s = "0";
        } else if (value == 5) {
            s = "1";
        } else if (value == 6) {
            s = "0";
        } else if (value == 9) {
            s = "1";
        } else if (value == 10) {
            s = "2";
        } else if (value == 11) {
            s = "1";
        } else if (value == 12) {
            s = "1";
        } else if (value == 25) {
            s = "3";
        } else if (value == 125) {
            s = "3";
        } else {
            assert value == 625;
            s = "8";
        }
        return s;
    }

    public String getMod() {
        String code = "";
        //TODO 优化取模
        if (Optimizer.getInstance().getOptimizer()) {
            String targetReg;
            if (conflictGraph.isHaveReg(result)) {
                targetReg = conflictGraph.getReg(result);
            } else {
                targetReg = "$t0";
            }
//            if (isNum(para1) && isNum(para2)) {
//                int value = Integer.parseInt(para1) % Integer.parseInt(para2);
//                code += String.format("li %s, %d\n", targetReg, value);
//            } else if (isNum(para1)) {
//                code += String.format("li $t2, %s\n", para1);
//                if (conflictGraph.isHaveReg(para2)) {
//                    code += String.format("div $t2, %s\n", conflictGraph.getReg(para2));
//                    code += String.format("mfhi %s\n", targetReg);
//                } else {
//                    code += loadTValue(para2);
//                    code += String.format("div $t2, $t1\n");
//                    code += String.format("mfhi %s\n", targetReg);
//                }
//            } else if (isNum(para2)) {
//                code += String.format("li $t2, %s\n", para2);
//                if (conflictGraph.isHaveReg(para1)) {
//                    code += String.format("div %s, $t2\n", conflictGraph.getReg(para1));
//                    code += String.format("mfhi %s\n", targetReg);
//                } else {
//                    code += loadTValue(para1);
//                    code += String.format("div $t1, $t2\n");
//                    code += String.format("mfhi %s\n", targetReg);
//                }
//            } else {
//                if (conflictGraph.isHaveReg(para1) && conflictGraph.isHaveReg(para2)) {
//                    code += String.format("div %s, %s\n", conflictGraph.getReg(para1), conflictGraph.getReg(para2));
//                    code += String.format("mfhi %s\n", targetReg);
//                } else if (conflictGraph.isHaveReg(para1)) {
//                    code += loadTValue(para2);
//                    code += String.format("div %s, $t1\n", conflictGraph.getReg(para1));
//                    code += String.format("mfhi %s\n", targetReg);
//                } else if (conflictGraph.isHaveReg(para2)) {
//                    code += loadTValue(para1);
//                    code += String.format("div $t1, %s\n", conflictGraph.getReg(para2));
//                    code += String.format("mfhi %s\n", targetReg);
//                } else {
//                    code += loadTValue(para1);
//                    code += loadT3Value(para2);
//                    code += String.format("div $t1, $t3\n");
//                    code += String.format("mfhi %s\n", targetReg);
//                }
//                if (targetReg.equals("$t0")) {
//                    code += store(result);
//                }
//                return code;
//            }
            code += opMOD(targetReg);
            if (targetReg.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        if (isNum(para1)) {
            code += String.format("li $t0, %s\n", para1);
        } else {
            code += loadT0Value(getRealName(para1));
        }
        if (isNum(para2)) {
            code += String.format("li $t1, %s\n", para2);
        } else {
            code += loadTValue(getRealName(para2));
        }
        code += String.format("div $t0, $t1\n");
        code += String.format("mfhi $t0\n");
        code += store(getRealName(result));
        return code;
    }

    public String opMOD(String targetReg) {
        String code = "";
        if (isNum(para1) && isNum(para2)) {
            int value = Integer.parseInt(para1) % Integer.parseInt(para2);
            code += String.format("li %s, %d\n", targetReg, value);
        } else if (isNum(para1)) {
            if (Integer.parseInt(para1) == 0) {
                code += String.format("li %s, 0\n", targetReg);
            }
            else {
                code += String.format("li $t2, %s\n", para1);
                if (conflictGraph.isHaveReg(para2)) {
                    code += String.format("div $t2, %s\n", conflictGraph.getReg(para2));
                    code += String.format("mfhi %s\n", targetReg);
                } else {
                    code += loadTValue(para2);
                    code += String.format("div $t2, $t1\n");
                    code += String.format("mfhi %s\n", targetReg);
                }
            }
        } else if (isNum(para2)) {
            if (Integer.parseInt(para2) == 1 || Integer.parseInt(para2) == -1) {
                code += String.format("li %s, 0\n", targetReg);
            } else if (isPower(para2)) {
                //code += "li $v0 1\n syscall\n";
                String label1 = CodeManager.getInstance().getLabel();
                String label2 = CodeManager.getInstance().getLabel();
                String reg1;
                if (conflictGraph.isHaveReg(para1)) {
                    reg1 = conflictGraph.getReg(para1);
                } else {
                    reg1 = "$t1";
                    code += loadTValue(para1);
                }
                if (reg1.equals(targetReg)) {
                    code += String.format("move $t1, %s\n", reg1);
                    reg1 = "$t1";
                }
                int shift = getPower(para2);
                int value = Integer.parseInt(para2);
                if (value < 0) {
                    value = -value;
                }
                //开始做除法
                code += String.format("bgez %s, %s\n", reg1, label1);
                code += String.format("addiu %s, %s, %d\n", targetReg, reg1, value - 1);
                code += String.format("sra %s, %s, %d\n", targetReg, targetReg, shift);
                code += String.format("j %s\n", label2);
                code += String.format("%s:\n", label1);
                code += String.format("sra %s %s, %d\n", targetReg, reg1, shift);
                code += String.format("%s:\n", label2);
                code += String.format("sll %s, %s, %d\n", targetReg, targetReg, shift);
                code += String.format("subu %s, %s, %s\n", targetReg, reg1, targetReg);
            } else {
                code += String.format("li $t2, %s\n", para2);
                if (conflictGraph.isHaveReg(para1)) {
                    code += String.format("div %s, $t2\n", conflictGraph.getReg(para1));
                    code += String.format("mfhi %s\n", targetReg);
                } else {
                    code += loadTValue(para1);
                    code += String.format("div $t1, $t2\n");
                    code += String.format("mfhi %s\n", targetReg);
                }
            }
        } else {
            //两个都不是数字
            if (conflictGraph.isHaveReg(para1) && conflictGraph.isHaveReg(para2)) {
                code += String.format("div %s, %s\n", conflictGraph.getReg(para1), conflictGraph.getReg(para2));
                code += String.format("mfhi %s\n", targetReg);
            } else if (conflictGraph.isHaveReg(para1)) {
                code += loadTValue(para2);
                code += String.format("div %s, $t1\n", conflictGraph.getReg(para1));
                code += String.format("mfhi %s\n", targetReg);
            } else if (conflictGraph.isHaveReg(para2)) {
                code += loadTValue(para1);
                code += String.format("div $t1, %s\n", conflictGraph.getReg(para2));
                code += String.format("mfhi %s\n", targetReg);
            } else {
                code += loadTValue(para1);
                code += loadT3Value(para2);
                code += String.format("div $t1, $t3\n");
                code += String.format("mfhi %s\n", targetReg);
            }
        }
        return code;
    }

    public String getAssign() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            if (conflictGraph.isHaveReg(para1)) {
               if (isNum(result)) {
                   code += String.format("li %s, %s\n", conflictGraph.getReg(para1), result);
                   return code;
               }
                if (conflictGraph.isHaveReg(result)) {
                     code += String.format("move %s, %s\n", conflictGraph.getReg(para1), conflictGraph.getReg(result));
                     return code;
                }
                code += loadValue(result, conflictGraph.getReg(para1));
                return code;
            } else {
                if (isNum(result)) {
                    code += String.format("li $t0, %s\n", result);
                    code += store(para1);
                    return code;
                }
                if (conflictGraph.isHaveReg(result)) {
                    code += String.format("move $t0, %s\n", conflictGraph.getReg(result));
                    code += store(para1);
                    return code;
                }
                code += loadT0Value(result);
                code += store(para1);
                return code;
            }
        }
        if (isNum(result)) {
            code += String.format("li $t0, %s\n", result);
        } else {
            code += loadT0Value(getRealName(result));
        }
        code += store(getRealName(para1));
        return code;
    }

    public String getGetInt() {
        String code = String.format("li $v0, 5\n");
        code += String.format("syscall\n");
        if (Optimizer.getInstance().getOptimizer()) {
            if (conflictGraph.isHaveReg(result)) {
                code += String.format("move %s, $v0\n", conflictGraph.getReg(result));
                return code;
            }
            code += store(result, "$v0");
            return code;
        }
        code += String.format("move $t0, $v0\n");
        code += store(getRealName(result));
        return code;
    }

    public String getNeg() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String targetReg;
            if (conflictGraph.isHaveReg(result)) {
                targetReg = conflictGraph.getReg(result);
            } else {
                targetReg = "$t0";
            }
            if (isNum(para1)) {
                code += String.format("li $t1, %s\n", para1);
                code += String.format("neg %s, $t1\n", targetReg);
            } else if (conflictGraph.isHaveReg(para1)) {
                code += String.format("neg %s, %s\n", targetReg, conflictGraph.getReg(para1));
            } else {
                code += loadTValue(para1);
                code += String.format("neg %s, $t1\n", targetReg);
            }
            if (targetReg.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        if (isNum(para1)) {
            code += String.format("li $t0, %s\n", para1);
        } else {
            code += loadT0Value(getRealName(para1));
        }
        code += String.format("neg $t0, $t0\n");
        code += store(getRealName(result));
        return code;
    }

    public String getNot() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String targetReg;
            if (conflictGraph.isHaveReg(result)) {
                targetReg = conflictGraph.getReg(result);
            } else {
                targetReg = "$t0";
            }
            if (isNum(para1)) {
                code += String.format("li $t1, %s\n", para1);
                code += String.format("seq %s, $t1, $0\n", targetReg);
            } else if (conflictGraph.isHaveReg(para1)) {
                code += String.format("seq %s, %s, $0\n", targetReg, conflictGraph.getReg(para1));
            } else {
                code += loadTValue(para1);
                code += String.format("seq %s, $t1, $0\n", targetReg);
            }
            if (targetReg.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        if (isNum(para1)) {
            code += String.format("li $t0, %s\n", para1);
        } else {
            code += loadT0Value(getRealName(para1));
        }
        code += String.format("seq $t0, $t0, $0\n");
        code += store(getRealName(result));
        return code;
    }

    public String getSw1D() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String reg;
            if (isNum(result)) {
                code += String.format("li $t0, %s\n", result);
                reg = "$t0";
            } else if (conflictGraph.isHaveReg(result)) {
                reg = conflictGraph.getReg(result);
            } else {
                code += loadT0Value(getRealName(result));
                reg = "$t0";
            }
            code += sw1D(para1, para2, reg);
            return code;
        }
        if (isNum(result)) {
            code += String.format("li $t0, %s\n", result);
        } else {
            code += loadT0Value(getRealName(result));
        }
        code += sw1D(getRealName(para1), getRealName(para2));
        return code;
    }

    //根据数组名和下标，将t0寄存器的值存入其中
    public String sw1D(String arrayName, String index) {
        String code = "";
        if (currentSymbolTable.isGlobal(arrayName)) {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
            int offset = term.getOffset_gp();
            //t2存放数组的首地址
            code += String.format("addiu $t2, $gp, %d\n", offset);
        } else {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
            //如果是形参
            if (term.getLength1() == 0) {
                int offset = term.getOffset_sp();
                code += String.format("lw $t2, %d($sp)\n", offset);
            } else {
                int offset = term.getOffset_sp();
                code += String.format("addiu $t2, $sp, %d\n", offset);
            }
        }
        //t1存放数组的下标
        code += loadTValue(index);
        code += String.format("sll $t1, $t1, 2\n");
        code += String.format("addu $t2, $t2, $t1\n");
        code += String.format("sw $t0, 0($t2)\n");
        return code;
    }

    //优化版本, 将目标寄存器存入数组指定下标位置，注意t0已经被占用
    public String sw1D(String arrayName, String index, String reg) {
        String code = "";
        if (currentSymbolTable.isGlobal(arrayName)) {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
            int offset = term.getOffset_gp();
            //t2存放数组的首地址
            code += String.format("addiu $t2, $gp, %d\n", offset);
        } else {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
            //如果是形参
            if (term.getLength1() == 0) {
                int offset = term.getOffset_sp();
                code += String.format("lw $t2, %d($sp)\n", offset);
            } else {
                int offset = term.getOffset_sp();
                code += String.format("addiu $t2, $sp, %d\n", offset);
            }
        }
        //t1存放数组的下标
        if (isNum(index)) {
            int offset = Integer.parseInt(index) * 4;
            code += String.format("addiu $t2, $t2, %d\n", offset);
        } else if (conflictGraph.isHaveReg(index)) {
            code += String.format("sll $t1, %s, 2\n", conflictGraph.getReg(index));
            code += String.format("addu $t2, $t2, $t1\n");
        } else {
            code += loadTValue(index);
            code += String.format("sll $t1, $t1, 2\n");
            code += String.format("addu $t2, $t2, $t1\n");
        }
        code += String.format("sw %s, 0($t2)\n", reg);
        return code;
    }

    //TODO 都是常数的时候一步到位
    public String getLoadAddress() {
        String code = "";
        String arrayName = getRealName(para1);
        boolean isOpt = Optimizer.getInstance().getOptimizer();
        //取一维数组地址或者二维但无下标
        if (para2 == null) {
            //全局变量
            if (currentSymbolTable.isGlobal(arrayName)) {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
                int offset = term.getOffset_gp();
                if (isOpt && conflictGraph.isHaveReg(result)) {
                    code += String.format("addiu %s, $gp, %d\n", conflictGraph.getReg(result), offset);
                    return code;
                }
                code += String.format("addiu $t0, $gp, %d\n", offset);
            } else {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
                //如果是形参
                if (term.getLength1() == 0) {
                    int offset = currentSymbolTable.searchOffsetSp(arrayName);
                    if (isOpt && conflictGraph.isHaveReg(result)) {
                        code += String.format("lw %s, %d($sp)\n", conflictGraph.getReg(result), offset);
                        return code;
                    }
                    code += String.format("lw $t0, %d($sp)\n", offset);
                } else {
                    int offset = currentSymbolTable.searchOffsetSp(arrayName);
                    if (isOpt && conflictGraph.isHaveReg(result)) {
                        code += String.format("addiu %s, $sp, %d\n", conflictGraph.getReg(result), offset);
                        return code;
                    }
                    code += String.format("addiu $t0, $sp, %d\n", offset);
                }
            }
            code += store(getRealName(result));
        } else {
            //取二维数组地址且有下标为para2
            String index = getRealName(para2);
            if (currentSymbolTable.isGlobal(arrayName)) {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
                int offset = term.getOffset_gp();
                //t2存放数组的首地址
                code += String.format("addiu $t2, $gp, %d\n", offset);
            } else {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
                //如果是形参
                if (term.getLength1() == 0) {
                    int offset = currentSymbolTable.searchOffsetSp(arrayName);
                    code += String.format("lw $t2, %d($sp)\n", offset);
                } else {
                    int offset = currentSymbolTable.searchOffsetSp(arrayName);
                    code += String.format("addiu $t2, $sp, %d\n", offset);
                }
            }
            if (isOpt) {
                int length2 = currentSymbolTable.getSymbolTerm(arrayName).getLength2();
                if (isNum(index)) {
                    int offset = Integer.parseInt(index) * 4 * length2;
                    if (conflictGraph.isHaveReg(result)) {
                        code += String.format("addiu %s, $t2, %d\n", conflictGraph.getReg(result), offset);
                        return code;
                    }
                    code += String.format("addiu $t0, $t2, %d\n", offset);
                } else {
                    if (conflictGraph.isHaveReg(index)) {
                        code += String.format("sll $t1, %s, 2\n", conflictGraph.getReg(index));
                        code += String.format("li $t0, %d\n", length2);
                        code += String.format("mult $t1, $t0\n");
                        code += String.format("mflo $t1\n");
                        if (conflictGraph.isHaveReg(result)) {
                            code += String.format("addu %s, $t2, $t1\n", conflictGraph.getReg(result));
                            return code;
                        }
                        code += String.format("addu $t0, $t2, $t1\n");
                    } else {
                        code += loadTValue(index);
                        code += String.format("sll $t1, $t1, 2\n");
                        code += String.format("li $t0, %d\n", length2);
                        code += String.format("mult $t1, $t0\n");
                        code += String.format("mflo $t1\n");
                        if (conflictGraph.isHaveReg(result)) {
                            code += String.format("addu %s, $t2, $t1\n", conflictGraph.getReg(result));
                            return code;
                        }
                        code += String.format("addu $t0, $t2, $t1\n");
                    }
                }
                code += store(getRealName(result));
                return code;
            }
            code += loadTValue(index);
            int length2 = currentSymbolTable.getSymbolTerm(arrayName).getLength2();
            code += String.format("li $t0, %d\n", length2);
            code += String.format("mult $t1, $t0\n");
            code += String.format("mflo $t1\n");
            code += String.format("sll $t1, $t1, 2\n");
            code += String.format("addu $t0, $t2, $t1\n");
            code += store(getRealName(result));
        }
        return code;
    }


    public String getLoadArray2D() {
        String code = "";
        String arrayName = getRealName(para1);
        String index1 = getRealName(para2);
        String index2 = getRealName(para3);
        if (currentSymbolTable.isGlobal(arrayName)) {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
            int offset = term.getOffset_gp();
            //t2存放数组的首地址
            code += String.format("addiu $t2, $gp, %d\n", offset);
        } else {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
            //如果是形参
            if (term.getLength1() == 0) {
                int offset = currentSymbolTable.searchOffsetSp(arrayName);
                code += String.format("lw $t2, %d($sp)\n", offset);
            } else {
                int offset = currentSymbolTable.searchOffsetSp(arrayName);
                code += String.format("addiu $t2, $sp, %d\n", offset);
            }
        }
        if (Optimizer.getInstance().getOptimizer()) {
            int length2 = currentSymbolTable.getSymbolTerm(arrayName).getLength2();
            //t1存放偏移量，再加上t2的地址就是目标地址，之后存到对应的寄存器或内存
            if (isNum(index1) && isNum(index2)) {
                //两个下标都是常数
                int offset = Integer.parseInt(index1) * length2 + Integer.parseInt(index2);
                code += String.format("addiu $t2, $t2, %d\n", offset * 4);
            } else if (isNum(index1)) {
                //第一个下标是常数
                int offset = Integer.parseInt(index1) * length2;
                code += String.format("addiu $t2, $t2, %d\n", offset * 4);
                if (conflictGraph.isHaveReg(index2)) {
                    code += String.format("sll $t1, %s, 2\n", conflictGraph.getReg(index2));
                    code += String.format("addu $t2, $t2, $t1\n");
                } else {
                    code += loadTValue(index2);
                    code += String.format("sll $t1, $t1, 2\n");
                    code += String.format("addu $t2, $t2, $t1\n");
                }
            } else if (isNum(index2)) {
                //第二个下标是常数
                int offset = Integer.parseInt(index2);
                code += String.format("addiu $t2, $t2, %d\n", offset * 4);
                if (conflictGraph.isHaveReg(index1)) {
                    code += String.format("sll $t1, %s, 2\n", conflictGraph.getReg(index1));
                    code += String.format("li $t0, %d\n", length2);
                    code += String.format("mult $t1, $t0\n");
                    code += String.format("mflo $t1\n");
                    code += String.format("addu $t2, $t2, $t1\n");
                } else {
                    code += loadTValue(index1);
                    code += String.format("sll $t1, $t1, 2\n");
                    code += String.format("li $t0, %d\n", length2);
                    code += String.format("mult $t1, $t0\n");
                    code += String.format("mflo $t1\n");
                    code += String.format("addu $t2, $t2, $t1\n");
                }
            } else {
                //两个下标都是变量
                String reg1;
                String reg2;
                if (conflictGraph.isHaveReg(index1)) {
                    reg1 = conflictGraph.getReg(index1);
                } else {
                    code += loadTValue(index1);
                    reg1 = "$t1";
                }
                if (conflictGraph.isHaveReg(index2)) {
                    reg2 = conflictGraph.getReg(index2);
                } else {
                    code += loadT3Value(index2);
                    reg2 = "$t3";
                }
                code += String.format("li $t0, %d\n", length2);
                code += String.format("mult %s, $t0\n", reg1);
                code += String.format("mflo $t1\n");
                code += String.format("addu $t1, $t1, %s\n", reg2);
                code += String.format("sll $t1, $t1, 2\n");
                code += String.format("addu $t2, $t2, $t1\n");
            }
            if (conflictGraph.isHaveReg(result)) {
                code += String.format("lw %s, 0($t2)\n", conflictGraph.getReg(result));
            } else {
                code += String.format("lw $t0, 0($t2)\n");
                code += store(getRealName(result));
            }
            return code;
        }
        code += loadTValue(index1);
        //t1 = index1 * length2; t2 = address; t3 = index2
        int length2 = currentSymbolTable.getSymbolTerm(arrayName).getLength2();
        code += String.format("li $t0, %d\n", length2);
        code += String.format("mult $t1, $t0\n");
        code += String.format("mflo $t1\n");
        code += loadT3Value(index2);
        //t1 = offset; t0 = address + offset; t0 = value
        code += String.format("addu $t1, $t1, $t3\n");
        code += String.format("sll $t1, $t1, 2\n");
        code += String.format("addu $t0, $t2, $t1\n");
        code += String.format("lw $t0, 0($t0)\n");
        code += store(getRealName(result));
        return code;
    }

    //向二维数组存放数据
    public String getSw2D() {
        String code = "";
        String arrayName = getRealName(para1);
        String index1 = getRealName(para2);
        String index2 = getRealName(para3);
        String realResult = getRealName(this.result);
        if (currentSymbolTable.isGlobal(arrayName)) {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
            int offset = term.getOffset_gp();
            //t2存放数组的首地址
            code += String.format("addiu $t2, $gp, %d\n", offset);
        } else {
            SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
            //如果是形参
            if (term.getLength1() == 0) {
                int offset = currentSymbolTable.searchOffsetSp(arrayName);
                code += String.format("lw $t2, %d($sp)\n", offset);
            } else {
                int offset = currentSymbolTable.searchOffsetSp(arrayName);
                code += String.format("addiu $t2, $sp, %d\n", offset);
            }
        }
        if (Optimizer.getInstance().getOptimizer()) {
            int length2 = currentSymbolTable.getSymbolTerm(arrayName).getLength2();
            //t1存放偏移量，再加上t2的地址就是目标地址，之后存到对应的寄存器或内存
            if (isNum(index1) && isNum(index2)) {
                //两个下标都是常数
                int offset = Integer.parseInt(index1) * length2 + Integer.parseInt(index2);
                code += String.format("addiu $t2, $t2, %d\n", offset * 4);
            } else if (isNum(index1)) {
                //第一个下标是常数
                int offset = Integer.parseInt(index1) * length2;
                code += String.format("addiu $t2, $t2, %d\n", offset * 4);
                if (conflictGraph.isHaveReg(index2)) {
                    code += String.format("sll $t1, %s, 2\n", conflictGraph.getReg(index2));
                    code += String.format("addu $t2, $t2, $t1\n");
                } else {
                    code += loadTValue(index2);
                    code += String.format("sll $t1, $t1, 2\n");
                    code += String.format("addu $t2, $t2, $t1\n");
                }
            } else if (isNum(index2)) {
                //第二个下标是常数
                int offset = Integer.parseInt(index2);
                code += String.format("addiu $t2, $t2, %d\n", offset * 4);
                if (conflictGraph.isHaveReg(index1)) {
                    code += String.format("sll $t1, %s, 2\n", conflictGraph.getReg(index1));
                    code += String.format("li $t0, %d\n", length2);
                    code += String.format("mult $t1, $t0\n");
                    code += String.format("mflo $t1\n");
                    code += String.format("addu $t2, $t2, $t1\n");
                } else {
                    code += loadTValue(index1);
                    code += String.format("sll $t1, $t1, 2\n");
                    code += String.format("li $t0, %d\n", length2);
                    code += String.format("mult $t1, $t0\n");
                    code += String.format("mflo $t1\n");
                    code += String.format("addu $t2, $t2, $t1\n");
                }
            } else {
                //两个下标都是变量
                String reg1;
                String reg2;
                if (conflictGraph.isHaveReg(index1)) {
                    reg1 = conflictGraph.getReg(index1);
                } else {
                    code += loadTValue(index1);
                    reg1 = "$t1";
                }
                if (conflictGraph.isHaveReg(index2)) {
                    reg2 = conflictGraph.getReg(index2);
                } else {
                    code += loadT3Value(index2);
                    reg2 = "$t3";
                }
                code += String.format("li $t0, %d\n", length2);
                code += String.format("mult %s, $t0\n", reg1);
                code += String.format("mflo $t1\n");
                code += String.format("addu $t1, $t1, %s\n", reg2);
                code += String.format("sll $t1, $t1, 2\n");
                code += String.format("addu $t2, $t2, $t1\n");
            }
            if (conflictGraph.isHaveReg(realResult)) {
                code += String.format("sw %s, 0($t2)\n", conflictGraph.getReg(realResult));
            } else {
                code += loadTValue(realResult);
                code += String.format("sw $t1, 0($t2)\n");
            }
            return code;
        }
        code += loadTValue(index1);
        //t1 = index1 * length2; t2 = address; t3 = index2
        int length2 = currentSymbolTable.getSymbolTerm(arrayName).getLength2();
        code += String.format("li $t0, %d\n", length2);
        code += String.format("mult $t1, $t0\n");
        code += String.format("mflo $t1\n");
        code += loadT3Value(index2);
        //t1 = offset; t0 = address + offset; t0 = value
        code += String.format("addu $t1, $t1, $t3\n");
        code += String.format("sll $t1, $t1, 2\n");
        code += String.format("addu $t0, $t2, $t1\n");
        //t0此时已经是最后的地址了
        //t1存放要存放的值
        code += loadTValue(realResult);
        code += String.format("sw $t1, 0($t0)\n");
        return code;

    }

    public String getPutLabel() {
        return String.format("%s:\n", result);
    }

    public String getEQ() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String target;
            if (conflictGraph.isHaveReg(result)) {
                target = conflictGraph.getReg(result);
            } else {
                target = "$t0";
            }
            String reg1;
            String reg2;
            if (conflictGraph.isHaveReg(para1)) {
                reg1 = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg1 = "$t1";
            }
            if (conflictGraph.isHaveReg(para2)) {
                reg2 = conflictGraph.getReg(para2);
            } else {
                code += loadT3Value(para2);
                reg2 = "$t3";
            }
            code += String.format("seq %s, %s, %s\n", target, reg1, reg2);
            if (target.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "seq $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getNE() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String target;
            if (conflictGraph.isHaveReg(result)) {
                target = conflictGraph.getReg(result);
            } else {
                target = "$t0";
            }
            String reg1;
            String reg2;
            if (conflictGraph.isHaveReg(para1)) {
                reg1 = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg1 = "$t1";
            }
            if (conflictGraph.isHaveReg(para2)) {
                reg2 = conflictGraph.getReg(para2);
            } else {
                code += loadT3Value(para2);
                reg2 = "$t3";
            }
            code += String.format("sne %s, %s, %s\n", target, reg1, reg2);
            if (target.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "sne $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getBEQZ() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String reg;
            if (conflictGraph.isHaveReg(para1)) {
                reg = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg = "$t1";
            }
            code += String.format("beqz %s, %s\n", reg, result);
            return code;
        }
        code += loadT0Value(getRealName(para1));
        code += String.format("beqz $t0, %s\n", result);
        return code;
    }

    public String getBNEZ() {
        String code = "";
        if (Optimizer.getInstance().getOptimizer()) {
            String reg;
            if (conflictGraph.isHaveReg(para1)) {
                reg = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg = "$t1";
            }
            code += String.format("bnez %s, %s\n", reg, result);
            return code;
        }
        code += loadT0Value(getRealName(para1));
        code += String.format("bnez $t0, %s\n", result);
        return code;
    }

    public String getLT() {
        String code = "";
        //TODO 优化slti
        if (Optimizer.getInstance().getOptimizer()) {
            String target;
            if (conflictGraph.isHaveReg(result)) {
                target = conflictGraph.getReg(result);
            } else {
                target = "$t0";
            }
            String reg1;
            String reg2;
            if (conflictGraph.isHaveReg(para1)) {
                reg1 = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg1 = "$t1";
            }
            if (conflictGraph.isHaveReg(para2)) {
                reg2 = conflictGraph.getReg(para2);
            } else {
                code += loadT3Value(para2);
                reg2 = "$t3";
            }
            code += String.format("slt %s, %s, %s\n", target, reg1, reg2);
            if (target.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "slt $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getLE() {
        String code = "";
        //TODO 优化slti
        if (Optimizer.getInstance().getOptimizer()) {
            String target;
            if (conflictGraph.isHaveReg(result)) {
                target = conflictGraph.getReg(result);
            } else {
                target = "$t0";
            }
            String reg1;
            String reg2;
            if (conflictGraph.isHaveReg(para1)) {
                reg1 = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg1 = "$t1";
            }
            if (conflictGraph.isHaveReg(para2)) {
                reg2 = conflictGraph.getReg(para2);
            } else {
                code += loadT3Value(para2);
                reg2 = "$t3";
            }
            code += String.format("sgt $t1, %s, %s\n", reg1, reg2);
            code += String.format("seq %s, $t1, $0\n", target);
            if (target.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "sle $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getGT() {
        String code = "";
        //TODO 优化slti
        if (Optimizer.getInstance().getOptimizer()) {
            String target;
            if (conflictGraph.isHaveReg(result)) {
                target = conflictGraph.getReg(result);
            } else {
                target = "$t0";
            }
            String reg1;
            String reg2;
            if (conflictGraph.isHaveReg(para1)) {
                reg1 = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg1 = "$t1";
            }
            if (conflictGraph.isHaveReg(para2)) {
                reg2 = conflictGraph.getReg(para2);
            } else {
                code += loadT3Value(para2);
                reg2 = "$t3";
            }
            code += String.format("sgt %s, %s, %s\n", target, reg1, reg2);
            if (target.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "sgt $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getGE() {
        String code = "";
        //TODO 优化slti
        if (Optimizer.getInstance().getOptimizer()) {
            String target;
            if (conflictGraph.isHaveReg(result)) {
                target = conflictGraph.getReg(result);
            } else {
                target = "$t0";
            }
            String reg1;
            String reg2;
            if (conflictGraph.isHaveReg(para1)) {
                reg1 = conflictGraph.getReg(para1);
            } else {
                code += loadTValue(para1);
                reg1 = "$t1";
            }
            if (conflictGraph.isHaveReg(para2)) {
                reg2 = conflictGraph.getReg(para2);
            } else {
                code += loadT3Value(para2);
                reg2 = "$t3";
            }
            code += String.format("slt $t1, %s, %s\n", reg1, reg2);
            code += String.format("seq %s, $t1, $0\n", target);
            if (target.equals("$t0")) {
                code += store(result);
            }
            return code;
        }
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "sge $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getGoto() {
        return String.format("j %s\n", result);
    }

    public boolean getIsEntrance() {
        return isEntrance;
    }

    public void setIsEntrance(boolean isEntrance) {
        this.isEntrance = isEntrance;
    }

    public HashSet<String> getUseSet() {
        return useSet;
    }

    public HashSet<String> getDefSet() {
        return defSet;
    }

    public void addToUseSet(String name) {
        useSet.add(name);
    }

    public void addToDefSet(String name) {
        defSet.add(name);
    }

    public HashSet<String> getInSet() {
        return inSet;
    }

    public HashSet<String> getOutSet() {
        return outSet;
    }

    public void setConflictGraph(ConflictGraph conflictGraph) {
        this.conflictGraph = conflictGraph;
    }

    public void setOp(OpType op) {
        this.op = op;
    }

}
