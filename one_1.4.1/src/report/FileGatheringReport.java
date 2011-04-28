package report;

import core.DTNHost;
import core.FileEventListener;

public class FileGatheringReport extends Report implements FileEventListener{

	public static String HEADER="# time  event";
	private int created;
	private int satisfied;
	private int delegated;
	private double timeSatisfied;
	
	/**
	 * Constructor.
	 */
	public FileGatheringReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		created = 0;
		satisfied = 0;
		delegated = 0;
		timeSatisfied = 0;
		write(HEADER);
	}
	
	
	@Override
	public void done() {
		write("");
		write(created + " file requests created");
		write(delegated + " times a file requests has been delegated");
		write(satisfied + " file requests satisfied (in "+ format(timeSatisfied) + " s)");
		super.done();
	}

	@Override
	public void newFileRequest(DTNHost where, String filehash) {
		if (!isWarmup()) {
			created++;
			write(format(getSimTime()) + " - query created in " + where + " (" + filehash + ")");
		}
	}

	@Override
	public void fileRequestDelegated(DTNHost from, DTNHost to, String filehash) {
		if (!isWarmup()) {
			delegated++;
			write(format(getSimTime()) + " query delegated from " + from + " to " + to);
		}
	}

	@Override
	public void fileRequestExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		write(format(getSimTime()) + " - file request expired in " + 
				where + " (" + filehash + " requested by "+requestor + ")");					
	}

	@Override
	public void fileRequestSatisfied(DTNHost where, String filehash,
			boolean selfSatisfied) {
		if (!isWarmup()) {
			satisfied++;
			timeSatisfied = getSimTime();
			if(selfSatisfied){
				write(format(getSimTime()) + " - file request satisfied in " + 
						where + " without delegation (" + filehash + ")");					
			}
			else{
				write(format(getSimTime()) + " - file request satisfied in " + 
						where + " using delegation  (" + filehash + ")");
			}
			
		}
	}
	
}
