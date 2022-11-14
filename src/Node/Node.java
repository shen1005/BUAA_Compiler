package Node;
import WordAnalysis.Word;

import java.util.ArrayList;

public class Node {
    private String symbol;
    protected Word word = null;
    protected Node father = null;
    protected ArrayList<Node> sons = new ArrayList<Node>();
    protected ErrorAnalysis errorAnalysis = ErrorAnalysis.getInstance();
    protected boolean isWhileSmt = false;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected String nowFuncName = null;
    protected static boolean isFuncDef = false;
    protected String rightLabel;
    protected String wrongLabel;
    protected String beginLabel;


    public Node(String symbol) {
        this.symbol = symbol;
    }

    public Node(String symbol, Word word) {
        this.symbol = symbol;
        this.word = word;
    }


    public void addSon(Node son) {
        sons.add(son);
    }

    public void addSons(ArrayList<Node> sons) {
        this.sons.addAll(sons);
    }

    public ArrayList<Node> getSons() {
        return sons;
    }

    public String getSymbol() {
        return symbol;
    }

    public String toString() {
        return symbol + " " + word + "\n";
    }

    public void findError() {
        for (Node son : sons) {
            son.findError();
        }
    }

    protected String getFuncType() {
        return null;
    }

    protected ArrayList<Integer> getParaTypes() {
        return null;
    }

    protected int getParaType() {
        return 0;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    public void setWhileSmt(boolean isWhileSmt) {
        this.isWhileSmt = isWhileSmt;
    }

    public boolean isWhileSmt() {
        return isWhileSmt;
    }

    public Node getChild(int a) {
        return sons.get(a);
    }

    public Node getFather() {
        return father;
    }

    public String getWord() {
        return word.getWord();
    }

    public int getLine() {
        return word.getLine();
    }

    public void setRightLabel(String rightLabel) {
        this.rightLabel = rightLabel;
    }

    public void setWrongLabel(String wrongLabel) {
        this.wrongLabel = wrongLabel;
    }

    public void setBeginLabel(String beginLabel) {
        this.beginLabel = beginLabel;
    }

    public String getRightLabel() {
        return rightLabel;
    }

    public String getWrongLabel() {
        return wrongLabel;
    }

    public String getBeginLabel() {
        return beginLabel;
    }
}
