package ats.blockchain.web.cfg;

import java.io.Serializable;
import java.util.List;

public class NodeStruct implements Serializable 
{
	private static final long serialVersionUID = -1814664201736327491L;

	private String myLegalName;
	private String keyStorePassword;
	private String trustStorePassword;
	private String p2pAddress;
	private RPCSettingStruct rpcSettings;
	private List<RPCUserStruct> rpcUsers;
	private NotaryStruct notary;
	
	public String getMyLegalName() {
		return myLegalName;
	}
	public void setMyLegalName(String myLegalName) {
		this.myLegalName = myLegalName;
	}
	public String getKeyStorePassword() {
		return keyStorePassword;
	}
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
	public String getTrustStorePassword() {
		return trustStorePassword;
	}
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}
	public String getP2pAddress() {
		return p2pAddress;
	}
	public void setP2pAddress(String p2pAddress) {
		this.p2pAddress = p2pAddress;
	}
	public RPCSettingStruct getRpcSettings() {
		return rpcSettings;
	}
	public void setRpcSettings(RPCSettingStruct rpcSettings) {
		this.rpcSettings = rpcSettings;
	}
	public List<RPCUserStruct> getRpcUsers() {
		return rpcUsers;
	}
	public void setRpcUsers(List<RPCUserStruct> rpcUsers) {
		this.rpcUsers = rpcUsers;
	}
	public NotaryStruct getNotary() {
		return notary;
	}
	public void setNotary(NotaryStruct notary) {
		this.notary = notary;
	}
}
