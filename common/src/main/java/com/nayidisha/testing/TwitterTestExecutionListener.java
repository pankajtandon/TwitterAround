package com.nayidisha.testing;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import com.nayidisha.twitter.domain.TAUser;
import com.nayidisha.twitter.service.CampaignService;
import com.nayidisha.utils.NDUtils;

public class TwitterTestExecutionListener implements TestExecutionListener{


	
	public void afterTestClass(TestContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void afterTestMethod(TestContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void beforeTestClass(TestContext context) throws Exception {
		
		CampaignService campaignService = (CampaignService)context.getApplicationContext().getBean("campaignService");
		//First delete and then create a user
		campaignService.deleteUser(TestingConstants.TEST_USER); 
		TAUser u = createNewUser();
		u.setUpdatedBy("un"); //bootstrap
		campaignService.addUser(u);
		NDUtils.setUpSecurityContext(TestingConstants.TEST_USER, TestingConstants.TEST_USER_PASSWORD, TestingConstants.TEST_USER_ROLE);
		
	}

	public void beforeTestMethod(TestContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void prepareTestInstance(TestContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	private TAUser createNewUser(){
		TAUser user = new TAUser();
		user.setId(TestingConstants.TEST_USER); //userName
		user.setEmail("my@email.com");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		return user;
	}
}
