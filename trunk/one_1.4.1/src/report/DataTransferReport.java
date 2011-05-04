package report;

import core.DTNHost;
import core.FileEventListener;

public class DataTransferReport extends Report implements FileEventListener {
	
	public static String HEADER="# time  from  to  bytesTrasferred";
	private long totalData;
	
	/**
	 * Constructor.
	 */
	public DataTransferReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		totalData = 0;
		write(HEADER);
	}
	
	@Override
	public void done() {
		write("");
		write("Total data trasferred (bytes): "+ totalData);
		super.done();
	}	

	@Override
	public void dataTransferred(DTNHost from, DTNHost to, int bytes) {
		if (!isWarmup()) {
			totalData += bytes; 
			write(format(getSimTime()) + " " + from + " " + to + " " + bytes);
		}		
	}

	@Override
	public void newVirtualFile(DTNHost where, String filehash) {}

	@Override
	public void fileRequestDelegated(DTNHost from, DTNHost to, String filehash) {}

	@Override
	public void pendingDownloadExpired(DTNHost where, DTNHost requestor,
			String filehash) {}

	@Override
	public void downloadFWDExpired(DTNHost where, DTNHost requestor,
			String filehash) {}

	@Override
	public void fileRequestSatisfied(DTNHost where, String filehash,
			boolean selfSatisfied) {}

}
