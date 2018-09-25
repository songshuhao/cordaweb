package ats.blockchain.web.test;


import java.lang.reflect.Field;
import java.util.List;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.cordapp.diamond.schema.PackageSchemaV1;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.utilities.NetworkHostAndPort;

public class QueryTest {

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
				
				List<NodeInfo> nodeInfos = cordarpcops.networkMapSnapshot();
				for(NodeInfo node : nodeInfos) {
					Party p = node.getLegalIdentities().get(0);
					System.out.println(p.toString());
				}
				
				Field[] fields = PackageSchemaV1.PersistentPackageState.class.getDeclaredFields();
				for(Field field : fields) {
					System.out.println(field.getName());
				}
				
				QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
				
		        Field basketno = PackageSchemaV1.PersistentPackageState.class.getDeclaredField("basketno");
		        CriteriaExpression basketnoIndex = Builder.equal(basketno, "qls001");
		        QueryCriteria basketnoCriteria = new QueryCriteria.VaultCustomQueryCriteria(basketnoIndex);
		        
//		        Field status = PackageSchemaV1.PersistentPackageState.class.getDeclaredField("status");
//		        CriteriaExpression statusIndex = Builder.equal(status, "1");
//		        QueryCriteria statusCriteria = new QueryCriteria.VaultCustomQueryCriteria(statusIndex);
		        
		        QueryCriteria criteria = generalCriteria.and(basketnoCriteria);
		        List<StateAndRef<PackageState>> results = cordarpcops.vaultQueryByCriteria(criteria,PackageState.class).getStates();
				for(StateAndRef<PackageState> ref : results) {
					System.out.println(ref.getState().getData().toString());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private static int of(int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}

}
