package com.greenbirdtech.blockchain.cordapp.webdiamond.impl;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenbirdtech.blockchain.cordapp.webdiamond.cfg.NodeStruct;
import com.greenbirdtech.blockchain.cordapp.webdiamond.cfg.RPCSettingStruct;
import com.greenbirdtech.blockchain.cordapp.webdiamond.cfg.RPCUserStruct;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.PermConfigHelperInf;
import com.jasonclawson.jackson.dataformat.hocon.HoconFactory;

public class PermConfigHelper implements PermConfigHelperInf 
{
	public final static int NOTARYVALIDROLE=1;
	public final static int NOTARYNONVALIDROLE=2;
	public final static int SUPPLIERROLE=3;
	public final static int AOCROLE=4;
	public final static int OWNMGRROLE=5;
	public final static int AUDITROLE=6;
	public final static int VAULTROLE=7;
	
	private final static String[] ROLEPERM={"1100000","1100000","1010000","1101111","1101000","1100010","1100001"};
	
	private final static Logger logger = LogManager.getLogger(PermConfigHelper.class);
	
	private int role=0;
	private NodeStruct nodestruct=null;
	
	@Value("${corda.node}")
	private String nodeconf;
	
	public PermConfigHelper()
	{
	}
	
	public void init()
	{
		ObjectMapper objmap = new ObjectMapper(new HoconFactory());
		try
		{
			logger.info("Reading node information:"+nodeconf);
			nodestruct = objmap.readValue(ResourceUtils.getFile(nodeconf),NodeStruct.class);
		}
		catch (JsonMappingException jme)
		{
			logger.warn("Failure to parse node.conf:"+jme.toString());
		}
		catch (JsonParseException jpe)
		{
			logger.warn("Failure to parse node.conf:"+jpe.toString());
		}
		catch (IOException ioe)
		{
			logger.warn("Failure to parse node.conf:"+ioe.toString());
		}
	}
	
	@Override
	public int getRole()
	{
		return(role);
	}
	
	@Override
	public String getPerm() 
	{
		String perm=null;
		if ((role > 0) && (role <= ROLEPERM.length))
			perm = ROLEPERM[role-1];
		return(perm);
	}

	@Override
	public RPCSettingStruct getRpcsettingstruct() 
	{
		if ((nodestruct != null) && (nodestruct.getRpcSettings() != null))
			return(nodestruct.getRpcSettings());
		return(null);
	}

	@Override
	public RPCUserStruct getRpcuserstruct() 
	{
		if ((nodestruct != null) && (nodestruct.getRpcUsers() != null) && (nodestruct.getRpcUsers().size() > 0))
			return(nodestruct.getRpcUsers().get(0));
		return(null);
	}

}
