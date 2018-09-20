package com.greenbirdtech.blockchain.cordapp.webdiamond;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.greenbirdtech.blockchain.cordapp.diamond.data.DiamondInfo;
import com.greenbirdtech.blockchain.cordapp.diamond.data.DiamondState;
import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.DiamondTradeApi;
import com.greenbirdtech.blockchain.cordapp.webdiamond.util.StringUtil;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.MenuItem;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.UserSession;
import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.SessionListener;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.MenuInf;
import com.greenbirdtech.blockchain.cordapp.webdiamond.view.DiamondStateData;
import com.greenbirdtech.blockchain.cordapp.webdiamond.view.FormDiamondViewData;

import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;

@Controller
public class IssueDiamondController 
{
	private final static int DIAMONDUPLOAD=1;
	private final static int ISSUEDIAMOND=2;
	private final static int TMPBUFSIZE=4096;
	
	private final static String AOCSTR="AOC";
	private final static String EXTERNALIDSTR="EXTERNALID";
	private final static String CURRENCYSTR="CURRENCY";
	private final static String AMOUNTSTR="AMOUNT";
	
	private final static Logger logger = LogManager.getLogger(IssueDiamondController.class);
	
	@Autowired
	private MenuInf menuinf;
	
	@RequestMapping(value = "/issuediamond", method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView issuediamond(final HttpServletRequest request,final HttpServletResponse response) throws Exception
	{
		List<MenuItem> menuitemlist=null;		
		response.setCharacterEncoding(StringUtil.UTF8STR);
		if (menuinf.checkUserPermission(request))
		{
			int reqcode=0;
			int recordcount=MenuItem.NORECORDCODE;
			String retStr=null;
			ModelAndView mav = new ModelAndView("issuediamond");
			UserSession usersession = (UserSession)request.getSession(false).getAttribute(SessionListener.LOGONSESSION);
			List<String> userlist = new ArrayList<String>();
			menuitemlist = usersession.getMenuitemlist();
			DiamondTradeApi diamondtradeinf = menuinf.getTradediamondinf();
			if (diamondtradeinf != null)
			{
				InputStream ins = null;
				OutputStream ops = null;
				String aoc=null;
				String curr=null;
				String externalid=null;
				long price=(long)0;
				recordcount = 0;
				SimpleDateFormat dataDF = new SimpleDateFormat("yyyy-MM-dd");
				if (ServletFileUpload.isMultipartContent(request))
				{
					try
					{
						ServletFileUpload upload = new ServletFileUpload();
						FileItemIterator iter = upload.getItemIterator(request);
						String filename=null;
						while (iter.hasNext())
						{
							FileItemStream item = iter.next();
							ins = item.openStream();
							String fieldname = item.getFieldName();
							if (item.isFormField())
							{
								String str = Streams.asString(ins);
								if (fieldname.equals(MenuItem.REQCODE))
								{
									try
									{
										reqcode = Integer.parseInt(str);
									}
									catch (NumberFormatException nfe)
									{
									}
								}
								else if (fieldname.equals(AOCSTR))
									aoc = new String(str);
								else if (fieldname.equals(EXTERNALIDSTR))
									externalid = new String(str);
								else if (fieldname.equals(CURRENCYSTR))
									curr = new String(str);
								else if (fieldname.equals(AMOUNTSTR))
								{
									try
									{
										price = Long.parseLong(str);
									}
									catch (NumberFormatException nfe)
									{
									}
								}
								logger.info(String.format("Upload form %1$s:%2$s",item.getFieldName(),str));
							}
							else
							{
								if (fieldname.equals("uploadFormElement"))
								{
									int nRead=0;
									filename = generateFilename("diamondinfo");
									ops = new FileOutputStream(filename);
									byte[] byBuf = new byte[TMPBUFSIZE];
									while ((nRead=ins.read(byBuf,0,TMPBUFSIZE)) != -1)
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
						if ((reqcode == DIAMONDUPLOAD) && !StringUtil.isNull(filename) && !StringUtil.isNull(aoc) && !StringUtil.isNull(externalid) &&
								!StringUtil.isNull(curr) && (price > 0))
						{
							List<DiamondInfo> dilist = extractDiamondfile(filename,dataDF);
							if ((dilist != null) && (dilist.size() > 0))
							{
//								try
//								{
////									retStr = diamondtradeinf.issueDiamond(aoc,externalid,dilist);
//								}
//								catch (DiamondWebException de)
//								{
//									logger.warn("Failure to issue diamond from upload file:"+de.toString());
//									retStr = de.getMsg();
//								}
							}
							else
								retStr = "Invalid diamond file";
						}
						else
							retStr = "Invalid parameter";
					}
					catch (FileUploadException fue)
					{
						logger.warn("Failure to upload file:"+fue.toString());
						retStr = "File upload error";
					}
					catch (IOException ioe)
					{
						logger.warn("Failure to process file upload:"+ioe.toString());
						retStr = "File processing error";
					}
					finally
					{
						if (ops != null)
						{
							try
							{
								ops.close();
							}
							catch (IOException ioe)
							{
							}
						}
						if (ins != null)
						{
							try
							{
								ins.close();
							}
							catch (IOException ioe)
							{
							}
						}
					}
				}
				else
				{
					String reqstr = request.getParameter(MenuItem.REQCODE);
					if (!StringUtil.isNull(reqstr))
					{
						try
						{
							reqcode = Integer.parseInt(reqstr);
						}
						catch (NumberFormatException nfe)
						{
						}
					}
					reqstr = request.getParameter(MenuItem.REQPARAM);
					if (reqcode == ISSUEDIAMOND)
					{
						retStr = MenuItem.INVALIDPARAM;
						aoc = request.getParameter(AOCSTR);
						externalid = request.getParameter(EXTERNALIDSTR);
						curr = request.getParameter(CURRENCYSTR);
						String strTemp = request.getParameter(AMOUNTSTR);
						if (!StringUtil.isNull(strTemp))
						{
							try
							{
								price = Long.parseLong(strTemp);
							}
							catch (NumberFormatException nfe)
							{
							}
						}
						if (!StringUtil.isNull(reqstr) && !StringUtil.isNull(aoc) && !StringUtil.isNull(externalid) && !StringUtil.isNull(curr) && (price > 0))
						{
							DiamondInfo di = extractDiamondinfo(reqstr,dataDF,StringUtil.PIPESTR);
							if (di != null)
							{
								List<DiamondInfo> dilist = new ArrayList<DiamondInfo>();
								dilist.add(di);
//								try
//								{
//									retStr = diamondtradeinf.issueDiamond(aoc,externalid,dilist,new Amount<Currency>(price,Currency.getInstance(curr)));
//								}
//								catch (DiamondWebException de)
//								{
//									logger.warn("Failure to issue single diamond:"+de.toString());
//									retStr = de.getMsg();
//								}
							}
							else
								retStr = "Invalid diamond information";
						}
					}
				}
				List<StateAndRef<DiamondState>> issuediamondlist = diamondtradeinf.getIssuedDiamond();
				if ((issuediamondlist != null) && (issuediamondlist.size() > 0))
				{
					List<DiamondStateData> dsdlist = new ArrayList<DiamondStateData>();
					FormDiamondViewData.formDiamondView(issuediamondlist,dsdlist,menuinf);
					mav.addObject("DIAMONDLIST",dsdlist);
					recordcount = dsdlist.size();
				}
			}
			else
				retStr = MenuItem.RPCERROR;
			try
			{
				Map<String,List<String>> mapUserinfo = diamondtradeinf.getOtherUser();
				if ((mapUserinfo != null) && (mapUserinfo.size() > 0))
				{
					for (Map.Entry<String,List<String>> kvp : mapUserinfo.entrySet())
					{
						for (String username : kvp.getValue())
							userlist.add(username);
					}
				}
			}
			catch (DiamondWebException dwe)
			{
				logger.warn("Failure to get other user:"+dwe.getMsg());
			}
			mav.addObject("USERLIST",userlist);
			mav.addObject(MenuItem.RECORDCOUNT,Integer.valueOf(recordcount));
			if (!StringUtil.isNull(retStr))
				mav.addObject(MenuItem.REQRESULT,retStr);
			mav.addObject(MenuItem.CURRUSER,menuinf.getUsername());
			mav.addObject("CURRLIST",MenuItem.CURRENCYLIST);
			mav.addObject(MenuItem.MENUCODE,menuitemlist);
			return(mav);
		}
		request.getRequestDispatcher("/logon.jsp").forward(request,response);
		return(null);
	}
	
	private String generateFilename(String header)
	{
		SimpleDateFormat simpledf = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuilder strbuf = new StringBuilder();
		strbuf.append("./import/");
		strbuf.append(header);
		strbuf.append(StringUtil.FIELDNAMESEP);
		strbuf.append(simpledf.format(new Date(System.currentTimeMillis())));
		strbuf.append(".txt");
		return(strbuf.toString());
	}
	
	private List<DiamondInfo> extractDiamondfile(String filename,SimpleDateFormat dataDF)
	{
		int nLine=0;
		List<DiamondInfo> dilist=null;
		BufferedReader bufread=null;
		try
		{
			bufread = new BufferedReader(new InputStreamReader(new FileInputStream(filename),StringUtil.UTF8STR));
			String textline=null;
			dilist = new ArrayList<DiamondInfo>();
			while ((textline=bufread.readLine()) != null)
			{
				++nLine;
				if ((textline.length() == 0) && (textline.charAt(0) == StringUtil.COMMENTCHAR))
					continue;
				DiamondInfo di = extractDiamondinfo(textline,dataDF,StringUtil.TABSTR);
				if (di != null)
					dilist.add(di);
				else
					logger.warn(String.format("Error in line %1$d of input diamond file",nLine));
			}
		}
		catch (IOException ioe)
		{
			logger.warn("Failure to process diamond file:"+ioe.toString());
		}
		finally
		{
			if (bufread != null)
			{
				try
				{
					bufread.close();
				}
				catch (IOException ioe1)
				{
				}
				bufread = null;
			}
		}
		return(dilist);
	}
	
	private DiamondInfo extractDiamondinfo(String textline,SimpleDateFormat dataDF,String fieldsep)
	{
		DiamondInfo di=null;
		String[] strText = textline.split(fieldsep);
		if ((strText.length >= 3) && !StringUtil.isNull(strText[0]) && !StringUtil.isNull(strText[1]) && !StringUtil.isNull(strText[2]))
		{
			try
			{
				String giacertdate = (dataDF.parse(strText[1]) != null) ? strText[1] : null;
				String miner = ((strText.length >= 4) && !StringUtil.isNull(strText[3])) ? strText[3] : null;
				String minedate = ((strText.length >= 5) && !StringUtil.isNull(strText[4]) && (dataDF.parse(strText[4]) != null)) ? strText[4] : null;
				String cutter = ((strText.length >= 6) && !StringUtil.isNull(strText[5])) ? strText[5] : null;
				String craftsmanname = ((strText.length >= 7) && !StringUtil.isNull(strText[6])) ? strText[6] : null;
				String craftsmandate = ((strText.length >= 8) && !StringUtil.isNull(strText[7]) && (dataDF.parse(strText[7]) != null)) ? strText[7] : null;
				String dealername = ((strText.length >= 9) && !StringUtil.isNull(strText[8])) ? strText[8] : null;
				String dealdate = ((strText.length >= 10) && !StringUtil.isNull(strText[9]) && (dataDF.parse(strText[9]) != null)) ? strText[9] : null;
				di = new DiamondInfo(strText[0],giacertdate,strText[2],miner,minedate,cutter,craftsmanname,craftsmandate,dealername,dealdate);
			}
			catch (ParseException pe)
			{
				logger.warn("Invalid date format:"+pe.toString());
				di = null;
			}
		}
		return(di);
	}
}
