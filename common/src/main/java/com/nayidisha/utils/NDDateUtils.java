package com.nayidisha.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.nayidisha.common.NDRuntimeException;

public class NDDateUtils {
	public static String ND_STANDARD_DATE_FORMAT = "MMM dd yyyy";
	public static String ND_STANDARD_DATE_TIME_FORMAT = "MMM dd yyyy, hh:mm:ss a";
	
    public static String apply(Date adDate, String asFormat) {
        if (adDate == null) {
            return "";
        }
        SimpleDateFormat loSDF = new SimpleDateFormat(asFormat);
        loSDF.setTimeZone(TimeZone.getDefault());
        // local time
        return loSDF.format(adDate); 
    }
    
    
    /**
     * This is a convenience method that returns the standard format of displaying dates in ND. 
     * Format used is MMM dd, yyyy
     * @param aDate
     * @return
     */
    public static String applyNDStandardDate(Date aDate){
    	return NDDateUtils.apply(aDate, ND_STANDARD_DATE_FORMAT);
    }
    
    public static String applyNDStandardDateTime(Date aDate){
    	return NDDateUtils.apply(aDate, ND_STANDARD_DATE_TIME_FORMAT);
    }
    
    
    /**
     * Parses a Date string and returns a Date object.
     *
     *@param asDate              a string representing the date.
     *@param asFormat            string with the format to be applied
     *@return                    a new Date object
     */
    public static Date toDate(String asDate, String asFormat) {
    	try {
	        SimpleDateFormat loSDF = new SimpleDateFormat(asFormat);
	        Date ldDate = loSDF.parse(asDate);
	        return ldDate;
    	} catch (ParseException e) {
    		throw new NDRuntimeException("Error parsing date " + asDate, e);
    	}
    }
    
    public static Date toDateFromNDStandardDate(String asDate){
    	return toDate(asDate, ND_STANDARD_DATE_FORMAT);
    }
    
    /**
     * Returns a date that is daysFromToday days after of before depending on the sign of daysFromToday
     * @param daysFromToday
     * @return
     */
    public static Date getDateNDaysFromToday(int daysFromToday) {
        return getDateNDaysFromDate(new Date(), daysFromToday);
    }
    
    public static Date getDateNDaysFromDate(Date date, int daysFromToday) {
        Calendar thatDay = new GregorianCalendar();
        thatDay.setTime(date);
        thatDay.add(Calendar.DATE, daysFromToday);
        return thatDay.getTime();
    }
    
    /**
     * Returns the difference in days between two dates
     * @param date1
     * @param date2
     * @return
     */
    public static long daysBetween(Date date1, Date date2) {
        long start = date1.getTime();
        long end = date2.getTime();
        long diff = Math.abs(start - end);
        long days = diff / (1000*60*60*24); //divide by millisecs in a day
        return days;
    }
    
    public static long hoursBetween(Date date1, Date date2) {
    	long daysLong = daysBetween(date1, date2);
    	
        return Math.round(daysLong * 24.0);
    }
    
    public static long minutesBetween(Date date1, Date date2) {
    	long hoursLong = hoursBetween(date1, date2);
    	
        return Math.round(hoursLong * 60.0 );
    }    
    
    /**
     * Gets the Monday falling after the date specified
     * @param date
     * @return
     */
    public static Date getNextMonday(Date date) {
        Calendar thatDay = new GregorianCalendar();
        thatDay.setTime(date);
        int dayOfWeek = thatDay.get(Calendar.DAY_OF_WEEK);
        int increment = 9 - dayOfWeek;
        thatDay.add(Calendar.DAY_OF_MONTH, increment);
        return thatDay.getTime();
    }
    
    /**
     * Returns the first day of the next month.
     * @param date
     * @return
     */
    public static Date getNextFirstOfMonth(Date date) {
        Calendar thatDay = new GregorianCalendar();
        thatDay.setTime(date);
        thatDay.add(Calendar.MONTH, 1);
        thatDay.set(Calendar.DAY_OF_MONTH, 1);
        return thatDay.getTime();
    }
    
    public static Date getNextSeventhOfMonth(Date date) {
        Calendar thatDay = new GregorianCalendar();
        thatDay.setTime(date);
        thatDay.add(Calendar.MONTH, 1);
        thatDay.set(Calendar.DAY_OF_MONTH, 7);
        return thatDay.getTime();
    }
    public static Date getNextFifteenthOfMonth(Date date) {
        Calendar thatDay = new GregorianCalendar();
        thatDay.setTime(date);
        thatDay.add(Calendar.MONTH, 1);
        thatDay.set(Calendar.DAY_OF_MONTH, 15);
        return thatDay.getTime();
    }
    public static Date getNextTwentyFirstOfMonth(Date date) {
        Calendar thatDay = new GregorianCalendar();
        thatDay.setTime(date);
        thatDay.add(Calendar.MONTH, 1);
        thatDay.set(Calendar.DAY_OF_MONTH, 21);
        return thatDay.getTime();
    }
}
