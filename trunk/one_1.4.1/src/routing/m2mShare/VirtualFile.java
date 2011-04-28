package routing.m2mShare;

import java.util.HashMap;

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
			map = new IntervalMap();
		}
		else{
			int fileBytes = file.getSize();
			map = new IntervalMap(fileBytes, 1024);
		}	
	}
	
	@Override
	public void execute(Executor executor) {	
		System.err.println(SimClock.getTime()+" attivato VirtualFile");
		setActive();
		int serversFound = 0;
		HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();		
		for(DTNHost host: neighbours.keySet()){		
			boolean hasFile = host.getFileSystem().hasFile(fileHash);
			if(hasFile){
				
				if(executor.addCommunicator(neighbours.get(host))){
					serversFound++;
				}
				/*
				DTNFile file = host.getFileSystem().getFile(fileHash);
				myRouter.getHost().getFileSystem().addToFiles(file);
				System.err.println(myRouter.getHost()+" - trovato da solo il file cercato");
				myRouter.notifyQuerySatisfied(fileHash, true);
				setCompleted();*/
			}
		}	
		if(serversFound > 0){
			//still running for transfer
			return;
		}
		setIncomplete();
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
		int fileBytes = map.mapSize();
		return byteTransferred >= fileBytes;
	}
}
