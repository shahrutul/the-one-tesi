package routing.m2mShare;

import java.security.KeyStore.Entry;
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
		Vector<Pair<DTNHost, Connection>> servers = new Vector<BroadcastModule.Pair<DTNHost,Connection>>();
		HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();
		Vector<DTNHost> visitedHosts = new Vector<DTNHost>();
		visitedHosts.add(myRouter.getHost());
		
		for(DTNHost host: neighbours.keySet()){
			if(((M2MShareRouter)host.getRouter()).hasRemoteFile(fileHash, visitedHosts)){
				servers.add(new Pair<DTNHost, Connection>(host, neighbours.get(host)));
				System.err.println(SimClock.getTime()+" broadcastQuery trovati: "+host+" > "+neighbours.get(host));
			}
		}		
		
		return servers;
	}

	public boolean hasRemoteFile(String fileHash, Vector<DTNHost> visitedTillNow) {
		System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - host visitati: "+visitedTillNow.size());
		/* check if I own the searched file */
		if(myRouter.getHost().getFileSystem().hasFile(fileHash)){
			System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - possiede "+fileHash);
			return true;
		}
		
		if(routingTable.containsKey(fileHash)){
			System.err.print(SimClock.getTime()+ " "+myRouter.getHost()+" - routingTable (prima): ");
			for(Map.Entry routingEntry: routingTable.entrySet()){
				Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
				System.err.print(routingEntry.getKey()+ " - ");
				for(DTNHost host:serversList){
					System.err.print(host+ ", ");
				}
			}

			System.err.println();
			System.err.print(SimClock.getTime()+ " "+myRouter.getHost()+" - responseTable (prima): ");
			for(Map.Entry routingEntry: responseTable.entrySet()){
				Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
				System.err.print(routingEntry.getKey()+ " - ");
				for(DTNHost host:serversList){
					System.err.print(host+ ", ");
				}
			}
			System.err.println();
			return true;
		}
		else{
			System.err.print(SimClock.getTime()+ " "+myRouter.getHost()+" - routingTable (prima) non contiene "+fileHash);
			for(Map.Entry routingEntry: routingTable.entrySet()){
				Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
				System.err.print(routingEntry.getKey()+ " - ");
				for(DTNHost host:serversList){
					System.err.print(host+ ", ");
				}
			}
			System.err.println();
			System.err.print(SimClock.getTime()+ " "+myRouter.getHost()+" - responseTable (prima): ");
			for(Map.Entry routingEntry: responseTable.entrySet()){
				Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
				System.err.print(routingEntry.getKey()+ " - ");
				for(DTNHost host:serversList){
					System.err.print(host+ ", ");
				}
			}
			System.err.println();
		}
		
		
				
		/* else broadcast a yes/no query for the file */
		DTNHost askingHost = visitedTillNow.lastElement();
		HashMap<DTNHost, Connection> neighbours = (HashMap<DTNHost, Connection>) myRouter.getPresenceCollector().getHostsInRange();
		
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
			System.err.print(SimClock.getTime()+ " "+myRouter.getHost()+" - routingTable (dopo): ");
			for(Map.Entry routingEntry: routingTable.entrySet()){
				Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
				System.err.print(routingEntry.getKey()+ " - ");
				for(DTNHost host:serversList){
					System.err.print(host+ ", ");
				}
			}
			System.err.println();
			System.err.print(SimClock.getTime()+ " "+myRouter.getHost()+" - responseTable (dopo): "+responseTable.size());
			for(Map.Entry routingEntry: responseTable.entrySet()){
				Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
				System.err.print(routingEntry.getKey()+ " - ");
				for(DTNHost host:serversList){
					System.err.print(host+ ", ");
				}
			}
			System.err.println();
			return true;
		}
		System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - routingTable (dopo) non contiene "+fileHash);
		
		return false;
	}
	

	public void connectionLostWith(DTNHost hostNoMoreConnected) {
		Vector<String> filesNoMoreAvailable = new Vector<String>();
		for(Map.Entry routingEntry: routingTable.entrySet()){
			Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
			if(serversList.contains(hostNoMoreConnected)){
				serversList.remove(hostNoMoreConnected);
				
				if(serversList.isEmpty()){
					/* No other servers to that file */
					filesNoMoreAvailable.add((String) routingEntry.getKey());
				}
			}
		}
		
		for(String fileHash: filesNoMoreAvailable){
			routingTable.remove(fileHash);
			Vector<DTNHost> clients = responseTable.get(fileHash);
			for(DTNHost client:clients){
				System.err.println(SimClock.getTime()+ " "+myRouter.getHost()+" - connessione persa: avviso"+client);
				
			}
		}
	}
	
	private String mapToString(Map<String, Vector<DTNHost>> map){
		String s= SimClock.getTime()+ " "+myRouter.getHost()+" - "+map.getClass().getSimpleName()+": ";
		for(Map.Entry routingEntry: responseTable.entrySet()){
			Vector<DTNHost> serversList = (Vector<DTNHost>) routingEntry.getValue();
			s +=routingEntry.getKey()+ " - ";
			for(DTNHost host:serversList){
				s +=host+ ", ";
			}
		}
		s += "/n";
		return s;
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
