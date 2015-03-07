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

import java.util.ArrayList;
import java.util.List;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.*;

public class Parser {

	List<SyntaxException> exceptionList = new ArrayList<SyntaxException>();
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

	Parser(TokenStream tokens) {
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

	public Program parse() throws SyntaxException {
		Program p = null;
		try {
			p = Program();
			if (p != null)
				match(EOF);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty())
			return p;
		else
			return null;
	}

	List<SyntaxException> getExceptionList() {
		return null;
	}
	private Program Program() throws SyntaxException {
		List<QualifiedName> imp = new ArrayList<QualifiedName>();
		Token programStart = t;
		imp = ImportList();
		match(KW_CLASS);
		String classname = t.getText();
		match(IDENT);
		Block b = Block();
		Program p = new Program(programStart,imp,classname,b);
		return p;
	}

	private List<QualifiedName> ImportList() throws SyntaxException {
		// TODO Fill this in
		List<QualifiedName> implist = new ArrayList<QualifiedName>();
		Token start = t;
		while (!isKind(KW_CLASS)) {
			String qnbuilder = new String();
			match(KW_IMPORT);
			qnbuilder+=t.getText();
			match(IDENT);
			while (!isKind(SEMICOLON)) {
				qnbuilder+="/";
				match(DOT);
				qnbuilder+=t.getText();
				match(IDENT);
			}
			match(SEMICOLON);
			QualifiedName imp = new QualifiedName(start, qnbuilder);
			implist.add(imp);
			qnbuilder="";
		}
		
		return implist;

	}

	private Block Block() throws SyntaxException {
		Token blockStart = t;
		match(LCURLY);
		if (isKind(RCURLY)) {
			match(RCURLY);
			return new Block(blockStart, null);
		}
		List<BlockElem> elems = new ArrayList<BlockElem>();
		while (isKind(KW_DEF) || isKind(PREDICT_STATEMENT)) {
			BlockElem e ;
			if (isKind(KW_DEF)) {
				e = Declaration();
				elems.add(e);
				if (isKind(RCURLY)) {
					throw new SyntaxException(t,
							"expected one of  STATEMENT or DEF");
				} else if (isKind(SEMICOLON)) {
					match(SEMICOLON);
				}
			} else if (isKind(PREDICT_STATEMENT)) {
				e =	statement();
				elems.add(e);
				if (isKind(SEMICOLON)) {
					match(SEMICOLON);
				}
			} else {
				throw new SyntaxException(t,
						"expected one of  STATEMENT or DEF");
			}
		}
		match(RCURLY);
		Block b = new Block(blockStart,elems);
		return b;
	}

	private Declaration Declaration() throws SyntaxException {
		Token decStart = t;

		match(KW_DEF);
		if (isKind(IDENT)) {
			Token identStart = t;
			match(IDENT);
			if (isKind(COLON) || isKind(SEMICOLON)) {
				Declaration d =	varDec(decStart,identStart);
				return d;
			} else if (isKind(ASSIGN)) {
				Declaration d = closureDec(decStart, identStart);
				return d;
			} else {
				throw new SyntaxException(t, "expected one of  " + COLON + "or"
						+ ASSIGN);
			}
		} else {
			throw new SyntaxException(t, "expected " + IDENT);
		}
	}

	private VarDec varDec(Token decStart, Token identStart) throws SyntaxException {
		Type ty = null;
		if (isKind(IDENT)) {
			match(IDENT);
			ty = new UndeclaredType(decStart);
		} else if (isKind(COLON)) {
			match(COLON);
			ty = Type();
		} else {
			throw new SyntaxException(t, "expected one of  " + COLON + "or"
					+ SEMICOLON);
		}
		VarDec v = new VarDec(decStart, identStart,ty);
		return v;
	}

	private Type Type() throws SyntaxException {
		Token typeStart = t;
		Type ty;
		if (isKind(KW_INT)) {
			match(KW_INT);
			ty = new cop5555sp15.ast.SimpleType(typeStart, typeStart);
		} else if (isKind(KW_STRING)) {
			match(KW_STRING);
			ty = new cop5555sp15.ast.SimpleType(typeStart, typeStart);
		} else if (isKind(KW_BOOLEAN)) {
			match(KW_BOOLEAN);
			ty = new cop5555sp15.ast.SimpleType(typeStart, typeStart);
		} else if (isKind(AT)) {
			match(AT);
			if (isKind(AT)) {
				ty = keyValueType();
			} else {
				ty = listType();
			}
		} else {
			throw new SyntaxException(t,
					"expected one of  SimpleType, List or KeyValue");
		}
		return ty;
	}

	private SimpleType SimpleType(Token typeStart) throws SyntaxException {
		SimpleType sty;
		if (isKind(KW_INT)) {
			sty = new cop5555sp15.ast.SimpleType(typeStart, typeStart);
			consume();
		} else if (isKind(KW_STRING)) {
			consume();
			sty = new cop5555sp15.ast.SimpleType(typeStart, typeStart);
		} else if (isKind(KW_BOOLEAN)) {
			consume();
			sty = new cop5555sp15.ast.SimpleType(typeStart, typeStart);
		} else {
			throw new SyntaxException(t, "expected one of  " + KW_INT + "or"
					+ "or" + KW_BOOLEAN + "or" + KW_STRING);
		}
		return sty;
	}

	private KeyValueType keyValueType() throws SyntaxException {
		Token kvStart = t;
		match(AT);
		match(LSQUARE);
		SimpleType kTy = SimpleType(kvStart);
		match(COLON);
		Type vTy = Type();
		match(RSQUARE);
		KeyValueType kvTy = new KeyValueType(kvStart, kTy, vTy);
		return kvTy;
	}

	private ListType listType() throws SyntaxException {
		Token listStart = t;
		match(LSQUARE);
		Type Ty = Type();
		match(RSQUARE);
		ListType lTy = new ListType(listStart, Ty);
		return lTy;
	}

	private ClosureDec closureDec(Token decStart, Token identStart) throws SyntaxException {
		match(ASSIGN);
		Closure cl = Closure(identStart);
		ClosureDec clDec = new ClosureDec(decStart, identStart, cl);
		return clDec;
	}

	private Closure Closure(Token identStart) throws SyntaxException {
		Token closureStart = t;
		match(LCURLY);
		List<VarDec> formalarg;
		formalarg = formalArgList(closureStart, identStart);
		match(ARROW);
		List<Statement> statement = new ArrayList<Statement>();
		while (isKind(PREDICT_STATEMENT)) {
			statement.add(statement());
			if (isKind(SEMICOLON)) {
				match(SEMICOLON);
			}
		}
		match(RCURLY);
		Closure cl = new Closure(closureStart, formalarg, statement);
		return cl;
	}

	private List<VarDec> formalArgList(Token decStart, Token identStart) throws SyntaxException {
		List<VarDec> lvDec = new ArrayList<VarDec>();
		while (!isKind(ARROW)) {
			lvDec.add(varDec(decStart,identStart));
			while (isKind(COMMA)) {
				match(COMMA);
				lvDec.add(varDec(decStart, identStart));
			}
		}
		return lvDec;
	}

	private Statement statement() throws SyntaxException {
		if (isKind(IDENT)) {
			Token firstToken = t;
			LValue lv = LVALUE();
			match(ASSIGN);
			Expression e = Expression();
			Statement lvs = new AssignmentStatement(firstToken, lv, e);
			return lvs;
		} else if (isKind(KW_PRINT)) {
			Token printStart = t;
			match(KW_PRINT);
			Expression e = Expression();
			Statement ps = new PrintStatement(printStart, e);
			return ps;
		} else if (isKind(KW_WHILE)) {
			Token whileStart = t;
			match(KW_WHILE);
			if (isKind(TIMES)) {
				
				match(TIMES);
				match(LPAREN);
				Expression e = Expression();
				if (isKind(RANGE)) {
					RangeExpression re = rangeExpression();
					match(RPAREN);
					Block b = Block();
					Statement wrS = new WhileRangeStatement(whileStart, re, b);
					return wrS;
				}
				match(RPAREN);
				Block b = Block();
				Statement wsS = new WhileStarStatement(whileStart, e, b);
				return wsS;
				
			} else {
				match(LPAREN);
				Expression e = Expression();
				match(RPAREN);
				Block b = Block();
				Statement wS = new WhileStatement(whileStart, e, b);
				return wS;
			}
		} else if (isKind(KW_IF)) {
			Token ifStart = t;
			match(KW_IF);
			match(LPAREN);
			Expression e = Expression();
			match(RPAREN);
			Block b = Block();
			if (isKind(KW_ELSE)) {
				match(KW_ELSE);
				Block eb = Block();
				Statement ifeS = new IfElseStatement(ifStart, e, b, eb);
				return ifeS;
			}
			Statement ifS = new IfStatement(ifStart, e, b);
			return ifS;
		} else if (isKind(MOD)) {
			Token exprStStart = t;
			match(MOD);
			Expression e = Expression();
			Statement exprSt = new ExpressionStatement(exprStStart, e);
			return exprSt;
		} else if (isKind(KW_RETURN)) {
			Token retStart = t;
			match(KW_RETURN);
			Expression e = Expression();
			Statement retS = new ReturnStatement(retStart, e);
			return retS;
		}
		return null;
	}

	private void closureEvalExpression() throws SyntaxException {
		match(LPAREN);
		expressionList();
		match(RPAREN);
	}

	private LValue LVALUE() throws SyntaxException {
		match(IDENT);
		if (isKind(LSQUARE)) {
			match(LSQUARE);
			Expression();
			match(RSQUARE);
		}
		return null;
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

	private RangeExpression rangeExpression() throws SyntaxException {
		match(RANGE);
		Expression();
		return null;
	}

	private Expression Expression() throws SyntaxException {
		term();
		while (isKind(REL_OPS)) {
			match(REL_OPS);
			term();
		}
		return null;
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
			Closure(t);// This is WRONG, fix it later
		} else {
			throw new SyntaxException(t, "expected factor");
		}
	}

}
