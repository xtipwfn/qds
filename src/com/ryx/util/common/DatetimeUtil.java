package com.ryx.util.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatetimeUtil {
	
	/**
	 * 格式化日期加上00:00:00,如果为空,则返回当前日期减去月份
	 */
	public static Date FormateMindate(String mindate,int month) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GregorianCalendar aGregorianCalendar = new GregorianCalendar();
		aGregorianCalendar.set(Calendar.MONTH, aGregorianCalendar.get(Calendar.MONTH) - month);
		String lastdate = formatter.format(aGregorianCalendar.getTime());
		if (mindate == null||"".equals(mindate)) {
			mindate = lastdate;
		} 
		mindate = mindate + " 00:00:00";
		try {
			return formatter2.parse(mindate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 格式化日期加上00:00:00并减去月份2,如果为空,则返回当前日期减去月份再减去月份2
	 */
	public static Date mindateMinusMonth(String mindate,int month,int month2) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GregorianCalendar aGregorianCalendar = new GregorianCalendar();
		aGregorianCalendar.set(Calendar.MONTH, aGregorianCalendar.get(Calendar.MONTH) - month);
		Date date=new Date();
		if(mindate == null){
			date=aGregorianCalendar.getTime();
		}
		else{
			try {
				date=formatter.parse(mindate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		aGregorianCalendar.setTime(date);
		aGregorianCalendar.set(Calendar.MONTH, aGregorianCalendar.get(Calendar.MONTH) - month2);
		String newdate = formatter.format(aGregorianCalendar.getTime());
		newdate = newdate + " 00:00:00";
		try {
			return formatter2.parse(newdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 格式化日期加上23:59:59,如果为空,则返回当前日期
	 */
	public static Date FormateMaxdate(String maxdate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datenow = formatter.format(new Date()); 
		if (maxdate == null||"".equals(maxdate)) {
			maxdate = datenow;
		} 
		maxdate = maxdate + " 23:59:59";
		try {
			return formatter2.parse(maxdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 格式化日期加上23:59:59并减去月份,如果为空,则返回当前日期减去月份
	 */
	public static Date maxdateMinusMonth(String maxdate,int month) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		if (maxdate != null) {
			try {
				date=formatter.parse(maxdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		GregorianCalendar aGregorianCalendar = new GregorianCalendar();
		aGregorianCalendar.setTime(date);
		aGregorianCalendar.set(Calendar.MONTH, aGregorianCalendar.get(Calendar.MONTH) - month);
		String newdate = formatter.format(aGregorianCalendar.getTime());
		newdate = newdate + " 23:59:59";
		try {
			return formatter2.parse(newdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 字符串转日期
	 */
	public static Date StringToDate(String date) {
		if (date != null) {
			try {
				if (!date.contains("-")){
					return new SimpleDateFormat("yyyyMMdd").parse(date);
				}
				return new SimpleDateFormat("yyyy-MM-dd").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return new Date();
	}
	
	/**
	 * 字符串转日期
	 */
	public static Date StringToDate(String date,String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		if (date != null) {
			try {
				return formatter.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return new Date();
	}
	
	/**
	 * 获取月份第一天
	 */
	public static String getFirstDay(String month) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		try {
			date = formatter.parse(month);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formatter2.format(date);
	}
	
	/**
	 * 获取月份最后一天
	 */
	public static String getLastDay(String month) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		try {
			date = formatter.parse(month);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar aGregorianCalendar = new GregorianCalendar();
		aGregorianCalendar.setTime(date);
		aGregorianCalendar.set(Calendar.MONTH, aGregorianCalendar.get(Calendar.MONTH) +1);
		aGregorianCalendar.set(Calendar.DATE, aGregorianCalendar.get(Calendar.DATE) -1);
		return formatter2.format(aGregorianCalendar.getTime());
	}
	
	/**
	 * 如果为空则返回当前时间
	 */
	public static Date addDefault(Date date) {
		if(date==null){
			return new Date();
		}
		return date;
	}
	
	/**
	 * 获取当前日期字符串
	 */
	public static String getDateString() {
		return  new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
	/**
	 * 获取当前日期字符串
	 */
	public static String getDateString(String format) {
		return  new SimpleDateFormat(format).format(new Date());
	}
	
	/**
	 * 获取日期字符串
	 */
	public static String getDateString(Date date) {
		return  new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	/**
	 * 获取日期字符串
	 */
	public static String getDateString(Date date,String format) {
		return  new SimpleDateFormat(format).format(date);
	}
	
	/**
	 * 获取当前日期时间字符串
	 */
	public static String getDatetimeString() {
		return  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
	/**
	 * 获取当前日期时间长字符串
	 */
	public static String getLongDatetimeString() {
		return  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	/**
	 * 获取当前月份字符串
	 */
	public static String getMonthString() {
		return  new SimpleDateFormat("yyyyMM").format(new Date());
	}
	
	/**
	 * 获取多少天前日期字符串
	 */
	public static String getDateStringDay(int day) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.set(Calendar.DATE, GregorianCalendar.get(Calendar.DATE) - day);
		return formatter.format(GregorianCalendar.getTime());
	}
	/**
	 * 获取多少天前日期字符串
	 */
	public static String getDateStringDay(int day,String formate) {
		SimpleDateFormat formatter = new SimpleDateFormat(formate);
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.set(Calendar.DATE, GregorianCalendar.get(Calendar.DATE) - day);
		return formatter.format(GregorianCalendar.getTime());
	}
	/**
	 * 获取多少天前日期字符串
	 */
	public static String getDateStringDay(Date date,int day) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.setTime(date);
		GregorianCalendar.set(Calendar.DATE, GregorianCalendar.get(Calendar.DATE) - day);
		return formatter.format(GregorianCalendar.getTime());
	}
	/**
	 * 获取多少月前日期字符串
	 */
	public static String getDateStringMonth(int Month) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.set(Calendar.MONTH, GregorianCalendar.get(Calendar.MONTH) - Month);
		return formatter.format(GregorianCalendar.getTime());
	}
	/**
	 * 获取多少月前日期字符串（YYYY-MM）
	 */
	public static String getDateStringMonth2(int Month) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.set(Calendar.DATE, 1);
		GregorianCalendar.set(Calendar.MONTH, GregorianCalendar.get(Calendar.MONTH) - Month);
		return formatter.format(GregorianCalendar.getTime());
	}
	
	/**
	 * 获取多少月前日期字符串(第一天)
	 */
	public static String getDateStringMonthFirstDay(int Month) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.set(Calendar.DATE, 1);
		GregorianCalendar.set(Calendar.MONTH, GregorianCalendar.get(Calendar.MONTH) - Month+1);
		return formatter.format(GregorianCalendar.getTime());
	}
	
	/**
	 * 获取当前时间之后多少小时
	 */
	public static String getDateStringHour(int hours,String formate) {
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.set(Calendar.HOUR, GregorianCalendar.get(Calendar.HOUR) + hours);
		SimpleDateFormat formatter = new SimpleDateFormat(formate);
		return formatter.format(GregorianCalendar.getTime());
	}
	
	/**
	 * 获取指定时间之后多少小时
	 */
	public static String getDateStringHour(Date date,int hours,String formate) {
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.setTime(date);
		GregorianCalendar.set(Calendar.HOUR, GregorianCalendar.get(Calendar.HOUR) + hours);
		SimpleDateFormat formatter = new SimpleDateFormat(formate);
		return formatter.format(GregorianCalendar.getTime());
	}
	
	/**
	 * 计算时间差
	 */
	public static long getDiffDay(Date date1,Date date2){
		return (date1.getTime()-date2.getTime())/(24*60*60*1000)>0 ? (date1.getTime()-date2.getTime())/(24*60*60*1000):
		       (date2.getTime()-date1.getTime())/(24*60*60*1000);
	}
	
	/**
	 * 获得当前日期后多少月
	 */
	public static Date AddMonth(int Month) {
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.set(Calendar.MONTH, GregorianCalendar.get(Calendar.MONTH) + Month);
		return GregorianCalendar.getTime();
	}
	
	/**
	 * 获得当前日期后多少天
	 */
	public static Date AddDay(int day) {
		GregorianCalendar GregorianCalendar = new GregorianCalendar();
		GregorianCalendar.set(Calendar.DATE, GregorianCalendar.get(Calendar.DATE) + day);
		return GregorianCalendar.getTime();
	}
	/**
     * 默认日期格式
     */
    public static String DEFAULT_FORMAT = "yyyy-MM-dd";
 
    /**
     * 格式化日期
     * @param date 日期对象
     * @return String 日期字符串
     */
    public static String formatDate(Date date){
        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_FORMAT);
        String sDate = f.format(date);
        return sDate;
    }
     
    /**
     * 获取当年的第一天
     * @param year
     * @return
     */
    public static Date getCurrYearFirst(){
        Calendar currCal=Calendar.getInstance();  
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearFirst(currentYear);
    }
     
    /**
     * 获取当年的最后一天
     * @param year
     * @return
     */
    public static Date getCurrYearLast(){
        Calendar currCal=Calendar.getInstance();  
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }
     
    /**
     * 获取某年第一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }
     
    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
         
        return currYearLast;
    }
    /**
     * 根据传入数字查询前num年的年份 yyyy
     * @param num
     * @return
     */
    public static String getYearNumber(int num){
    	Calendar cal = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy");
		Calendar calendar2 = Calendar.getInstance();
		calendar2.clear();
		calendar2.set(Calendar.YEAR, cal.get(Calendar.YEAR) - num);
		Date currYearFirst = calendar2.getTime();
		String ssrqq = sf.format(currYearFirst);
    	return ssrqq;
    }
    public static void main(String[] args) {
    	Date date = new Date();
        date.setTime(1636344087148L);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
	}
    
  //计算日期相差秒数
  	public static int SecBetween(Date smdate,Date bdate){
          Calendar cal = Calendar.getInstance();
          long time1 = 0;
          long time2 = 0;
          
          try{
               cal.setTime(smdate);   
               time1 = cal.getTimeInMillis();
               cal.setTime(bdate);
               time2 = cal.getTimeInMillis();
          }catch(Exception e){
              e.printStackTrace();
          }
          long between_days=(time2-time1)/1000;  
         return Integer.parseInt(String.valueOf(between_days));     
      }
}
