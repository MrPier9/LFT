public class NumberTok extends Token {
	public static int lexeme;

	// public NumberTok(int tag, int n) {
	// super(tag);
	// lexeme = n;
	// }

	public NumberTok(int tag, int n) {
		super(tag);
		lexeme = n;
	}

	public String toString() {
		return "<" + tag + "," + lexeme + ">";
	}

	public static int getVal() {
		return lexeme;
	}

	public static void setVal(int n) {
		lexeme = n;
	}

	public static final NumberTok num = new NumberTok(Tag.NUM, lexeme);

	// public final NumberTok num = new NumberTok(Tag.NUM, lexeme);
}
