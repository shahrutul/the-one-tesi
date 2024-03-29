package routing.m2mShare;

import java.util.HashMap;
import java.util.Vector;

import core.Connection;
import core.DTNFile;
import core.DTNHost;
import core.SimScenario;
import routing.M2MShareRouter;
import routing.m2mShare.BroadcastModule.Pair;

public class VirtualFile extends DTNActivity {

	private String fileHash;
	private String filename;
	private int fromAddr;
	private double creationTime;
	private M2MShareRouter myRouter;
	private IntervalMap map;
	private boolean selfSatisfied;
	
	public VirtualFile(FileRequest request, M2MShareRouter m2mShareRouter) {
		this.filename = request.getFilename();
		this.fileHash = DTNFile.hashFromFilename(request.getFilename());
		this.fromAddr = request.getFromAddr();
		this.creationTime = request.getCreationTime();
		this.myRouter = m2mShareRouter;
		setID(myRouter.getNextId());
		this.selfSatisfied = true;
		
		DTNFile file = SimScenario.getInstance().getFileGenerator().getFile(fileHash);
		if(file == null){
			map = new IntervalMap(1000000, 1024);
		}
		else{
			int fileBytes = file.getSize();
			map = new IntervalMap(fileBytes-1, 1024);
		}		
	}
	
	@Override
	public void execute(Executor executor) {	
		int communicatorActivated = 0;
		setActive();
		if(myRouter.useBroadcastModule()){
		Vector<Pair<DTNHost, Connection>> servers = myRouter.broadcastQuery(fileHash);
		
		if(servers != null){
			for(Pair<DTNHost, Connection> server:servers){
				try {
					executor.addCommunicator(server.getSecond(), server.getFirst(), fileHash, map.cut(myRouter.getFileDivisionStrategyType()));
					communicatorActivated++;
				} catch (Exception e) {
					//nothing to download
				}
			}
		}
		}
		else{
			HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();		
			for(DTNHost host: neighbours.keySet()){	
				if(host.getFileSystem().hasFile(fileHash) && executor.moreCommunicatorsAvailable()){
					//compatibleHostsConns.add(neighbours.get(host));	
					try {
						executor.addCommunicator(neighbours.get(host), host, fileHash, map.cut(myRouter.getFileDivisionStrategyType()));
						communicatorActivated++;
					} catch (Exception e) {
						//nothing to download
					}
				}
			}
		}

		/*
		//Vector<Connection> compatibleHostsConns = new Vector<Connection>();
			*/
		
		/*
		if(compatibleHostsConns.size() == 0 || executor.getAvailableCommunicators()==0){
			//no host found or no Communicator available
			setIncomplete();
			return;
		}
		int availableCommunicators = Math.min(compatibleHostsConns.size(), executor.getAvailableCommunicators());				
		int[] startingPoints = map.getStartingPoints(availableCommunicators);
		
		for(int i=0; i< availableCommunicators; i++){
			executor.addCommunicator(compatibleHostsConns.get(i),startingPoints[i], 
					map.getMinFreeSpace(startingPoints[i]), map.getTotalFreeSpace());
		}
		*/
		if(communicatorActivated == 0){
			setIncomplete();
		}
		// else still running for transfer
		// status == Active
		
	}

	public String getFileHash() {
		return fileHash;
	}

	public int getFromAddr() {
		return fromAddr;
	}

	public double getCreationTime() {
		return creationTime;
	}
	
	public void setSelfSatisfied(boolean self){
		this.selfSatisfied = self;
	}
	
	@Override
	public void addTransferredData(int[] intervals, DTNHost from){
		int bytesTrasferred = 0;
		try {
			for(int i=0; i<intervals.length-1; i+=2){
				map.update(intervals[i], intervals[i+1]);
				bytesTrasferred += intervals[i+1]-intervals[i]+1;
			}	
			System.err.println(myRouter.getHost() +" In VirtualFile Mappa aggiornata: "+map);			
			
		} catch (Exception e) {}
		myRouter.notifyDataTransferred(from, myRouter.getHost(), bytesTrasferred);
		if(map.mapSize() == 0){
			createFile();
			System.err.println(myRouter.getHost() +" virtualFile completa");
			setCompleted();				
		}
	}

	private void createFile() {
		DTNFile file = new DTNFile(filename, fileHash, map.getEndInterval());
		if(!myRouter.getHost().getFileSystem().hasFile(fileHash)){
			myRouter.getHost().getFileSystem().addToFiles(file);
		}		
	}

	@Override
	public int[] getRestOfMap() {
		if(map == null){
			return null;
		}
		return map.assignRestofMap();
	}

	@Override
	public void setCompleted() {
		super.setCompleted();
		myRouter.notifyfileRequestSatisfied(fileHash, selfSatisfied);
	}

	public int[] getMapForDelegation(int fileDivisionStrategyType) {
		try {
			return map.cut(fileDivisionStrategyType);
		} catch (Exception e) {
			return null;
		}
	}
	
	
}
