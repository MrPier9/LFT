import java.io.*;

public class Valutatore {
        private Lexer lex;
        private BufferedReader pbr;
        private Token look;

        public Valutatore(Lexer l, BufferedReader br) {
                lex = l;
                pbr = br;
                move();
        }

        void move() {
                // come in Esercizio 3.1
                look = lex.lexical_scan(pbr);
        }

        void error(String s) {
                // come in Esercizio 3.1
                throw new Error("near line " + lex.line + ": " + s);
        }

        void match(int t) {
                // come in Esercizio 3.1
                if (look.tag == t) {
                        if (look.tag != Tag.EOF)
                                move();
                } else
                        error("syntax error");
        }

        public void start() {
                int expr_val;

                // ... completare ...
                switch (look.tag) {
                        case '(':
                        case Tag.NUM:
                                expr_val = expr();
                                match(Tag.EOF);
                                break;
                        default:
                                expr_val = -1;
                                error("Syntax error in start");
                }
                // ... completare ...
                System.out.println(expr_val);
        }

        private int expr() {
                int term_val, exprp_val;

                // ... completare ...
                switch (look.tag) {
                        case '(':
                        case Tag.NUM:
                                term_val = term();
                                exprp_val = exprp(term_val);
                                break;
                        default:
                                exprp_val = 0;
                                error("Syntax error in expr");
                                break;
                }
                // ... completare ...
                return exprp_val;
        }

        private int exprp(int exprp_i) {
                int term_val, exprp_val;
                switch (look.tag) {
                        case '+':
                                match('+');
                                term_val = term();
                                exprp_val = exprp(exprp_i + term_val);
                                break;
                        case '-':
                                match('-');
                                term_val = term();
                                exprp_val = exprp(exprp_i - term_val);
                                break;
                        case ')':
                        case -1:
                                exprp_val = exprp_i;
                                break;
                        default:
                                exprp_val = 0;
                                error("Syntax error in exprp");
                                break;

                        // ... completare ...
                }
                return exprp_val;
        }

        private int term() {
                // ... completare ...
                int termp_i, term_val;
                switch (look.tag) {
                        case '(':
                        case Tag.NUM:
                                termp_i = fact();
                                term_val = termp(termp_i);
                                break;
                        default:
                                term_val = 0;
                                error("Syntax error in term");
                }
                return term_val;
        }

        private int termp(int termp_i) {
                // ... completare ...
                int fact_val, termp_val;
                switch (look.tag) {
                        case '*':
                                match('*');
                                fact_val = fact();
                                termp_val = termp(termp_i * fact_val);
                                break;
                        case '/':
                                match('/');
                                fact_val = fact();
                                termp_val = termp(termp_i / fact_val);
                                break;
                        case '+':
                        case '-':
                        case ')':
                        case -1:
                                termp_val = termp_i;
                                break;
                        default:
                                termp_val = 0;
                                error("Syntax error in termp");
                                break;
                }
                return termp_val;
        }

        private int fact() {
                // ... completare ...
                int fact_val, expr_val;
                switch (look.tag) {
                        case '(':
                                match('(');
                                expr_val = expr();
                                match(')');
                                fact_val = expr_val;
                                break;
                        case Tag.NUM:
                                match(Tag.NUM);
                                fact_val = NumberTok.getVal();
                                break;
                        default:
                                fact_val = 0;
                                error("Syntax error in fact");
                }
                return fact_val;
        }

        public static void main(String[] args) {
                Lexer lex = new Lexer();
                String path = "./prova2.txt"; // il percorso del file da leggere
                try {
                        BufferedReader br = new BufferedReader(new FileReader(path));
                        Valutatore valutatore = new Valutatore(lex, br);
                        valutatore.start();
                        br.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}