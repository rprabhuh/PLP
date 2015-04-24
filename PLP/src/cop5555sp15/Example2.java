package cop5555sp15;

import java.util.List;

/**
 * This code takes a list of names and prints a greeting.
 * Output:
 * Hello
Prabhu
Hello
Rahul
 */
public class Example2 {
	public static void main(String[] args) throws Exception {
		String source = "class C{def l1:@[string]; l1 = @[\"Rahul\",\"Prabhu\"]; def x:int;"
				+ "x = 1;"
				+ "while(x>=0) {"
				+ "print \"Hello\";"
				+ "print l1[x];"
				+ "x = x-1;"
				+ "}"
				+ "}"; 
		Codelet codelet = CodeletBuilder.newInstance(source);
		codelet.execute();
	}
}