package input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import core.Settings;
import core.SimError;


public class M2MShareFileRequestReader implements EventQueue {

	private static final String FILE_PATH_S = "queryFile";

	/** file request generator's rng seed -setting id ({@value})*/
	public static final String RNG_SEED = "rngSeed";
	private String filePath;
	private List<M2MShareFileRequestEvent> queries;
	private Scanner scanner;
	private int nextEventIndex;
	private Random rng; 
	

	public M2MShareFileRequestReader(Settings s){
		this.filePath = s.getSetting(FILE_PATH_S);
		
		try {
			this.scanner = new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			throw new SimError(e.getMessage(),e);
		}
		
		if (s.contains(RNG_SEED)) {
			int seed = s.getInt(RNG_SEED);
			rng = new Random(seed);
		}
		else {
			rng = new Random(0);
		}
		
		queries = readQueries();
		this.nextEventIndex = 0;
		System.out.println("Queries to generate: "+queries.size());
	}

	/** 
	 * Returns the next message creation event
	 * @see input.EventQueue#nextEvent()
	 */
	@Override
	public ExternalEvent nextEvent() {
		if (queries.size() == 0 || nextEventIndex>= queries.size()) { // no more events
			return new ExternalEvent(Double.MAX_VALUE);
		}
		ExternalEvent ee = queries.get(nextEventIndex);
		nextEventIndex++;
		return ee;
	}

	/**
	 * Returns next message creation event's time
	 * @see input.EventQueue#nextEventsTime()
	 */
	@Override
	public double nextEventsTime() {
		if (queries.size() == 0 || nextEventIndex>= queries.size()) {
			// in case user request time of an event that doesn't exist
			return Double.MAX_VALUE;
		}
		else {
			return queries.get(nextEventIndex).getTime();
		}
	}
	
	public List<M2MShareFileRequestEvent> readQueries() {
		ArrayList<M2MShareFileRequestEvent> queriesList = new ArrayList<M2MShareFileRequestEvent>();
		int queriesRead = 0;
		// skip empty and comment lines
		Pattern skipPattern = Pattern.compile("(#.*)|(^\\s*$)");

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Scanner lineScan = new Scanner(line);
			if (skipPattern.matcher(line).matches()) {
				// skip empty and comment lines
				continue;
			}
			
			double time=0;
			int fromAddr;
			String filename;			
					
			try {
				time = lineScan.nextDouble();
				
				
				fromAddr = lineScan.nextInt();	
				filename = lineScan.next();
				
				queriesList.add(new M2MShareFileRequestEvent
						(time, fromAddr, filename));


				// discard the newline in the end
				if (lineScan.hasNextLine()) {
					lineScan.nextLine(); // TODO: test
				}
				queriesRead++;				
			} catch(InputMismatchException e){
				@SuppressWarnings("unused")
				String type = lineScan.next();
				
				time = lineScan.nextDouble();
				int group = lineScan.nextInt();
				int hostID = rng.nextInt(1024);
				//System.err.println(hostID + " in group "+group+" - "+(hostID % 50));
				
					
				filename = lineScan.next();

				queriesList.add(new M2MShareFileRequestEvent
						(time, hostID, filename, group));


				// discard the newline in the end
				if (lineScan.hasNextLine()) {
					lineScan.nextLine(); // TODO: test
				}
				queriesRead++;
			} catch (Exception e) {
				throw new SimError("Can't parse M2MShare query creation event " + 
						(queriesRead+1) + " from '" + line + "'", e);
			}
		}
		this.scanner.close();
		return queriesList;
	}

	

}
