package matching;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.opencensus.internal.StringUtils;

//TODO: DEMAND MANAGER plz
public class DemandManager {

	public static void main(String[] args) {
		 System.out.println("+++++++++++++++++++++++++++++++++");
	System.out.println("List转字符串");
		 List<String> list1 = new ArrayList<String>();
		 list1.add("1");
		  list1.add("2");
		 list1.add("3");
		 String ss = String.join(",", list1);

		 System.out.println(ss);
		System.out.println("+++++++++++++++++++++++++++++++++");
		System.out.println("字符串转List");
		List<String> listString = Arrays.asList(ss.split(","));
		for (String string : listString) {
		System.out.println(string);
		}
		System.out.println("+++++++++++++++++++++++++++++++++");
	}
	
	public DemandManager() {
		super();
	}
	
	/**
	 * Match the demands to the supplies.
	 * @param demandList
	 */
	void matchDemandsToSupplies(List<Demand> demandList) {
		//TODO: ?maybe get the demand list in the method body instead of passing as an argument.
		Collections.sort(demandList);
		for (Demand d : demandList) {
			d.matchToSupply();
		}
	}
}
