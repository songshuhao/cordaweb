package ats.blockchain.web.corda.impl;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo;
import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.cordapp.diamond.flow.DiamondAuditRespFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondChangeOwnerRespFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondCollectFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondIssueFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondLabRespFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondRedeemFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondRedeemRespFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondReqAuditFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondReqChangeOwnerFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondReqLabVerifyFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondReqVaultVerifyFlow;
import ats.blockchain.cordapp.diamond.flow.DiamondVaultRespFlow;
import ats.blockchain.cordapp.diamond.flow.PackageIssueFlow;
import ats.blockchain.cordapp.diamond.flow.PackageRemoveFlow;
import ats.blockchain.cordapp.diamond.schema.PackageSchemaV1;
import ats.blockchain.cordapp.diamond.util.ClassMethodFactory;
import ats.blockchain.cordapp.diamond.util.Constants;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.utils.AOCBeanUtils;
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
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

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

	/**
	 * 根据uuid 查询所有状态的package信息
	 * 
	 * @param linearid
	 * @return
	 */
	public List<StateAndRef<PackageState>> getAllPackageState(List<UniqueIdentifier> linearid) {
		QueryCriteria criteria = new QueryCriteria.LinearStateQueryCriteria(null, linearid, Vault.StateStatus.ALL,
				null);

		return (rpcops.vaultQueryByCriteria(criteria, PackageState.class).getStates());
	}

	/**
	 * 根据uuid 查询所有状态的package信息
	 * 
	 * @param linearid
	 * @return
	 */
	public List<StateAndRef<PackageState>> getAllPackageStateByPage(int pageNum, int pageSize,
			List<UniqueIdentifier> linearid) {
		QueryCriteria criteria = new QueryCriteria.LinearStateQueryCriteria(null, linearid, Vault.StateStatus.ALL,
				null);

		PageSpecification page = new PageSpecification(pageNum, pageSize);
		return (rpcops.vaultQueryByWithPagingSpec(PackageState.class, criteria, page).getStates());
	}

	/**
	 * 根据状态查询符合条件的状态为未消费的package
	 * 
	 * @param status
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<StateAndRef<PackageState>> getPackageStateByStatus(@Nonnull String... status) {
		List<String> asList = Arrays.asList(status);
		logger.debug("getPackageStateByStatus :{}", asList);
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		Field statusField = getField(PackageSchemaV1.PersistentPackageState.class, "status");
		CriteriaExpression exp = Builder.in(statusField, asList);
		QueryCriteria crit = new QueryCriteria.VaultCustomQueryCriteria(exp);
		QueryCriteria cc = generalCriteria.and(crit);
		Page<PackageState> result = rpcops.vaultQueryByCriteria(cc, PackageState.class);

		List<StateAndRef<PackageState>> list = result.getStates();
		logger.debug("getPackageStateByStatus {} query result size:{} ", asList, list.size());
		return list;
	}

	/**
	 * 根据状态查询符合条件的状态为未消费的package<br>
	 * redeemOwnerId 不为空，则根据Owner查询不包含该redeemOwnerId 的记录<br>
	 * 为空则不加Owner条件查询
	 * 
	 * @param redeemOwnerId
	 * @param status
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<StateAndRef<PackageState>> getPackageStateWithoutRedeemByStatus(String redeemOwnerId,
			@Nonnull String... status) {
		List<String> asList = Arrays.asList(status);
		logger.debug("getPackageStateByStatus :{}", asList);
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);

		Field statusField = getField(PackageSchemaV1.PersistentPackageState.class, "status");
		CriteriaExpression<?, Boolean> exp = Builder.in(statusField, asList);
		QueryCriteria crit = new QueryCriteria.VaultCustomQueryCriteria(exp);
		QueryCriteria cc = null;
		if (StringUtils.isNotBlank(redeemOwnerId)) {
			Field ownField = getField(PackageSchemaV1.PersistentPackageState.class, "owner");
			CriteriaExpression expOwn = Builder.notEqual(ownField, redeemOwnerId);
			QueryCriteria critOwn = new QueryCriteria.VaultCustomQueryCriteria(expOwn);
			cc = generalCriteria.and(crit).and(critOwn);
		} else {
			cc = generalCriteria.and(crit);
		}

		Page<PackageState> result = rpcops.vaultQueryByCriteria(cc, PackageState.class);

		List<StateAndRef<PackageState>> list = result.getStates();
		logger.debug("getPackageStateByStatus {} query result size:{} ", asList, list.size());
		return list;
	}

	/**
	 * 根据状态查询符合条件的状态为consumedStatus的package
	 * 
	 * @param status
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<StateAndRef<PackageState>> getPackageStateByStatus(Vault.StateStatus consumedStatus,
			@Nonnull String... status) {
		List<String> asList = Arrays.asList(status);
		logger.debug("getPackageStateByStatus,consumedStatus:{} ,status:{}", asList);
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(consumedStatus);
		Field statusField = getField(PackageSchemaV1.PersistentPackageState.class, "status");
		CriteriaExpression exp = Builder.in(statusField, asList);
		QueryCriteria crit = new QueryCriteria.VaultCustomQueryCriteria(exp);
		QueryCriteria cc = generalCriteria.and(crit);
		Page<PackageState> result = rpcops.vaultQueryByCriteria(cc, PackageState.class);

		List<StateAndRef<PackageState>> list = result.getStates();
		logger.debug("getPackageStateByStatus {} query result size:{} ", asList, list.size());
		return list;
	}

	/**
	 * 根据篮子id查询符合条件的状态为consumedStatus的package
	 * 
	 * @param status
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<StateAndRef<PackageState>> getPackageStateById(Vault.StateStatus consumedStatus,
			@Nonnull String... basketNo) {
		List<String> asList = Arrays.asList(basketNo);
		logger.debug("getPackageStateById :{}", asList);
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.CONSUMED);
		Field statusField = getField(PackageSchemaV1.PersistentPackageState.class, "basketno");
		CriteriaExpression exp = Builder.in(statusField, asList);
		QueryCriteria crit = new QueryCriteria.VaultCustomQueryCriteria(exp);
		QueryCriteria cc = generalCriteria.and(crit);
		Page<PackageState> result = rpcops.vaultQueryByCriteria(cc, PackageState.class);
		List<StateAndRef<PackageState>> list = result.getStates();
		List<UniqueIdentifier> uList = Lists.newArrayList();
		list.stream().forEach(p -> uList.add(p.getState().getData().getLinearId()));

		List<StateAndRef<PackageState>> uResult = getAllPackageState(uList);

		logger.debug("getPackageStateById {} query result size:{} ", asList, uResult.size());
		return uResult;
	}

	/**
	 * 根据篮子id查询符合条件的状态为consumedStatus的package
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param consumedStatus
	 * @param basketNo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<StateAndRef<PackageState>> getPackageStatePageById(int pageNumber, int pageSize,
			Vault.StateStatus consumedStatus, @Nonnull String... basketNo) {
		List<String> asList = Arrays.asList(basketNo);
		logger.debug("getPackageStateById :{}", asList);
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(consumedStatus);
		Field statusField = getField(PackageSchemaV1.PersistentPackageState.class, "basketno");
		CriteriaExpression exp = Builder.in(statusField, asList);
		QueryCriteria crit = new QueryCriteria.VaultCustomQueryCriteria(exp);
		QueryCriteria cc = generalCriteria.and(crit);
		Page<PackageState> result = rpcops.vaultQueryByCriteria(cc, PackageState.class);

		// Page<PackageState> result =
		// rpcops.vaultQueryByWithPagingSpec(PackageState.class, cc, page);

		List<StateAndRef<PackageState>> list = result.getStates();
		List<UniqueIdentifier> uList = Lists.newArrayList();
		list.stream().forEach(p -> uList.add(p.getState().getData().getLinearId()));

		List<StateAndRef<PackageState>> uResult = getAllPackageStateByPage(pageNumber, pageSize, uList);

		logger.debug("getPackageStateById {} query result size:{} ", asList, uResult.size());
		return uResult;
	}

	/**
	 * 查询所有钻石
	 * 
	 * @return
	 */
	public List<StateAndRef<PackageState>> getAllPackageState() {
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		Page<PackageState> result = rpcops.vaultQueryByCriteria(generalCriteria, PackageState.class);
		List<StateAndRef<PackageState>> list = result.getStates();
		logger.debug("getAllPackageState query result size:{} ", list.size());
		return list;
	}

	public List<StateAndRef<PackageState>> getAllPackageState(int pageNum, int pageSize) {
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		PageSpecification page = new PageSpecification(pageNum, pageSize);

		Page<PackageState> result = rpcops.vaultQueryByWithPagingSpec(PackageState.class, generalCriteria, page);
		List<StateAndRef<PackageState>> list = result.getStates();
		logger.debug("getAllPackageState query result size:{} ", list.size());
		return list;
	}

	/**
	 * 根据篮子id查询符合条件的状态为UNCONSUMED的package
	 * 
	 * @param status
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<StateAndRef<PackageState>> getPackageStateById(@Nonnull String... basketNo) {
		List<String> asList = Arrays.asList(basketNo);
		logger.debug("getPackageStateById :{}", asList);
		QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		Field statusField = getField(PackageSchemaV1.PersistentPackageState.class, "basketno");
		CriteriaExpression exp = Builder.in(statusField, asList);
		QueryCriteria crit = new QueryCriteria.VaultCustomQueryCriteria(exp);
		QueryCriteria cc = generalCriteria.and(crit);
		Page<PackageState> result = rpcops.vaultQueryByCriteria(cc, PackageState.class);
		List<StateAndRef<PackageState>> list = result.getStates();
		logger.debug("getPackageStateById {} query result size:{} ", asList, list.size());
		return list;
	}

	private static Field getField(Class<?> clazz, String filedName) {
		return ClassMethodFactory.Instance.getClassMethods(clazz).getField(filedName);
	}

	/**
	 * 获得指定节点，指定baksetno的package信息
	 * 
	 * @param counterparty
	 * @param basketno
	 * @return
	 * @throws DiamondWebException
	 */
	public PackageState getCounterPartyDiamond(String counterparty, String basketno) throws DiamondWebException {
		PackageState diamondstate = null;
		CordaX500Name x500Name = CordaX500Name.parse(counterparty);
		final Party counterIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (counterIdentity == null)
			throw new DiamondWebException("Counterparty not found");
		try {
			FlowHandle<PackageState> flowhandle = rpcops.startFlowDynamic(DiamondCollectFlow.Initiator.class,
					counterIdentity, basketno);
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

	private PackageState getSinglePackageState(String baksetno) {
		List<StateAndRef<PackageState>> list = getPackageStateById(baksetno);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0).getState().getData();

	}

	/**
	 * package 相关的操作
	 * 
	 * @param supplier
	 *            供应商
	 * @param pkgInfo
	 *            package 信息
	 * @param state
	 *            create|submit|remove
	 * @return
	 * @throws DiamondWebException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createPackage(String supplier, PackageState pkgInfo, String state) throws DiamondWebException {
		if (pkgInfo == null) {
			return;
		}
		String basketno = pkgInfo.getBasketno();
		String stateStr = Constants.PKG_STATE_MAP.get(state);

		logger.debug("{} :aoc {}, basketno:{} ,pkgInfo: {} ", stateStr, supplier, basketno, pkgInfo);
		CordaX500Name x500Name = CordaX500Name.parse(supplier);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null) {
			logger.error("{} supplier not found {}", stateStr, supplier);
			throw new DiamondWebException(stateStr + " supplier not found");
		}
		// List<Party> supplierlist = rpcops.nodeInfo().getLegalIdentities();
		// if ((supplierlist == null) || (supplierlist.size() == 0)) {
		// logger.error("{} can't find supplier ", stateStr);
		// throw new DiamondWebException("can't find supplier");
		// }
		// logger.debug("getLegalIdentities {} ", supplierlist.toString());
		try {
			Class initClass = null;
			if (PackageState.PKG_ISSUE.equals(state)) {
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
			strbuf.append(supplier);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.error("Failure to " + stateStr, ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * 提交corda Package状态检查
	 * @param basketno
	 * @param newStatus 新提交的状态
	 * @throws DiamondWebException
	 */
	private  void checkPreStatus(String basketno,String newStatus) throws DiamondWebException {
		PackageState prePackage = getSinglePackageState(basketno);
		if (prePackage == null) {
			logger.error("unknown package: {}",basketno);
			throw new DiamondWebException("unknown package :" + basketno);
		}

		String preStatus = prePackage.getStatus();
		String stateStr = Constants.PKG_STATE_MAP.get(newStatus);
		String preStatusStr = Constants.PKG_STATE_MAP.get(preStatus);

		if (!AOCBeanUtils.checkPreState(newStatus, preStatus)) {
			logger.error("package check pre-status failed: {} ,status: {},preStatus: {}",basketno,stateStr,preStatusStr);
			throw new DiamondWebException("package :" + basketno + " status '"
					+ preStatusStr + "' is not allow '"
					+ stateStr+"'");
		}
	}

	/**
	 * 提交钻石
	 * 
	 * @param aoc
	 * @param basketno
	 * @return
	 * @throws DiamondWebException
	 */
	public void issueDiamond(String aoc, String basketno, List<DiamondsInfo> diamondinfolist)
			throws DiamondWebException {
		logger.debug("issueDiamond aoc: {} ,basketno : {}", aoc, basketno);
		
		if(AOCBeanUtils.isEmpty(diamondinfolist)|| diamondinfolist.get(0)==null) {
			logger.warn("diamond list is empty,won't issue diamond,baksetno: {}",basketno);
			return;
		}
		
		checkPreStatus(basketno, diamondinfolist.get(0).getStatus());
		
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		// List<Party> supplierlist = rpcops.nodeInfo().getLegalIdentities();
		// if ((supplierlist == null) || (supplierlist.size() == 0))
		// throw new DiamondWebException("can't find supplier");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(DiamondIssueFlow.Initiator.class,
					aocIdentity, basketno, diamondinfolist);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" supplied by ");
			strbuf.append(diamondinfolist.get(0).getSuppliercode()).append(" basketno ").append(basketno);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to issue diamond:", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * aoc请求Lab认证钻石
	 * 
	 * @param basketno
	 *            待认证basketno
	 * @param status
	 *            PackageState.AOC_REQ_LAB_VERIFY |AOC_SUBMIT_LAB_VERIFY
	 * @param lab
	 * @return
	 * @throws DiamondWebException
	 */
	public void reqLabVerifyDiamond(String basketno, String status, String lab) throws DiamondWebException {
		logger.debug("reqLabVerifyDiamond lab: {} ,basketno : {},status : {}", lab, basketno, status);
		
		checkPreStatus(basketno, status);
		CordaX500Name x500Name = CordaX500Name.parse(lab);
		final Party labIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (labIdentity == null)
			throw new DiamondWebException("lab not found");
		// List<Party> aoclist = rpcops.nodeInfo().getLegalIdentities();
		// if ((aoclist == null) || (aoclist.size() == 0))
		// throw new DiamondWebException("can't find aoc");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops
					.startFlowDynamic(DiamondReqLabVerifyFlow.Initiator.class, basketno, status, labIdentity);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" verify by ");
			strbuf.append(labIdentity).append(" basketno ").append(basketno);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to reqLabVerify diamond:", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * Lab响应认证请求
	 * 
	 * @param pkgInfo
	 * @return
	 * @throws DiamondWebException
	 */
	public void labVerifyResp(PackageInfo pkgInfo) throws DiamondWebException {
		logger.debug("labVerifyResp {}", pkgInfo.toString());
		checkPreStatus(pkgInfo.getBasketno(), pkgInfo.getStatus());
		String aoc = pkgInfo.getAoc();
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		// List<Party> lablist = rpcops.nodeInfo().getLegalIdentities();
		// if ((lablist == null) || (lablist.size() == 0))
		// throw new DiamondWebException("can't find Lab");
		logger.info("Diamond {} is verify completely by {}", pkgInfo.getBasketno(), pkgInfo.getLocgradlab());
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(DiamondLabRespFlow.Initiator.class,
					pkgInfo.getBasketno(), aocIdentity, pkgInfo.getGiaapproveddate(), pkgInfo.getStatus(),
					pkgInfo.getResult(), pkgInfo.getReverification());
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" for diamond ");
			strbuf.append(pkgInfo.getBasketno());
			strbuf.append(" having verified and sent to ");
			strbuf.append(aoc);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to confirm diamond lab verify:{}", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * aoc请求vault确认钻石信息
	 * 
	 * @param basketno
	 *            待确认钻石
	 * @param status
	 *            AOC_REQ_VAULT_VERIFY|AOC_SUBMIT_VAULT_VERIFY
	 * @param vault
	 *            请求vault
	 * @param owner
	 *            钻石拥有者
	 * @return
	 * @throws DiamondWebException
	 */
	public void reqVaultVerifyDiamond(String basketno, String status, String vault, String owner)
			throws DiamondWebException {
		logger.debug("reqVaultVerifyDiamond vault: {} ,basketno : {},status :{},owner: {}", vault, basketno, status,
				owner);
		checkPreStatus(basketno, status);
		CordaX500Name x500Name = CordaX500Name.parse(vault);
		final Party vaultIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (vaultIdentity == null)
			throw new DiamondWebException("vault " + vault + " not found");
		// List<Party> aoclist = rpcops.nodeInfo().getLegalIdentities();
		// if ((aoclist == null) || (aoclist.size() == 0))
		// throw new DiamondWebException("can't find aoc");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(
					DiamondReqVaultVerifyFlow.Initiator.class, basketno, status, vaultIdentity, owner);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" supplied by ");
			strbuf.append(DiamondApplicationRunner.getAllUserMap().get("AOC")).append(" basketno ").append(basketno);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to reqVaultVerify diamond:", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * vault 响应aoc确认请求
	 * 
	 * @param pkgInfo
	 * @return
	 * @throws DiamondWebException
	 */
	public void vaultVerifyResp(PackageInfo pkgInfo) throws DiamondWebException {
		logger.debug("vaultVerifyResp {}", pkgInfo.toString());
		checkPreStatus(pkgInfo.getBasketno(), pkgInfo.getStatus());
		String aoc = pkgInfo.getAoc();
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		// List<Party> vaultlist = rpcops.nodeInfo().getLegalIdentities();
		// if ((vaultlist == null) || (vaultlist.size() == 0))
		// throw new DiamondWebException("can't find vaultor");
		logger.info("Diamond {} is verify completely by {}", pkgInfo.getBasketno(), pkgInfo.getVault());
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(
					DiamondVaultRespFlow.Initiator.class, pkgInfo.getBasketno(), aocIdentity, pkgInfo.getStatus(),
					pkgInfo.getInvtymgr(), pkgInfo.getSealedbagno());
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" for diamond ");
			strbuf.append(pkgInfo.getBasketno());
			strbuf.append(" having verified and sent to ");
			strbuf.append(aoc);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to confirm diamond vault:{}", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * aoc向vault发起钻石拥有者更改确认请求
	 * 
	 * @param basketno
	 * @param vault
	 * @param owner
	 *            新拥有者
	 * @return
	 * @throws DiamondWebException
	 */
	public void submitChangeOwnerDiamond(String basketno, String vault, String owner,String status) throws DiamondWebException {
		logger.debug("reqChangeOwnerDiamond vault: {} ,basketno : {},owner : {}", vault, basketno, owner);
		checkPreStatus(basketno, status);
		CordaX500Name x500Name = CordaX500Name.parse(vault);
		final Party vaultIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (vaultIdentity == null)
			throw new DiamondWebException("vault " + vault + " not found");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops
					.startFlowDynamic(DiamondReqChangeOwnerFlow.Initiator.class, basketno, owner, vaultIdentity);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" supplied by ");
			strbuf.append(DiamondApplicationRunner.getAllUserMap().get("AOC")).append(" basketno ").append(basketno);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to reqVaultVerify diamond:", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * vault 响应钻石拥有者更改请求
	 * 
	 * @param basketno
	 * @param aoc
	 * @return
	 * @throws DiamondWebException
	 */
	public void changeOwnerResp(String basketno, String aoc,String status) throws DiamondWebException {
		logger.debug("changeOwnerResp aoc: {} ,basketno : {}", aoc, basketno);
		checkPreStatus(basketno, status);
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops
					.startFlowDynamic(DiamondChangeOwnerRespFlow.Initiator.class, basketno, aocIdentity);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" for diamond ");
			strbuf.append(basketno);
			strbuf.append(" having verified and sent to ");
			strbuf.append(aoc);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to confirm diamond lab verify:{}", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * aoc 请求auditor 审计
	 * 
	 * @param auditor
	 * @param basketno
	 * @return
	 * @throws DiamondWebException
	 */
	public void auditDiamond(String auditor, String basketno,String status) throws DiamondWebException {
		logger.debug("auditDiamond auditor: {} ,basketno : {}", auditor, basketno);
		checkPreStatus(basketno, status);
		CordaX500Name x500Name = CordaX500Name.parse(auditor);
		final Party auditorIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (auditorIdentity == null)
			throw new DiamondWebException("auditor not found");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops
					.startFlowDynamic(DiamondReqAuditFlow.Initiator.class, basketno, auditorIdentity);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId()).append(" basketno ").append(basketno);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to audit diamond:", ex);

			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * auditor 响应审计请求
	 * 
	 * @param basketno
	 * @param aoc
	 * @param auditdate
	 * @param status
	 * @param result
	 * @return
	 * @throws DiamondWebException
	 */
	public void auditDiamondResp(String basketno, String aoc, String auditdate, String status, String result)
			throws DiamondWebException {
		logger.debug("auditDiamondResp aoc: {} ,basketno : {},status: {} , result: {}", aoc, basketno, status, result);
		checkPreStatus(basketno, status);
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(
					DiamondAuditRespFlow.Initiator.class, basketno, aocIdentity, auditdate, status, result);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" for diamond ");
			strbuf.append(basketno);
			strbuf.append(" having verified and sent to ");
			strbuf.append(aoc);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to confirm diamond lab verify:{}", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * 钻石提取请求
	 * 
	 * @param vault
	 * @param basketno
	 * @return
	 * @throws DiamondWebException
	 */
	public void redeemDiamond(String basketno, String vault) throws DiamondWebException {
		logger.debug("redeemDiamond vault: {} ,basketno : {}", vault, basketno);
		CordaX500Name x500Name = CordaX500Name.parse(vault);
		final Party vaultIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (vaultIdentity == null)
			throw new DiamondWebException("vault not found");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops.startFlowDynamic(DiamondRedeemFlow.Initiator.class,
					vaultIdentity, basketno);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" basketno ").append(basketno);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to redeem diamond:", ex.getMessage());
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}

	/**
	 * 响应钻石提取
	 * 
	 * @param basketno
	 * @param aoc
	 * @return
	 * @throws DiamondWebException
	 */
	public void redeemDiamondResp(String basketno, String aoc) throws DiamondWebException {
		logger.debug("redeemDiamondResp aoc: {} ,basketno : {}", aoc, basketno);
		CordaX500Name x500Name = CordaX500Name.parse(aoc);
		final Party aocIdentity = rpcops.wellKnownPartyFromX500Name(x500Name);
		if (aocIdentity == null)
			throw new DiamondWebException("AOC not found");
		try {
			final FlowHandle<SignedTransaction> flowhandle = rpcops
					.startFlowDynamic(DiamondRedeemRespFlow.Initiator.class, basketno, aocIdentity);
			final SignedTransaction stxn = flowhandle.getReturnValue().get();
			StringBuilder strbuf = new StringBuilder();
			strbuf.append("Transaction completed with id ");
			strbuf.append(stxn.getId());
			strbuf.append(" for diamond ");
			strbuf.append(basketno);
			strbuf.append(" having verified and sent to ");
			strbuf.append(aoc);
			logger.info(strbuf.toString());
		} catch (Exception ex) {
			logger.warn("Failure to redeem diamond:{}", ex);
			throw new DiamondWebException(ex.getMessage(), ex);
		}
	}
}
