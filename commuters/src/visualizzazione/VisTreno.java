package visualizzazione;

import modello.*;
import modello.azioni.Treno;

public class VisTreno extends HelpShower {
	static final int TRAIN_THICKNESS = 20;
	
	Pendolari applet;
	Treno treno;

	public VisTreno(Pendolari a, Treno t) {
		this.applet = a;
		this.treno = t;
	}
	
	public void draw(double ora) {
		applet.strokeWeight(TRAIN_THICKNESS);
		applet.stroke(treno.active ? 128 : 0, 128);
		applet.line(applet.p(treno.from.x), applet.p(treno.from.y),
					applet.p(treno.to.x), 	applet.p(treno.to.y)	);
		
		if (treno.active) {
			applet.stroke(64, 255);
			double percentuale, viaggio;
			for (short partenza : treno.partenze) {
				viaggio = (ora- partenza);
				viaggio = viaggio < 0 ? viaggio+Ambiente.STEPS_IN_ONE_DAY : viaggio;
				percentuale = (viaggio) /
								(treno.durata((short) ora, applet.giorno));
				if (percentuale >= -0.2 && percentuale <= 1.2) {
					if (percentuale > 1) percentuale = 1;
					if (percentuale < 0) percentuale = 0;
					
					float x = (float) (applet.p(treno.from.x) * (1-percentuale) + 
								applet.p(treno.to.x) * percentuale);
					float y = (float) (applet.p(treno.from.y) * (1-percentuale) + 
					applet.p(treno.to.y) * percentuale);
					applet.line(x - (TRAIN_THICKNESS/2), y,
							x + (TRAIN_THICKNESS/2), 	y	);
				}
			}
		}
	}

	public void click() {
		treno.active = !treno.active;
		for (Agente a : applet.sistema.agenti)
			a.epsilon = Agente.ORIGINAL_EPSILON;
	}

	public boolean isClicked(int x, int y) {
		
		
		double d1 = distSquare(applet.p(treno.from.x), applet.p(treno.from.y), x, y);
		double d2 = distSquare(applet.p(treno.to.x), applet.p(treno.to.y), x, y);
		double dTreno = distSquare(applet.p(treno.from.x), applet.p(treno.from.y), applet.p(treno.to.x), applet.p(treno.to.y) );
		
		if (d1 > dTreno || d2 > dTreno)
			return false;
		
		double distMouseLinea = Math.sqrt(d1 - ( Math.pow(d1-d2+dTreno,2) / (4*dTreno) ) );
		
		return (distMouseLinea <= (TRAIN_THICKNESS/2));
		
	}
	
	private static double distSquare(double x1, double y1, double x2, double y2) {
		return  Math.pow(x1-x2,2) + Math.pow(y1-y2,2);
	}

	
	public void ifMouseOverShowHelp() {
		ifMouseOverShowHelp(applet, treno.active ? "Stop this train" : "Activate this train");
	}
	
}
