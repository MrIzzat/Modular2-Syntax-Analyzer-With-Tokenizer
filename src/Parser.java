
public class Parser {

	private Tokenizer tk;

	private int currentTokenID;

	public Parser(Tokenizer tk) {
		this.tk = tk;

	}

	@SuppressWarnings("unused")
	private void viewProductionRules() {// Just to view the production rules and be collapsible
//	module-decl ==> module-heading    declarations   procedure-decl   block    name  .
//	module-heading   ==> module        name      ; 
//	block  ==>  begin        stmt-list         end
//	declarations   ==>  const-decl    var-decl    
//	const-decl  ==> const    const-list     |       lambda
//	const-list    ==>   ( name      =    value   ;  )* 
//	var-decl  ==> var    var-list     |      lambda
//	var-list   ==>  ( var-item     ;  )*    
//	var-item   ==>  name-list     :     data-type    
//	name-list  ==>   name    ( ,   name )*
//	data-type  ==>   integer    |    real   |     char 
//	procedure-decl ==>    procedure-heading        declarations        block       name  ;
//	procedure-heading   ==> procedure        name      ; 
//	stmt-list ==>     statement    ( ;   statement )*  
//	statement ==>  ass-stmt   |    read-stmt    |    write-stmt    |      if-stmt   
//	                                  |  while-stmt    |     repeat-stmt  |   exit-stmt   |   call-stmt    |       lambda
//	ass-stmt ==> name     :=      exp
//	exp ==> term     (  add-oper    term  )* 
//	term ==> factor   ( mul-oper   factor )*     
//	factor ==>  “(“     exp     “)”     |     name      |      value
//	add-oper ==>  +    |     -  
//	mul-oper ==> *     |     /       |      mod     |    div
//	read-stmt ==>readint   “(“    name-list “)”     |  readreal   “(“    name-list  “)”     
//	                              |     readchar    “(“    name-list    “)”    |    readln  
//	write-stmt ==>writeint  “(“  write-list “)”   |  writereal   “(“  write-list    “)”     
//	                               writechar  “(“    write-list “)”     |    writeln  
//	write-list  ==>   write-item    ( ,   write-item )*
//	write-item  ==>   name   |    value   
//	if-stmt ==> if  condition   then   stmt-list   elseif-part   else-part    end
//	elseif-part ==> ( elseif  condition   then   stmt-list  )*      
//	else-part ==>   else     stmt-list     |    lambda
//	while-stmt ==> while      condition       do      stmt-list   end
//	repeat-stmt   ==>  loop      stmt-list       until        condition   
//	exit-stmt   ==>  exit      
//	call-stmt   ==>  call name          (*  This is a procedure name   *)
//	condition ==>   name-value       relational-oper        name-value   
//	name-value ==>  name    |      value 
//	relational-oper ==>  =      |     |=    |    <     |       <=     |     >     |     >=
//	name ==> letter ( letter | digit )*
//	value ==> integer-value   |   real-value
//	integer-value ==> digit ( digit )*    
//	real-value ==> digit ( digit )*. digit ( digit )*

	}

	public void StartParsing() throws TokenException, ParserException {
		main();

	}

	private void main() throws TokenException, ParserException {// module-decl ==> module-heading declarations procedure-decl block name .
		getToken();

		module_heading();

		declarations();

		procedure_decl();

		block();

		if (isUserDefinedName())
			getToken();
		else
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),1,"Module did not end with a valid user defined name");

		if (currentTokenID == 32){// .
			getToken();
			if (currentTokenID==0)
				System.out.println("Successful Parsing");
			else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),53,"Program syntax is correct but there is extra text after the \".\"");
			
		}
		else
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),2,"(did you forget the \".\" at the end?");

	}

	private void module_heading() throws TokenException, ParserException { // module-heading ==> module name ;
		if (currentTokenID == 1) {// module
			getToken();

			if (isUserDefinedName())
				getToken();
			else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),4,"Module name not is not defined");

			if (currentTokenID == 31) {// ;
				getToken();
			} else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),5,"Missing ; at the end of module heading");

		} else 
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),3,"\"module\" keyword not used when declaring module");
		

	}
	
	private void block() throws TokenException, ParserException {// block ==> begin stmt-list end
		if (currentTokenID == 2) {// begin
			getToken();
			stmt_list();
			if (currentTokenID == 3) {// end
				getToken();
			} else {
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),7, "Block did not end with an \"end\" statement");
			}
		} else {
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),6,"Block did not start with \"begin\" keyword");
		}

	}


	private void declarations() throws TokenException, ParserException {// declarations ==> const-decl var-decl
		const_decl();
		var_decl();

	}
	
	private void const_decl() throws TokenException, ParserException {// const-decl ==> const const-list | lambda
		if (currentTokenID == 4) {// const
			getToken();
			const_list();
		} else {
			if (currentTokenID != 5 && currentTokenID != 9 && currentTokenID != 2)// if the current token is not var or
																					// procedure or begin because of lambda
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),8,"Either define a constant or a variable or a procedure or begin a block");
			}

	}
	
	private void const_list() throws TokenException, ParserException {//const-list ==>   ( name      =    value   ;  )* 
		while(isUserDefinedName()) {
			getToken();
			if(currentTokenID==45) {// = 
				getToken();
				if(isValue()) { 
					getToken();
					if(currentTokenID==31) {// ;
						getToken();
					}else
						throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),9,"constant does not end with \";\"");
				}else 
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),10,"constant does not have a value defined for it");
			}else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),11,"constant does not use a \"=\"");

		}
		//else
		//	throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),12,"constant does not have a name");

	}
	
	private void var_decl() throws TokenException, ParserException {// var-decl ==> var var-list | lambda
		if (currentTokenID == 5) {// var
			getToken();
			var_list();
		} else {
			if (currentTokenID != 2 && currentTokenID != 9) // if the token is begin or procedure
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),13,"Either define a variable or a procedure or begin a block");
		}

	}
	
	private void var_list() throws TokenException, ParserException {// var-list   ==>  ( var-item     ;  )*    
		
		while(isUserDefinedName()){//first of var-item --> first of  name-list --> name.
			var_item();
			if(currentTokenID==31){// ; 
				getToken();
			}
			else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),14,"var item did not end with \";\"");
		}
		

	}
	

	private void var_item() throws TokenException, ParserException{//var-item ==>  name-list     :     data-type    
		if(isUserDefinedName()) {
			name_list();
			if(currentTokenID==48) {// :
				getToken();
				data_type();
			}else {
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),15,"var item does not have a \":\"");
			}
		}
	}
	
	private void name_list()throws TokenException, ParserException {//name-list  ==>   name    ( ,   name )*
		if(isUserDefinedName()) {
			getToken();
			while(currentTokenID==39) {//while token is ,
				getToken();
				if(isUserDefinedName())
					getToken();
				else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),16,"\",\" is not followed by a name");
			}
			
		}else
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),17,"var item does not have a user-defined name");
		
	}

	private void data_type() throws TokenException, ParserException{//data-type ==>   integer    |    real   |     char 
		if(currentTokenID==6) // integer
			getToken();
		else
			if(currentTokenID==7)//real
				getToken();
			else
				if(currentTokenID==8)//char
					getToken();
				else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),18,"var item does not contain a data type");
		
	}

	private void procedure_decl() throws TokenException, ParserException {// procedure-decl ==> procedure-heading declarations block name ;
		procedure_heading();
		declarations();
		block();
		if (isUserDefinedName())
			getToken();
		else
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),19,"procedure does not have a user-defined name");

		if (currentTokenID == 31)// ;
			getToken();
		else {
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),20,"missing \";\" at the end of procedure declaration");
		}

	}

	private void procedure_heading() throws TokenException, ParserException{
		if(currentTokenID==9) {//procedure
			getToken();
			if(isUserDefinedName()) {
				getToken();
				if(currentTokenID==31){//;
					getToken();
				}else 
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),22,"procedure heading does not have a \";\"");
				
			}else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),21,"procedure heading does not have a user-defined name");
		}else
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),22,"\"procedure\" keyword not used ");

	}


	private void stmt_list() throws TokenException, ParserException{// stmt-list ==> statement ( ; statement )*
		statement();
		while (currentTokenID == 31) {// ;
			getToken();
			statement();
		}
	}

	private void statement() throws TokenException, ParserException{//statement ==>  ass-stmt   |    read-stmt    |    write-stmt    |      if-stmt   
        						//|  while-stmt    |     repeat-stmt  |   exit-stmt   |   call-stmt    |       
		
		if(isUserDefinedName()) 
			ass_stmt();
		else 
			if(currentTokenID==10||currentTokenID==11
			||currentTokenID==12||currentTokenID==13)// check if token is readint, readreal, readchar or readln
				read_stmt();
			else if (currentTokenID==14||currentTokenID==15
			||currentTokenID==16||currentTokenID==17)// check if token is writeint, writereal, writechar or writeln
				write_stmt();
			else if (currentTokenID==18)//if
				if_stmt();
			else if(currentTokenID==23)//while
				while_stmt();
			else if(currentTokenID==25)//loop
				repeat_stmt();
			else if(currentTokenID==27)//exit
				exit_statement();
			else if(currentTokenID==28)//call 
				call_statement();
			else if (currentTokenID!=31&& currentTokenID!=20 // the lambda part: check if the statement is followed by ;, but because of the second statemnt in statment list is followed by lambda
			&&currentTokenID!=22&& currentTokenID!=3//must also take follow of stmt-list, which can be: elseif, else, end and until. 
			&& currentTokenID!=26)// 
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),23,"statement list did not end correctly (could be missing an \"end\", \";\", \"until\", \"elseif\" or \"else\" ");
	}
	
	
	
	
	
	private void ass_stmt() throws TokenException, ParserException{//ass-stmt ==> name     :=      exp
		if(isUserDefinedName()) {
			getToken();
			if(currentTokenID==52) {// :=
				getToken();
				exp();
			}
			else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),24,"assignment statement does not have token \":=\"");

		}
		
	}
	
	private void exp()throws TokenException, ParserException {//exp ==> term     (  add-oper    term  )* 
		term();
		while(currentTokenID==40||currentTokenID==41) {//while the token is + or -
			add_oper();
			term();
		}
		
	}

	private void term() throws TokenException, ParserException{//term ==> factor   ( mul-oper   factor )* 
		factor();
		while(currentTokenID==42||currentTokenID==43 //while the token is *,/,mod or div
				||currentTokenID==29||currentTokenID==30) {
			mul_oper();
			factor();
		}
			
	}
	
	private void factor () throws TokenException, ParserException{//factor ==>  “(“     exp     “)”     |     name      |      value
		
		if(currentTokenID==33){// (
			getToken();
			exp();
			if(currentTokenID==34) // )
				getToken();
			else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),25," factor exp did not end with \")\"");
		}else 
			if(isUserDefinedName())
				getToken();
			else
				if(isValue())
					getToken();
				else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),26,"did not add an exp, name or value factor for this term.");
			
		
	}
	
	
	
	private void add_oper() throws TokenException, ParserException{//add-oper ==>  +    |     - 
		if(currentTokenID==40)// +
			getToken();
		else
			if(currentTokenID==41) // -
				getToken();
			else 
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),27,"Did not add an add operation (+,-)");
	}
	
	private void mul_oper() throws TokenException, ParserException{//mul-oper ==> *     |     /       |      mod     |    div
		
		if(currentTokenID==42)//*
			getToken();
		else
			if(currentTokenID==43) // /
				getToken();
			else if(currentTokenID==29)// mod
				getToken();
			else if(currentTokenID==30)// div
					getToken();
			else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),28,"Did not add a multiplication operation (*, /, mod, div) to this term.");
	}

	private void read_stmt() throws TokenException, ParserException{//read-stmt ==> readint   “(“    name-list “)”     |  readreal   “(“    name-list  “)”     
       							//|     readchar    “(“    name-list    “)”    |    readln  
		
		if (currentTokenID == 10) {// readint
			getToken();
			if (currentTokenID == 33) {// (
				getToken();
				name_list();
				if (currentTokenID == 34) {// )
					getToken();
				} else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),29,"readint does not have a \")\"");

			} else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),30,"readint does not have a \"(\"");

		} else {
			if (currentTokenID == 11) {// readreal
				getToken();
				if (currentTokenID == 33) {// (
					getToken();
					name_list();
					if (currentTokenID == 34) {// )
						getToken();
					} else
						throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),31,"readreal does not have a \")\"");

				} else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),32,"readreal does not have a \"(\"");


			} else {
				if (currentTokenID == 12) {// readchar
					getToken();
					if (currentTokenID == 33) {// (
						getToken();
						name_list();
						if (currentTokenID == 34) {// )
							getToken();
						} else
							throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),33,"readchar does not have a \")\"");

					} else
						throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),34,"readchar does not have a \"(\"");
				} else if (currentTokenID == 13) // readln
					getToken();

			}
		}
		
	}
	
	
	
	

	

	private void write_stmt() throws TokenException, ParserException{// write-stmt ==> writeint “(“ write-list “)” | writereal “(“ write-list “)”
								// writechar “(“ write-list “)” | writeln

		if (currentTokenID == 14) {// writeint
			getToken();
			if (currentTokenID == 33) {// (
				getToken();
				write_list();
				if (currentTokenID == 34) {// )
					getToken();
				} else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),35," writeint does not have a \")\"");

			} else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),36,"writeint does not have a \"(\"");

		} else {
			if (currentTokenID == 15) {// writereal
				getToken();
				if (currentTokenID == 33) {// (
					getToken();
					write_list();
					if (currentTokenID == 34) {// )
						getToken();
					} else
						throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),37,"writereal does not have a \")\"");

				} else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),38,"writereal does not have a \"(\"");

			} else {
				if (currentTokenID == 16) {// writechar
					getToken();
					if (currentTokenID == 33) {// (
						getToken();
						write_list();
						if (currentTokenID == 34) {// )
							getToken();
						} else
							throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),39,"writechar does not have a \")\"");

					} else
						throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),40,"writechar does not have a \"(\"");
				} else if (currentTokenID == 17) // writeln
					getToken();

			}
		}

	}

	private void write_list() throws TokenException, ParserException{// write-list ==> write-item ( , write-item )*

		write_item();

		while (currentTokenID == 39) {// ,
			getToken();
			write_item();
		}

	}

	private void write_item() throws TokenException, ParserException{// write-item ==> name | value

		if (isUserDefinedName())// user-defined
			getToken();
		else if (isValue())// is an integer or a float
			getToken();
		else
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),41,"write item is not a name or a value");

	}

	private void if_stmt() throws TokenException, ParserException{// if-stmt ==> if condition then stmt-list elseif-part else-part end

		if (currentTokenID == 18) {// if
			getToken();
			condition();
			if (currentTokenID == 19) {// then
				getToken();

				stmt_list();
				elseIf_Part();
				else_part();

				if (currentTokenID == 3) {// end
					getToken();
				} else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),42,"if statement does not contain \"end\" keyword");

			} else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),43,"if statement does not contain \"then\" keyword");
		}

	}

	private void elseIf_Part() throws TokenException, ParserException{// elseif-part ==> ( elseif condition then stmt-list )*

		while (currentTokenID == 20) {// elseif
			getToken();
			condition();
			if (currentTokenID == 19) {// then
				getToken();
				stmt_list();
			} else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),44,"elseif statement does not contain \"then\" keyword");
		}

	}

	private void else_part() throws TokenException, ParserException{// else-part ==> else stmt-list | lambda

		if (currentTokenID == 22) {// else
			getToken();
			stmt_list();
		}
//		 else {
//			if (currentTokenID != 3)// end
//				getToken();
//			else
//				System.out.println("Error 18: if statement does not contain the \"end\" keyword");
//		}

	}

	private void while_stmt() throws TokenException, ParserException{// while-stmt ==> while condition do stmt-list end
		if (currentTokenID == 23) {// while
			getToken();
			condition();
			if (currentTokenID == 24) {// do
				getToken();
				stmt_list();
				if (currentTokenID == 3) {// end
					getToken();
				} else
					throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),45,"while statement does not contain the \"end\" keyword");
			

			} else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),46,"while statement does not contain the \"do\" keyword");
		} 
	}

	private void repeat_stmt() throws TokenException, ParserException{// repeat-stmt ==> loop stmt-list until condition

		if (currentTokenID == 25) {// loop
			getToken();
			stmt_list();
			if (currentTokenID == 26) {// until
				getToken();
				condition();
			} else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),47,"repeat statement does not end with \"until\" keyword");

		} else {
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),48,"repeat statement not defined with \"loop\" keyword");

		}

	}

	private void exit_statement()throws TokenException {// exit-stmt ==> exit
		if (currentTokenID == 27)// exit
			getToken();
	}

	private void call_statement() throws TokenException, ParserException{// call-stmt ==> call name
		if (currentTokenID == 28) { // call
			getToken();
			if (isUserDefinedName())
				getToken();
			else
				throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),49,"Call statement does not contain a user-defined name");
		} 
	}

	private void condition() throws TokenException, ParserException{// condition ==> name-value relational-oper name-value

		name_value();

		relational_oper();

		name_value();

	}

	private void name_value() throws TokenException, ParserException {// name-value ==> name | value
		if (isUserDefinedName())
			getToken();
		else if (isValue())
			getToken();
		else
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),50,"this token is not a user-defined name nor a value (integer or real)");
	}
	
	private void relational_oper() throws TokenException, ParserException {// relational-oper ==> = | |= | < | <= | > | >=
		if (currentTokenID == 45)// =
			getToken();
		else if (currentTokenID == 49) // |= (not equals)
			getToken();
		else if (currentTokenID == 46) // <
			getToken();
		else if (currentTokenID == 50)// <=
			getToken();
		else if (currentTokenID == 47)// >
			getToken();
		else if (currentTokenID == 51)// >=
			getToken();
		else
			throw new ParserException(tk.getCurrentLine(),tk.getCurrentToken(),51,"No relational operation is defined");

	}
	
	private boolean isUserDefinedName() {// return user-defined name: name ==> letter ( letter | digit )*
		if (currentTokenID == (tk.getReservedTokens().length - 1))// fancy way of saying 54
			return true;
		return false;

	}

	private boolean isValue() {// value ==> integer-value | real-value
		if (isInteger_value())
			return true;
		else if (isReal_value())
			return true;
		else
			return false;

	}

	private boolean isInteger_value() {// integer-value ==> digit ( digit )*
		if (currentTokenID == 53) // integer
			return true;
		return false;

	}

	private boolean isReal_value() {// real-value ==> digit ( digit )*. digit ( digit )*
		if (currentTokenID == 54)// real value (Float)
			return true;
		return false;
	}

	

	

	private void getToken() throws TokenException {
		currentTokenID = tk.getToken();
//		if (currentTokenID == 0)
//			System.out.println("TOKEN ERROR");// throw an exception
	}

}
