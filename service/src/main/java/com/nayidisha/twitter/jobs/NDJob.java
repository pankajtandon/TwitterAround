package com.nayidisha.twitter.jobs;

import com.nayidisha.twitter.jobs.TestJob;


// Cannot move this to aroundPoint-common because it references the classes that implement Job which are in aroundpoint-service project. 
public enum NDJob {
	TEST_JOB("testJob", "test", "Does nothing", "Writes some lines to log file", TestJob.class ),
	GET_TWITTERERS_JOB("getTwitterersJob", "twitter", "Gets Twitterers", "Gets Twitterers who are talking about keywords", GetTwitterersJob.class ),
	//SEND_TWEETS_JOB("sendTweetsJob", "twitter", "Send Tweets ", "Sends Tweets(A) to twitterers that are stored", SendTweetsJob.class ),
	SEND_TWEETS_JOBA("sendTweetsJobMorning", "twitter", "Send Tweets - Morning", "Sends Tweets(A) to twitterers that are stored", SendTweetsJob.class ),
	SEND_TWEETS_JOBB("sendTweetsJobNoon", "twitter", "Send Tweets - Noon", "Sends Tweets(B) to twitterers that are stored", SendTweetsJob.class ),
	SEND_TWEETS_JOBC("sendTweetsJobEvening", "twitter", "Send Tweets - Evening", "Sends Tweets(C) to twitterers that are stored", SendTweetsJob.class ),
	SEND_TWEETS_JOBD("sendTweetsJobNight", "twitter", "Send Tweets - Night", "Sends Tweets(D) to twitterers that are stored", SendTweetsJob.class ),
	FOLLOW_TWITTERERS_JOB("followTwitterersJob", "twitter", "Follows Twitterers", "Follows Twitterers that have been tweeted to", FollowTwitterersJob.class );	
	
	
	
	private final String name;
	private final String group;
	private final String shortDesc;
	private final String longDesc;
	private final Class clazz;
	
	private NDJob(String name, String group, String shortDesc, String longDesc, Class clazz){ 
		this.name = name;
		this.group = group;
		this.shortDesc = shortDesc;
		this.longDesc = longDesc;
		this.clazz = clazz;
	}

	public static NDJob getNDJob(String name, String group){
		NDJob thisJob = null;
		for(NDJob job: NDJob.values()){
			if(job.getName().equals(name)  && job.getGroup().equals(group)){
				thisJob = job;
			}
		}
		return thisJob;
	}
	public Class getClazz() {
		return clazz;
	}

	public String getGroup() {
		return group;
	}

	public String getLongDesc() {
		return longDesc;
	}

	public String getName() {
		return name;
	}

	public String getShortDesc() {
		return shortDesc;
	}
}
