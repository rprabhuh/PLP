package cop5555sp15;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import static cop5555sp15.TokenStream.Kind.*;

public class Scanner {

	TokenStream stream;
	FSA_STATES state;
	int linecount,beg,end;
	public static enum FSA_STATES{
		START,
		DIGIT,
		IDENT,
		COMMENT,
	}
	public Scanner(TokenStream stream) {
		this.stream = stream;
		this.state = FSA_STATES.START;
		this.linecount = 0;
		this.beg = 0;
		this.end = 0;
	}


	// Fills in the stream.tokens list with recognized tokens 
     //from the input
	public void scan() {
          //IMPLEMENT THIS
		for(int i=0;i<=stream.inputChars.length;i++) {
			switch(state){
			case START:
				if(i==stream.inputChars.length) {
					TokenStream.Token t = stream.new Token(EOF,beg,end,linecount);
					stream.tokens.add(t);
				} else if(Character.isWhitespace(stream.inputChars[i])) {
					beg++;
					end++;
					if(stream.inputChars[i] == 10) {
						//If the input char is LF, increment the linecount 
						linecount++;
					} else if (stream.inputChars[i] == 13) {
						//If the input char is a CR, look ahead and see if you have a LF
						//If the next char is a LF, increment i by 1
						if(stream.inputChars[i+1] == 10) {
							i++;
							beg++;
							end++;
						}
						//Increment linecount
						linecount++;
					}
				} else if(stream.inputChars[i] == '/') {
					beg++;
					end++;
					if(stream.inputChars[i+1]=='*')
						state = FSA_STATES.COMMENT;
						i = i+1;
						beg++;
						end++;
				}
				break;
				
			case COMMENT:
				if(stream.inputChars[i] == '*' && stream.inputChars[i+1] == '/') {
					state = FSA_STATES.START;
					end = end + 2;
				}
				beg++;
				break;
			case DIGIT:
				break;
			case IDENT:
				break;
			default:
				break;
			}
		}
	}

}

