package report;

import core.DTNHost;
import core.FileEventListener;
import core.SimClock;
import core.SimScenario;

public class FileGatheringReport extends Report implements FileEventListener{

	private int created;
	private int satisfied;
	private int delegated;
	private int downExpired;
	private int downCompleted;
	private int fwdExpired;
	private double timeSatisfied;
	private int fwdReturned;
	private long startTime;
	private long totalData;
	
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
		downExpired = 0;
		fwdExpired = 0;
		fwdReturned = 0;
		downCompleted = 0;
		startTime = System.currentTimeMillis();
	}
	
	
	@Override
	public void done() {
		write("Total data:\t" + totalData);
		write("VirtualFile created:\t" + created);
		write("VirtualFile delegated:\t" + delegated);
		write("PendingDownloads completed:\t" + downCompleted);
		write("PendingDownloads expired:\t" + downExpired);
		write("DownloadFWDs expired:\t" + fwdExpired);
		write("DownloadFWDs returned:\t" + fwdReturned);		
		write("VirtualFile completed:\t" + satisfied);
		write("First VirtualFile satisfied:\t" + timeSatisfied);
		write("Simulated time:\t" + SimClock.getIntTime());	
		double duration = (System.currentTimeMillis() - startTime)/1000.0;
		write("Simulation time:\t" + String.format("%.2f", duration));
		super.done();
	}

	@Override
	public void newVirtualFile(DTNHost where, String filehash) {
		created++;
	}

	@Override
	public void fileRequestDelegated(DTNHost from, DTNHost to, String filehash) {
		delegated++;
	}

	@Override
	public void pendingDownloadExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		downExpired++;	
		checkCompleted();
	}

	@Override
	public void fileRequestSatisfied(DTNHost where, String filehash,
			boolean selfSatisfied) {
		satisfied++;
		timeSatisfied = getSimTime();
		checkCompleted();
	}

	@Override
	public void downloadFWDExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		fwdExpired++;
		checkCompleted();
	}

	@Override
	public void dataTransferred(DTNHost from, DTNHost to, int bytes) {
		totalData += bytes; 
	}

	@Override
	public void downloadFWDReturned(DTNHost from, DTNHost requestor,
			String filehash) {
		fwdReturned++;
		checkCompleted();
	}

	@Override
	public void pendingDownloadCompleted(DTNHost where, DTNHost requestor,
			String filehash) {
		downCompleted++;	
	}
	
	private void checkCompleted(){
		boolean complete = (satisfied > 0) &&
			(fwdReturned + fwdExpired + downExpired >= delegated);
		if(complete){
			SimScenario.getInstance().getWorld().cancelSim();
		}
	}

	@Override
	public void dataRedundancyUpdated(DTNHost where, int bytes) {}
	
}
