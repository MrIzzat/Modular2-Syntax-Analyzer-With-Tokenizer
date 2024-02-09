import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Main extends Application{

	static Tokenizer tk;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		
		launch(args);
	}



	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FileChooser fileChooser = new FileChooser();//C:\\Users\\MrIzzat\\eclipse-workspace\\CompilerProject\\src\\
		fileChooser.setInitialDirectory(new File("./"));
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		
		Scanner sc = new Scanner(selectedFile);
		String code = "";

		while(sc.hasNextLine()) {
			code += sc.nextLine()+"\n";
		}
		
		
		tk = new Tokenizer(code+"\0");
		
		
//		int tok=0; //extracts tokens and views them
//		while (!tk.reachedEnd) {
//			tok = tk.getToken();
//			System.out.println("Token ID: "+tok+" | Token: "+tk.getCurrentToken()+" | Token Type: "+tk.getReservedTokens()[tok]);
//		}
//		
		
		
		Parser parser = new Parser(tk);
		
		try {
			parser.StartParsing();
		}
		catch (ParserException e) {
			parserErrors(e.errorNumber,e.lineNumber,e.token,e.errorDescription);
		}
		catch (TokenException e) {
			tokenErrors(e.errorNumber,e.lineNumber,e.token,e.errorDescription);
		}
		
		
		sc.close();
		
		//tryTestCases();
		
		System.exit(0);
		
		
		
	}
	
	public static void parserErrors(int errorNumber, int lineNumber, String token, String description) {
		System.out.println("Parser Error "+errorNumber+": "+description+"\nOn line: "
				+lineNumber+"\nNear token: "+token);
		
	}

	public static void tokenErrors(int errorNumber, int lineNumber,String token,String description) {
		System.out.println("Token Error "+errorNumber+": "+description+"\nOn line: "
	+lineNumber+"\nWith token: "+token);
	}
	
	
	
	
	
	public static void tryTestCases() throws FileNotFoundException {
		for(int i=0;i<35;i++) {
			Scanner sc = new Scanner(new File("./TestCases/input"+i+".txt"));
			String code = "";

			while(sc.hasNextLine()) {
				code += sc.nextLine()+"\n";
				tk = new Tokenizer(code+"\0");
				
				
			}
			Parser parser = new Parser(tk);
			
			try {
				parser.StartParsing();
			}
			catch (ParserException e) {
				parserErrors(e.errorNumber,e.lineNumber,e.token,e.errorDescription);
			}
			catch (TokenException e) {
				tokenErrors(e.errorNumber,e.lineNumber,e.token,e.errorDescription);
			}
			System.out.println("\n");
			sc.close();
		}
	
	}
}
