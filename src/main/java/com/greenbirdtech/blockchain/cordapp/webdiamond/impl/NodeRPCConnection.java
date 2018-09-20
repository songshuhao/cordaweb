package com.greenbirdtech.blockchain.cordapp.webdiamond.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.greenbirdtech.blockchain.cordapp.webdiamond.DiamondWebException;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;

public class NodeRPCConnection 
{
	private final static Logger logger = LogManager.getLogger(NodeRPCConnection.class);
	
	private final String serverip;
	private final int port;
	private final String username;
	private final String password;
	
	private CordaRPCConnection cordarpcconn=null;
	private CordaRPCOps cordarpcops=null;
	
	public NodeRPCConnection(String serverip,int port,String username,String password)
	{
		this.serverip = serverip;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public void startConnection() throws DiamondWebException
	{
		CordaRPCClient cordaclient = new CordaRPCClient(new NetworkHostAndPort(serverip,port),CordaRPCClientConfiguration.DEFAULT);
		try
		{
			logger.info("Start cordarpcclient with user:"+username);
			cordarpcconn = cordaclient.start(username, password);
			cordarpcops = cordarpcconn.getProxy();
			logger.info("Corda RPC protocol version:"+cordarpcconn.getServerProtocolVersion());
		}
		catch (Exception ex)
		{
			logger.warn("Failure to start Corda RPC connection:"+ex.toString());
			throw new DiamondWebException(ex.getMessage());
		}
	}
	
	public void closeConnection()
	{
		if (cordarpcconn != null)
		{
			cordarpcconn.notifyServerAndClose();
			cordarpcconn = null;
		}
	}
	
	public CordaRPCOps getCordarpcops()
	{
		if (cordarpcconn != null)
			return(cordarpcops);
		return(null);
	}
}
