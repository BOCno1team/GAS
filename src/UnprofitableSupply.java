
import main.java.org.example.cfc.InvokeBCP;

public class UnprofitableSupply extends Supply {
	//block chain connection profile
	private final static String chainCode = "go_package8";

	public static void main(String[] args) {
		uplinkUnprofitableSupply(901, "water", 100, "L", 501);
		uplinkUnprofitableSupply(902, "water", 200, "L", 503);
		
	}
	
	public UnprofitableSupply(int supplyId, String name, double amount, String unit, int providerID, int providerRank) {
		super(supplyId, name, amount, unit, providerID, providerRank);
	}

    public void uplinkUnprofitableSupply() {
    	InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(this.getSupplyId()),String.valueOf(this.getName()),
						String.valueOf(this.getAmount()),String.valueOf(this.getUnit()),String.valueOf(this.getProviderId())};
		try {
			invoke.invoke(chainCode,"initUnprofitablesupply",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public static void uplinkUnprofitableSupply(int supplyId, String name, double amount, String unit, int providerID) {
    	InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(supplyId),name,
						String.valueOf(amount),String.valueOf(unit),String.valueOf(providerID)};
		try {
			invoke.invoke(chainCode,"initUnprofitablesupply",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateUnprofitableSupply() {
		InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(this.getSupplyId()),String.valueOf(this.getAmount())};
		try {
			invoke.invoke(chainCode,"updateUnprofitablesupplyamount",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updateUnprofitableSupply(int supplyId, double amount) {
		InvokeBCP invoke = new InvokeBCP();
		String[] invokeArgs = new String[]{String.valueOf(supplyId),String.valueOf(amount)};
		try {
			invoke.invoke(chainCode,"updateUnprofitablesupplyamount",invokeArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "Unprofitable [supplyId=" + this.getSupplyId() + ", name=" + this.getName() + ", amount=" + this.getAmount() + 
				", with provider rank" + Organization.getRankById(this.getProviderId()) + "]";
	}
}

