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
		int demandId = 101;
		String name = "bread";
		String category = "Food";
		int amountNeeded = 600;
		String unit = "kg";
		int demanderId = 601;
		int priority = 1;
		Demand d1 = new Demand(demandId, name, category, amountNeeded, unit, demanderId, priority);

		MatchResult result = d1.matchToSupply();

		int orgID = 503;
		System.out.println("****** Feedback process for org" + orgID + " starts ******");	
		List<Integer> feedbackList = result.getFeedbackOrgs(orgID);
		System.out.println("-----" + orgID + " should get feedback from organizations: " + feedbackList + "\n");
		
		//print previous result
		System.out.println("Previous score of org " + orgID + " is" + Organization.getScoreById(503));
		
		System.out.println("\nGrading in process...");
		result.giveFeedback(demandId, 503, 5, 5, 5, 5, 5);
		result.giveFeedback(demandId, 503, 3, 3, 3, 3, 3);
		result.giveFeedback(demandId, 503, 4, 4, 4, 4, 4);
	
//		verify result
//		String key = Integer.toString(demandId)+"-"+Integer.toString(503);
//		QueryBCP query = new QueryBCP();
//		String[] queryArgs = new String[]{key}; 

		try {
			Thread.sleep(100000);
//			String jsonStr = query.query("go_package2","query", queryArgs);
//			System.out.println(jsonStr);			
	   } catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("new score of org" + orgID + " is " + Organization.getScoreById(503));
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
	public Demand(int demandId, String name, String category, int amount, String unit, int demanderId, int priority) {
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
		System.out.println("****** matching in unprofitable supply pool ******");
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
		System.out.println("\n****** calculating price in profitable supply pool ******");
		double price = supplyManager.calculatePriceInProfitableSupplyPool(this.getName(), (int) amountStillNeeded);
		System.out.println("Total price needed is: " + price + "USD\n");
		
		// Map in the profitable supply pool with the fund.
		System.out.println("****** calculating fund available ******");
		double fund = price < supplyManager.getTotalFund() ? price : supplyManager.getTotalFund();
		System.out.println("Fund actually provided is " + fund + "USD\n");
		
		System.out.println("****** matching in profitable supply pool ******");
		List<Supply> profitableSupplyList = supplyManager.mapInProfitableSupplyPool(this.getName(),
				(int) amountStillNeeded, fund);
		sum += supplyManager.getTotalAmount(profitableSupplyList);
		double fundUsed = supplyManager.getTotalPrice(profitableSupplyList);

		// Deduct the fund actually used in the unprofitable supply pool.
		System.out.println("\n****** matching for fund to cover the profitable supplies ******");
		List<Supply> fundList = supplyManager.mapInUnprofitableSupplyPool("Fund", fundUsed);
		if (fundUsed != supplyManager.getTotalAmount(fundList)) {
			throw new RuntimeException();
		}
		
		System.out.println("\n\n##### Matching finished - Final result as follows:");
		System.out.println("Unprofitable supply List:");
		System.out.println(unprofitableSupplyList);
		
		System.out.println("\n Fund List is:");
		System.out.println(fundList);

		System.out.println("\n Profitable supply List is:");
		System.out.println(profitableSupplyList);

		System.out.println("\n##### Actually gathered amount of resources is:" + sum + this.getUnit());

		if (sum < this.amountNeeded) {
			System.out.println("Unfulfilled demand is added to the demand pool.\n\n");
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
	public int getDemanderId() {
		return demanderId;
	}

	public void setDemanderId(int demanderId) {
		this.demanderId = demanderId;
	}
	
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

	public static void sleep(int second) {
		try {
			Thread.sleep(second * 1000);
	   } catch (Exception e) {
			e.printStackTrace();
		}
	}
}
