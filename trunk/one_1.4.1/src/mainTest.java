import routing.m2mShare.Interval;
import routing.m2mShare.IntervalMap;


public class mainTest {

	public static void main(String[] args) {
		IntervalMap map = new IntervalMap(1000000,1024);
		System.err.println(map);
		System.err.println(map.mapSize());
		
		int[] outMap;
		try {
			outMap = map.cut(true);
			for(int i:outMap){
				System.err.print(i+" ");
			}
			System.err.println();
			System.err.println(map.mapSize());
			
			map.update(0, 300000);	
			System.err.println();
			System.err.println(map);
			System.err.println(map.mapSize());
			
			/*
			outMap = map.cut(false);
			for(int i:outMap){
				System.err.print(i+" ");
			}
			System.err.println();
			System.err.println(map);
			
			Interval[] i2 = map.update(0, 500000);
			for(Interval i: i2){
				System.err.print(i+" ");
			}
			System.err.println();System.err.println();
			System.err.println(map);
			
			outMap = map.cut(false);
			for(int i:outMap){
				System.err.print(i+" ");
			}
			System.err.println();
			System.err.println(map);*/
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		int availableCommunicators = 1;
		int[] startingPoints = map.getStartingPoints(availableCommunicators);		
		for(int i=0; i< availableCommunicators; i++){
			System.err.print(startingPoints[i]+" ");
		}
		System.err.println();
		
		availableCommunicators = 2;
		startingPoints = map.getStartingPoints(availableCommunicators);		
		for(int i=0; i< availableCommunicators; i++){
			System.err.print(startingPoints[i]+" ");
		}
		System.err.println();
		
		availableCommunicators = 3;
		startingPoints = map.getStartingPoints(availableCommunicators);		
		for(int i=0; i< availableCommunicators; i++){
			System.err.print(startingPoints[i]+" ");
		}
		System.err.println();
		
		availableCommunicators = 4;
		startingPoints = map.getStartingPoints(availableCommunicators);		
		for(int i=0; i< availableCommunicators; i++){
			System.err.print(startingPoints[i]+" ");
		}
		System.err.println();*/
	}
}
