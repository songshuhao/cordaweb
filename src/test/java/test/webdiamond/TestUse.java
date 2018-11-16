package test.webdiamond;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.DefaultResourceLoader;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo;
import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.bean.ExportConfig;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.bean.StateInfo;
import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.corda.impl.DiamondTradeApi;
import ats.blockchain.web.model.Diamondsinfo;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.CustSort;
import ats.blockchain.web.utils.DateFormatUtils;
import ats.blockchain.web.utils.ExcelUtil;
import ats.blockchain.web.utils.ResultUtil;
import ats.blockchain.web.utils.StringUtil;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
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
	String basketno = "bsk1006";

	@Before
	public void setUp() {

	}

	 @Test
	public void testCsvRead() {
		List<ExportConfig> list;
		try {
			list = AOCBeanUtils.getObjectFromCsv("./src/main/resources/templates/export.cfg", ExportConfig.class);
			System.out.println(list.get(0));
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testResultSuccess() {
		Map<String, Object> m = null;

		assertFalse(ResultUtil.isSuccess(m));
		m = new HashMap<String, Object>();
		assertFalse(ResultUtil.isSuccess(m));
		m.put("state", "success");
		assertTrue(ResultUtil.isSuccess(m));
		m.put("state", "failed");
		assertFalse(ResultUtil.isSuccess(m));

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
		try {
			 api.createPackage(supLegalName, pkg, pkgIssue);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}

//		 addDiamond();
		// testReqLabVerify();
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
		di.setStatus(PackageState.DMD_CREATE);
		di.setGiano("g11001");
		di.setProductcode("100D1Duo");
		di.setCraftsmandate("2018-10-01");
		di.setSuppliercode(supLegalName);
		di.setCraftsmanname("craf");
		di.setCut("cut");
		list.add(di);
		try {
			api.issueDiamond(aocLegalName, basketno, list);
		} catch (DiamondWebException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testReqLabVerify() {
		init(aocUser, aocPwd, aocPort);
		try {
			 api.reqLabVerifyDiamond(basketno, PackageState.AOC_SUBMIT_LAB_VERIFY, labLegalName);
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
			api.labVerifyResp(inf);
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
			api.labVerifyResp(inf);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSubmitReqVaultVerify() {
		init(aocUser, aocPwd, aocPort);
		try {
			// String externalid,String status,String vault,String owner
		  api.reqVaultVerifyDiamond(basketno, PackageState.AOC_SUBMIT_VAULT_VERIFY, valutLegalName,
					"owner111");
//			testRespVaultVerify();
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
			 api.vaultVerifyResp(inf);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReqChangeOwner() {
		init(aocUser, aocPwd, aocPort);

		try {
			 api.submitChangeOwnerDiamond(basketno, valutLegalName, "new owner", PackageState.DMD_SUBMIT_CHG_OWNER);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRespChangeOwner() {
		init(valutUser, valutPwd, valutPort);

		try {
			 api.changeOwnerResp(basketno, aocLegalName,PackageState.DMD_CHANGE_OWNER_PASS);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRedeemDiamond() {
		init(aocUser, aocPwd, aocPort);

		try {
			 api.redeemDiamond(basketno, valutLegalName);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRespRedeemDiamond() {
		init(valutUser, valutPwd, valutPort);
		try {
			 api.redeemDiamondResp(basketno, aocLegalName);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAuditDiamond() {
		init(aocUser, aocPwd, aocPort);

		try {
			 api.auditDiamond( auditLegalName,basketno,PackageState.AOC_REQ_AUDIT_VERIFY);
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRespAuditDiamond() {
		init(auditUser, auditPwd, auditPort);
		try {
			 api.auditDiamondResp(basketno, aocUser, "2018-09-26", PackageState.AUDIT_VERIFY_PASS,
					"pass");
		} catch (DiamondWebException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void queryPackage() {
		// init(supUser, supPwd, supPort);
		// init(valutUser, valutPwd, valutPort);
		init(aocUser, aocPwd, aocPort);
		// List<StateAndRef<PackageState>> pkg =
		// api.getPackageStateById(Vault.StateStatus.UNCONSUMED,basketno);
		// List<StateAndRef<PackageState>> pkg =
		// api.getPackageStateByStatus(Vault.StateStatus.ALL,PackageState.AOC_SUBMIT_VAULT_VERIFY);
		List<StateAndRef<PackageState>> pkg = api.getAllPackageState();

		System.out.println("query result: " + pkg.size());
		pkg.stream().forEach(p -> {

			PackageState data = p.getState().getData();

			System.out.println(data.toString());
			System.out.println(JSON.toJSONString(data.getDiamondinfolist()));
		});
	}

	@Test
	public void queryPackagePage() {
		init(aocUser, aocPwd, aocPort);
		// List<StateAndRef<PackageState>> pkg =
		// api.getPackageStatePageById(2,2,Vault.StateStatus.ALL,basketno,"bsk1002");
		// List<StateAndRef<PackageState>> pkg =
		// api.getPackageStateByStatus(Vault.StateStatus.ALL,PackageState.AOC_SUBMIT_VAULT_VERIFY);
		List<StateAndRef<PackageState>> pkg = api.getAllPackageState(2, 2);

		System.out.println("query result: " + pkg.size());
		pkg.stream().forEach(p -> {

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
			DiamondApplicationRunner r = new  DiamondApplicationRunner();
			r.setCordaApi(new CordaApi() {
				@Override
				public DiamondTradeApi getTradediamondinf() {
					return api;
				}
				
			});
			r.setResourceLoader(new DefaultResourceLoader());
			r.setPreStateCheck("classpath:/templates/status.properties");
			r.initNodeInfoList();
			r.initPreState();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void testSort() {
		List<PackageInfo> list = Lists.newArrayList();
		System.out.println("add ===");
		for (int j = 0; j < 10; j++) {
			PackageInfo p = new PackageInfo();

			p.setBasketno(StringUtil.getPackageSeqno());
			list.add(p);
			System.out.println(p.getBasketno());
		}
		System.out.println("sort ===");
		CustSort<PackageInfo> compare = new CustSort<PackageInfo>();
		compare.setAsc(true);
		compare.setSortField("basketno");
		Collections.sort(list, compare);

		list.forEach(p -> {
			System.out.println(p.getBasketno());
		});
	}
	
	
	@Test
	public void testStringArrayType() {
		ExportConfig e =new ExportConfig();
		try {
			 Field f = e.getClass().getDeclaredField("header");
			 Class<?> type = f.getType();
		} catch (NoSuchFieldException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	@Test
	public void testReplace() {
		String p = "D:\\aaa\\\\bbb";
		System.out.println(p.replaceAll("[\\\\]", "/"));
		
		StringReader sr = new StringReader(p);
		
	}
	
	@Test
	public void testPath() {
		 Path p = Paths.get("./data/aa");
		 File f = p.toFile();
		 System.out.println(f.getName());
		 System.out.println(f.isDirectory());
		 System.out.println(f.length());
		 
	}
	
	@Test
	public void testReadExcelDiamond() {
		try {
			InputStream is = new FileInputStream("data/diamond_20181107_162900227.xls");
			List<DiamondInfoData> list = ExcelUtil.readExcelContent(".xls", is , DiamondInfoData.class);
			list.forEach(d -> System.out.println(d));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testReadExcelPackage() {
		try {
			InputStream is = new FileInputStream("data/diamond_20181107_162900227.xls");
			List<PackageInfo> list = ExcelUtil.readExcelContent(".xls", is , PackageInfo.class);
			list.forEach(d -> System.out.println(d));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReadStateInfo() {
		
		InputStream ins = null;
		try {
			ins = new FileInputStream("src/main/resources/templates/status.properties");
			List<StateInfo> list = AOCBeanUtils.getObjectFromCsvByMethod(ins, StateInfo.class);
			list.forEach(d -> System.out.println(d));
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
