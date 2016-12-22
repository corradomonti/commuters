package modello;

import java.io.*;

import modello.azioni.Azione;
import modello.policydata.CoppiaStatoAzione;
import modello.policydata.EligibilityTrace;
import modello.policydata.ValueFunctionQ;

public class Agente {
	public final boolean WRITE_DATA_FILE = false;
	public FileWriter outputRewardFile, outputUpdateFile;
	public boolean verboseOnControl = false;
	public boolean verboseOnPrediction = false;
	
	Ambiente ambiente;
	public final Posto casa;
	public final Posto lavoro;
	public final short inizioLavoro = Ambiente.ora( 9,00 );
	public final short fineLavoro = Ambiente.ora( 18,00 );
	
	public Stato statoCorrente;
	public Azione azioneCorrente;
	public Stato statoSuccessivo;
	public Azione azioneSuccessiva;
	public double reward = 0; //ovvero il reward relativo all'azione tra statoCorrente e statoSuccessivo
	
	public double totalReward = 0;
	public double rewardLastDay = 0, currentDayReward = 0;
	public double maxUpdate = 1000;
	
	public int giornoStatoCorrente;
	public int giornoStatoSuccessivo;
	
	public ValueFunctionQ q = new ValueFunctionQ();
	public EligibilityTrace e = new EligibilityTrace();
	
	public static final double
	ALFA = 0.75,
	ORIGINAL_EPSILON = 0.75,
	EPSILON_DECAY = Math.pow(0.01/ORIGINAL_EPSILON,1.0 / 12000.0 ),
	SOGLIA_DI_CONVERGENZA = 0, //Math.pow(10, -15),

	//N_STEPS = 1 * (double) Ambiente.STEPS_IN_ONE_DAY,
	GAMMA = (Math.pow(Math.E, Math.log(0.01)/ Ambiente.STEPS_IN_ONE_DAY ) ),
	LAMBDA = 1;//MONTECARLO
	
	public double epsilon = ORIGINAL_EPSILON;
	public boolean greedyness = false, lastActionWasGreedy;
	private double delta;
	
	public Agente(Agente a) {
		this(a.casa, a.lavoro, a.ambiente);
	}
	
	public Agente(Posto casa, Posto lavoro, Ambiente ambiente) {
		this.ambiente = ambiente;
		this.casa = casa;
		this.lavoro = lavoro;
		setTempoSpazioToZero();
		
		if (WRITE_DATA_FILE)
			try {
				File dir = new File("statistical_data" + File.separator + ambiente.toString());
				dir.mkdirs();
				File f = new File(dir, "Reward-per-day." + this.toString() + ".txt");
				f.createNewFile();
				System.out.println(f.getAbsolutePath());
				outputRewardFile = new FileWriter(f);
				
				f = new File(dir, "Max-Update." + this.toString() + ".txt");
				f.createNewFile();
				System.out.println(f.getAbsolutePath());
				outputUpdateFile = new FileWriter(f);
			} catch (IOException e) {e.printStackTrace();}
	}
	
	public void setTempoSpazioToZero() {
		giornoStatoCorrente = 0;
		giornoStatoSuccessivo = 0;
		
		
		statoCorrente = new Stato(casa, 0);
		azioneCorrente = policy(statoCorrente, greedyness, true);
		statoSuccessivo = ambiente.arrivo(statoCorrente, azioneCorrente, giornoStatoCorrente);
	}
	
	public Agente(int x1, int y1, Ambiente ambiente) {
		this(new Posto(x1, y1), Ambiente.getInstance().STANDARD_WORKING_PLACE, ambiente);
	}
	
	public Agente(int x1, int y1, int x2, int y2, Ambiente ambiente) {
		this(new Posto(x1, y1), new Posto(x2, y2), ambiente);
	}
	
	public void next() {		
		azioneSuccessiva = policy(statoSuccessivo, greedyness, true);
		updatePolicy();
		
		statoCorrente = statoSuccessivo;
		azioneCorrente = azioneSuccessiva;
		statoSuccessivo = ambiente.arrivo(statoCorrente, azioneCorrente, giornoStatoCorrente);
		
		//update time
		giornoStatoCorrente = giornoStatoSuccessivo;
		if (statoSuccessivo.ora < statoCorrente.ora) {
			giornoStatoSuccessivo++;
			
			//EPSILON DECAY
			epsilon*=EPSILON_DECAY;
			
			//update reward stats
			rewardLastDay = currentDayReward;
			currentDayReward = 0;
			if (WRITE_DATA_FILE)
				try {
				outputRewardFile.write(Double.toString(rewardLastDay));
				outputRewardFile.write(new char[] {'\r', '\n'});
				} catch (IOException e) {e.printStackTrace();}
		}
		
		reward = getReward(statoCorrente, statoSuccessivo, azioneCorrente);
		totalReward+= reward;
		currentDayReward+= reward;
	}
	
	public Azione policy(Stato stato, boolean greedy, boolean actualAction) {
		Azione[] scelte = ambiente.getActions(this, stato);
		
		if (actualAction && verboseOnControl) {
			ambiente.out.flush();
			ambiente.out.println("_____POLICY: EVERY ACTION_______");
			ambiente.out.println("Current state: " + stato);
			for (int i = 0; i < scelte.length; i++)
				ambiente.out.println(i + ") " + scelte[i] + ": value " + q.get(stato, scelte[i]));
		}
		
		Azione[] decise;
		
		if (actualAction)
			lastActionWasGreedy = true;
		
		if (greedy || Math.random() > epsilon)
			decise = q.azioniMigliori(stato, scelte);
		else {
			decise = scelte;
			if (actualAction) {
				lastActionWasGreedy = false;
				if (verboseOnControl)
					ambiente.out.println("# EXPLORATIVE ACTION #");
			}
		}
		
		int x = (int) (Math.random() * decise.length);
		
		if (actualAction && verboseOnControl) {
			ambiente.out.println("_____POLICY: OPTIMAL ACTIONS_____");
			for (int i = 0; i < decise.length; i++)
				ambiente.out.println(i + ") " + decise[i] + ": value " + q.get(stato, decise[i]));
			ambiente.out.println("I chose number " + x);
		}
		
		return decise[x];
	}
	
	public void updatePolicy() {
		CoppiaStatoAzione sa1 = new CoppiaStatoAzione(statoCorrente, azioneCorrente);
		CoppiaStatoAzione sa2;
		if (lastActionWasGreedy)
			sa2 = new CoppiaStatoAzione(statoSuccessivo, azioneSuccessiva);
		else
			sa2 = new CoppiaStatoAzione(statoSuccessivo, policy(statoSuccessivo, true, false));
		
		delta = reward + GAMMA * q.get(sa2) - q.get(sa1);
		
		if (verboseOnPrediction) {
			ambiente.out.flush();
			ambiente.out.println("____UPDATE POLICY____");
			ambiente.out.println("s,a = " + sa1);
			ambiente.out.println("Delta=" + delta + ", reward = " + reward);
			ambiente.out.println("original e(s,a) = " + e.get(sa1));
			ambiente.out.println("original q(s,a) = " + q.get(sa1));
		}
		
		
		e.addTo(sa1, 1);
		maxUpdate = q.updateWith(ALFA*delta, e,
				lastActionWasGreedy ? GAMMA*LAMBDA : 0
						) / ALFA;
		
		if (verboseOnPrediction) {
			ambiente.out.println("new e(s,a) = " + e.get(sa1));
			ambiente.out.println("new q(s,a) = " + q.get(sa1));
		}
		
		if (!lastActionWasGreedy && maxUpdate < SOGLIA_DI_CONVERGENZA) {
			greedyness = true;
		}
		
		if (WRITE_DATA_FILE)
			if (!lastActionWasGreedy)
				try {
					outputUpdateFile.write(Double.toString(maxUpdate == 0 ? -999 : Math.log10(this.maxUpdate)));
					outputUpdateFile.write(new char[] {'\r', '\n'});
					} catch (IOException e) {e.printStackTrace();}
	}
	
	public double getReward(Stato stato1, Stato stato2, Azione azione) {
		double valoreTempo, bonusPuntualeAlLavoro = 0, bonusMezzoDiTrasporto = 0;
		
		//calcolo il tempo dell'azione
		int tempo = stato2.ora - stato1.ora;
		if (tempo <= 0)
			tempo+=Ambiente.STEPS_IN_ONE_DAY;
		
		//determino se era tempo lavorativo
		boolean tempoDiLavoro = (isTempoLavorativo(stato1) && isTempoLavorativo(stato2));
		
		//determino se ero a casa o al lavoro
		boolean aCasa = false, alLavoro = false;
		
		if (stato2.posto.equals(stato1.posto)) {
			if (stato1.posto.equals(casa))
				aCasa = true;
			else
				if (stato1.posto.equals(lavoro))
					alLavoro = true;
		} else {
			//assegno un bonus se arrivo al lavoro puntuale
			if (stato2.posto.equals(lavoro))
				if (stato2.ora == inizioLavoro)
					bonusPuntualeAlLavoro = 6;
		}
		
		//assegno i valori alle varie combinazioni di dove sono e dove dovrei essere
		valoreTempo = 
			(Ambiente.isNotte(stato1.ora) || Ambiente.isNotte(stato2.ora)) ?
						-1.5 : //sono in giro di notte
						-1 ; //sono in giro di giorno
		if (tempoDiLavoro) {
			if (alLavoro)
				valoreTempo = 3; //al lavoro
		} else {
			if (aCasa)
					valoreTempo = 1;	//a casa quando posso
		}
		
		//bonus per i mezzi di trasporto
//		if (azione instanceof Treno)
//			bonusMezzoDiTrasporto = 1;
//		else
//			bonusMezzoDiTrasporto = 0;
		
		double reward = valoreTempo*tempo + bonusPuntualeAlLavoro + bonusMezzoDiTrasporto;
		return reward;
	}
	
	public boolean isTempoLavorativo(Stato s) {
		return (s.ora > inizioLavoro && s.ora < fineLavoro);
	}
	
	public int tempoTotaleStatoSuccessivo() {
		return (Ambiente.STEPS_IN_ONE_DAY * giornoStatoSuccessivo) + this.statoSuccessivo.ora;
	}
	public int tempoTotaleStatoCorrente() {
		return (Ambiente.STEPS_IN_ONE_DAY * giornoStatoCorrente) + this.statoCorrente.ora;
	}

}
