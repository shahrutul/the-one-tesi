
package routing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import routing.m2mShare.BroadcastModule;
import routing.m2mShare.BroadcastModule.Pair;
import routing.m2mShare.QueuingCentral.NoActivityInQueueException;
import routing.m2mShare.DTNActivity;
import routing.m2mShare.DTNDownloadFwd;
import routing.m2mShare.DTNPendingDownload;
import routing.m2mShare.DTNScheduler;
import routing.m2mShare.DTNPresenceCollector;
import routing.m2mShare.IdGenerator;
import routing.m2mShare.IntervalMap;
import routing.m2mShare.FileRequest;
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
	private static final String USE_BROADCAST_S = "useBroadcastModule";
	private static final String MULTI_HOP_PROBABILITY_S = "multiHopProbability";
	
	public static final int FREQUENCY_THRESHOLD = 2;
	public static final int SCAN_FREQUENCY = 10;
	
	/* wait one day before redelegate a pending task */
	private static final int WAIT_BEFORE_REDELEGATE = 86400;
		
	private int frequencyThreshold;
	private boolean useDelegation;
	private boolean delegateToAll;
	private boolean useBroadcastModule;

		
	private DTNPresenceCollector presenceCollector;
	private int scanFrequency;
	private boolean stopOnFirstQuerySatisfied;
	private DTNScheduler scheduler;
	private QueuingCentral queuingCentral;
	private BroadcastModule broadcastModule;
	private IdGenerator idGenerator;
	private Random rng;
	private int fileDivisionStrategyType;
	private int multiHopProbability;
	private int delegationDepth;
	
	
	private int maxDelegationValueCarried;

	private Map<DTNHost, Double> servants;
	
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
		
		if (M2MShareSettings.contains(MULTI_HOP_PROBABILITY_S)) {
			multiHopProbability = M2MShareSettings.getInt(MULTI_HOP_PROBABILITY_S);
		}
		else {
			multiHopProbability = 100;
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
		
		if (M2MShareSettings.contains(USE_BROADCAST_S)) {
			setUseBroadcastModule(M2MShareSettings.getBoolean(USE_BROADCAST_S));
		}
		else {
			setUseBroadcastModule(false);
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
		this.multiHopProbability = r.multiHopProbability;
		init();
	}
	
	/**
	 * Initializes lists and maps
	 */
	private void init() {
		servants = new HashMap<DTNHost, Double>();
		idGenerator = new IdGenerator();
		presenceCollector = new DTNPresenceCollector(this, frequencyThreshold, useDelegation, delegateToAll);
		queuingCentral = new QueuingCentral();
		scheduler = new DTNScheduler(presenceCollector, queuingCentral, this);
		broadcastModule = new BroadcastModule(this);
		maxDelegationValueCarried = -1;
		Settings settings = new Settings("MovementModel");
		int rngSeed;
		if (settings.contains("rngSeed")) {
			rngSeed = settings.getInt("rngSeed");
		}
		else {
			rngSeed = 0;
		}
		rng = new Random(rngSeed);
	}
	
		
	
	@Override
	public void update() {
		super.update();		
		scheduler.runUpdate();						
	}
	

	public void delegate(DTNHost otherHost) {
		/*
		if(alreadyDelegated(otherHost)){
			return;
		}		*/
		M2MShareRouter otherRouter = (M2MShareRouter) otherHost.getRouter();
		for(int i=0; i < queuingCentral.getQueueSize(QueuingCentral.VIRTUAL_FILE_QUEUE_ID); i++){
			try{				
				VirtualFile virtualFile = (VirtualFile) queuingCentral.pop(QueuingCentral.VIRTUAL_FILE_QUEUE_ID);
				/* Moved here for performance reasons */
				//if(!otherRouter.containsPendingDownload(getHost(), virtualFile.getFileHash())){
				if(!otherRouter.hasPendingTask(virtualFile.getID())){
					//System.err.println(SimClock.getTime()+ " - "+ myRouter.getHost()+" delega virtuaFile a "+otherHost);
					int[] newMap = virtualFile.getMapForDelegation(fileDivisionStrategyType);
					if(newMap == null){
						queuingCentral.push(virtualFile, QueuingCentral.VIRTUAL_FILE_QUEUE_ID);
						continue;
					}
					Vector<DTNHost> delChain = new Vector<DTNHost>();
					delChain.add(getHost());
					DTNActivity newActivity = new DTNPendingDownload(
							getHost(), 
							virtualFile.getFileHash(),
							new IntervalMap(newMap),
							presenceCollector.getDelegationTTL(),
							delChain,							 
							otherRouter,
							virtualFile.getID()
					);

					if(otherRouter.addPendingDownload(newActivity)){
						putServant(otherHost);
					}
				}
				queuingCentral.push(virtualFile, QueuingCentral.VIRTUAL_FILE_QUEUE_ID);

			}
			catch(NoActivityInQueueException e){}
		}
		//I delegate also pendingDownloads
		for(int i=0; i < queuingCentral.getQueueSize(QueuingCentral.DTN_PENDING_ID); i++){
			try{
				DTNPendingDownload pendingToDelegate = (DTNPendingDownload) queuingCentral.pop(QueuingCentral.DTN_PENDING_ID);
				
				if((SimClock.getTime() >= (pendingToDelegate.getReceivingTime() + WAIT_BEFORE_REDELEGATE)) &&
						!otherRouter.hasPendingTask(pendingToDelegate.getID()) &&
						(!pendingToDelegate.getDelegationChain().contains(otherHost)) && 
						pendingToDelegate.getHop() < getDelegationDepth() &&
						isProbableToDelegate()){
					//System.err.println(SimClock.getTime()+ " - "+ myRouter.getHost()+" delega virtuaFile a "+otherHost);
					int[] newMap = pendingToDelegate.getMapForDelegation(fileDivisionStrategyType);
					if(newMap == null){
						queuingCentral.push(pendingToDelegate, QueuingCentral.DTN_PENDING_ID);
						continue;
					}
					@SuppressWarnings("unchecked")
					Vector<DTNHost> newDelChain = (Vector<DTNHost>) pendingToDelegate.getDelegationChain().clone();
					newDelChain.add(getHost());
					DTNActivity newActivity = new DTNPendingDownload(
							getHost(), 
							pendingToDelegate.getFilehash(),
							new IntervalMap(newMap),
							presenceCollector.getDelegationTTL(),
							//pendingToDelegate.getHop() + 1,
							newDelChain,							
							otherRouter,
							pendingToDelegate.getID()
					);

					if(otherRouter.addPendingDownload(newActivity)){
						//update original Activity's TTL
						pendingToDelegate.updateTTL();
						putServant(otherHost);
					}
				}
				
				queuingCentral.push(pendingToDelegate, QueuingCentral.DTN_PENDING_ID);

			}
			catch(NoActivityInQueueException e){}
		}
	}
	

	private boolean isProbableToDelegate() {
		return rng.nextInt(100) <= multiHopProbability;
	}


	private boolean hasPendingTask(String activityID) {
		DTNActivity vf = queuingCentral.getActivityFromID(activityID);
		return vf != null;
	}


	private void putServant(DTNHost otherHost){
		if(servants.containsKey(otherHost)){
			return;
		}
		else{
			servants.put(otherHost, SimClock.getTime()+presenceCollector.getDelegationTTL());
		}
	}
	
	private void removeServant(DTNHost otherHost){
		servants.remove(otherHost);
	}

	public void notifyDownloadFwdCompleted(DTNHost otherHost) {
		removeServant(otherHost);
	}
	
	private boolean alreadyDelegated(DTNHost otherHost){
		if(servants.containsKey(otherHost)){
			if(SimClock.getTime() < servants.get(otherHost)){
				return true;
			}
			else{
				servants.remove(otherHost);
			}
		}
		return false;
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
	
	public String getNextId(){
		return getHost().toString()+"_"+idGenerator.getNextId();
	}
	
	
	public void notifyfileRequestSatisfied(String filehash, boolean selfSatisfied){
		notifyfileRequestSatisfied(getHost(), filehash, selfSatisfied);
		if(stopOnFirstQuerySatisfied){
			SimScenario.getInstance().getWorld().cancelSim();
		}
	}
	
	/**
	 * Add a FileRequest to the list of my queries
	 * @param request the FileRequest to be added
	 */
	public void addFileRequest(FileRequest request) {
		queuingCentral.addFileRequest(request, this);	
		scheduler.setSomethingToDo(1, true);
		notifyfileRequestCreated(getHost(), DTNFile.hashFromFilename(request.getFilename()));
		if(maxDelegationValueCarried < 0){
			maxDelegationValueCarried = 0;
		}
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



	public boolean containsPendingDownload(DTNHost requestor, String filehash) {
		return queuingCentral.containsPendingDownload(requestor, filehash);
	}

	public boolean addPendingDownload(DTNActivity newActivity) {		
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
			
			if(maxDelegationValueCarried < ((DTNPendingDownload)newActivity).getHop()){
				maxDelegationValueCarried = ((DTNPendingDownload)newActivity).getHop();
			}
			
			return true;
		}		
		else{
			return false;
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


	public int[] getIntervalsForDownloadFwd(String activityID) {
		DTNActivity vf = queuingCentral.getActivityFromID(activityID);
		if(vf == null){
			return null;
		}
		return vf.getRestOfMap();
	}


	public void dataFromDownloadFwd(String activityID, int[] intervals, DTNHost from) {
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
		
		//RoutingInfo tasks = new RoutingInfo(queuingCentral.size() + " active tasks");
		RoutingInfo tasks = new RoutingInfo("1 active task");
		tasks.addMoreInfo(new RoutingInfo("DTNPendingDownload from A32 (47%)"));
		
		top.addMoreInfo(tasks);
		
		return top;
	}


	public void setUseBroadcastModule(boolean useBroadcastModule) {
		this.useBroadcastModule = useBroadcastModule;
	}


	public boolean useBroadcastModule() {
		return useBroadcastModule;
	}

	public int getMaxDelegationValueCarried(){
		return maxDelegationValueCarried;
	}


	@Override
	public void notifyPendingDownloadExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		maxDelegationValueCarried = -1;
		super.notifyPendingDownloadExpired(where, requestor, filehash);
	}


	@Override
	public void notifyDownloadFWDExpired(DTNHost where, DTNHost requestor,
			String filehash) {
		maxDelegationValueCarried = -1;
		super.notifyDownloadFWDExpired(where, requestor, filehash);
	}


	@Override
	public void notifyDownloadFWDReturned(DTNHost from, DTNHost requestor,
			String filehash) {
		maxDelegationValueCarried = -1;
		super.notifyDownloadFWDReturned(from, requestor, filehash);
	}
	
	


}
