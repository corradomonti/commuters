package modello.azioni;

import modello.Posto;

public class Fermo extends Azione {

	public Fermo(Posto p) {
		super(p, p);
	}

	@Override
	public short oraDiArrivo(short ora, int giorno) {
		return (short) (ora + 1);
	}
	
	public String getName() {
		return "still";
	}
	
	public String toString() {
		return "still at " + super.from.toString();
	}
	
	public int getCode() {
		return 0;
	}

}
