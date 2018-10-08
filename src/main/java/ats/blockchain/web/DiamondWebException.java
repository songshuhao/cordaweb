package ats.blockchain.web;

public class DiamondWebException extends Exception 
{
	private static final long serialVersionUID = 5721830044547698539L;

	private String err;

	public DiamondWebException(String msg)
	{
		super(msg);
		this.err = msg;
	}
	
	public DiamondWebException(String msg,Exception exceptionItem)
	{
		super(msg, exceptionItem);
		this.err = msg;
	}

	@Override
	public String getMessage() {
		return err!=null?err:super.getMessage();
	}
	
	
}
