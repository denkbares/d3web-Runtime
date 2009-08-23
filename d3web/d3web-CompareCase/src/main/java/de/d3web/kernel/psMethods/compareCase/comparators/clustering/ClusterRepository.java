package de.d3web.kernel.psMethods.compareCase.comparators.clustering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.d3web.caserepository.CaseObject;
import de.d3web.kernel.psMethods.compareCase.comparators.CompareMode;
import de.d3web.utilities.caseLoaders.CaseRepository;

/**
 * Repository for all clusters. Can be used for retrieval, update, clustering and deletion.
 * @author bruemmer
 */
public class ClusterRepository {

	public static int __CASE_PROCESS = 0;

	private static Map instancesByKbid = new HashMap();

	private boolean initialized = false;
	private String kbid = null;
	private double startMinSim = 0;
	private Set clusters = null;
	private Map clustersByCaseId = null;
	private Set cases = null;
	private CompareMode compareMode = null;

	private ClusterRepository(String kbid) {
		this.kbid = kbid;
		clusters = new HashSet();
		clustersByCaseId = new HashMap();
	}

	public static ClusterRepository getInstance(String kbid) {
		if (instancesByKbid.get(kbid) == null) {
			instancesByKbid.put(kbid, new ClusterRepository(kbid));
		}
		return (ClusterRepository) instancesByKbid.get(kbid);
	}

	/**
	 * 
	 * @param cases all cases that shall be clustered
	 * @param compareMode Mode for case comparison
	 */
	public void initialize(Set caseIds, CompareMode compareMode, double startMinSim) {
		this.compareMode = compareMode;
		clusters = new HashSet();
		clustersByCaseId = new HashMap();
		__CASE_PROCESS = 0;
		CaseCluster.___COMPARE_SUM = 0;
		System.out.println("Start clustering...");
		reCluster(caseIds, startMinSim);
		System.out.println("done.");
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public int getClusterCount() {
		return clusters.size();
	}

	/**
	 * Retrieves the clusters that match for the given case
	 * @param queryCase query to search for similar clusters
	 * @return matching clusters
	 */
	public Set retrieveClusters(String queryCaseId) {
		Set ret = new HashSet();
		Iterator iter = clusters.iterator();
		while (iter.hasNext()) {
			CaseCluster cluster = (CaseCluster) iter.next();
			CaseObject queryCase = CaseRepository.getInstance().getCaseById(kbid, queryCaseId);
			if (cluster.fulfilsMinSim(queryCase)) {
				ret.add(cluster);
			}
		}
		return ret;
	}

	public Set retrieveAllClustersContaining(String caseId) {
		Set ret = (Set) clustersByCaseId.get(caseId);
		if (ret == null) {
			ret = new HashSet();
		}
		return ret;
	}

	/**
	 * @param cobj query case
	 * @return a set of the most similar cases for the current query
	 */
	public Set retrieveMostSimilarCaseIds(CaseObject queryCase) {
		Set ret = new HashSet();
		Iterator iter = clusters.iterator();
		while (iter.hasNext()) {
			CaseCluster cluster = (CaseCluster) iter.next();
			if (cluster.fulfilsMinSim(queryCase)) {
				ret.addAll(cluster.getCaseIds());
				ret.add(cluster.getCenterId());
			}
		}
		return ret;
	}

	public CaseCluster retrieveClusterByRepresentative(String caseId) {
		Iterator iter = retrieveAllClustersContaining(caseId).iterator();
		while (iter.hasNext()) {
			CaseCluster cluster = (CaseCluster) iter.next();
			if (cluster.getCenterId().equals(caseId)) {
				return cluster;
			}
		}
		return null;
	}

	/**
	 * Adds a new case to cluster repository
	 * @param cobj
	 */
	public void update(String caseId) {
		__CASE_PROCESS++;
		delete(caseId);
		cases.add(caseId);
		CaseCluster newCluster = new CaseCluster(kbid, caseId, startMinSim, compareMode);
		if (!clusters.contains(newCluster)) {
			// createCluster
			Iterator iter = cases.iterator();
			while (iter.hasNext()) {
				String cId = (String) iter.next();
				CaseObject cobj = CaseRepository.getInstance().getCaseById(kbid, cId);
				if (!cId.equals(caseId) && newCluster.fulfilsMinSim(cobj)) {
					newCluster.addCaseId(cId);
				}
			}
			// check overlapping (contain)
			boolean wasContained = false;
			iter = clusters.iterator();
			while (iter.hasNext()) {
				CaseCluster cluster = (CaseCluster) iter.next();
				if (cluster.containsCluster(newCluster)) {
					cluster.union(newCluster);
					updateClustersByCaseId(newCluster, cluster);
					wasContained = true;
				}
			}
			if (!wasContained) {
				clusters.add(newCluster);
				updateClustersByCaseId(newCluster, newCluster);
			}
		}
	}

	private void updateClustersByCaseId(CaseCluster clusterWithNewCases, CaseCluster clusterToEnter) {
		String centerId = clusterWithNewCases.getCenterId();
		putIntoClusterByCaseIdHash(centerId, clusterToEnter);
		Iterator caseIdIter = clusterWithNewCases.getCaseIds().iterator();
		while (caseIdIter.hasNext()) {
			String caseId = (String) caseIdIter.next();
			putIntoClusterByCaseIdHash(caseId, clusterToEnter);
		}
	}

	/**
	 * Starts the clustering algorithm with the new minimal similarity
	 * @param startMinSim
	 */
	public void reCluster(Set caseIds, double startMinSim) {
		this.cases = new HashSet(caseIds);
		this.startMinSim = startMinSim;
		Iterator iter = caseIds.iterator();
		while (iter.hasNext()) {
			String cId = (String) iter.next();
			System.out.print("updating: " + cId + " ...");
			update(cId);
			System.out.println(" (" + __CASE_PROCESS + ") done. Comparisons yet: " + CaseCluster.___COMPARE_SUM);
		}
	}

	/**
	 * Removes the given case from cluster repository
	 * @param cobj
	 */
	public void delete(String caseId) {
		cases.remove(caseId);
		Set toUpdate = new HashSet();
		Iterator iter = retrieveAllClustersContaining(caseId).iterator();
		// remove all cluster entries for this case
		clustersByCaseId.remove(caseId);
		while (iter.hasNext()) {
			CaseCluster cluster = (CaseCluster) iter.next();
			if (cluster != null) {
				String centerId = cluster.getCenterId();

				cluster.removeCaseId(centerId);

				if (centerId.equals(caseId)) {
					toUpdate.addAll(cluster.getCaseIds());
					clusters.remove(cluster);
					// remove all entries for toUpdate-cases and current cluster 
					removeClusterEntriesInHash(toUpdate, cluster);

					// update the elements of deleted cluster

					Iterator updateIter = toUpdate.iterator();
					while (updateIter.hasNext()) {
						String updId = (String) updateIter.next();
						update(updId);
					}

				}
			}
		}
	}

	private void putIntoClusterByCaseIdHash(String caseId, CaseCluster cluster) {
		Set clusters = (Set) clustersByCaseId.get(caseId);
		if (clusters == null) {
			clusters = new HashSet();
			clustersByCaseId.put(caseId, clusters);
		}
		clusters.add(cluster);
	}

	private void removeClusterEntriesInHash(Set toDel, CaseCluster cluster) {
		Iterator iter = toDel.iterator();
		while (iter.hasNext()) {
			String caseId = (String) iter.next();

			Set clusters = (Set) clustersByCaseId.get(caseId);
			if (clusters == null) {
				clusters = new HashSet();
				clustersByCaseId.put(caseId, clusters);
			} else {
				clusters.remove(cluster);
			}
		}
	}
}
