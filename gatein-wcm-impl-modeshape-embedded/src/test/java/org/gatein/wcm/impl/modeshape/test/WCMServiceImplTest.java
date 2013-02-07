package org.gatein.wcm.impl.modeshape.test;

import org.gatein.wcm.impl.modeshape.WCMServiceImpl;
import org.junit.Test;

public class WCMServiceImplTest {

	@Test
	public void runningStandAloneTest() throws Exception {
		
		WCMServiceImpl.start();
		
		
		
		WCMServiceImpl.stop();
	}
	
}
