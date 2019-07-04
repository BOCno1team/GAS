import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import main.java.org.example.cfc.InvokeBCP;
import main.java.org.example.cfc.QueryBCP;

public class MatchResult {
	private Demand demand = null;
	private List<Supply> unprofitableList=null;
	private List<Supply> profitableList=null;
	private List<Supply> fundList=null;
	private double sumGethered;
	
	private final static String chainCode = "go_package8";
	
	public MatchResult(Demand demand, List<Supply> unprofitableList, List<Supply> profitableList,
			List<Supply> fundList, double sumGethered) {
		super();
		this.demand = demand;
		this.unprofitableList = unprofitableList;
		this.profitableList = profitableList;
		this.fundList = fundList;
		this.sumGethered = sumGethered;
	}
	
	void giveFeedback(int demandId, int receiverId, int grade1, int grade2, int grade3, int grade4, int grade5) {
		String key = Integer.toString(demandId)+"-"+Integer.toString(receiverId);
		QueryBCP query = new QueryBCP();
		String[] queryArgs = new String[]{key}; 

		try {
			String jsonStr = query.query(chainCode,"query", queryArgs);
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
			String[] invokeArgs = new String[]{key, newGradeString};
			InvokeBCP invoke = new InvokeBCP();
			invoke.invoke(chainCode,"set",invokeArgs);
			
			if (count == totalCount) {
				Organization receiver = Organization.queryOrgById(receiverId);				
				receiver.updateOrganization(new1/totalCount, new2/totalCount, 
						new3/totalCount, new4/totalCount, new5/totalCount);
				System.out.println("updated the organization's new grade");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public List<Integer> getFeedbackOrgs(int orgId){
		String orgType = Organization.getTypeById(orgId);
				
		List<Integer> orgList = new ArrayList<Integer>();
		for (Supply s : unprofitableList) {
			if (orgId != s.getProviderId() && !orgList.contains(s) && 
					Organization.getTypeById(s.getProviderId())!=orgType) {
				orgList.add(s.getProviderId());	
			}
		}
		
		for (Supply s : profitableList) {
			if (orgId != s.getProviderId() && !orgList.contains(s) && 
					Organization.getTypeById(s.getProviderId())!=orgType) {
				orgList.add(s.getProviderId());		
			}
		}
		
		for (Supply s : fundList) {
			if (orgId != s.getProviderId() && !orgList.contains(s) && 
					Organization.getTypeById(s.getProviderId())!=orgType) {
				orgList.add(s.getProviderId());		
			}
		}
		
		System.out.println("-----" + orgId +" should give & get feedback from organizations:----");
		System.out.println(orgList); //******************
		
		
		return orgList;
		
		
	}
	
	public int calculateNumOfMembers(int orgId) {
		return getFeedbackOrgs(orgId).size();
	}
	
	public void prepareForFeedback() {
		for (Supply s : unprofitableList) {
			initOneFeedback(s.getProviderId());
		}
		
		for (Supply s : profitableList) {
			initOneFeedback(s.getProviderId());
		}
		
		for (Supply s : fundList) {
			initOneFeedback(s.getProviderId());	
		}
	}
	
	public void initOneFeedback(int receiverId) {
		String key = Integer.toString(this.demand.getDemandId())+"-"+Integer.toString(receiverId);
		JSONObject initialGrade = new JSONObject();
		
		initialGrade.put("grade1", 0);
		initialGrade.put("grade2", 0);
		initialGrade.put("grade3", 0);
		initialGrade.put("grade4", 0);
		initialGrade.put("grade5", 0);
		initialGrade.put("count", 0);
		initialGrade.put("total", initialGrade);
		
		String initialGradeString = JSONObject.toJSONString(initialGrade);
		String[] invokeArgs = new String[]{key, initialGradeString};
		
		try {
			InvokeBCP invoke = new InvokeBCP();
			invoke.invoke(chainCode,"set",invokeArgs);
		} catch (Exception e) {
					// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
