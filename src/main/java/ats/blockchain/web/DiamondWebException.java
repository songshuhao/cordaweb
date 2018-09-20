package ats.blockchain.web;

import java.util.ArrayList;
import java.util.List;

import com.greenbirdtech.blockchain.cordapp.webdiamond.util.StringUtil;

public class DiamondWebException extends Exception 
{
	private static final long serialVersionUID = 5721830044547698539L;

	protected List<Exception> errItems = new ArrayList<Exception>();
	private String msg=null;
	
	public DiamondWebException(Throwable cause) {
		super(cause);
	}

	public DiamondWebException(String msg)
	{
		this.msg = msg;
	}
	
	public DiamondWebException(String msg,Exception exceptionItem)
	{
		this.msg = msg;
		errItems.add(exceptionItem);
	}
	
	public DiamondWebException(String msg,List<Exception> listofexception)
	{
		this.msg = msg;
		errItems.addAll(listofexception);
	}
	
	public String getMsg()
	{
		return(msg);
	}
	
	@Override
	public String toString()
	{
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(DiamondWebException.class.getName());
		strbuf.append(':');
		if (!StringUtil.isNull(msg))
			strbuf.append(msg);
		strbuf.append('\n');
		for (int i=errItems.size()-1; i>=0; i--)
		{
			Exception errItem = errItems.get(i);
			strbuf.append(errItem.toString());
			if (i > 0)
				strbuf.append('\n');
		}
		return(strbuf.toString());
	}
}
