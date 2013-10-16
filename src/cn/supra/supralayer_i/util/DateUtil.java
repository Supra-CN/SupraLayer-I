/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2003 - 2007 Funambol, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission 
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE 
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 * 
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite 
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 * 
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 * 
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably 
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */

package cn.supra.supralayer_i.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import cn.supra.supralayer_i.R;



/**
 * Utility class for date manipulation.
 * This class gives a simple interface for common Date, Calendar and Timezone
 * operations.
 * It is possible to apply subsequent transformations to an initial date, and
 * retrieve the changed Date object at any point.
 *
 */
public class DateUtil {

    /**
     * Format a date-time into UTC format. For example the following string
     * 19980119T070000Z represents 19th Jan 1998 at 07:00:00 in the GMT timezone
     *
     * @param datetime is the date-time to be formatted
     *
     * @return the UTC representation of the give date-time 
     */
    public static String formatDateTimeUTC(Calendar datetime)
    {
        String ret = "";
        int tt = 0;
        ret += datetime.get(Calendar.YEAR);

        tt = datetime.get(Calendar.MONTH)+1;
        ret += tt<10 ? "0"+tt : ""+tt;

        tt = datetime.get(Calendar.DAY_OF_MONTH);
        ret += (tt<10 ? "0"+tt : ""+tt) + "T";

        tt = datetime.get(Calendar.HOUR_OF_DAY);
        ret += tt<10 ? "0"+tt : ""+tt;

        tt = datetime.get(Calendar.MINUTE);
        ret += tt<10 ? "0"+tt : ""+tt;

        tt = datetime.get(Calendar.SECOND);
        ret += (tt<10 ? "0"+tt : ""+tt) + "Z";
        return ret;
    }

    /**
     * Format a date to standard format.
     *
     * @param date is the date to be formatted
     *
     * @return the representation of the give date 
     */
    public static String formatDate(Calendar date)
    {
        StringBuffer ret = new StringBuffer();
        int tt = 0;
        ret.append(date.get(Calendar.YEAR));
        ret.append("-");

        tt = date.get(Calendar.MONTH)+1;
        if (tt < 10) {
            ret.append("0");
        }
        ret.append(tt).append("-");

        tt = date.get(Calendar.DAY_OF_MONTH);
        if (tt < 10) {
            ret.append("0");
        }
        ret.append(tt);
        return ret.toString();
    }


    /**
     * Format a date-time into UTC format. For example the following string
     * 19980119T070000Z represents 19th Jan 1998 at 07:00:00 in the GMT timezone
     *
     * @param date is the date to be formatted. This value will be interpreted
     * as relative to the default timezone 
     *
     * @return the UTC representation of the give date-time 
     */
    public static String formatDateTimeUTC(Date date)
    {
        Calendar datetime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        datetime.setTime(date);
        return formatDateTimeUTC(datetime);
    }

    /**
     * Format a date-time into UTC format. For example the following string
     * 19980119T070000Z represents 19th Jan 1998 at 07:00:00 in the GMT timezone
     *
     * @param date is the date to be formatted. This value will be interpreted
     * as relative to the default timezone 
     *
     * @return the UTC representation of the give date-time 
     */
    public static String formatDateTimeUTC(long date)
    {
        Date d = new Date(date);
        return formatDateTimeUTC(d);
    }

    /**
     * Parse date and time from a string. The method supports multiple formats,
     * in particular:
     * <li>
     *   - yyyyMMddTHHmmssZ
     *   - yyyyMMddTHHmmss
     *   - yyyyMMdd
     *   - yyyy-MM-ddTHH:mm:ssZ
     *   - yyyy-MM-ddTHH:mm:ss
     * @param field date time in one of the supported formats
     * @return a calendar representing the date time
     * @throws IllegalArgumentException if the input date is not valid
     * (according to the acceptable formats)
     */
    public static Calendar parseDateTime(String field)
    {
        Calendar date = null;

        if (field == null || field.length() == 0) {
            throw new IllegalArgumentException("Invalid date: " + field);
        }

        if (field.charAt(field.length()-1) == 'Z') {
            date = Calendar.getInstance(TimeZone.getTimeZone("GMT")); //GMT
        } else {
            date = Calendar.getInstance(TimeZone.getDefault());
        }
        
        int idx = 0;
        idx = parseDateField(field, idx, 4, '-', Calendar.YEAR, date);
        idx = parseDateField(field, idx, 2, '-', Calendar.MONTH, date);
        idx = parseDateField(field, idx, 2, '-', Calendar.DAY_OF_MONTH, date);

        if (idx < field.length()) {
            if (field.charAt(idx)=='T')
            {
                ++idx;
                idx = parseDateField(field, idx, 2, ':', Calendar.HOUR_OF_DAY, date);
                idx = parseDateField(field, idx, 2, ':', Calendar.MINUTE, date);
                idx = parseDateField(field, idx, 2, ':', Calendar.SECOND, date);
            }
            // else we ignore everything else
        }
        return date;
    }

    /**
     * Get a safe time for all day given a certain calendar. The value returned
     * by this method is guaranteed to be in the given day, even if it is
     * shifted during UTC transformation. This transformation is usually helpful
     * for dates associated to all day events.
     *
     * @param cal a calendar
     * @return the same calendar whose hour is shifted in a way that UTC
     * translation will leave the event in the same day
     */
    public static Calendar getSafeTimeForAllDay(Calendar cal) 
    {
        if (TimeZone.getDefault().getRawOffset() > 0) {
            cal.set(Calendar.HOUR_OF_DAY, 14);
        } else if (TimeZone.getDefault().getRawOffset() <= 0) {
            cal.set(Calendar.HOUR_OF_DAY, 1);
        }
        return cal;
    }
 
 

    private static int parseDateField(String date, int start, int len, char sep,
                                      int field, Calendar cal) {

        if (start + len > date.length()) {
            throw new IllegalArgumentException("Invalid date: " + date);
        }
        int value = Integer.parseInt(date.substring(start, start + len));
        start += len;
        if (start < date.length()) {
            // Check if there is a separator to skip
            if (date.charAt(start) == sep) {
                start++;
            }
        }
        if (field == Calendar.MONTH) {
            cal.set(field, value - 1);
        } else {
            cal.set(field, value);
        }
        return start;
    }
    
//    /**
//	 * return the formatted date string
//	 * @param timestamp
//	 * @param format
//	 * @return
//	 * @deprecated  use {@link android.text.format.DateFormat#format} for higher performance
//	 */
//	public static final String getFormattedDateString(long timestamp, String format) {
//        Date dateTime = new Date(timestamp);
//        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
//        String dateString = dateFormatter.format(dateTime);
//        return dateString;
//	}
	
	/**
	 * 比较日期
	 * @param date1
	 * @param date2
	 * @return －1: date1 is before date2; 0: date1 equals date2; 1: date1 is after date2
	 */
	public static final int compareDay(long date1,long date2) {
		//return getFormattedDateString(, "yyyyMMdd").equals(getFormattedDateString(twoTime, "yyyyMMdd"));
		Time time = new Time();
        time.set(date1);
        int year = time.year;
        int month = time.month;
        int day = time.monthDay;
        
        time.set(date2);
        if(year < time.year) return -1;
        else if(year == time.year){
        	if(month < time.month) return -1;
        	else if(month == time.month){
        		if(day < time.monthDay) return -1;
        		if(day == time.monthDay) return 0;
        		else return 1;
        	}
        	else return 1;
        }
        else return 1;
        
	}
	public static final int compareDay(Date date1,Date date2){
		return compareDay(date1.getTime(),date2.getTime());
	}
	
	public static boolean isYesterday(long crtTime) {
		long yesterday = System.currentTimeMillis() - 86400000;
		return compareDay(yesterday, crtTime) == 0;
	}
	
	
	
	
	/**
	 * 格式化日期   1小时  = 60*60*1000 = 3600000 毫秒
	 *          1分钟 = 60*1000    = 60000     毫秒 
	 */
	public static String formatDate(Context con,long pubDate) {
//		long curTime = System.currentTimeMillis();
//		StringBuilder sb = new StringBuilder();
//		long minutes  = 0;
//		long hours = 0;
//		long newTime = curTime - pubDate ;
//		if(pubDate > 0) {
//			if(newTime > 0) {
//				if(DateUtils.isToday(pubDate)) {
//					if(newTime < 3600000) {
//						minutes = newTime / 60000;
//						sb.append(minutes+" ");
//						sb.append(con.getString(R.string.minute_ago));
//					}else {
//						hours =  newTime / 3600000;
//						sb.append(hours+" ");
//						sb.append(con.getString(R.string.hour_ago));
//					}
//				}else if(isYesterday(pubDate)) {
//					sb.append(DateFormat.format("kk:mm", pubDate)/*getFormattedDateString(pubDate, "HH:mm ")*/);
//					sb.append(con.getString(R.string.wg_rss_date_yesterday));
//				}else {
//					sb.append(DateFormat.format("kk:mm MM/dd", pubDate)/*getFormattedDateString(pubDate, "HH:mm ")*/);
//					//sb.append(DateFormat.format();/*getFormattedDateString(pubDate, "MM/dd")*/);
//				}
//			}else if(newTime < 0){
//				//日期显示 显示分为：今天就按当天时间来显示，昨天的话则显示昨天，以前的话则显示日期
//				if(DateUtils.isToday(pubDate)) {
//					sb.append(DateFormat.format("kk:mm", pubDate));
//				}else if(isYesterday(pubDate)) {
//					sb.append(DateFormat.format("kk:mm", pubDate));
//					sb.append(' ').append(con.getString(R.string.wg_rss_date_yesterday));
//				}else {
//					sb.append(DateFormat.format("kk:mm MM/dd", pubDate));
//					//sb.append(getFormattedDateString(pubDate, "MM/dd"));
//				}
//			}
//		}
//		
//		return sb.toString();
		return null;
	}

	public static String datePattern = "yyyy-MM-dd";

	public static final String getDate(String pattern) {
		Date date = new Date();
		return getDate(date, pattern);
	}

	public static final String getDate(Date date, String pattern) {
		SimpleDateFormat df = null;
		String returnValue = "";
		if (date != null) {
			df = new SimpleDateFormat(pattern);
			returnValue = df.format(date);
		}
		return (returnValue);
	}

	public static Date getDate(String dateString, String pattern) {
		SimpleDateFormat df = null;
		Date date = new Date();
		if (dateString != null) {
			try {
				df = new SimpleDateFormat(pattern);
				date = df.parse(dateString);
			} catch (Exception e) {
			}
		}
		return date;
	}

	/**
	 * 返回毫秒
	 * 
	 * @param date
	 *            日期
	 * @return 返回毫秒
	 */
	public static long getMillis(java.util.Date date) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		return c.getTimeInMillis();
	}

	/**
	 * 日期相加
	 * 
	 * @param date
	 *            日期
	 * @param day
	 *            天数
	 * @return 返回相加后的日期
	 */
	public static java.util.Date addDate(java.util.Date date, int day) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
		return c.getTime();
	}

}
