import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import main.java.org.example.cfc.QueryBCP;

/*
 * Work for the subclasses.
 */
public class SupplyManager {
	//block chain connection profile
	private final static String chainCode = "go_package8";

	public SupplyManager() {
		super();
	}

	// TODO get the corresponding list of supplies
	public static List<UnprofitableSupply> getUnprofitableSupplyList(String ResourceName) {
		QueryBCP queryHelper =new QueryBCP();
		String[] args =new String[]{ResourceName};
		String jsonStr;
		List<UnprofitableSupply> resultList = new ArrayList<UnprofitableSupply>();
		JSONObject jsonObj = null;
		int supplyId = 0;
		String name = null;
		double amount = 0;
		String unit = null;
		int providerId = 0;
		int providerRank = 0;
		try {
			jsonStr = queryHelper.query(chainCode, "queryUnproByName", args);
			JSONArray jsonArr = JSONObject.parseArray(jsonStr);
			for (int i = 0 ; i < jsonArr.size() ; i++){
				jsonObj = jsonArr.getJSONObject(i);
				jsonObj = JSONObject.parseObject(jsonObj.getString("Record"));
				supplyId = jsonObj.getIntValue("supplyID");
				name = jsonObj.getString("name");
				amount = jsonObj.getDoubleValue("amount");
				unit = jsonObj.getString("unit");
				providerId = jsonObj.getIntValue("organization");
				providerRank = Organization.getRankById(providerId);
				resultList.add(new UnprofitableSupply(supplyId,name,amount,unit,providerId,providerRank));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultList;
	}

	// TODO get the corresponding list of supplies
	public List<ProfitableSupply> getProfitableSupplyList(String ResourceName) {
		QueryBCP queryHelper =new QueryBCP();
		String[] args =new String[]{ResourceName};
		String jsonStr;
		List<ProfitableSupply> resultList = new ArrayList<ProfitableSupply>();
		JSONObject jsonObj = null;
		int supplyId = 0;
		String name = null;
		int amount = 0;
		String unit = null;
		int providerId = 0;
		double unitPrice = 0;
		int providerRank = 0;
		try {
			jsonStr = queryHelper.query(chainCode, "queryProByName", args);
			JSONArray jsonArr = JSONObject.parseArray(jsonStr);
			for (int i = 0 ; i < jsonArr.size() ; i++){
				jsonObj = jsonArr.getJSONObject(i);
				jsonObj = JSONObject.parseObject(jsonObj.getString("Record"));
				supplyId = jsonObj.getIntValue("supplyID");
				name = jsonObj.getString("name");
				amount = jsonObj.getIntValue("amount");
				unit = jsonObj.getString("unit");
				providerId = jsonObj.getIntValue("organization");
				unitPrice = jsonObj.getDoubleValue("unitprice");
				providerRank = Organization.getRankById(providerId);
				resultList.add(new ProfitableSupply(supplyId,name,amount,unit,providerId,unitPrice,providerRank));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultList;
	}

	/**
	 * 
	 * @param list
	 * @return 
	 */
	public double getTotalAmount(List<? extends Supply> list) {
		double total = list.stream().mapToDouble(s -> s.getAmount()).sum();
		return total;
	}
	
	public double getTotalPrice(List<Supply> profitableSupplyList) {
		double totalPrice = 0;

		for (Supply s : profitableSupplyList) {
			totalPrice += ((ProfitableSupply)s).getUnitPrice() * s.getAmount();
		}
		return totalPrice;
	}
	
	public double getTotalFund() {
		List<UnprofitableSupply> fundList =  getUnprofitableSupplyList("Fund");
		
		double totalFund = fundList.stream().mapToDouble(s -> s.getAmount()).sum();
		return totalFund;
	}
	
	/**
	 * Map the demand in the unprofitable supply pool.
	 * 
	 * @param amountNeeded
	 * @param supplyList
	 * @return the list of supplies that's mapped to the demand.
	 */
	List<Supply> mapInUnprofitableSupplyPool(String resourceName, double amountNeeded) {
		double sum = 0;
		List<Supply> supplyList = new ArrayList<Supply>();

		List<UnprofitableSupply> unprofitableSupplyPool = getUnprofitableSupplyList(resourceName);
		Collections.sort(unprofitableSupplyPool);

		for (UnprofitableSupply s : unprofitableSupplyPool) {
			if (sum == amountNeeded) {
				break;
			}
			double amountStillNeeded = amountNeeded - sum;
			double amountUsed = s.getAmount() > amountStillNeeded ? amountStillNeeded : s.getAmount();
			
			// Add supply to be used to the supply list
			UnprofitableSupply sCopy = (UnprofitableSupply) s.clone();
			sCopy.setAmount(amountUsed);
			supplyList.add(sCopy);
			
			// Update info
			sum += amountUsed;
			s.deductAmount(amountUsed);
			s.updateUnprofitableSupply();
			
			System.out.println(String.format("%s provided %f amount", s.getProviderId(), amountUsed));
		}
		
		return supplyList;
	}

	/**
	 * Calculate the price needed to pay for the most optimal amount of resources in the 
	 * profitable supply pool.
	 * 
	 * @param resourceName
	 * @param amountNeeded
	 * @return the price for supplies in the profitable supply pool.
	 */
	double calculatePriceInProfitableSupplyPool(String resourceName, int amountNeeded) {
		double price = 0;
		int sum = 0;

		List<ProfitableSupply> profitableSupplyPool = getProfitableSupplyList(resourceName);
		Collections.sort(profitableSupplyPool);

		for (ProfitableSupply s : profitableSupplyPool) {			
			if (sum == amountNeeded) {
				break;
			}

			int amountStillNeeded = amountNeeded - sum;		
			int amountUsed = (int) (s.getAmount() > amountStillNeeded ? amountStillNeeded : s.getAmount());
			sum += amountUsed;
			price += amountUsed * s.getUnitPrice();
		}

		return price;
	}

	/**
	 * Map in the profitable supply pool with the given amount of fund.
	 * 
	 * @param resourceName
	 * @param amountNeeded
	 * @param fund
	 * @return the list of supplies that's mapped to the demand.
	 */
	List<Supply> mapInProfitableSupplyPool(String resourceName, int amountNeeded, double fund) {
		double fundLeft=fund;
		int sum = 0;
		List<Supply> supplyList = new ArrayList<Supply>();

		List<ProfitableSupply> profitableSupplyPool = getProfitableSupplyList(resourceName);
		Collections.sort(profitableSupplyPool);

		for (ProfitableSupply s : profitableSupplyPool) {
			int amountStillNeeded = amountNeeded - sum;
			
			// The amount of supply affordable with the fund.
			int amountAffordable = (int) (fundLeft / s.getUnitPrice());
			if (amountAffordable == 0) { // Since supplies with low unit prices rank ahead.
				break; 
			}
			// The actual amount of supply provided considering both fund and amount.
			int amountProvided = (int) (amountAffordable > s.getAmount() ? s.getAmount() : amountAffordable);
			
			int amountUsed = amountProvided > amountStillNeeded ? amountStillNeeded : amountProvided;
			
			// Add supply to be used to the supply list
			ProfitableSupply sCopy = (ProfitableSupply) s.clone();
			sCopy.setAmount(amountUsed);
			supplyList.add(sCopy);
			
			// Update info
			sum += amountUsed;
			fundLeft -= amountUsed * s.getUnitPrice();
			s.deductAmount(amountUsed);
			s.updateProfitableSupply();
		}
		
		return supplyList;
	}
	public static void main(String[] args) throws Exception{
		getUnprofitableSupplyList("milk");
	}
}

