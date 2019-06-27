import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demand implements Comparable<Demand>, Serializable {
	private int demandId;
	private String name;
	private String category;
	private int amountNeeded;
	private String unit;
	private int priority; // range from 1 to 3 to represent the urgency.
	private int DemanderId;

	private static int currentDemandId = 1;
	private static final long serialVersionUID = 20190625050327L;

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
	public Demand(String name, String category, int amount, String unit) {
		super();
		this.demandId = ++currentDemandId;
		this.name = name;
		this.category = category;
		this.amountNeeded = amount;
		this.unit = unit;
		this.priority = priorityForCategoryMap.get(category);
	}

	/**
	 * The constructor with user defined priority.
	 * 
	 * @param name
	 * @param amount
	 * @param unit
	 * @param priority
	 */
	public Demand(String name, String category, int amount, String unit, int priority) {
		super();
		this.demandId = ++currentDemandId;
		this.name = name;
		this.category = category;
		this.amountNeeded = amount;
		this.unit = unit;
		this.priority = priority;
	}

	/*
	 * Match this demand in the supply pools.
	 */
	public void matchToSupply() {
		SupplyManager supplyManager = new SupplyManager();
		
		// First match with the unprofitable supply pool
		List<Supply> supplyList = supplyManager.mapInUnprofitableSupplyPool(this.getName(), this.amountNeeded);
		
		
		double amountStillNeeded = this.amountNeeded - supplyManager.getTotalAmount(supplyList);
		if (amountStillNeeded == 0) {
			return;
		}

		 /*
		  *   Calculate the price needed to pay for the available resources 
		  *   in the profitable supply pool.
		  */
		double price = supplyManager.calculatePriceInProfitableSupplyPool(this.getName(), amountStillNeeded);
		double fund = price < supplyManager.getTotalFund() ? price : supplyManager.getTotalFund();
		
		// Map in the profitable supply pool with the fund.
//		supplyManager.mapInProfitableSupplyPool(this.getName(), amountStillNeeded, fund);
//		sum += amountPricePair[0];
//		double fundUsed = amountPricePair[1];
//
//		// Deduct the fund actually used in the unprofitable supply pool.
//		if (fundUsed != supplyManager.mapInUnprofitableSupplyPool("Fund", fundUsed)) {
//			throw new RuntimeException();
//		}
		// TODO broadcast transaction to the chain. SUM & FUND USED

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
