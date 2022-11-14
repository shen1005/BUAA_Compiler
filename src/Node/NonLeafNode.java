package Node;

import MidCode.Visitor;

import java.util.ArrayList;
import java.util.Objects;

public class NonLeafNode extends Node {

    public NonLeafNode(String symbol) {
        super(symbol);
    }

    @Override
    public String toString() {
        String temp = "";
        ArrayList<Node> words = getSons();
        int i;
        for (i = 0; i < words.size(); i++) {
            temp += words.get(i).toString();
        }
        if (getSymbol().equals("BType") || getSymbol().equals("Decl") || getSymbol().equals("BlockItem")) {
            return temp;
        }
        temp += "<" + getSymbol() + ">" + '\n';
        return temp;
    }

    @Override
    public void findError() {
//        for (Node son : sons) {
//            son.findError();
//        }
        String symbol = getSymbol();
        if (symbol.equals("ConstDecl")) {
            checkConstDecl();
        } else if (symbol.equals("VarDecl")) {
            checkVarDecl();
        } else if (symbol.equals("ConstDef")) {
            checkConstDef();
        } else if (symbol.equals("VarDef")) {
            checkVarDef();
        } else if (symbol.equals("FuncDef")) {
            checkFuncDef();
        } else if (symbol.equals("MainFuncDef")) {
            checkMainFuncDef();
        } else if (symbol.equals("FuncFParam")) {
            checkFuncFParam();
        } else if (symbol.equals("Block")) {
            checkBlock();
        } else if (symbol.equals("Stmt")) {
            checkStmt();
        } else if (symbol.equals("LVal")) {
            checkLval();
        } else if (symbol.equals("UnaryExp")) {
            checkUnaryExp();
        } else {
            check();
        }
    }

    private void checkConstDecl() {
        for (Node son: sons) {
            son.findError();
        }
        String type = sons.get(1).getSons().get(0).getSymbol();
        String identifier = sons.get(2).getSons().get(0).word.getWord();
        errorAnalysis.getCurrentTable().getTypeTable().put(identifier, type);
    }

    private void checkVarDecl() {
        for (Node son: sons) {
            son.findError();
        }
        String type = sons.get(0).getSons().get(0).getSymbol();
        String identifier = sons.get(1).getSons().get(0).word.getWord();
        errorAnalysis.getCurrentTable().getTypeTable().put(identifier, type);
    }

    private void checkConstDef() {
        for (Node son: sons) {
            son.findError();
        }
        String identifier = sons.get(0).word.getWord();
        if (errorAnalysis.getCurrentTable().getTable().containsKey(identifier) || errorAnalysis.getCurrentTable().getConstTable().containsKey(identifier)) {
            errorAnalysis.addError(sons.get(0).word.getLine(), "b");
        } else {
            int dim = 0;
            for (Node son : sons) {
                if (son.getSymbol().equals("LBRACK")) {
                    dim++;
                }
            }
            errorAnalysis.getCurrentTable().getConstTable().put(identifier, dim);
        }
    }

    private void checkVarDef() {
        for (Node son: sons) {
            son.findError();
        }
        String identifier = sons.get(0).word.getWord();
        if (errorAnalysis.getCurrentTable().getTable().containsKey(identifier) || errorAnalysis.getCurrentTable().getConstTable().containsKey(identifier)) {
            errorAnalysis.addError(sons.get(0).word.getLine(), "b");
        } else {
            int dim = 0;
            for (Node son : sons) {
                if (son.getSymbol().equals("LBRACK")) {
                    dim++;
                }
            }
            errorAnalysis.getCurrentTable().getTable().put(identifier, dim);
        }
    }

    private void checkFuncDef() {
        String type = sons.get(0).getFuncType();
        String identifier = sons.get(1).word.getWord();
        boolean isWrong = false;
        if (errorAnalysis.getFuncTable().containsKey(identifier) || errorAnalysis.getCurrentTable().getTable().containsKey(identifier) || errorAnalysis.getCurrentTable().getConstTable().containsKey(identifier)) {
            errorAnalysis.addError(sons.get(1).word.getLine(), "b");
            if (errorAnalysis.getFuncTable().containsKey(identifier)) {
                isWrong = true;
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        nowFuncName = identifier;
        int paramNum = 0;
        ArrayList<Integer> paraType;
        if (!sons.get(3).getSymbol().equals("FuncFParams")) {
            paraType = new ArrayList<>();
        } else {
            paraType = sons.get(3).getParaTypes();
            paramNum = paraType.size();
        }
        FuncDel funcDel = new FuncDel(identifier, type, paramNum, paraType);
        if (!isWrong) {
            errorAnalysis.getFuncTable().put(identifier, funcDel);
        }
        errorAnalysis.changeTable();
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        errorAnalysis.addFunc2Symbol(identifier, errorAnalysis.getCurrentTable());
        isFuncDef = true;
        for (Node son: sons) {
            son.findError();
        }
        errorAnalysis.backTable();
        if (type.equals("void")) {
            if (sons.get(sons.size() - 1) instanceof NonLeafNode) {
                NonLeafNode last = (NonLeafNode) sons.get(sons.size() - 1);
                ArrayList<Integer> line = last.isHaveReturn();
                if (line.size() != 0) {
                    for (Integer i: line) {
                        errorAnalysis.addError(i, "f");
                    }
                }
            }
        } else if (type.equals("int")) {
             NonLeafNode block = (NonLeafNode) sons.get(sons.size() - 1);
             int line = block.lastReturn();
             if (line != -1) {
                 errorAnalysis.addError(line, "g");
             }
        }
    }

    private void checkMainFuncDef() {
        errorAnalysis.changeTable();
        isFuncDef = true;
        errorAnalysis.addFunc2Symbol("main", errorAnalysis.getCurrentTable());
        for (Node son: sons) {
            son.findError();
        }
        errorAnalysis.backTable();
        if (errorAnalysis.getFuncTable().containsKey("main") || errorAnalysis.getCurrentTable().getTable().containsKey("main") || errorAnalysis.getCurrentTable().getConstTable().containsKey("main")) {
            errorAnalysis.addError(sons.get(0).word.getLine(), "b");
        }
        NonLeafNode block = (NonLeafNode) sons.get(sons.size() - 1);
        int line = block.lastReturn();
        if (line != -1) {
            errorAnalysis.addError(line, "g");
        }
    }

    public void checkFuncFParam() {
        String identifier = sons.get(1).word.getWord();
        if (errorAnalysis.getCurrentTable().getTable().containsKey(identifier) || errorAnalysis.getCurrentTable().getConstTable().containsKey(identifier)) {
            errorAnalysis.addError(sons.get(1).word.getLine(), "b");
        } else {
            int dim = 0;
            for (Node son : sons) {
                if (son.getSymbol().equals("LBRACK")) {
                    dim++;
                }
            }
            errorAnalysis.getCurrentTable().getTable().put(identifier, dim);
        }
    }

    private void checkBlock() {
        boolean meDo = true;
        if (isFuncDef) {
            isFuncDef = false;
            meDo = false;
        }else {
            errorAnalysis.changeTable();
        }
        for (Node son: sons) {
            son.findError();
        }
        if (meDo) {
            errorAnalysis.backTable();
        }
    }

    @Override//这个方法是为了方便在语义分析中获取函数的返回值类型
    protected String getFuncType() {
        return sons.get(0).word.getWord();
    }

    @Override//这个方法是为了方便在语义分析中获取函数的所有参数类型
    protected ArrayList<Integer> getParaTypes() {
        ArrayList<Integer> paraTypes = new ArrayList<>();
        for (Node son : sons) {
            if (son instanceof NonLeafNode) {
                paraTypes.add(son.getParaType());
            }
        }
        return paraTypes;
    }

    public void checkStmt() {
        for (Node son: sons) {
            son.findError();
        }
        if (sons.get(0).getSymbol().equals("LVal")) {
            String identifier = sons.get(0).getSons().get(0).word.getWord();
            int line = sons.get(0).getSons().get(0).word.getLine();
            if (errorAnalysis.isErrorConstAssign(identifier)) {
                errorAnalysis.addError(line, "h");
            }
        }
        else if (sons.get(0).getSymbol().equals("BREAKTK") || sons.get(0).getSymbol().equals("CONTINUETK")) {
            if (!isInWhileStmt()) {
                errorAnalysis.addError(sons.get(0).word.getLine(), "m");
            }
        }
        else if (sons.get(0).getSymbol().equals("PRINTFTK")) {
            int num = getIntNum(sons.get(2).word.getWord());
            int num2 = 0;
            for (Node node: sons) {
                if (node.getSymbol().equals("Exp")) {
                    num2++;
                }
            }
            if (num != num2) {
                errorAnalysis.addError(sons.get(0).word.getLine(), "l");
            }
        }
    }

    private void checkLval() {
        for (Node son: sons) {
            son.findError();
        }
        String identifier = sons.get(0).word.getWord();
        int line = sons.get(0).word.getLine();
        if (!(errorAnalysis.isInTable(identifier) || errorAnalysis.isInConstTable(identifier))) {
            errorAnalysis.addError(line, "c");
        }
    }

    public void check() {
        for (Node son: sons) {
            son.findError();
        }
    }

    @Override//这个方法是为了方便在语义分析中获取函数的每一个参数类型
    protected int getParaType() {
        int dim = 0;
        for (Node son : sons) {
            if (son.getSymbol().equals("LBRACK")) {
                dim++;
            }
        }
        return dim;
    }

    public ArrayList<Integer> isHaveReturn() {
        ArrayList<Integer> all = new ArrayList<>();
        int i;
        for (i = 0; i < sons.size(); i++) {
            if (sons.get(i) instanceof NonLeafNode) {
                ArrayList<Integer> line = ((NonLeafNode) sons.get(i)).isHaveReturn();
                all.addAll(line);
            } else if (sons.get(i).getSymbol().equals("RETURNTK") && i + 1 < sons.size() && !sons.get(i + 1).getSymbol().equals("SEMICN")) {
                all.add(sons.get(i).word.getLine());
            }
        }
        return all;
    }

    //专门为block而设1 -1代表有好return 其它代表没有return，最后大括号的行号
    public int lastReturn() {
        int line = sons.get(sons.size() - 1).word.getLine();
        if (sons.size() == 2) {
            return sons.get(sons.size() - 1).word.getLine();
        }
        if (sons.get(sons.size() - 2).getSons().get(0).getSymbol().equals("Stmt")) {
            NonLeafNode stmt = (NonLeafNode) sons.get(sons.size() - 2).getSons().get(0);
            if (stmt.getSons().get(0).getSymbol().equals("RETURNTK") && stmt.sons.size() > 1 && !stmt.getSons().get(1).getSymbol().equals("SEMICN")) {
                return -1;
            }
        }
        return line;
    }

    public boolean isInWhileStmt() {
        boolean flag = false;
        Node temp = this;
        while (temp.father != null) {
            if (temp.isWhileSmt) {
                flag = true;
                break;
            }
            temp = temp.father;
        }
        return flag;
    }

    public int getIntNum(String str) {
        int num = 0;
        for (int i = 0; i < str.length() - 1; i++) {
            if (str.charAt(i) == '%' && str.charAt(i + 1) == 'd') {
                num++;
            }
        }
        return num;
    }

    public void checkUnaryExp() {
        for (Node son: sons) {
            son.findError();
        }
        if (sons.get(0).getSymbol().equals("IDENFR")) {
            String identifier = sons.get(0).word.getWord();
            int line = sons.get(0).word.getLine();
            if (!errorAnalysis.isInFuncTable(identifier)) {
                errorAnalysis.addError(line, "c");
                return;
            }
            FuncDel funcDel = errorAnalysis.getFuncDel(sons.get(0).word.getWord());
            if (sons.size() >= 3 && sons.get(2).getSymbol().equals("FuncRParams")) {
                ArrayList<Integer> paraTypes = new ArrayList<>();
                for (Node son: sons.get(2).sons) {
                    if (son.getSymbol().equals("Exp")) {
                        paraTypes.add(((NonLeafNode) son).getParaList());
                    }
                }
                if (funcDel.getParaNum() != paraTypes.size()) {
                    errorAnalysis.addError(sons.get(0).word.getLine(), "d");
                }
                else {
                    for (int i = 0; i < paraTypes.size(); i++) {
                        if (!Objects.equals(paraTypes.get(i), funcDel.getParaType().get(i))) {
                            errorAnalysis.addError(sons.get(0).word.getLine(), "e");
                            break;
                        }
                    }
                }
            } else if (sons.size() == 3 && funcDel.getParaNum() != 0) {
                errorAnalysis.addError(sons.get(0).word.getLine(), "d");
            }
        }
    }

    private Integer getParaList() {
       NonLeafNode addExp = (NonLeafNode) sons.get(0);
       if (addExp.getSons().size() != 1) {
           return 0;
       }
       else {
           NonLeafNode mulExp = (NonLeafNode) addExp.sons.get(0);
           if (mulExp.sons.size() != 1) {
               return 0;
           }
           NonLeafNode UnaryExp = (NonLeafNode) mulExp.sons.get(0);
            if (UnaryExp.sons.size() != 1) {
                if (UnaryExp.sons.get(0).getSymbol().equals("IDENFR")) {
                    String identifier = UnaryExp.sons.get(0).word.getWord();
                    FuncDel funcDel = errorAnalysis.getFuncDel(identifier);
                    if (funcDel.getType().equals("void")) {
                        return -1;
                    }
                }
               return 0;
            }
            NonLeafNode primaryExp = (NonLeafNode) UnaryExp.sons.get(0);
            if (!primaryExp.sons.get(0).getSymbol().equals("LVal")) {
                return 0;
            }
            NonLeafNode lVal = (NonLeafNode) primaryExp.sons.get(0);
            String identifier = lVal.sons.get(0).word.getWord();
            int dim = errorAnalysis.getDim(identifier);
            for (int i = 1; i < lVal.sons.size(); i++) {
                if (lVal.sons.get(i).getSymbol().equals("LBRACK")) {
                    dim--;
                }
            }
            return dim;
       }
    }

    @Override
    public void setFather(Node father) {
        this.father = father;
        for (Node son: sons) {
            son.setFather(this);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public int getConstInitValValue(SymbolTable temp) {
        if (sons.size() == 1) {
            if (getSymbol().equals("ConstInitVal")) {
                return ((NonLeafNode) sons.get(0)).getConstExpValue(temp);
            }
            else {
                return ((NonLeafNode) sons.get(0).getChild(0)).getAddExpValue(temp);
            }
        }
        else return 114514;
    }

    public int getConstExpValue(SymbolTable temp) {
        return ((NonLeafNode)sons.get(0)).getAddExpValue(temp);
    }

    public int getAddExpValue(SymbolTable temp) {
        if (sons.size() == 1) {
            return ((NonLeafNode)sons.get(0)).getMulExpValue(temp);
        } else if (sons.get(1).getSymbol().equals("PLUS")) {
            return ((NonLeafNode)sons.get(0)).getAddExpValue(temp) + ((NonLeafNode)sons.get(2)).getMulExpValue(temp);
        } else if (sons.get(1).getSymbol().equals("MINU")) {
            return ((NonLeafNode)sons.get(0)).getAddExpValue(temp) - ((NonLeafNode)sons.get(2)).getMulExpValue(temp);
        }
        else return 114514;
    }

    public int getMulExpValue(SymbolTable temp) {
        if (sons.size() == 1) {
            return ((NonLeafNode)sons.get(0)).getUnaryExpValue(temp);
        } else if (sons.get(1).getSymbol().equals("MULT")) {
            return ((NonLeafNode)sons.get(0)).getMulExpValue(temp) * ((NonLeafNode)sons.get(2)).getUnaryExpValue(temp);
        } else if (sons.get(1).getSymbol().equals("DIV")) {
            return ((NonLeafNode)sons.get(0)).getMulExpValue(temp) / ((NonLeafNode)sons.get(2)).getUnaryExpValue(temp);
        } else if (sons.get(1).getSymbol().equals("MOD")) {
            return ((NonLeafNode)sons.get(0)).getMulExpValue(temp) % ((NonLeafNode)sons.get(2)).getUnaryExpValue(temp);
        }
        else return 114514;
    }

    public int getUnaryExpValue(SymbolTable temp) {
        if (sons.size() == 1) {
            return ((NonLeafNode)sons.get(0)).getPrimaryExpValue(temp);
        } else if (sons.get(0).getChild(0).getSymbol().equals("PLUS")) {
            return ((NonLeafNode)sons.get(1)).getUnaryExpValue(temp);
        } else if (sons.get(0).getChild(0).getSymbol().equals("MINU")) {
            return -((NonLeafNode)sons.get(1)).getUnaryExpValue(temp);
        }
        else return 114514;
    }

    public int getPrimaryExpValue(SymbolTable temp) {
        if (sons.get(0).getSymbol().equals("LVal")) {
            return ((NonLeafNode)sons.get(0)).getLValValue(temp);
        } else if (sons.get(0).getSymbol().equals("LPARENT")) {
            return ((NonLeafNode)sons.get(1)).getConstExpValue(temp);
        } else if (sons.get(0).getSymbol().equals("Number")) {
            return Integer.parseInt(sons.get(0).getChild(0).getWord());
        }
        else return 114514;
    }

    public int getLValValue(SymbolTable temp) {
        if (sons.size() == 1) {
            return errorAnalysis.getCurrentTable().getValue(sons.get(0).getWord(), temp);
        } else if (sons.size() == 4) {
            return errorAnalysis.getCurrentTable().get1DValue(sons.get(0).getWord(), ((NonLeafNode) sons.get(2)).getConstExpValue(temp), temp);
        } else {
            return errorAnalysis.getCurrentTable().get2DValue(sons.get(0).getWord(), ((NonLeafNode) sons.get(2)).getConstExpValue(temp), ((NonLeafNode) sons.get(5)).getConstExpValue(temp), temp);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int getLength1D(SymbolTable temp) {
        for (Node son: sons) {
            if (son.getSymbol().equals("ConstExp")) {
                return ((NonLeafNode) son).getConstExpValue(temp);
            }
        }
        return 114514;
    }

    public int getLength2D(SymbolTable temp) {
        int num = 0;
        for (Node son: sons) {
            if (son.getSymbol().equals("ConstExp")) {
                num++;
                if (num == 2) {
                    return ((NonLeafNode) son).getConstExpValue(temp);
                }
            }
        }
        return 114514;
    }

    public ArrayList<Integer> get1DValue(SymbolTable temp) {
        ArrayList<Integer> value = new ArrayList<>();
        for (Node son: sons) {
            if (son.getSymbol().equals("ConstInitVal") || son.getSymbol().equals("InitVal")) {
                value.add(((NonLeafNode) son).getConstInitValValue(temp));
            }
        }
        return value;
    }

    public ArrayList<ArrayList<Integer>> get2DValue(SymbolTable temp) {
        ArrayList<ArrayList<Integer>> value = new ArrayList<>();
        for (Node son: sons) {
            if (son.getSymbol().equals("ConstInitVal") || son.getSymbol().equals("InitVal")) {
                value.add(((NonLeafNode) son).get1DValue(temp));
            }
        }
        return value;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int getNumMINU() {
        if (!sons.get(0).getSymbol().equals("UnaryOp")) {
            return 0;
        } else {
            if (sons.get(0).getChild(0).getSymbol().equals("MINU")) {
                return 1 + ((NonLeafNode) sons.get(1)).getNumMINU();
            } else {
                return ((NonLeafNode) sons.get(1)).getNumMINU();
            }
        }
    }

    public int getNumNOT() {
        if (!sons.get(0).getSymbol().equals("UnaryOp")) {
            return 0;
        } else {
            if (sons.get(0).getChild(0).getSymbol().equals("NOT")) {
                return 1 + ((NonLeafNode) sons.get(1)).getNumNOT();
            } else {
                return ((NonLeafNode) sons.get(1)).getNumNOT();
            }
        }
    }

    public ArrayList<Node> getEqExp() {
        assert getSymbol().equals("LandExp");
        ArrayList<Node> eqExp = new ArrayList<>();
        if (sons.size() == 1) {
            eqExp.add(sons.get(0));
            return eqExp;
        }
        eqExp.addAll(((NonLeafNode) sons.get(0)).getEqExp());
        eqExp.add(sons.get(2));
        return eqExp;
    }

    public ArrayList<Node> getLandExp() {
        assert getSymbol().equals("LorExp");
        ArrayList<Node> landExp = new ArrayList<>();
        if (sons.size() == 1) {
            landExp.add(sons.get(0));
            return landExp;
        }
        landExp.addAll(((NonLeafNode) sons.get(0)).getLandExp());
        landExp.add(sons.get(2));
        return landExp;
    }
}
