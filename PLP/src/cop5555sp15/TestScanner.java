package cop5555sp15;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import cop5555sp15.TokenStream;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import static cop5555sp15.TokenStream.Kind.*;

public class TestScanner {

	@Test
	public void emptyInput() {
		System.out.println("Test: emptyInput");
		String input = "";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		assertEquals(1, stream.tokens.size()); // creates EOF token
		assertEquals(EOF, stream.nextToken().kind);

	}

	@Test
	public void noWhiteSpace() {
		System.out.println("Test: noWhitespace");
		String input = "@%";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		assertEquals(3, stream.tokens.size()); // one each for @ and %, plus the
												// eof
												// token
		assertEquals(AT, stream.nextToken().kind);
		assertEquals(MOD, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);

	}

	@Test
	public void errorToken() {
		System.out.println("Test: noWhitespace");
		String input = "@#  *";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		assertEquals(4, stream.tokens.size()); // one each for @,#, and *, plus
												// the eof token
		assertEquals(AT, stream.nextToken().kind);
		assertEquals(ILLEGAL_CHAR, stream.nextToken().kind);
		assertEquals(TIMES, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);

	}

	@Test
	public void onlySpaces() {
		System.out.println("Test: onlySpaces");
		String input = "     "; // five spaces
		System.out.println("input is five spaces");
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		assertEquals(1, stream.tokens.size()); // creates EOF token
		Token t = stream.nextToken();
		System.out.println(stream);
		assertEquals(EOF, t.kind);
		assertEquals(5, t.beg);
	}

	@Test
	public void skipWhiteSpace() {
		System.out.println("skipWhiteSpace");
		String input = "   ;;;   %@%\n  \r   \r\n ;;;";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		assertEquals(SEMICOLON, stream.nextToken().kind);
		assertEquals(SEMICOLON, stream.nextToken().kind);
		assertEquals(SEMICOLON, stream.nextToken().kind);
		assertEquals(MOD, stream.nextToken().kind);
		assertEquals(AT, stream.nextToken().kind);
		assertEquals(MOD, stream.nextToken().kind);
		assertEquals(SEMICOLON, stream.nextToken().kind);
		assertEquals(SEMICOLON, stream.nextToken().kind);
		Token t = stream.nextToken();
		assertEquals(SEMICOLON, t.kind);
		assertEquals(4, t.getLineNumber());
	}

	@Test
	public void dotsAndRanges() {
		System.out.println("dotsAndRanges");
		String input = ".\n..\n.. . . ..\n...\n";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		assertEquals(DOT, stream.nextToken().kind);
		assertEquals(RANGE, stream.nextToken().kind);
		assertEquals(RANGE, stream.nextToken().kind);
		assertEquals(DOT, stream.nextToken().kind);
		assertEquals(DOT, stream.nextToken().kind);
		assertEquals(RANGE, stream.nextToken().kind);
		assertEquals(RANGE, stream.nextToken().kind);
		assertEquals(DOT, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);
		assertEquals(3, stream.tokens.get(5).getLineNumber());// 5th token is on
																// line 3
	}

	@Test
	public void firstPartAtEndOfInput() {
		System.out.println("firstPartATEndOfInput");
		String input = "!";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		assertEquals(NOT, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);
	}

	@Test
	public void twoStateTokens() {
		System.out.println("twoStateTokens");
		String input = "= == =\n= ! != - -> -! =!!";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		assertEquals(ASSIGN, stream.nextToken().kind);
		assertEquals(EQUAL, stream.nextToken().kind);
		assertEquals(ASSIGN, stream.nextToken().kind);
		assertEquals(ASSIGN, stream.nextToken().kind);
		assertEquals(NOT, stream.nextToken().kind);
		assertEquals(NOTEQUAL, stream.nextToken().kind);
		assertEquals(MINUS, stream.nextToken().kind);
		assertEquals(ARROW, stream.nextToken().kind);
		assertEquals(MINUS, stream.nextToken().kind);
		assertEquals(NOT, stream.nextToken().kind);
		assertEquals(ASSIGN, stream.nextToken().kind);
		assertEquals(NOT, stream.nextToken().kind);
		assertEquals(NOT, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);
	}

	// This test constructs the exptected token list and compares to the one
	// created by the Scanner
	@Test
	public void compareTokenList() {
		System.out.println("compareTokenList");
		String input = "= ==";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Token t0 = stream.new Token(ASSIGN, 0, 1, 1);
		Token t1 = stream.new Token(EQUAL, 2, 4, 1);
		Token t2 = stream.new Token(EOF, 4, 4, 1);
		ArrayList<Token> expected_tokens = new ArrayList<Token>();
		expected_tokens.add(t0);
		expected_tokens.add(t1);
		expected_tokens.add(t2);
		assertArrayEquals(expected_tokens.toArray(), stream.tokens.toArray());
	}

	@Test
	public void lessAndGreater() {
		System.out.println("lessAndGreater");
		String input = " < << <= > >> >= -> <>";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		assertEquals(LT, stream.nextToken().kind);
		assertEquals(LSHIFT, stream.nextToken().kind);
		assertEquals(LE, stream.nextToken().kind);
		assertEquals(GT, stream.nextToken().kind);
		assertEquals(RSHIFT, stream.nextToken().kind);
		assertEquals(GE, stream.nextToken().kind);
		assertEquals(ARROW, stream.nextToken().kind);
		assertEquals(LT, stream.nextToken().kind);
		assertEquals(GT, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);
	}

	@Test
	public void intLiterals() {
		System.out.println("lessAndGreater");
		String input = "0 1 23 45+ 67<=9";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, INT_LIT, PLUS,
				INT_LIT, LE, INT_LIT, EOF };
		String[] expectedTexts = { "0", "1", "23", "45", "+", "67", "<=", "9",
				"" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void stringLiterals() {
		System.out.println("stringLiterals");
		String input = " \"abc\" \"def\" \"ghijk\" \"123\" \"&^%$\" ";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { STRING_LIT, STRING_LIT, STRING_LIT,
				STRING_LIT, STRING_LIT, EOF };
		String[] expectedTexts = { "abc", "def", "ghijk", "123", "&^%$", "" }; // need
																				// empty
																				// string
																				// for
																				// eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void identifiers() {
		System.out.println("identifiers");
		String input = " abc ddef ghijk 123 a234 32a";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { IDENT, IDENT, IDENT, INT_LIT, IDENT, INT_LIT,
				IDENT, EOF };
		String[] expectedTexts = { "abc", "ddef", "ghijk", "123", "a234", "32",
				"a", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void keywords() {
		System.out.println("keywords");
		String input = " int  string  boolean import  class  def  while if  else  return  print aaa";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { KW_INT, KW_STRING, KW_BOOLEAN, KW_IMPORT,
				KW_CLASS, KW_DEF, KW_WHILE, KW_IF, KW_ELSE, KW_RETURN,
				KW_PRINT, IDENT, EOF };
		String[] expectedTexts = { "int", "string", "boolean", "import",
				"class", "def", "while", "if", "else", "return", "print",
				"aaa", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void boolAndNullLiterals() {
		System.out.println("boolAndNullLiterals");
		String input = " true false\n null";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { BL_TRUE, BL_FALSE, NL_NULL, EOF };
		String[] expectedTexts = { "true", "false", "null", "" }; // need empty
																	// string
																	// for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void multiLineString() {
		System.out.println("multiLineString");
		String input = " \"true false\n null\" ";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { STRING_LIT, EOF };
		String[] expectedTexts = { "true false\n null", "" }; // need empty
																// string for
																// eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));

	}

	@Test
	public void comments() {
		System.out.println("comments");
		String input = "/**/ 0 1 45+ 67<=9";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
				INT_LIT, EOF };
		String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "" }; // need
																				// empty
																				// string
																				// for
																				// eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void comments2() {
		System.out.println("comments2");
		String input = "/**/ 0 1 /** ***/ 45+ 67<=9";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
				INT_LIT, EOF };
		String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "" }; // need
																				// empty
																				// string
																				// for
																				// eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void comments3() {
		System.out.println("comments3");
		String input = "/**/ 0 1 /** ***/ 45+ 67<=9/*";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
				INT_LIT, UNTERMINATED_COMMENT, EOF };
		String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "/*",
				"" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void onlyComment() {
		System.out.println("onlyComment");
		String input = "/**/";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { EOF };
		String[] expectedTexts = { "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void singleSlash() {
		System.out.println("singleSlash");
		String input = "/";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
	}

	@Test
	public void illegalChars() {
		System.out.println("illegalchars");
		String input = "#abc#123#->#";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { ILLEGAL_CHAR, IDENT, ILLEGAL_CHAR, INT_LIT,
				ILLEGAL_CHAR, ARROW, ILLEGAL_CHAR, EOF };
		String[] expectedTexts = { "#", "abc", "#", "123", "#", "->", "#", "" }; // need
																					// empty
																					// string
																					// for
																					// eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void digits() {
		System.out.println("digits");
		String input = "1 2 3 0123";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT,
				EOF };
		String[] expectedTexts = { "1", "2", "3", "0", "123", "" }; // need
																	// empty
																	// string
																	// for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void unterminatedStrings() {
		System.out.println("Unterminated Strings");
		String input = "\" Hello";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { UNTERMINATED_STRING, EOF };
		String[] expectedTexts = { "\" Hello", "" }; // need empty string for
														// eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void multilineStrings() {
		System.out.println("multilineStrings");
		String input = "\"Hello \n Hello\" \"Hello World\"";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { STRING_LIT, STRING_LIT, EOF };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertSame(stream.tokens.get(0).lineNumber, 1);
		assertSame(stream.tokens.get(1).lineNumber, 2);
	}

	@Test
	public void stateswitching() {
		System.out.println("stateswitching");
		String input = "abc0123 0a 0123";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { IDENT, INT_LIT, IDENT, INT_LIT, INT_LIT, EOF };
		String[] expectedTexts = { "abc0123", "0", "a", "0", "123", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void operators() {
		System.out.println("operators");
		String input = "...>>><<<";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { RANGE, DOT, RSHIFT, GT, LSHIFT, LT, EOF };
		String[] expectedTexts = { "..", ".", ">>", ">", "<<", "<", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void operators2() {
		System.out.println("operators2");
		String input = "===-->";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { EQUAL, ASSIGN, MINUS, ARROW, EOF };
		String[] expectedTexts = { "==", "=", "-", "->", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void operators3() {
		System.out.println("operators3");
		String input = "!!==";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { NOT, NOTEQUAL, ASSIGN, EOF };
		String[] expectedTexts = { "!", "!=", "=", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void operators4() {
		System.out.println("operators4");
		String input = "<=>==>";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { LE, GE, ASSIGN, GT, EOF };
		String[] expectedTexts = { "<=", ">=", "=", ">", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void operators5() {
		System.out.println("operators5");
		String input = "|&+-*/%@";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { BAR, AND, PLUS, MINUS, TIMES, DIV, MOD, AT,
				EOF };
		String[] expectedTexts = { "|", "&", "+", "-", "*", "/", "%", "@", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void separators() {
		System.out.println("Separators");
		String input = ";,(){}[]:?";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { SEMICOLON, COMMA, LPAREN, RPAREN, LCURLY,
				RCURLY, LSQUARE, RSQUARE, COLON, QUESTION, EOF };
		String[] expectedTexts = { ";", ",", "(", ")", "{", "}", "[", "]", ":",
				"?", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void keywords1() {
		System.out.println("keywords1");
		String input = "print(\"Hello\")";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { KW_PRINT, LPAREN, STRING_LIT, RPAREN, EOF };
		String[] expectedTexts = { "print", "(", "Hello", ")", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void keywords2() {
		System.out.println("keywords2");
		String input = "int string boolean import class def while if else  return print true false";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { KW_INT, KW_STRING, KW_BOOLEAN, KW_IMPORT,
				KW_CLASS, KW_DEF, KW_WHILE, KW_IF, KW_ELSE, KW_RETURN,
				KW_PRINT, BL_TRUE, BL_FALSE, EOF };
		String[] expectedTexts = { "int", "string", "boolean", "import",
				"class", "def", "while", "if", "else", "return", "print",
				"true", "false", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void keywords3() {
		System.out.println("keywords2");
		String input = "size key value";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { KW_SIZE, KW_KEY, KW_VALUE, EOF };
		String[] expectedTexts = { "size", "key", "value", "" };
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	// Creates an array containing the kinds of the tokens in the token list
	Kind[] makeKindArray(TokenStream stream) {
		Kind[] kinds = new Kind[stream.tokens.size()];
		for (int i = 0; i < stream.tokens.size(); ++i) {
			kinds[i] = stream.tokens.get(i).kind;
		}
		return kinds;

	}

	// Creates an array containing the texts of the tokens in the token list
	String[] makeTokenTextArray(TokenStream stream) {
		String[] kinds = new String[stream.tokens.size()];
		for (int i = 0; i < stream.tokens.size(); ++i) {
			kinds[i] = stream.tokens.get(i).getText();
		}
		return kinds;
	}

}