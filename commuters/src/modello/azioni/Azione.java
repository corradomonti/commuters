package modello.azioni;

import modello.Posto;
import modello.Stato;

public abstract class Azione implements Comparable<Azione> {
	public Posto from;
	public Posto to;
	
	public Azione(Posto from, Posto to) {
		this.from = from;
		this.to = to;
	}
	
	public boolean isActive() { return true; }
	
	public abstract short oraDiArrivo(short ora, int giorno);
	
	public abstract int getCode();
	public int hashCode() {
		int fromTo = ( ((from.hashCode()+to.hashCode())*(from.hashCode()+to.hashCode()+1))/2 ) + to.hashCode();
		return ( ((fromTo+getCode())*(fromTo+getCode()+1))/2 ) + getCode(); //accoppiamento di cantor
	}
	
	public int compareTo(Azione a) {
		int c;
		if ( (c = getCode() - a.getCode()) != 0) return c;
		else return hashCode() - a.hashCode();
		
	}
	
	public boolean equals(Azione a) {
		return ( a.from.equals(from) && a.to.equals(to) && a.getName().equals(getName()) ) ;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Azione)
			return equals((Azione) o);
		else
			return false;
	}
	
	public boolean canStartFrom(Stato partenza) {
		return from.equals(partenza.posto);
	}
	
	public Stato arrivo(Stato da, int giorno) {
		return new Stato(to, oraDiArrivo(da.ora, giorno));
	}
	
	public abstract String getName();
	public String toString() {
		return getName() + " from " + from + " to " + to; 
	}
	
}
