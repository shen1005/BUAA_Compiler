package Node;

import WordAnalysis.Word;

import java.util.ArrayList;

public class GrammarAnalysis {
    private NonLeafNode root;
    private int pos;
    private ArrayList<Word> words;
    private ErrorAnalysis errorAnalysis = ErrorAnalysis.getInstance();

    private int getLastLine() {
        int temp = pos - 1;
        while (temp >= 0 && (words.get(temp).getWord().equals(";") || words.get(temp).getWord().equals("}") || words.get(temp).getWord().equals("{")
                || words.get(temp).getWord().equals(")") || words.get(temp).getWord().equals("else") || words.get(temp).getWord().equals("]") ||
                    words.get(temp).getWord().equals("[") || words.get(temp).getWord().equals("("))) {
            temp--;
        }
        return words.get(temp).getLine();
    }

    public GrammarAnalysis(ArrayList<Word> words) {
        this.words = words;
        pos = 0;
        root = null;
    }

    public Node getRoot() {
        return root;
    }

    public boolean isDecl() {
        if (pos >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        if (pos + 2 >= words.size()) {
            return false;
        }
        Word temp_2 = words.get(pos + 2);
        return temp.getSymbol().equals("CONSTTK") ||
                (temp.getSymbol().equals("INTTK") && !temp_2.getSymbol().equals("LPARENT"));
    }

    public boolean isAddOp() {
        if (pos >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        return temp.getSymbol().equals("PLUS") || temp.getSymbol().equals("MINU");
    }

    public boolean isMulOp() {
        if (pos >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        return temp.getSymbol().equals("MULT") || temp.getSymbol().equals("DIV") || temp.getSymbol().equals("MOD");
    }

    public boolean isFunc() {
        if (pos + 1 >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        Word temp_1 = words.get(pos + 1);
        return temp.getSymbol().equals("IDENFR") && temp_1.getSymbol().equals("LPARENT");
    }

    public boolean isPrimaryExp() {
        if (pos >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        return temp.getSymbol().equals("IDENFR") || temp.getSymbol().equals("INTCON") ||
                temp.getSymbol().equals("LPARENT");
    }

    public boolean isUnaryOp() {
        if (pos >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        return temp.getSymbol().equals("PLUS") || temp.getSymbol().equals("MINU") ||
                temp.getSymbol().equals("NOT");
    }

    public boolean isFuncDef() {
        if (pos + 1 >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        Word temp_1 = words.get(pos + 1);
        return temp.getSymbol().equals("VOIDTK") || (temp.getSymbol().equals("INTTK") && !temp_1.getSymbol().equals("MAINTK"));
    }

    public boolean isBlockItem() {
        if (pos >= words.size()) {
            return false;
        }
        return isDecl() || isStmt();
    }

    public boolean isStmt() {
        if (pos >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        String symbol = temp.getSymbol();
        return symbol.equals("SEMICN") || symbol.equals("LBRACE") || symbol.equals("IFTK") ||
                symbol.equals("WHILETK")|| symbol.equals("PRINTFTK") || symbol.equals("RETURNTK") ||
                symbol.equals("IDENFR") || symbol.equals("BREAKTK") || symbol.equals("CONTINUETK") || isExp();
    }

    public boolean isExp() {
        if (pos >= words.size()) {
            return false;
        }
        String temp = words.get(pos).getSymbol();
        return temp.equals("PLUS") || temp.equals("MINU") || temp.equals("IDENFR") ||
                temp.equals("INTCON") || temp.equals("LPARENT") || temp.equals("NOT");
    }

    public boolean isLval() {
        if (pos >= words.size()) {
            return false;
        }
        Word temp = words.get(pos);
        return temp.getSymbol().equals("IDENFR");
    }

    public void analysis() {
        root = new NonLeafNode("CompUnit");
        while (isDecl()) {
            NonLeafNode decl = Decl();
            root.addSon(decl);
        }
        while (isFuncDef()) {
            NonLeafNode funcDef = FuncDef();
            root.addSon(funcDef);
        }
        root.addSon(MainFuncDef());
        setFather();
    }

    public NonLeafNode Decl() {
        NonLeafNode decl = new NonLeafNode("Decl");
        if (words.get(pos).getSymbol().equals("CONSTTK")) {
            decl.addSon(ConstDecl());
        } else {
            decl.addSon(VarDecl());
        }
        return decl;
    }

    public NonLeafNode ConstDecl() {
        NonLeafNode constDecl = new NonLeafNode("ConstDecl");
        constDecl.addSon(new LeafNode("CONSTTK", words.get(pos++)));
        if (words.get(pos).getSymbol().equals("INTTK")) {
            NonLeafNode BType = BType();
            constDecl.addSon(BType);
        }
        constDecl.addSon(ConstDef());
        while (words.get(pos).getSymbol().equals("COMMA")) {
            constDecl.addSon(new LeafNode("COMMA", words.get(pos++)));
            constDecl.addSon(ConstDef());
        }
        if (!words.get(pos).getSymbol().equals("SEMICN")) {
            errorAnalysis.addError(getLastLine(), "i");
            return constDecl;
        }
        constDecl.addSon(new LeafNode("SEMICN", words.get(pos++)));
        return constDecl;
    }

    public NonLeafNode VarDecl() {
        NonLeafNode varDecl = new NonLeafNode("VarDecl");
        varDecl.addSon(BType());
        varDecl.addSon(VarDef());
        while (words.get(pos).getSymbol().equals("COMMA")) {
            varDecl.addSon(new LeafNode("COMMA", words.get(pos++)));
            varDecl.addSon(VarDef());
        }
        if (!words.get(pos).getSymbol().equals("SEMICN")) {
            errorAnalysis.addError(getLastLine(), "i");
            return varDecl;
        }
        varDecl.addSon(new LeafNode("SEMICN", words.get(pos++)));
        return varDecl;
    }

    public NonLeafNode VarDef() {
        NonLeafNode varDef = new NonLeafNode("VarDef");
        varDef.addSon(new LeafNode("IDENFR", words.get(pos++)));
        while (words.get(pos).getSymbol().equals("LBRACK")) {
            varDef.addSon(new LeafNode("LBRACK", words.get(pos++)));
            varDef.addSon(ConstExp());
            if (!words.get(pos).getSymbol().equals("RBRACK")) {
                errorAnalysis.addError(getLastLine(), "k");
                continue;
            }
            varDef.addSon(new LeafNode("RBRACK", words.get(pos++)));
        }
        if (words.get(pos).getSymbol().equals("ASSIGN")) {
            varDef.addSon(new LeafNode("ASSIGN", words.get(pos++)));
            varDef.addSon(InitVal());
        }
        return varDef;
    }

    public NonLeafNode InitVal() {
        NonLeafNode initVal = new NonLeafNode("InitVal");
        if (words.get(pos).getSymbol().equals("LBRACE")) {
            initVal.addSon(new LeafNode("LBRACE", words.get(pos++)));
            initVal.addSon(InitVal());
            while (words.get(pos).getSymbol().equals("COMMA")) {
                initVal.addSon(new LeafNode("COMMA", words.get(pos++)));
                initVal.addSon(InitVal());
            }
            initVal.addSon(new LeafNode("RBRACE", words.get(pos++)));
        } else {
            initVal.addSon(Exp());
        }
        return initVal;
    }

    public NonLeafNode BType() {
        NonLeafNode bType = new NonLeafNode("BType");
        bType.addSon(new LeafNode("INTTK", words.get(pos++)));
        return bType;
    }

    public NonLeafNode ConstDef() {
        NonLeafNode constDef = new NonLeafNode("ConstDef");
        constDef.addSon(new LeafNode("IDENFR", words.get(pos++)));
        while (words.get(pos).getSymbol().equals("LBRACK")) {
            constDef.addSon(new LeafNode("LBRACK", words.get(pos++)));
            constDef.addSon(ConstExp());
            if (!words.get(pos).getSymbol().equals("RBRACK")) {
                errorAnalysis.addError(getLastLine(), "k");
                continue;
            }
            constDef.addSon(new LeafNode("RBRACK", words.get(pos++)));
        }
        constDef.addSon(new LeafNode("ASSIGN", words.get(pos++)));
        constDef.addSon(ConstInitVal());
        return constDef;
    }

    public NonLeafNode ConstInitVal() {
        NonLeafNode constInitVal = new NonLeafNode("ConstInitVal");
        if (words.get(pos).getSymbol().equals("LBRACE")) {
            constInitVal.addSon(new LeafNode("LBRACE", words.get(pos++)));
            if (!words.get(pos).getSymbol().equals("RBRACE")) {
                constInitVal.addSon(ConstInitVal());
                while (words.get(pos).getSymbol().equals("COMMA")) {
                    constInitVal.addSon(new LeafNode("COMMA", words.get(pos++)));
                    constInitVal.addSon(ConstInitVal());
                }
                constInitVal.addSon(new LeafNode("RBRACE", words.get(pos++)));
            }
            else {
                constInitVal.addSon(new LeafNode("RBRACE", words.get(pos++)));
            }
        } else {
            constInitVal.addSon(ConstExp());
        }
        return constInitVal;
    }

    public NonLeafNode ConstExp() {
        NonLeafNode constExp = new NonLeafNode("ConstExp");
        constExp.addSon(AddExp());
        return constExp;
    }

    public NonLeafNode AddExp() {
        NonLeafNode addExp = new NonLeafNode("AddExp");
        NonLeafNode mulExp = MulExp();
        NonLeafNode now;
        if (isAddOp()) {
            now = new NonLeafNode("AddExp");
            now.addSon(mulExp);
            while (isAddOp()) {
                NonLeafNode temp = new NonLeafNode("AddExp");
                temp.addSon(now);
                temp.addSon(new LeafNode(words.get(pos).getSymbol(), words.get(pos++)));
                temp.addSon(MulExp());
                now = temp;
            }
            return now;
        }
        else {
            addExp.addSon(mulExp);
        }
        return addExp;
    }

    public NonLeafNode MulExp() {
        NonLeafNode mulExp = new NonLeafNode("MulExp");
        NonLeafNode unaryExp = UnaryExp();
        NonLeafNode now;
        if (isMulOp()) {
            now = new NonLeafNode("MulExp");
            now.addSon(unaryExp);
            while (isMulOp()) {
                NonLeafNode temp = new NonLeafNode("MulExp");
                temp.addSon(now);
                temp.addSon(new LeafNode(words.get(pos).getSymbol(), words.get(pos++)));
                temp.addSon(UnaryExp());
                now = temp;
            }
            return now;
        }
        else {
            mulExp.addSon(unaryExp);
        }
        return mulExp;
    }

    public NonLeafNode UnaryExp() {
        NonLeafNode unaryExp = new NonLeafNode("UnaryExp");
        if (isUnaryOp()) {
            unaryExp.addSon(UnaryOp());
            unaryExp.addSon(UnaryExp());
        } else if (isFunc()) {
            unaryExp.addSon(new LeafNode("IDENFR", words.get(pos++)));
            unaryExp.addSon(new LeafNode("LPARENT", words.get(pos++)));
            if (isExp()) {
                unaryExp.addSon(FuncRParams());
            }
            if (!words.get(pos).getSymbol().equals("RPARENT")) {
                errorAnalysis.addError(getLastLine(), "j");
            } else {
                unaryExp.addSon(new LeafNode("RPARENT", words.get(pos++)));
            }
        } else {
            unaryExp.addSon(PrimaryExp());
        }
        return unaryExp;
    }

    public NonLeafNode UnaryOp() {
        NonLeafNode unaryOp = new NonLeafNode("UnaryOp");
        unaryOp.addSon(new LeafNode(words.get(pos).getSymbol(), words.get(pos++)));
        return unaryOp;
    }

    public NonLeafNode PrimaryExp() {
        NonLeafNode primaryExp = new NonLeafNode("PrimaryExp");
        if (words.get(pos).getSymbol().equals("LPARENT")) {
            primaryExp.addSon(new LeafNode("LPARENT", words.get(pos++)));
            primaryExp.addSon(Exp());
            primaryExp.addSon(new LeafNode("RPARENT", words.get(pos++)));
        } else if (words.get(pos).getSymbol().equals("IDENFR")) {
            primaryExp.addSon(Lval());
        } else {
            primaryExp.addSon(Number());
        }
        return primaryExp;
    }

    public NonLeafNode Number() {
        NonLeafNode number = new NonLeafNode("Number");
        number.addSon(new LeafNode("INTCON", words.get(pos++)));
        return number;
    }

    public NonLeafNode Exp() {
        NonLeafNode exp = new NonLeafNode("Exp");
        exp.addSon(AddExp());
        return exp;
    }

    public NonLeafNode FuncRParams() {
        NonLeafNode funcRParams = new NonLeafNode("FuncRParams");
        funcRParams.addSon(Exp());
        while (words.get(pos).getSymbol().equals("COMMA")) {
            funcRParams.addSon(new LeafNode("COMMA", words.get(pos++)));
            funcRParams.addSon(Exp());
        }
        return funcRParams;
    }

    public NonLeafNode FuncDef() {
        NonLeafNode funcDef = new NonLeafNode("FuncDef");
        NonLeafNode funcType = FuncType();
        funcDef.addSon(funcType);
        funcDef.addSon(new LeafNode("IDENFR", words.get(pos++)));
        funcDef.addSon(new LeafNode("LPARENT", words.get(pos++)));
        if (words.get(pos).getSymbol().equals("INTTK")) {
            funcDef.addSon(FuncFParams());
        }
        if (!words.get(pos).getSymbol().equals("RPARENT")) {
            errorAnalysis.addError(getLastLine(), "j");
        } else {
            funcDef.addSon(new LeafNode("RPARENT", words.get(pos++)));
        }
        funcDef.addSon(Block());
        return funcDef;
    }

    public NonLeafNode FuncFParams() {
        NonLeafNode funcFParams = new NonLeafNode("FuncFParams");
        funcFParams.addSon(FuncFParam());
        while (words.get(pos).getSymbol().equals("COMMA")) {
            funcFParams.addSon(new LeafNode("COMMA", words.get(pos++)));
            funcFParams.addSon(FuncFParam());
        }
        return funcFParams;
    }

    public NonLeafNode FuncFParam() {
        NonLeafNode funcFParam = new NonLeafNode("FuncFParam");
        funcFParam.addSon(BType());
        funcFParam.addSon(new LeafNode("IDENFR", words.get(pos++)));
        if (words.get(pos).getSymbol().equals("LBRACK")) {
            funcFParam.addSon(new LeafNode("LBRACK", words.get(pos++)));
            if (!words.get(pos).getSymbol().equals("RBRACK")) {
                errorAnalysis.addError(getLastLine(), "k");
            } else {
                funcFParam.addSon(new LeafNode("RBRACK", words.get(pos++)));
            }
        }
        if (words.get(pos).getSymbol().equals("LBRACK")) {
            funcFParam.addSon(new LeafNode("LBRACK", words.get(pos++)));
            funcFParam.addSon(ConstExp());
            if (!words.get(pos).getSymbol().equals("RBRACK")) {
                errorAnalysis.addError(getLastLine(), "k");
            } else {
                funcFParam.addSon(new LeafNode("RBRACK", words.get(pos++)));
            }
        }
        return funcFParam;
    }

    public NonLeafNode FuncType() {
        NonLeafNode funcType = new NonLeafNode("FuncType");
        if (words.get(pos).getSymbol().equals("INTTK")) {
            funcType.addSon(new LeafNode("INTTK", words.get(pos++)));
        } else {
            funcType.addSon(new LeafNode("VOIDTK", words.get(pos++)));
        }
        return funcType;
    }

    public NonLeafNode Block() {
        NonLeafNode block = new NonLeafNode("Block");
        block.addSon(new LeafNode("LBRACE", words.get(pos++)));
        while (isBlockItem()) {
            block.addSon(BlockItem());
        }
        block.addSon(new LeafNode("RBRACE", words.get(pos++)));
        return block;
    }

    public NonLeafNode BlockItem() {
        NonLeafNode blockItem = new NonLeafNode("BlockItem");
        if (isDecl()) {
            blockItem.addSon(Decl());
        } else {
            blockItem.addSon(Stmt());
        }
        return blockItem;
    }

    public NonLeafNode Stmt() {
        NonLeafNode stmt = new NonLeafNode("Stmt");
        int before = pos;
        if (isLval()) {
            NonLeafNode lval = Lval();
            if (words.get(pos).getSymbol().equals("ASSIGN")) {
                stmt.addSon(lval);
                stmt.addSon(new LeafNode("ASSIGN", words.get(pos++)));
                if (isExp()) {
                    stmt.addSon(Exp());
                    if (!words.get(pos).getSymbol().equals("SEMICN")) {
                        errorAnalysis.addError(getLastLine(), "i");
                    } else {
                        stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
                    }
                } else {
                    stmt.addSon(new LeafNode("GETINTTK", words.get(pos++)));
                    stmt.addSon(new LeafNode("LPARENT", words.get(pos++)));
                    if (!words.get(pos).getSymbol().equals("RPARENT")) {
                        errorAnalysis.addError(getLastLine(), "j");
                    } else {
                        stmt.addSon(new LeafNode("RPARENT", words.get(pos++)));
                    }
                    if (!words.get(pos).getSymbol().equals("SEMICN")) {
                        errorAnalysis.addError(getLastLine(), "i");
                    } else {
                        stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
                    }
                }
            }
            else {
                pos = before;
                stmt.addSon(Exp());
                if (!words.get(pos).getSymbol().equals("SEMICN")) {
                    errorAnalysis.addError(getLastLine(), "i");
                } else {
                    stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
                }
            }
        }
        else if (words.get(pos).getSymbol().equals("SEMICN")) {
            stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
        } else if (words.get(pos).getSymbol().equals("LBRACE")) {
            stmt.addSon(Block());
        } else if (words.get(pos).getSymbol().equals("IFTK")) {
            stmt.addSon(new LeafNode("IFTK", words.get(pos++)));
            stmt.addSon(new LeafNode("LPARENT", words.get(pos++)));
            stmt.addSon(Cond());
            if (!words.get(pos).getSymbol().equals("RPARENT")) {
                errorAnalysis.addError(getLastLine(), "j");
            } else {
                stmt.addSon(new LeafNode("RPARENT", words.get(pos++)));
            }
            stmt.addSon(Stmt());
            if (words.get(pos).getSymbol().equals("ELSETK")) {
                stmt.addSon(new LeafNode("ELSETK", words.get(pos++)));
                stmt.addSon(Stmt());
            }
        } else if (words.get(pos).getSymbol().equals("WHILETK")) {
            stmt.addSon(new LeafNode("WHILETK", words.get(pos++)));
            stmt.addSon(new LeafNode("LPARENT", words.get(pos++)));
            stmt.addSon(Cond());
            if (!words.get(pos).getSymbol().equals("RPARENT")) {
                errorAnalysis.addError(getLastLine(), "j");
            } else {
                stmt.addSon(new LeafNode("RPARENT", words.get(pos++)));
            }
            NonLeafNode stmt1 = Stmt();
            stmt1.setWhileSmt(true);
            stmt.addSon(stmt1);
        }  else if (words.get(pos).getSymbol().equals("RETURNTK")) {
            stmt.addSon(new LeafNode("RETURNTK", words.get(pos++)));
            if (isExp()) {
                stmt.addSon(Exp());
            }
            if (!words.get(pos).getSymbol().equals("SEMICN")) {
                errorAnalysis.addError(getLastLine(), "i");
            } else {
                stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
            }
        }  else if (words.get(pos).getSymbol().equals("PRINTFTK")) {
            stmt.addSon(new LeafNode("PRINTFTK", words.get(pos++)));
            stmt.addSon(new LeafNode("LPARENT", words.get(pos++)));
            if (words.get(pos).getSymbol().equals("STRCON")) {
                stmt.addSon(new LeafNode("STRCON", words.get(pos++)));
            }
            while (words.get(pos).getSymbol().equals("COMMA")) {
                stmt.addSon(new LeafNode("COMMA", words.get(pos++)));
                stmt.addSon(Exp());
            }
            if (!words.get(pos).getSymbol().equals("RPARENT")) {
                errorAnalysis.addError(getLastLine(), "j");
            } else {
                stmt.addSon(new LeafNode("RPARENT", words.get(pos++)));
            }
            if (!words.get(pos).getSymbol().equals("SEMICN")) {
                errorAnalysis.addError(getLastLine(), "i");
            } else {
                stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
            }
        } else if (words.get(pos).getSymbol().equals("BREAKTK")) {
            stmt.addSon(new LeafNode("BREAKTK", words.get(pos++)));
            if (!words.get(pos).getSymbol().equals("SEMICN")) {
                errorAnalysis.addError(getLastLine(), "i");
            } else {
                stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
            }
        } else if (words.get(pos).getSymbol().equals("CONTINUETK")) {
            stmt.addSon(new LeafNode("CONTINUETK", words.get(pos++)));
            if (!words.get(pos).getSymbol().equals("SEMICN")) {
                errorAnalysis.addError(getLastLine(), "i");
            } else {
                stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
            }
        } else if (isExp()) {
            stmt.addSon(Exp());
            if (!words.get(pos).getSymbol().equals("SEMICN")) {
                errorAnalysis.addError(getLastLine(), "i");
            } else {
                stmt.addSon(new LeafNode("SEMICN", words.get(pos++)));
            }
        }
        return stmt;
    }

    public NonLeafNode Cond() {
        NonLeafNode cond = new NonLeafNode("Cond");
        cond.addSon(LorExp());
        return cond;
    }

    public NonLeafNode LorExp() {
        NonLeafNode lorExp = new NonLeafNode("LOrExp");
        NonLeafNode landExp = LandExp();
        NonLeafNode now;
        if (words.get(pos).getSymbol().equals("OR")) {
            now = new NonLeafNode("LOrExp");
            now.addSon(landExp);
            while (words.get(pos).getSymbol().equals("OR")) {
                NonLeafNode temp = new NonLeafNode("LOrExp");
                temp.addSon(now);
                temp.addSon(new LeafNode(words.get(pos).getSymbol(), words.get(pos++)));
                temp.addSon(LandExp());
                now = temp;
            }
            return now;
        } else {
            lorExp.addSon(landExp);
        }
        return lorExp;
    }

    public NonLeafNode LandExp() {
        NonLeafNode landExp = new NonLeafNode("LAndExp");
        NonLeafNode eqExp = EqExp();
        NonLeafNode now;
        if (words.get(pos).getSymbol().equals("AND")) {
            now = new NonLeafNode("LAndExp");
            now.addSon(eqExp);
            while (words.get(pos).getSymbol().equals("AND")) {
                NonLeafNode temp = new NonLeafNode("LAndExp");
                temp.addSon(now);
                temp.addSon(new LeafNode(words.get(pos).getSymbol(), words.get(pos++)));
                temp.addSon(EqExp());
                now = temp;
            }
            return now;
        } else {
            landExp.addSon(eqExp);
        }
        return landExp;
    }

    public NonLeafNode EqExp() {
        NonLeafNode eqExp = new NonLeafNode("EqExp");
        NonLeafNode relExp = RelExp();
        NonLeafNode now;
        if (words.get(pos).getSymbol().equals("EQL") || words.get(pos).getSymbol().equals("NEQ")) {
            now = new NonLeafNode("EqExp");
            now.addSon(relExp);
            while (words.get(pos).getSymbol().equals("EQL") || words.get(pos).getSymbol().equals("NEQ")) {
                NonLeafNode temp = new NonLeafNode("EqExp");
                temp.addSon(now);
                temp.addSon(new LeafNode(words.get(pos).getSymbol(), words.get(pos++)));
                temp.addSon(RelExp());
                now = temp;
            }
            return now;
        } else {
            eqExp.addSon(relExp);
        }
        return eqExp;
    }

    public NonLeafNode RelExp() {
        NonLeafNode relExp = new NonLeafNode("RelExp");
        NonLeafNode addExp = AddExp();
        NonLeafNode now;
        if (words.get(pos).getSymbol().equals("LSS") || words.get(pos).getSymbol().equals("LEQ") || words.get(pos).getSymbol().equals("GRE") || words.get(pos).getSymbol().equals("GEQ")) {
            now = new NonLeafNode("RelExp");
            now.addSon(addExp);
            while (words.get(pos).getSymbol().equals("LSS") || words.get(pos).getSymbol().equals("LEQ") || words.get(pos).getSymbol().equals("GRE") || words.get(pos).getSymbol().equals("GEQ")) {
                NonLeafNode temp = new NonLeafNode("RelExp");
                temp.addSon(now);
                temp.addSon(new LeafNode(words.get(pos).getSymbol(), words.get(pos++)));
                temp.addSon(AddExp());
                now = temp;
            }
            return now;
        } else {
            relExp.addSon(addExp);
        }
        return relExp;
    }

    public NonLeafNode Lval() {
        NonLeafNode lval = new NonLeafNode("LVal");
        lval.addSon(new LeafNode("IDENFR", words.get(pos++)));
        while (words.get(pos).getSymbol().equals("LBRACK")) {
            lval.addSon(new LeafNode("LBRACK", words.get(pos++)));
            lval.addSon(Exp());
            if (!words.get(pos).getSymbol().equals("RBRACK")) {
                errorAnalysis.addError(getLastLine(), "k");
            } else {
                lval.addSon(new LeafNode("RBRACK", words.get(pos++)));
            }
        }
        return lval;
    }

    public NonLeafNode MainFuncDef() {
        NonLeafNode mainFuncDef = new NonLeafNode("MainFuncDef");
        mainFuncDef.addSon(new LeafNode("INTTK", words.get(pos++)));
        mainFuncDef.addSon(new LeafNode("MAINTK", words.get(pos++)));
        mainFuncDef.addSon(new LeafNode("LPARENT", words.get(pos++)));
        if (!words.get(pos).getSymbol().equals("RPARENT")) {
            errorAnalysis.addError(getLastLine(), "j");
        } else {
            mainFuncDef.addSon(new LeafNode("RPARENT", words.get(pos++)));
        }
        mainFuncDef.addSon(Block());
        return mainFuncDef;
    }

    public void findError() {
        root.findError();
    }

    public void setFather() {
        for (Node node : root.getSons()) {
            node.setFather(root);
        }
    }

    @Override
    public String toString() {
        return root.toString();
    }

}
