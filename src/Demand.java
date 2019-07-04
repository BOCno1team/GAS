import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.org.example.cfc.InvokeBCP;
import main.java.org.example.cfc.QueryBCP;

public class Demand implements Comparable<Demand>, Serializable {
	private int demandId;
	private String name;
	private String category;
	private int amountNeeded;
	private String unit;
	private int priority; // range from 1 to 3 to represent the urgency.
	private int demanderId;

	private final static String chainCode = "go_package8";
	private static final long serialVersionUID = 20190625050327L;

	public static void main(String[] args) {	
		int demandId = 1;
		String name = "bread";
		String category = "Food";
		int amountNeeded = 800;
		String unit = "L";
		int priority = 1;
		Demand d1 = new Demand(demandId, name, category, amountNeeded, unit, priority);
		
//		ProfitableSupply.updateProfitableSupply(1001, 300);
//		ProfitableSupply.updateProfitableSupply(1002, 100);
//		UnprofitableSupply.updateUnprofitableSupply(901, 100);
//		UnprofitableSupply.updateUnprofitableSupply(901, 200);
		
		InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(801),
				String.valueOf(100)};
		try {
			invoke.invoke(chainCode,"updateProfitablesupplyamount",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InvokeBCP invoke2 = new InvokeBCP();
		String[] invokeArgs2 = new String[]{String.valueOf(802),
				String.valueOf(150)};
		try {
			invoke.invoke(chainCode,"updateProfitablesupplyamount",invokeArgs2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InvokeBCP invoke3 = new InvokeBCP();
		String[] invokeArgs3 = new String[]{String.valueOf(703),
				String.valueOf(300)};
		try {
			invoke.invoke(chainCode,"updateUnprofitablesupplyamount",invokeArgs3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		ProfitableSupply.updateProfitableSupply(801, 300);
//		ProfitableSupply.updateProfitableSupply(802, 150);
//		UnprofitableSupply.updateUnprofitableSupply(701, 100);
//		UnprofitableSupply.updateUnprofitableSupply(702, 200);
//		UnprofitableSupply.updateUnprofitableSupply(703, 300);
	
		int supplyId2 = 701;
		String name2 = "bread";
		double amount2 =  100;
		String unit2 = "kg";
		int providerId2 = 501;
		UnprofitableSupply s2 = new UnprofitableSupply(supplyId2, name2, amount2, unit2, providerId2, 4);
		
		int supplyId3 = 702;
		String name3 = "bread";
		double amount3 =  200;
		String unit3 = "kg";
		int providerId3 = 502;
		UnprofitableSupply s3 = new UnprofitableSupply(supplyId3, name3, amount3, unit3, providerId3, 4);
		
		s2.setAmount(100);
		s2.updateUnprofitableSupply();
		
		s3.setAmount(200);
		s3.updateUnprofitableSupply();
		
		MatchResult result = d1.matchToSupply();
		
		int orgID = 503; 
		List<Integer> feedbackList = result.getFeedbackOrgs(orgID);
		System.out.println("-----" + orgID +" should give & get feedback from organizations:----");
		System.out.println(feedbackList); //******************
		
		
		result.giveFeedback(1, 503, 5, 5, 5, 5, 5);
		
		//print previous result
		System.out.println("Previous score of org" + orgID + " is" + Organization.getScoreById(503));
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		result.giveFeedback(1, 503, 10, 10, 10, 10, 10);
		
		
		//verify result
		String key = Integer.toString(1)+"-"+Integer.toString(503);
		QueryBCP query = new QueryBCP();
		String[] queryArgs = new String[]{key}; 

		try {
			String jsonStr = query.query("go_package2","query", queryArgs);
			System.out.println(jsonStr);
	   } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("new score of org" + orgID + " is" + Organization.getScoreById(503));
	}
	/*
	 * The map that matches a category to its corresponding priority.
	 */
	private static Map<String, Integer> priorityForCategoryMap = new HashMap<String, Integer>() {
		{
			put("Food", 1);
			put("Medical", 2);
			put("Rescue", 3);
			put("Epidemic Prevention", 4);
			put("Construction", 6);
		}
	};

	/**
	 * The constructor with the default priority.
	 * 
	 * @param name
	 * @param amount
	 * @param unit
	 */
	public Demand(int demandId, String name, String category, int amount, String unit, int demanderId) {
		super();
		this.demandId = demandId;
		this.name = name;
		this.category = category;
		this.amountNeeded = amount;
		this.unit = unit;
		this.priority = priorityForCategoryMap.get(category);
		this.demanderId = demanderId;
	}

	/**
	 * The constructor with user defined priority.
	 * 
	 * @param name
	 * @param amount
	 * @param unit
	 * @param priority
	 */
	public Demand(int demandId,String name, String category, int amount, String unit, int demanderId, int priority) {
		super();
		this.demandId = demandId;
		this.name = name;
		this.category = category;
		this.amountNeeded = amount;
		this.unit = unit;
		this.demanderId = demanderId;
		this.priority = priority;
	}

	/*
	 * Match this demand in the supply pools.
	 */
	public MatchResult matchToSupply() {
		SupplyManager supplyManager = new SupplyManager();

		// First match with the unprofitable supply pool
		System.out.println("********** matching in unprofitable supply pool **********");
		List<Supply> unprofitableSupplyList = supplyManager.mapInUnprofitableSupplyPool(this.getName(),
				this.amountNeeded);
		double sum = supplyManager.getTotalAmount(unprofitableSupplyList);
		
		
		double amountStillNeeded = this.amountNeeded - sum;
		if (amountStillNeeded <= 0) {
			System.out.println("Unprofitable supply List is:\n");
			System.out.println(unprofitableSupplyList);
			return new MatchResult(this, unprofitableSupplyList, null, null, sum);		
		}

		// Calculate the price needed to pay for the available resources
		// in the profitable supply pool.
		System.out.println("********** calculating price in profitable supply pool **********");
		double price = supplyManager.calculatePriceInProfitableSupplyPool(this.getName(), (int) amountStillNeeded);
		
		
		// Map in the profitable supply pool with the fund.
		System.out.println("********** calculating fund available **********");
		double fund = price < supplyManager.getTotalFund() ? price : supplyManager.getTotalFund();
		System.out.println("********** matching in profitable supply pool **********");
		List<Supply> profitableSupplyList = supplyManager.mapInProfitableSupplyPool(
				this.getName(), (int) amountStillNeeded, fund);
		sum += supplyManager.getTotalAmount(profitableSupplyList);
		double fundUsed = supplyManager.getTotalPrice(profitableSupplyList);

		// Deduct the fund actually used in the unprofitable supply pool.
		List<Supply> fundList = supplyManager.mapInUnprofitableSupplyPool("Fund", fundUsed);
		if (fundUsed != supplyManager.getTotalAmount(fundList)) {
			throw new RuntimeException();
		}
		
		System.out.println("Unprofitable supply List:\n");
		System.out.println(unprofitableSupplyList);
		
		System.out.println("Total price needed is: " + price + "USD");
		System.out.println("Fund actually provided is " + fund + "USD");
		System.out.println("\n Fund List is:");
		System.out.println(fundList);
		
		System.out.println("\n Profitable supply List is:");
		System.out.println(profitableSupplyList);
		
		System.out.println("Actually gathered amount of resources is:" + sum);
		
		if (sum < this.amountNeeded) {
			System.out.println("\nUnfulfilled demand is added to the demand pool\n");
		}
		
		MatchResult result = new MatchResult(this, unprofitableSupplyList, profitableSupplyList, fundList, sum);
		result.prepareForFeedback();
		return result;
	}

	@Override
	public int compareTo(Demand other) {
		if (this.priority < other.priority) {
			return 1;
		} else if (this.priority > other.priority) {
			return -1;
		}
		return 0;
	}

	// The getters and setters
	public int getDemandId() {
		return demandId;
	}

	public void setDemandId(int demandId) {
		this.demandId = demandId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amountNeeded;
	}

	public void setAmount(int amount) {
		this.amountNeeded = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
