import java.io.*;
//import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    private char old_peek = ' ';
    private char next_peek = ' ';
    private boolean flag = true;

    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {

        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n') {
                line++;
            }
            // System.out.println(flag);
            if (flag == true) {
                readch(br);
            } else {
                flag = true;
            }

            if (peek != (char) -1 && !Character.isLetter(peek) && Character.isLetter(next_peek)) {
                String s = "";
                flag = false;
                s = s + next_peek;
                next_peek = ' ';
                new Word(257, s);
            }
            if (peek != (char) -1 && !Character.isDigit(peek) && Character.isDigit(next_peek)) {
                String s = "";
                flag = false;
                s = s + next_peek;
                next_peek = ' ';
                int num = Integer.valueOf(s);
                return new NumberTok(num);
            }

        }

        switch (peek)

        {
            case '!':
                peek = ' ';
                return Token.not;

            // ... gestire i casi di ( ) { } + - * / ; , ... //
            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek = ' ';
                return Token.rpt;

            case '{':
                peek = ' ';
                return Token.lpg;

            case '}':
                peek = ' ';
                return Token.rpg;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '*':
                peek = ' ';
                return Token.mult;

            case '/':
                readch(br);
                if (peek == '*') {
                    while (true) {
                        readch(br);
                        if (peek == '*') {
                            old_peek = peek;
                            peek = ' ';
                        }
                        if (peek == '/' && old_peek == '*') {
                            peek = ' ';
                            /* return null; */
                            return new Token(0);
                        }
                        if (peek == (char) -1) {
                            System.err.println("Erroneous character: "
                                    + "interrupted comment");
                            return null;
                        }
                    }
                } else if (peek == '/') {
                    while (peek != '\n')
                        readch(br);
                    return new Token(0);
                } else {
                    next_peek = peek;
                    peek = ' ';
                    return Token.div;
                }

            case ';':
                peek = ' ';
                return Token.semicolon;

            case ',':
                peek = ' ';
                return Token.comma;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : " + peek);
                    return null;
                }

                // ... gestire i casi di || < > <= >= == <> ... //
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : " + peek);
                    return null;
                }

            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else {
                    next_peek = peek;
                    peek = ' ';
                    return Word.lt;
                }

            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    next_peek = peek;
                    peek = ' ';
                    return Word.gt;
                }

            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after = : " + peek);
                    return null;
                }

            case (char) -1:
                if (next_peek == ' ')
                    return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek) || (Character.isLetter(next_peek) && peek == (char) -1)) {

                    // ... gestire il caso degli identificatori e delle parole chiave //
                    String s = "";
                    if (Character.isLetter(next_peek)) {
                        s = s + next_peek;
                        flag = false;
                        next_peek = ' ';
                    }

                    if (peek != (char) -1) {
                        do {
                            s = s + peek;
                            readch(br);
                            if (peek == '_') {
                                s = s + peek;
                                readch(br);
                                if (peek >= '0' && peek <= '9') { //
                                    s = s + peek; //
                                    readch(br); //
                                } // nel commento
                            } else if (peek >= '0' && peek <= '9') { // parte es 2.2
                                s = s + peek; //
                                readch(br); //
                            }
                        } while (peek >= 'a' && peek <= 'z' || peek >= '0' && peek <= '9');
                    } else
                        return new Word(257, s);

                    switch (s) {
                        case "assign":
                            return Word.assign;

                        case "to":
                            return Word.to;

                        case "if":
                            return Word.iftok;

                        case "else":
                            return Word.elsetok;

                        case "while":
                            return Word.whiletok;

                        case "begin":
                            return Word.begin;

                        case "end":
                            return Word.end;

                        case "print":
                            return Word.print;

                        case "read":
                            return Word.read;

                        default:
                            return new Word(257, s);
                    }
                } else if (Character.isDigit(peek) || (Character.isDigit(next_peek) && peek == (char) -1)) {

                    // ... gestire il caso dei numeri ... //
                    String s = "";
                    if (Character.isDigit(next_peek)) {
                        s = s + next_peek;
                        next_peek = ' ';

                        if (peek == (char) -1) {
                            next_peek = ' ';
                            flag = false;
                            int num = Integer.valueOf(s);
                            return new NumberTok(num);
                        }
                    }
                    old_peek = peek;
                    readch(br);
                    flag = false;

                    if (old_peek == '0' && (peek >= '0' && peek <= '9')) {
                        System.err.println("Erroneous character <"
                                + old_peek + "> as first digit");
                        return null;
                    }

                    s = s + old_peek;
                    int n;

                    while (peek >= '0' && peek <= '9') {
                        s = s + peek;
                        readch(br);
                        if (peek >= 'a' && peek <= 'z') {
                            System.err.println("Erroneous character"
                                    + " after number");
                            return null;
                        }
                    }
                    n = Integer.valueOf(s);
                    return new NumberTok(n);
                } else {
                    System.err.println("Erroneous character: "
                            + peek);
                    return null;
                }
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                if (tok.tag != 0)
                    System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}