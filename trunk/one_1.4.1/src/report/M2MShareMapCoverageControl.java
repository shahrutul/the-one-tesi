/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.HashMap;
import java.util.HashSet;

import core.Coord;
import core.DTNHost;
import core.MovementListener;
import core.Settings;
import routing.M2MShareRouter;


public class M2MShareMapCoverageControl extends Report implements MovementListener {
	
	public static final String M2MSHARE_NS = "M2MShareRouter";
	public static final String DELEGATION_DEPTH_S = "delegationDepth";
	
	private int[][] map = new int[1000][800];
	
	
	public M2MShareMapCoverageControl() {
		init();		
	}

	public void initialLocation(DTNHost host, Coord location) {
		stampaPosizione(host, location);
	}

	public void newDestination(DTNHost host, Coord dst, double speed) {
		stampaPosizione(host, dst);
	}

	private void stampaPosizione(DTNHost host, Coord location) {
				
		try{
			int x = ((int) location.getX()) / 10;
			int y = ((int) location.getY()) / 10;
			map[x][y]++;
		}
		catch(Exception e){}
		//write(""+location.getX()+'\t'+location.getY() + '\t' + delDepth); 
	}

	@Override
	public void done() {
		for(int yi = 799; yi>=0; yi--){
			String s="";
			for(int xi=0; xi<1000; xi++){
				s = s + map[xi][yi] + " ";
			}
			write(s);
		}
		
		super.done();
	}

	
	
}
