package com.nayidisha.twitter.service.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration (locations={"/twitterContext.xml", "AbstractTwitterTests-context.xml"})
public class AbstractTwitterTests  extends AbstractTransactionalJUnit4SpringContextTests{ 

	//Common methods here
	
}
