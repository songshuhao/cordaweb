package ats.blockchain.web.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class ResultUtil {

	public static String msg(boolean state, String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", state ? "success" : "fail");
		result.put("message", msg);
		return JSON.toJSONString(result);
	}

	public static Map<String, Object> msgMap(boolean state, String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", state ? "success" : "fail");
		result.put("message", msg);
		return result;
	}

	public static String fail(String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", "fail");
		result.put("message", msg);
		return JSON.toJSONString(result);
	}

	public static Map<String, Object> failMap(String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", "fail");
		result.put("message", msg);
		return result;
	}

	public static String fail(String msg, Object data) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", "fail");
		result.put("message", msg);
		result.put("data", data);
		return JSON.toJSONString(result);
	}

	public static Map<String, Object> failMsg(String msg, Object data) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", "fail");
		result.put("message", msg);
		result.put("data", data);
		return result;
	}

	public static String parseMap(Map<String, Object> map) {
		if (map != null) {
			return JSON.toJSONString(map);
		}
		return "";
	}

	public static String getMessage(Map<String,Object> map) {
		return (String) map.get("message");
	}
	
	public static boolean isSuccess(Map<String, Object> map) {
		
		return map != null ? (map.containsKey("state") ? "sucess".equals(map.get("state")) : false) : false;
	}

	private final static String success = "{\"state\":\"success\"}";

	public static String success() {
		return success;
	}
}
