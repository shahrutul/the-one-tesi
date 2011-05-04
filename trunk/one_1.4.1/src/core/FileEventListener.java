package core;

public interface FileEventListener {
	
	/**
	 * Method is called when a new query is created
	 * @param q M2MShareQuery that was created
	 */
	public void newVirtualFile(DTNHost where, String filehash);
	
	/**
	 * Method is called when a query is delegated
	 * @param q The query that is going to be delegated
	 * @param from Node where the query is delegated from 
	 * @param to Node where the query is delegated to
	 */
	public void fileRequestDelegated(DTNHost from, DTNHost to, String filehash);
	
	/**
	 * Method is called when a query is expired
	 * @param q The query that has expired
	 * @param where The host where the query has expired
	 */
	public void pendingDownloadExpired(DTNHost where, DTNHost requestor, String filehash);
	
	public void downloadFWDExpired(DTNHost where, DTNHost requestor, String filehash);
	
	public void dataTransferred(DTNHost from, DTNHost to, int bytes);	
	
	
	/**
	 * Method is called when a query is successfully satisfied
	 * @param q The query that has been satisfied
	 */
	public void fileRequestSatisfied(DTNHost where, String filehash, boolean selfSatisfied);

}
