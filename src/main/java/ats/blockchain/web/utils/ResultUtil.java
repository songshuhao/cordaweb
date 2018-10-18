package ats.blockchain.web.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class ResultUtil {
	private static final String successStr = "success";
	private static final String failStr = "fail";
	private static final String stateKey = "state";
	private static final String msgKey = "message";
	private static final String dataKey = "data";
	
	public static String msg(boolean state, String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(stateKey, state ? successStr : failStr);
		result.put(msgKey, msg);
		return JSON.toJSONString(result);
	}

	public static Map<String, Object> msgMap(boolean state, String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(stateKey, state ? successStr : failStr);
		result.put(msgKey, msg);
		return result;
	}

	public static String fail(String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(stateKey, failStr);
		result.put(msgKey, msg);
		return JSON.toJSONString(result);
	}

	public static Map<String, Object> failMap(String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(stateKey, failStr);
		result.put(msgKey, msg);
		return result;
	}

	public static String fail(String msg, Object data) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(stateKey, failStr);
		result.put(msgKey, msg);
		result.put(dataKey, data);
		return JSON.toJSONString(result);
	}

	public static Map<String, Object> failMsg(String msg, Object data) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(stateKey, failStr);
		result.put(msgKey, msg);
		result.put(dataKey, data);
		return result;
	}

	public static String parseMap(Map<String, Object> map) {
			return map != null ?JSON.toJSONString(map):"";
	}

	public static String getMessage(Map<String,Object> map) {
		return (String) map.get(msgKey);
	}
	
	public static boolean isSuccess(Map<String, Object> map) {
		return map != null ? (map.containsKey(stateKey) ? successStr.equals(map.get(stateKey)) : false) : false;
	}

	private final static String success = "{\"state\":\"success\"}";

	public static String success() {
		return success;
	}
}
