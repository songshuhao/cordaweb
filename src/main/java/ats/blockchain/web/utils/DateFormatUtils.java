package ats.blockchain.web.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
/**
 * 
 * <P>
 * 提供快速线程安全的日期格式化方法
 * <p>
 * 
 * @author Hongyu Shi
 */
public class DateFormatUtils {
	
	  public static final String DATE_FORMAT = "yyyy-MM-dd";
	  
	  public static final  String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
	  public static final String DATE_FORMAT_y2ms = "yyyyMMdd_HHmmssS";
	 
	  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	  public static final String TIME_FORMAT = "HH:mm:ss";
	 
	  
	  
	  public static String DATE_FORMAT_CHINESE = "yyyy年M月d日";
     
    private static final ThreadLocal<Map<String,SimpleDateFormat>> DATE_FORMAT_HOLDER = new ThreadLocal<Map<String,SimpleDateFormat>>(){
        @Override
        protected Map<String, SimpleDateFormat> initialValue() {
            Map<String, SimpleDateFormat> map = new HashMap<String, SimpleDateFormat>();
            map.put("yyyyMMdd", new SimpleDateFormat("yyyyMMdd"));
            map.put("HHmmss", new SimpleDateFormat("HHmmss"));
            return map;
        }
    };
    private DateFormatUtils(){
    }
    /**
     * 获取符合pattern格式的SimpleDateFormat对象<br/>
     * 如果pattern格式的SimpleDateFormat对象不存在，会创建并缓存之<br/>
     * 
     * @param pattern
     * @return 
     * */
    public static SimpleDateFormat getDateFormat(String pattern){
        Map<String, SimpleDateFormat> map = DATE_FORMAT_HOLDER.get();
        SimpleDateFormat sdf = map.get(pattern);
        if(sdf == null){
            sdf = new SimpleDateFormat(pattern);
            map.put(pattern, sdf);
        }
        return sdf;
    }
    
    public static Date parse(String date,String format) throws ParseException {
        return getDateFormat(format).parse(date);
    }

    public static String format(Date date,String format) {
        return getDateFormat(format).format(date);
    }
    
    public static String format(String date,String format)
    {
    	if(StringUtils.isBlank(date))
    	{
    		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_yyyyMMdd);
    	    String datestr = df.format(new Date());
    		return datestr;
    	}else
    	{
    		date = date.replace(format, "");
    		return date;
    	}
    }
    
    public static void main(String[] args) throws ParseException
	{
		System.out.println(DateFormatUtils.parse("2012-12-12", "yyyy-MM-dd"));
	}
}