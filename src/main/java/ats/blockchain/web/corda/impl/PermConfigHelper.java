package ats.blockchain.web.corda.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonclawson.jackson.dataformat.hocon.HoconFactory;

import ats.blockchain.web.cfg.NodeStruct;
import ats.blockchain.web.cfg.RPCSettingStruct;
import ats.blockchain.web.cfg.RPCUserStruct;
import ats.blockchain.web.corda.PermConfigHelperInf;

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
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	public PermConfigHelper()
	{
	}
	
	public void init()
	{
		ObjectMapper objmap = new ObjectMapper(new HoconFactory());
		objmap.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		InputStream nodeStream = null;
		try
		{
			Resource nodeResource = resourceLoader.getResource(nodeconf);		
			nodeStream = nodeResource.getInputStream();
			objmap.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			logger.info("Reading node information:"+nodeconf);
			nodestruct = objmap.readValue(nodeStream,NodeStruct.class);
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
		finally
		{
			if(nodeStream != null) {
				try {
					nodeStream.close();
				} catch (IOException e) {
				}
			}
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
