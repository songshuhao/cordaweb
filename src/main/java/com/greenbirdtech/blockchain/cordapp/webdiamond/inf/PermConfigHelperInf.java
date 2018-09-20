package com.greenbirdtech.blockchain.cordapp.webdiamond.inf;

import com.greenbirdtech.blockchain.cordapp.webdiamond.cfg.RPCSettingStruct;
import com.greenbirdtech.blockchain.cordapp.webdiamond.cfg.RPCUserStruct;

public interface PermConfigHelperInf 
{
	public String getPerm();
	public int getRole();
	public RPCSettingStruct getRpcsettingstruct();
	public RPCUserStruct getRpcuserstruct();
}
