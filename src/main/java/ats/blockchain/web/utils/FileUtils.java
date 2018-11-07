package ats.blockchain.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvWriter;

/**
 * 
 * @author shuhao.song <br>
 *         2018-9-6 11:45:15 files tools
 *
 */
public class FileUtils {
	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * 解析导入的csv文件（默认,分隔符）
	 * 
	 * @param request
	 * @param csvName
	 * @param inputName
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getFile(HttpServletRequest request, String inputName, Class<T> clazz) throws Exception {
		return getFile(request, inputName, clazz, StringUtil.COMMACHAR);
	}

	/**
	 * 解析导入的csv文件
	 * 
	 * @param request
	 * @param inputName
	 * @param clazz
	 * @param delimiter
	 *            csv分割符
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getFile(HttpServletRequest request, String inputName, Class<T> clazz, char delimiter)
			throws Exception {
		List<T> list = new ArrayList<T>();
		List<FileItem> fileItemList = null;
		fileItemList = parseRequest(request);

		for (FileItem item : fileItemList) {
			if (item.isFormField()) {
				logger.debug("field: {} ,value: {}",item.getFieldName(),item.getString());
			} else {
				String fileName = item.getName();
				logger.debug("field: {} ,value: {}",item.getFieldName(),item.getName());
				int idx = fileName.lastIndexOf('.');
				if (idx <= 0) {
					throw new IOException("invaild file name: " + fileName);
				}

				String suffix = fileName.substring(idx).toLowerCase();
				InputStream inputStream = null;
				try {
					inputStream = item.getInputStream();
					if (Constants.FILE_TYPE_CSV.equals(suffix)) {
						list = AOCBeanUtils.getObjectFromCsv(inputStream, clazz, delimiter);
					} else if (Constants.FILE_TYPE_XLS.equals(suffix) || Constants.FILE_TYPE_XLSX.equals(suffix)) {
						list = ExcelUtil.readExcelContent(suffix, inputStream, clazz);
					}
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
						}
					}
				}

			}

		}

		return list;
	}

	/**
	 * 解析HttpRequest
	 * 
	 * @param request
	 * @param inputName
	 * @return 返回的InputSteam 没有关闭，使用完需要手动关闭
	 * @throws FileUploadException
	 * @throws IOException
	 */
	private static InputStream parseRequest(HttpServletRequest request, String inputName)
			throws FileUploadException, IOException {
		InputStream fileStream = null;
		if (ServletFileUpload.isMultipartContent(request)) {
			InputStream ins = null;
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				ins = item.openStream();
				String fieldname = item.getFieldName();
				if (!item.isFormField() && fieldname.equals(inputName)) {
					fileStream = ins;
				} else if (item.isFormField()) {
					String str = IOUtils.toString(ins, StandardCharsets.UTF_8.name());
					if (fieldname.equals("step")) {
						request.setAttribute("step", str);
					} else if (fieldname.equals("fileName")) {
						request.setAttribute("fileName", str);
					}

					try {
						ins.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return fileStream;
	}

	public static List<FileItem> parseRequest(HttpServletRequest req) throws FileUploadException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存缓冲区，超过后写入临时文件
		factory.setSizeThreshold(10240000);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 设置单个文件的最大上传值
		upload.setFileSizeMax(10002400000l);
		// 设置整个request的最大值
		upload.setSizeMax(10002400000l);
		upload.setHeaderEncoding("UTF-8");

		List<FileItem> items = upload.parseRequest(req);
		return items;
	}

	private static String generateFilename(String path, String prefix, String suffix) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		path = path.replaceAll("[\\\\]+", "/");
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(path.endsWith("/") ? path : path + "/");
		strbuf.append(prefix);
		strbuf.append(StringUtil.FIELDNAMESEP);
		strbuf.append(DateFormatUtils.format(new Date(), DateFormatUtils.DATE_FORMAT_y2ms));
		strbuf.append(suffix);
		return (strbuf.toString());
	}

	/**
	 * @author shuhao.song
	 * @param response
	 * @param filePath
	 *            文件路径
	 * @param contentType
	 *            文件类型
	 * @throws IOException
	 */
	public static void exportFile(HttpServletResponse response, String filePath, String contentType)
			throws IOException {
		String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
		exportFile(response, new FileInputStream(filePath), fileName, contentType);
		Files.delete(Paths.get(filePath));
	}

	public static void exportFile(HttpServletResponse response, InputStream inputStream, String fileName,
			String contentType) throws IOException {
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
		InputStream in = inputStream;
		try {

			int len = 0;
			byte[] buffer = new byte[1024];
			response.setCharacterEncoding("UTF-8");
			OutputStream out = response.getOutputStream();

			while ((len = in.read(buffer)) > 0) {
				// out.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF
				// });
				out.write(buffer, 0, len);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

	}

	/**
	 * 将导出结果写入文件
	 * 
	 * @param filePath
	 *            生成文件的路径
	 * @param prefix
	 *            生成文件名的前缀
	 * @param header
	 *            生成文件的列头
	 * @param list
	 *            文件内容
	 * @return 生成文件的路径+文件名
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static <T> String generateExportFile(String filePath, String prefix, @Nonnull String[] header,
			@Nonnull List<T> list, String fileType)
			throws IOException, IllegalArgumentException, IllegalAccessException {
		String fileName = generateFilename(filePath, prefix, fileType);
		logger.debug("generateExportFile : {}", fileName);
		if (".csv".equals(fileType)) {
			generateCsv(header, list, fileName);
		} else if (".xls".equals(fileType) || ".xlsx".equals(fileType)) {
			FileOutputStream out = new FileOutputStream(fileName);
			ExcelUtil.exportExcel(prefix, fileType, header, list, out);

		}
		return fileName;

	}

	private static <T> void generateCsv(String[] header, List<T> list, String fileName)
			throws IOException, IllegalAccessException {
		CsvWriter writer = new CsvWriter(fileName, ',', Charset.forName("UTF-8"));
		try {
			writer.writeRecord(header);
			ClassMethods clazzMethod = ClassMethodFactory.Instance.getClassMethods(list.get(0).getClass());
			for (T t : list) {
				for (String fieldName : header) {
					Field field = clazzMethod.getField(fieldName);
					String value = "";
					if (field != null) {
						Object obj = field.get(t);
						value = obj != null ? obj.toString() : "";
					}
					writer.write(value);
				}
				writer.endRecord();
			}
		} finally {
			if (writer != null)
				writer.close();
		}
	}

}
