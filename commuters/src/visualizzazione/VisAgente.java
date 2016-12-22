package visualizzazione;

import modello.Agente;

public class VisAgente extends HelpShower {
	Pendolari applet;
	Agente agente;
	int colore;
	int numero;
	
	static final int CIRCLE_SIZE=15;
	
	float x,y, dx, dy;
	float energyCircleSize = CIRCLE_SIZE+7, energyCircleAlpha = 255;
	float timeToHighlight = 0;
	
	
	public VisAgente(Pendolari a, Agente ag, int n) {
		this(a, ag, a.color(255, 64, 64, 192), n);
	}
	
	public VisAgente(Pendolari a, Agente ag, int col, int n) {
		this.applet = a;
		this.agente = ag;
		this.colore = col;
		this.numero = n;
	}
	
	public void draw(double t) {
		applet.strokeWeight(1);
		applet.stroke(0, 255);
		applet.noFill();
		applet.ellipse(applet.p(agente.casa.x), applet.p(agente.casa.y), CIRCLE_SIZE+7, CIRCLE_SIZE+7);
		applet.rect(applet.p(agente.lavoro.x), applet.p(agente.lavoro.y), CIRCLE_SIZE+7, CIRCLE_SIZE+7);
		
		if (agente.reward > 0) {
			energyCircleSize++;
			energyCircleAlpha*=0.85;
			if (energyCircleSize > 55) {
				energyCircleAlpha = 255;
				energyCircleSize = CIRCLE_SIZE+7;
			}
			
			if (agente.statoCorrente.posto.equals(agente.casa)) {
				applet.stroke(0xFFFFFF00, energyCircleAlpha);
				applet.ellipse(applet.p(agente.casa.x), applet.p(agente.casa.y), energyCircleSize, energyCircleSize);
			} else {
				applet.stroke(0xFF00FFFF, energyCircleAlpha);
				applet.rect(applet.p(agente.lavoro.x), applet.p(agente.lavoro.y), energyCircleSize, energyCircleSize);
			}
		}
		

			
	
		
		calcolaPosizione();
		dx = (applet.noise((float) applet.t, this.hashCode()/100.0f)*NOISE*2-NOISE);
		dy = (applet.noise(this.hashCode()/100.0f,(float) applet.t)*NOISE*2-NOISE);
		
		if (timeToHighlight > 0) {
			timeToHighlight--;
			if (applet.frameCount % 14 < 7) {
				float alpha = 255.0f * (timeToHighlight / INITIAL_HIGHLIGHT);
				applet.noFill();
				applet.stroke(255,255, 100, alpha);
				applet.strokeWeight(4.8f);
				applet.ellipse(x+dx, y+dy, CIRCLE_SIZE+5, CIRCLE_SIZE+5);
			}
		}
		
		if (applet.agenteSelezionato.equals(this.agente)) {
			applet.stroke(255);
			applet.strokeWeight(2);
		} else
			applet.noStroke();
		applet.fill(colore);
		applet.ellipse(x+dx, y+dy, CIRCLE_SIZE, CIRCLE_SIZE);
	}

	final static float NOISE = 12.0f;
	private final float INITIAL_HIGHLIGHT = 50 * 30;
	
	private void calcolaPosizione() {
		float percentuale = ((float) applet.tempoTotale() - agente.tempoTotaleStatoCorrente())
								/
							((float) agente.tempoTotaleStatoSuccessivo() - (float) agente.tempoTotaleStatoCorrente());
		
		percentuale = Math.max(0, Math.min(1, percentuale));
		
		x = (applet.p(agente.statoCorrente.posto.x) * (1-percentuale) + 
					 applet.p(agente.statoSuccessivo.posto.x) * percentuale);
		y = (applet.p(agente.statoCorrente.posto.y) * (1-percentuale) + 
				 	applet.p(agente.statoSuccessivo.posto.y) * percentuale);
	}

	public boolean isClicked(int mouseX, int mouseY) {
		return Math.sqrt((Math.pow(mouseX-(x+dx),2) + Math.pow(mouseY-(y+dy),2))) <= CIRCLE_SIZE/2;
	}
	
	public void click() {
		applet.selectAgent(this.numero);
	}

	public void ifMouseOverShowHelp() {
		ifMouseOverShowHelp(applet,
				(timeToHighlight > 0 ? "BEST AGENT - " : "") +
						(agente == applet.agenteSelezionato ?
								"selected agent" :
								"Select this agent")
						   );
	}

	public void highlight() {
		timeToHighlight = INITIAL_HIGHLIGHT;
	}
	
	public void undoHighlight() {
		timeToHighlight = 0;
	}

}
