package visualizzazione;

import java.text.DecimalFormat;
import java.util.ArrayList;

import modello.Agente;
import modello.Ambiente;
import modello.AmbienteBuilder;
import modello.Posto;
import modello.azioni.Azione;
import modello.azioni.Treno;
import processing.core.PApplet;
import processing.core.PImage;
import visualizzazione.testo.ProcessingTextStream;
import visualizzazione.testo.TextArea;

public class Pendolari extends PApplet {
	private static final long serialVersionUID = 1L;
	
	final static int xSize = 500, ySize = 500;
	final static int pixelPerUnitaDiSpazio = 7;
	
	final static float rate = 20;
	float dt = 1;
	
	public final static int NUMBER_OF_AGENTS = 24;
	
	final int[] AGENT_COLORS = {
			color(255, 	64,  64, 192),
			color(64,  255,  64, 192),
			color(64,   64, 255, 192),
			color(255,  64, 255, 192),
			color(255, 255,  64, 192),
			color(255, 200,  64, 192),
			color(190, 190, 190, 192)
	};
	
	Ambiente sistema;
	Agente agenteSelezionato;
	ArrayList<VisTreno> rappresTreni = new ArrayList<VisTreno>();
	ArrayList<VisAgente> rappresAgenti = new ArrayList<VisAgente>();
	ArrayList<Button> pulsanti = new ArrayList<Button>(),
					  pulsantiRealtime = new ArrayList<Button>();
	ProcessingTextStream textParametri, textInfoAzione, textInfoPolicy;
	
	public double t = 0;
	public int giorno = 0;
	
	boolean realtimeMode = true, activeTime = true;
	DecimalFormat formatter = new DecimalFormat("#.#######");
	Grafico g;
	
    public static void main(String args[]) {
      PApplet.main(new String[] { visualizzazione.Pendolari.class.getName() });
    }

	
	public void setup() {
		size(1145, 500);
		smooth();
		this.ellipseMode(CENTER);
		this.rectMode(CENTER);
		
		sistema = AmbienteBuilder.buildFrom("map.txt");
		
		for (Azione a : sistema.mezzi)
			if (a instanceof Treno)
				rappresTreni.add(new VisTreno(this, (Treno) a));

		creaAgenti(sistema, this, NUMBER_OF_AGENTS);
		
		agenteSelezionato = sistema.agenti[0];
		g = new Grafico(sistema, agenteSelezionato, this, xSize, 0);
		selectAgent(0);
		
		textParametri = new TextArea(this,
				xSize + Grafico.SEP,
				Grafico.getTotalYSize() + Grafico.SEP
				);
		
		textInfoAzione = new TextArea(this,
				xSize + Grafico.SEP,
				Grafico.getTotalYSize() + 150
				);
		
		textInfoPolicy = new TextArea(this,
				xSize + 300,
				Grafico.getTotalYSize() + Grafico.SEP);
		sistema.out = textInfoPolicy;
		
		creaPulsanti();
		
		frameRate(rate);
		setMode(true, false);
	}
	
	public static Agente[] creaAgenti(Ambiente sys, PApplet app, int quanti) {
		sys.agenti = new Agente[quanti];
		float noise = 12;
		
		for (int i = 0; i < sys.agenti.length; i++) {
			
			int type = i % (sys.stazioni.length);
			Posto p = sys.stazioni[type];
			
			sys.agenti[i] = new Agente(
			p.x + Math.max(-p.x, (int) (app.noise(i)*noise*2-noise)), 
			p.y + Math.max(-p.y,(int) (app.noise(0.2f,i)*noise*2-noise)),
			sys);
			if (app instanceof Pendolari)
				((Pendolari) app).rappresAgenti.add(
						new VisAgente(
								(Pendolari) app,
								sys.agenti[i],
								((Pendolari) app).AGENT_COLORS[type % ((Pendolari) app).AGENT_COLORS.length],
								i)
						);
		
		}
		
		return sys.agenti;
	}

	void setMode(boolean mode, boolean wannaGreedyStates) {
		this.realtimeMode = mode;
		
		if (realtimeMode) {
			this.cursor(PApplet.WAIT);
			//rappresentazione policy
			g.active = false;
			
			for (Agente a : sistema.agenti) {
				a.greedyness = true;
				//faccio in modo che tornino ad essere greedy
				if (wannaGreedyStates)
					for (int i = 0; i < Ambiente.STEPS_IN_ONE_DAY+2; i++)
						a.next();
			}
			
			sistema.setAllAgentsToTheSameTime(this);
			
			agenteSelezionato.verboseOnControl = true;
			activeTime = true;
			this.cursor(PApplet.ARROW);
			
		} else {
			//grafico
			g.active = true;
			
			for (Agente a : sistema.agenti)
				a.greedyness = false;
			
			agenteSelezionato.verboseOnControl = false;
			agenteSelezionato.verboseOnPrediction = false;
			
			for (VisAgente a : rappresAgenti)
				a.undoHighlight();
		}
	}
	
	public void draw() {
		background(140);
		printParametri();
		
		//background
		noStroke();
		fill(0xFF006600);
		rect(xSize/2, ySize/2, xSize, ySize);
		
		g.draw();
		
		if (realtimeMode) {
			for (HelpShower obj : rappresTreni) {
				obj.draw(t);
				obj.ifMouseOverShowHelp();
			}
			
			for (HelpShower obj : rappresAgenti) {
				obj.draw(t);
				obj.ifMouseOverShowHelp();
			}
			
			if (Ambiente.isNotte(t)) {
				noStroke();
				fill(0, 64);
				rect(xSize/2, ySize/2, xSize, ySize);
			}
			
			for (Agente agente : sistema.agenti)
				if (agente.tempoTotaleStatoSuccessivo() <= this.tempoTotale())
					agente.next();
			
			if (activeTime) {
				t+=(dt/Ambiente.TIMESTEP);
				while (t >= Ambiente.STEPS_IN_ONE_DAY) {
					t-=Ambiente.STEPS_IN_ONE_DAY;
					giorno++;
				}
			}
			
			textInfoAzione.draw();
			textInfoPolicy.draw();
			printInfo(agenteSelezionato);
			
			for (Button p : pulsantiRealtime) {
				p.draw(t);
				p.ifMouseOverShowHelp();
			}
		}
		
		textParametri.draw();
		
		for (Button p : pulsanti) {
			p.draw(t);
			p.ifMouseOverShowHelp();
		}
		
		g.drawLegendaIfRequested();
	}
	
	private void printParametri() {
		textParametri.flush();
		textParametri.println(agenteSelezionato.giornoStatoSuccessivo + " days " +
				Ambiente.printOra((short) t ) );
		
		if (agenteSelezionato.maxUpdate == 0)
			textParametri.println("Max update = NULL");
		else
			textParametri.println("Max update = 10^(" + ((int) (Math.log10(agenteSelezionato.maxUpdate)*100)) / 100.0 +")");
		textParametri.println("Reward/day avg: " + (int) (agenteSelezionato.totalReward / Math.max(agenteSelezionato.giornoStatoSuccessivo, 1)));
		textParametri.println("Reward/day last day: " + (int) agenteSelezionato.rewardLastDay);
		textParametri.println("Greedy policy = " + agenteSelezionato.greedyness);
		textParametri.println("epsilon=" + formatter.format(agenteSelezionato.epsilon));
	}
	

	private void printInfo(Agente agente) {
		textInfoAzione.flush();
		textInfoAzione.println("state1: " + agente.statoCorrente);
		textInfoAzione.println("action: " + agente.azioneCorrente);
		textInfoAzione.println("Reward " + agente.reward);
		textInfoAzione.println("state2: " + agente.statoSuccessivo);
	}

	public float p(float originalP) {
		return originalP * pixelPerUnitaDiSpazio;
	}
	
	public double tempoTotale() {
		return giorno * Ambiente.STEPS_IN_ONE_DAY +  t;
	}
	
	public void mouseClicked() {
		for (Button p : pulsanti)
			if (p.isClicked(mouseX, mouseY)) {
				p.click();
				return;
			}
		
		for (Button p : pulsantiRealtime)
			if (p.isClicked(mouseX, mouseY)) {
				p.click();
				return;
			}
		
		for (Clickable o : rappresAgenti)
			if (o.isClicked(this.mouseX, this.mouseY)) {
				o.click();
				return;
			}
		
		for (Clickable o : rappresTreni)
			if (o.isClicked(this.mouseX, this.mouseY))
				o.click();

	}
	
	private void creaPulsanti() {
		int x = 420;
		int y = 10, dx = 50;
		pulsanti.add(
				new Button( this, x, y-8, 48, 48)
					 {
						public void click() {
							setMode(!realtimeMode, true);
						}
						
						PImage iconOn = loadImage("lamp.png"),
							iconOff = loadImage("camera.png");
						
						public PImage getIcon() {
							return realtimeMode ? iconOn : iconOff;
						}
						
						public String getHelp() {
							return realtimeMode ? "Start learning phase." :
								"Observe present behaviour.";
						}
					 
					 }
			);
		
		pulsantiRealtime.add(
				new Button( this, x, 450, 42, 42, "Highlight best agent.", loadImage("find.png"))
					 {
						public void click() {  findBestAgent(); }
					 }
			);
		
		pulsantiRealtime.add(
				new Button( this, x-=(dx+16), y, 32, 32)
					 {
						public void click() { activeTime = !activeTime; }
						
						PImage playIcon = loadImage("play.png"),
							pauseIcon = loadImage("pause.png");
						
						public PImage getIcon() {
							return activeTime ? pauseIcon : playIcon;
						}
						public String getHelp() {
							return activeTime ? "Pause" : "Play";
					   }
					 }
			);
		
		pulsantiRealtime.add(
				new Button( this, x-=dx, y, 32, 32, "Faster", loadImage("plus.png"))
					 { public void click() { dt*=2; if (dt > 32) dt = 32; } }
			);
		
		pulsantiRealtime.add(
				new Button( this, x-=dx, y, 32, 32, "Slower", loadImage("meno.png"))
					 { public void click() { dt/=2; } }
			);
	
		
		pulsanti.add(
				new Button( this, g.kx+Grafico.X_SIZE-Grafico.SEP, Grafico.SEP*2, 32, 32, loadImage("question.png"))
					 { public void click() { g.legenda1 = !g.legenda1; }
						
					   public String getHelp() {
							return g.legenda1 ? "Hide legend" : "Show legend";
					   }
					 }
			);
		
		pulsanti.add(
				new Button( this, g.kx+2*Grafico.X_SIZE, Grafico.SEP*2, 32, 32, loadImage("question.png"))
					 { public void click() { g.legenda2 = !g.legenda2; }
						
						public String getHelp() {
							return g.legenda2 ? "Hide legend" : "Show legend";
						}
					 }
			);
		
		pulsanti.add(
				new Button( this, 654, 305, 15, 15, "Greedy or not greedy", loadImage("arrow.png"))
					 { public void click() {
						 agenteSelezionato.greedyness = !agenteSelezionato.greedyness;
						}
					 }
			);
		
		pulsanti.add(
				new Button( this, 642, 321, 15, 15, "Reset", loadImage("undo.png"))
					 { public void click() {
						 agenteSelezionato.epsilon = Agente.ORIGINAL_EPSILON;
						}
					 }
			);
			
	}
	
	public void keyPressed() {
		g.keyPressed();
	}

	public void selectAgent(int i) {
		agenteSelezionato.verboseOnControl = false;
		agenteSelezionato.verboseOnPrediction = false;
		sistema.out.flush();
		
		agenteSelezionato = sistema.agenti[i];
		
		agenteSelezionato.verboseOnControl = true;
		
		g.agenteSelezionato = this.agenteSelezionato;
		sistema.attualeCasa = this.agenteSelezionato.casa;
		g.setup();
		g.draw();
	
	}
	
	public void findBestAgent() {
		double maxReward = Double.NEGATIVE_INFINITY;
		for (Agente a : sistema.agenti)
			maxReward = Math.max(maxReward, a.rewardLastDay);
		
		for (VisAgente a : rappresAgenti)
			if (a.agente.rewardLastDay == maxReward)
				a.highlight();
			else
				a.undoHighlight();
	}

}
