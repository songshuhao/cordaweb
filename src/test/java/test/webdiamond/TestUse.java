package test.webdiamond;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo;
import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.corda.impl.DiamondTradeApi;
import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.Diamondsinfo;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.DateFormatUtils;
import ats.blockchain.web.utils.StringUtil;
import kotlin.reflect.jvm.internal.impl.serialization.jvm.JvmPackageTable.PackageTable;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.utilities.NetworkHostAndPort;

public class TestUse {
	DiamondTradeApi api;
	private String aocUser = "AOC";
	private String aocPwd = "AOC";
	private int aocPort = 10008;

	private String supUser = "SupplierA";
	private String supPwd = "SupplierA";
	private int supPort = 10011;
	private String labUser = "gia";
	private String labPwd = "gia";
	private int labPort = 10015;
	private String valutUser = "VaultA";
	private String valutPwd = "VaultA";
	private int valutPort = 10013;
	private String auditUser = "audit";
	private String auditPwd = "audit";
	private int auditPort = 10017;
	
	private String aocLegalName = "OU=AOC,O=AOC,L=HKSAR,C=CN";
	private String supLegalName = "OU=Supplier,O=SupplierA,L=HKSAR,C=CN";
	private String labLegalName = "OU=Lab,O=GIA,L=HKSAR,C=CN";
	private String valutLegalName = "OU=Vault,O=VaultA,L=HKSAR,C=CN";
	private String auditLegalName = "OU=Auditor,O=AuditorA,L=HKSAR,C=CN";
	String basketno = "bsk1003";

	@Before
	public void setUp() {

	}

	// @Test
	public void testCsvRead() {
		List<Basketinfo> list;
		try {
			list = AOCBeanUtils.getObjectFromCsv("./data/b.csv", Basketinfo.class);
			System.out.println(JSON.toJSONString(list));
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void testBeanCopy() {
		Diamondsinfo basketinfo = new Diamondsinfo();
		basketinfo.setBasketno("badl");
		basketinfo.setClarity("cla");
		DiamondsInfo d1 = new DiamondsInfo();
		BeanUtils.copyProperties(basketinfo, d1);

		System.out.println(JSON.toJSON(d1));
	}

	@Test
	public void addPackage() {
		init(aocUser, aocPwd, aocPort);

		String pkgStr = "{\"aoc\":\"OU=AOC,O=AOC,L=HKSAR,C=CN\",\"basketno\":\"" + basketno
				+ "\",\"diamondsnumber\":1,\"mimweight\":1,\"productcode\":\"100D1Duo\",\"suppliercode\":\"OU=Supplier,O=SupplierA,L=HKSAR,C=CN\",\"totalweight\":1}";
		PackageInfo bk = JSON.parseObject(pkgStr, PackageInfo.class);
		bk.setSeqNo(StringUtil.getPackageSeqno());
		PackageState pkg = new PackageState();
		BeanUtils.copyProperties(bk, pkg);

		String pkgIssue = PackageState.PKG_ISSUE;
		String is;
		try {
			is = api.createPackage(supLegalName, pkg, pkgIssue);
			System.out.println("issuePackage " + is);
		} catch (DiamondWebException e) {
		}
		
		addDiamond();
		testReqLabVerify();
		testRespLabVerify();
		//testSubmitReqVaultVerify();
		//testRespVaultVerify();

	}


	@Test
	public void addDiamond() {
		init(supUser, supPwd, supPort);

		List<DiamondsInfo> list = Lists.newArrayList();
		DiamondsInfo di = new DiamondsInfo();
		di.setTradeid(StringUtil.getDiamondSeqno());
		di.setBasketno(basketno);
		di.setClarity("A");
		di.setColor("F");
		di.setSize(BigDecimal.ONE);
		di.setGiano("g11001");
		di.setProductcode("100D1Duo");
		di.setCraftsmandate("2018-10-01");
		di.setSuppliercode(supLegalName);
		di.setCraftsmanname("craf");
		di.setCut("cut");
		list.add(di);
		try {
			String rs = api.issueDiamond(aocLegalName, basketno, list);
			System.out.println("addDiamond " + rs);
		} catch (DiamondWebException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReqLabVerify() {
		init(aocUser, aocPwd, aocPort);
		try {
			String rs = api.reqLabVerifyDiamond(basketno, PackageState.AOC_SUBMIT_LAB_VERIFY, labLegalName);
			System.out.println("Submit ReqLabVerify "+rs);
			testRespLabVerify();
		} catch (DiamondWebException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRespLabVerify() {
		init(labUser, labPwd, labPort);
		
		PackageInfo inf = new PackageInfo();
		inf.setBasketno(basketno);
		inf.setAoc(aocLegalName);
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		inf.setGiaapproveddate(date);
		inf.setStatus(PackageState.LAB_VERIFY_PASS);
		inf.setResult("pass");
		inf.setReverification("verfied");
		inf.setGiacontrolno("");
		try {
		String rs = 	api.labVerifyResp(inf );
		System.out.println(" RespLabVerify " +rs);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testRespLabVerifyNoPass() {
		init(labUser, labPwd, labPort);
		
		PackageInfo inf = new PackageInfo();
		inf.setBasketno(basketno);
		inf.setAoc(aocLegalName);
		inf.setGiacontrolno("giaccc");
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		inf.setGiaapproveddate(date);
		inf.setStatus(PackageState.LAB_VERIFY_NOPASS);
		inf.setResult("pass");
		inf.setReverification("verfied");
		try {
		String rs = 	api.labVerifyResp(inf );
		System.out.println(" RespLabVerify " +rs);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSubmitReqVaultVerify() {
		init(aocUser, aocPwd, aocPort);
		try {
			// String externalid,String status,String vault,String owner
			String rs = api.reqVaultVerifyDiamond(basketno, PackageState.AOC_SUBMIT_VAULT_VERIFY, valutLegalName,"owner111");
			System.out.println("submit ReqvalutVerify "+rs);
		} catch (DiamondWebException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testRespVaultVerify() {
		init(valutUser, valutPwd, valutPort);
		
		PackageInfo inf = new PackageInfo();
		inf.setBasketno(basketno);
		inf.setAoc(aocLegalName);
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		inf.setInvtymgr("invty");
		inf.setSealedbagno("sealed001");
		inf.setStatus(PackageState.VAULT_VERIFY_PASS);
		inf.setResult("pass");
		inf.setReverification("verfied");
		try {
			String rs = api.vaultVerifyResp(inf );
			System.out.println(rs);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRespChangeOwner() {
		init(valutUser, valutPwd, valutPort);
		
		try {
			String rs = api.changeOwnerResp(basketno, aocLegalName);
			System.out.println("testRespChangeOwner "+rs);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testRedeemDiamond() {
		init(aocUser, aocPwd, aocPort);
		
		try {
			String rs = api.redeemDiamond(basketno, valutLegalName);
			System.out.println("testRedeemDiamond "+rs);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testRespRedeemDiamond() {
		init(valutUser, valutPwd, valutPort);
		try {
			String rs = api.redeemDiamondResp(basketno, aocLegalName);
			System.out.println("testRespRedeemDiamond "+rs);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAuditDiamond() {
		init(aocUser, aocPwd, aocPort);
		
		try {
			String rs = api.auditDiamond(basketno, valutLegalName);
			System.out.println("testAuditDiamond "+rs);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testRespAuditDiamond() {
		init(valutUser, valutPwd, valutPort);
		try {
			String rs = api.auditDiamondResp(basketno, aocUser, "2018-09-26", PackageState.AUDIT_VERIFY_NOPASS, "no pass");
			System.out.println("testRespAuditDiamond "+rs);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void queryPackage() {
//		init(supUser, supPwd, supPort);
//		init(valutUser, valutPwd, valutPort);
		init(aocUser, aocPwd, aocPort);
//		List<StateAndRef<PackageState>> pkg = api.getPackageStateById(Vault.StateStatus.UNCONSUMED,basketno);
//		List<StateAndRef<PackageState>> pkg = api.getPackageStateByStatus(Vault.StateStatus.ALL,PackageState.AOC_SUBMIT_VAULT_VERIFY);
		List<StateAndRef<PackageState>> pkg = api.getAllPackageState();
		
		System.out.println("query result: "+pkg.size());
		pkg.stream().forEach(p ->{
			
			PackageState data = p.getState().getData();
			
			System.out.println(data.toString());	
			System.out.println(JSON.toJSONString(data.getDiamondinfolist()));	
		});
	}

	void init(String username, String pwd, int port) {
		CordaRPCConnection cordarpcconn = null;
		CordaRPCOps cordarpcops = null;
		CordaRPCClient cordaclient = new CordaRPCClient(new NetworkHostAndPort("127.0.0.1", port),
				CordaRPCClientConfiguration.DEFAULT);
		try {
			System.out.println("Start cordarpcclient with user:" + username);
			cordarpcconn = cordaclient.start(username, pwd);
			cordarpcops = cordarpcconn.getProxy();
			System.out.println("Corda RPC protocol version:" + cordarpcconn.getServerProtocolVersion());
			api = new DiamondTradeApi(cordarpcops);
			System.out.println("DiamondTradeApi create");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
