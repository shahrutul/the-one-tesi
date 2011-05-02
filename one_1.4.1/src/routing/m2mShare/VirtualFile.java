package routing.m2mShare;

import java.util.HashMap;
import java.util.Vector;

import core.Connection;
import core.DTNFile;
import core.DTNHost;
import core.SimClock;
import core.SimScenario;
import routing.M2MShareRouter;

public class VirtualFile extends DTNActivity {

	private String fileHash;
	private int fromAddr;
	private double creationTime;
	private M2MShareRouter myRouter;
	private IntervalMap map;
	
	public VirtualFile(M2MShareQuery m2mQuery, M2MShareRouter m2mShareRouter) {
		this.fileHash = DTNFile.hashFromFilename(m2mQuery.getFilename());
		this.fromAddr = m2mQuery.getFromAddr();
		this.creationTime = m2mQuery.getCreationTime();
		this.myRouter = m2mShareRouter;
		DTNFile file = SimScenario.getInstance().getFileGenerator().getFile(fileHash);
		if(file == null){
			map = new IntervalMap(1000000, 1024);
		}
		else{
			int fileBytes = file.getSize();
			map = new IntervalMap(fileBytes, 1024);
		}		
	}
	
	@Override
	public void execute(Executor executor) {	
		int communicatorActivated = 0;
		System.err.println(SimClock.getTime()+" attivato VirtualFile");
		setActive();
		//Vector<Connection> compatibleHostsConns = new Vector<Connection>();
		HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();		
		for(DTNHost host: neighbours.keySet()){	
			if(host.getFileSystem().hasFile(fileHash) && executor.moreCommunicatorsAvailable()){
				//compatibleHostsConns.add(neighbours.get(host));	
				try {
					executor.addCommunicator(neighbours.get(host),map.cut(false));
				} catch (Exception e) {
					//nothing to download
				}
			}
		}	
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

	@Override
	public boolean isComplete(int byteTransferred) {
		map.insertData(0, byteTransferred);
		System.err.println("Mappa aggiornata: "+map);
		int fileBytes = map.mapSize();
		return byteTransferred >= fileBytes;
	}
}
