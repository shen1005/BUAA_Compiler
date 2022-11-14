package WordAnalysis;
import java.util.ArrayList;
import java.util.HashMap;

public class WordAnalysis {
    private ArrayList<Word> words = new ArrayList<Word>();
    private HashMap<String, String> reverseMaps = new HashMap<String, String>();
    private ArrayList<String> reservedWords = new ArrayList<String>();
    private int line = 1;

    public WordAnalysis() {
        reverseMaps.put("main", "MAINTK");
        reverseMaps.put("int", "INTTK");
        reverseMaps.put("const", "CONSTTK");
        reverseMaps.put("return", "RETURNTK");
        reverseMaps.put("break", "BREAKTK");
        reverseMaps.put("continue", "CONTINUETK");
        reverseMaps.put("if", "IFTK");
        reverseMaps.put("else", "ELSETK");
        reverseMaps.put("while", "WHILETK");
        reverseMaps.put("printf", "PRINTFTK");
        reverseMaps.put("getint", "GETINTTK");
        reverseMaps.put("void", "VOIDTK");
    }

    public void Analyze(String text) {
        int pos = 0;
        while (pos < text.length()) {
            if (text.charAt(pos) == ' ' || text.charAt(pos) == '\t' ||
                    text.charAt(pos) == '\n' || text.charAt(pos) == '\r') {
                if (text.charAt(pos) == '\n') {
                    line++;
                }
                pos++;
            } else if (Character.isLetter(text.charAt(pos)) || text.charAt(pos) == '_') {
                pos = dealWord(pos, text);
            } else if (Character.isDigit(text.charAt(pos))) {
                pos = dealNumber(pos, text);
            } else if (text.charAt(pos) == '+' || text.charAt(pos) == '-' ||
                    text.charAt(pos) == '*' ||
                    text.charAt(pos) == '<' || text.charAt(pos) == '>' ||
                    text.charAt(pos) == '=' || text.charAt(pos) == '!' ||
                    text.charAt(pos) == '&' || text.charAt(pos) == '|' ||
                     text.charAt(pos) == '%') {
                pos = dealOperator(pos, text);
            } else if (text.charAt(pos) == '/') {
                if (pos + 1 < text.length() && (text.charAt(pos + 1) == '/' || text.charAt(pos + 1) == '*')) {
                    pos = dealComment(pos, text);
                } else {
                    pos = dealOperator(pos, text);
                }
            } else if (text.charAt(pos) == '"') {
                pos = dealString(pos, text);
            } else if (text.charAt(pos) == '\'') {
                pos += 2;
            } else if (text.charAt(pos) == '{' || text.charAt(pos) == '}' ||
                    text.charAt(pos) == '(' || text.charAt(pos) == ')' ||
                    text.charAt(pos) == '[' || text.charAt(pos) == ']') {
                pos = dealBracket(pos, text);
            } else if (text.charAt(pos) == ',') {
                pos++;
                words.add(new Word("COMMA", ",", line));
            } else if (text.charAt(pos) == ';') {
                pos++;
                words.add(new Word("SEMICN", ";", line));
            } else {
                pos++;
            }
        }
    }

    private int dealWord(int pos, String text) {
        String temp = "";
        while (pos < text.length() && (Character.isLetter(text.charAt(pos)) || Character.isDigit(text.charAt(pos)) ||
                text.charAt(pos) == '_')) {
            temp += text.charAt(pos);
            pos++;
        }

        if (reverseMaps.containsKey(temp)) {
            reservedWords.add(temp);
            words.add(new Word(reverseMaps.get(temp), temp, line));
        } else {
            words.add(new Word("IDENFR", temp, line));
        }
        return pos;
    }

    public int dealNumber(int pos, String text) {
        String temp = "";
        while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
            temp += text.charAt(pos);
            pos++;
        }
        words.add(new Word("INTCON", temp, line));
        return pos;
    }

    public int dealOperator(int pos, String text) {
        String temp = "";
        temp += text.charAt(pos);
        switch (text.charAt(pos)) {
            case '+':
                words.add(new Word("PLUS", temp, line));
                pos++;
                break;
            case '-':
                words.add(new Word("MINU", temp, line));
                pos++;
                break;
            case '*':
                words.add(new Word("MULT", temp, line));
                pos++;
                break;
            case '/':
                words.add(new Word("DIV", temp, line));
                pos++;
                break;
            case '<':
                if (pos + 1 < text.length() && text.charAt(pos + 1) == '=') {
                    temp += text.charAt(pos + 1);
                    words.add(new Word("LEQ", temp, line));
                    pos += 2;
                } else {
                    words.add(new Word("LSS", temp, line));
                    pos++;
                }
                break;
            case '>':
                if (pos + 1 < text.length() && text.charAt(pos + 1) == '=') {
                    temp += text.charAt(pos + 1);
                    words.add(new Word("GEQ", temp, line));
                    pos += 2;
                } else {
                    words.add(new Word("GRE", temp, line));
                    pos++;
                }
                break;
            case '=':
                if (pos + 1 < text.length() && text.charAt(pos + 1) == '=') {
                    temp += text.charAt(pos + 1);
                    words.add(new Word("EQL", temp, line));
                    pos += 2;
                } else {
                    words.add(new Word("ASSIGN", temp, line));
                    pos++;
                }
                break;
            case '!':
                if (pos + 1 < text.length() && text.charAt(pos + 1) == '=') {
                    temp += text.charAt(pos + 1);
                    words.add(new Word("NEQ", temp, line));
                    pos += 2;
                } else {
                    words.add(new Word("NOT", temp, line));
                    pos++;
                }
                break;
            case '%':
                words.add(new Word("MOD", temp, line));
                pos++;
                break;
            case '&':
                if (pos + 1 < text.length() && text.charAt(pos + 1) == '&') {
                    temp += text.charAt(pos + 1);
                    words.add(new Word("AND", temp, line));
                    pos += 2;
                } else {
                    words.add(new Word("BITAND", temp, line));
                    pos++;
                }
                break;
            case '|':
                if (pos + 1 < text.length() && text.charAt(pos + 1) == '|') {
                    temp += text.charAt(pos + 1);
                    words.add(new Word("OR", temp, line));
                    pos += 2;
                } else {
                    words.add(new Word("BITOR", temp, line));
                    pos++;
                }
                break;
            default:
                pos++;
                break;
        }
        return pos;
    }

    public int dealComment(int pos, String text) {
        if (text.charAt(pos + 1) == '/') {
            pos += 2;
            while (pos < text.length() && text.charAt(pos) != '\n') {
                pos++;
            }
        } else {
            pos += 2;
            while (pos < text.length() && !(text.charAt(pos) == '*' && pos + 1 < text.length() && text.charAt(pos + 1) == '/')) {
                if (text.charAt(pos) == '\n') {
                    line++;
                }
                pos++;
            }
            pos += 2;
        }
        return pos;
    }

    public int dealString(int pos, String text) {
        pos++;
        String temp = "";
        temp += '"';
        while (pos < text.length() && text.charAt(pos) != '"') {
            temp += text.charAt(pos);
            pos++;
        }
        temp += '"';
        words.add(new Word("STRCON", temp, line));
        pos++;
        return pos;
    }

    public int dealSeparator(int pos, String text) {
        pos++;
        while (text.charAt(pos) != ')' && text.charAt(pos) != ']' && text.charAt(pos) != '}') {
            pos++;
        }
        pos++;
        return pos;
    }


    public int dealBracket(int pos, String text) {
        String temp = "";
        temp += text.charAt(pos);
        switch (text.charAt(pos)) {
            case '{':
                words.add(new Word("LBRACE", temp, line));
                pos++;
                break;
            case '}':
                words.add(new Word("RBRACE", temp, line));
                pos++;
                break;
            case '(':
                words.add(new Word("LPARENT", temp, line));
                pos++;
                break;
            case ')':
                words.add(new Word("RPARENT", temp, line));
                pos++;
                break;
            case '[':
                words.add(new Word("LBRACK", temp, line));
                pos++;
                break;
            case ']':
                words.add(new Word("RBRACK", temp, line));
                pos++;
                break;
            default:
                pos++;
                break;
        }
        return pos;
    }


    public ArrayList<Word> getWords() {
        return words;
    }

}

