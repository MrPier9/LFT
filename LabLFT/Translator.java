import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;
    Boolean flag = true;
    int fexprlist = 0;

    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error");
    }

    public void prog() {
        switch (look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
            case '}':
                int lnext_prog = code.newLabel();
                statlist(lnext_prog);
                code.emitLabel(lnext_prog);
                match(Tag.EOF);
                try {
                    code.toJasmin();
                } catch (java.io.IOException e) {
                    System.out.println("IO error\n");
                }
                break;
            default:
                error("Syntax error in prog");
        }
    }

    private void statlist(int nextstatlist_i) {
        switch (look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
            case '}':
                stat(nextstatlist_i);
                int lnext_statlistp = code.newLabel();
                code.emit(OpCode.GOto, lnext_statlistp);
                statlistp(lnext_statlistp);
                if (flag == true)
                    code.emit(OpCode.GOto, nextstatlist_i);
                break;
            default:
                error("Syntax error in statlist");
        }
    }

    private void statlistp(int nextstatlistp_i) {
        switch (look.tag) {
            case ';':
                match(';');
                stat(nextstatlistp_i);
                int lnext_statlistp;
                lnext_statlistp = code.newLabel();
                code.emit(OpCode.GOto, lnext_statlistp);
                statlistp(lnext_statlistp);
                break;
            case '}':
            case Tag.EOF:
                code.emitLabel(nextstatlistp_i);
                break;
            default:
                error("Syntax error in statlistp");
        }
    }

    public void stat(int nextstat_i) {
        int lnext_t, lnext_while, lnext_f;
        switch (look.tag) {
            case Tag.ASSIGN:
                if (nextstat_i != 0)
                    code.emitLabel(nextstat_i);
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(Tag.ASSIGN);
                break;
            case Tag.READ:
                if (nextstat_i != 0)
                    code.emitLabel(nextstat_i);
                match(Tag.READ);
                code.emit(OpCode.invokestatic);
                match('(');
                idlist(/* completare */Tag.READ);
                match(')');
                break;
            case Tag.PRINT:
                if (nextstat_i != 0)
                    code.emitLabel(nextstat_i);
                match(Tag.PRINT);
                match('(');
                exprlist();
                match(')');
                code.emit(OpCode.invokestatic, 1);
                break;
            case Tag.WHILE:
                if (nextstat_i != 0)
                    lnext_while = nextstat_i;
                else
                    lnext_while = code.newLabel();
                code.emitLabel(lnext_while);
                match(Tag.WHILE);
                match('(');
                lnext_t = code.newLabel();
                bexpr(lnext_t);
                match(')');
                lnext_f = code.newLabel();
                code.emit(OpCode.GOto, lnext_f);
                stat(lnext_t);
                code.emit(OpCode.GOto, lnext_while);
                code.emitLabel(lnext_f);
                break;
            case Tag.IF:
                if (nextstat_i != 0)
                    code.emitLabel(nextstat_i);
                match(Tag.IF);
                match('(');
                lnext_t = code.newLabel();
                bexpr(lnext_t);
                match(')');
                lnext_f = code.newLabel();
                code.emit(OpCode.GOto, lnext_f);
                stat(lnext_t);
                int lnext = code.newLabel();
                code.emit(OpCode.GOto, lnext);
                statp(lnext_f, lnext);
                break;
            case '{':
                flag = false;
                match('{');
                statlist(nextstat_i);
                match('}');
                flag = true;
                break;
            default:
                error("Syntax error in stat");
        }
    }

    private void statp(int lnext_f, int lnext) {
        switch (look.tag) {
            case Tag.END:
                match(Tag.END);
                code.emitLabel(lnext_f);
                code.emit(OpCode.GOto, lnext);
                code.emitLabel(lnext);
                break;
            case Tag.ELSE:
                match(Tag.ELSE);
                stat(lnext_f);
                match(Tag.END);
                code.emit(OpCode.GOto, lnext);
                code.emitLabel(lnext);
                break;
            default:
                error("Syntax error in statp");
        }
    }

    private void idlist(int opcode) {
        switch (look.tag) {
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                code.emit(OpCode.istore, id_addr);
                idlistp(opcode);
                break;
            default:
                error("Syntax error in idlist");
        }
    }

    private void idlistp(int opcode) {
        switch (look.tag) {
            case ',':
                match(',');
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                switch (opcode) {
                    case Tag.READ:
                        code.emit(OpCode.iload, id_addr);
                        break;
                    case Tag.ASSIGN:
                        code.emit(OpCode.istore, id_addr);
                        break;
                    default:
                        error("error in idlist");
                }
                idlistp(opcode);
                break;
            case ')':
            case ';':
            case Tag.END:
            case Tag.ELSE:
            case '}':
            case Tag.EOF:
                break;
            default:
                error("Syntax error in idlistp");
        }
    }

    private void bexpr(int label_t) {
        switch (look.tag) {
            case Tag.RELOP:
                String lexeme = ((Word) look).lexeme;
                match(Tag.RELOP);
                expr();
                expr();
                if (lexeme == "<")
                    code.emit(OpCode.if_icmplt, label_t);
                else if (lexeme == ">")
                    code.emit(OpCode.if_icmpgt, label_t);
                else if (lexeme == "<=")
                    code.emit(OpCode.if_icmple, label_t);
                else if (lexeme == ">=")
                    code.emit(OpCode.if_icmpge, label_t);
                else if (lexeme == "==")
                    code.emit(OpCode.if_icmpeq, label_t);
                else if (lexeme == "<>")
                    code.emit(OpCode.if_icmpne, label_t);
                break;
            default:
                error("Syntax error in bexpr");
        }
    }

    private void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                if (fexprlist >= 1) {
                    code.emit(OpCode.iadd);
                    fexprlist = 0;
                } else {
                    fexprlist = 0;
                }
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                if (fexprlist >= 1) {
                    code.emit(OpCode.imul);
                    fexprlist = 0;
                } else {
                    fexprlist = 0;
                }
                break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                int numVal = NumberTok.getVal();
                code.emit(OpCode.ldc, numVal);
                match(Tag.NUM);
                break;
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                code.emit(OpCode.iload, id_addr);
                break;
            default:
                error("Syntax error in expr");
        }
    }

    private void exprlist() {
        switch (look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            default:
                error("Syntax error in exprlist");
        }
    }

    private void exprlistp() {
        switch (look.tag) {
            case ',':
                fexprlist++;
                match(',');
                expr();
                exprlistp();
                break;
            case ')':
                break;
            default:
                error("Syntax error in exprlistp");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./es1.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
