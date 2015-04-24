package cop5555sp15;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.List;

import cop5555sp15.TestCodeGenerationAssignment5.DynamicClassLoader;
import cop5555sp15.ast.ASTNode;
import cop5555sp15.ast.CodeGenVisitor;
import cop5555sp15.ast.Program;
import cop5555sp15.ast.TypeCheckVisitor;
import cop5555sp15.ast.TypeCheckVisitor.TypeCheckException;
import cop5555sp15.symbolTable.SymbolTable;

public class CodeletBuilder {
	public static Codelet newInstance(String source) throws Exception {
		TokenStream stream = new TokenStream(source);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		Parser parser = new Parser(stream);
		System.out.println();
		ASTNode ast = parser.parse();
		if (ast == null) {
			System.out.println("errors " + parser.getErrors());
		}
		assertNotNull(ast);

		Program program = (Program) ast;
		SymbolTable symbolTable = new SymbolTable();
		TypeCheckVisitor tv = new TypeCheckVisitor(symbolTable);
		try {
			ast.visit(tv, null);
		} catch (TypeCheckException e) {
			System.out.println(e.getMessage());
			fail("no errors expected");
		}
		
		CodeGenVisitor cv = new CodeGenVisitor();
		byte[] bytecode = (byte[]) ast.visit(cv, null);
		
    	assertNotNull(bytecode);
    	
    	FileOutputStream fos = new FileOutputStream(program.JVMName+".class");
    	fos.write(bytecode);
    	fos.close();
    	
    	DynamicClassLoader loader = new DynamicClassLoader(Thread
                .currentThread().getContextClassLoader());
        Class<?> testClass = loader.define(program.JVMName, bytecode);
        Codelet codelet = (Codelet) testClass.newInstance();
         return codelet;

	}

	public static Codelet newInstance(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		String input = new String(data, "UTF-8");
		
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		Parser parser = new Parser(stream);
		System.out.println();
		ASTNode ast = parser.parse();
		if (ast == null) {
			System.out.println("errors " + parser.getErrors());
		}
		assertNotNull(ast);

		Program program = (Program) ast;
		SymbolTable symbolTable = new SymbolTable();
		TypeCheckVisitor tv = new TypeCheckVisitor(symbolTable);
		try {
			ast.visit(tv, null);
		} catch (TypeCheckException e) {
			System.out.println(e.getMessage());
			fail("no errors expected");
		}
		
		CodeGenVisitor cv = new CodeGenVisitor();
		byte[] bytecode = (byte[]) ast.visit(cv, null);
		
    	assertNotNull(bytecode);
    	
    	FileOutputStream fos = new FileOutputStream("~/"+program.JVMName+".class");
    	fos.write(bytecode);
    	fos.close();
    	
    	DynamicClassLoader loader = new DynamicClassLoader(Thread
                .currentThread().getContextClassLoader());
        Class<?> testClass = loader.define(program.JVMName, bytecode);
        Codelet codelet = (Codelet) testClass.newInstance();
         return codelet;
		
	}

	@SuppressWarnings("rawtypes")
	public static List getList(Codelet codelet, String name) throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		List l = (List) l1Field.get(codelet);
		return l;
	}

	public static int getInt(Codelet codelet, String name) throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		int i = (int) l1Field.get(codelet);
		return i;
	}

	public static void setInt(Codelet codelet, String name, int value)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		l1Field.set(codelet, value);
	}

	public static String getString(Codelet codelet, String name)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		String str = (String) l1Field.get(codelet);
		return str;

	}

	public static void setString(Codelet codelet, String name, String value)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		l1Field.set(codelet, value);
	}

	public static boolean getBoolean(Codelet codelet, String name)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		boolean b = (boolean) l1Field.get(codelet);
		return b;

	}

	public static void setBoolean(Codelet codelet, String name, boolean value)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		l1Field.set(codelet, value);
	}
	
	
}