package modello.policydata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import modello.Stato;
import modello.azioni.Azione;

public class ValueFunctionQ {
	private Map<CoppiaStatoAzione,Double> map;
	public final double INITIAL_VALUE = 0;
	
	public ValueFunctionQ() {
		map = new HashMap<CoppiaStatoAzione,Double>();
	}
	
	public void addTo(CoppiaStatoAzione sa, double x) {
		map.put(sa, get(sa) + x);
	}
	
	public double updateWith(double x, EligibilityTrace e, double decay) {
		double delta, maxDelta = 0;
		
		for (CoppiaStatoAzione sa : e.map.keySet()) {
			delta = x*e.map.get(sa).doubleValue();
			maxDelta = Math.max(maxDelta, Math.abs(delta));
			map.put(sa, get(sa) + delta);
			if (decay != 0)
				e.map.put(sa, e.map.get(sa).doubleValue()*decay);
		}
		
		if (decay == 0)
			e.setAllToZero();
		
		return maxDelta;
	}
	
	public double get(CoppiaStatoAzione sa) {
		Double q = map.get(sa);
		return (q == null ? INITIAL_VALUE : q.doubleValue());
	}
	public double get(Stato s, Azione a) {
		return get(new CoppiaStatoAzione(s,a));
	}
	
	public Azione[] azioniMigliori(Stato s, Azione[] scelte) {
		//inizializzo q come zero per tutto
		double[] q = new double[scelte.length];
		for (int i = 0; i < q.length; i++)
			q[i] = INITIAL_VALUE;
		
		//trovo i q disponibili
		for (CoppiaStatoAzione sa : map.keySet())
			if (sa.s.equals(s))
				for (int i = 0; i < scelte.length; i++)
					if (scelte[i].equals(sa.a))
						q[i] = map.get(sa);
		
		//trovo il maggiore
		double maxQ = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < q.length; i++)
			if (q[i] > maxQ)
				maxQ = q[i];
		
		//trovo tutte le azioni ottimali
		ArrayList<Azione> r = new ArrayList<Azione>();
		for (int i = 0; i < scelte.length; i++)
			if (q[i] >= maxQ)
				r.add(scelte[i]);
		
		Azione[] arrayDiAzioni = {};
		return r.toArray(arrayDiAzioni);
	}
	
	public void printAll() {
		for (CoppiaStatoAzione sa : map.keySet()) {
			System.out.println(sa.toString() + " -> " + map.get(sa));
		}
	}
}
