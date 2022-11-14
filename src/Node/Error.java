package Node;

public class Error implements Comparable<Error> {
    private int line;
    private String error;

    public Error(int line, String error) {
        this.line = line;
        this.error = error;
    }

    @Override
    public String toString() {
        return line + " " + error + '\n';
    }

    public int getLine() {
        return line;
    }

    @Override
    public int compareTo(Error o) {
        return line - o.line;
    }
}
