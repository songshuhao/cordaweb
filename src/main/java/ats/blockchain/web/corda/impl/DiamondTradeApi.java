package ats.blockchain.web.corda.impl;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.greenbirdtech.blockchain.cordapp.diamond.data.DiamondState;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo;
import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.cordapp.diamond.flow.DiamondCollectFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondCreateFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondIssueFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondLabRespFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondReqLabVerifyFlow;
import ats.blockchain.cordapp.diamond.flow.PackageCreateFlow;
import ats.blockchain.cordapp.diamond.flow.PackageIssueFlow;
import ats.blockchain.cordapp.diamond.flow.PackageRemoveFlow;
import ats.blockchain.cordapp.diamond.schema.PackageSchemaV1;
import ats.blockchain.cordapp.diamond.util.ClassMethodFactory;
import ats.blockchain.cordapp.diamond.util.Constants;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.PackageInfo;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.Vault.Page;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;

public class DiamondTradeApi {
	public final static String CURRUSER = "CURRUSER";
	public final static String OTHERUSER = "OTHERUSER";

	private final static Logger logger = LogManager.getLogger(DiamondTradeApi.class);

	private final List<String> servicelist = ImmutableList.of("Notary", "Network Map Service");

	private final CordaRPCOps rpcops;
	private Party party;

	public DiamondTradeApi(CordaRPCOps rpcops) {
		this.rpcops = rpcops;
		this.party = rpcops.nodeInfo().getLegalIdentities().get(0);
	}

	public List<NodeInfo> getNodeInfos() {
		List<NodeInfo> nodeInfos = rpcops.networkMapSnapshot();
		logger.debug("getNodeInfos :{}", nodeInfos.toString());
		return nodeInfos;
	}
	
	public String getCurrUser() {
		return (party.getName().toString());
	}

	public Map<String, List<String>> getOtherUser() throws DiamondWebException {
		Map<String, List<String>> mapOtheruser = ImmutableMap.of(OTHERUSER, rpcops.networkMapSnapshot().stream()
				.filter(nodeInfo -> (!nodeInfo.getLegalIdentities().get(0).equals(party)
						&& !servicelist.contains(nodeInfo.getLegalIdentities().get(0).getName().getOrganisation())))
				.map(it -> it.getLegalIdentities().get(0).getName().toString()).collect(toList()));
		if ((mapOtheruser == null) || (mapOtheruser.size() == 0))
			throw new DiamondWebException("No other user is found");
		logger.info("Total number of other user:" + mapOtheruser.get(OTHERUSER).size());
		return (mapOtheruser);
	}

	public List<StateAndRef<DiamondState>> getAllDiamond() {
		return (rpcops.vaultQuery(DiamondState.class).getStates());
	}

	public List<StateAndRef<PackageState>> getAllPackageState(UniqueIdentifier linearid) {
		QueryCriteria criteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearid),
				Vault.StateStatus.ALL, null);
		return (rpcops.vaultQueryByCriteria(criteria, PackageState.class).getStates());
	}

	public List<StateAndRef<PackageState>> getPackageStateByStatus(@Nonnull String... status) {
		List<String> asList = Arrays.asList(status);
		logger.debug("getPackageStateByStatus :{}", asList);
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		Field statusField = getField(PackageSchemaV1.PersistentPackageState.class, "status");
		CriteriaExpression exp = Builder.in(statusField, asList);
		QueryCriteria crit = new QueryCriteria.VaultCustomQueryCriteria(exp);
		QueryCriteria cc =generalCriteria.and(crit);
		Page<PackageState> result = rpcops.vaultQueryByCriteria(cc, PackageState.class);
		
		List<StateAndRef<PackageState>> list = result.getStates();
		logger.debug("getPackageStateByStatus {} query result size:{} ",asList, list.size());
		return list;
	}

	public List<StateAndRef<PackageState>> getPackageStateById(@Nonnull String... basketNo) {
		List<String> asList = Arrays.asList(basketNo);
		logger.debug("getPackageStateById :{}", asList);
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		Field statusField = getField(PackageSchemaV1.PersistentPackageState.class, "basketno");
		CriteriaExpression exp = Builder.equal(statusField, asList);
		QueryCriteria crit = new QueryCriteria.VaultCustomQueryCriteria(exp);
		QueryCriteria cc = generalCriteria.and(crit);
		Page<PackageState> result = rpcops.vaultQueryByCriteria(cc, PackageState.class);
		List<StateAndRef<PackageState>> list = result.getStates();
		logger.debug("getPackageStateById {} query result size:{} ", asList,list.size());
		return list;
	}

	private static Field getField(Class<?> clazz, String filedName) {
		return ClassMethodFactory.Instance.getClassMethods(clazz).getField(filedName);
	}

	public List<StateAndRef<DiamondState>> getIssuedDiamond() {
		List<StateAndRef<DiamondState>> diamondlist = rpcops.vaultQuery(DiamondState.class).getStates().stream()
				.filter(it -> it.getState().getData().getSupplier().equals(party)).collect(toList());
		if (diamondlist != null)
			logger.info(String.format("Diamond issued by %1$s:%2$d", party.getName().getOrganisation(),
					diamondlist.size()));
		return (diamondlist);
	}

	public PackageState getCounterPartyDiamond(String counterparty, String externalid) throws DiamondWebException {
		PackageState diamondstate = null;
		CordaX500Name x500Name = CordaX500Name.parse(counterparty);
		final Party counterIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (counterIdentity == null)
			throw new DiamondWebException("Counterparty not found");
		try {
			FlowHandle<PackageState> flowhandle = rpcops.startFlowDynamic(DiamondCollectFlow.Initiator.class,
					counterIdentity, externalid);
			diamondstate = flowhandle.getReturnValue().get();
		} catch (ExecutionException ee) {
			logger.warn("Failure to get diamond:{}", ee);
			throw new DiamondWebException(ee.getMessage());
		} catch (Exception ex) {
			logger.warn("Failure to get diamond:{}", ex);
			throw new DiamondWebException("Diamond enquiry failure");
		}
		return (diamondstate);
	}

	/**
	 * 
	 * @param supplier
	 * @param pkgInfo
	 * @param state
	 *            enum in PackageState
	 * @return
	 * @throws DiamondWebException
	 */
	public String createPackage(String supplier, PackageState pkgInfo, String state) throws DiamondWebException {
		if (pkgInfo == null) {
			return null;
		}
		String externalid = pkgInfo.getBasketno();
		String stateStr = Constants.PKG_STATE_MAP.get(state);
		logger.debug("{} :aoc {}, externalId:{} ,pkgInfo: {} ", stateStr, supplier, externalid, pkgInfo);
		CordaX500Name x500Name = CordaX500Name.parse(supplier);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null) {
			logger.error("{} supplier not found {}", stateStr, supplier);
			throw new DiamondWebException(stateStr + " supplier not found");
		}
		List<Party> supplierlist = rpcops.nodeInfo().getLegalIdentities();
		if ((supplierlist == null) || (supplierlist.size() == 0)) {
			logger.error("{} Unknown supplier ", stateStr);
			throw new DiamondWebException("Unknown supplier");
		}
		logger.debug("getLegalIdentities {} ", supplierlist.toString());
		try {
			Class initClass = null;
			if (PackageState.PKG_CREATE.equals(state)) {
				initClass = PackageCreateFlow.Initiator.class;
			} else if (PackageState.PKG_ISSUE.equals(state)) {
				initClass = PackageIssueFlow.Initiator.class;
			} else if (PackageState.PKG_REMOVE.equals(state)) {
				initClass = PackageRemoveFlow.Initiator.class;
			} else {
				logger.error("Unsupport operation in createPackage: {}", stateStr);
				throw new DiamondWebException("Unsupport operation :" + stateStr);
			}

			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(initClass, aocIdentity, pkgInfo);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append(stateStr);
			strbuf.append(" Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" supplied by ");
			strbuf.append(supplierlist.get(0).getName());
			return (strbuf.toString());
		} catch (Exception ex) {
			logger.error("Failure to " + stateStr, ex);
			throw new DiamondWebException(ex);
		}
	}

	public String createDiamond(String aoc, String externalid, List<DiamondsInfo> diamondinfolist)
			throws DiamondWebException {
		logger.debug("createDiamond aoc: {} ,basketno : {}",aoc,externalid);
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		List<Party> supplierlist = rpcops.nodeInfo().getLegalIdentities();
		if ((supplierlist == null) || (supplierlist.size() == 0))
			throw new DiamondWebException("Unknown supplier");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(DiamondCreateFlow.Initiator.class,
					aocIdentity, externalid,diamondinfolist);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" supplied by ");
			strbuf.append(supplierlist.get(0).getName()).append(" externalid ").append(externalid);
			return (strbuf.toString());
		} catch (ExecutionException ee) {
			logger.warn("Failure to create diamond:", ee);
			throw new DiamondWebException("Diamond create error");
		} catch (Exception ex) {
			logger.warn("Failure to create diamond:", ex);
			throw new DiamondWebException("Diamond create failure");
		}
	}
	public String issueDiamond(String aoc, String externalid)
			throws DiamondWebException {
		logger.debug("issueDiamond aoc: {} ,basketno : {}",aoc,externalid);
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		List<Party> supplierlist = rpcops.nodeInfo().getLegalIdentities();
		if ((supplierlist == null) || (supplierlist.size() == 0))
			throw new DiamondWebException("Unknown supplier");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(DiamondIssueFlow.Initiator.class,
					aocIdentity, externalid);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" supplied by ");
			strbuf.append(supplierlist.get(0).getName()).append(" externalid ").append(externalid);
			return (strbuf.toString());
		} catch (ExecutionException ee) {
			logger.warn("Failure to issue diamond:", ee);
			throw new DiamondWebException("Diamond issue error");
		} catch (Exception ex) {
			logger.warn("Failure to issue diamond:", ex);
			throw new DiamondWebException("Diamond issue failure");
		}
	}
	
	public String reqLabVerifyDiamond( String externalid,String status,String lab)
			throws DiamondWebException {
		logger.debug("reqLabVerifyDiamond lab: {} ,basketno : {}",lab,externalid);
		CordaX500Name x500Name = CordaX500Name.parse(lab);
		final Party labIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (labIdentity == null)
			throw new DiamondWebException("lab not found");
		List<Party> supplierlist = rpcops.nodeInfo().getLegalIdentities();
		if ((supplierlist == null) || (supplierlist.size() == 0))
			throw new DiamondWebException("Unknown aoc");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(DiamondReqLabVerifyFlow.Initiator.class,
					 externalid,status,labIdentity);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" supplied by ");
			strbuf.append(supplierlist.get(0).getName()).append(" externalid ").append(externalid);
			return (strbuf.toString());
		} catch (ExecutionException ee) {
			logger.warn("Failure to reqLabVerify diamond:", ee);
			throw new DiamondWebException("Diamond reqLabVerify error");
		} catch (Exception ex) {
			logger.warn("Failure to reqLabVerify diamond:", ex);
			throw new DiamondWebException("Diamond reqLabVerify failure");
		}
	}
//	public String transferDiamond(String owner, UniqueIdentifier linearid, Amount<Currency> amount, String ownmgr)
//			throws DiamondWebException {
//		CordaX500Name x500Name = CordaX500Name.parse(ownmgr);
//		final Party ownmgrIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
//		if (ownmgrIdentity == null)
//			throw new DiamondWebException("Ownmgr not found");
//		logger.info(String.format("Diamond %1$s is transferred to %2$s with price %3$s to be verified by %4$s",
//				linearid.getExternalId(), owner, StringUtil.formTxnprice(amount), ownmgr));
//		try {
//			final FlowHandle<SignedTransaction> flowhandle = rpcops
//					.startFlowDynamic(DiamondTransferFlow.Initiator.class, linearid, ownmgrIdentity, owner, amount);
//			final SignedTransaction stxn = flowhandle.getReturnValue().get();
//			StringBuilder strbuf = new StringBuilder();
//			strbuf.append("Transaction completed with id ");
//			strbuf.append(stxn.getId());
//			strbuf.append(" for diamond ");
//			strbuf.append(linearid.getExternalId());
//			strbuf.append(" with price ");
//			strbuf.append(StringUtil.formTxnprice(amount));
//			strbuf.append(" to owner ");
//			strbuf.append(owner);
//			strbuf.append(" to be confirmed by ");
//			strbuf.append(ownmgr);
//			return (strbuf.toString());
//		} catch (Exception ex) {
//			logger.warn("Failure to transfer diamond:{}", ex);
//			throw new DiamondWebException("Diamond transfer failure");
//		}
//	}

//	public String transferrespDiamond(UniqueIdentifier linearid, String aoc) throws DiamondWebException {
//		CordaX500Name x500Name = CordaX500Name.parse(aoc);
//		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
//		if (aocIdentity == null)
//			throw new DiamondWebException("AOC not found");
//		logger.info(String.format("Diamond %1$s having transferred is to be delivered to %2$s",
//				linearid.getExternalId(), aoc));
//		try {
//			SimpleDateFormat dataDF = new SimpleDateFormat("yyyy-MM-dd");
//			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(
//					DiamondTransferRespFlow.Initiator.class, linearid, aocIdentity,
//					dataDF.format(new Date(System.currentTimeMillis())));
//			final SignedTransaction stxn = flowhandle.getReturnValue().get();
//			StringBuilder strbuf = new StringBuilder();
//			strbuf.append("Transaction completed with id ");
//			strbuf.append(stxn.getId());
//			strbuf.append(" for diamond ");
//			strbuf.append(linearid.getExternalId());
//			strbuf.append(" having moved and sent to ");
//			strbuf.append(aoc);
//			return (strbuf.toString());
//		} catch (Exception ex) {
//			logger.warn("Failure to confirm diamond transfer:{}", ex);
//			throw new DiamondWebException("Diamond transfer response failure");
//		}
//	}
//
//	public String moveDiamond(UniqueIdentifier linearid, String vault, String location) throws DiamondWebException {
//		CordaX500Name x500Name = CordaX500Name.parse(vault);
//		final Party vaultIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
//		if (vaultIdentity == null)
//			throw new DiamondWebException("Vault not found");
//		logger.info(String.format("Diamond $1$s is moved to location %2$s to be verified by %3$s",
//				linearid.getExternalId(), location, vault));
//		try {
//			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(DiamondMoveFlow.Initiator.class,
//					linearid, vaultIdentity, location);
//			final SignedTransaction stxn = flowhandle.getReturnValue().get();
//			StringBuilder strbuf = new StringBuilder();
//			strbuf.append("Transaction completed with id ");
//			strbuf.append(stxn.getId());
//			strbuf.append(" for diamond ");
//			strbuf.append(linearid.getExternalId());
//			strbuf.append(" to move to ");
//			strbuf.append(location);
//			strbuf.append(" verified by ");
//			strbuf.append(vault);
//			return (strbuf.toString());
//		} catch (Exception ex) {
//			logger.warn("Failure to move diamond to another location:{}", ex);
//			throw new DiamondWebException("Diamond move failure");
//		}
//	}
//
//	public String moverespDiamond(UniqueIdentifier linearid, String aoc) throws DiamondWebException {
//		CordaX500Name x500Name = CordaX500Name.parse(aoc);
//		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
//		if (aocIdentity == null)
//			throw new DiamondWebException("AOC not found");
//		logger.info(
//				String.format("Diamond %1$s having moved is to be verified by %2$s", linearid.getExternalId(), aoc));
//		try {
//			SimpleDateFormat dataDF = new SimpleDateFormat("yyyy-MM-dd");
//			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(
//					DiamondMoveRespFlow.Initiator.class, linearid, aocIdentity,
//					dataDF.format(new Date(System.currentTimeMillis())));
//			final SignedTransaction stxn = flowhandle.getReturnValue().get();
//			StringBuilder strbuf = new StringBuilder();
//			strbuf.append("Transaction completed with id ");
//			strbuf.append(stxn.getId());
//			strbuf.append(" for diamond ");
//			strbuf.append(linearid.getExternalId());
//			strbuf.append(" having moved and sent to ");
//			strbuf.append(aoc);
//			return (strbuf.toString());
//		} catch (Exception ex) {
//			logger.warn("Failure to confirm diamond move:{}", ex);
//			throw new DiamondWebException("Diamond move response failure");
//		}
//	}
//
	public String verifyDiamond(String basketno, String verifier,String status) throws DiamondWebException {
		CordaX500Name x500Name = CordaX500Name.parse(verifier);
		final Party verifyIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (verifyIdentity == null)
			throw new DiamondWebException("verifier not found");
		List<Party> aoclist = rpcops.nodeInfo().getLegalIdentities();
		if ((aoclist == null) || (aoclist.size() == 0))
			throw new DiamondWebException("Unknown AOC");
		logger.info(String.format("Diamond %1$s is verify by %2$s", basketno, verifier));
		try {
			Class clazz = null;
			if(status.equals(PackageState.AOC_REQ_LAB_VERIFY)||status.equals(PackageState.AOC_SUBMIT_LAB_VERIFY)) {
				clazz = DiamondReqLabVerifyFlow.class;
			}
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(clazz,
					basketno,status, verifyIdentity);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" for diamond ");
			strbuf.append(basketno);
			strbuf.append(" to be ").append(Constants.PKG_STATE_MAP.get(status)).append(" by ");
			strbuf.append(verifier);
			return (strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to audit diamond:", ex);
			throw new DiamondWebException("Diamond audit failure",ex);
		}
	}

	public String labVerifyResp(PackageInfo pkgInfo) throws DiamondWebException {
		String aoc = pkgInfo.getAoc();
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		List<Party> auditorlist = rpcops.nodeInfo().getLegalIdentities();
		if ((auditorlist == null) || (auditorlist.size() == 0))
			throw new DiamondWebException("Unkown auditor");
		logger.info(String.format("Diamond %1$s is audited completely by %2$s", pkgInfo.getBasketno(),
				auditorlist.get(0).getName().toString()));
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(
					DiamondLabRespFlow.Initiator.class,pkgInfo.getBasketno(),aoc,pkgInfo.getGiaapproveddate(),pkgInfo.getStatus(),pkgInfo.getResult(),pkgInfo.getReverification());
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" for diamond ");
			strbuf.append(pkgInfo.getBasketno());
			strbuf.append(" having audited and sent to ");
			strbuf.append(aoc);
			return (strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to confirm diamond audit:{}", ex);
			throw new DiamondWebException("Diamond audit response failure");
		}
	}
//
//	public String redeemDiamond(UniqueIdentifier linearid) throws DiamondWebException {
//		logger.info(String.format("Diamond %1$s is redeemed", linearid));
//		try {
//			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(DiamondRedeemFlow.Initiator.class,
//					linearid);
//			final SignedTransaction stxn = flowhandle.getReturnValue().get();
//			StringBuilder strbuf = new StringBuilder();
//			strbuf.append("Transaction completed with id ");
//			strbuf.append(stxn.getId());
//			strbuf.append(" for diamond ");
//			strbuf.append(linearid.getExternalId());
//			strbuf.append(" is redeemed");
//			return (strbuf.toString());
//		} catch (Exception ex) {
//			logger.warn("Failure to redeem diamond:{}", ex);
//			throw new DiamondWebException("Diamond redeem failure");
//		}
//	}

	public static void main(String[] args) {
		CordaRPCConnection cordarpcconn = null;
		CordaRPCOps cordarpcops = null;
		String username = "AOC";
		String password = "AOC";
		CordaRPCClient cordaclient = new CordaRPCClient(new NetworkHostAndPort("127.0.0.1", 10008),
				CordaRPCClientConfiguration.DEFAULT);
		try {
			System.out.println("Start cordarpcclient with user:" + username);
			cordarpcconn = cordaclient.start(username, password);
			cordarpcops = cordarpcconn.getProxy();
			System.out.println("Corda RPC protocol version:" + cordarpcconn.getServerProtocolVersion());
			if (cordarpcops == null) {
				System.out.println("CordaRPCOps is null");
			} else {
				NodeInfo nInfo = cordarpcops.nodeInfo();
				if (nInfo != null) {
					System.out.println("********************");
					List<Party> pList = nInfo.getLegalIdentities();
					for (Party p : pList) {
						System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
						System.out.println("getLegalIdentities:" + p.toString());
					}
				}
			}
			DiamondTradeApi api = new DiamondTradeApi(cordarpcops);
			String pkgStr = "{\"aoc\":\"O=AOC,L=HKSAR,C=CN\",\"basketno\":\"bsk1001\",\"diamondsnumber\":1,\"mimweight\":1,\"productcode\":\"100D1Duo\",\"suppliercode\":\"O=SupplierA,L=HKSAR,C=CN\",\"totalweight\":1}";
			PackageInfo bk = JSON.parseObject(pkgStr, PackageInfo.class);
			PackageState pkg = new PackageState();
			// List<StateAndRef<PackageState>> list =
			// api.getPackageStateById("bsk1001");
			// pkg = list.get(0).getState().getData();
			// logger.debug(" packageState: {}",JSON.toJSON(pkg));
			BeanUtils.copyProperties(bk, pkg);
			//
//			 String pkgIssue = PackageState.PKG_CREATE;
			String pkgIssue = PackageState.PKG_ISSUE;
			String is = api.createPackage("O=SupplierA,L=HKSAR,C=CN", pkg, pkgIssue);
			System.out.println("##############" + is);
			// List<StateAndRef<PackageState>> rs =
			// api.getPackageStateByStatus(pkgIssue);
			// for (StateAndRef<PackageState> state : rs) {
			// PackageState data = state.getState().getData();
			// PackageInfo i = new PackageInfo();
			// BeanUtils.copyProperties(data, i);
			// logger.debug(" packageState: {}", JSON.toJSON(i));
			// }

			String basketNo = "bsk1001";
//			List<StateAndRef<PackageState>> rs2 = api.getPackageStateByStatus(pkgIssue);
//			 List<StateAndRef<PackageState>> rs2 =
//			 api.getPackageStateById(basketNo);
//			for (StateAndRef<PackageState> state : rs2) {
//				PackageState data = state.getState().getData();
//				PackageInfo i = new PackageInfo();
//				basketNo = data.getBasketno();
//				List<DiamondsInfo> ll = data.getDiamondinfolist();
//				BeanUtils.copyProperties(data, i);
//				logger.debug("{} packageState: {}", basketNo, JSON.toJSON(i));
//			}
//
//			System.out.println(basketNo + " PackageState size:" + rs2.size());
			// 7694f67697924b08b3485e85cea86e5c

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
