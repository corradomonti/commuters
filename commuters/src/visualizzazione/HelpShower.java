package visualizzazione;

import processing.core.PApplet;

public abstract class HelpShower implements Clickable {
	abstract public boolean isClicked(int x, int y);
	
	abstract public void click();
	
	abstract public void ifMouseOverShowHelp();
	
	public void ifMouseOverShowHelp(PApplet app, String msg) {
		if (msg.length() > 0 && isClicked(app.mouseX, app.mouseY)) {
			app.noStroke();
			app.fill(255, 200);
			app.rectMode(PApplet.CORNER);
			app.rect(app.mouseX, app.mouseY, msg.length()*7+18, 20);
			app.rectMode(PApplet.CENTER);
			app.fill(0);
			app.text(msg, app.mouseX+10, app.mouseY+18);
		}
	}
}
