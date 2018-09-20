package ats.blockchain.web.cfg;

import java.io.Serializable;

public class RPCUserStruct implements Serializable 
{
	private static final long serialVersionUID = -8256315203408059653L;

	private String user;
	private String password;
	private String[] permissions;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String[] getPermissions() {
		return permissions;
	}
	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}
}
