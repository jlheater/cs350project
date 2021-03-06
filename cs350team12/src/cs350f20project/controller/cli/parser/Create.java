package cs350f20project.controller.cli.parser;

import java.util.ArrayList;
import java.util.List;
import cs350f20project.controller.cli.TrackLocator;
import cs350f20project.controller.command.A_Command;
import cs350f20project.controller.command.PointLocator;
import cs350f20project.controller.command.creational.*;
import cs350f20project.datatype.*;

/*
 * This class handles all the CREATE commands
22 CREATE POWER CATENARY id1 WITH POLES idn+
23 CREATE POWER POLE id1 ON TRACK id2 DISTANCE number FROM ( START | END )
24 CREATE POWER STATION id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA coordinates_delta WITH ( SUBSTATION | SUBSTATIONS )idn+
25 CREATE POWER SUBSTATION id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA coordinates_delta WITH CATENARIES idn+
28 CREATE STOCK CAR id AS BOX CommandCreateStockCarBox
29 CREATE STOCK CAR id AS CABOOSE CommandCreateStockCarCaboose
30 CREATE STOCK CAR id AS FLATBED CommandCreateStockCarFlatbed
31 CREATE STOCK CAR id AS PASSENGER CommandCreateStockCarPassenger
32 CREATE STOCK CAR id AS TANK CommandCreateStockCarTank
33 CREATE STOCK CAR id AS TENDER CommandCreateStockCarTender
34 CREATE STOCK ENGINE id1 AS DIESEL ON TRACK id2 DISTANCE number FROM ( START | END ) FACING ( START | END ) CommandCreateStockEngineDiesel
39 CREATE TRACK BRIDGE DRAW id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 ANGLE angle
40 CREATE TRACK BRIDGE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
41 CREATE TRACK CROSSING id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
42 CREATE TRACK CROSSOVER id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 START coordinates_delta3 END coordinates_delta4
43 CREATE TRACK CURVE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 ( ( DISTANCE ORIGIN number ) | ( ORIGIN coordinates_delta3 ) )
44 CREATE TRACK END id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
45 CREATE TRACK LAYOUT id1 WITH TRACKS idn+
46 CREATE TRACK ROUNDHOUSE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA ORIGIN coordinates_delta1 ANGLE ENTRY angle1 START angle2 END angle3 WITH integer SPURS LENGTH number1 TURNTABLE LENGTH number2
47 CREATE TRACK STRAIGHT id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
48 CREATE TRACK SWITCH TURNOUT id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) STRAIGHT DELTA START coordinates_delta1 END coordinates_delta2 CURVE DELTA START coordinates_delta3 END coordinates_delta4 DISTANCE ORIGIN number
49 CREATE TRACK SWITCH WYE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 DISTANCE ORIGIN number1 DELTA START coordinates_delta3 END coordinates_delta4 DISTANCE ORIGIN number2
 */

public class Create extends ParserBase{

	public Create(Tokenizer tokens) {
		super(tokens);
	}
	
	public A_Command parse(){
		String token = tokens.getNext();
		if(token == null)
			return tokens.invalidToken();
		if(token.equalsIgnoreCase("POWER")) {
			return createPower();
		}
		else if(token.equalsIgnoreCase("STOCK")) {
			return createStock();
		}
		else if(token.equalsIgnoreCase("TRACK")) {
			return createTrack();
		}
		
		return checkArgs(token);
	}
	
	// Determines which POWER method needs to be called
	public A_Command createPower() {
		String nextToken = tokens.getNext();
		if(nextToken == null)
			return tokens.invalidToken();
		
		if(nextToken.equalsIgnoreCase("CATENARY"))
			return powerCatenary();
		if(nextToken.equalsIgnoreCase("POLE"))
			return powerPole();
		if(nextToken.equalsIgnoreCase("STATION"))
			return powerStation();
		if(nextToken.equalsIgnoreCase("SUBSTATION"))
			return powerSubstation();
		
		return tokens.invalidToken();	
	}
	
	// Determines which STOCK method needs to be called
	public A_Command createStock() {
		String nextToken = tokens.getNext();
		if(nextToken == null)
			return tokens.invalidToken();
	
		if(nextToken.equalsIgnoreCase("CAR"))
			return stockCar();
		if(nextToken.equalsIgnoreCase("ENGINE"))
			return stockEngine();
		
		return tokens.invalidToken();
	}
	
	// Determines which TRACK method needs to be called
	public A_Command createTrack() {
		String nextToken = tokens.getNext();
		if(nextToken == null)
			return tokens.invalidToken();
		if(nextToken.equalsIgnoreCase("BRIDGE"))
			return trackBridge();
		if(nextToken.equalsIgnoreCase("CROSSING"))
			return trackCrossing();
		if(nextToken.equalsIgnoreCase("CROSSOVER"))
			return trackCrossover();
		if(nextToken.equalsIgnoreCase("CURVE"))
			return trackCurve();
		if(nextToken.equalsIgnoreCase("END"))
			return trackEnd();
		if(nextToken.equalsIgnoreCase("LAYOUT"))
			return trackLayout();
		if(nextToken.equalsIgnoreCase("ROUNDHOUSE")) 
			return trackRoundhouse();
		if(nextToken.equalsIgnoreCase("STRAIGHT"))
			return trackStraight();
		if(nextToken.equalsIgnoreCase("SWITCH"))
			return trackSwitch();
		
		return tokens.invalidToken();
	}
	
	// CREATE POWER commands
	
	private A_Command powerCatenary() {
		// 22 CREATE POWER CATENARY id1 WITH POLES idn+
		// When entering this method tokens.getNext() should be id1
	
		//id1
		String id = tokens.getNext();
		if(!Checks.checkID(id, false)) {
			return tokens.invalidToken();
		}
		
		//WITH POLES
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("WITHPOLES")) {
			return tokens.invalidToken();
		}
		
		List<String> poleIds = new ArrayList<String>();

		//idn+
		String currentPoleId;
		for(int i = 6; i < tokens.size(); i++) {
			currentPoleId = tokens.get(i);
			
			if(!Checks.checkID(currentPoleId, false))
				return tokens.invalidToken();
			
			poleIds.add(currentPoleId);
		}
	
		return new CommandCreatePowerCatenary(id, poleIds);
	}
	
	private A_Command powerPole() {
		// 23 CREATE POWER POLE id1 ON TRACK id2 DISTANCE number FROM ( START | END )
		// When entering this method tokens.getNext() should be id1
		String poleId = tokens.getNext(); //id1
		if(!Checks.checkID(poleId, false)) {
			return tokens.invalidToken();
		}
		
		//ON TRACK
		String keywords = tokens.getNext() + tokens.getNext(); 
		if(!keywords.equalsIgnoreCase("ONTRACK")) {
			return tokens.invalidToken();
		}
		
		String trackId = tokens.getNext(); //id2
		if(!Checks.checkID(trackId, false)) {
			return tokens.invalidToken();
		}
		
		//DISTANCE
		if(!tokens.getNext().equalsIgnoreCase("DISTANCE")) { 
			return tokens.invalidToken();
		}
		
		//number
		String distanceFromString = tokens.getNext(); 
		if(!Checks.checkStringIsDouble(distanceFromString)) {
			return tokens.invalidToken();
		}
		Double distanceFrom = Double.parseDouble(distanceFromString);
		
		//FROM
		if(!tokens.getNext().equalsIgnoreCase("FROM")) { 
			return tokens.invalidToken();
		}
		
		//(START | END)
		boolean isFromStart = false;
		String startOrEnd = tokens.getNext(); 
		if(!Checks.checkStringIsOneOfTheseValues(startOrEnd, new String[] {"START", "END"})){
			return tokens.invalidToken();
		}
		
		if(startOrEnd.equalsIgnoreCase("START")) {
			isFromStart = true;
		}
		
		return new CommandCreatePowerPole(poleId, new TrackLocator(trackId, distanceFrom, isFromStart));
	}
	
	private A_Command powerStation() {
		// 24 CREATE POWER STATION id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA coordinates_delta WITH ( SUBSTATION | SUBSTATIONS )idn+
		// When entering this method tokens.getNext() should be id1
		
		//id1
		String id1 = tokens.getNext();
		if(!Checks.checkID(id1, false))
			return tokens.invalidToken();

		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE"))
			return tokens.invalidToken();
		
		//( coordinates_world | ( '$' id2 ) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//DELTA
		if(!tokens.getNext().equalsIgnoreCase("DELTA"))
			return tokens.invalidToken();
		
		//coordinates_delta
		CoordinatesDelta coordinatesDelta = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//WITH
		if(!tokens.getNext().equalsIgnoreCase("WITH"))
			return tokens.invalidToken();
		
		//( SUBSTATION | SUBSTATIONS )
		boolean suborsubs = Checks.booleanFromString(tokens.getNext(), "SUBSTATION", "SUBSTATIONS");
		
		//idn+
		ArrayList<String> ids = new ArrayList<String>();
		String token = tokens.getNext();
		while(token != null) {
			if(Checks.checkID(token, false)) {
				ids.add(token);
				token = tokens.getNext();
			}
		}
		if(ids.size()==0)
			return tokens.invalidToken();
		if(ids.size() > 1 && suborsubs == true)
			return tokens.invalidToken();
		
		return new CommandCreatePowerStation(id1, coordinatesWorld, coordinatesDelta, ids);
	}
	
	private A_Command powerSubstation() {
		// 25 CREATE POWER SUBSTATION id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA coordinates_delta WITH CATENARIES idn+
		// When entering this method tokens.getNext() should be id1
		
		//id1
		String id1 = tokens.getNext();
		if(!Checks.checkID(id1, false))
			return tokens.invalidToken();
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE"))
			return tokens.invalidToken();
		
		//( coordinates_world | ( '$' id2) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//DELTA
		if(!tokens.getNext().equalsIgnoreCase("DELTA"))
			return tokens.invalidToken();
		
		//coordinates_delta
		CoordinatesDelta coordinatesDelta = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//WITH CATENARIES
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("WITHCATENARIES"))
			return tokens.invalidToken();
		
		//idn+
		ArrayList<String> ids = new ArrayList<String>();
		String token = tokens.getNext();
		while(token != null) {
			if(Checks.checkID(token, false)) {
				ids.add(token);
				token = tokens.getNext();
			}
		}
		if(ids.size()==0)
			return tokens.invalidToken();
		
		return new CommandCreatePowerSubstation(id1, coordinatesWorld, coordinatesDelta, ids);
	}
	
	// CREATE STOCK commands
	private A_Command stockCar() {
		String token = tokens.getNext();
		if(!Checks.checkID(token, false))
			return tokens.invalidToken();
		String id = token;
		token = tokens.getNext();
		if(!token.equalsIgnoreCase("AS"))
			return tokens.invalidToken();
		token = tokens.getNext();
		//Switch case changes token to uppercase and then compares it to possible final tokens
		switch(token.toUpperCase()) {
		case "BOX":
			return new CommandCreateStockCarBox(id);
		case "CABOOSE":
			return new CommandCreateStockCarCaboose(id);
		case "FLATBED":
			return new CommandCreateStockCarFlatbed(id);
		case "PASSENGER":
			return new CommandCreateStockCarPassenger(id);
		case "TANK": 
			return new CommandCreateStockCarTank(id);
		case "TENDER":
			return new CommandCreateStockCarTender(id);
		default:
			return tokens.invalidToken();
		}
	}
	
	private A_Command stockEngine() {
		// 34 CREATE STOCK ENGINE id1 AS DIESEL ON TRACK id2 DISTANCE number FROM ( START | END ) FACING ( START | END ) CommandCreateStockEngineDiesel
		// When entering this method, tokens.getNext() should be id1
		String engineId = tokens.getNext(); //id1
		if(!Checks.checkID(engineId, false)) {
			return tokens.invalidToken();
		}
		
		String keywords = tokens.getNext() + tokens.getNext() + tokens.getNext() + tokens.getNext(); //AS DIESEL ON TRACK
		if(!keywords.equalsIgnoreCase("ASDIESELONTRACK")) {
			return tokens.invalidToken();
		}
		
		String trackId = tokens.getNext(); //id2
		if(!Checks.checkID(trackId, false)) {
			return tokens.invalidToken();
		}
		
		if(!tokens.getNext().equalsIgnoreCase("DISTANCE")) { //DISTANCE
			return tokens.invalidToken();
		}
		
		String distanceFromString = tokens.getNext(); //number
		if(!Checks.checkStringIsDouble(distanceFromString)) {
			return tokens.invalidToken();
		}
		Double distance = Double.parseDouble(distanceFromString);
		
		if(!tokens.getNext().equalsIgnoreCase("FROM")) { //FROM
			return tokens.invalidToken();
		}
		
		boolean isFromStart = false;
		String fromDirection = tokens.getNext(); //(START | END)
		if(!Checks.checkStringIsOneOfTheseValues(fromDirection, new String[] {"START", "END"})) {
			return tokens.invalidToken();
		}
		if(fromDirection.equalsIgnoreCase("START")) {
			isFromStart = true;
		}
		
		if(!tokens.getNext().equalsIgnoreCase("FACING")) { //FACING
			return tokens.invalidToken();
		}
		
		boolean isFacingStart = false;
		String facingDirection = tokens.getNext(); //(START | END)
		if(!Checks.checkStringIsOneOfTheseValues(facingDirection, new String[] {"START", "END"})) {
			return tokens.invalidToken();
		}
		if(facingDirection.equalsIgnoreCase("START")) {
			isFacingStart = true;
		}
		
		return new CommandCreateStockEngineDiesel(engineId, new TrackLocator(trackId, distance, isFromStart), isFacingStart);
	}
	
	// CREATE TRACK commands
	
	private A_Command trackBridge() {
		/*
		 * Can create helper methods for TRACK BRIDGE and BRIDGE DRAW
		 * When entering this method, tokens.getNext() should be either "DRAW" or id1
		 * Determine whether TRACK BRIDGE or BRIDGE DRAW and then call the corresponding helper method for DRAW if DRAW. Otherwise handle in this method.
		 * 39 CREATE TRACK BRIDGE DRAW id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 ANGLE angle
		 * 40 CREATE TRACK BRIDGE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
		 */
		
		//If the next token is "DRAW" call trackBridgeDraw(), otherwise next token is id1
		String nextToken = tokens.getNext();
		if(nextToken.equals("DRAW")) {
			return trackBridgeDraw();
		}
		
		//id1
		String bridgeId = nextToken;
		if(!Checks.checkID(bridgeId, false)) {
			return tokens.invalidToken();
		}
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		
		//(coordinates_world | ('$' id2)
		String reference = tokens.getNext();
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(reference, tokens.getParser());
		
		//DELTA START
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta deltaStart = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta2
		CoordinatesDelta deltaEnd = Checks.parseCoordinatesDelta(tokens.getNext());
		
		return new CommandCreateTrackBridgeFixed(bridgeId, new PointLocator(coordinatesWorld, deltaStart, deltaEnd));
	}
	
	// trackBridge helper method
	private A_Command trackBridgeDraw() {
		//39 CREATE TRACK BRIDGE DRAW id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 ANGLE angle
		String nextToken = tokens.getNext();
		String bridgeId = nextToken;
		if(!Checks.checkID(bridgeId, false)) {
			return tokens.invalidToken();
		}
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		//(coordinates_world | ('$' id2)
		String reference = tokens.getNext();
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(reference, tokens.getParser());
		//DELTA START
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		//coordinates_delta1
		CoordinatesDelta deltaStart = Checks.parseCoordinatesDelta(tokens.getNext());
		String next = tokens.getNext();
		System.out.println(next);
		if(!next.equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		CoordinatesDelta deltaEnd = Checks.parseCoordinatesDelta(tokens.getNext());
		next = tokens.getNext();
		if(!next.equalsIgnoreCase("ANGLE")) {
			return tokens.invalidToken();
		}
		double angle;
		next=tokens.getNext();
		try {
		angle = Double.parseDouble(next);
		} catch(Exception e) {
			return tokens.invalidToken();
		}
		return new CommandCreateTrackBridgeDraw(bridgeId, new PointLocator(coordinatesWorld, deltaStart, deltaEnd), new Angle(angle));
	}
	
	private A_Command trackCrossing() {
		// 41 CREATE TRACK CROSSING id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
		// When entering this method, tokens.getNext() should be id1
		
		//id1
		String crossingId = tokens.getNext();
		if(!Checks.checkID(crossingId, false)) {
			return tokens.invalidToken();
		}
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		
		//( coordinates_world | ( '$' ids ) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//DELTA START
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta deltaStart = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta2
		CoordinatesDelta deltaEnd = Checks.parseCoordinatesDelta(tokens.getNext());

		return new CommandCreateTrackCrossing(crossingId, new PointLocator(coordinatesWorld, deltaStart, deltaEnd));
	}
	
	private A_Command trackCrossover() {
		// 42 CREATE TRACK CROSSOVER id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 START coordinates_delta3 END coordinates_delta4
		// When entering this method, tokens.getNext() should be id1
		
		//id1
		String crossoverId = tokens.getNext();
		if(!Checks.checkID(crossoverId, false)) {
			return tokens.invalidToken();
		}
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		
		//( coordinates_world | ( '$' id2 ) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//DELTA START
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta deltaStart1 = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta2
		CoordinatesDelta deltaEnd1 = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//START
		if(!tokens.getNext().equalsIgnoreCase("START")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta3
		CoordinatesDelta deltaStart2 = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta4
		CoordinatesDelta deltaEnd2 = Checks.parseCoordinatesDelta(tokens.getNext());
		
		return new CommandCreateTrackCrossover(crossoverId, coordinatesWorld, deltaStart1, deltaEnd1, deltaStart2, deltaEnd2);
	}
	
	private A_Command trackCurve() {
		// 43 CREATE TRACK CURVE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 ( ( DISTANCE ORIGIN number ) | ( ORIGIN coordinates_delta3 ) )
		// When entering this method, tokens.getNext() should be id1

		//id1
		String curveId = tokens.getNext();
		if(!Checks.checkID(curveId, false)) {
			return tokens.invalidToken();
		}
		
		System.out.println(curveId);
		
		//REFERENCE
		String nextToken = tokens.getNext();
		if(!nextToken.equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		System.out.println(nextToken);
		
		//( coordinates_world | ( '$' id2 ) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		System.out.println(coordinatesWorld);
		//DELTA START
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta deltaStart = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta2
		CoordinatesDelta deltaEnd = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//( ( DISTANCE ORIGIN number ) | (ORIGIN coordinates_delta3 ) )
		String currentToken = tokens.getNext();
		if(!Checks.checkStringIsOneOfTheseValues(currentToken, new String[] {"DISTANCE", "ORIGIN"})) {
			return tokens.invalidToken();
		}
		
		if(currentToken.equalsIgnoreCase("DISTANCE")) {
			if(!tokens.getNext().equalsIgnoreCase("ORIGIN")) {
				return tokens.invalidToken();
			}
			double number = Double.parseDouble(tokens.getNext());
			
			return new CommandCreateTrackCurve(curveId, coordinatesWorld, deltaStart, deltaEnd, number);
			
		}
		
		CoordinatesDelta deltaOrigin = Checks.parseCoordinatesDelta(tokens.getNext());

		return new CommandCreateTrackCurve(curveId, coordinatesWorld, deltaStart, deltaEnd, deltaOrigin);
	}
	
	private A_Command trackEnd() {
		// 44 CREATE TRACK END id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
		// When entering this method, tokens.getNext() should be id1
		
		//id1
		String endId = tokens.getNext();
		if(!Checks.checkID(endId, false)) {
			return tokens.invalidToken();
		}
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		
		//( coordinates_word | ( '$' id2 ) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//DELTA START
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta deltaStart = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta2
		CoordinatesDelta deltaEnd = Checks.parseCoordinatesDelta(tokens.getNext());

		return new CommandCreateTrackEnd(endId, new PointLocator(coordinatesWorld, deltaStart, deltaEnd));
	}
	
	private A_Command trackLayout() {
		// 45 CREATE TRACK LAYOUT id1 WITH TRACKS idn+ => CommandCreateTrackLayout
		// When entering this method, tokens.getNext() should be id1
		
		//id1
		String trackLayoutId = tokens.getNext(); 
		if(!Checks.checkID(trackLayoutId, false)) {
			return tokens.invalidToken();
		}
		
		//WITH TRACKS
		if(!(tokens.getNext() + tokens.getNext()).equalsIgnoreCase("WITHTRACKS")) { 
			return tokens.invalidToken();
		}
		
		List<String> trackIds = new ArrayList<String>();
		
		//idn+
		String currentTrackId;
		for(int i = 6; i < tokens.size(); i++) { 
			currentTrackId = tokens.get(i);
			
			if(!Checks.checkID(currentTrackId, false))
				return tokens.invalidToken();
			trackIds.add(currentTrackId);
		}
		
		return new CommandCreateTrackLayout(trackLayoutId, trackIds);
	}
	
	private A_Command trackRoundhouse() {
		// 46 CREATE TRACK ROUNDHOUSE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA ORIGIN coordinates_delta1 ANGLE ENTRY angle1 START angle2 END angle3 WITH integer SPURS LENGTH number1 TURNTABLE LENGTH number2
		// When entering this method, tokens.getNext() should be id1
		
		//id1
		String roundhouseId = tokens.getNext();
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		
		//( coordinates_world | ( '$' id2 ) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//DELTA ORIGIN
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTAORIGIN")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta deltaOrigin = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//ANGLE ENTRY
		keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("ANGLEENTRY")) {
			return tokens.invalidToken();
		}
		
		//angle1
		Angle entryAngle = new Angle(Double.parseDouble(tokens.getNext()));
		
		//START
		if(!tokens.getNext().equalsIgnoreCase("START")) {
			return tokens.invalidToken();
		}
		
		//angle2
		Angle startAngle = new Angle(Double.parseDouble(tokens.getNext()));
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//angle3
		Angle endAngle = new Angle(Double.parseDouble(tokens.getNext()));
		
		//WITH
		if(!tokens.getNext().equalsIgnoreCase("WITH")) {
			return tokens.invalidToken();
		}
		
		//integer
		int numberSpurs = Integer.parseInt(tokens.getNext());
		
		//SPURS LENGTH
		keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("SPURSLENGTH")) {
			return tokens.invalidToken();
		}
		
		//number1
		Double spurLength = Double.parseDouble(tokens.getNext());
		
		//TURNTABLE LENGTH
		keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("TURNTABLELENGTH")) {
			return tokens.invalidToken();
		}
		
		//number2
		Double turntableLength = Double.parseDouble(tokens.getNext());

		return new CommandCreateTrackRoundhouse(roundhouseId, coordinatesWorld, deltaOrigin, entryAngle, startAngle, endAngle, numberSpurs, spurLength, turntableLength);
	}
	
	private A_Command trackStraight() {
		// 47 CREATE TRACK STRAIGHT id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
		// When entering this method, tokens.getNext() should be id1
		String trackId = tokens.getNext();
		if(!Checks.checkID(trackId, false)) {
			return tokens.invalidToken();
		}
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		
		//( coordinates_world | ( 'S' id2 ) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//DELTA START
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta deltaStart = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta2
		CoordinatesDelta deltaEnd = Checks.parseCoordinatesDelta(tokens.getNext());
		
		return new CommandCreateTrackStraight(trackId, new PointLocator(coordinatesWorld, deltaStart, deltaEnd));
	}
	
	private A_Command trackSwitch() {
		/*
		 * Can create helper methods for TRACK SWITCH TURNOUT and TRACK SWITCH WYE
		 * When entering this method, tokens.getNext() should be either "TURNOUT" or "WYE", then call the corresponding helper method
		 * 48 CREATE TRACK SWITCH TURNOUT id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) STRAIGHT DELTA START coordinates_delta1 END coordinates_delta2 CURVE DELTA START coordinates_delta3 END coordinates_delta4 DISTANCE ORIGIN number
		 * 49 CREATE TRACK SWITCH WYE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 DISTANCE ORIGIN number1 DELTA START coordinates_delta3 END coordinates_delta4 DISTANCE ORIGIN number2
		 */
		
		//TURNOUT or WYE
		String currentToken = tokens.getNext();
		if(!Checks.checkStringIsOneOfTheseValues(currentToken, new String[] {"TURNOUT", "WYE"})) {
			return tokens.invalidToken();
		}
		
		//TURNOUT
		if(currentToken.equalsIgnoreCase("TURNOUT")) {
			return trackSwitchTurnout();
		}
		
		//WYE
		return trackSwitchWye();
		
	}
	
	// trackSwitch helper methods
	
	private A_Command trackSwitchTurnout() {
		//48 CREATE TRACK SWITCH TURNOUT id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) STRAIGHT DELTA START coordinates_delta1 END coordinates_delta2 CURVE DELTA START coordinates_delta3 END coordinates_delta4 DISTANCE ORIGIN number
		
		//id1
		String turnoutId = tokens.getNext();
		if(!Checks.checkID(turnoutId, false)) {
			return tokens.invalidToken();
		}
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		
		//( coordinates_world | ( '$' id2 ) ) 
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//STRAIGHT DELTA START
		String keywords = tokens.getNext() + tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("STRAIGHTDELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta straightDeltaStart = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta2
		CoordinatesDelta straightDeltaEnd = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//CURVE DELTA START
		keywords = tokens.getNext() + tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("CURVEDELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta3
		CoordinatesDelta curveDeltaStart = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta4
		CoordinatesDelta curveDeltaEnd = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//DISTANCE ORIGIN
		keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DISTANCEORIGIN")) {
			return tokens.invalidToken();
		}
		
		//number
		Double distance = Double.parseDouble(tokens.getNext());
		
		CoordinatesDelta curveDeltaOrigin = ShapeArc.calculateDeltaOrigin(coordinatesWorld, curveDeltaStart, curveDeltaEnd, distance);
		
		return new CommandCreateTrackSwitchTurnout(turnoutId, coordinatesWorld, straightDeltaStart, straightDeltaEnd, curveDeltaStart, curveDeltaEnd, curveDeltaOrigin);
	}
	
	private A_Command trackSwitchWye() {
		//49 CREATE TRACK SWITCH WYE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2 DISTANCE ORIGIN number1 DELTA START coordinates_delta3 END coordinates_delta4 DISTANCE ORIGIN number2

		//id1
		String wyeId = tokens.getNext();
		if(!Checks.checkID(wyeId, false)) {
			return tokens.invalidToken();
		}
		
		//REFERENCE
		if(!tokens.getNext().equalsIgnoreCase("REFERENCE")) {
			return tokens.invalidToken();
		}
		
		//( coordinates_world | ( 'S' id2 ) )
		CoordinatesWorld coordinatesWorld = Checks.parseCoordinatesWorld(tokens.getNext(), tokens.getParser());
		
		//DELTA START
		String keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta1
		CoordinatesDelta deltaStart1 = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta2
		CoordinatesDelta deltaEnd1 = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//DISTANCE ORIGIN
		keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DISTANCEORIGIN")) {
			return tokens.invalidToken();
		}
		
		//number1
		CoordinatesDelta deltaOrigin1 = ShapeArc.calculateDeltaOrigin(coordinatesWorld, deltaStart1, deltaEnd1, Double.parseDouble(tokens.getNext()));
		
		//DELTA START
		keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DELTASTART")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta3
		CoordinatesDelta deltaStart2 = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//END
		if(!tokens.getNext().equalsIgnoreCase("END")) {
			return tokens.invalidToken();
		}
		
		//coordinates_delta4
		CoordinatesDelta deltaEnd2 = Checks.parseCoordinatesDelta(tokens.getNext());
		
		//DISTANCE ORIGIN
		keywords = tokens.getNext() + tokens.getNext();
		if(!keywords.equalsIgnoreCase("DISTANCEORIGIN")) {
			return tokens.invalidToken();
		}
		
		//number2
		CoordinatesDelta deltaOrigin2 = ShapeArc.calculateDeltaOrigin(coordinatesWorld, deltaStart2, deltaEnd2, Double.parseDouble(tokens.getNext()));
		
		return new CommandCreateTrackSwitchWye(wyeId, coordinatesWorld, deltaStart1, deltaEnd1, deltaOrigin1, deltaStart2, deltaEnd2, deltaOrigin2);
	}
}
