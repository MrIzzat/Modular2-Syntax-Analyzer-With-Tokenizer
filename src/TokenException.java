
public class TokenException extends Exception{

	public int lineNumber;
	public String token;
	public int errorNumber;
	public String errorDescription;
	
	
	public TokenException(int lineNumber,String token,int errorNumber,String errorDescription) {
		this.lineNumber = lineNumber;
		this.token = token;
		this.errorNumber = errorNumber;
		this.errorDescription = errorDescription;
	}
}
