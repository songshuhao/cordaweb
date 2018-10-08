package ats.blockchain.web.servcie;

import java.util.List;
import java.util.Map;

import ats.blockchain.web.bean.DiamondInfoData;

public interface DiamondsInfoService {

	Map<String,Object> addDiamondInfo(DiamondInfoData di);
	
	List<DiamondInfoData> getDiamondInfoByStatus(String... status);
	
	List<DiamondInfoData> submitDiamondList();
	
	List<DiamondInfoData> getDiamondInfoData();
	
	List<DiamondInfoData> getDiamondInfoHistory(String giano,String basketno);
	
}
