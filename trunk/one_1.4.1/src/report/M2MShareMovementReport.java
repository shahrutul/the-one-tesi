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


public class M2MShareMovementReport extends Report implements MovementListener {
	
	public static final String M2MSHARE_NS = "M2MShareRouter";
	public static final String DELEGATION_DEPTH_S = "delegationDepth";
	
	private HashMap<Coord, Integer> locations;
	private int delegationDepth;
	
	
	public M2MShareMovementReport() {
		Settings settings = new Settings(M2MSHARE_NS);
		
		if (settings.contains(DELEGATION_DEPTH_S)) {
			delegationDepth = settings.getInt(DELEGATION_DEPTH_S);
		}
		else {
			delegationDepth = 3;
		}
		
		locations = new HashMap<Coord, Integer>();
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
		if(locations.get(location) == null || locations.get(location) > delDepth){
			locations.put(location, delDepth);
		}
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
		for(int depth = delegationDepth; depth >= 0; depth--){
			for(Coord c: locations.keySet()){
				if(locations.get(c) != null && locations.get(c).intValue() == depth){
					write(String.format("%.2f\t%.2f",c.getX(),c.getY())+ '\t' + locations.get(c));					
				}
			}
		}
		
		super.done();
	}

	
	
}
