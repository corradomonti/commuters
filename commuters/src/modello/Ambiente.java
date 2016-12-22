package modello;
import java.util.*;

import modello.azioni.Azione;
import modello.azioni.Cammina;
import modello.azioni.Fermo;
import modello.azioni.Treno;


public class Ambiente {
	
	public static final short TIMESTEP = 30;
	public static final short STEPS_IN_ONE_DAY = (24 * 60) / TIMESTEP;
	
	public java.io.PrintStream out = System.out;
	
	//definizione ambiente
	public Posto STANDARD_WORKING_PLACE = new Posto(60,60);
	
	public Posto attualeCasa = null;
	
	public Posto[] stazioni = {
			new Posto(10, 10), //0
			new Posto(10, 60), //1
			new Posto(60, 60), //2
	};
	
	public Azione[] mezzi = {
			new Treno(stazioni[0],
					stazioni[1],
					ogni(30), 
					30),
			new Treno(stazioni[1],
					stazioni[2],
					ogni(30),
					30),
			new Treno(stazioni[2],
					stazioni[0],
					ogni(30),
					60)
	};
	
	public Agente[] agenti;
	
	
	//funzioni di gestione degli orari
	public static short ora(int h, int m) {
		return (short) (h * (60/TIMESTEP) + (m/TIMESTEP));
	}
	public static String printOra(short t) {
		int h = t / (60/TIMESTEP);
		int m = (t - h*(60/TIMESTEP)) * TIMESTEP;
		return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m;
	}
	
	public static short[] ogni(int m) {
		short[] r = new short[24*60/m];
		for (int i = 0; i < r.length; i++)
			r[i] = (short) (i*m/TIMESTEP);
		return r;
	}
	
	//singleton
	private static Ambiente instance = null;
	public static Ambiente getInstance() {
		if (instance == null) instance = new Ambiente();
		return instance;
	}
	
	//funzioni essenziali dell'ambiente
	public Azione[] getActions(Agente agente, Stato s) {
		SortedSet<Azione> r = new TreeSet<Azione>();
		
		r.add(new Fermo(s.posto));
		
		for (Azione m : mezzi)
			if (m.isActive() && m.canStartFrom(s))
				r.add(m);
		
		try {r.add(new Cammina(s.posto, agente.casa)); } catch (Exception postiCoincidenti) {}
		try {r.add(new Cammina(s.posto, agente.lavoro)); } catch (Exception postiCoincidenti) {}
		for (Posto p : stazioni)
			try {r.add(new Cammina(s.posto, p)); } catch (Exception postiCoincidenti) {}
		
		Azione[] arrayDiAzioni = {};
		return r.toArray(arrayDiAzioni);
	}
	
	public Stato arrivo(Stato s, Azione a, int giorno) {
		return a.arrivo(s, giorno);
	}
	
	public void setAllAgentsToTheSameTime(visualizzazione.Pendolari app) {
		int[] newTime = setAllAgentsToTheSameTime();
		app.giorno = newTime[0];
		app.t = newTime[1];
	}
	
	public int[] setAllAgentsToTheSameTime() {
		//finds maximum of agents state 1 times
		
		int day = Integer.MIN_VALUE;
		for (Agente a : agenti)
			day = Math.max(day, a.giornoStatoCorrente);
		
		int hour = Integer.MIN_VALUE;
		for (Agente a : agenti)
			if (a.giornoStatoCorrente == day)
				hour = Math.max(hour, a.statoCorrente.ora);
		
		hour++;
		
		for (Agente a : agenti)
			while (a.giornoStatoSuccessivo < day
					|| (a.giornoStatoSuccessivo == day
							&& a.statoSuccessivo.ora < hour))
				a.next();
		
		int[] time = {day, hour};
		return time;
	}
	
	//utility
	public String getStationName(Posto p) throws Exception {
		for (int i = 0; i < stazioni.length; i++)
			if (p.equals(stazioni[i]))
				return "Station " + (i+1);
		
		throw new Exception ("Not a station");
	}
	public static boolean isNotte(double t) {
		return t <= ora(7, 00) || t >= ora(21, 00);
	}
	
	
}
