package modello;

public class Stato {
	public Posto posto;
	public short ora;
	
	public Stato(Posto p, short ora) {
		this.posto = p;
		this.ora = ora;
		while (this.ora >= Ambiente.STEPS_IN_ONE_DAY)
			this.ora-=Ambiente.STEPS_IN_ONE_DAY;
	}
	
	public Stato(Posto p, int ora) {
		this(p, (short) ora);
	}
	
	public String toString() {
		return posto.toString() + ", at " + Ambiente.printOra(ora);
	}
	
	public boolean equals(Stato s) {
		return (s.posto.equals(this.posto) && s.ora == this.ora);
	}
	public boolean equals(Object o) {
		if (o instanceof Stato)
			return equals((Stato) o);
		else
			return false;
	}
	 public int hashCode() {
		 return ( ((posto.hashCode()+ora)*(posto.hashCode()+ora+1))/2 ) + ora; //accoppiamento di cantor
	 }
}
