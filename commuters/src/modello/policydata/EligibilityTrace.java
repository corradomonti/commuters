package modello.policydata;

import java.util.HashMap;
import java.util.Map;

public class EligibilityTrace {
	Map<CoppiaStatoAzione,Double> map;
	
	public EligibilityTrace() {
		map = new HashMap<CoppiaStatoAzione,Double>();
	}
	
	public void addTo(CoppiaStatoAzione sa, double q) {
		map.put(sa, get(sa) + q);
	}
	
	
	public void setAllToZero() {
		map.clear();
	}
	
	public double get(CoppiaStatoAzione sa) {
		Double e = map.get(sa);
		return (e == null ? 0 : e.doubleValue());
	}
	
	public void printAll() {
		for (CoppiaStatoAzione sa : map.keySet()) {
			System.out.println(sa.toString() + " -> " + map.get(sa));
		}
	}
}
