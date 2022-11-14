package Node;

import WordAnalysis.Word;

public class LeafNode extends Node{

    public LeafNode(String symbol, Word word, Node father) {
        super(symbol);
    }

    public LeafNode(String symbol, Word word) {
        super(symbol, word);
    }

    @Override
    public String toString() {
        return getSymbol() + " " + word.getWord() + '\n';
    }

    @Override
    public void findError() {
        if (getSymbol().equals("STRCON")) {
            String str = word.getWord();
            int i = 1;
            boolean flag = true;
            while (i < str.length() - 1) {
                if (str.charAt(i) == '%') {
                    if ( str.charAt(i + 1) != 'd') {
                        flag = false;
                        break;
                    }
                }
                if (str.charAt(i) == '\\') {
                    if (str.charAt(i + 1) != 'n') {
                        flag = false;
                        break;
                    }
                }
                char c = str.charAt(i);
                if (!(c == '%' || c == ' ' || c == '!' || (c <= 126 && c >= 40))) {
                    flag = false;
                    break;
                }
                i++;
            }
            if (!flag) {
                errorAnalysis.addError(word.getLine(), "a");
            }
        }
    }
}
