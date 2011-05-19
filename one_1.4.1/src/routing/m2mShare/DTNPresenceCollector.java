package routing.m2mShare;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import routing.M2MShareRouter;

import core.Connection;
import core.DTNHost;
import core.SimClock;

public class DTNPresenceCollector {
	
	/** default probation window value = 2 days */
	private static final int PROBATION_WINDOW_DEFAULT_SIZE = 2;
	
	private M2MShareRouter myRouter;	
	
	private double lastScanTime;
	private double lastAdaptTime;
	private int probationWindow;
	private double expectedRatio;
	private int frequencyThreshold;
	
	/** encounters list */
	private Map<DTNHost, Integer> encounters;
	
	private Map<DTNHost, Connection> hostsInRange;
	private Map<Connection, DTNHost> connections;
	//private Map<DTNHost, Double> connectionsDuration;

	private int servantsThisDay;

	private boolean useDelegation;

	private boolean delegateToAll;

	public DTNPresenceCollector(M2MShareRouter m2mShareRouter, int frequencyThreshold, boolean useDelegation, boolean delegateToAll) {
		this.myRouter = m2mShareRouter;
		this.lastScanTime = new Random(0).nextDouble() * myRouter.getScanFrequency();
		this.useDelegation = useDelegation;
		this.delegateToAll = delegateToAll;

		this.lastAdaptTime = SimClock.getTime();
		this.probationWindow = PROBATION_WINDOW_DEFAULT_SIZE;
		this.frequencyThreshold = frequencyThreshold;
		this.encounters = new HashMap<DTNHost, Integer>();
		this.hostsInRange = new HashMap<DTNHost, Connection>();
		this.connections = new HashMap<Connection, DTNHost>();
		//this.connectionsDuration = new HashMap<DTNHost, Double>();
		this.servantsThisDay = 0;
	}

	public void update() {
		/* tune parameters every day */
		if(SimClock.getTime() >= lastAdaptTime + 86400){
			pw_adapt();
			lastAdaptTime = SimClock.getTime();
			servantsThisDay = 0;
		}
		
		if(isScanning()){			
			this.hostsInRange.clear();
			this.connections.clear();
			
			for(Connection con :myRouter.getConnections()){				
				if (con.isUp()) {
					DTNHost otherHost = con.getOtherNode(myRouter.getHost());					
					
					//M2MShareRouter works only with other M2MShareRouters
					if((otherHost.getRouter() instanceof M2MShareRouter)){
						hostsInRange.put(otherHost, con);
						connections.put(con, otherHost);/*
						if(!connectionsDuration.containsKey(otherHost)){
							connectionsDuration.put(otherHost, SimClock.getTime());
						}*/
						int newEncountersValue = getEncountersFor(otherHost) +1;
						
						if(!useDelegation){
							continue;
						}
						if(delegateToAll && myRouter.hasSomethingToDelegate()){
							myRouter.getScheduler().delegate(otherHost);
						}
						else
						if(newEncountersValue >= frequencyThreshold && 
								myRouter.hasSomethingToDelegate()){
							
							//System.err.println(SimClock.getIntTime() + " - "+otherHost + " ha superato la soglia in "+myRouter.getHost());
							servantsThisDay++;
							myRouter.getScheduler().delegate(otherHost);							
							encounters.remove(otherHost);
						}
						else{
							encounters.put(otherHost, newEncountersValue);
						}
					}
					
				}
			}
			
			myRouter.getScheduler().runTriggeredActivityFlow();
			
		}		
	}
	
	/**
	 * Checks if the PresenceCollector is currently in the scanning mode
	 * @return True if the PresenceCollector is scanning; false if not
	 */
	boolean isScanning() {
		
		double simTime = SimClock.getTime();
		int scanFrequency = myRouter.getScanFrequency();
		
		if (simTime < lastScanTime) {
			return false; /* not time for the first scan */
		}
		else if (simTime >= lastScanTime + scanFrequency) {
			lastScanTime = simTime; /* time to start the next scan round */
			return true;
		}
		return false;
	}
	
	
	private void pw_adapt(){
		int L = 100;
		expectedRatio = L / probationWindow;
		int activeRatio = this.servantsThisDay;
		
		if(activeRatio >= expectedRatio){
			probationWindow--;
			if(probationWindow < 1){
				probationWindow = 2;
				setFrequencyThreshold(frequencyThreshold+1);
			}
		}
		else{
			probationWindow = probationWindow * 2;
			if(probationWindow > 30){
				probationWindow = 30;
				setFrequencyThreshold(frequencyThreshold-1);
			}
		}
	}
		

	/**
	 * Returns the current encounter times value for a host or 0 if entry for
	 * the host doesn't exist.
	 * @param host The host to look the encounter times for
	 * @return the current encounter times value
	 */
	public int getEncountersFor(DTNHost host) {
		if (encounters.containsKey(host)) {
			return encounters.get(host);
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Returns a map of this router's encounter times
	 * @return a map of this router's encounter times
	 */
	public Map<DTNHost, Integer> getEncountersMap() {
		return this.encounters;
	}
	
	
	/**
	 * Checks if an other Host is in range now
	 * @param otherHost the other host to ckeck
	 * @return true if otherHost is in range now, false otherwise
	 */
	boolean isHostInRange(DTNHost otherHost){
		//it must be present in scanned hosts list, which is upated every "scanFrequency" sec
		if(!hostsInRange.containsKey(otherHost)){
			return false;
		}
		return hostsInRange.get(otherHost).isUp();
		
	}

	
	private void setFrequencyThreshold(int i) {
		this.frequencyThreshold = i;
		myRouter.setFrequencyThreshold(this.frequencyThreshold);
	}

	public int getNrOfEncounters() {		
		return encounters.size();
	}

	public Map<DTNHost, Connection> getHostsInRange() {
		return hostsInRange;
	}
	

	public void changedConnection(Connection con) {		
		if(con.isUp()){
			return;
		}
		DTNHost hostNoMoreConnected = con.getOtherNode(myRouter.getHost());
		if(hostNoMoreConnected == null || !(hostNoMoreConnected.getRouter() instanceof M2MShareRouter)){
			return;
		}
		myRouter.getBroadcastModule().connectionLostWith(hostNoMoreConnected);
		
		if(connections.containsKey(con)){	
			hostsInRange.remove(hostNoMoreConnected);
			connections.remove(con);
			myRouter.getScheduler().removeCommunicator(con);
			/*
			if(myRouter.getHost().getAddress() == 32){
				double duration = SimClock.getTime() - connectionsDuration.get(hostNoMoreConnected);
				System.err.println(SimClock.getTime()+"-"+ connectionsDuration.get(hostNoMoreConnected) + "32 connesso con "+hostNoMoreConnected+" per "+ duration +
						"("+ con.getSpeed()*duration+ " trasferiti)");
			}*/
			//connectionsDuration.remove(hostNoMoreConnected);
			
		}		
	}

	public Connection getConnectionFor(DTNHost host) {
		return hostsInRange.get(host);
	}

	public int getDelegationTTL() {		
		// 86400 seconds for 1 day
		return probationWindow*86400;
	}
	
	

}
