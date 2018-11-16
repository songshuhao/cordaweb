package ats.blockchain.web.servcie;

import java.util.List;
import java.util.Map;

import ats.blockchain.web.bean.DiamondInfoData;

public interface DiamondsInfoService {

	Map<String, Object> addDiamondInfo(DiamondInfoData di);

	Map<String, Object> editDiamondInfo(DiamondInfoData di);

	Map<String, Object> deleteDiamondInfo(DiamondInfoData di);
	Map<String, Object> deleteDiamondInfo(String userid,String basketno,String status);

	List<DiamondInfoData> getDiamondInfoByStatus(String userid, String... status);

	List<DiamondInfoData> submitDiamondList(String userid);

	List<DiamondInfoData> getDiamondInfoData(int pageNum,int pageSize);

	List<DiamondInfoData> getDiamondInfoHistory(String giano, String basketno);
	
	Map<String,Object> checkGiano(String userid,String tradeid,String giano);
	
	/**
	 * 获取最新状态下重复钻石的集合。
	 * add by shuhao.song
	 */
	Map<String,List<DiamondInfoData>> getDuplicateDiamondsList(String basketno);
}
