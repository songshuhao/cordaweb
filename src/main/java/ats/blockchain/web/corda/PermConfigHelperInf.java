package ats.blockchain.web.corda;

import ats.blockchain.web.cfg.RPCSettingStruct;
import ats.blockchain.web.cfg.RPCUserStruct;

public interface PermConfigHelperInf 
{
	public String getPerm();
	public int getRole();
	public RPCSettingStruct getRpcsettingstruct();
	public RPCUserStruct getRpcuserstruct();
}
