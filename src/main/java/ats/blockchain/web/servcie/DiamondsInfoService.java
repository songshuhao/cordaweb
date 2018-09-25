package ats.blockchain.web.servcie;

import java.util.List;

import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.model.Diamondsinfo;

public interface DiamondsInfoService {

	boolean addDiamondInfo(DiamondInfoData di);
	
	List<DiamondInfoData> getDiamondInfoByStatus(String... status);
	
	List<DiamondInfoData> submitDiamondList();
	
}
