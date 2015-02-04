package cop5555sp15;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import static cop5555sp15.TokenStream.Kind.*;

public class Scanner {

	TokenStream stream;
	FSA_STATES state;
	int linecount, beg, string_beg;

	private enum FSA_STATES {
		START, DIGIT, IDENT, COMMENT, STRING_LIT
	}

	public Scanner(TokenStream stream) {
		this.stream = stream;
		this.state = FSA_STATES.START;
		this.linecount = 1;
		this.beg = -1;
		this.string_beg = 1;
	}

	// Fills in the stream.tokens list with recognized tokens
	// from the input
	public void scan() {
		// IMPLEMENT THIS
		for (int i = 0; i <= stream.inputChars.length; i++) {
			switch (state) {
			case START:
				if (i == stream.inputChars.length) {
					Token t = stream.new Token(EOF, i, i, linecount);
					stream.tokens.add(t);
				} else if (Character.isWhitespace(lookup(i))) {
					beg++;
					if (lookup(i) == 10) {
						// If the input char is LF, increment the linecount
						linecount++;
					} else if (lookup(i) == 13) {
						// If the input char is a CR, look ahead and see if you
						// have a LF
						// If the next char is a LF, increment i by 1
						if (lookup(i + 1) == 10) {
							i++;
							beg++;
						}
						// Increment linecount
						linecount++;
					}
				} else if (lookup(i) == '/') {
					beg++;
					if (stream.inputChars.length != i + 1
							&& lookup(i + 1) == '*') {
						state = FSA_STATES.COMMENT;
						i++;
						beg++;
					} else {
						Token t = stream.new Token(DIV, i, i + 1, linecount);
						stream.tokens.add(t);
					}
				} else if (Character.isDigit(lookup(i))) {
					if (stream.inputChars[i] == '0') {
						Token t = stream.new Token(INT_LIT, i, i + 1, linecount);
						stream.tokens.add(t);
					} else {
						state = FSA_STATES.DIGIT;
						beg = i;
					}
				} else if (Character.isJavaIdentifierStart(lookup(i))) {
					state = FSA_STATES.IDENT;
					beg = i;
				} else if (isSeparator(stream.inputChars[i])) {
					Kind k = getSeparator(stream.inputChars[i]);
					if (k == ILLEGAL_CHAR) {
						System.out.println("There is a bug. Fix it!!");
						System.out.println(Thread.currentThread()
								.getStackTrace());
						System.exit(1);
					} else if (k == DOT) {
						if (lookup(i + 1) == '.') {
							Token t = stream.new Token(RANGE, i, i + 2,
									linecount);
							stream.tokens.add(t);
							i++;
							beg = beg + 1;
							continue;
						} else {
							Token t = stream.new Token(k, i, i + 1, linecount);
							stream.tokens.add(t);
						}
					} else {
						Token t = stream.new Token(k, i, i + 1, linecount);
						stream.tokens.add(t);
					}
				} else if (isOperator(stream.inputChars[i])) {
					Kind k = getOperator(stream.inputChars[i]);
					if (k == ASSIGN) {
						if (lookup(i + 1) == '=') {
							Token t = stream.new Token(EQUAL, i, i + 2,
									linecount);
							stream.tokens.add(t);
							i++;
							beg = beg + 1;
							continue;
						}
					} else if (k == NOT) {
						if (lookup(i + 1) == '=') {
							Token t = stream.new Token(NOTEQUAL, i, i + 2,
									linecount);
							stream.tokens.add(t);
							i++;
							beg = beg + 1;
							continue;
						}
					} else if (k == LT) {
						if (lookup(i + 1) == '=') {
							Token t = stream.new Token(LE, i, i + 2, linecount);
							stream.tokens.add(t);
							i++;
							beg = beg + 1;
							continue;
						} else if (lookup(i + 1) == '<') {
							Token t = stream.new Token(LSHIFT, i, i + 2,
									linecount);
							stream.tokens.add(t);
							i++;
							beg = beg + 1;
							continue;
						}
					} else if (k == GT) {
						if (lookup(i + 1) == '=') {
							Token t = stream.new Token(GE, i, i + 2, linecount);
							stream.tokens.add(t);
							i++;
							beg = beg + 1;
							continue;
						} else if (lookup(i + 1) == '>') {
							Token t = stream.new Token(RSHIFT, i, i + 2,
									linecount);
							stream.tokens.add(t);
							i++;
							beg = beg + 1;
							continue;
						}
					} else if (k == MINUS) {
						if (lookup(i + 1) == '>') {
							Token t = stream.new Token(ARROW, i, i + 2,
									linecount);
							stream.tokens.add(t);
							i++;
							beg = beg + 1;
							continue;
						}
					} else if (k == ILLEGAL_CHAR) {
						System.out.println("There is a bug. Fix it!");
						System.out.println(Thread.currentThread()
								.getStackTrace());
						System.exit(1);
					}
					Token t = stream.new Token(k, i, i + 1, linecount);
					stream.tokens.add(t);
				} else if (lookup(i) == '"') {
					beg = i;
					string_beg = linecount;
					state = FSA_STATES.STRING_LIT;
				} else {
					Token t = stream.new Token(ILLEGAL_CHAR, i, i + 1,
							linecount);
					stream.tokens.add(t);
					beg++;
				}

				break;

			case COMMENT:
				if (i == stream.inputChars.length) {
					if (beg == 0) {
						Token t1 = stream.new Token(UNTERMINATED_COMMENT, beg,
								i, linecount);
						stream.tokens.add(t1);
					} else {
						Token t1 = stream.new Token(UNTERMINATED_COMMENT,
								beg - 2, i, linecount);
						stream.tokens.add(t1);
					}
					Token t2 = stream.new Token(EOF, i, i, linecount);
					stream.tokens.add(t2);
				} else if (lookup(i) == '*' && lookup(i + 1) == '/') {
					state = FSA_STATES.START;
					beg += 2;
					i++;
				} else if (Character.isWhitespace(stream.inputChars[i])) {
					if (lookup(i) == 10) {
						// If the input char is LF, increment the linecount
						linecount++;
					} else if (lookup(i) == 13) {
						// If the input char is a CR, look ahead and see if you
						// have a LF
						// If the next char is a LF, increment i by 1
						if (lookup(i + 1) == 10) {
							i++;
							beg++;
						}
						// Increment linecount
						linecount++;
					}
				} else {
					beg++;
				}
				break;
			case DIGIT:
				if (i == stream.inputChars.length) {
					Token t = stream.new Token(INT_LIT, beg, i, linecount);
					Token t2 = stream.new Token(EOF, i, i, linecount);
					stream.tokens.add(t);
					stream.tokens.add(t2);
				} else if (!Character.isDigit(lookup(i))) {
					Token t = stream.new Token(INT_LIT, beg, i, linecount);
					beg = i;
					stream.tokens.add(t);
					state = FSA_STATES.START;

					if (Character.isWhitespace(stream.inputChars[i])) {
						if (Character.isWhitespace(lookup(i))) {
							beg++;
							if (lookup(i) == 10) {
								// If the input char is LF, increment the
								// linecount
								linecount++;
							} else if (lookup(i) == 13) {
								// If the input char is a CR, look ahead and see
								// if you have a LF
								// If the next char is a LF, increment i by 1
								if (lookup(i + 1) == 10) {
									i++;
									beg++;
								}
								// Increment linecount
								linecount++;
							}
						}
					} else if (Character.isJavaIdentifierStart(lookup(i))) {
						state = FSA_STATES.IDENT;
					} else if (lookup(i) == '/') {
						beg++;
						if (stream.inputChars.length != i + 1
								&& lookup(i + 1) == '*') {
							state = FSA_STATES.COMMENT;
							i++;
							beg++;
						} else {
							Token t1 = stream.new Token(DIV, i, i + 1,
									linecount);
							stream.tokens.add(t1);
						}
					} else if (lookup(i) == '"') {
						beg = i;
						string_beg = linecount;
						state = FSA_STATES.STRING_LIT;
					} else if (isSeparator(stream.inputChars[i])) {
						Kind k = getSeparator(stream.inputChars[i]);
						if (k == ILLEGAL_CHAR) {
							System.out.println("There is a bug. Fix it!!");
							System.out.println(Thread.currentThread()
									.getStackTrace());
							System.exit(1);
						} else if (k == DOT) {
							if (lookup(i + 1) == '.') {
								Token t1 = stream.new Token(RANGE, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							} else {
								Token t1 = stream.new Token(k, i, i + 1,
										linecount);
								stream.tokens.add(t1);
							}
						} else {
							Token t1 = stream.new Token(k, i, i + 1, linecount);
							stream.tokens.add(t1);
						}
					} else if (isOperator(stream.inputChars[i])) {
						Kind k = getOperator(stream.inputChars[i]);
						if (k == ASSIGN) {
							if (lookup(i + 1) == '=') {
								Token t1 = stream.new Token(EQUAL, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == NOT) {
							if (lookup(i + 1) == '=') {
								Token t1 = stream.new Token(NOTEQUAL, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == LT) {
							if (lookup(i + 1) == '=') {
								Token t1 = stream.new Token(LE, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							} else if (lookup(i + 1) == '<') {
								Token t1 = stream.new Token(LSHIFT, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == GT) {
							if (lookup(i + 1) == '=') {
								Token t1 = stream.new Token(GE, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							} else if (lookup(i + 1) == '>') {
								Token t1 = stream.new Token(RSHIFT, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == MINUS) {
							if (lookup(i + 1) == '>') {
								Token t1 = stream.new Token(ARROW, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == ILLEGAL_CHAR) {
							System.out.println("There is a bug. Fix it!");
							System.out.println(Thread.currentThread()
									.getStackTrace());
							System.exit(1);
						}
						Token t1 = stream.new Token(k, i, i + 1, linecount);
						stream.tokens.add(t1);
					} else {
						Token t1 = stream.new Token(ILLEGAL_CHAR, i, i + 1,
								linecount);
						stream.tokens.add(t1);
						beg++;
					}
				}
				break;
			case IDENT:
				if (i == stream.inputChars.length) {
					Token t1 = stream.new Token(getKeywordLit(String.valueOf(
							stream.inputChars, beg, i - beg)), beg, i,
							linecount);
					Token t2 = stream.new Token(EOF, i, i, linecount);
					stream.tokens.add(t1);
					stream.tokens.add(t2);
				} else if (!Character.isJavaIdentifierPart(lookup(i))) {
					Token t = stream.new Token(getKeywordLit(String.valueOf(
							stream.inputChars, beg, i - beg)), beg, i,
							linecount);
					beg = i;
					stream.tokens.add(t);
					state = FSA_STATES.START;

					if (Character.isWhitespace(stream.inputChars[i])) {
						if (Character.isWhitespace(lookup(i))) {
							beg++;
							if (lookup(i) == 10) {
								// If the input char is LF, increment the
								// linecount
								linecount++;
							} else if (lookup(i) == 13) {
								// If the input char is a CR, look ahead and see
								// if you have a LF
								// If the next char is a LF, increment i by 1
								if (lookup(i + 1) == 10) {
									i++;
									beg++;
								}
								// Increment linecount
								linecount++;
							}
						}
					} else if (lookup(i) == '/') {
						beg++;
						if (stream.inputChars.length != i + 1
								&& lookup(i + 1) == '*') {
							state = FSA_STATES.COMMENT;
							i++;
							beg++;
						} else {
							Token t1 = stream.new Token(DIV, i, i + 1,
									linecount);
							stream.tokens.add(t1);
						}
					} else if (lookup(i) == '"') {
						beg = i;
						string_beg = linecount;
						state = FSA_STATES.STRING_LIT;
					} else if (isSeparator(stream.inputChars[i])) {
						Kind k = getSeparator(stream.inputChars[i]);
						if (k == ILLEGAL_CHAR) {
							System.out.println("There is a bug. Fix it!!");
							System.out.println(Thread.currentThread()
									.getStackTrace());
							System.exit(1);
						} else if (k == DOT) {
							if (lookup(i + 1) == '.') {
								Token t1 = stream.new Token(RANGE, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							} else {
								Token t1 = stream.new Token(k, i, i + 1,
										linecount);
								stream.tokens.add(t1);
							}
						} else {
							Token t1 = stream.new Token(k, i, i + 1, linecount);
							stream.tokens.add(t1);
						}
					} else if (isOperator(stream.inputChars[i])) {
						Kind k = getOperator(stream.inputChars[i]);
						if (k == ASSIGN) {
							if (lookup(i + 1) == '=') {
								Token t1 = stream.new Token(EQUAL, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == NOT) {
							if (lookup(i + 1) == '=') {
								Token t1 = stream.new Token(NOTEQUAL, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == LT) {
							if (lookup(i + 1) == '=') {
								Token t1 = stream.new Token(LE, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							} else if (lookup(i + 1) == '<') {
								Token t1 = stream.new Token(LSHIFT, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == GT) {
							if (lookup(i + 1) == '=') {
								Token t1 = stream.new Token(GE, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							} else if (lookup(i + 1) == '>') {
								Token t1 = stream.new Token(RSHIFT, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == MINUS) {
							if (lookup(i + 1) == '>') {
								Token t1 = stream.new Token(ARROW, i, i + 2,
										linecount);
								stream.tokens.add(t1);
								i++;
								beg = beg + 1;
								continue;
							}
						} else if (k == ILLEGAL_CHAR) {
							System.out.println("There is a bug. Fix it!");
							System.out.println(Thread.currentThread()
									.getStackTrace());
							System.exit(1);
						}
						Token t1 = stream.new Token(k, i, i + 1, linecount);
						stream.tokens.add(t1);
					} else {
						Token t1 = stream.new Token(ILLEGAL_CHAR, i, i + 1,
								linecount);
						stream.tokens.add(t1);
						beg++;
					}
				}
				break;
			case STRING_LIT:
				if (i == stream.inputChars.length) {
					Token t1 = stream.new Token(UNTERMINATED_STRING, beg, i,
							string_beg);
					Token t2 = stream.new Token(EOF, i, i, linecount);
					stream.tokens.add(t1);
					stream.tokens.add(t2);
				} else if (lookup(i) == '"' && lookup(i - 1) != '\\') {
					Token t1 = stream.new Token(STRING_LIT, beg, i + 1,
							string_beg);
					stream.tokens.add(t1);
					state = FSA_STATES.START;
				} else if (Character.isWhitespace(stream.inputChars[i])) {
					if (lookup(i) == 10) {
						// If the input char is LF, increment the linecount
						linecount++;
					} else if (lookup(i) == 13) {
						// If the input char is a CR, look ahead and see if you
						// have a LF
						// If the next char is a LF, increment i by 1
						if (lookup(i + 1) == 10) {
							i++;
						}
						// Increment linecount
						linecount++;
					}
				}
				break;
			default:
				System.out.println("There is a bug in the code. Fix it!!");
				System.out.println(Thread.currentThread().getStackTrace());
				System.exit(1);
				break;
			}
		}
	}

	private int lookup(int i) {
		if (i >= stream.inputChars.length) {
			return -1;
		}
		return stream.inputChars[i];
	}

	private Kind getKeywordLit(String lit) {
		switch (lit) {
		case "int":
			return KW_INT;
		case "string":
			return KW_STRING;
		case "boolean":
			return KW_BOOLEAN;
		case "import":
			return KW_IMPORT;
		case "class":
			return KW_CLASS;
		case "def":
			return KW_DEF;
		case "while":
			return KW_WHILE;
		case "if":
			return KW_IF;
		case "else":
			return KW_ELSE;
		case "return":
			return KW_RETURN;
		case "print":
			return KW_PRINT;
		case "true":
			return BL_TRUE;
		case "false":
			return BL_FALSE;
		case "null":
			return NL_NULL;
		default:
			return IDENT;
		}
	}

	private boolean isSeparator(char ch) {
		switch (ch) {
		case '.':
		case ';':
		case ',':
		case '(':
		case ')':
		case '[':
		case ']':
		case '{':
		case '}':
		case ':':
		case '?':
			return true;
		default:
			return false;
		}
	}

	private Kind getSeparator(char ch) {
		switch (ch) {
		case '.':
			return DOT;
		case ';':
			return SEMICOLON;
		case ',':
			return COMMA;
		case '(':
			return LPAREN;
		case ')':
			return RPAREN;
		case '[':
			return LSQUARE;
		case ']':
			return RSQUARE;
		case '{':
			return LCURLY;
		case '}':
			return RCURLY;
		case ':':
			return COLON;
		case '?':
			return QUESTION;
		default:
			return ILLEGAL_CHAR;
		}
	}

	private boolean isOperator(char ch) {
		switch (ch) {
		case '=':
		case '|':
		case '&':
		case '!':
		case '<':
		case '>':
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '@':
			return true;
		default:
			return false;
		}
	}

	private Kind getOperator(char ch) {
		switch (ch) {
		case '=':
			return ASSIGN;
		case '|':
			return BAR;
		case '&':
			return AND;
		case '!':
			return NOT;
		case '<':
			return LT;
		case '>':
			return GT;
		case '+':
			return PLUS;
		case '-':
			return MINUS;
		case '*':
			return TIMES;
		case '/':
			return DIV;
		case '%':
			return MOD;
		case '@':
			return AT;
		default:
			return ILLEGAL_CHAR;
		}
	}
}