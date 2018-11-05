package ats.blockchain.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.csvreader.CsvWriter;

/**
 * 
 * @author shuhao.song <br>
 *         2018-9-6 11:45:15 files tools
 *
 */
public class FileUtils {
	/**
	 * 
	 * @param request
	 * @param csvName
	 * @param inputName
	 * @return
	 * @throws FileUploadException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> List<T> getFile(HttpServletRequest request,String inputName, Class<T> clazz)
			throws FileUploadException, IOException, InstantiationException, IllegalAccessException {
		List<T> list = new ArrayList<T>();
		if (ServletFileUpload.isMultipartContent(request)) {
			InputStream ins = null;
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				try {
					ins = item.openStream();
					String fieldname = item.getFieldName();
					if (!item.isFormField() && fieldname.equals(inputName)) {
						list.addAll(AOCBeanUtils.getObjectFromCsv(ins, clazz));
					}else if(item.isFormField() && fieldname.equals("step")){
						String step = IOUtils.toString(ins, StandardCharsets.UTF_8.name());
						request.setAttribute("step", step);
					}
				} finally {
					if (ins != null) {
						try {
							ins.close();
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return list;
	}

	private static String generateFilename(String path, String prefix, String suffix) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		path = path.replaceAll("[\\]+", "/");
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(path.endsWith("/") ? path + "/" : path);
		strbuf.append(prefix);
		strbuf.append(StringUtil.FIELDNAMESEP);
		strbuf.append(DateFormatUtils.format(new Date(), DateFormatUtils.DATE_FORMAT_y2ms));
		strbuf.append(suffix);
		return (strbuf.toString());
	}

	public static void main(String[] args) {
		String filePath = "E:\\project\\180723AOC-BChain\\20 SourceCode\\webdiamond\\src\\main\\resources\\templates\\basketinfo.csv";
		System.out.println(filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.length()));
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
		String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.length());
		exportFile(response, new FileInputStream(filePath), fileName, contentType);
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
				out.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
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
	public static <T> String generateExportFile(String filePath, String prefix,@Nonnull String[] header,@Nonnull List<T> list) throws IOException, IllegalArgumentException, IllegalAccessException {
		String fileName = generateFilename(filePath, prefix, ".csv");
		CsvWriter writer = new CsvWriter(fileName, ',',Charset.forName("UTF-8"));
		try{
			writer.writeRecord(header);
			ClassMethods clazzMethod = ClassMethodFactory.Instance.getClassMethods(list.get(0).getClass());
			for(T t : list ) {
				for(String fieldName: header) {
					Field field = clazzMethod.getField(fieldName);
					String value= "";
					if(field!=null) {
						Object obj = field.get(t);
						value = obj!=null ?obj.toString():"";
					}
					writer.write(value);
				}
				writer.endRecord();
			}
		}finally {
			if(writer!=null)
			writer.close();
		}
		
		return fileName;

	}

}
