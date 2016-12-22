package visualizzazione.testo;

import processing.core.PApplet;

public class TextLinesArea extends ProcessingTextStream {
	public static final float RIGA = 16;
	final static String font = "DejaVuSansCondensed-14.vlw";
	int i = -1, max;
	PApplet app;
	String[] msg;
	float x1, y1, x2, y2;
	
	public TextLinesArea(PApplet app, float x1, float y1, float x2, float y2) {
		this.app = app;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		max = (int) (y2 / RIGA);
		msg = new String[max];
		
		app.textFont(app.loadFont(font));
	}
	
	public void println(String s) {
		i++;
		if (i >= max)
			i = 0;
		msg[i] = s;
	}
	
	public void draw() {
		if (i > -1) {
			app.rectMode(PApplet.CORNER);
			String tot = "";
			int j = i;
			do {
				tot+= (msg[j] + "\r\n");
				j++;
				if (j >= max)
					j = 0;
			} while (j != i);
			
			app.fill(0);
			app.text(tot, x1, y1, x2, y2);
			app.rectMode(PApplet.CENTER);
		}
	}
}
