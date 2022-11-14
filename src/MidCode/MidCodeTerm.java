package MidCode;

import Node.ErrorAnalysis;
import Node.SymbolTable;
import Node.SymbolTerm;
import Type.OpType;

import java.util.ArrayList;

public class MidCodeTerm {
    private OpType op;
    private String para1;
    private String para2;
    private String para3;
    private String result;
    private final String funcDec = "##################FUNC_DEC##################\n";
    private SymbolTable currentSymbolTable;

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
                return OpType.PRINT_INT + "  " + para1;
            case PRINT_STRING:
                return OpType.PRINT_STRING + "  " + para1;
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
                return OpType.SW_RET + " " + para1;
            case EXIT:
                return OpType.EXIT + " " + para1;
            case RET_VOID:
                return OpType.RET_VOID + " ";
            case RET_VALUE:
                return OpType.RET_VALUE + " " + para1;
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
        int size = symbolTable.getSize() + 4;
        code += String.format("subiu $sp,$sp,%d\n",size);
        SymbolTable.addStackSize(size);
        return code;
    }

    public String getCall() {
        String code = String.format("jal %s\n", result);
        return code;
    }

    public String getFinCall() {
        String name = result;
        SymbolTable symbolTable = ErrorAnalysis.getInstance().getFunc2Symbol(name);
        int size = symbolTable.getSize() + 4;
        String code = String.format("addiu $sp,$sp,%d\n", size);
        SymbolTable.addStackSize(-size);
        code += String.format("lw $ra, 0($sp)\n");
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
        String para_temp = para1;
        String code = load1D(para_temp, getRealName(para2));
        code += store(result);
        return code;
    }

    public String getRetVoid() {
        return String.format("jr $ra\n");
    }

    public String getRetValue() {
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
        code += loadTValue(getRealName(result));
        code += String.format("move $a0, $t1\n");
        code += String.format("syscall\n");
        return code;
    }

    public String getAdd() {
        String code = "";
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

    public String getMul() {
        String code = "";
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

    public String getMod() {
        String code = "";
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

    public String getAssign() {
        String code = "";
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
        code += String.format("move $t0, $v0\n");
        code += store(getRealName(result));
        return code;
    }

    public String getNeg() {
        String code = "";
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

    public String getLoadAddress() {
        String code = "";
        String arrayName = getRealName(para1);
        //取一维数组地址或者二维但无下标
        if (para2 == null) {
            //全局变量
            if (currentSymbolTable.isGlobal(arrayName)) {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
                int offset = term.getOffset_gp();
                code += String.format("addiu $t0, $gp, %d\n", offset);
            } else {
                SymbolTerm term = currentSymbolTable.getSymbolTerm(arrayName);
                //如果是形参
                if (term.getLength1() == 0) {
                    int offset = currentSymbolTable.searchOffsetSp(arrayName);
                    code += String.format("lw $t0, %d($sp)\n", offset);
                } else {
                    int offset = currentSymbolTable.searchOffsetSp(arrayName);
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
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "seq $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getNE() {
        String code = "";
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "sne $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getBEQZ() {
        String code = "";
        code += loadT0Value(getRealName(para1));
        code += String.format("beqz $t0, %s\n", result);
        return code;
    }

    public String getBNEZ() {
        String code = "";
        code += loadT0Value(getRealName(para1));
        code += String.format("bnez $t0, %s\n", result);
        return code;
    }

    public String getLT() {
        String code = "";
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "slt $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getLE() {
        String code = "";
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "sle $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getGT() {
        String code = "";
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "sgt $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getGE() {
        String code = "";
        code += loadT0Value(getRealName(para1));
        code += loadTValue(getRealName(para2));
        code += "sge $t0, $t0, $t1\n";
        code += store(getRealName(result));
        return code;
    }

    public String getGoto() {
        return String.format("j %s\n", result);
    }
}
