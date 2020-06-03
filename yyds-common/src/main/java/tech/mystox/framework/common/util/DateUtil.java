package tech.mystox.framework.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/4 10:16
 * \* Description:
 * \
 */
public class DateUtil {
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat defaultFormat;

    private DateUtil() {
        defaultFormat = new SimpleDateFormat(DEFAULT_PATTERN);
    }

    public static DateUtil getInstance() {
        return SingleInstance.instance;
    }


    private static class SingleInstance {
        private final static DateUtil instance = new DateUtil();
    }


    public String format(Date date) {
        return defaultFormat.format(date);
    }

    public String format(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public Date parse(String resource) {
        Date date = null;
        try {
            date = defaultFormat.parse(resource);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    public Date parse(String resource, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(resource);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    public Date getFirstDayOfMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getFirstDayOfMonth(int year, int month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getLastDayOfMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getLastDayOfMonth(int year, int month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }
    public Date getFirstDayOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }

    public Date getLastDayOfYear(int year)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year+1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }


    public Date getFirstDayOfWeek(int year, int weekOfYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }

    public Date getLastDayOfWeek(int year, int weekOfYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear + 1);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public static void Wait(long time)
    {
        long referencetime=System.currentTimeMillis();
        while(System.currentTimeMillis()-referencetime<time) { ; }
    }


    public static void main(String[] args)
    {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date time = DateUtil.getInstance().getLastDayOfMonth();
//        System.out.println(format.format(time));
//        System.out.println(time.getTime());
//        System.out.println(format.format(DateUtil.getInstance().getFirstDayOfMonth(2020, 1)));
//        System.out.println(format.format(DateUtil.getInstance().getLastDayOfMonth(2020, 1)));
//        int weeksInWeekYear = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
//        System.out.println(format.format(DateUtil.getInstance().getFirstDayOfWeek(2020, weeksInWeekYear)));
//        System.out.println(format.format(DateUtil.getInstance().getLastDayOfWeek(2020, weeksInWeekYear)));
//
//        System.out.println(format.format(DateUtil.getInstance().getFirstDayOfYear(2020)));
//        System.out.println(format.format(DateUtil.getInstance().getLastDayOfYear(2020)));
        DateUtil.Wait(3000);
    }
}