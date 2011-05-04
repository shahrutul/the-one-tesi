package routing.m2mShare;

import java.util.Vector;

import routing.M2MShareRouter;

public class QueuingCentral {
	
	public class NoActivityInQueueException extends Exception{
		private static final long serialVersionUID = 1L;
	}
	
	static final int MAX_PENDING_QUERIES_NUMBER = 10;
	static final int UPLOAD_NUMBER_LIMIT = 10;
	
	public static final int DTN_DOWNLOAD_QUEUE_ID = 0;
	public static final int DTN_PENDING_ID = 1;
	public static final int DTN_PENDING_UPLOAD_ID = 2;
	public static final int QUERY_QUEUE_ID = 3;
	public static final int UPLOAD_QUEUE_ID = 4;
	public static final int VIRTUAL_FILE_QUEUE_ID = 5;
	
	Vector<Vector<DTNActivity>> queues;	
		
	public QueuingCentral() {
		queues = new Vector<Vector<DTNActivity>>();
		for(int i=0; i < 6; i++){
			queues.add(new Vector<DTNActivity>());
		}
	}
	
	
	public void push(DTNActivity activity, int queueIndex){
		queues.get(queueIndex).add(activity);
	}
	
	public DTNActivity pop(int queueIndex) throws NoActivityInQueueException{
		try{			
			return queues.get(queueIndex).remove(0);
			
		}catch(IndexOutOfBoundsException e){
			throw new NoActivityInQueueException();
		}		
	}
	
	public int getQueueSize(int queueIndex){
		return queues.get(queueIndex).size();		
	}
	

	public void addFileRequest(M2MShareQuery m2mQuery, M2MShareRouter m2mShareRouter) {
		VirtualFile vfActivity = new VirtualFile(m2mQuery, m2mShareRouter);
		push(vfActivity, VIRTUAL_FILE_QUEUE_ID);		
	}

	public boolean contains(DTNActivity newActivity, int queueIndex) {
		return queues.get(queueIndex).contains(newActivity);
	}


	public int size() {
		int nrOfActivities=0;
		for(int i=0; i<6; i++){
			nrOfActivities += getQueueSize(i);
		}
		return 0;
	}


	public VirtualFile getVirtualFile(String filehash) {
		try{
			for(DTNActivity activity: queues.get(VIRTUAL_FILE_QUEUE_ID)){
				if(((VirtualFile)activity).getFileHash().equals(filehash)){
					return (VirtualFile)activity;
				}
			}
		}catch (Exception e){}
		return null;		
	}
	
	

}
