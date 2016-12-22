package modello;

public class Posto {
	public short x, y;
	
	public Posto(short x, short y) {
		this.x = x;
		this.y = y;
	}
	
	public Posto(int x, int y) {
		this.x = (short) x;
		this.y = (short) y;
	}
	
	public boolean equals(Posto p) {
		return p != null && ((p.x == this.x) && (p.y == this.y));
	}
	
	public boolean equals(Object o) {
		if (o instanceof Posto)
			return this.equals((Posto) o);
		else
			return false;
	}
	
	public String toString() {
		if (this.equals(Ambiente.getInstance().STANDARD_WORKING_PLACE))
			return "Work";
		
		if (this.equals(Ambiente.getInstance().attualeCasa))
			return "Home";
		
		try { return Ambiente.getInstance().getStationName(this); }
			catch (Exception e) { /*not a station*/ }
		
		return "[" + x + "," + y + "]";
	}
	
	public double dist(Posto p2) {
		float dx = this.x - p2.x;
		float dy = this.y - p2.y;
		
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	 public int hashCode() {
		 return ( ((x+y)*(x+y+1))/2 ) + y; //accoppiamento di cantor
	 }
	
}
