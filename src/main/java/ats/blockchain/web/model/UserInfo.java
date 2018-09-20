package ats.blockchain.web.model;

import java.io.Serializable;

public class UserInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	
	private String userName;
	
	private String password;
	
	private String role;
	
	private String myLegalName;

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}

	public String getMyLegalName()
	{
		return myLegalName;
	}

	public void setMyLegalName(String myLegalName)
	{
		this.myLegalName = myLegalName;
	}
	
	
}
