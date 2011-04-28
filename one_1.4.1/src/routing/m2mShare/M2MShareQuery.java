package routing.m2mShare;

public class M2MShareQuery {	
	
	private static int idCounter = 0;

	private int fromAddr;
	private String filename;
	private double creationTime;
	private int id;

	public M2MShareQuery(int fromAddr, String filename, double creationTime) {
		this.fromAddr = fromAddr;
		this.filename = filename;
		this.creationTime = creationTime;
		id = idCounter;
		idCounter++;
	}

	public int getFromAddr() {
		return fromAddr;
	}

	public String getFilename() {
		return filename;
	}

	public double getCreationTime() {
		return creationTime;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof M2MShareQuery)){
			return false;
		}
		M2MShareQuery otherQuery = (M2MShareQuery) obj;
		if(otherQuery.getCreationTime() == this.getCreationTime() &&
				otherQuery.getFilename() == this.getFilename() &&
				otherQuery.getFromAddr() == this.getFromAddr()){
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "M2MShareQuery [fromAddr=" + fromAddr + ", filename=" + filename
				+ ", creationTime=" + creationTime + "]";
	}
	
	

}
