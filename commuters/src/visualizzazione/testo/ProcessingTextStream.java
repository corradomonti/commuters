package visualizzazione.testo;

public abstract class ProcessingTextStream extends java.io.PrintStream {
	
	public ProcessingTextStream() {
		super(System.err);
	}
	
	public void println(Object o) {
		println(o.toString());
	}
	
	public abstract void draw();
	
	public abstract void println(String s);
}
