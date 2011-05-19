
package routing;

import java.util.Map;
import java.util.Vector;

import routing.m2mShare.BroadcastModule;
import routing.m2mShare.BroadcastModule.Pair;
import routing.m2mShare.DTNActivity;
import routing.m2mShare.DTNDownloadFwd;
import routing.m2mShare.DTNPendingDownload;
import routing.m2mShare.DTNScheduler;
import routing.m2mShare.DTNPresenceCollector;
import routing.m2mShare.IdGenerator;
import routing.m2mShare.M2MShareQuery;
import routing.m2mShare.QueuingCentral;
import routing.m2mShare.VirtualFile;

import core.Connection;
import core.DTNFile;
import core.DTNHost;
import core.Settings;
import core.SimClock;
import core.SimScenario;

/**
 * Implementation of M2MShare router as described in 
 * <I></I> by
 * 
 */
public class M2MShareRouter extends ActiveRouter {
	
	
	/** M2MShare router's setting namespace ({@value})*/ 
	public static final String M2MSHARE_NS = "M2MShareRouter";
	/**
	 * Minimum times a host must be met before delegating -setting id ({@value}).
	 * Default value for setting is {@link #FREQUENCY_THRESHOLD}.*/
	public static final String FREQUENCY_THRESHOLD_S ="frequencyThreshold";
	
	public static final String SCAN_FREQUENCY_S = "scanFrequency";
	public static final String DELEGATION_DEPTH_S = "delegationDepth";
	public static final String DELEGATION_TYPE_S = "delegationType";
	public static final String FILE_DIVISION_TYPE_S = "fileDivisionType";
	private static final String STOP_SIMULATION_S = "stopOnFirstQuerySatisfied";
	
	public static final int FREQUENCY_THRESHOLD = 2;
	public static final int SCAN_FREQUENCY = 10;
	
	
	private int frequencyThreshold;
	private boolean useDelegation;
	private boolean delegateToAll;

		
	private DTNPresenceCollector presenceCollector;
	private int scanFrequency;
	private boolean stopOnFirstQuerySatisfied;
	private DTNScheduler scheduler;
	private QueuingCentral queuingCentral;
	private BroadcastModule broadcastModule;
	private int delegationDepth;
	private IdGenerator idGenerator;
	private int fileDivisionStrategyType;
	
	
	/**
	 * Constructor. Creates a new message router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public M2MShareRouter(Settings s) {
		super(s);
		Settings M2MShareSettings = new Settings(M2MSHARE_NS);
		if (M2MShareSettings.contains(FREQUENCY_THRESHOLD_S)) {
			frequencyThreshold = M2MShareSettings.getInt(FREQUENCY_THRESHOLD_S);
		}
		else {
			//System.out.println(FREQUENCY_THRESHOLD_S + " setting not found. Used default "
			//		+FREQUENCY_THRESHOLD_S+ " = " + FREQUENCY_THRESHOLD);
			frequencyThreshold = FREQUENCY_THRESHOLD;
		}
		
		if (M2MShareSettings.contains(SCAN_FREQUENCY_S)) {
			scanFrequency = M2MShareSettings.getInt(SCAN_FREQUENCY_S);
		}
		else {
			scanFrequency = SCAN_FREQUENCY;
		}
		
		int delegationType;
		if (M2MShareSettings.contains(DELEGATION_TYPE_S)) {
			delegationType = M2MShareSettings.getInt(DELEGATION_TYPE_S);
		}
		else {
			delegationType = 1;
		}
		switch (delegationType) {
		case 0:
			useDelegation = false;
			delegateToAll = false;
			break;
			
		case 1:
			useDelegation = true;
			delegateToAll = false;
			break;
			
		case 2:
			useDelegation = true;
			delegateToAll = true;
			break;

		default:
			useDelegation = true;
			delegateToAll = false;
			break;
		}
		
		if (M2MShareSettings.contains(FILE_DIVISION_TYPE_S)) {
			fileDivisionStrategyType = M2MShareSettings.getInt(FILE_DIVISION_TYPE_S);
		}
		else {
			fileDivisionStrategyType = 1;
		}
		
		if (M2MShareSettings.contains(STOP_SIMULATION_S)) {
			stopOnFirstQuerySatisfied = M2MShareSettings.getBoolean(STOP_SIMULATION_S);
		}
		else {
			stopOnFirstQuerySatisfied = false;
		}
		
		if (M2MShareSettings.contains(DELEGATION_DEPTH_S)) {
			delegationDepth = M2MShareSettings.getInt(DELEGATION_DEPTH_S);
		}
		else {
			delegationDepth = 1;
		}

		init();		
		
	}
	

	/**
	 * Copyconstructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected M2MShareRouter(M2MShareRouter r) {
		super(r);
		this.frequencyThreshold = r.frequencyThreshold;
		this.scanFrequency = r.scanFrequency;
		this.useDelegation = r.useDelegation;
		this.delegateToAll = r.delegateToAll;
		this.stopOnFirstQuerySatisfied = r.stopOnFirstQuerySatisfied;
		this.delegationDepth = r.delegationDepth;
		this.fileDivisionStrategyType = r.fileDivisionStrategyType;
		init();
	}
	
	/**
	 * Initializes lists and maps
	 */
	private void init() {
		idGenerator = new IdGenerator();
		presenceCollector = new DTNPresenceCollector(this, frequencyThreshold, useDelegation, delegateToAll);
		queuingCentral = new QueuingCentral();
		scheduler = new DTNScheduler(presenceCollector, queuingCentral, this);
		broadcastModule = new BroadcastModule(this);		
	}
	
		
	
	@Override
	public void update() {
		super.update();		
		scheduler.runUpdate();						
	}
	
	public Vector<Pair<DTNHost, Connection>> broadcastQuery(String fileHash){
		return broadcastModule.broadcastQuery(fileHash);
	}
	

	@Override
	public void changedConnection(Connection con) {
		presenceCollector.changedConnection(con);
	}


	public DTNScheduler getScheduler() {
		return this.scheduler;
	}
	
	public int getNextId(){
		return idGenerator.getNextId();
	}
	
	
	public void notifyfileRequestSatisfied(String filehash, boolean selfSatisfied){
		notifyfileRequestSatisfied(getHost(), filehash, selfSatisfied);
		if(stopOnFirstQuerySatisfied){
			SimScenario.getInstance().getWorld().cancelSim();
		}
	}
	
/*
	private void checkSatisfiedQuesies() {
		for(int i=myQueries.size()-1; i>=0; i--){
			M2MShareQuery myQuery = myQueries.get(i);
			String fileHash = DTNFile.hashFromFilename(myQuery.getFilename());
			if(getHost().getFileSystem().hasFile(fileHash)){
				// The query is satisfied, so I can remove it from the list
				myQueries.remove(myQuery);
				System.out.println(SimClock.getTime() + " - soddisfatta mia query: "+myQuery);
				notifyQuerySatisfied(myQuery, getHost());
				if(stopOnFirstQuerySatisfied){
					System.err.println("fine");
					SimScenario.getInstance().getWorld().cancelSim();
				}
			}
		}	
		
		
		// Do the same with delegated queries 
		for(int i=delegatedQueries.size()-1; i>=0; i--){
			M2MShareQuery delegatedQuery = delegatedQueries.get(i);
			String fileHash = DTNFile.hashFromFilename(delegatedQuery.getFilename());
			if(getHost().getFileSystem().hasFile(fileHash)){
				// The query is satisfied, so I can remove it from the list 
				delegatedQueries.remove(delegatedQuery);
				System.out.println(SimClock.getTime() + " - soddisfatta query delegata: "+delegatedQuery);
				notifyQuerySatisfied(delegatedQuery, getHost());
			}
		}
	}*/
	
/*
	private void checkNeededFiles(Connection conn) {
		DTNHost otherHost = conn.getOtherNode(getHost());
		for(M2MShareQuery myQuery : myQueries){
			String fileHash = DTNFile.hashFromFilename(myQuery.getFilename());
			if(otherHost.getFileSystem().hasFile(fileHash) && !isTransferringFile(otherHost,fileHash)){
				System.err.println("Richiedo file");
				requestFile(otherHost, fileHash);
			}
		}
		
		for(M2MShareQuery delegatedQuery : delegatedQueries){
			String fileHash = DTNFile.hashFromFilename(delegatedQuery.getFilename());
			if(otherHost.getFileSystem().hasFile(fileHash) && !isTransferringFile(otherHost,fileHash)){
				System.err.println("Richiedo file per query delegata");
				requestFile(otherHost, fileHash);
			}
		}
		
	}*/
	/*
	private boolean isTransferringFile(Connection conn, String fileHash) {
		
		Message msgOnFly = conn.getMessage();
		
		if(msgOnFly == null){
			System.err.println("nessun messagio in tasferimento");
			return false;			 
		}
		try{
			System.err.println(msgOnFly.getProperty(M2MShareConstants.MSG_TYPE_STR));
		}catch(Exception e){}
		
		if(!msgOnFly.getProperty(M2MShareConstants.MSG_TYPE_STR).equals(M2MShareConstants.TYPE_DATA_RESPONSE)){
			System.err.println("Messaggio non file");
			return false;
		}				
		if(msgOnFly.getProperty(M2MShareConstants.MSG_FILE_ID).equals(fileHash)){
			return true;
		}	
		return false;
	}

	private boolean isTransferringFile(DTNHost otherHost, String fileHash) {
		List<Connection> connections = getConnections();
		for(Connection conn: connections){
			if(!conn.getOtherNode(getHost()).equals(otherHost) ||
					!conn.isUp() ||
					conn.isMessageTransferred()){
				continue;
			}

			Message msgOnFly = conn.getMessage();
			try{
				System.err.println(msgOnFly.getProperty(M2MShareConstants.MSG_TYPE_STR));
			}catch(Exception e){}
			
			if(msgOnFly == null){
				continue; 
			}
			
			if(!msgOnFly.getProperty(M2MShareConstants.MSG_TYPE_STR).equals(M2MShareConstants.TYPE_DATA_RESPONSE)){
				continue;
			}				
			if(msgOnFly.getProperty(M2MShareConstants.MSG_FILE_ID).equals(fileHash)){
				return true;
			}						
		}
		return false;
	}
	

	protected void requestFile(DTNHost otherHost, String fileHash) {
		//DTNFile file = otherHost.getFileSystem().getFile(fileHash);
		//getHost().getFileSystem().addToFiles(file);
		String msgID = "FR"+"_"+SimClock.getTime()+"_"+getHost().getAddress()+"-"+otherHost.getAddress();
		int msgSize = 32;
		Message m = new Message(getHost(), otherHost, msgID, msgSize);
		m.addProperty(M2MShareConstants.MSG_TYPE_STR, M2MShareConstants.TYPE_DATA_REQUEST);
		m.addProperty(M2MShareConstants.MSG_FILE_ID, fileHash);
		addToMessages(m, true);		
	}*/
	
/*
	private void delegateMyQueries(DTNHost otherHost) {
		
		for(M2MShareQuery query : myQueries){
			/**
			 * Query_request message size:
			 * -Type = 4 bytes
			 * -Type key = 1 byte
			 * -Criteria Length = variable
			 * -Search Criteria = variable
			 * -Request Id = 8 bytes
			 * -Source = 4 bytes
			 *//*
			int msgCriteriaLength = query.getFilename().length() + 8 + 4;
			int msgSize = 4 + 1 + msgCriteriaLength + query.getFilename().length() + 8 + 4;
			System.out.println("Creato mess query con size = "+msgSize);
			
			// create the message and prepare it to be sent 
			String msgID = "Q"+query.getId()+"_"+SimClock.getTime()+"_"+getHost().getAddress()+"-"+otherHost.getAddress();
			Message m = new Message(getHost(), otherHost, msgID, msgSize);
			m.addProperty(M2MShareConstants.MSG_TYPE_STR, M2MShareConstants.TYPE_QUERY);
			m.addProperty(M2MShareConstants.MSG_TYPE_KEY_STR, false);
			m.addProperty(M2MShareConstants.MSG_CRITERIA_LENGTH_STR, msgCriteriaLength);
			m.addProperty(M2MShareConstants.MSG_SEARCH_CRITERIA_STR, query.getFilename());
			m.addProperty(M2MShareConstants.MSG_REQUEST_ID_STR, query.getId());
			m.addProperty(M2MShareConstants.MSG_SOURCE_STR, getHost().getAddress());
			System.out.println(m);			

			addToMessages(m, true);				
			
			notifyQueryDelegated(query, getHost(), otherHost);
			
		}		
	}*/
	
/*
	private boolean hasDelegatedQuery(M2MShareQuery query) {
		for(M2MShareQuery tempQuery : delegatedQueries){
			if(tempQuery == query){
				return true;
			}
		}
		return false;
	}*/

	/**
	 * Add a query to the list of my queries
	 * @param query the query to be added
	 */
	public void addM2MQuery(M2MShareQuery query) {
		queuingCentral.addFileRequest(query, this);	
		scheduler.setSomethingToDo(1, true);
		notifyfileRequestCreated(getHost(), DTNFile.hashFromFilename(query.getFilename()));
	}
	
	@Override
	public MessageRouter replicate() {
		M2MShareRouter r = new M2MShareRouter(this);
		return r;
	}

	public int getScanFrequency() {		
		return scanFrequency;
	}

	public void setFrequencyThreshold(int frequencyThreshold2) {
		this.frequencyThreshold = frequencyThreshold2;
	}

	public int getFrequencyThreshold() {
		return this.frequencyThreshold;
	}

	public DTNPresenceCollector getPresenceCollector() {
		return presenceCollector;
	}


	
	/**
	 * Method called when a new Message is received
	 */
	/*
	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message m = super.messageTransferred(id, from);
		int msgType = -1;
		
		try{
			msgType = (Integer)m.getProperty(M2MShareConstants.MSG_TYPE_STR);
		}catch(Exception e){
			return m;			
		}
		
		switch(msgType){
			// Received a delegated Query 
			case M2MShareConstants.TYPE_QUERY:
				System.err.println(SimClock.getTime() +" - In "+getHost().getAddress() + " ricevuta query "+m);
				M2MShareQuery query = new M2MShareQuery(
						(Integer)m.getProperty(M2MShareConstants.MSG_SOURCE_STR),
						(String)m.getProperty(M2MShareConstants.MSG_SEARCH_CRITERIA_STR),
						SimClock.getTime());
				addDelegatedQuery(query);
				break;
				
			// Received a File Request 
			case M2MShareConstants.TYPE_DATA_REQUEST:
				System.err.println(SimClock.getTime() +" - In "+getHost().getAddress() + " ricevuta richiesta di file "+m);
				String fileHash = (String)m.getProperty(M2MShareConstants.MSG_FILE_ID);
				
				if(isTransferringFile(from, fileHash)){
					System.err.println("Trasferimento già in corso: non rispondo");
					break;
				}
				// Create the file for the requestor 
				String msgID = "F"+"_"+SimClock.getTime()+"_"+getHost().getAddress()+"-"+from.getAddress();
				int msgSize = getHost().getFileSystem().getFile(fileHash).getSize();
				Message mResponse = new Message(getHost(), from, msgID, msgSize);
				mResponse.addProperty(M2MShareConstants.MSG_TYPE_STR, M2MShareConstants.TYPE_DATA_RESPONSE);
				mResponse.addProperty(M2MShareConstants.MSG_FILE_ID, fileHash);
				mResponse.addProperty(M2MShareConstants.MSG_FILE_NAME, getHost().getFileSystem().getFile(fileHash).getFilename());
				mResponse.addProperty(M2MShareConstants.MSG_FILE_SIZE, getHost().getFileSystem().getFile(fileHash).getSize());
				addToMessages(mResponse, true);
				System.err.println(SimClock.getTime()+" - Cominciato trasferimento da "+getHost() + " a "+from);
						
				break;
				
			// Received a File Response 
			case M2MShareConstants.TYPE_DATA_RESPONSE:
				System.err.println(SimClock.getTime() +" - In "+getHost().getAddress() + " ricevuto file "+m);
								
				DTNFile newFile = new DTNFile(
						(String)m.getProperty(M2MShareConstants.MSG_FILE_NAME), 
						(String)m.getProperty(M2MShareConstants.MSG_FILE_ID),
						(Integer)m.getProperty(M2MShareConstants.MSG_FILE_SIZE));
				getHost().getFileSystem().addToFiles(newFile);				
				break;
		
		}	
		
		
		return m;
	}
	*/

	@Override
	public RoutingInfo getRoutingInfo() {
		RoutingInfo top = super.getRoutingInfo();
		RoutingInfo ri = new RoutingInfo(presenceCollector.getNrOfEncounters() + 
				" encounters");
		
		for (Map.Entry<DTNHost, Integer> e : presenceCollector.getEncountersMap().entrySet()) {
			DTNHost host = e.getKey();
			Integer value = e.getValue();
			
			ri.addMoreInfo(new RoutingInfo(String.format("%s : %d", 
					host, value)));
		}
		top.addMoreInfo(ri);
		/*
		RoutingInfo myQueriesInfo = new RoutingInfo("My queries: "+ myQueries.size());
		top.addMoreInfo(myQueriesInfo);
		
		RoutingInfo delegatedQueriesInfo = new RoutingInfo("Delegated queries: "+ delegatedQueries.size());
		top.addMoreInfo(delegatedQueriesInfo);*/
		
		return top;
	}



	public boolean containsPendingDownload(DTNHost requestor, String filehash) {
		return queuingCentral.containsPendingDownload(requestor, filehash);
	}

	public void addPendingDownload(DTNActivity newActivity) {		
		if(!((DTNPendingDownload)newActivity).getRequestor().equals(getHost()) &&
				!queuingCentral.contains(newActivity, QueuingCentral.DTN_PENDING_ID)
				&& !queuingCentral.containsDownloadFwd(((DTNPendingDownload)newActivity).getRequestor(), ((DTNPendingDownload)newActivity).getFilehash())){
			notifyfileRequestDelegated(
					((DTNPendingDownload)newActivity).getRequestor(), 
					getHost(), 
					((DTNPendingDownload)newActivity).getFilehash());
			System.err.println(SimClock.getIntTime() + " - "+getHost() + " - ricevuta PendingDownload "+(DTNPendingDownload)newActivity);
			queuingCentral.push(newActivity, QueuingCentral.DTN_PENDING_ID);
			scheduler.setSomethingToDo(2, true);
		}		
	}

	public boolean hasSomethingToDelegate() {		
		return (this.queuingCentral.getQueueSize(QueuingCentral.QUERY_QUEUE_ID) +
				this.queuingCentral.getQueueSize(QueuingCentral.VIRTUAL_FILE_QUEUE_ID) +
				this.queuingCentral.getQueueSize(QueuingCentral.DTN_PENDING_ID))> 0;
	}


	public void addDownloadFwd(DTNDownloadFwd newActivity) {		
		if(!queuingCentral.contains(newActivity, QueuingCentral.DTN_PENDING_UPLOAD_ID)){
			System.err.println(SimClock.getIntTime() + " - "+getHost() + " - aggiunto DownloadFWD");
			queuingCentral.push(newActivity, QueuingCentral.DTN_PENDING_UPLOAD_ID);
			scheduler.setSomethingToDo(0, true);
		}	
	}


	public int[] getIntervalsForDownloadFwd(int activityID) {
		DTNActivity vf = queuingCentral.getActivityFromID(activityID);
		if(vf == null){
			return null;
		}
		return vf.getRestOfMap();
	}


	public void dataFromDownloadFwd(int activityID, int[] intervals, DTNHost from) {
		DTNActivity vf = queuingCentral.getActivityFromID(activityID);
		if(vf == null){
			return;
		}
		if(vf instanceof VirtualFile){
			((VirtualFile) vf).setSelfSatisfied(false);
		}		
		vf.addTransferredData(intervals, from);
	}


	public boolean isStopOnFirstQuerySatisfied() {
		return stopOnFirstQuerySatisfied;
	}


	public boolean hasRemoteFile(String fileHash, Vector<DTNHost> visitedHosts) {
		return broadcastModule.hasRemoteFile(fileHash, visitedHosts);
	}


	public BroadcastModule getBroadcastModule() {
		return broadcastModule;		
	}


	public void relayedFileNoMoreAvailable(String fileHash, DTNHost relayHost) {
		broadcastModule.relayedFileNoMoreAvailable(fileHash, relayHost);
		scheduler.removeCommunicator(fileHash, relayHost);
	}


	public int getDelegationDepth() {
		return delegationDepth;
	}
	
	public int getFileDivisionStrategyType(){
		return fileDivisionStrategyType;
	}


}
