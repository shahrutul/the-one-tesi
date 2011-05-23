package routing.m2mShare;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import routing.M2MShareRouter;

import core.Connection;
import core.DTNHost;
import core.SimClock;

public class BroadcastModule {
	
	private Map<String, Vector<DTNHost>> routingTable;
	private Map<String, Vector<DTNHost>> responseTable;

	private M2MShareRouter myRouter;
	
	
	public BroadcastModule(M2MShareRouter myRouter) {
		this.myRouter = myRouter;
		routingTable = new HashMap<String, Vector<DTNHost>>();
		responseTable = new HashMap<String, Vector<DTNHost>>();
	}

	public Vector<Pair<DTNHost, Connection>> broadcastQuery(String fileHash) {
		HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();
		if(neighbours.size() == 0){
			return null;
		}
		Vector<Pair<DTNHost, Connection>> servers = new Vector<BroadcastModule.Pair<DTNHost,Connection>>();
		Vector<DTNHost> visitedHosts = new Vector<DTNHost>();
		visitedHosts.add(myRouter.getHost());
		
		for(DTNHost host: neighbours.keySet()){
			if(((M2MShareRouter)host.getRouter()).hasRemoteFile(fileHash, visitedHosts)){
				servers.add(new Pair<DTNHost, Connection>(host, neighbours.get(host)));
				//System.err.println(SimClock.getTime()+" broadcastQuery trovati: "+host+" > "+neighbours.get(host));
			}
		}		
		
		return servers;
	}

	public boolean hasRemoteFile(String fileHash, Vector<DTNHost> visitedTillNow) {
		//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - host visitati: "+visitedTillNow.size());
		/* check if I own the searched file */
		if(myRouter.getHost().getFileSystem().hasFile(fileHash)){
			//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - possiede "+fileHash);
			return true;
		}
		
		DTNHost askingHost = visitedTillNow.lastElement();
		//printMaps();
		
		if(routingTable.containsKey(fileHash)){			
			
			Vector<DTNHost> clients = responseTable.get(fileHash);
			if(clients == null){
				clients = new Vector<DTNHost>();
				clients.add(askingHost);
				responseTable.put(fileHash, clients);
			}
			else{
				if(!clients.contains(askingHost)){
					clients.add(askingHost);
				}
			}
			return true;
		}		
				
		/* else broadcast a yes/no query for the file */		
		HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();
		
		@SuppressWarnings("unchecked")
		Vector<DTNHost> visitedHosts = (Vector<DTNHost>) visitedTillNow.clone();
		visitedHosts.add(myRouter.getHost());
		
		for(DTNHost host: neighbours.keySet()){
			if(visitedHosts.contains(host)){
				continue;
			}
			if(((M2MShareRouter)host.getRouter()).hasRemoteFile(fileHash, visitedHosts)){
				Vector<DTNHost> servers = routingTable.get(fileHash);
				if(servers == null){
					servers = new Vector<DTNHost>();
					servers.add(host);
					routingTable.put(fileHash, servers);
				}
				else{
					if(!servers.contains(host)){
						servers.add(host);
					}
				}
			}
		}	
		
		/* check if I am in contact with a DTNHost with the searched file */
		if(routingTable.containsKey(fileHash)){			
			Vector<DTNHost> clients = responseTable.get(fileHash);
			if(clients == null){
				clients = new Vector<DTNHost>();
				clients.add(askingHost);
				responseTable.put(fileHash, clients);
			}
			else{
				if(!clients.contains(askingHost)){
					clients.add(askingHost);
				}
			}
			//printMaps();
			return true;
		}
		//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - routingTable (dopo) non contiene "+fileHash);
		
		return false;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void connectionLostWith(DTNHost hostNoMoreConnected) {
		if(!routingTable.isEmpty()){
			//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" non più connesso con "+hostNoMoreConnected);
			//printMaps();
		}
		else{
			return;
		}
		
		Vector<String> filesNoMoreAvailable = new Vector<String>();
		for(Map.Entry routingEntry: routingTable.entrySet()){
			Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
			if(serversList.contains(hostNoMoreConnected)){
				//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" rimuovo da routing "+hostNoMoreConnected);
				serversList.remove(hostNoMoreConnected);
				
				if(serversList.isEmpty()){
					/* No other servers to that file */
					filesNoMoreAvailable.add((String) routingEntry.getKey());
				}
			}
		}
		for(Map.Entry routingEntry: responseTable.entrySet()){
			Vector<DTNHost> clientsList = (Vector<DTNHost>) routingEntry.getValue();
			if(clientsList.contains(hostNoMoreConnected)){
				//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" rimuovo da response "+hostNoMoreConnected);
				clientsList.remove(hostNoMoreConnected);
			}
		}
		
		for(String fileHash: filesNoMoreAvailable){
			routingTable.remove(fileHash);
			Vector<DTNHost> clients = responseTable.get(fileHash);
			for(DTNHost client:clients){
				//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - connessione persa: avviso"+client);
				((M2MShareRouter)client.getRouter()).relayedFileNoMoreAvailable(fileHash, myRouter.getHost());
			}
			responseTable.remove(fileHash);
		}
		//printMaps();
	}
	

	public void relayedFileNoMoreAvailable(String fileHash, DTNHost relayHost) {
		if(!routingTable.isEmpty()){
			//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" file remoto perso da "+relayHost);
			//printMaps();
		}
		else{
			return;
		}
		
		boolean wasLast = false;
		Vector<DTNHost> serversList = routingTable.get(fileHash);
		if(serversList != null && serversList.contains(relayHost)){
			///System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" rimuovo da routing "+relayHost);
			serversList.remove(relayHost);
			
			wasLast = serversList.isEmpty();			
		}
		
		if(wasLast){
			routingTable.remove(fileHash);
			Vector<DTNHost> clients = responseTable.get(fileHash);
			for(DTNHost client:clients){
				//System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - connessione persa: avviso"+client);
				((M2MShareRouter)client.getRouter()).relayedFileNoMoreAvailable(fileHash, myRouter.getHost());
			}
			responseTable.remove(fileHash);
		}
		//printMaps();
	}
	
	
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private void printMaps(){
		System.err.print(SimClock.getTime()+ " "+myRouter.getHost()+" - routingTable: ");
		for(Map.Entry routingEntry: routingTable.entrySet()){
			Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
			System.err.print(routingEntry.getKey()+ " - ");
			for(DTNHost host:serversList){
				System.err.print(host+ ", ");
			}
		}
		System.err.println();
		System.err.print(SimClock.getTime()+ " "+myRouter.getHost()+" - responseTable: ");
		for(Map.Entry routingEntry: responseTable.entrySet()){
			Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
			System.err.print(routingEntry.getKey()+ " - ");
			for(DTNHost host:serversList){
				System.err.print(host+ ", ");
			}
		}
		System.err.println();
	}

	public class Pair <T, U>
	{
		private final T first;
		private final U second;
		private transient final int hash;
		public Pair( T f, U s )
		{
			this.first = f;
			this.second = s;
			hash = (first == null? 0 : first.hashCode() * 31)
			+(second == null? 0 : second.hashCode());
		}
		public T getFirst()
		{
			return first;
		}
		public U getSecond()
		{
			return second;
		}
		@Override
		public int hashCode()
		{
			return hash;
		}
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals( Object oth )
		{
			if ( this == oth )
			{
				return true;
			}
			if ( oth == null || !(getClass().isInstance( oth )) )
			{
				return false;
			}
			Pair<T, U> other = getClass().cast( oth );
			return (first == null? other.first == null : first.equals( other.first ))
			&& (second == null? other.second == null : second.equals( other.second ));
		}
	}
	
}
