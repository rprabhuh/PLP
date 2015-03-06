package cop5555sp15;

import static org.junit.Assert.*;

import org.junit.Test;

import cop5555sp15.SimpleParser.SyntaxException;
import cop5555sp15.TokenStream.Kind;
import static cop5555sp15.TokenStream.Kind.*;

public class TestSimpleParser {

	private void parseIncorrectInput(String input,
			Kind ExpectedIncorrectTokenKind) {
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		SimpleParser parser = new SimpleParser(stream);
		System.out.println(stream);
		try {
			parser.parse();
			fail("expected syntax error");
		} catch (SyntaxException e) {
			assertEquals(ExpectedIncorrectTokenKind, e.t.kind); // class is the
																// incorrect
																// token
		}
	}

	private void parseCorrectInput(String input) throws SyntaxException {
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
	}

	/**
	 * This is an example of testing correct input Just call parseCorrectInput
	 * 
	 * @throws SyntaxException
	 */
	@Test
	public void almostEmpty() throws SyntaxException {
		System.out.println("almostEmpty");
		String input = "class A { } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	/**
	 * This is an example of testing incorrect input. The second parameter to
	 * parseIncorrectInput is the Kind of the erroneous token. For example, in
	 * this test, the ] should be a }, so the parameter is RSQUARE
	 * 
	 * @throws SyntaxException
	 */
	@Test
	public void almostEmptyIncorrect() throws SyntaxException {
		System.out.println("almostEmpty");
		String input = "class A { ] ";
		System.out.println(input);
		parseIncorrectInput(input, RSQUARE);
	}

	@Test
	public void import1() throws SyntaxException {
		System.out.println("import1");
		String input = "import X; class A { } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void import2() throws SyntaxException {
		System.out.println("import2");
		String input = "import X.Y.Z; import W.X.Y; class A { } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void import3() throws SyntaxException {
		System.out.println("import2");
		String input = "import class A { } "; // this input is wrong.
		System.out.println(input);
		Kind ExpectedIncorrectTokenKind = KW_CLASS;
		parseIncorrectInput(input, ExpectedIncorrectTokenKind);
	}

	@Test
	public void def_simple_type1() throws SyntaxException {
		System.out.println("def_simple_type1");
		String input = "class A {def B:int; def C:boolean; def S: string;} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void def_simple_type2() throws SyntaxException {
		System.out.println("def_simple_type2");
		String input = "class A {def B:int; def C:boolean; def S: string} ";
		System.out.println(input);
		parseIncorrectInput(input, RCURLY);
	}

	@Test
	public void def_key_value_type1() throws SyntaxException {
		System.out.println("def_key_value_type1");
		String input = "class A {def C:@[string]; def S:@@[int:boolean];} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void def_key_value_type2() throws SyntaxException {
		System.out.println("def_key_value_type1");
		String input = "class A {def C:@[string]; def S:@@[int:@@[string:boolean]];} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void def_closure1() throws SyntaxException {
		System.out.println("def_closure1");
		String input = "class A {def C={->};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void def_closure2() throws SyntaxException {
		System.out.println("def_closure2");
		String input = "class A {def C={->};  def z:string;} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void factor1() throws SyntaxException {
		System.out.println("factor1");
		String input = "class A {def C={->x=y;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void factor2() throws SyntaxException {
		System.out.println("factor2");
		String input = "class A {def C={->x=y[z];};  def D={->x=y[1];};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void factor3() throws SyntaxException {
		System.out.println("factor3");
		String input = "class A {def C={->x=3;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void factor4() throws SyntaxException {
		System.out.println("factor4");
		String input = "class A {def C={->x=\"hello\";};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void factor5() throws SyntaxException {
		System.out.println("factor5");
		String input = "class A {def C={->x=true; z = false;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void factor6() throws SyntaxException {
		System.out.println("factor6");
		String input = "class A {def C={->x=-y; z = !y;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void factor7() throws SyntaxException {
		System.out.println("factor7");
		String input = "class A {def C={->x= &y; z = !y;};} ";
		System.out.println(input);
		parseIncorrectInput(input, AND);
	}

	@Test
	public void expressions1() throws SyntaxException {
		System.out.println("expressions1");
		String input = "class A {def C={->x=x+1; z = 3-4-5;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void expressions2() throws SyntaxException {
		System.out.println("expressions2");
		String input = "class A {def C={->x=x+1/2*3--4; z = 3-4-5;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void expressions3() throws SyntaxException {
		System.out.println("expressions3");
		String input = "class A {def C={->x=x+1/2*3--4; z = 3-4-5;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void expressions4() throws SyntaxException {
		System.out.println("expressions4");
		String input = "class A {x = a<<b; c = b>>z;} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void statements1() throws SyntaxException {
		System.out.println("statements1");
		String input = "class A {x = y; z[1] = b; print a+b; print (x+y-z);} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void statements2() throws SyntaxException {
		System.out.println("statements2");
		String input = "class A  {\n while (x) {};  \n while* (1..4){}; } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void statements3() throws SyntaxException {
		System.out.println("statements3");
		String input = "class A  {\n if (x) {};  \n if (y){} else {}; \n if (x) {} else {if (z) {} else {};} ; } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void emptyStatement() throws SyntaxException {
		System.out.println("emptyStatement");
		String input = "class A  { ;;; } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void statements4() throws SyntaxException {
		System.out.println("statements4");
		String input = "class A  { %a(1,2,3); } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void statements5() throws SyntaxException {
		System.out.println("statements5");
		String input = "class A  { x = a(1,2,3); } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void closureEval() throws SyntaxException {
		System.out.println("closureEva");
		String input = "class A  { x[z] = a(1,2,3); } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void list1() throws SyntaxException {
		System.out.println("list1");
		String input = "class A  { \n x = @[a,b,c]; \n y = @[d,e,f]+x; \n } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void maplist1() throws SyntaxException {
		System.out.println("maplist1");
		String input = "class A  { x = @@[x:y]; y = @@[x:y,4:5]; } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void NoClass() throws SyntaxException {
		System.out.println("No Class");
		String input = "import A { ] ";
		System.out.println(input);
		parseIncorrectInput(input, Kind.LCURLY);
	}

	@Test
	public void MissingSemi() throws SyntaxException {
		System.out.println("Missing Semi");
		String input = "import A class { } ";
		System.out.println(input);
		parseIncorrectInput(input, Kind.KW_CLASS);
	}

	@Test
	public void DoubleDot() throws SyntaxException {
		System.out.println("Double Dot");
		String input = "import A..B; class { } ";
		System.out.println(input);
		parseIncorrectInput(input, Kind.RANGE);
	}

	@Test
	public void VardecI() throws SyntaxException {
		System.out.println("VarDecIncorrect");
		String input = "import A.B; class A { def Hi:;} ";
		System.out.println(input);
		parseIncorrectInput(input, Kind.SEMICOLON);
	}

	@Test
	public void ClosureDecI() throws SyntaxException {
		System.out.println("ClosureDecI");
		String input = "import A.B; class A { def Hi:int=3;} ";
		System.out.println(input);
		parseIncorrectInput(input, Kind.ASSIGN);
	}

	@Test
	public void ClosureDecC() throws SyntaxException {
		System.out.println("ClosureDecC");
		String input = "import A.B; class A { def a={->b=3};} ";
		System.out.println(input);
		// parseIncorrectInput(input,Kind.ASSIGN);
		parseCorrectInput(input);
	}

	@Test
	public void WhileC() throws SyntaxException {
		System.out.println("WhileC");
		String input = "import A.B; class A {while*(a){ a=b;} } ";
		System.out.println(input);
		// parseIncorrectInput(input,Kind.ASSIGN);
		parseCorrectInput(input);
	}

	@Test
	public void print() throws SyntaxException {
		System.out.println("print");
		String input = "import A.B; class A {print\"Hello\";}";
		System.out.println(input);
		// parseIncorrectInput(input,Kind.ASSIGN);
		parseCorrectInput(input);
	}

	@Test
	public void mod() throws SyntaxException {
		System.out.println("Mod");
		String input = "import A.B; class A {%abc;}";
		System.out.println(input);
		// parseIncorrectInput(input,Kind.ASSIGN);
		parseCorrectInput(input);
	}

	@Test
	public void testreturn() throws SyntaxException {
		System.out.println("WhileC");
		String input = "import A.B; class A {return true; return hello; return a+b;}";
		System.out.println(input);
		// parseIncorrectInput(input,Kind.ASSIGN);
		parseCorrectInput(input);
	}

	@Test
	public void Empty() throws SyntaxException {
		System.out.println("Empty");
		String input = " ";
		System.out.println(input);
		parseIncorrectInput(input, EOF);
	}

	@Test
	public void WrongAllowableInput1() throws SyntaxException {
		System.out.println("WrongAllowableInput");
		String input = "import java.lang.java; class A{a=true+false;} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void WrongAllowableInput2() throws SyntaxException {
		System.out.println("WrongAllowableInput");
		String input = "import java.lang.java; class A{b=true+\"HELLO\";} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void WrongAllowableInput3() throws SyntaxException {
		System.out.println("WrongAllowableInput");
		String input = "import java.lang.java; class A{a[true]=true;} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void RelOp1() throws SyntaxException {
		System.out.println("RelOp");
		String input = "import java.lang.java; class A{while(a==b){}if(a!=b){}else{}if(a|b){}} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void RelOp2() throws SyntaxException {
		System.out.println("WrongAllowableInput");
		String input = "import java.lang.java; class A{while(a&b){}if(a<b){}else{}if(a>b){}if(a<b){}if(a>=b){}if(a<=b){}}";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void RelOp3() throws SyntaxException {
		System.out.println("RelOp");
		String input = "import java.lang.java; class A{print a==b} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void RelOp4() throws SyntaxException {
		System.out.println("RelOp");
		String input = "import java.lang.java; class A{return a==b} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void RelOp5() throws SyntaxException {
		System.out.println("RelOp");
		String input = "import java.lang.java; class A{%a==b} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

}
