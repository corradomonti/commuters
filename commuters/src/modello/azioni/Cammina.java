package modello.azioni;

import modello.Ambiente;
import modello.Posto;

public class Cammina extends Azione {
	static final double MINUTI_PER_UNITA_DI_SPAZIO = 10;
	
	
	private static final double VELOCITA = Ambiente.TIMESTEP / MINUTI_PER_UNITA_DI_SPAZIO;
	
	public Cammina(Posto da, Posto a) throws Exception {
		super(da, a);
		if (da.equals(a))
			throw new Exception("You can't walk staying still.");
		if (durata() >= Ambiente.STEPS_IN_ONE_DAY)
			throw new Exception("You can't walk more than a day.");
	}
	
	public int getCode() {
		return 1;
	}

	public short oraDiArrivo(short ora, int giorno) {
		return (short) (ora + durata());
	}
	
	public short durata() {
		return (short) (this.from.dist(this.to) / VELOCITA);
	}
	
	public String getName() {
		return "walk";
	}

}
