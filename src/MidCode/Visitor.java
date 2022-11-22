package MidCode;

import Node.Node;
import Node.NonLeafNode;
import Node.ErrorAnalysis;
import Node.SymbolTable;
import Node.*;
import Optimize.Optimizer;
import Type.OpType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Visitor {
    private NonLeafNode root;
    private final CodeManager codeManager = CodeManager.getInstance();
    private final ErrorAnalysis errorAnalysis = ErrorAnalysis.getInstance();
    private final SymbolTable rootTable = errorAnalysis.getRootTable();
    private SymbolTable currentTable = rootTable;
    private boolean isFuncDef = false;
    private boolean isMain = false;
    private boolean isGlobal = true;
    private static Visitor visitor = new Visitor();

    public Visitor(NonLeafNode root) {
        this.root = root;
        rootTable.setZero();
        this.getMidCode(root);
    }

    public Visitor() {

    }

    public String getMidCode(Node node) {
        if (node.getSymbol().equals("ConstDef")) {
            return dealConstDef(node);
        } else if (node.getSymbol().equals("VarDef")) {
            return dealVarDef(node);
           // return "0";
        } else if (node.getSymbol().equals("AddExp")) {
            return dealAddExp(node);
        } else if (node.getSymbol().equals("MulExp")) {
            return dealMulExp(node);
        } else if (node.getSymbol().equals("UnaryExp")) {
            return dealUnaryExp(node, true);
        } else if (node.getSymbol().equals("PrimaryExp")) {
            return dealPrimaryExp(node);
        } else if (node.getSymbol().equals("LVal")) {
            return dealLVal(node, false);
        } else if (node.getSymbol().equals("InitVal")) {
            return dealInitVal(node);
        } else if (node.getSymbol().equals("Exp")) {
            return dealExp(node);
        } else if (node.getSymbol().equals("Block")) {
            return dealBlock(node);
        } else if (node.getSymbol().equals("Stmt")) {
            return dealStmt(node);
        } else if (node.getSymbol().equals("GETINTTK")) {
            return dealGetInt(node);
        } else if (node.getSymbol().equals("FuncDef")) {
            return dealFuncDef(node);
        } else if (node.getSymbol().equals("FuncFParams")) {
            return dealFuncFParams(node);
        } else if (node.getSymbol().equals("FuncFParam")) {
            return dealFuncFParam(node);
        } else if (node.getSymbol().equals("MainFuncDef")) {
            return dealMainFuncDef(node);
        } else if (node.getSymbol().equals("RelExp")) {
            return dealRelExp(node);
        } else if (node.getSymbol().equals("EqExp")) {
            return dealEqExp(node);
        }
        else {
            for (Node son : node.getSons()) {
                getMidCode(son);
            }
        }
        return "0";
    }

    public int getDim(Node node) {
        int dim = 0;
        for (Node son: node.getSons()) {
            if (son.getSymbol().equals("LBRACK")) {
                dim++;
            }
        }
        return dim;
    }

    public String dealConstDef(Node node) {
        assert node.getSymbol().equals("ConstDef");
        String name = node.getChild(0).getWord();
        int dim = getDim(node);
        String identifier;
        if (isGlobal) {
            identifier = name + "_-1";
        } else {
            identifier = name + "_" + node.getChild(0).getLine();
        }
        if (dim == 0) {
            int index = node.getSons().size() - 1;
            int value = ((NonLeafNode)node.getChild(index).getChild(0)).getConstExpValue(currentTable);
            SymbolTerm symbolTerm = new SymbolTerm(name, dim, value);
            symbolTerm.setLine(isGlobal ? -1 : node.getChild(0).getLine());
            symbolTerm.setConst(true);
            symbolTerm.setSize(false);
            if (isGlobal) {
                symbolTerm.setAddressGp();
            }
            currentTable.addTerm(symbolTerm);
            if (isGlobal) {
                codeManager.addMidCode(new MidCodeTerm(OpType.GLOBAL_DEC, null, null, identifier, currentTable));
            } else {
                CodeManager.getInstance().addMidCode(new MidCodeTerm(OpType.ASSIGN, identifier, null, String.valueOf(value), currentTable));
            }
        } else if (dim == 1) {
            int length1D = ((NonLeafNode) node).getLength1D(currentTable);
            int index = node.getSons().size() - 1;
            ArrayList<Integer> length1Value = ((NonLeafNode) node.getChild(index)).get1DValue(currentTable);
            SymbolTerm symbolTerm = new SymbolTerm(name, 1, length1D, length1Value);
            symbolTerm.setLine(isGlobal ? -1 : node.getChild(0).getLine());
            symbolTerm.setConst(true);
            symbolTerm.setSize(false);
            if (isGlobal) {
                symbolTerm.setAddressGp();
            }
            currentTable.addTerm(symbolTerm);
            if (isGlobal) {
                codeManager.addMidCode(new MidCodeTerm(OpType.GLOBAL_DEC, null, null, identifier, currentTable));
            } else {
                for (int i = 0; i < length1Value.size(); i++) {
                    codeManager.addMidCode(new MidCodeTerm(OpType.SW_1D, identifier, String.valueOf(i), String.valueOf(length1Value.get(i)), currentTable));
                }
            }
        } else if (dim == 2) {
            int length1D = ((NonLeafNode) node).getLength1D(currentTable);
            int length2D = ((NonLeafNode) node).getLength2D(currentTable);
            int index = node.getSons().size() - 1;
            ArrayList<ArrayList<Integer>> length2Value = ((NonLeafNode) node.getChild(index)).get2DValue(currentTable);
            SymbolTerm symbolTerm = new SymbolTerm(name, 2, length1D, length2D, length2Value);
            symbolTerm.setLine(isGlobal ? -1 : node.getChild(0).getLine());
            symbolTerm.setConst(true);
            symbolTerm.setSize(false);
            if (isGlobal) {
                symbolTerm.setAddressGp();
            }
            currentTable.addTerm(symbolTerm);
            if (isGlobal) {
                codeManager.addMidCode(new MidCodeTerm(OpType.GLOBAL_DEC, null, null, identifier, currentTable));
            } else {
                for (int i = 0; i < length2Value.size(); i++) {
                    for (int j = 0; j < length2Value.get(i).size(); j++) {
                        codeManager.addMidCode(new MidCodeTerm(OpType.SW_2D, identifier, String.valueOf(i), String.valueOf(j), String.valueOf(length2Value.get(i).get(j)), currentTable));
                    }
                }
            }
        }
        return "0";
    }

    public String dealVarDef(Node node) {
        String name = node.getChild(0).getWord();
        int dim = getDim(node);
        String identifier;
        if (isGlobal) {
            identifier = name + "_-1";
        } else {
           identifier = name + "_" + node.getChild(0).getLine();
        }
        if (!node.getChild(node.getSons().size() - 1).getSymbol().equals("InitVal")) {
            if (dim == 0) {
                SymbolTerm symbolTerm = new SymbolTerm(name, dim, 0);
                symbolTerm.setLine(isGlobal ? -1 : node.getChild(0).getLine());
                symbolTerm.setSize(false);
                if (isGlobal) {
                    symbolTerm.setAddressGp();
                }
                currentTable.addTerm(symbolTerm);
            } else if (dim == 1) {
                int length1D = ((NonLeafNode) node).getLength1D(currentTable);
                SymbolTerm symbolTerm = new SymbolTerm(name, 1, 0);
                symbolTerm.setLength1(length1D);
                symbolTerm.setLine(isGlobal ? -1 : node.getChild(0).getLine());
                symbolTerm.setSize(false);
                if (isGlobal) {
                    symbolTerm.setAddressGp();
                }
                currentTable.addTerm(symbolTerm);
            } else if (dim == 2) {
                int length1D = ((NonLeafNode) node).getLength1D(currentTable);
                int length2D = ((NonLeafNode) node).getLength2D(currentTable);
                SymbolTerm symbolTerm = new SymbolTerm(name, 2, length1D, length2D);
                symbolTerm.setLine(isGlobal ? -1 : node.getChild(0).getLine());
                symbolTerm.setSize(false);
                if (isGlobal) {
                    symbolTerm.setAddressGp();
                }
                currentTable.addTerm(symbolTerm);
            }
            if (isGlobal) {
                codeManager.addMidCode(new MidCodeTerm(OpType.GLOBAL_DEC, null, null, identifier, currentTable));
            }
        } else {
            if (dim == 0) {
                int index = node.getSons().size() - 1;
                if (isGlobal) {
                    NonLeafNode AddExp = (NonLeafNode) node.getChild(index).getChild(0).getChild(0);
                    int temp = AddExp.getAddExpValue(currentTable);
                    SymbolTerm symbolTerm = new SymbolTerm(name, dim, temp);
                    symbolTerm.setLine(-1);
                    symbolTerm.setSize(false);
                    symbolTerm.setAddressGp();
                    currentTable.addTerm(symbolTerm);
                    codeManager.addMidCode(new MidCodeTerm(OpType.GLOBAL_DEC, null, null, identifier, currentTable));
                    return "0";
                }
                String value = getMidCode(node.getChild(index).getChild(0));
                SymbolTerm symbolTerm = new SymbolTerm(name, 0, 0);
                symbolTerm.setLine(node.getChild(0).getLine());
                symbolTerm.setSize(false);
                currentTable.addTerm(symbolTerm);
                codeManager.addMidCode(new MidCodeTerm(OpType.ASSIGN, identifier, null, value, currentTable));
            } else if (dim == 1) {
                int length1D = ((NonLeafNode) node).getLength1D(currentTable);
                if (isGlobal) {
                    codeManager.addMidCode(new MidCodeTerm(OpType.GLOBAL_DEC, null, null, identifier, currentTable));
                    ArrayList<Integer> length1Value = ((NonLeafNode) node.getChild(node.getSons().size() - 1)).get1DValue(currentTable);
                    SymbolTerm symbolTerm = new SymbolTerm(name, 1, length1D, length1Value);
                    symbolTerm.setLine(-1);
                    symbolTerm.setSize(false);
                    symbolTerm.setAddressGp();
                    currentTable.addTerm(symbolTerm);
                    return "0";
                }
                SymbolTerm symbolTerm = new SymbolTerm(name, 1, 0);
                symbolTerm.setLength1(length1D);
                symbolTerm.setLine(node.getChild(0).getLine());
                symbolTerm.setSize(false);
                currentTable.addTerm(symbolTerm);
                int index = node.getSons().size() - 1;
                Node initVal = node.getChild(index);
                ArrayList<String> value = new ArrayList<>();
                for (int i = 0; i < initVal.getSons().size(); i++) {
                    if (initVal.getChild(i).getSymbol().equals("InitVal")) {
                        value.add(getMidCode(initVal.getChild(i)));
                    }
                }
                for (int i = 0; i < value.size(); i++) {
                    codeManager.addMidCode(new MidCodeTerm(OpType.SW_1D, identifier, String.valueOf(i), value.get(i), currentTable));
                }
            } else {
                int length1D = ((NonLeafNode) node).getLength1D(currentTable);
                int length2D = ((NonLeafNode) node).getLength2D(currentTable);
                if (isGlobal) {
                    codeManager.addMidCode(new MidCodeTerm(OpType.GLOBAL_DEC, null, null, identifier, currentTable));
                    ArrayList<ArrayList<Integer>> length2Value = ((NonLeafNode) node.getChild(node.getSons().size() - 1)).get2DValue(currentTable);
                    SymbolTerm symbolTerm = new SymbolTerm(name, 2, length1D, length2D, length2Value);
                    symbolTerm.setLine(-1);
                    symbolTerm.setSize(false);
                    symbolTerm.setAddressGp();
                    currentTable.addTerm(symbolTerm);
                    return "0";
                }
                SymbolTerm symbolTerm = new SymbolTerm(name, 2, length1D, length2D);
                symbolTerm.setLine(node.getChild(0).getLine());
                symbolTerm.setSize(false);
                currentTable.addTerm(symbolTerm);
                int index = node.getSons().size() - 1;
                Node initVal = node.getChild(index);
                ArrayList<ArrayList<String>> value = new ArrayList<>();
                for (int i = 0; i < initVal.getSons().size(); i++) {
                    if (initVal.getChild(i).getSymbol().equals("InitVal")) {
                        ArrayList<String> temp = new ArrayList<>();
                        for (int j = 0; j < initVal.getChild(i).getSons().size(); j++) {
                            if (initVal.getChild(i).getChild(j).getSymbol().equals("InitVal")) {
                                temp.add(getMidCode(initVal.getChild(i).getChild(j)));
                            }
                        }
                        value.add(temp);
                    }
                }
                for (int i = 0; i < value.size(); i++) {
                    for (int j = 0; j < value.get(i).size(); j++) {
                        codeManager.addMidCode(new MidCodeTerm(OpType.SW_2D, identifier, String.valueOf(i), String.valueOf(j), value.get(i).get(j), currentTable));
                    }
                }
            }
        }
        return "0";
    }

    public String dealInitVal(Node node) {
        return getMidCode(node.getChild(0));
    }

    public String dealExp(Node node) {
        return getMidCode(node.getChild(0));
    }

    public String dealAddExp(Node node) {
        assert node.getSymbol().equals("AddExp");
        if (node.getSons().size() == 1) {
            return getMidCode(node.getChild(0));
        } else {
            String left = getMidCode(node.getChild(0));
            String right = getMidCode(node.getChild(2));
            OpType op = node.getChild(1).getSymbol().equals("PLUS")? OpType.ADD : OpType.SUB;
            if (isNum(left) && isNum(right)) {
                int leftValue = Integer.parseInt(left);
                int rightValue = Integer.parseInt(right);
                if (op == OpType.ADD) {
                    return String.valueOf(leftValue + rightValue);
                } else {
                    return String.valueOf(leftValue - rightValue);
                }
            } else if(isNum(left) && Integer.parseInt(left) == 0) {
                if (op == OpType.ADD) {
                    return right;
                } else {
                    String result = codeManager.getTemp(currentTable);
                    codeManager.addMidCode(new MidCodeTerm(OpType.NEG, right, null, result, currentTable));
                    return result;
                }
            } else if (isNum(right) && Integer.parseInt(right) == 0) {
                return left;
            }
            String result = codeManager.getTemp(currentTable);
            codeManager.addMidCode(new MidCodeTerm(op, left, right, result, currentTable));
            return result;
        }
    }

    //TODO 那neg改成0-sub
    public String dealMulExp(Node node) {
        assert node.getSymbol().equals("MulExp");
        if (node.getSons().size() == 1) {
            return getMidCode(node.getChild(0));
        } else {
            String left = getMidCode(node.getChild(0));
            String right = getMidCode(node.getChild(2));
            OpType op = node.getChild(1).getSymbol().equals("MULT") ? OpType.MUL : (node.getChild(1).getSymbol().equals("DIV") ? OpType.DIV : OpType.MOD);
            if (isNum(left) && isNum(right)) {
                int leftValue = Integer.parseInt(left);
                int rightValue = Integer.parseInt(right);
                int resultValue = 0;
                switch (op) {
                    case MUL:
                        resultValue = leftValue * rightValue;
                        break;
                    case DIV:
                        resultValue = leftValue / rightValue;
                        break;
                    case MOD:
                        resultValue = leftValue % rightValue;
                        break;
                }
                return String.valueOf(resultValue);
            } else if (op.equals(OpType.MUL) && isNum(left) && (Integer.parseInt(left) == 1 || Integer.parseInt(left) == -1)) {
                if (Integer.parseInt(left) == 1) {
                    return right;
                } else {
                    String result = codeManager.getTemp(currentTable);
                    codeManager.addMidCode(new MidCodeTerm(OpType.NEG, right, null, result, currentTable));
                    return result;
                }
            } else if (op != OpType.MOD && isNum(right) && (Integer.parseInt(right) == 1 || Integer.parseInt(right) == -1)) {
                if (Integer.parseInt(right) == 1) {
                    return left;
                } else {
                    String result = codeManager.getTemp(currentTable);
                    codeManager.addMidCode(new MidCodeTerm(OpType.NEG, left, null, result, currentTable));
                    return result;
                }
            } else if (op.equals(OpType.MOD) && isNum(right) && (Integer.parseInt(right) == 1 || Integer.parseInt(right) == -1)) {
                return "0";
            } else if (op.equals(OpType.MUL) && isNum(left) && Integer.parseInt(left) == 0) {
                return "0";
            } else if (op.equals(OpType.MUL) && isNum(right) && Integer.parseInt(right) == 0) {
                return "0";
            } else if ((op.equals(OpType.DIV) || op.equals(OpType.MOD)) && isNum(left) && Integer.parseInt(left) == 0) {
                return "0";
            }
            String result = codeManager.getTemp(currentTable);
            codeManager.addMidCode(new MidCodeTerm(op, left, right, result, currentTable));
            return result;
        }
    }

    // isMulExp表示是否直接被mulExp调用
    public String dealUnaryExp(Node node, boolean isMulExp) {
        assert node.getSymbol().equals("UnaryExp");
        if (node.getSons().size() == 1) {
            return getMidCode(node.getChild(0));
        } else if (node.getChild(0).getSymbol().equals("UnaryOp")) {
//            if (!isMulExp) {
//                return dealUnaryExp(node.getChild(1), false);
//            }
//            int numOfMINU = ((NonLeafNode) node).getNumMINU();
//            int numOfNOT = ((NonLeafNode) node).getNumNOT();
//            String result = dealUnaryExp(node.getChild(1), false);
//            if (numOfMINU % 2 == 1) {
//                String temp = codeManager.getTemp(currentTable);
//                codeManager.addMidCode(new MidCodeTerm(OpType.NEG, result, null, temp, currentTable));
//                result = temp;
//            }
//            if (numOfNOT % 2 == 1) {
//                String temp = codeManager.getTemp(currentTable);
//                codeManager.addMidCode(new MidCodeTerm(OpType.NOT, result, null, temp, currentTable));
//                result = temp;
//            }
            String op = node.getChild(0).getChild(0).getSymbol();
            String temp = codeManager.getTemp(currentTable);
            String result = dealUnaryExp(node.getChild(1), false);
            if (op.equals("MINU")) {
                if (isNum(result)) {
                    return String.valueOf(-Integer.parseInt(result));
                }
                codeManager.addMidCode(new MidCodeTerm(OpType.NEG, result, null, temp, currentTable));
                result = temp;
            } else if (op.equals("NOT")) {
                if (isNum(result)) {
                    int num = Integer.parseInt(result);
                    if (num == 0) {
                        return "1";
                    } else {
                        return "0";
                    }
                }
                codeManager.addMidCode(new MidCodeTerm(OpType.NOT, result, null, temp, currentTable));
                result = temp;
            } else {
                return result;
            }
            return result;
        } else if (node.getChild(0).getSymbol().equals("IDENFR")){
            String name = node.getChild(0).getWord();
            codeManager.addMidCode(new MidCodeTerm(OpType.PRE_CALL, null, null, name, currentTable));
            Node FuncRParams = node.getChild(2);
            int cnt = 0;
            for (Node exp: FuncRParams.getSons()) {
                if (exp.getSymbol().equals("Exp")) {
                    String param = getMidCode(exp);
                    codeManager.addMidCode(new MidCodeTerm(OpType.PUSH, param, Integer.toString(cnt++), name, currentTable));
                }
            }
            codeManager.addMidCode(new MidCodeTerm(OpType.CALL, null, null, name, currentTable));
            codeManager.addMidCode(new MidCodeTerm(OpType.FIN_CALL, null, null, name, currentTable));
            String type = getFuncType(name);
            if (type.equals("void")) {
                return "114514";
            } else {
                String result = codeManager.getTemp(currentTable);
                codeManager.addMidCode(new MidCodeTerm(OpType.SW_RET, null, null, result, currentTable));
                return result;
            }
        }
        return "114514";
    }

    public String dealPrimaryExp(Node node) {
        if (node.getChild(0).getSymbol().equals("Number")) {
            return node.getChild(0).getChild(0).getWord();
        } else if (node.getSons().size() > 1) {
            return getMidCode(node.getChild(1));
        } else if (node.getChild(0).getSymbol().equals("LVal")){
            return dealLVal(node.getChild(0), true);
        }
        return "114514";
    }

    public String dealLVal(Node node, boolean isPrimary) {
        if (isPrimary) {
            String name = node.getChild(0).getWord();
            String identifier = currentTable.getNowName(name);
            int dim = currentTable.getDim(name);
            if (node.getSons().size() == 1) {
                if (dim == 0) {
                    if (currentTable.visitIsConst(name)) {
                        int value = currentTable.getVisitValue(name);
                        return String.valueOf(value);
                    }
                    return identifier;
                } else {
                    String temp = codeManager.getTemp(currentTable);
                    codeManager.addMidCode(new MidCodeTerm(OpType.LOAD_ADDRESS, identifier, null, temp, currentTable));
                    return temp;
                }
            } else if (node.getSons().size() == 4) {
                if (dim == 1) {
                    String index = getMidCode(node.getChild(2));
                    if (isNum(index) && currentTable.visitIsConst(name)) {
                        int value = currentTable.getVisit1DValue(name, Integer.parseInt(index));
                        return String.valueOf(value);
                    }
                    String temp = codeManager.getTemp(currentTable);
                    codeManager.addMidCode(new MidCodeTerm(OpType.LOAD_ARRAY_1D, identifier, index, temp, currentTable));
                    return temp;
                } else {
                    String index1 = getMidCode(node.getChild(2));
                    String temp = codeManager.getTemp(currentTable);
                    codeManager.addMidCode(new MidCodeTerm(OpType.LOAD_ADDRESS, identifier, index1, temp, currentTable));
                    return temp;
                }
            } else {
                String index1 = getMidCode(node.getChild(2));
                String index2 = getMidCode(node.getChild(5));
                if (isNum(index1) && isNum(index2) && currentTable.visitIsConst(name)) {
                    int value = currentTable.getVisit2DValue(name, Integer.parseInt(index1), Integer.parseInt(index2));
                    return String.valueOf(value);
                }
                String temp = codeManager.getTemp(currentTable);
                codeManager.addMidCode(new MidCodeTerm(OpType.LOAD_ARRAY_2D, identifier, index1, index2, temp, currentTable));
                return temp;
            }
        }
        return "114514";
    }

    public String dealBlock(Node node) {
        assert node.getSymbol().equals("Block");
        boolean isMeDone = true;
        if (isFuncDef) {
            isMeDone = false;
            isFuncDef = false;
        } else {
            currentTable = currentTable.getNextSon();
        }
        for (int i = 0; i < node.getSons().size(); i++) {
            getMidCode(node.getChild(i));
        }
        if (isMeDone) {
            currentTable = currentTable.getFather();
        }
        return "0";
    }

    public String dealStmt(Node node) {
        if (node.getChild(0).getSymbol().equals("LVal")) {
            String name = node.getChild(0).getChild(0).getWord();
            String identifier = currentTable.getNowName(name);
            int dim = currentTable.getDim(name);
            NonLeafNode LVal = (NonLeafNode) node.getChild(0);
            if (LVal.getSons().size() == 1) {
                String exp = getMidCode(node.getChild(2));
                codeManager.addMidCode(new MidCodeTerm(OpType.ASSIGN,identifier, null, exp, currentTable));
            } else if (LVal.getSons().size() == 4) {
                if (dim == 1) {
                    String index = getMidCode(LVal.getChild(2));
                    String exp = getMidCode(node.getChild(2));
                    codeManager.addMidCode(new MidCodeTerm(OpType.SW_1D, identifier, index, exp, currentTable));
                }
            } else if (dim == 2) {
                String index1 = getMidCode(LVal.getChild(2));
                String index2 = getMidCode(LVal.getChild(5));
                String exp = getMidCode(node.getChild(2));
                codeManager.addMidCode(new MidCodeTerm(OpType.SW_2D, identifier, index1, index2, exp, currentTable));
            }
        } else if (node.getChild(0).getSymbol().equals("PRINTFTK")) {
            String str = node.getChild(2).getWord().substring(1, node.getChild(2).getWord().length() - 1);
            int cnt = 4;
            Pattern pattern = Pattern.compile("%d|[^%]*");
            Matcher matcher = pattern.matcher(str);
                ArrayList<MidCodeTerm> temp = new ArrayList<>();
            while (matcher.find()) {
                if (matcher.group().equals("%d")) {
                    String exp = getMidCode(node.getChild(cnt));
                    cnt += 2;
                   temp.add(new MidCodeTerm(OpType.PRINT_INT, null, null, exp, currentTable));
                } else if (!matcher.group().equals("")) {
                    temp.add(new MidCodeTerm(OpType.PRINT_STRING, null, null,codeManager.getStr(matcher.group()), currentTable));
                }
            }
            for (MidCodeTerm midCodeTerm: temp) {
                codeManager.addMidCode(midCodeTerm);
            }
        } else if (node.getChild(0).getSymbol().equals("RETURNTK")) {
            if (isMain) {
                codeManager.addMidCode(new MidCodeTerm(OpType.EXIT, null, null, null, currentTable));
            } else if (node.getSons().size() == 3) {
                String exp = getMidCode(node.getChild(1));
                codeManager.addMidCode(new MidCodeTerm(OpType.RET_VALUE,  null, null, exp, currentTable));
            } else {
                codeManager.addMidCode(new MidCodeTerm(OpType.RET_VOID, null, null, null, currentTable));
            }
        } else if (node.getChild(0).getSymbol().equals("IFTK")) {
                dealIf(node);
        } else if (node.getChild(0).getSymbol().equals("WHILETK")) {
                dealWhile(node);
        } else if (node.getChild(0).getSymbol().equals("BREAKTK")) {
                dealBreak(node);
        } else if (node.getChild(0).getSymbol().equals("CONTINUETK")) {
                dealContinue(node);
        } else {
            for (int i = 0; i < node.getSons().size(); i++) {
                getMidCode(node.getChild(i));
            }
        }
        return "114514";
    }

    public String dealGetInt(Node node) {
        String temp = codeManager.getTemp(currentTable);
        codeManager.addMidCode(new MidCodeTerm(OpType.GetInt,  null, null, temp, currentTable));
        return temp;
    }

    public String dealFuncDef(Node node) {
        isFuncDef = true;
        isGlobal = false;
        String name = node.getChild(1).getWord();
        SymbolTable.resetSp();
        codeManager.addMidCode(new MidCodeTerm(OpType.FUNC_DEC, null, null, name, currentTable));
        currentTable = currentTable.getNextSon();
        for (Node son: node.getSons()) {
            getMidCode(son);
        }
        currentTable.setAddressSp();
        currentTable = currentTable.getFather();
        if (errorAnalysis.getFuncTable().get(name).getType().equals("void")) {
            codeManager.addMidCode(new MidCodeTerm(OpType.RET_VOID, null, null, null, currentTable));
        }
        return "114514";
    }

    public String dealFuncFParams(Node node) {
        for (Node son: node.getSons()) {
            getMidCode(son);
        }
        return "114514";
    }

    public String dealFuncFParam(Node node) {
        String name = node.getChild(1).getWord();
        int line = node.getChild(1).getLine();
        int dim = 0;
        if (node.getSons().size() == 2) {
            dim = 0;
            SymbolTerm symbolTerm = new SymbolTerm(name, dim, 0);
            symbolTerm.setLine(line);
            symbolTerm.setSize(true);
            symbolTerm.setParam();
            currentTable.addTerm(symbolTerm);
        } else if (node.getSons().size() == 4) {
            dim = 1;
            SymbolTerm symbolTerm = new SymbolTerm(name, dim, 0);
            symbolTerm.setLine(line);
            symbolTerm.setSize(true);
            symbolTerm.setParam();
            currentTable.addTerm(symbolTerm);
        } else  {
            dim = 2;
            int length = ((NonLeafNode)node.getChild(5)).getConstExpValue(currentTable);
            SymbolTerm symbolTerm = new SymbolTerm(name, dim, 0);
            symbolTerm.setLine(line);
            symbolTerm.setSize(true);
            symbolTerm.setParam();
            symbolTerm.setLength2(length);
            currentTable.addTerm(symbolTerm);
        }
        return "114514";
    }

    public String dealMainFuncDef(Node node) {
        isGlobal = false;
        codeManager.addMidCode(new MidCodeTerm(OpType.FUNC_DEC, null, null, "main", currentTable));
        isFuncDef = true;
        SymbolTable.resetSp();
        currentTable = currentTable.getNextSon();
        isMain = true;
        for (Node son: node.getSons()) {
            getMidCode(son);
        }
        currentTable.setAddressSp();
        currentTable = currentTable.getFather();
        return "114514";
    }

    public String getFuncType(String name) {
        if (name.equals("main")) {
            return "int";
        }
        String type = errorAnalysis.getFuncType(name);
        return type;
    }

    public String dealRelExp(Node node) {
        if (node.getSons().size() == 1) {
            return getMidCode(node.getChild(0));
        }
        String  temp = getMidCode(node.getChild(0));
        String op = node.getChild(1).getWord();
        String addExp = getMidCode(node.getChild(2));
        if (isNum(temp) && isNum(addExp)) {
            int a = Integer.parseInt(temp);
            int b = Integer.parseInt(addExp);
            if (op.equals("<")) {
                if (a < b) {
                    return "1";
                } else {
                    return "0";
                }
            } else if (op.equals("<=")) {
                if (a <= b) {
                    return "1";
                } else {
                    return "0";
                }
            } else if (op.equals(">")) {
                if (a > b) {
                    return "1";
                } else {
                    return "0";
                }
            } else if (op.equals(">=")) {
                if (a >= b) {
                    return "1";
                } else {
                    return "0";
                }
            }
        }
        String result = codeManager.getTemp(currentTable);
        if (op.equals(">")) {
            codeManager.addMidCode(new MidCodeTerm(OpType.GT, temp, addExp, result, currentTable));
        } else if (op.equals("<")) {
            codeManager.addMidCode(new MidCodeTerm(OpType.LT, temp, addExp, result, currentTable));
        } else if (op.equals(">=")) {
            codeManager.addMidCode(new MidCodeTerm(OpType.GE, temp, addExp, result, currentTable));
        } else if (op.equals("<=")) {
            codeManager.addMidCode(new MidCodeTerm(OpType.LE, temp, addExp, result, currentTable));
        }
        return result;
    }

    public String dealEqExp(Node node) {
        if (node.getSons().size() == 1) {
            return getMidCode(node.getChild(0));
        }
        String  temp = getMidCode(node.getChild(0));
        String op = node.getChild(1).getWord();
        String relExp = getMidCode(node.getChild(2));
        if (isNum(temp) && isNum(relExp)) {
            int a = Integer.parseInt(temp);
            int b = Integer.parseInt(relExp);
            if (op.equals("==")) {
                if (a == b) {
                    return "1";
                } else {
                    return "0";
                }
            } else if (op.equals("!=")) {
                if (a != b) {
                    return "1";
                } else {
                    return "0";
                }
            }
        }
        String result = codeManager.getTemp(currentTable);
        if (op.equals("==")) {
            codeManager.addMidCode(new MidCodeTerm(OpType.EQ, temp, relExp, result, currentTable));
        } else if (op.equals("!=")) {
            codeManager.addMidCode(new MidCodeTerm(OpType.NE, temp, relExp, result, currentTable));
        }
        return result;
    }

    public void dealLandExp(Node node, boolean isFinal, String label, String OptLabel) {
        ArrayList<Node> eqExp = ((NonLeafNode) node).getEqExp();
        if (isFinal) {
            //判断是否跳到最后面
            if (OptLabel == null) {
                for (Node temp : eqExp) {
                    String result = dealEqExp(temp);
                    codeManager.addMidCode(new MidCodeTerm(OpType.BEQZ, result, null, label, currentTable));
                }
            } else {
                for (int i = 0; i < eqExp.size(); i++) {
                    String result = dealEqExp(eqExp.get(i));
                    if (i != eqExp.size() - 1) {
                        codeManager.addMidCode(new MidCodeTerm(OpType.BEQZ, result, null, label, currentTable));
                    } else {
                        codeManager.addMidCode(new MidCodeTerm(OpType.BNEZ, result, null, OptLabel, currentTable));
                    }
                }
            }
        } else {
            //这是传进来的label是正确时进入的块
            String label1 = codeManager.getLabel();
            for (int i = 0; i < eqExp.size() - 1; i++) {
                String result = dealEqExp(eqExp.get(i));
                codeManager.addMidCode(new MidCodeTerm(OpType.BEQZ, result, null, label1, currentTable));
            }
            String result = dealEqExp(eqExp.get(eqExp.size() - 1));
            codeManager.addMidCode(new MidCodeTerm(OpType.BNEZ, result, null, label, currentTable));
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label1, currentTable));
        }
    }

    public String dealLorExp(Node node, String rightLabel, String wrongLabel, boolean isOpt) {
        ArrayList<Node> landExp = ((NonLeafNode) node).getLandExp();
        if (!isOpt) {
            for (int i = 0; i < landExp.size() - 1; i++) {
                dealLandExp(landExp.get(i), false, rightLabel, null);
            }
            dealLandExp(landExp.get(landExp.size() - 1), true, wrongLabel, null);
        } else {
            for (int i = 0; i < landExp.size() - 1; i++) {
                dealLandExp(landExp.get(i), false, rightLabel, null);
            }
            dealLandExp(landExp.get(landExp.size() - 1), true, wrongLabel, rightLabel);
        }
        return "114514";
    }

    public String dealCond(Node node, String rightLabel, String wrongLabel, boolean isBottom) {
        dealLorExp(node.getChild(0), rightLabel, wrongLabel, isBottom);
        return "114514";
    }

    public void dealIf(Node node) {
        String label1 = codeManager.getLabel();
        String label2 = codeManager.getLabel();
        dealCond(node.getChild(2), label1, label2, false);
        codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label1, currentTable));
        getMidCode(node.getChild(4));
        if (node.getSons().size() > 5) {
            String label3 = codeManager.getLabel();
            codeManager.addMidCode(new MidCodeTerm(OpType.GOTO, null, null, label3, currentTable));
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label2, currentTable));
            getMidCode(node.getChild(6));
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label3, currentTable));
        } else {
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label2, currentTable));
        }
    }

    public void dealWhile(Node node) {
        String label1 = codeManager.getLabel();
        String label2 = codeManager.getLabel();
        String label3 = codeManager.getLabel();
        node.setBeginLabel(label1);
        node.setRightLabel(label2);
        node.setWrongLabel(label3);
        if (!Optimizer.getInstance().getOptimizer()) {
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label1, currentTable));
            dealCond(node.getChild(2), label2, label3, false);
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label2, currentTable));
            getMidCode(node.getChild(4));
            codeManager.addMidCode(new MidCodeTerm(OpType.GOTO, null, null, label1, currentTable));
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label3, currentTable));
        } else {
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label1, currentTable));
            dealCond(node.getChild(2), label2, label3, false);
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label2, currentTable));
            getMidCode(node.getChild(4));
            dealCond(node.getChild(2), label2, label3, true);
            codeManager.addMidCode(new MidCodeTerm(OpType.PUT_LABEL, null, null, label3, currentTable));
        }
    }

    public void dealBreak(Node node) {
        Node temp = node;
        while (temp != null) {
            if (temp.isWhileSmt()) {
                Node whileNode = temp.getFather();
                codeManager.addMidCode(new MidCodeTerm(OpType.GOTO, null, null, whileNode.getWrongLabel(), currentTable));
                return;
            }
            temp = temp.getFather();
        }
        System.out.println("break语句不在循环体内");
    }

    public void dealContinue(Node node) {
        Node temp = node;
        while (temp != null) {
            if (temp.isWhileSmt()) {
                Node whileNode = temp.getFather();
                codeManager.addMidCode(new MidCodeTerm(OpType.GOTO, null, null, whileNode.getBeginLabel(), currentTable));
                return;
            }
            temp = temp.getFather();
        }
        System.out.println("continue语句不在循环体内");
    }

    public boolean isNum(String str) {
        return str.matches("[-+]*[0-9]+");
    }
}
