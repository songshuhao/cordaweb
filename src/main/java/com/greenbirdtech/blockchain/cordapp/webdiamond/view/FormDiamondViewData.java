package com.greenbirdtech.blockchain.cordapp.webdiamond.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.greenbirdtech.blockchain.cordapp.diamond.data.DiamondInfo;
import com.greenbirdtech.blockchain.cordapp.diamond.data.DiamondState;
import com.greenbirdtech.blockchain.cordapp.diamond.util.StringUtil;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.MenuInf;

import net.corda.core.contracts.StateAndRef;

public class FormDiamondViewData 
{
	private final static Logger logger = LogManager.getLogger(FormDiamondViewData.class);
	
	public static void formSingleDiamondView(DiamondState ds,List<DiamondStateData> dsdlist,MenuInf menuinf)
	{
		List<DiamondInfo> dilist = ds.getDiamondinfolist();
		DiamondStateData dsd = new DiamondStateData();
		dsd.setExternalid(ds.getLinearId().getExternalId());
		dsd.setUuid(String.format("%1$d_%2$d",ds.getLinearId().getId().getMostSignificantBits(),ds.getLinearId().getId().getLeastSignificantBits()));
		dsd.setAoc((ds.getAoc() == null) ? StringUtil.UNKNOWNSTR : ds.getAoc().getName().toString());
		dsd.setAuditdate((ds.getAuditdate() == null) ? StringUtil.UNKNOWNSTR : ds.getAuditdate());
		dsd.setAuditor((ds.getAuditor() == null) ? StringUtil.UNKNOWNSTR : ds.getAuditor().getName().toString());
		dsd.setLocation(!StringUtil.isNull(ds.getLocation()) ? ds.getLocation() : StringUtil.UNKNOWNSTR);
		dsd.setLocdate(!StringUtil.isNull(ds.getLocdate()) ? ds.getLocdate() : StringUtil.UNKNOWNSTR);
		dsd.setOwnmgr((ds.getOwnmgr() == null) ? StringUtil.UNKNOWNSTR : ds.getOwnmgr().getName().toString());
		dsd.setOwner(!StringUtil.isNull(ds.getOwner()) ? ds.getOwner() : StringUtil.UNKNOWNSTR);
		dsd.setOwnerdate(!StringUtil.isNull(ds.getOwnerdate()) ? ds.getOwnerdate() : StringUtil.UNKNOWNSTR);
		dsd.setSupplier((ds.getSupplier() == null) ? StringUtil.UNKNOWNSTR : ds.getSupplier().getName().toString());
		dsd.setInitprice((ds.getInitamount() == null) ? StringUtil.UNKNOWNSTR : StringUtil.formTxnprice(ds.getInitamount()));
		dsd.setTxnprice((ds.getAmount() == null) ? StringUtil.UNKNOWNSTR : StringUtil.formTxnprice(ds.getAmount()));
		dsd.setVault((ds.getVault() == null) ? StringUtil.UNKNOWNSTR : ds.getVault().getName().toString());
		dsd.setStatus(ds.getStatus());
		dsd.setStatusstr(((ds.getStatus() > 0) && (ds.getStatus() <= DiamondStateData.STRSTATE.length)) ? DiamondStateData.STRSTATE[ds.getStatus()-1] : StringUtil.UNKNOWNSTR);
		if ((dilist != null) && (dilist.size() > 0))
		{
			List<DiamondInfoData> didlist = new ArrayList<DiamondInfoData>();
			for (DiamondInfo di : dilist)
			{
				DiamondInfoData did = new DiamondInfoData();
				did.setCraftsmandate((di.getCraftsmandate() == null) ? StringUtil.UNKNOWNSTR : di.getCraftsmandate());
				did.setCraftsmanname(!StringUtil.isNull(di.getCraftsmanname()) ? di.getCraftsmanname() : StringUtil.UNKNOWNSTR);
				did.setCutter(!StringUtil.isNull(di.getCutter()) ? di.getCutter() : StringUtil.UNKNOWNSTR);
				did.setDealdate((di.getDealdate() == null) ? StringUtil.UNKNOWNSTR : di.getDealdate());
				did.setDealername(!StringUtil.isNull(di.getDealername()) ? di.getDealername() : StringUtil.UNKNOWNSTR);
				did.setGiacertdate((di.getGiacertdate() == null) ? StringUtil.UNKNOWNSTR : di.getGiacertdate());
				did.setGiacertid(!StringUtil.isNull(di.getGiacertid()) ? di.getGiacertid() : StringUtil.UNKNOWNSTR);
				did.setGiacertinfo(!StringUtil.isNull(di.getGiacertaddinfo()) ? di.getGiacertaddinfo() : StringUtil.UNKNOWNSTR);
				did.setMinedate((di.getMinedate() == null) ? StringUtil.UNKNOWNSTR : di.getMinedate());
				did.setMiner(!StringUtil.isNull(di.getMiner()) ? di.getMiner() : StringUtil.UNKNOWNSTR);
				didlist.add(did);
			}
			menuinf.updateDiamondinfo(dsd.getExternalid(),didlist);
		}
		else
			logger.warn("Cannot find diamond info:"+dsd.getExternalid());
		dsdlist.add(dsd);
	}
	
	public static void formDiamondView(List<StateAndRef<DiamondState>> dslist,List<DiamondStateData> dsdlist,MenuInf menuinf)
	{
		for (StateAndRef<DiamondState> dstate : dslist)
		{
			DiamondState ds = dstate.getState().getData();
			List<DiamondInfo> dilist = ds.getDiamondinfolist();
			DiamondStateData dsd = new DiamondStateData();
			dsd.setExternalid(ds.getLinearId().getExternalId());
			dsd.setUuid(String.format("%1$d_%2$d",ds.getLinearId().getId().getMostSignificantBits(),ds.getLinearId().getId().getLeastSignificantBits()));
			dsd.setAoc((ds.getAoc() == null) ? StringUtil.UNKNOWNSTR : ds.getAoc().getName().toString());
			dsd.setAuditdate((ds.getAuditdate() == null) ? StringUtil.UNKNOWNSTR : ds.getAuditdate());
			dsd.setAuditor((ds.getAuditor() == null) ? StringUtil.UNKNOWNSTR : ds.getAuditor().getName().toString());
			dsd.setLocation(!StringUtil.isNull(ds.getLocation()) ? ds.getLocation() : StringUtil.UNKNOWNSTR);
			dsd.setLocdate(!StringUtil.isNull(ds.getLocdate()) ? ds.getLocdate() : StringUtil.UNKNOWNSTR);
			dsd.setOwnmgr((ds.getOwnmgr() == null) ? StringUtil.UNKNOWNSTR : ds.getOwnmgr().getName().toString());
			dsd.setOwner(!StringUtil.isNull(ds.getOwner()) ? ds.getOwner() : StringUtil.UNKNOWNSTR);
			dsd.setOwnerdate(!StringUtil.isNull(ds.getOwnerdate()) ? ds.getOwnerdate() : StringUtil.UNKNOWNSTR);
			dsd.setSupplier((ds.getSupplier() == null) ? StringUtil.UNKNOWNSTR : ds.getSupplier().getName().toString());
			dsd.setInitprice((ds.getInitamount() == null) ? StringUtil.UNKNOWNSTR : StringUtil.formTxnprice(ds.getInitamount()));
			dsd.setTxnprice((ds.getAmount() == null) ? StringUtil.UNKNOWNSTR : StringUtil.formTxnprice(ds.getAmount()));
			dsd.setVault((ds.getVault() == null) ? StringUtil.UNKNOWNSTR : ds.getVault().getName().toString());
			dsd.setStatus(ds.getStatus());
			dsd.setStatusstr(((ds.getStatus() > 0) && (ds.getStatus() <= DiamondStateData.STRSTATE.length)) ? DiamondStateData.STRSTATE[ds.getStatus()-1] : StringUtil.UNKNOWNSTR);
			if ((dilist != null) && (dilist.size() > 0))
			{
				List<DiamondInfoData> didlist = new ArrayList<DiamondInfoData>();
				for (DiamondInfo di : dilist)
				{
					DiamondInfoData did = new DiamondInfoData();
					did.setCraftsmandate((di.getCraftsmandate() == null) ? StringUtil.UNKNOWNSTR : di.getCraftsmandate());
					did.setCraftsmanname(!StringUtil.isNull(di.getCraftsmanname()) ? di.getCraftsmanname() : StringUtil.UNKNOWNSTR);
					did.setCutter(!StringUtil.isNull(di.getCutter()) ? di.getCutter() : StringUtil.UNKNOWNSTR);
					did.setDealdate((di.getDealdate() == null) ? StringUtil.UNKNOWNSTR : di.getDealdate());
					did.setDealername(!StringUtil.isNull(di.getDealername()) ? di.getDealername() : StringUtil.UNKNOWNSTR);
					did.setGiacertdate((di.getGiacertdate() == null) ? StringUtil.UNKNOWNSTR : di.getGiacertdate());
					did.setGiacertid(!StringUtil.isNull(di.getGiacertid()) ? di.getGiacertid() : StringUtil.UNKNOWNSTR);
					did.setGiacertinfo(!StringUtil.isNull(di.getGiacertaddinfo()) ? di.getGiacertaddinfo() : StringUtil.UNKNOWNSTR);
					did.setMinedate((di.getMinedate() == null) ? StringUtil.UNKNOWNSTR : di.getMinedate());
					did.setMiner(!StringUtil.isNull(di.getMiner()) ? di.getMiner() : StringUtil.UNKNOWNSTR);
					didlist.add(did);
				}
				menuinf.updateDiamondinfo(dsd.getExternalid(),didlist);
			}
			else
				logger.warn("Cannot find diamond info:"+dsd.getExternalid());
			dsdlist.add(dsd);
		}
	}
	
	public static void formDiamondStateView(List<StateAndRef<DiamondState>> dslist,List<DiamondStateData> dsdlist)
	{
		for (StateAndRef<DiamondState> dstate : dslist)
		{
			DiamondState ds = dstate.getState().getData();
			DiamondStateData dsd = new DiamondStateData();
			dsd.setExternalid(ds.getLinearId().getExternalId());
			dsd.setUuid(String.format("%1$x_%2$x",ds.getLinearId().getId().getMostSignificantBits(),ds.getLinearId().getId().getLeastSignificantBits()));
			dsd.setAoc((ds.getAoc() == null) ? StringUtil.UNKNOWNSTR : ds.getAoc().getName().toString());
			dsd.setAuditdate((ds.getAuditdate() == null) ? StringUtil.UNKNOWNSTR : ds.getAuditdate());
			dsd.setAuditor((ds.getAuditor() == null) ? StringUtil.UNKNOWNSTR : ds.getAuditor().getName().toString());
			dsd.setLocation(!StringUtil.isNull(ds.getLocation()) ? ds.getLocation() : StringUtil.UNKNOWNSTR);
			dsd.setLocdate(!StringUtil.isNull(ds.getLocdate()) ? ds.getLocdate() : StringUtil.UNKNOWNSTR);
			dsd.setOwnmgr((ds.getOwnmgr() == null) ? StringUtil.UNKNOWNSTR : ds.getOwnmgr().getName().toString());
			dsd.setOwner(!StringUtil.isNull(ds.getOwner()) ? ds.getOwner() : StringUtil.UNKNOWNSTR);
			dsd.setOwnerdate(!StringUtil.isNull(ds.getOwnerdate()) ? ds.getOwnerdate() : StringUtil.UNKNOWNSTR);
			dsd.setSupplier((ds.getSupplier() == null) ? StringUtil.UNKNOWNSTR : ds.getSupplier().getName().toString());
			dsd.setInitprice((ds.getInitamount() == null) ? StringUtil.UNKNOWNSTR : StringUtil.formTxnprice(ds.getInitamount()));
			dsd.setTxnprice((ds.getAmount() == null) ? StringUtil.UNKNOWNSTR : StringUtil.formTxnprice(ds.getAmount()));
			dsd.setVault((ds.getVault() == null) ? StringUtil.UNKNOWNSTR : ds.getVault().getName().toString());
			dsd.setStatus(ds.getStatus());
			dsd.setStatusstr(((ds.getStatus() > 0) && (ds.getStatus() <= DiamondStateData.STRSTATE.length)) ? DiamondStateData.STRSTATE[ds.getStatus()-1] : StringUtil.UNKNOWNSTR);
			dsdlist.add(dsd);
		}
	}
}
