package core;

import java.util.ArrayList;
import java.util.List;

public class DTNVirtualFile implements Comparable<DTNVirtualFile>{

	/** Name of the file */
	private String filename;
	/** Hash of the file */
	private String hash;
	/** Size of the file (bytes) */
	private int length; 
	/** The FileSystem the Virtual File belongs to */
	private FileSystem fileSystem;
	/** available parts of the file */
	private List<filePart> parts = new ArrayList<DTNVirtualFile.filePart>();
	
	public DTNVirtualFile(String filename, String hash, int size, FileSystem fs) {
		this.filename = filename;
		this.hash = hash;
		this.length = size;	
		this.fileSystem = fs;
	}
	
	
	public DTNVirtualFile(String filename, int size, FileSystem fs) {
		this(filename, DTNFile.hashFromFilename(filename), size, fs);
	}
	
	/**
	 * Add a File part to the Virtual File selected
	 * @param beginPos the begin of the part
	 * @param endPos the end of the part
	 * @return true if the file is complete, false otherwise
	 */
	public boolean addPart(int beginPos, int endPos){
		if(beginPos < 0 || endPos > length){
			return false;
		}
		
		if(parts.size() == 0){
			parts.add(new filePart(beginPos, endPos));
			return false;
		}
		
		int previousPart = -1;
		for(; previousPart < parts.size()-1 && parts.get(previousPart+1).beginPos<beginPos; previousPart++);
		
		if(getCompletedPerc()==100){
			build();
			return true;
		}
		
		return false;
	}
		
	private void build() {
		this.fileSystem.addToFiles(new DTNFile(filename, hash, length));
	}


	/**
	 * Returns the size of the file (in bytes)
	 * @return the size of the file
	 */
	public int getSize() {
		return this.length;
	}
	
	/**
	 * Returns the filename of the file
	 * @return the filename of the file
	 */
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * Returns the hash of the file
	 * @return the hash of the file
	 */
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * Returns a string representation of the file
	 * @return a string representation of the file
	 */
	public String toString () {
		return filename;
	}
	

	public int getCompletedPerc() {
		int available = 0;
		for(int i=0; i<parts.size(); i++){
			available += parts.get(i).getLength();
		}
		return (length / available)*100;
	}

	@Override
	public int compareTo(DTNVirtualFile f) {
		return getHash().compareTo(f.getHash());
	}
	
	private class filePart{
		private int beginPos;
		private int endPos;
			
		
		public filePart(int beginPos, int endPos) {
			super();
			this.beginPos = beginPos;
			this.endPos = endPos;
		}



		private int getLength(){
			return endPos-beginPos;
		}
	}
	
}
