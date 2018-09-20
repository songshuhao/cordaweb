package ats.blockchain.web.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 
 * @author shuhao.song
 * 2018-9-6 11:45:15
 * files tools
 *
 */
public class FileUtils
{
	/**
	 * 
	 * @param request
	 * @param csvName
	 * @param inputNmae
	 * @return
	 * @throws FileUploadException
	 * @throws IOException
	 */
	public static String getFile(HttpServletRequest request,String csvName,String inputNmae) throws FileUploadException, IOException
	{
		String filename = "";
		if (ServletFileUpload.isMultipartContent(request))
		{
			InputStream ins = null;
			OutputStream ops = null;
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext())
			{
				FileItemStream item = iter.next();
				ins = item.openStream();
				String fieldname = item.getFieldName();
				if (item.isFormField())
				{
					
				}else
				{
					if (fieldname.equals(inputNmae))
					{
						int nRead=0;
						filename = generateFilename(csvName);
						ops = new FileOutputStream(filename);
						byte[] byBuf = new byte[4096];
						while ((nRead=ins.read(byBuf,0,4096)) != -1)
							ops.write(byBuf,0,nRead);
					}
				}
				if (ins != null)
				{
					ins.close();
					ins = null;
				}
				if (ops != null)
				{
					ops.close();
					ops = null;
				}
			}
		}
		return filename;
	}
	
	private static String generateFilename(String header)
	{
		SimpleDateFormat simpledf = new SimpleDateFormat("yyyyMMddHHmmss");
		File f = new File("./import/");
		if(!f.exists()) {
			f.mkdirs();
		}
		StringBuilder strbuf = new StringBuilder();
		strbuf.append("./import/");
		strbuf.append(header);
		strbuf.append(StringUtil.FIELDNAMESEP);
		strbuf.append(simpledf.format(new Date(System.currentTimeMillis())));
		strbuf.append(".txt");
		return(strbuf.toString());
	}
}
