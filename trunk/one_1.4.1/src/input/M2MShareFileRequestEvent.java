package input;

import routing.M2MShareRouter;
import routing.m2mShare.FileRequest;
import core.DTNHost;
import core.SettingsError;
import core.SimScenario;
import core.World;

public class M2MShareFileRequestEvent extends ExternalEvent {
	
	
	private static final long serialVersionUID = -5314237059901818199L;
	/** address of the node which want to search a file */
	protected int fromAddr;
	/** name of the file the file request refers to */
	protected String filename;
	/** name of the file the file request refers to */
	protected int group;
	
	public M2MShareFileRequestEvent(double time, int fromAddr, String filename) {
		super(time);
		this.fromAddr = fromAddr;
		this.filename = filename;
		this.group = -1;
	}
	
	public M2MShareFileRequestEvent(double time, int hostID, String filename,
			int group) {
		super(time);
		this.fromAddr = hostID;
		this.filename = filename;
		this.group = group;
	}

	/**
	 * Creates the query this event represents. 
	 */
	@Override
	public void processEvent(World world) {
		DTNHost host;
		if(group == -1){
			/* host address defined in settings */
			host = world.getNodeByAddress(this.fromAddr);
		}else{
			/* host address randomly chosen in a group */
			int[] groupSizes = SimScenario.getInstance().getGroupSizes();
			int finalAddr = this.fromAddr % groupSizes[this.group-1];
			for(int i=0; i<this.group-1; i++){
				finalAddr += groupSizes[i];
			}
			this.fromAddr = finalAddr;
			host = world.getNodeByAddress(finalAddr);
		}
		
		if(!(host.getRouter() instanceof M2MShareRouter)){
			/* M2MShareFileRequest works only with M2MShareRouter */
			throw new SettingsError(host + " is not using M2MShareRouter");
		}
		
		FileRequest request = new FileRequest(fromAddr, filename, time);
		((M2MShareRouter)host.getRouter()).addFileRequest(request);		
		
	}

	@Override
	public String toString() {
		return "Query @" + this.time + ": from host " + fromAddr + " - search "+ filename;
	}
	
	

}
