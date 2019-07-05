import com.alibaba.fastjson.JSONObject;

import main.java.org.example.cfc.InvokeBCP;
import main.java.org.example.cfc.QueryBCP;

public class ProfitableSupply extends Supply{
	private int amount; // There is no fund in the profitable supply pool, so the amount should be an integer.
	private int unitPrice;

	private final static String chainCode = "go_package8";
	
	public static void main(String[] args) {
//		uplinkProfitableSupply(1003, "water", 150, "L", 502, 3);
//		uplinkProfitableSupply(803, "water", 100, "L", 501, 1);
		
		updateProfitableSupplyAmount(802, 150);
		
		QueryBCP query = new QueryBCP();
		String[] queryArgs = new String[]{Integer.toString(503)};
		String[] queryArgs2 = new String[]{Integer.toString(101)+"-"+Integer.toString(503)};

		try {
			String jsonStr = query.query("go_package8","queryByKey",queryArgs);
			String jsonStr2 = query.query("go_package8","queryByKey",queryArgs2);
			System.out.println(jsonStr);
			System.out.println(jsonStr2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public ProfitableSupply(int supplyId, String name, int amount, String unit, int providerId, int unitPrice, int rank) {
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
	
	public static void uplinkProfitableSupply(int supplyId, String name, int amount, String unit, int providerId, int unitPrice) {
		InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(supplyId), String.valueOf(name),
				String.valueOf(amount),String.valueOf(unit),String.valueOf(providerId), String.valueOf(unitPrice)};
		try {
			invoke.invoke(chainCode,"initProfitablesupply",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updateProfitableSupplyAmount(int supplyId, int amount) {
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
	public void updateProfitableSupplyAmount() {
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
				", providerID"+ this.getProviderId() + " with rank" + Organization.getRankById(this.getProviderId()) + "]";
	}


	public int getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}
	
}
