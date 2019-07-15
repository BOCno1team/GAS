package matching;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import main.java.org.example.cfc.InvokeBCP;
import main.java.org.example.cfc.QueryBCP;

public class MatchResult {
	private Demand demand = null;
	private List<Supply> unprofitableList = null;
	private List<Supply> profitableList = null;
	private List<Supply> fundList = null;
	private double sumGethered;

	private final static String chainCode = "go_package8";

	public static void main(String[] args) {
		// initOneFeedback(501);

		String key = Integer.toString(1) + "-" + Integer.toString(501);
		QueryBCP query = new QueryBCP();
		String[] queryArgs = new String[] { key };

		try {
			String jsonStr = query.query(chainCode, "queryByKey", queryArgs);
			System.out.println(jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MatchResult(Demand demand, List<Supply> unprofitableList, List<Supply> profitableList, List<Supply> fundList,
			double sumGethered) {
		super();
		this.demand = demand;
		this.unprofitableList = unprofitableList;
		this.profitableList = profitableList;
		this.fundList = fundList;
		this.sumGethered = sumGethered;
	}

	/**
	 * Give feedback to the organization with receiverId.
	 * @param demandId
	 * @param receiverId
	 * @param grade1
	 * @param grade2
	 * @param grade3
	 * @param grade4
	 * @param grade5
	 */
	void giveFeedback(int demandId, int receiverId, int grade1, int grade2, int grade3, int grade4, int grade5) {
		String key = Integer.toString(demandId) + "-" + Integer.toString(receiverId);
		QueryBCP query = new QueryBCP();
		String[] queryArgs = new String[] { key };

		try {
			String jsonStr = query.query("go_package2", "query", queryArgs);
			JSONObject json = JSONObject.parseObject(jsonStr);
			int sum1 = json.getIntValue("grade1");
			int sum2 = json.getIntValue("grade2");
			int sum3 = json.getIntValue("grade3");
			int sum4 = json.getIntValue("grade4");
			int sum5 = json.getIntValue("grade5");
			int count = json.getIntValue("count");
			int totalCount = json.getIntValue("total");
			count++;

			JSONObject newGrade = new JSONObject();
			int new1 = grade1 + sum1;
			int new2 = grade2 + sum2;
			int new3 = grade3 + sum3;
			int new4 = grade4 + sum4;
			int new5 = grade5 + sum5;

			newGrade.put("grade1", new1);
			newGrade.put("grade2", new2);
			newGrade.put("grade3", new3);
			newGrade.put("grade4", new4);
			newGrade.put("grade5", new5);
			newGrade.put("count", count);
			newGrade.put("total", totalCount);

			String newGradeString = JSONObject.toJSONString(newGrade);
			String[] invokeArgs = new String[] { key, newGradeString };
			InvokeBCP invoke = new InvokeBCP();
			invoke.invoke("go_package2", "set", invokeArgs);

			if (count == totalCount) {
				Organization receiver = Organization.queryOrgById(receiverId);
				receiver.updateOrganization(new1 / totalCount, new2 / totalCount, new3 / totalCount, new4 / totalCount,
						new5 / totalCount);
				System.out.println("Updated the organization's new grade");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the IDs of organizations that orgId needs to give & get feedback.
	 * @param orgId
	 * @return
	 */
	public List<Integer> getFeedbackOrgs(int orgId) {
		String orgType = Organization.getTypeById(orgId);

		List<Integer> orgList = getAllInvolvedOrg();
		List<Integer> result = new ArrayList<Integer>();

		for (int ID : orgList) {
			if (orgId != ID && Organization.getTypeById(ID) != orgType) {
				result.add(ID);
			}
		}

		return result;
	}

	/**
	 * Calculate the number of organizations that it needs to give & get feedback.
	 * @param orgId
	 * @return the number of organizations that it needs to give & get feedback.
	 */
	public int calculateNumOfMembers(int orgId) {
		return getFeedbackOrgs(orgId).size();
	}

	public void prepareForFeedback() {
		List<Integer> orgList = getAllInvolvedOrg();
		for (int orgId : orgList) {
			initOneFeedback(orgId);
		}
	}

	/**
	 * Initialize the grading data for the org with receiverID.
	 * @param receiverId
	 */
	public void initOneFeedback(int receiverId) {
		String key = Integer.toString(this.demand.getDemandId()) + "-" + Integer.toString(receiverId); ////////// 1
		// String key = "1501";
		JSONObject initialGrade = new JSONObject();
		int total = calculateNumOfMembers(receiverId);

		initialGrade.put("grade1", 0);
		initialGrade.put("grade2", 0);
		initialGrade.put("grade3", 0);
		initialGrade.put("grade4", 0);
		initialGrade.put("grade5", 0);
		initialGrade.put("count", 0);
		initialGrade.put("total", total);

		String initialGradeString = JSONObject.toJSONString(initialGrade);
		String[] invokeArgs = new String[] { key, initialGradeString };

		try {
			InvokeBCP invoke = new InvokeBCP();
			invoke.invoke("go_package2", "set", invokeArgs);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * Get the IDs of all the organizations involved in this transaction.
	 * @return IDs of all the organizations involved in this transaction.
	 */
	private List<Integer> getAllInvolvedOrg() {
		List<Integer> orgList = new ArrayList<>();
		orgList.add(this.getDemand().getDemanderId());
		
		for (Supply s : unprofitableList) {
			if (!orgList.contains(s.getProviderId())) {
				orgList.add(s.getProviderId());
			}
		}

		for (Supply s : profitableList) {
			if (!orgList.contains(s.getProviderId())) {
				orgList.add(s.getProviderId());
			}
		}

		for (Supply s : fundList) {
			if (!orgList.contains(s.getProviderId())) {
				orgList.add(s.getProviderId());
			}
		}
		return orgList;
	}

	public Demand getDemand() {
		return demand;
	}

	public void setDemand(Demand demand) {
		this.demand = demand;
	}

	public List<Supply> getUnprofitableList() {
		return unprofitableList;
	}

	public void setUnprofitableList(List<Supply> unprofitableList) {
		this.unprofitableList = unprofitableList;
	}

	public List<Supply> getProfitableList() {
		return profitableList;
	}

	public void setProfitableList(List<Supply> profitableList) {
		this.profitableList = profitableList;
	}

	public List<Supply> getFundList() {
		return fundList;
	}

	public void setFundList(List<Supply> fundList) {
		this.fundList = fundList;
	}

	public double getSumGethered() {
		return sumGethered;
	}

	public void setSumGethered(double sumGethered) {
		this.sumGethered = sumGethered;
	}
}
