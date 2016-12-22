package visualizzazione;

public interface Clickable extends Drawable {
	public boolean isClicked(int x, int y);
	
	public void click();
}
