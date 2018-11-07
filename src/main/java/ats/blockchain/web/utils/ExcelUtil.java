package ats.blockchain.web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtil {

	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
	private static Pattern isNumber = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");

	/**
	 * 这是一个通用的方法,利用了JAVA的反射机制,可以将放置在JAVA集合中并且符合一定条件的数据以EXCEL的形式输出到指定IO设备上
	 * 
	 * @param sheetName
	 *            表格标题名
	 * @param headers
	 *            表格属性列名对象
	 * @param dataset
	 *            需要显示的数据集合，集合中一定要放置符合javabean风格的类的对象。此方法支持的
	 *            javabean属性的数据类型有基本数据类型以String,Date
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @throws IOException
	 */
	public static <T> void exportExcel(String sheetName, String fileType, String[] header, Collection<T> dataset,
			OutputStream out) throws IOException {
		if (header == null || header.length == 0) {
			return;
		}

		// 声明一个工作簿
		Workbook workbook = null;
		if (".xls".equals(fileType)) {
			workbook = new HSSFWorkbook();
		} else if (".xlsx".equals(fileType)) {
			workbook = new XSSFWorkbook();
		}
		if (workbook == null) {
			throw new IllegalArgumentException("unsupport file type: " + fileType);
		}

		// 生成一个表格
		Sheet sheet = workbook.createSheet(sheetName);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth(15);
		// 设置单元格背景色
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

		// 产生表格标题行
		Row row = sheet.createRow(0);
		int headerSize = header.length;
		for (int i = 0; i < headerSize; i++) {
			Cell cell = row.createCell(i);
			String exportHeader = header[i];
			HSSFRichTextString text = new HSSFRichTextString(exportHeader);
			cell.setCellValue(text);// 把数据放到单元格中
			cell.setCellStyle(style);
		}

		// 遍历集合数据,产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 1;
		while (it.hasNext()) {
			row = sheet.createRow(index++);
			T t = it.next();
			for (int k = 0; k < header.length; k++) {
				String column = header[k];
				Cell cell = row.createCell(k);
				Object value = AOCBeanUtils.getFieldValue(t, column);
				if (value instanceof String) {
					cell.setCellValue((String) value);
					cell.setCellType(CellType.STRING);
				} else if (value instanceof Boolean) {
					cell.setCellValue((Boolean) value);
					cell.setCellType(CellType.BOOLEAN);
				} else if (value instanceof Date) {
					cell.setCellValue((Date) value);
				} else {
					cell.setCellValue(value == null ? "" : value.toString());
					cell.setCellType(CellType.STRING);
				}
				logger.debug("set {}'s cell value: {} ", column, value);
			}
		}
		try {
			logger.debug("write to {} ", sheetName);
			workbook.write(out);
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				logger.warn("excel close error:", e);
			}
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 根据指定的文件头返回excel表格中的数据对象
	 * 
	 * @param headerList
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> readExcelContent(String fileType, InputStream is, Class<T> target) throws Exception {
		// 声明一个工作簿
		 Workbook wb = WorkbookFactory.create(is);
		if (wb == null) {
			throw new IllegalArgumentException("unsupport file type: " + fileType);
		}

		Sheet sheet = wb.getSheetAt(0);
		Row row = sheet.getRow(0);
		// 标题总列数
		int colNum = row.getPhysicalNumberOfCells();
		String[] title = new String[colNum];
		for (int i = 0; i < colNum; i++) {
			String stringCellValue = row.getCell(i).getStringCellValue();
			title[i] = stringCellValue;
		}

		sheet = wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		List<T> exportList = new ArrayList<T>(rowNum - 1);
		row = sheet.getRow(0);
		String clazzName = target.getSimpleName();
		// 正文内容应该从第二行开始,第一行为表头的标题
		for (int j = 1; j <= rowNum; j++) {
			row = sheet.getRow(j);
			T obj = target.newInstance();
			int emptyCount = 0;
			for (int t = 0; t < colNum; t++) {
				String fieldName = title[t];
				Object value = getCellFormatValue(row.getCell(t));
				if (value == null || value.toString().trim().length() == 0) {
					emptyCount++;
					continue;
				}
				setFieldValue(obj, fieldName, value);
				logger.trace("set {} : {}={}  ", clazzName, fieldName, value);
			}

			if (emptyCount != colNum) {
				exportList.add(obj);
			} else {
				logger.debug("empty row don't add");
			}
		}
		return exportList;
	}

	/**
	 * 
	 * @Title: getCellFormatValue @Description: TODO @param @param
	 *         cell @param @return @return Object @throws
	 */
	@SuppressWarnings("deprecation")
	private static Object getCellFormatValue(Cell cell) {
		Object cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			final int cellType = cell.getCellType();
			switch (cellType) {
			case Cell.CELL_TYPE_FORMULA: {
				cellvalue = cell.getCellFormula();
				break;
			}
			case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
				// 取得当前Cell的数值
				cell.getCellStyle().getDataFormat();
				if (DateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式
					// data格式是带时分秒的：2013-7-10 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();
					// data格式是不带带时分秒的：2013-7-10
					Date date = cell.getDateCellValue();
					short format = cell.getCellStyle().getDataFormat();
					/**
					 * yyyy-MM-dd----- 14<br>
					 * yyyy年m月d日--- 31 <br>
					 * yyyy年m月------- 57<br>
					 * m月d日 ---------- 58<br>
					 * HH:mm----------- 20 <br>
					 * h时mm分 ------- 32
					 */
					String sdf = "";
					if (format == 14 || format == 31 || format == 57 || format == 58) {
						// 日期
						sdf = DateFormatUtils.DATE_FORMAT;
					} else if (format == 20 || format == 32) {
						// 时间
						sdf = DateFormatUtils.TIME_FORMAT;
					} else {
						sdf = DateFormatUtils.DATE_TIME_FORMAT;
					}
					cellvalue = DateFormatUtils.format(date, sdf);
				} else {
					cellvalue = cell.getNumericCellValue();
				}
				break;
			case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				cellvalue = cell.getBooleanCellValue();
				break;
			default:// 默认的Cell值
				cellvalue = "";
				logger.warn("unsupport type: {}", cell.getCellType());
				break;
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;
	}

	/**
	 * 给指定对象的field赋值
	 * 
	 * @param src
	 * @param fieldName
	 * @param value
	 */
	public static <T> void setFieldValue(T src, String fieldName, Object value) {
		if (src == null) {
			return;
		}
		String clazzName = src.getClass().getName();
		ClassMethods clzM = ClassMethodFactory.Instance.getClassMethods(src.getClass());
		Field cf = clzM.getField(fieldName);
		if (cf == null) {
			logger.warn("no field {} in class: {}", fieldName, clazzName);
			return;
		}

		try {
			cf.setAccessible(true);
			value = convertType(cf.getType(), value);
			cf.set(src, value);
		} catch (IllegalArgumentException e) {
			logger.error("getField {} error:{}", fieldName, e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error("getField {} error:{}", fieldName, e.getMessage());
		}
	}

	private static Object convertType(Class<?> type, Object value) {
		Class<?> valType = value.getClass();
		if (type.equals(valType)) {
			return value;
		}
		Object obj = null;
		if (BigDecimal.class.equals(type)) {
			if (value instanceof String) {
				String valStr = (String) value;
				if (isNumber.matcher(valStr == null ? "" : valStr).matches()) {
					obj = new BigDecimal((String) value);
				} else {
					obj = null;
				}
			} else if (value instanceof Double) {
				obj = BigDecimal.valueOf((Double) value);
			} else if (value instanceof Float) {
				obj = BigDecimal.valueOf((Float) value);
			} else if (value instanceof Number) {
				obj = new BigDecimal(((Number) value).longValue());
			}
		} else if (String.class.equals(type)) {
			if (value instanceof Date) {
				obj = DateFormatUtils.format((Date) value, DateFormatUtils.DATE_FORMAT);
			}else if(value instanceof Number){
				obj = String.valueOf(((Number) value).longValue());
			}else {
				obj = value.toString();
			}
		} else if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
			if(value instanceof String) {
				obj = Integer.valueOf((String)value);
			}else if (value instanceof Number) {
				obj = ((Number)value).intValue();
			}
		}  else {
			obj = value;
		}

		return obj;
	}

}
