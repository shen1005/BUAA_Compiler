package WordAnalysis;

public class Word {
    protected String symbol;
    protected String word;
    protected int line;

    public Word(String symbol, String word, int line) {
        this.symbol = symbol;
        this.word = word;
        this.line = line;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return symbol + " " + word + "\n";
    }

    public int getLine() {
        return line;
    }
}
