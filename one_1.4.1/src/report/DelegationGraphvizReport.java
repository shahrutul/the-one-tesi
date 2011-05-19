package report;

import core.DTNHost;
import core.FileEventListener;

public class DelegationGraphvizReport extends Report implements
		FileEventListener {
	/** Name of the graphviz report ({@value})*/
	public static final String GRAPH_NAME = "delegationGraph";
	
	@Override
	public void init() {
		super.init();
		write("digraph " + GRAPH_NAME + " {");
		write("rankdir = LR");
		setPrefix("\t"); // indent following lines by one tab
	}

	@Override
	public void newVirtualFile(DTNHost where, String filehash) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fileRequestDelegated(DTNHost from, DTNHost to, String filehash) {
		write(from + "->" + to + ";");
	}
	
	
	public void done() {
				
		setPrefix(""); // don't indent anymore
		write("}");
		
		super.done();
	}
	

	@Override
	public void pendingDownloadExpired(DTNHost where, DTNHost requestor,
			String filehash) {}

	@Override
	public void pendingDownloadCompleted(DTNHost where, DTNHost requestor,
			String filehash) {
		write(where+"[color = green, shape = diamond]");
	}

	@Override
	public void downloadFWDExpired(DTNHost where, DTNHost requestor,
			String filehash) {}

	@Override
	public void downloadFWDReturned(DTNHost from, DTNHost requestor,
			String filehash) {
		write(from + "->" + requestor + "[color = green];");
	}

	@Override
	public void dataTransferred(DTNHost from, DTNHost to, int bytes) {}

	@Override
	public void fileRequestSatisfied(DTNHost where, String filehash,
			boolean selfSatisfied) {}

}
