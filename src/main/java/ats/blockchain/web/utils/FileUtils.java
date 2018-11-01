package ats.blockchain.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 
 * @author shuhao.song 2018-9-6 11:45:15 files tools
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
	public static <T> List<T> getFile(HttpServletRequest request, String csvName, String inputName, Class<T> clazz)
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

	/**
	 * 将文件上传到import目录下
	 * 
	 * @param request
	 * @param csvName
	 * @param inputNmae
	 * @return
	 * @throws FileUploadException
	 * @throws IOException
	 */
	public static String getFileName(HttpServletRequest request, String csvName, String inputNmae)
			throws FileUploadException, IOException {
		String filename = "";
		if (ServletFileUpload.isMultipartContent(request)) {
			InputStream ins = null;
			OutputStream ops = null;
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				ins = item.openStream();
				String fieldname = item.getFieldName();
				if (item.isFormField()) {

				} else {
					if (fieldname.equals(inputNmae)) {
						int nRead = 0;
						filename = generateFilename(csvName);
						ops = new FileOutputStream(filename);
						byte[] byBuf = new byte[4096];
						while ((nRead = ins.read(byBuf, 0, 4096)) != -1)
							ops.write(byBuf, 0, nRead);
					}
				}
			}
		}
		return filename;
	}

	private static String generateFilename(String header) {
		SimpleDateFormat simpledf = new SimpleDateFormat("yyyyMMddHHmmss");
		File f = new File("./import/");
		if (!f.exists()) {
			f.mkdirs();
		}
		StringBuilder strbuf = new StringBuilder();
		strbuf.append("./import/");
		strbuf.append(header);
		strbuf.append(StringUtil.FIELDNAMESEP);
		strbuf.append(simpledf.format(new Date(System.currentTimeMillis())));
		strbuf.append(".txt");
		return (strbuf.toString());
	}
	
	public static void main(String[] args)
	{
		String filePath = "E:\\project\\180723AOC-BChain\\20 SourceCode\\webdiamond\\src\\main\\resources\\templates\\basketinfo.csv";
		System.out.println(filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.length()));
	}
	
	
	/**
	 * @author shuhao.song
	 * @param response
	 * @param filePath 文件路径
	 * @param contentType 文件类型
	 * @throws UnsupportedEncodingException
	 */
	 public static void exportFile(HttpServletResponse response, String filePath, String contentType) throws UnsupportedEncodingException
	 {
		 File file = new File(filePath);
		 String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.length());
		 response.setContentType(contentType);
		 response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
		 InputStream in = null;
		 OutputStream out = null;
		 try
		{
			 in = new FileInputStream(filePath);  
		      int len = 0;  
		      byte[] buffer = new byte[1024];
		      response.setCharacterEncoding("UTF-8");  
		      out = response.getOutputStream();
		      while ((len = in.read(buffer)) > 0) 
		      {  
		          out.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });  
		          out.write(buffer, 0, len);  
		      }
		      
		} catch (Exception e)
		{
			// TODO: handle exception
		}finally {
			if(in != null)
			{
				try
				{
					in.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(out != null)
			{
				try
				{
					out.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//待执行
			if(file.exists())
			{
				file.delete();
			}
		}
	 }
	 
	 public static void exportFile(HttpServletResponse response, InputStream inputStream, String fileName,String contentType) throws UnsupportedEncodingException
	 {
		 response.setContentType(contentType);
		 response.setHeader("Content-Disposition",  
			      "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
		 InputStream in = inputStream;
		 try
		{
			 
		      int len = 0;  
		      byte[] buffer = new byte[1024];
		      response.setCharacterEncoding("UTF-8");  
		      OutputStream out = response.getOutputStream();
		      while ((len = in.read(buffer)) > 0) 
		      {  
		          out.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });  
		          out.write(buffer, 0, len);  
		      }
		      
		} catch (Exception e)
		{
			// TODO: handle exception
		}finally {
			if(in != null)
			{
				try
				{
					in.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		 
	 }
	
	
}
