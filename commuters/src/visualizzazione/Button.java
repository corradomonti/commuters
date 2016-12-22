package visualizzazione;

import processing.core.PApplet;
import processing.core.PImage;

public class Button extends HelpShower {
		protected int x,  y, sizex, sizey;
		private PApplet app;
		private String help;
		private PImage icon;

		public Button(PApplet app, int x, int y, int sizex, int sizey, String help, PImage icon) {
	        this.app = app;
			this.x = x;
			this.y = y;
			this.sizex = sizex;
			this.sizey = sizey;
			this.help = help;
			this.icon = icon;
		}
		
		public Button(PApplet app, int x, int y, int sizex, int sizey) {
			this(app, x, y, sizex, sizey, "", null);
		}
		
		public Button(PApplet app, int x, int y, int sizex, int sizey, PImage icon) {
			this(app, x, y, sizex, sizey, "", icon);
		}

		public boolean isClicked(int mouseX, int mouseY) {
			return (mouseX < (x+sizex) && mouseX > x && 
					mouseY < (y+sizey) && mouseY > y);
		}
		
		public void draw(double t) {
	        app.imageMode(PApplet.CENTER);
			app.image(getIcon(), x + sizex/2, y + sizey/2);
		}
		
		public PImage getIcon() {
			return icon;
		}
		
		public void ifMouseOverShowHelp() {
			ifMouseOverShowHelp(app, getHelp());
		}
		
		public String getHelp() {
			return help;
		}

		public void click() {
			
		}
}
