package visualizzazione.testo;

import processing.core.PApplet;

public class TextArea extends ProcessingTextStream {
	final static String font = "DejaVuSansCondensed-14.vlw";
	PApplet app;
	String msg;
	float x1, y1;
	
	public TextArea(PApplet app, float x1, float y1) {
		this.app = app;
		this.x1 = x1;
		this.y1 = y1;

		msg = "";
		
		app.textFont(app.loadFont(font));
	}
	
	public void println(String s) {
		msg+= (s + "\r\n");
	}
	
	public void draw() {
		app.fill(0);
		app.text(msg, x1, y1);
	}

	public void flush() {
		msg = "";
	}



}
