package input;

import routing.M2MShareRouter;
import routing.m2mShare.FileRequest;
import core.DTNHost;
import core.Message;
import core.World;

public class M2MShareQueryCreationEvent extends ExternalEvent {
	
	
	private static final long serialVersionUID = -5314237059901818199L;
	/** address of the node which want to search a file */
	protected int fromAddr;
	/** name of the file the query refers to */
	protected String filename;
	
	public M2MShareQueryCreationEvent(double time, int fromAddr, String filename) {
		super(time);
		this.fromAddr = fromAddr;
		this.filename = filename;
	}
	
	/**
	 * Creates the query this event represents. 
	 */
	@Override
	public void processEvent(World world) {
		System.err.println("Sto processanto la query");
		DTNHost host = world.getNodeByAddress(this.fromAddr);
		if(!(host.getRouter() instanceof M2MShareRouter)){
			/* M2MShareQuery works only with M2MShareRouter */
			return;
		}
		
		FileRequest query = new FileRequest(fromAddr, filename, time);
		((M2MShareRouter)host.getRouter()).addM2MQuery(query);		
		
	}

	@Override
	public String toString() {
		return "Query @" + this.time + ": from host " + fromAddr + " - search "+ filename;
	}
	
	

}
