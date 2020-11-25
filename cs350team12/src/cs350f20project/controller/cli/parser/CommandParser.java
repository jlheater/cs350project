package cs350f20project.controller.cli.parser;


import java.util.ArrayList;

import cs350f20project.controller.command.A_Command;
import cs350f20project.controller.command.meta.CommandMetaDoExit;
import cs350f20project.controller.command.meta.CommandMetaViewDestroy;
import cs350f20project.controller.command.structural.CommandStructuralCommit;
import cs350f20project.controller.command.structural.CommandStructuralCouple;
import cs350f20project.controller.command.structural.CommandStructuralUncouple;

public class CommandParser {
	
	private MyParserHelper parserHelper;
	private ArrayList<Tokenizer> tokenizers;
	
/*
CommandParser contains all the misc commands. It passes the DO and CREATE commands to their respective classes
51 @EXIT
52 @RUN string
55 CLOSE VIEW id
56 OPEN VIEW id1 ORIGIN ( coordinates_world | ( '$' id2 ) ) WORLD WIDTH integer1 SCREEN WIDTH integer2 HEIGHT integer3
60 COMMIT
61 COUPLE STOCK id1 AND id2
62 LOCATE STOCK id1 ON TRACK id2 DISTANCE number FROM ( START | END )
65 UNCOUPLE STOCK id1 AND id2
66 USE id AS REFERENCE coordinates_world
67 Rule#2 through Rule#65
*/
	public CommandParser(MyParserHelper parserHelper, String commandText) {
		
		String[] commandTexts = commandText.split(";");
		this.parserHelper = parserHelper;
		
		this.tokenizers = new ArrayList<Tokenizer>();
		for(String command: commandTexts) {
			tokenizers.add(new Tokenizer(command.trim(), parserHelper));
		}
	}

	// So this is where the 41 if statements/rules will go
	// And we can create a new class for each rule so it cleans this up a bit
	public void parse(){
		for(Tokenizer tokens: this.tokenizers) {
			String token = tokens.getNext();

			if(token == null)
				throw new RuntimeException("Error! Invalid token!");
			
			if(token.equalsIgnoreCase("CREATE"))
				createCommand(tokens);
			else if(token.equalsIgnoreCase("DO"))
				doCommand(tokens);
			else if(token.equalsIgnoreCase("@EXIT"))
				exit();
			else if(token.equalsIgnoreCase("@RUN"))
				run();
			else if(token.equalsIgnoreCase("COMMIT"))
				commit();
			else if(token.equalsIgnoreCase("USE"))
				use();
			else if(token.equalsIgnoreCase("CLOSE"))
				closeView(tokens);
			else if(token.equalsIgnoreCase("OPEN"))
				openView();
			else if(token.equalsIgnoreCase("COUPLE"))
				coupleOrUncoupleStock(tokens, true);
			else if(token.equalsIgnoreCase("LOCATE"))
				locateStock();
			else if(token.equalsIgnoreCase("UNCOUPLE"))
				coupleOrUncoupleStock(tokens, false);
			else {
				throw new RuntimeException("Error! Invalid token!");
			}
		}
	}
	
	//parses the tokens through an instance of the class CREATE
	public void createCommand(Tokenizer tokens) {
		Create create = new Create(tokens);
		this.parserHelper.getActionProcessor().schedule(create.parse());
	}
	
	//parses the tokens through an instance of the class DO
	public void doCommand(Tokenizer tokens) {
		Do d = new Do(tokens);
		this.parserHelper.getActionProcessor().schedule(d.parse());
	}
	
	
	
	//Exit function
	public void exit() {
		// 51 @EXIT
		A_Command command = new CommandMetaDoExit();
		this.parserHelper.getActionProcessor().schedule(command);
	}
	
	public void run() {
		//52 @RUN string
		
	}
	
	public void commit() {
		//60 COMMIT
	}
	
	public void use() {
		//66 USE id AS REFERENCE coordinates_world
		//when entering this method tokens.getNext() should be the id
	}
	
	public void closeView(Tokenizer tokens) {
		// 55 CLOSE VIEW id
		String id = tokens.get(2);
		if(!Checks.checkID(id, false)) {
			tokens.invalidToken();
		}
		
		this.parserHelper.getActionProcessor().schedule(new CommandMetaViewDestroy(id));	
	}
	
	public void openView() {
		// 56 OPEN VIEW id1 ORIGIN ( coordinates_world | ( '$' id2 ) ) WORLD WIDTH integer1 SCREEN WIDTH integer2 HEIGHT integer3
		// check to make sure the next token is in fact "VIEW" otherwise invalid token
		
	}
	
	public void coupleOrUncoupleStock(Tokenizer tokens, boolean isCoupleElseUncouple) {
		// Implements one of the two commands depending on the passed in boolean value: true is COUPLE, false is UNCOUPLE
		// 61 COUPLE STOCK id1 AND id2
		// 65 UNCOUPLE STOCK id1 AND id2
		String stockId1 = tokens.get(2);
		if(!Checks.checkID(stockId1, false)) {
			tokens.invalidToken();
		}
		
		String stockId2 = tokens.get(4);
		if(!Checks.checkID(stockId2, false)) {
			tokens.invalidToken();
		}
		
		A_Command command;
		if(isCoupleElseUncouple) {
			command = new CommandStructuralCouple(stockId1, stockId2);
		}
		else {
			command = new CommandStructuralUncouple(stockId1, stockId2);
		}
		
		this.parserHelper.getActionProcessor().schedule(command);
	}
	
	public void locateStock() {
		// 62 LOCATE STOCK id1 ON TRACK id2 DISTANCE number FROM ( START | END )
		// check token.getNext() is "STOCK" otherwise invalid token
	}
}
