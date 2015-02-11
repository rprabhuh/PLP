package cop5555sp15;

import static cop5555sp15.TokenStream.Kind.AND;
import static cop5555sp15.TokenStream.Kind.ARROW;
import static cop5555sp15.TokenStream.Kind.ASSIGN;
import static cop5555sp15.TokenStream.Kind.AT;
import static cop5555sp15.TokenStream.Kind.BAR;
import static cop5555sp15.TokenStream.Kind.BL_FALSE;
import static cop5555sp15.TokenStream.Kind.BL_TRUE;
import static cop5555sp15.TokenStream.Kind.COLON;
import static cop5555sp15.TokenStream.Kind.COMMA;
import static cop5555sp15.TokenStream.Kind.DIV;
import static cop5555sp15.TokenStream.Kind.DOT;
import static cop5555sp15.TokenStream.Kind.EOF;
import static cop5555sp15.TokenStream.Kind.EQUAL;
import static cop5555sp15.TokenStream.Kind.GE;
import static cop5555sp15.TokenStream.Kind.GT;
import static cop5555sp15.TokenStream.Kind.IDENT;
import static cop5555sp15.TokenStream.Kind.INT_LIT;
import static cop5555sp15.TokenStream.Kind.KW_BOOLEAN;
import static cop5555sp15.TokenStream.Kind.KW_CLASS;
import static cop5555sp15.TokenStream.Kind.KW_DEF;
import static cop5555sp15.TokenStream.Kind.KW_ELSE;
import static cop5555sp15.TokenStream.Kind.KW_IF;
import static cop5555sp15.TokenStream.Kind.KW_IMPORT;
import static cop5555sp15.TokenStream.Kind.KW_INT;
import static cop5555sp15.TokenStream.Kind.KW_PRINT;
import static cop5555sp15.TokenStream.Kind.KW_RETURN;
import static cop5555sp15.TokenStream.Kind.KW_STRING;
import static cop5555sp15.TokenStream.Kind.KW_WHILE;
import static cop5555sp15.TokenStream.Kind.LCURLY;
import static cop5555sp15.TokenStream.Kind.LE;
import static cop5555sp15.TokenStream.Kind.LPAREN;
import static cop5555sp15.TokenStream.Kind.LSHIFT;
import static cop5555sp15.TokenStream.Kind.LSQUARE;
import static cop5555sp15.TokenStream.Kind.LT;
import static cop5555sp15.TokenStream.Kind.MINUS;
import static cop5555sp15.TokenStream.Kind.MOD;
import static cop5555sp15.TokenStream.Kind.NOT;
import static cop5555sp15.TokenStream.Kind.NOTEQUAL;
import static cop5555sp15.TokenStream.Kind.PLUS;
import static cop5555sp15.TokenStream.Kind.RANGE;
import static cop5555sp15.TokenStream.Kind.RCURLY;
import static cop5555sp15.TokenStream.Kind.RPAREN;
import static cop5555sp15.TokenStream.Kind.RSHIFT;
import static cop5555sp15.TokenStream.Kind.RSQUARE;
import static cop5555sp15.TokenStream.Kind.SEMICOLON;
import static cop5555sp15.TokenStream.Kind.STRING_LIT;
import static cop5555sp15.TokenStream.Kind.TIMES;
import static cop5555sp15.TokenStream.Kind.KW_SIZE;
import static cop5555sp15.TokenStream.Kind.KW_KEY;
import static cop5555sp15.TokenStream.Kind.KW_VALUE;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;

public class SimpleParser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;
		Kind[] expected;
		String msg;

		SyntaxException(Token t, Kind expected) {
			this.t = t;
			msg = "";
			this.expected = new Kind[1];
			this.expected[0] = expected;

		}

		public SyntaxException(Token t, String msg) {
			this.t = t;
			this.msg = msg;
		}

		public SyntaxException(Token t, Kind[] expected) {
			this.t = t;
			msg = "";
			this.expected = expected;
		}

		public String getMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append(" error at token ").append(t.toString()).append(" ")
					.append(msg);
			sb.append(". Expected: ");
			for (Kind kind : expected) {
				sb.append(kind).append(" ");
			}
			return sb.toString();
		}
	}

	TokenStream tokens;
	Token t;

	SimpleParser(TokenStream tokens) {
		this.tokens = tokens;
		t = tokens.nextToken();

	}

	private Kind match(Kind kind) throws SyntaxException {
		if (isKind(kind)) {
			consume();
			return kind;
		}
		throw new SyntaxException(t, kind);
	}

	private Kind match(Kind... kinds) throws SyntaxException {
		Kind kind = t.kind;
		if (isKind(kinds)) {
			consume();
			return kind;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(kind1).append(" ");
		}
		throw new SyntaxException(t, "expected one of " + sb.toString());
	}

	private boolean isKind(Kind kind) {
		return (t.kind == kind);
	}

	private void consume() {
		if (t.kind != EOF)
			t = tokens.nextToken();
	}

	private boolean isKind(Kind... kinds) {
		for (Kind kind : kinds) {
			if (t.kind == kind)
				return true;
		}
		return false;
	}

	// This is a convenient way to represent fixed sets of
	// token kinds. You can pass these to isKind.
	static final Kind[] REL_OPS = { BAR, AND, EQUAL, NOTEQUAL, LT, GT, LE, GE };
	static final Kind[] WEAK_OPS = { PLUS, MINUS };
	static final Kind[] STRONG_OPS = { TIMES, DIV };
	static final Kind[] VERY_STRONG_OPS = { LSHIFT, RSHIFT };
	static final Kind[] PREDICT_STATEMENT = { IDENT, KW_PRINT, KW_WHILE, KW_IF,
			MOD, KW_RETURN, SEMICOLON };

	public void parse() throws SyntaxException {
		Program();
		match(EOF);
	}

	private void Program() throws SyntaxException {
		ImportList();
		match(KW_CLASS);
		match(IDENT);
		Block();
	}

	private void ImportList() throws SyntaxException {
		// TODO Fill this in
		while (!isKind(KW_CLASS)) {
			match(KW_IMPORT);
			match(IDENT);
			while (!isKind(SEMICOLON)) {
				match(DOT);
				match(IDENT);
			}
			match(SEMICOLON);
		}

	}

	private void Block() throws SyntaxException {
		match(LCURLY);
		if (isKind(RCURLY)) {
			match(RCURLY);
			return;
		}
		while (isKind(KW_DEF) || isKind(PREDICT_STATEMENT)) {
			if (isKind(KW_DEF)) {
				Declaration();
				match(SEMICOLON);
			} else if (isKind(PREDICT_STATEMENT)) {
				statement();
				if(isKind(SEMICOLON)) {
					match(SEMICOLON);
				}
			} else {
				throw new SyntaxException(t,
						"expected one of  STATEMENT or DEF");
			}
		}

		match(RCURLY);
	}

	private void Declaration() throws SyntaxException {
		match(KW_DEF);
		if (isKind(IDENT)) {
			match(IDENT);
			if (isKind(COLON) || isKind(SEMICOLON)) {
				varDec();
			} else if (isKind(ASSIGN)) {
				closureDec();
			} else {
				throw new SyntaxException(t, "expected one of  " + COLON + "or"
						+ ASSIGN);
			}
		} else {
			throw new SyntaxException(t, "expected " + IDENT);
		}
	}

	private void varDec() throws SyntaxException {
		if (isKind(SEMICOLON)) {
			match(SEMICOLON);
		} else if (isKind(COLON)) {
			match(COLON);
			Type();
		} else {
			throw new SyntaxException(t, "expected one of  " + COLON + "or"
					+ SEMICOLON);
		}
	}

	private void Type() throws SyntaxException {
		if (isKind(KW_INT)) {
			match(KW_INT);
		} else if (isKind(KW_STRING)) {
			match(KW_STRING);
		} else if (isKind(KW_BOOLEAN)) {
			match(KW_BOOLEAN);
		} else if (isKind(AT)) {
			match(AT);
			if (isKind(AT)) {
				keyValueType();
			} else {
				listType();
			}
		} else {
			throw new SyntaxException(t,
					"expected one of  SimpleType, List or KeyValue");
		}
	}

	private void SimpleType() throws SyntaxException {
		if (isKind(KW_INT)) {
			consume();
		} else if (isKind(KW_STRING)) {
			consume();
		} else if (isKind(KW_BOOLEAN)) {
			consume();
		} else {
			throw new SyntaxException(t, "expected one of  " + KW_INT + "or"
					+ "or" + KW_BOOLEAN + "or" + KW_STRING);
		}
	}

	private void keyValueType() throws SyntaxException {
		match(AT);
		match(LSQUARE);
		SimpleType();
		match(COLON);
		Type();
		match(RSQUARE);
	}

	private void listType() throws SyntaxException {
		match(LSQUARE);
		Type();
		match(RSQUARE);
	}

	private void closureDec() throws SyntaxException {
		match(ASSIGN);
		Closure();
	}

	private void Closure() throws SyntaxException {
		match(LCURLY);
		formalArgList();
		match(ARROW);
		while (isKind(PREDICT_STATEMENT)) {
			statement();
			if(isKind(SEMICOLON)) {
				match(SEMICOLON);
			}
		}
		match(RCURLY);
	}

	private void formalArgList() throws SyntaxException {
		while (!isKind(ARROW)) {
			varDec();
			while (isKind(COMMA)) {
				match(COMMA);
				varDec();
			}
		}
	}

	private void statement() throws SyntaxException {
		if (isKind(IDENT)) {
			LVALUE();
			match(ASSIGN);
			Expression();
		} else if (isKind(KW_PRINT)) {
			match(KW_PRINT);
			Expression();
		} else if (isKind(KW_WHILE)) {
			match(KW_WHILE);
			if (isKind(TIMES)) {
				match(TIMES);
				match(LPAREN);
				Expression();
				if (isKind(RANGE)) {
					rangeExpression();
				}
				match(RPAREN);
				Block();
			} else {
				match(LPAREN);
				Expression();
				match(RPAREN);
				Block();
			}
		} else if (isKind(KW_IF)) {
			match(KW_IF);
			match(LPAREN);
			Expression();
			match(RPAREN);
			Block();
			if (isKind(KW_ELSE)) {
				match(KW_ELSE);
				Block();
			}
		} else if (isKind(MOD)) {
			match(MOD);
			Expression();
		} else if (isKind(KW_RETURN)) {
			match(KW_RETURN);
			Expression();
		}

	}

	private void closureEvalExpression() throws SyntaxException {
		// match(IDENT);
		match(LPAREN);
		expressionList();
		match(RPAREN);
	}

	private void LVALUE() throws SyntaxException {
		match(IDENT);
		if (isKind(LSQUARE)) {
			match(LSQUARE);
			Expression();
			match(RSQUARE);
		}
	}

	private void list() throws SyntaxException {
		match(LSQUARE);
		expressionList();
		match(RSQUARE);
	}

	private void expressionList() throws SyntaxException {
		Expression();
		while (isKind(COMMA)) {
			match(COMMA);
			Expression();
		}
	}

	private void keyValueExpression() throws SyntaxException {
		Expression();
		match(COLON);
		Expression();
	}

	private void keyValueList() throws SyntaxException {
		keyValueExpression();
		while (isKind(COMMA)) {
			match(COMMA);
			keyValueExpression();
		}
	}

	private void mapList() throws SyntaxException {
		match(AT);
		match(LSQUARE);
		keyValueList();
		match(RSQUARE);
	}

	private void rangeExpression() throws SyntaxException {
		//Expression();
		match(RANGE);
		Expression();
	}

	private void Expression() throws SyntaxException {
		term();
		while (isKind(REL_OPS)) {
			match(REL_OPS);
			term();
		}
	}

	private void term() throws SyntaxException {
		elem();
		while (isKind(WEAK_OPS)) {
			match(WEAK_OPS);
			elem();
		}
	}

	private void elem() throws SyntaxException {
		thing();
		while (isKind(STRONG_OPS)) {
			match(STRONG_OPS);
			thing();
		}
	}

	private void thing() throws SyntaxException {
		factor();
		while (isKind(VERY_STRONG_OPS)) {
			match(VERY_STRONG_OPS);
			factor();
		}
	}

	private void factor() throws SyntaxException {
		if (isKind(IDENT)) {
			match(IDENT);
			if (isKind(LSQUARE)) {
				match(LSQUARE);
				Expression();
				match(RSQUARE);
			} else if (isKind(LPAREN)) {
				closureEvalExpression();
			}
		} else if (isKind(INT_LIT)) {
			match(INT_LIT);
		} else if (isKind(BL_TRUE)) {
			match(BL_TRUE);
		} else if (isKind(BL_FALSE)) {
			match(BL_FALSE);
		} else if (isKind(STRING_LIT)) {
			match(STRING_LIT);
		} else if (isKind(LPAREN)) {
			match(LPAREN);
			Expression();
			match(RPAREN);
		} else if (isKind(NOT)) {
			match(NOT);
			factor();
		} else if (isKind(MINUS)) {
			match(MINUS);
			factor();
		} else if (isKind(KW_SIZE)) {
			match(KW_SIZE);
			Expression();
		} else if (isKind(KW_KEY)) {
			match(KW_KEY);
			match(LPAREN);
			Expression();
			match(RPAREN);
		} else if (isKind(KW_VALUE)) {
			match(KW_VALUE);
			match(LPAREN);
			Expression();
			match(RPAREN);
		} else if (isKind(AT)) {
			match(AT);
			if (isKind(AT)) {
				mapList();
			} else if (isKind(LSQUARE)) {
				list();
			} else {
				throw new SyntaxException(t, "expected one of  " + AT + "or"
						+ LSQUARE);
			}
		} else if (isKind(LCURLY)) {
			Closure();
		} else {
			throw new SyntaxException(t, "expected factor");
		}
	}

}
