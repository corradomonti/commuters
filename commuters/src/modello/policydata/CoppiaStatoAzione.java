package modello.policydata;

import modello.*;
import modello.azioni.Azione;

public class CoppiaStatoAzione {
	public Stato s;
	public Azione a;
	
	public CoppiaStatoAzione(Stato s, Azione a) {
		this.s = s;
		this.a = a;
	}
	
	public boolean equals(CoppiaStatoAzione sa) {
		return sa.s.equals(s) && sa.a.equals(a);
	}
	
	public boolean equals(Object o) {
		if (o instanceof CoppiaStatoAzione)
			return equals((CoppiaStatoAzione) o);
		else
			return false;
	}
	
	public String toString() {
		return s.toString() + "->" + a.toString();
	}
	
	public int hashCode() {
		int x = s.hashCode();
		int y = a.hashCode();
		return ( ((x+y)*(x+y+1))/2 ) + y; //accoppiamento di cantor
	}


}
