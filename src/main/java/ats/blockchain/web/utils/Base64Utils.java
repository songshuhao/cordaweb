package ats.blockchain.web.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Base64加解密
 * 
 * @author shuhao.song
 * @version 1.0
 */
public abstract class Base64Utils {
 
	/**
	 * 字符编码
	 */
	public final static String ENCODING = "UTF-8";
 
	/**
	 * Base64编码
	 * 
	 * @param data 待编码数据
	 * @return String 编码数据
	 * @throws Exception
	 */
	public static String encode(String data){
 
		byte[] in = null;
		String out = null;
		try
		{
			in = Base64.getEncoder().encode(data.getBytes(ENCODING));
			out = new String(in, ENCODING);
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		return out;
	}
 
 
	/**
	 * Base64解码
	 * 
	 * @param data 待解码数据
	 * @return String 解码数据
	 * @throws Exception
	 */
	public static String decode(String data)
	{
		byte[] in = null;
		String out = null;
		try
		{
			in = Base64.getDecoder().decode(data.getBytes(ENCODING));
			out = new String(in, ENCODING);
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		return out;
	}
	
	public static void main(String[] args)
	{
		String owner = "111122222";
		String decode = Base64Utils.encode(owner);
		System.out.println(Base64Utils.encode(owner));
		System.out.println(Base64Utils.decode(decode));
	}
 
}
