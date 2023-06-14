/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

/**
 * THIS CLASS WILL PERFOM ALL THAT REGARDING TO DATE_CALENDAR_TIME
 * @author rango
 */
public class DateUtil {
    
    /**
     * TO CHECK IF A SQL.DATE IS BETWEEN 2 SQL.DATE
     * @param mainDate
     * @param debut
     * @param fin
     * @return 
     */
    public static boolean isSqlDateBetween(java.sql.Date mainDate, java.sql.Date debut, java.sql.Date fin){
        java.util.Date  dateMain = new java.util.Date(mainDate.getTime());
        java.util.Date dateDebut = new java.util.Date(debut.getTime());
        java.util.Date dateFin = new java.util.Date(fin.getTime());
        
        return isDateBetweem(dateMain, dateDebut, dateFin);
    }
    
    /**
     * String to -> java.sql.Timestamp
     * @param sqlDateType The type of sql.date needed
     * @param dateString
     * @param datePattern
     * @return
     * @throws Exception 
     */
    public static <T> T stringToSqlDate(Class<T> sqlDateType, String dateString, DatePattern datePattern) throws Exception{
        T result = null;
    
        try {
            Date temp = stringToDate(dateString, datePattern);
            if(sqlDateType == java.sql.Timestamp.class){
                return (T) utilDateToSqlDate(java.sql.Timestamp.class, temp);
            }else if(sqlDateType == java.sql.Date.class){
                return (T) utilDateToSqlDate(java.sql.Date.class, temp);
            }
            return result;
        } catch (Exception e) {
            throw new Exception("Error on parsing string date to java.sql.Timestamp or java.sql.Date");
        }
    }
    
    
    /**
     * String to Time -> BUT Format should be 11:00:00
     * @param timeString
     * @return
     * @throws Exception 
     */
    public static java.sql.Time stringToSqlTime(String timeString) throws Exception{
        try {
            return java.sql.Time.valueOf(timeString);
        } catch (Exception e) {
            throw new Exception("Error on casting string to time");
        }
    }
    
    
    /**
     * CASTING UTIL/CALENDAR -> JAVA.SQL type
     * @param <T>
     * @param sqlDateType -> the sql.class date requested
     * @param aDate -> maybe a calendar ofr date or localdate -> you can add some class if possible
     * @return
     * @throws Exception 
     */
    public static <T> T utilDateToSqlDate(Class<T> sqlDateType,Object aDate) throws Exception{
        T result = null;
        
        try {
            Date date = null;

            if(aDate.getClass() == java.util.GregorianCalendar.class){             // If field -> Calendar
                System.out.println("Hello");
                date = new Date(((Calendar) aDate).getTimeInMillis());
            } else if(aDate.getClass() == Date.class){                             // If field -> Date
                date = (Date) aDate;
            } else if(aDate.getClass() == LocalDateTime.class){                     // If field -> LocalDateTime
                Instant insant = ((LocalDateTime) aDate).toInstant(ZoneOffset.UTC);
                date = Date.from(insant);
            }
                
            if(sqlDateType == java.sql.Timestamp.class){        // If requested -> timestamp sql
                java.sql.Timestamp sqlStamp = new java.sql.Timestamp(date.getTime());
                return (T) sqlStamp;
            } else if(sqlDateType == java.sql.Date.class){      // If requested -> dqte sql
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                return (T) sqlDate;
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on casting the date to timestap data type");
        }
    }
    
    
    /**
     * TO CHECK IF A DATE IS BETWEEN TWO CALENDAR
     * @param mainDate the date to verify
     * @param debut
     * @param end
     * @return 
     */
    public static boolean isDateBetweem(Date mainDate, Date debut, Date end){
        Calendar main = Calendar.getInstance();
        main.setTime(mainDate);
        
        Calendar calDebut = Calendar.getInstance();
        calDebut.setTime(debut);
        
        Calendar calFin = Calendar.getInstance();
        calFin.setTime(end);
        
        return isCalendarBetween(main, calDebut, calFin);
    }
    
    /**
     * TO CHECK IF A CALENDAR IS BETWEEN TWO CALENDAR
     * @param mainDate the calendar to verify
     * @param debut
     * @param end
     * @return 
     */
    public static boolean isCalendarBetween(Calendar mainDate, Calendar debut, Calendar end){
        if((mainDate.after(debut) == true && mainDate.before(end) == true) || (mainDate.getTime().compareTo(debut.getTime()) == 0) || 
        (mainDate.getTime().compareTo(end.getTime()) == 0)){
            return true;
        }
        return false;
    }
    
    
    /**
     * DIFFERENCE BETWEEN TWO DATES TO SECOND - MINUTS - etc
     * @param debut
     * @param end
     * @param timeType
     * @return 
     */
    public static double diffBetweenDates(Date debut, Date end, TimeType timeType){
        Calendar calDebut = Calendar.getInstance();
        calDebut.setTime(debut);
        
        Calendar calFin = Calendar.getInstance();
        calFin.setTime(end);
        
        return diffBetweenCalendars(calDebut, calFin, timeType);
    }
    
    /**
     * GET THE DIFFERENCE BETWEEN TWO DATES TO SECONDE OR MINUTES OR DAY OR ... 
     * @param debut
     * @param end
     * @param timeType enum class -> Choosing
     * @return 
     */
    public static double diffBetweenCalendars(Calendar debut, Calendar end, TimeType timeType){

//        int differenceSeconde = (int) Duration.between(debut.toInstant(), end.toInstant()).toSeconds();     // Get the difference in second
        int differenceSeconde = (int)Duration.between(debut.toInstant(), end.toInstant()).getSeconds();
        
        if(timeType.equals(TimeType.SECOND) == true) return (double) differenceSeconde/1;
        if(timeType.equals(TimeType.MINUTE) == true) return (double) differenceSeconde/60;
        if(timeType.equals(TimeType.HOUR) == true) return (double) differenceSeconde/3600;
        if(timeType.equals(TimeType.DAY) == true) return (double) differenceSeconde/86400;
        if(timeType.equals(TimeType.MONTH) == true) return (double) differenceSeconde/2629746;
        if(timeType.equals(TimeType.YEAR) == true) return (double) differenceSeconde/31556952;
        return 0.0;
    }
    
    /**
     * CONVERTING STRING TO DATE
     * @param dateString
     * @param pattern
     * @return
     * @throws Exception 
     */  
    public static Date stringToDate(String dateString, DatePattern pattern) throws Exception{
        try {
            return new SimpleDateFormat(pattern.value()).parse(dateString);  
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on parsing string into Date "+dateString + " with the fornat "+pattern.value());
        }
    }
    
    /**
     * CONVERTING A STRING TO CALENDAR
     * @param dateString
     * @param pattern
     * @return 
     */
    public static Calendar stringToCalendar(String dateString, DatePattern pattern) throws Exception{
        Calendar calendar = Calendar.getInstance();
        
        try {
            Date date = stringToDate(dateString, pattern);
            calendar.setTime(date);
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on parsing a string to calendar");
        }
    }
    
    /**
     * CONVERTING A DATE TO STRING
     * @param date
     * @param pattern enum class => We have to choose
     * @return 
     */
    public static String dateToString(Date date, DatePattern pattern){
        DateFormat dateFormat = new SimpleDateFormat(pattern.value());
        String strDate = dateFormat.format(date);
        return strDate;
    }
    
    
    /**
     * CONVERTING A CALENDAR => STRING( pattern i.e: dd-MM-yyyy hh:mi:ss)
     * @param calendar
     * @param pattern  enum class => We have to choose
     * @return 
     */
    public static String calendarToString(Calendar calendar, DatePattern pattern){
        Date date = calendar.getTime();
        return dateToString(date, pattern);
    }
}
