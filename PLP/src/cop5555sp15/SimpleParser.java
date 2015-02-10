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
		if (isKind(KW_DEF)) {
			Declaration();
		} else {
			throw new SyntaxException(t, "expected one of " + IDENT + "or"
					+ KW_DEF);
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
			if (isKind(LSQUARE)) {
				match(LSQUARE);
				Type();
				match(RSQUARE);
			} else if (isKind(AT)) {
				match(LSQUARE);
				SimpleType();
				match(COLON);
				Type();
				match(RSQUARE);
			} else {
				throw new SyntaxException(t, "expected one of  " + LSQUARE
						+ "or" + AT);
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

	private void keyValueType() throws SyntaxException{
		
	}
	
	private void listType() throws SyntaxException {
		
	}
	
	private void closureDec() throws SyntaxException {
		match(ASSIGN);
		Closure();
	}

	private void Closure() throws SyntaxException {
		match(LCURLY);
		formalArgList();
		match(ARROW);
		statement();
		match(RCURLY);
	}

	private void formalArgList() throws SyntaxException {
		while (!isKind(ARROW)) {
			varDec();
			while (isKind(COMMA)) {
				match(COMMA);
				varDec();
			}
		}// TODO Auto-generated method stub
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
					match(RANGE);
					Expression();
				}
				match(RPAREN);
			} else {
				match(LPAREN);
				Expression();
				match(RPAREN);
				Block();
			}
		} else if (isKind(KW_IF)) {
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
		match(IDENT);
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
		match(AT);
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
		while(isKind(COMMA)){
			match(COMMA);
			keyValueExpression();
		}
	}
	
	private void mapList() throws SyntaxException {
		match(AT);
		match(AT);
		match(LSQUARE);
		keyValueList();
		match(RSQUARE);
	}

	private void rangeExpression() throws SyntaxException {
		Expression();
		match(RANGE);
		Expression();
	}
	
	private void Expression() throws SyntaxException {

	}

	private void Term() throws SyntaxException {
		
	}
	
	private void Elem() throws SyntaxException {
		
	}
	
	private void thing() throws SyntaxException {
		
	}
	
	private void factor() throws SyntaxException {
		
	}
	
	private void relOp() {
		
	}
	
	private void weakOP() throws SyntaxException {
		
	}
	
	private void strongOp() throws SyntaxException {
		
	}
	
	private void veryStrongOp() throws SyntaxException {
		
	}
}
