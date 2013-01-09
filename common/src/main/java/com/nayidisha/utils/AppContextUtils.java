package com.nayidisha.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.nayidisha.twitter.service.TwitterService;

public class AppContextUtils {
	private static ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{ "classpath:twitterContext.xml" });
	
    public static TwitterService getTwitterService(){
    	return (TwitterService)appContext.getBean("twitterService");
    }


}
