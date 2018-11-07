package ats.blockchain.web.utils;

import java.security.MessageDigest;

/**
 * Md5加密
 * 
 * @author shuhao.song
 * @version 1.0
 */
public class Md5Utils {
 
	/**
	 * 字符编码
	 */
	public final static String ENCODING = "UTF-8";
 
	//公盐
    private static final String PUBLIC_SALT = "corda" ;
    //十六进制下数字到字符的映射数组  
    private final static String[] hexDigits = {"0", "1", "2", "3", "4",  
        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
     
    /**
     * 用户密码加密，盐值为 ：PUBLIC_SALT+密码
     * @param  password 密码
     * @return  MD5加密字符串
     */
    public static String encryptPassword(String password){
        return encodeByMD5(PUBLIC_SALT+password);
    }
     
    /**
     * md5加密算法
     * @param  originString
     * @return 
     */
    private static String encodeByMD5(String originString){  
        if (originString != null){  
            try{  
                //创建具有指定算法名称的信息摘要  
                MessageDigest md = MessageDigest.getInstance("MD5");  
                //使用指定的字节数组对摘要进行最后更新，然后完成摘要计算  
                byte[] results = md.digest(originString.getBytes());  
                //将得到的字节数组变成字符串返回  
                String resultString = byteArrayToHexString(results);  
                return resultString.toUpperCase();  
            } catch(Exception ex){  
                ex.printStackTrace();  
            }  
        }  
        return null;  
    } 
     
    /**  
     * 转换字节数组为十六进制字符串 
     * @param     字节数组 
     * @return    十六进制字符串 
     */  
    private static String byteArrayToHexString(byte[] b){  
        StringBuffer resultSb = new StringBuffer();  
        for (int i = 0; i < b.length; i++){  
            resultSb.append(byteToHexString(b[i]));  
        }  
        return resultSb.toString();  
    }  
       
    /** 将一个字节转化成十六进制形式的字符串     */  
    private static String byteToHexString(byte b){  
        int n = b;  
        if (n < 0)  
            n = 256 + n;  
        int d1 = n / 16;  
        int d2 = n % 16;  
        return hexDigits[d1] + hexDigits[d2];  
    }
	
    
	public static void main(String[] args)
	{
		String passwordString = "user2user2";
		System.out.println(Md5Utils.encryptPassword(passwordString));
	}
 
}
