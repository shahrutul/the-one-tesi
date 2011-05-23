package routing.m2mShare;

public class IdGenerator {
	private int nextId;

	public IdGenerator(int nextId) {
		super();
		this.nextId = nextId;
	}
	
	public IdGenerator() {
		super();
		this.nextId = 0;
	}
	
	public int getNextId(){
		return nextId++;
	}
	
}
