package ats.blockchain.web.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.csvreader.CsvReader;
import com.google.common.collect.Lists;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo;
import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.config.DiamondApplicationRunner;
import net.corda.core.contracts.StateAndRef;

/**
 * Object转换类
 * 
 * @ClassName: BeanUtils
 * @Description: TODO
 * @author liaoshuai.qin
 * @date 2016年11月16日 下午7:43:22
 *
 */
public class AOCBeanUtils {

	private static Logger logger = LoggerFactory.getLogger(AOCBeanUtils.class);
	private static final String ERR_FMT = "convert field: %s,value: %s to type: %s failed!";

	/**
	 * StringToObject
	 * 
	 * @throws ParseException
	 * 
	 * @throws Exception
	 */
	private static Object StringToObject(Class<?> clazz, String str) throws ParseException {
		if (str == null || str.trim().length() == 0) {
			return null;
		}
		Object o = str;
		if (clazz == Date.class) {
			try {
				o = DateFormatUtils.parse(str, "yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e1) {
				try {
					o = DateFormatUtils.parse(str, "yyyyMMdd");
				} catch (ParseException e2) {
					try {
						o = DateFormatUtils.parse(str, "yyyyMMdd HH:mm:ss");
					} catch (ParseException e3) {
						try {
							o = DateFormatUtils.parse(str, "yyyy/MM/dd HH:mm:ss");
						} catch (ParseException e4) {
							logger.error("date parse error: while convert {} to Date.", str);
							throw e4;
						}
					}
				}
			}
		} else if (clazz == BigDecimal.class) {
			o = new BigDecimal(str);
		} else if (clazz == Long.class) {
			o = new Long(str);
		} else if (clazz == Integer.class) {
			o = new Integer(str);
		} else if (clazz == int.class) {
			o = Integer.parseInt(str);
		} else if (clazz == float.class) {
			o = Float.parseFloat(str);
		} else if (clazz == boolean.class) {
			o = Boolean.parseBoolean(str);
		} else if (clazz == byte.class) {
			o = Byte.parseByte(str);
		}
		return o;
	}

	/**
	 * 用来转换前台传递的参数
	 * 
	 * @throws InstantiationException
	 * @throws ConvertException
	 *
	 */
	public static <T> T MapToObject(Class<T> beanClass, Map<String, Object> map) throws IntrospectionException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		if (map == null)
			return null;

		T obj = beanClass.newInstance();

		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			String fieldName = property.getName();
			if (map.containsKey(fieldName)) {
				Class<?> proType = property.getPropertyType();
				Object value = map.get(fieldName);
				if (value == null)
					continue;

				if (!proType.isInstance(value)) {
					try {
						value = StringToObject(proType, value.toString());
					} catch (Exception e) {
						String msg = String.format(ERR_FMT, fieldName, value, proType.getName());
						logger.error(msg);
					}
				}
				Method setter = property.getWriteMethod();

				if (setter != null) {
					setter.invoke(obj, value);
				}
			}
		}
		return obj;
	}

	public static <T> Map<String, Object> ObjectToMap(T obj)
			throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (obj == null)
			return null;
		Map<String, Object> map = new HashMap<String, Object>();
		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			Method getter = property.getReadMethod();
			if (getter != null && !"class".equals(property.getName())) {
				Object value = getter.invoke(obj);
				if (value != null) {
					map.put(property.getName(), value);
				}
			}
		}

		return map;
	}

	public static <T> Map<String, String> ObjectToStringMap(T obj)
			throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (obj == null)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			Method getter = property.getReadMethod();
			if (getter != null && !"class".equals(property.getName())) {
				Object value = getter.invoke(obj);
				if (value != null) {
					map.put(property.getName(), value.toString());
				}
			}
		}

		return map;
	}

	/**
	 * 用来转换通过sql查询出来的结果
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IntrospectionException
	 * @throws ParseException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public static <T> T HashMapToObject(Class<T> beanClass, HashMap<String, Object> map)
			throws InstantiationException, IllegalAccessException, IntrospectionException, ParseException,
			IllegalArgumentException, InvocationTargetException {
		if (map == null)
			return null;

		Map<String, Object> tempMap = new HashMap<String, Object>();
		for (Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey().toUpperCase().replaceAll("_", "");
			tempMap.put(key, entry.getValue());
		}

		T obj = beanClass.newInstance();

		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			if (tempMap.containsKey(property.getName().toUpperCase())) {
				Class<?> proType = property.getPropertyType();
				Object value = tempMap.get(property.getName().toUpperCase());
				if (!proType.isInstance(value) && value != null) {
					value = StringToObject(proType, value.toString());
				}
				Method setter = property.getWriteMethod();
				if (setter != null && value != null) {
					setter.invoke(obj, value);
				}
			}
		}

		return obj;
	}

	public static <T> List<T> HashMapListToObjectList(Class<T> beanClass, List<HashMap<String, Object>> mapList)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			IntrospectionException, ParseException {
		if (mapList == null)
			return null;

		List<T> result = new ArrayList<T>();
		for (HashMap<String, Object> map : mapList) {
			result.add(AOCBeanUtils.HashMapToObject(beanClass, map));
		}

		return result;
	}

	/**
	 * 获得指定对象src的 fieldName
	 * 
	 * @param src
	 * @param fieldName
	 * @return filedValue
	 */
	public static <T> Object getFieldValue(T src, String fieldName) {
		if (src == null) {
			return null;
		}
		Object val = null;
		String clazzName = src.getClass().getName();
		ClassMethods clzM = ClassMethodFactory.Instance.getClassMethods(src.getClass());
		Field cf = clzM.getField(fieldName);
		if (cf == null) {
			try {
				logger.warn("no field {} in class: {}. {}", fieldName, clazzName, AOCBeanUtils.ObjectToStringMap(src));
			} catch (Exception e) {
				logger.error("getField {} error:{}", fieldName, e.getMessage());
			}
			return null;
		}
		try {
			cf.setAccessible(true);
			val = cf.get(src);
		} catch (IllegalArgumentException e) {
			logger.error("getField {} error:{}", fieldName, e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error("getField {} error:{}", fieldName, e.getMessage());
		}

		return val;
	}

	/**
	 * 从csv获取内容并转换为对象
	 * 
	 * @param csvFile
	 * @param clazz
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> List<T> getObjectFromCsv(String csvFile, Class<T> clazz)
			throws IOException, InstantiationException, IllegalAccessException {
		if (StringUtils.isBlank(csvFile)) {
			logger.warn("csv file is empty.");
			return Collections.emptyList();
		}
		CsvReader reader = new CsvReader(new FileReader(csvFile), ',');
		return parseCsv(clazz, reader);
	}

	private static <T> List<T> parseCsv(Class<T> clazz, CsvReader reader)
			throws IOException, InstantiationException, IllegalAccessException {
		ClassMethods clazzMethod = ClassMethodFactory.Instance.getClassMethods(clazz);
		List<T> list = new ArrayList<T>();
		reader.readHeaders();
		String[] header = reader.getHeaders();
		int count = reader.getHeaderCount();
		if (count == 0) {
			logger.warn("csv file is invaild: no headers.");
			return Collections.emptyList();
		}
		while (reader.readRecord()) {
			String[] row = reader.getValues();
			T t = clazz.newInstance();
			for (int i = 0; i < count; i++) {
				String fieldName = header[i];
				String value = row[i];
				Field f = clazzMethod.getField(fieldName);
				if(f==null) {
					logger.warn("can't get filed: {}",fieldName);
					continue;
				}
				Object objVal = convertType(value, f.getType());
				f.set(t, objVal);
			}
			list.add(t);
		}
		return list;
	}
	/**
	 * 从csv获取内容并转换为对象
	 * @param inputStream
	 * @param clazz
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> List<T> getObjectFromCsv(InputStream inputStream, Class<T> clazz)
			throws IOException, InstantiationException, IllegalAccessException {
		if (inputStream == null) {
			logger.warn("Can not get csv file");
			return Collections.emptyList();
		}

		InputStreamReader isReader = new InputStreamReader(inputStream);
		CsvReader csvReader =new CsvReader(isReader, ',');
		return parseCsv(clazz, csvReader);
	}

	private static Object convertType(String value, Class<?> type) {
		Object obj = null;
		if (value == null) {
			return null;
		}
		if (BigDecimal.class.equals(type)) {
			obj = new BigDecimal(value);
		} else if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
			obj = Integer.valueOf(value);
		} else if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) {
			obj = Boolean.valueOf(value);
		} else if (Long.TYPE.equals(type) || Long.class.equals(type)) {
			obj = Long.valueOf(value);
		}  else {
			obj = value;
		}
		return obj;
	}

	public static List<PackageAndDiamond> convertPakageState2PackageInfo(
			@Nonnull List<StateAndRef<PackageState>> stateList) {
		List<PackageAndDiamond> pkgList = new ArrayList<>(stateList.size());
		for (StateAndRef<PackageState> state : stateList) {
			PackageAndDiamond pad = convertSinglePkgState2PkgInfo(state);
			pkgList.add(pad);
		}
		return pkgList;
	}

	public static PackageAndDiamond convertSinglePkgState2PkgInfo(StateAndRef<PackageState> state) {
		PackageAndDiamond pad = new PackageAndDiamond();
		PackageInfo pkgInf = new PackageInfo();
		PackageState data = state.getState().getData();

		BeanUtils.copyProperties(data, pkgInf);

		pkgInf.setAoc(data.getAoc() != null ? data.getAoc().getName().toString() : "");
		pkgInf.setSuppliercode(data.getSuppliercode() != null ? data.getSuppliercode().getName().toString() : "");
		pkgInf.setAuditor(data.getAuditor() != null ? data.getAuditor().getName().toString() : "");
//			pkgInf.setOwner(data.getOwner()!=null?data.getOwner().getName().toString():"");
		pkgInf.setOwner(data.getOwner());
		pkgInf.setVault(data.getVault() != null ? data.getVault().getName().toString() : "");
		pkgInf.setGradlab(data.getGradlab() != null ? data.getGradlab().getName().toString() : "");
		pkgInf.setUuid(String.format("%1$x_%2$x", data.getLinearId().getId().getMostSignificantBits(),
				data.getLinearId().getId().getLeastSignificantBits()));

		pad.setPkgInfo(pkgInf);
		List<DiamondsInfo> list = data.getDiamondinfolist();
		List<DiamondInfoData> diList = Lists.newArrayList();
		if (list != null) {
			for (DiamondsInfo li : list) {
				DiamondInfoData di = new DiamondInfoData();
				BeanUtils.copyProperties(li, di);
				di.setStatusDesc(data.getStatusDesc());
				diList.add(di);
			}
			pad.setDiamondList(diList);
		}
		return pad;
	}

	public static <T> boolean isEmpty(Collection<T> list) {
		boolean isEmpty = true;
		if (null != list && !list.isEmpty()) {
			isEmpty = false;
		}
		return isEmpty;
	}

	public static <T> boolean isNotEmpty(Collection<T> list) {
		boolean isEmpty = true;
		if (null != list && !list.isEmpty()) {
			isEmpty = false;
		}
		return !isEmpty;
	}
	
	private static final Pattern baksetnoPattern = Pattern.compile("^[A-Za-z0-9]{1,12}$");
	private static final Pattern diamondNumPattern = Pattern.compile("^[+]?\\d{1,10}$");
	private static final Pattern weightPattern = Pattern.compile("^[+]?(\\d{1,10})(.\\d+)?$");
	
	public static Map<String,Object> checkPackage(@Nonnull PackageInfo pkg) {
		String baksetno = pkg.getBasketno();
		StringBuilder msg = new StringBuilder();
		if(!baksetnoPattern.matcher(baksetno).matches()) {
			msg.append("package :").append(baksetno).append("\n");
			msg.append("invaild packageno: ").append(baksetno).append("\n");
		}
		int diamondsnumber = pkg.getDiamondsnumber();
		String diamondNum = String.valueOf(diamondsnumber);
		if(!diamondNumPattern.matcher(diamondNum).matches()||diamondsnumber==0) {
			msg.append("invaild diamondnumber: ").append(diamondNum).append("\n");
		}
		
		BigDecimal total = pkg.getTotalweight();
		if(!weightPattern.matcher(total.toPlainString()).matches()||total.compareTo(BigDecimal.ZERO)==0) {
			msg.append("invaild total weight: ").append(diamondNum).append("\n");
		}
		
		BigDecimal min = pkg.getMimweight();
		if(!weightPattern.matcher(min.toPlainString()).matches()|| min.compareTo(BigDecimal.ZERO)==0) {
			msg.append("invaild min weight: ").append(diamondNum).append("\n");
		}
		if(total.compareTo(min.multiply(new BigDecimal(diamondsnumber)))<0) {
			msg.append("totalweight,minweight,diamandsnumber don't match.\n");
		}
		
		String code = pkg.getProductcode();
		if(!DiamondApplicationRunner.getProductMap().containsKey(code)) {
			msg.append("unknown product code:").append(code).append("\n");
		}
		
		boolean b = msg.length()==0;
		String rs = b?"success":msg.toString();
		
		return ResultUtil.msgMap(b, rs);
	}
}
