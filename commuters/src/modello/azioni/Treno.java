package modello.azioni;
import java.util.Arrays;

import modello.Ambiente;
import modello.Posto;
import modello.Stato;

public class Treno extends Azione {
	public short[] partenze;
	public int durataMedia;
	public boolean active;
	
	public Treno(Posto da, Posto a, short[] partenze, int durata) {
		super(da, a);
		active = true;
		this.partenze = partenze;
		this.durataMedia = durata/Ambiente.TIMESTEP;
		
		Arrays.sort(partenze);
	}
	
	public boolean isActive() { return active; }

	public short oraDiArrivo(short ora, int giorno) {
		return (short) (ora + durata(ora, giorno));
	}
	
	public int durata(short ora, int giorno) {
		return durataMedia;
	}
	
	

	public String getName() {
		return "train";
	}
	
	
	public int getCode() {
		return 2;
	}
	

	@Override
	public boolean canStartFrom(Stato stato) {
		return (from.equals(stato.posto) && Arrays.binarySearch(partenze, stato.ora) >= 0);
	}
	
}
