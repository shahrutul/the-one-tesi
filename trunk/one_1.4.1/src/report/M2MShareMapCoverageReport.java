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


public class M2MShareMapCoverageReport extends Report implements MovementListener {
	
	public static final String M2MSHARE_NS = "M2MShareRouter";
	public static final String DELEGATION_DEPTH_S = "delegationDepth";
	
	private int[][] map = new int[1000][800];
	private int delegationDepth;
	
	
	public M2MShareMapCoverageReport() {
		Settings settings = new Settings(M2MSHARE_NS);
		
		if (settings.contains(DELEGATION_DEPTH_S)) {
			delegationDepth = settings.getInt(DELEGATION_DEPTH_S);
		}
		else {
			delegationDepth = 3;
		}
		
		init();		
	}

	public void initialLocation(DTNHost host, Coord location) {
		stampaPosizione(host, location);
	}

	public void newDestination(DTNHost host, Coord dst, double speed) {
		stampaPosizione(host, dst);
	}

	private void stampaPosizione(DTNHost host, Coord location) {
		int delDepth = getDelegationDepth(host);
		if(delDepth == -1 ){
			return;
		}		
		try{
			int x = ((int) location.getX()) / 10;
			int y = ((int) location.getY()) / 10;
			map[x][y]++;
		}
		catch(Exception e){}
		//write(""+location.getX()+'\t'+location.getY() + '\t' + delDepth); 
	}

	private int getDelegationDepth(DTNHost host) {
		if(!(host.getRouter() instanceof M2MShareRouter)){
			return -1;
		}
		M2MShareRouter router = (M2MShareRouter) host.getRouter();
		return router.getMaxDelegationValueCarried();
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
