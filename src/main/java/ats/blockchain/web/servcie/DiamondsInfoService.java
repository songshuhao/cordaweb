package ats.blockchain.web.servcie;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import ats.blockchain.web.bean.DiamondInfoData;

public interface DiamondsInfoService {

	Map<String, Object> addDiamondInfo(DiamondInfoData di);

	Map<String, Object> editDiamondInfo(DiamondInfoData di);

	Map<String, Object> deleteDiamondInfo(DiamondInfoData di);

	List<DiamondInfoData> getDiamondInfoByStatus(String userid, String... status);

	List<DiamondInfoData> submitDiamondList(String userid);

	List<DiamondInfoData> getDiamondInfoData(int pageNum,int pageSize);

	List<DiamondInfoData> getDiamondInfoHistory(String giano, String basketno);
	
	Map<String,Object> checkGiano(String userid,String tradeid,String giano);
	
}
