/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.HashMap;

import core.Coord;
import core.DTNHost;
import core.MovementListener;
import routing.M2MShareRouter;


public class M2MShareMovementReport extends Report implements MovementListener {
	
	private HashMap<Coord, Integer> locations;
	
	
	public M2MShareMovementReport() {
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
		if(delDepth == -1){
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
		for(Coord c: locations.keySet()){
			write(String.format("%.2f\t%.2f",c.getX(),c.getY())+ '\t' + locations.get(c));
			//write(""+c.getX()+'\t'+c.getY() + '\t' + locations.get(c));
		}
		super.done();
	}

	
	
}
