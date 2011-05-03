import routing.m2mShare.Interval;
import routing.m2mShare.IntervalMap;


public class mainTest {

	public static void main(String[] args) {
		IntervalMap map = new IntervalMap(1000000,1024);
		System.err.println(map);
		
		
		try {
			int[] outMap = map.cut(false);
			stampa(outMap);
			System.err.println("mapSize di mapout: "+IntervalMap.mapSize(outMap));
			System.err.println();
			
			int[] outMap2 = map.cut(false);
			stampa(outMap2);
			System.err.println("mapSize di mapout2: "+IntervalMap.mapSize(outMap2));
			System.err.println();
			
			map.update(outMap[0], outMap[0]+100000);		
			System.err.println(map);
			
			map.update(outMap2[0], outMap2[0]+100000);
			System.err.println(map);			
			
			int[] outMap3= map.assignRestofMap();
			stampa(outMap3);
			System.err.println("mapSize di mapout3: "+IntervalMap.mapSize(outMap3));
			System.err.println();
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
	
	static void stampa(int[] array){
		for(int i:array){
			System.err.print(i+" ");
		}
		System.err.println();
	}
}
