
import java.util.Random;

import routing.m2mShare.Communicator;
import routing.m2mShare.Interval;
import routing.m2mShare.IntervalMap;


public class mainTest {
	
	private static Random rng = new Random();
	private static boolean isProbableToDelegate() {
		return rng.nextInt(100) <= 60;
	}

	public static void main(String[] args) {
		int fileServersN = 6;
		

		try {
			for(int i=0; i<10; i++){
				if(isProbableToDelegate()){
					System.err.println("true");
				}
				else{
					System.err.println(false);
				}
			}
			/*
			for(int strat = 0; strat < 3; strat++){
				IntervalMap map = new IntervalMap(25000000,1024);
				int[][] outmapS = new int[fileServersN][];
				int sommaT = 0;
				for(int i=0; i< fileServersN; i++){
					int[] outMap = map.cut(strat);
					outmapS[i] = outMap;
					//stampa(IntervalMap.interestingIntervals(outMap));
					sommaT += IntervalMap.interestingIntervalsSize(IntervalMap.interestingIntervals(outMap));
				}
				for(int i=0; i< fileServersN; i++){
					//stampa(map.assignRestofMap());
					//System.err.println("aggiungo da "+IntervalMap.interestingIntervals(outmapS[i])[0]+
					//		" a "+IntervalMap.interestingIntervals(outmapS[i])[1]);
					map.update(IntervalMap.interestingIntervals(outmapS[i])[0], IntervalMap.interestingIntervals(outmapS[i])[1]);
					//System.err.println("mapsize="+map.mapSize());
				}
				
				//System.err.println("mapsize="+map.mapSize());
				if(map.mapSize() != 0){
					//System.err.println("mancano "+IntervalMap.mapSize(map.assignRestofMap()));
					sommaT += IntervalMap.mapSize(map.assignRestofMap());
				}
				System.err.println("Dati con strategia "+strat+" = "+sommaT);
				System.err.println();
			}*/

			/*
			map.cut(t);

			int[] outMap = map.cut(t);
			IntervalMap delegatedMap = new IntervalMap(outMap);
			System.err.print(map+"\toutmap: ");stampa(outMap);
			stampa(IntervalMap.interestingIntervals(outMap));
			System.err.println("mapSize di mapout: "+IntervalMap.mapSize(outMap));
			System.err.println("interestSize di mapout: "+ IntervalMap.interestingIntervalsSize(IntervalMap.interestingIntervals(outMap)));
			System.err.println();
			System.err.println("delegatedMap: "+delegatedMap);
			int[] delegatedMapoutMap = delegatedMap.cut(t);
			stampa(IntervalMap.interestingIntervals(delegatedMapoutMap));
			System.err.println("mapSize di mapout: "+IntervalMap.mapSize(delegatedMapoutMap));
			System.err.println("interestSize di mapout: "+ IntervalMap.interestingIntervalsSize(IntervalMap.interestingIntervals(delegatedMapoutMap)));


			/*
			int[] outMap = map.cut(t);
			System.err.print("outmap: ");stampa(outMap);
			stampa(IntervalMap.interestingIntervals(outMap));
			System.err.println("mapSize di mapout: "+IntervalMap.mapSize(outMap));
			System.err.println();
			
			int[] outMap2 = map.cut(t);
			System.err.print("outmap2: ");stampa(outMap2);
			System.err.print("interest outmap2: ");stampa(IntervalMap.interestingIntervals(outMap2));
			System.err.println("mapSize di mapout2: "+IntervalMap.mapSize(outMap2));
			System.err.println("mapSize interest di mapout2: "+
					IntervalMap.interestingIntervalsSize(IntervalMap.interestingIntervals(outMap2)));
			System.err.println();
			
			map.update(outMap[0], outMap[0]+100000);		
			System.err.println(map);
			
			map.update(IntervalMap.interestingIntervals(outMap2)[0], IntervalMap.interestingIntervals(outMap2)[1]);
			System.err.println(map);
			
			int[] outMap3 = map.cut(t);
			System.err.print("outmap2: ");stampa(outMap3);
			/*
			int[] outMap3= map.assignRestofMap();
			System.err.print("outmap3: ");stampa(outMap3);
			System.err.print("interest outmap3: ");stampa(IntervalMap.interestingIntervals(outMap3));
			System.err.println("mapSize di mapout3: "+IntervalMap.mapSize(outMap3));
			System.err.println("mapSize interest di mapout3: "+
					IntervalMap.interestingIntervalsSize(IntervalMap.interestingIntervals(outMap3)));
			System.err.println();*/
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
		if(array==null){
			System.err.println("null");
			return;
		}
		for(int i:array){
			System.err.print(i+" ");
		}
		System.err.println();
	}
}
