package cop5555sp15;

import java.util.List;

/**
 * This code initializes a list and iterates over it using a while loop and prints it.
 * Output:
 * 
10
9
8
7
6
5
4
3
2
1
done

 * 
 */
public class Example1 {
	public static void main(String[] args) throws Exception {
		String source = "class C{def l1:@[int]; l1 = @[1,2,3,4,5,6,7,8,9,10]; def x:int;"
				+ "x = 9;"
				+ "while(x>=0) {"
				+ "print l1[x];"
				+ "x = x-1;"
				+ "}"
				+ "print \"done\";}"; 
		Codelet codelet = CodeletBuilder.newInstance(source);
		codelet.execute();
	}
}