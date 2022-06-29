import java.io.*;

public class Parser32 {
  private Lexer lex;
  private BufferedReader pbr;
  private Token look;

  public Parser32(Lexer l, BufferedReader br) {
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
      error("syntax error match");
  }

  public void prog() {
    // ... completare ...
    System.out.println("prog");
    switch (look.tag) {
      case Tag.ASSIGN:
      case Tag.PRINT:
      case Tag.READ:
      case Tag.WHILE:
      case Tag.IF:
      case '}':
        statlist();
        match(-1);
        break;
      default:
        error("Syntax error in prog");
    }

  }

  private void statlist() {
    // ... completare ...
    System.out.println("statlist");
    switch (look.tag) {
      case Tag.ASSIGN:
      case Tag.PRINT:
      case Tag.READ:
      case Tag.WHILE:
      case Tag.IF:
      case '}':
        stat();
        statlistp();
        break;
      default:
        error("Syntax error in statlist");
    }
  }

  private void statlistp() {
    System.out.println("statlistp");
    switch (look.tag) {
      case ';':
        match(';');
        stat();
        statlistp();
        break;
      case '}':
      case Tag.EOF:
        break;
      default:
        error("Syntax error in statlistp");
    }
  }

  private void stat() {
    // ... completare ...
    System.out.println("stat");
    switch (look.tag) {
      case Tag.ASSIGN:
        match(Tag.ASSIGN);
        expr();
        match(Tag.TO);
        idlist();
        break;
      case Tag.PRINT:
        match(Tag.PRINT);
        match('(');
        exprlist();
        match(')');
        break;
      case Tag.READ:
        match(Tag.READ);
        match('(');
        idlist();
        match(')');
        break;
      case Tag.WHILE:
        match(Tag.WHILE);
        match('(');
        bexpr();
        match(')');
        stat();
        break;
      case Tag.IF:
        match(Tag.IF);
        match('(');
        bexpr();
        match(')');
        stat();
        statp();
      case '{':
        match('{');
        statlist();
        match('}');
        break;
      default:
        error("Syntax error in stat");
    }
  }

  private void statp() {
    // ... completare ...
    System.out.println("statp");
    switch (look.tag) {
      case Tag.END:
        match(Tag.END);
        break;
      case Tag.ELSE:
        match(Tag.ELSE);
        stat();
        statp();
        match(Tag.END);
        break;
      default:
        error("Syntax error in statp");
    }
  }

  private void idlist() {
    // ... completare ...
    System.out.println("idlist");
    switch (look.tag) {
      case Tag.ID:
        match(Tag.ID);
        idlistp();
        break;
      default:
        error("Syntax error in idlist");
    }
  }

  private void idlistp() {
    // ... completare ...
    System.out.println("idlistp");
    switch (look.tag) {
      case ',':
        match(',');
        match(Tag.ID);
        idlistp();
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

  private void bexpr() {
    // ... completare ...
    System.out.println("bexpr");
    switch (look.tag) {
      case Tag.RELOP:
        match(Tag.RELOP);
        expr();
        expr();
        break;
      default:
        error("Syntax error in bexpr");
    }
  }

  private void expr() {
    // ... completare ...
    System.out.println("expr");
    switch (look.tag) {
      case '+':
        match('+');
        match('(');
        exprlist();
        match(')');
        break;
      case '-':
        match('-');
        expr();
        expr();
        break;
      case '*':
        match('*');
        match('(');
        exprlist();
        match(')');
        break;
      case '/':
        match('/');
        expr();
        expr();
        break;
      case Tag.NUM:
        match(Tag.NUM);
        break;
      case Tag.ID:
        match(Tag.ID);
        break;
      default:
        error("Syntax error in expr");
    }
  }

  private void exprlist() {
    // ... completare ...
    System.out.println("exprlist");
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
    // ... completare ...
    System.out.println("exprlistp");
    switch (look.tag) {
      case ',':
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
    String path = "./esempio_semplice.txt"; // il percorso del file da leggere
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
      Parser32 parser = new Parser32(lex, br);
      parser.prog();
      System.out.println("Input OK");
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}