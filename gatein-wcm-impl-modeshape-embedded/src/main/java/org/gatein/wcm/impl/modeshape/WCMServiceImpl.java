package org.gatein.wcm.impl.modeshape;

import java.util.concurrent.ExecutionException;

import org.modeshape.jcr.ModeShapeEngine;

public class WCMServiceImpl {

	private static ModeShapeEngine msEngine = null;
	
	static public synchronized void start() {
		
		if ( msEngine == null ) {		
			msEngine = new ModeShapeEngine();
			msEngine.start();
			System.out.println( "[[ WCMServiceImpl started... ]]" );
		}
	}
	
	static public synchronized void  stop() {
		
		if ( msEngine != null ) {
			try {
				msEngine.shutdown().get();
				System.out.println( "[[ WCMSErviceImpl stopped... ]]" );
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
