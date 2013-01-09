package com.nayidisha.twitter.jobs;

public enum NDTrigger {
	
	//For purposes of AP, NDTrigger enum has unique names. So the group will NOT be used to determine the trigger, just the name.
    //The cron trigger fmt is s   m    hr    dayOfMonth   month dayOfWeek  year 
	TEST1("test1", "In the next 10 seconds and every 10 seconds thereafter", "daily",     "0/10 *  * * * ?"), 
	EVERY1("every1", "In the next minute and every minute thereafter", "daily",     "0 0/1  * * * ?"), 
	EVERY5("every5", "In the next 5 minutes and every 5 minutes thereafter", "daily", "0 0/5 * * * ?"),
	EVERY10("every10", "In the next 10 minutes and every 10 minutes thereafter", "daily", "0 0/10 * * * ?"),
	EVERY15("every15", "In the next 15 minutes and every 15 minutes thereafter", "daily", "0 0/15 * * * ?"),
	EVERY29("every29", "In the next 29 minutes and every 29 minutes thereafter", "daily", "0 0/29 * * * ?"),
	EVERY30("every30", "In the next 30 minutes and every 30 minutes thereafter", "daily", "0 0/30 * * * ?"),
	EVERY60("every60", "In the next hour and every hour thereafter", "daily",     "0 0 0/1 * * ?"),
	  
	SPL_EVERY6("specialEvery6", "Fire every 6 minutes starting at 7am and ending at 11pm, every day", "daily", "0 0/6 7-22 * * ?"),	
	SPL_EVERY11("specialEvery11", "Fire every 11 minutes starting at 7am and ending at 11pm, every day", "daily", "0 0/11 7-22 * * ?"),	
	SPL_EVERY21("specialEvery21", "Fire every 21 minutes starting at 7am and ending at 11pm, every day", "daily", "0 0/21 7-22 * * ?"),	
	SPL_EVERY31("specialEvery31", "Fire every 31 minutes starting at 7am and ending at 11pm, every day", "daily", "0 0/31 7-22 * * ?"),	
	SPL_EVERY41("specialEvery41", "Fire every 41 minutes starting at 7am and ending at 11pm, every day", "daily", "0 0/41 7-22 * * ?"),	
	SPL_EVERY51("specialEvery51", "Fire every 51 minutes starting at 7am and ending at 11pm, every day", "daily", "0 0/51 7-22 * * ?"),	

	MORNING_MADNESS("morningMadness", "Fire every 19 minutes starting at 7am and ending at 11am, every day", "daily", "0 0/19 7-11 * * ?"),
	NOON_MOON("noonMoon", "Fire every 29 minutes starting at 12pm and ending at 3pm, every day", "daily", "0 0/29 12-14 * * ?"),
	EVENING_EXCITMENT("eveningExcitment", "Fire every 9 minutes starting at 3pm and ending at 7pm, every day", "daily", "0 0/9 15-18 * * ?"),
	NIGHT_FRIGHT("nightFright", "Fire every 19 minutes starting at 7pm and ending at 11pm, every day", "daily", "0 0/19 19-22 * * ?"),


	DAY1("day1", "Everyday at 12:05 pm EST", "daily",                           "0 5 12 * * ?"),
    DAY2("day2", "Everyday at 1:05 pm EST", "daily",                            "0 5 13 * * ?"),
    
    WEEK1("week1", "Every Friday at 12:05 pm EST",   "weekly",                  "0 5 12 ? * FRI"),
    WEEK2("week2", "Every Friday at 3:05 pm EST",   "weekly",                   "0 5 15 ? * FRI"),
    WEEK3("week3", "Every Friday at 1:05 am EST",    "weekly",                  "0 5  1 ? * FRI"),
    WEEK4("week4", "Every Saturday at 1:05 pm EST",  "weekly",                  "0 5 13 ? * SAT"),
    WEEK5("week5", "Every Sunday at 1:05 pm EST", "weekly",                     "0 5 13 ? * SUN"),
    
    MONTH1("month1", "Every 1st of month at 12:05 pm EST", "monthly",           "0 5 12 1 * ?"),
    MONTH2("month2", "Every last day of the month at 12:05 pm EST", "monthly",  "0 5 12 L * ?"),
    MONTH3("month3", "Every last Sunday of the month at 12:05 pm EST","monthly","0 5 12 ? * 1L");
    
    private  String name;
    private  String desc;
    private  String group;
    private  String cronExpression;
    private NDTrigger(String name, String desc, String group, String cronExpression){
        this.name = name;
        this.desc = desc;
        this.group = group;
        this.cronExpression = cronExpression;
    }
    
    public static NDTrigger getNDTrigger(String name) {
        NDTrigger thisTrigger = null;
        for (NDTrigger trigger: NDTrigger.values()) {
            if ((trigger.getName().equals(name))) {
                thisTrigger = trigger;
            }
        }
        return thisTrigger;
    }

    
    public String getCronExpression() {
        return cronExpression;
    }

    
    public String getDesc() {
        return desc;
    }

    
    public String getGroup() {
        return group;
    }

    
    public String getName() {
        return name;
    }

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setName(String name) {
		this.name = name;
	}

}
