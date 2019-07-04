import com.alibaba.fastjson.JSONObject;

import main.java.org.example.cfc.InvokeBCP;
import main.java.org.example.cfc.QueryBCP;

public class ProfitableSupply extends Supply{
	private int amount; // There is no fund in the profitable supply pool, so the amount should be an integer.
	private double unitPrice;

	private final static String chainCode = "go_package8";
	
	public static void main(String[] args) {
//		uplinkProfitableSupply(1001, "water", 300, "L", 504, 2);
//		uplinkProfitableSupply(1002, "water", 100, "L", 502, 3);
		
		QueryBCP query = new QueryBCP();
		String[] queryArgs = new String[]{Integer.toString(1001)};
		int rank = -1;
		try {
			String jsonStr = query.query("go_package2","query",queryArgs);
			JSONObject json = JSONObject.parseObject(jsonStr);
			System.out.println(jsonStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ProfitableSupply(int supplyId, String name, int amount, String unit, int providerId, double unitPrice, int rank) {
		super(supplyId, name, amount, unit, providerId, rank);
		this.unitPrice = unitPrice;
	}
	
	public void uplinkProfitableSupply() {
		InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(this.getSupplyId()),String.valueOf(this.getName()),
						String.valueOf(this.getAmount()),String.valueOf(this.getUnit()),String.valueOf(this.getProviderId()), String.valueOf(this.getUnitPrice())};
		try {
			invoke.invoke(chainCode,"initProfitablesupply",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void uplinkProfitableSupply(int supplyId, String name, int amount, String unit, int providerId, double unitPrice) {
		InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(supplyId),name,
				String.valueOf(amount),String.valueOf(unit),String.valueOf(providerId), String.valueOf(unitPrice)};
		try {
			invoke.invoke(chainCode,"initProfitablesupply",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updateProfitableSupply(int supplyId, double amount) {
		InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(supplyId),String.valueOf(amount)};
		try {
			invoke.invoke(chainCode,"updateProfitablesupplyamount",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	// TODO Implement the update supply method
	public void updateProfitableSupply() {
		InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(this.getSupplyId()),String.valueOf(this.getAmount())};
		try {
			invoke.invoke(chainCode,"updateProfitablesupplyamount",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int compareTo(Supply other) {
		if (this.getProviderRank() > other.getProviderRank()) {
			return -1; 
		} else if (this.getProviderRank() < other.getProviderRank()) {
			return 1;
		} else { 
			if (this.getUnitPrice() < ((ProfitableSupply) other).getUnitPrice()) {
				return -1;
			} else if (this.getUnitPrice() > ((ProfitableSupply) other).getUnitPrice()) {
				return 1;
		}else { 
				if (this.getAmount() > other.getAmount()) {
					return -1;
				} else if (this.getAmount() < other.getAmount()) {
					return +1;
				}
			}
		}
		return 0;
	}
	
	@Override
	public String toString() {		
		return "ProfitableSupply [supplyId=" + this.getSupplyId() + ", name=" + 
				this.getName() + ", amount=" + this.getAmount() + ", unitPrice=" + this.getUnitPrice() +
				" with provider rank" + Organization.getRankById(this.getProviderId()) + "]";
	}


	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
}
