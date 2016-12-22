package visualizzazione;


import modello.*;
import modello.azioni.Azione;
import modello.policydata.CoppiaStatoAzione;
import processing.core.PApplet;

public class Grafico {
	private PApplet app;
	
	static final int X_SIZE = 300, Y_SIZE = 200, SEP = 15;
	final int STEP_PER_ORA = 60 / Ambiente.TIMESTEP,
		CYCLES_PER_FRAME = 200,
		UPDATE_AGENT_TIME_EVERY = (500*Ambiente.STEPS_IN_ONE_DAY) /CYCLES_PER_FRAME;
	final float X_PER_HOUR = X_SIZE / 24.0f;
	int generalYBase = -80,
		kx = 0, ky = 0,
		updateTime = UPDATE_AGENT_TIME_EVERY;
	float pixel_per_unit = 4.0f;
	Ambiente sistema;
	Agente agenteSelezionato;
	
	Posto casa, lavoro;
	Azione[] a1 = new Azione[4];
	Azione[] a2 = new Azione[5];
	
	boolean active = false;
	public boolean legenda1 = false, legenda2 = false;
	
	int[] col1 = {0xFF008800, 0x99AA0000, 0x990000AA, 0x70333333, 0xFFFFFF00, 0x99FF00FF, 0x9900FFFF, 0xFFDD7700};
	int[] col2 = {0xFF008800, 0x99AA0000, 0x990000AA, 0x70333333, 0xFFFFFF00, 0x99FF00FF, 0x9900FFFF, 0xFFDD7700};
	
	public Grafico(Ambiente sistema, Agente agente, PApplet app, int kx, int ky) {
		this.app = app;
		this.sistema = sistema;
		this.agenteSelezionato = agente;
		this.kx = kx;
		this.ky = ky;
	}
	
	public void setup() {
		casa = agenteSelezionato.casa;
		lavoro = agenteSelezionato.lavoro;
		
		try {
			a1 = sistema.getActions(agenteSelezionato,
					new Stato(casa, Ambiente.ora(8,00)));
			a2 = sistema.getActions(agenteSelezionato,
					new Stato(lavoro, Ambiente.ora(18,00)));
			
		} catch (Exception e) {throw new RuntimeException(e);}
		
	}
	
	public static int getTotalXSize() {
		return X_SIZE * 2 + SEP*3;
	}
	public static int getTotalYSize() {
		return Y_SIZE + SEP*2;
	}
	
	public void draw() {
		app.rectMode(PApplet.CORNER);
		app.noStroke();
		app.fill(140);
		app.rect(kx+0, ky+0, getTotalXSize(), getTotalYSize());
		
		app.stroke(0);
		app.strokeWeight(2);
		app.fill(230);
		
		app.rect(kx+SEP, ky+SEP, X_SIZE, Y_SIZE);
		app.rect(kx+X_SIZE + SEP*2, ky+SEP, X_SIZE, Y_SIZE);
		
		app.noStroke();
		
		app.fill(0, 32);
		
		app.rect(kx+SEP,
				ky+SEP,
				(agenteSelezionato.inizioLavoro/STEP_PER_ORA) * X_PER_HOUR,
				Y_SIZE);
		app.rect(kx+SEP + (agenteSelezionato.fineLavoro/STEP_PER_ORA) * X_PER_HOUR,
				ky+SEP,
				X_SIZE - (agenteSelezionato.fineLavoro/STEP_PER_ORA) * X_PER_HOUR,
				Y_SIZE);
		
		
		app.rect(kx+X_SIZE + SEP*2,
				ky+SEP,
				(agenteSelezionato.inizioLavoro/STEP_PER_ORA) * X_PER_HOUR,
				Y_SIZE);
		app.rect(kx+X_SIZE + SEP*2 + (agenteSelezionato.fineLavoro/STEP_PER_ORA) * X_PER_HOUR,
				ky+SEP,
				X_SIZE - (agenteSelezionato.fineLavoro/STEP_PER_ORA) * X_PER_HOUR,
				Y_SIZE);
		
		
		app.stroke(0);
		app.strokeWeight(1);
		
		app.line(kx+SEP, ky+generalYBase + SEP + Y_SIZE,
				kx+SEP + X_SIZE, ky+generalYBase + SEP + Y_SIZE);
		app.line(kx+SEP*2 + X_SIZE, ky+generalYBase + SEP + Y_SIZE,
				kx+SEP*2 + 2*X_SIZE, ky+generalYBase + SEP + Y_SIZE);
		
		for (int i = 0; i < 24; i++) {
			app.line(kx+SEP + X_PER_HOUR * i, ky+SEP,
					kx+SEP + X_PER_HOUR * i, ky+Y_SIZE + SEP);
			app.line(kx+X_SIZE + SEP*2 + X_PER_HOUR * i, ky+SEP,
					kx+X_SIZE + SEP*2 + X_PER_HOUR * i, ky+Y_SIZE + SEP);
		}
		
		app.strokeWeight(3);
		for (int i = 0; i < a1.length; i++)
			drawGrafico(casa, a1[i], kx+SEP, col1[i % col1.length]);
		for (int i = 0; i < a2.length; i++)
			drawGrafico(lavoro, a2[i], kx+X_SIZE + SEP*2, col2[i % col2.length]);
		
		if (active) {
			for (int i = 0; i < CYCLES_PER_FRAME; i++)
				for (Agente a : sistema.agenti)
					a.next();
			updateTime--;
			if (updateTime == 0) {
				updateTime = UPDATE_AGENT_TIME_EVERY;
				app.cursor(PApplet.WAIT);
				sistema.setAllAgentsToTheSameTime(((Pendolari) app));
				app.cursor(PApplet.ARROW);
			}
		}
		
		app.noStroke();
		app.fill(140);
		app.rect(kx+0, ky+SEP+Y_SIZE+2, getTotalXSize(), 1200);
		
		app.rectMode(PApplet.CENTER);
		
	}
	
	private void drawGrafico(Posto p, Azione a, int xBase, int color) {
		if (a != null) {
			app.stroke(color);
			float yBase = ky+generalYBase + SEP + Y_SIZE;
			CoppiaStatoAzione sa = new CoppiaStatoAzione(
					new Stato(p, 0),
					a
					);
			float newY, precY = pixel_per_unit * (float) agenteSelezionato.q.get(sa);
			
			for (int i = 1; i < 25; i++) {
				sa = new CoppiaStatoAzione(
						new Stato(p, STEP_PER_ORA * i),
						a
						);
				newY = pixel_per_unit* (float) agenteSelezionato.q.get(sa);
				app.line(xBase + X_PER_HOUR * (i-1), yBase-precY,
					 xBase + X_PER_HOUR * i,	 yBase-newY);
				
				precY = newY;
			}
		}
	}
	
	public void drawLegendaIfRequested() {
		if (legenda1)
			this.drawLegenda(1);
		if (legenda2)
			this.drawLegenda(2);
	}
	
	public void keyPressed() {
		switch (app.key) {
		case ' ':	active = !active;		break;
		case '+':	pixel_per_unit*=1.2;	break;
		case '-':	pixel_per_unit/=1.2;	break;
		case 'h':	break;
		}
			
		if (app.key == PApplet.CODED) {
			if (app.keyCode  == PApplet.DOWN)
				generalYBase+= Y_SIZE*0.2;
			else if (app.keyCode  == PApplet.UP)
				generalYBase-= Y_SIZE*0.2;
		}
	}
	
	private void drawLegenda(int quale) {
		Azione[] azioni = quale == 1 ? a1 : a2;
		int[] colori = quale == 1 ? col1 : col2;
		Posto p = quale == 1 ? casa : lavoro; 
		
		int y = ky+2*SEP+Y_SIZE;
		int x = quale == 1 ? kx+SEP : kx+X_SIZE + SEP*2;
		
		app.stroke(0);
		app.strokeWeight(2);
		app.fill(150);
		app.rectMode(PApplet.CORNER);
		app.rect(x, y, X_SIZE, (azioni.length+2)*SEP*2);
		app.noStroke();
		
		app.fill(0);
		y+=SEP*2;
		app.textAlign(PApplet.CENTER);
		app.text("Action value during the day at " + p,
				x+X_SIZE/2, y);
		app.textAlign(PApplet.LEFT);
		
		
		for (int i = 0; i < azioni.length; i++) {
			y+= SEP*2;
			app.fill(colori[i % colori.length]);
			app.ellipse(x + SEP*3, y, 12, 12);
			app.fill(0);
			app.text(azioni[i].toString().replace('_', ' '), x + 12 + SEP*4 + 10, y+5);
		}
		
		app.rectMode(PApplet.CENTER);
			
		
	}
}
