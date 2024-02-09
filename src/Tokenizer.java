import javafx.scene.shape.Line;

public class Tokenizer {

	private String[] reservedTokens;// organized such that [0] is the token to be compared with, followed by

	// reserved words
	// followed finally by special tokens.
	private int currentLine = 1;

	private int indexOfFirstReservedWord = 1;
	private int indexOfLastReservedWord = 32;


	private int currentCharIndex = 0;
	private String currentToken;

	public boolean reachedEnd;

	private String sourceCode;

	private String alphabet = "abcdefghijlkmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String digits = "1234567890";

	private String legalCharacters = "abcdefghijlkmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "1234567890><=+-/[]{}().*;:,| \n\t\0";

	public Tokenizer(String sourceCode) {
		reservedTokens = new String[] { "", "module", "begin", "end", "const", "var", "integer", "real", "char",
				"procedure", "readint", "readreal", "readchar", "readln", "writeint", "writereal", "writechar",
				"writeln", "if", "then", "elseif", "then", "else", "while", "do", "loop", "until",
				"exit", "call", "mod", "div", ";", ".", "(", ")", "[", "]", "{", "}", ",", "+", "-", "*", "/", "%", "=",
				"<", ">", ":", "|=", "<=", ">=",":=","integerValue","realValue", "errors", "userDefined" };

		this.sourceCode = sourceCode;

	}

	@SuppressWarnings("unused")
	private void viewTokensAndIDs() {// add this function just so the tokens can be visible with their IDs and be collapsible.
//		module 1
//		begin 2
//		end 3
//		const 4
//		var 5
//		integer 6
//		real 7
//		char 8
//		procedure 9
//		readint 10
//		readreal 11
//		readchar 12
//		readln 13
//		writeint 14
//		writereal 15
//		writechar 16
//		writeln 17
//		if 18
//		then 19
//		elseif 20
//		then 21
//		else 22
//		while 23
//		do 24
//		loop 25
//		until 26
//		exit 27
//		call 28
//		mod 29
//		div 30
//		; 31
//		. 32
//		( 33
//		) 34
//		[ 35
//		] 36
//		{ 37
//		} 38
//		, 39
//		+ 40
//		- 41
//		* 42
//		/ 43
//		% 44
//		= 45
//		< 46
//		> 47
//		: 48
//		|= 49
//		<= 50
//		>= 51
//		:= 52
//		integerValue 53
//		realValue 54
//		errors 55
//		userDefined 56
	}

	public int getToken() throws TokenException {
		//System.out.println(currentCharIndex);
		char currentChar = getNextChar();
		
		
		while (currentChar == ' ' || currentChar == '\n' || currentChar == '\t') {
			if (currentChar == '\n') {
				currentLine += 1;
			}
			currentChar = getNextChar();
		}
		
		int FSA = -1;

		if (isDigit(currentChar))
			FSA = 0;
		else if (isAlphabetic(currentChar))
			FSA = 1;
		else if (currentChar == '+')
			FSA = 2;
		else if (currentChar == ';')
			FSA = 3;
		else if (currentChar == ':')
			FSA = 4;
		else if (currentChar == ',')
			FSA = 5;
		else if (currentChar == '=')
			FSA = 6;
		else if (currentChar == '(')
			FSA = 7;
		else if (currentChar == ')')
			FSA = 8;
		else if (currentChar == '-')
			FSA = 9;
		else if (currentChar == '*')
			FSA = 10;
		else if (currentChar == '/')
			FSA = 11;
		else if (currentChar == '.')
			FSA = 12;
		else if (currentChar == '|')
			FSA = 13;
		else if (currentChar == '<')
			FSA = 14;
		else if (currentChar == '>')
			FSA = 15;
		else if (currentChar == 0)
			FSA = 20;

		
		//System.out.println(currentChar + " " + currentCharIndex);
		// System.out.println(FSA);
		int possibleValue = -1;

		switch (FSA) {

		case 0:// Value FSA
			possibleValue = valueFSA(currentChar);// possible value of token
			if (possibleValue == 1) {// integerValue
				return 53;
			} else {
				if (possibleValue == 2) {// realValue
					return 54;
				} else {
					if (possibleValue == 5) {
						throw new TokenException(currentLine,currentToken,2,"Real Syntax is incorrect");
					}
				}
			}
			break;
		case 1:// Name FSA
			possibleValue = nameFSA(currentChar);
			return possibleValue;

		case 2: // + FSA
			currentToken = currentChar+"";
			return 40;
		case 3:// ; FS;
			currentToken = currentChar+"";
			return 31;
		case 4: // : FSA
			possibleValue = colonFSA(currentChar);
			if (possibleValue == 0)
				return 52;
			else if (possibleValue == 1)
				return 48;
			else
				throw new TokenException(currentLine,currentToken,3,"I have no idea what went wrong");
		case 5: // , FSA
			currentToken = currentChar+"";
			return 39;
		case 6: // = FSA
			currentToken = currentChar+"";
			return 45;
		case 7: // ( FSA
			currentToken = currentChar+"";
			return 33;
		case 8: // ) FSA
			currentToken = currentChar+"";
			return 34;
		case 9: // - FSA
			currentToken = currentChar+"";
			return 41;
		case 10: // * FSA
			currentToken = currentChar+"";
			return 42;
		case 11: // / FSA
			currentToken = currentChar+"";
			return 43;
		case 12:// . FSA
			currentToken = currentChar+"";
			return 32;
		case 13: // | FSA
			possibleValue = notEqualFSA(currentChar);
			if (possibleValue == 0)
				return 49;
			else if (possibleValue == 1)
				throw new TokenException(currentLine,currentToken,4,"Stray |");
			else
				throw new TokenException(currentLine,currentToken,3,"I have no idea what went wrong");
		case 14: // < FSA
			possibleValue = lessThanFSA(currentChar);
			if(possibleValue ==0) {
				return 50;
			}else {
				if(possibleValue==1)
					return 46;
				else
					throw new TokenException(currentLine,currentToken,3,"I have no idea what went wrong");
			}
		case 15: // > FSA
			possibleValue = moreThanFSA(currentChar);
			if(possibleValue ==0) {
				return 51;
			}else {
				if(possibleValue==1)
					return 47;
				else
					throw new TokenException(currentLine,currentToken,3,"I have no idea what went wrong");
			}
		case 20:// found the \0 at the end of the file
			currentLine--;
			reachedEnd=true;
			return 0;
		default:
			throw new TokenException(currentLine,currentToken,3,"I have no idea what went wrong");
		}

		return 0;
	}

	private int moreThanFSA(char currentChar) throws TokenException {
		currentToken = "";
		if (currentChar == '>') {
			currentToken += currentChar;
			char nextChar = getNextChar();
			if(nextChar == '=') {
				currentToken += nextChar;
				return 0;
			}
			else
				currentCharIndex--;
				return 1;
		}
		return 2;
	}
	private int colonFSA(char currentChar) throws TokenException {
		currentToken = "";
		if (currentChar == ':') {
			currentToken += currentChar;
			char nextChar = getNextChar();
			if(nextChar == '=') {
				currentToken += nextChar;
				return 0;
			}
			else
				currentCharIndex--;
				return 1;
		}
		return 2;
	}

	private int lessThanFSA(char currentChar) throws TokenException {
		currentToken = "";
		if (currentChar == '<') {
			currentToken += currentChar;
			char nextChar = getNextChar();
			if(nextChar == '=') {
				currentToken += nextChar;
				return 0;
			}
			else
				currentCharIndex--;
				return 1;
		}
		return 2;
	}
	private int notEqualFSA(char currentChar) throws TokenException {

		currentToken = "";
		if (currentChar == '|') {
			currentToken += currentChar;
			char nextChar = getNextChar();
			if (nextChar == '=') {
				currentToken += nextChar;
				return 0;
			} else
				currentCharIndex--;
				return 1;
		}

		return 2;
	}

	private int nameFSA(char currentChar) throws TokenException {

		currentToken = "";
		if (isAlphabetic(currentChar)) {
			currentToken += currentChar;
			char nextChar = getNextChar();
			//System.out.println(nextChar);
			while (isDigit(nextChar) || isAlphabetic(nextChar)) {
				currentToken += nextChar;
				nextChar = getNextChar();
				
			}
			
			currentCharIndex--;

			int possibleValue = checkIfReserved();

			if (possibleValue == -1) {
				return reservedTokens.length - 1;
			} else {
				return possibleValue;
			}

		}

		return 0;
	}

	private int checkIfReserved() {
		reservedTokens[0] = currentToken.toLowerCase();
		
		for (int i = indexOfFirstReservedWord; i < indexOfLastReservedWord + 1; i++) {
			if (reservedTokens[0].equals(reservedTokens[i]))
				return i;
		}

		return -1;

	}

	private int valueFSA(char currentChar) throws TokenException {
		
		currentToken = "";
		if (isDigit(currentChar)) {
			currentToken+=currentChar;
			char nextChar = getNextChar();
			while (isDigit(nextChar)) {
				currentToken+=nextChar;
				nextChar = getNextChar();
			}
			if (nextChar != '.') {
				currentCharIndex--;
				return 1;// It's an integer
			} else {
				currentToken+=nextChar;
				nextChar = getNextChar();
				if (!isDigit(nextChar)) {
					return 5; // Float syntax as incorrect, d. no continutation.
				}
				while (isDigit(nextChar)) {
					currentToken+=nextChar;
					nextChar = getNextChar();
				}
				currentCharIndex--;
				return 2;// it's a float.
			}

		}
		return 0;// impossible to reach because the switch case only arrives if the
		// character is a digit.
		// is added because of compiler issues.
	}

	private boolean isDigit(char character) {

		for (int i = 0; i < digits.length(); i++) {
			if (character == digits.charAt(i)) {
				return true;
			}
		}
		return false;
	}

	private boolean isAlphabetic(char character) {

		character = Character.toLowerCase(character);

		for (int i = 0; i < alphabet.length(); i++) {
			if (character == alphabet.charAt(i)) {
				return true;
			}
		}
		return false;
	}

	private char getNextChar() throws TokenException {
		
		if (currentCharIndex < sourceCode.length()) {
			
			char currentChar = sourceCode.charAt(currentCharIndex);
			
			if (!islegal(currentChar)) {
				throw new TokenException(currentLine,currentChar+"",1,"Illegal Character used");
			}
			

			
//			if (isAlphabetic(currentChar)) {
//				currentChar = Character.toLowerCase(currentChar);
//			}
			
			currentCharIndex++;
			
			return currentChar;
		}
		return 0;
	}

	private boolean islegal(char character) {
		for (int i = 0; i < legalCharacters.length(); i++) {
			if (character == legalCharacters.charAt(i)) {
				return true;
			}
		}
		return false;
	}

	
	
	
	
	//setters and getters
	public String[] getReservedTokens() {
		return reservedTokens;
	}

	public void setReservedTokens(String[] reservedTokens) {
		this.reservedTokens = reservedTokens;
	}
	
	public String getCurrentToken() {
		return currentToken;
	}

	public void setCurrentToken(String currentToken) {
		this.currentToken = currentToken;
	}

	public int getCurrentLine() {
		return currentLine;
	}

	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
	}

}
