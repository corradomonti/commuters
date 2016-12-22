package modello;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modello.azioni.Azione;
import modello.azioni.Treno;


public class AmbienteBuilder {
	
	public static Ambiente buildFrom(String path) {
		return buildFrom(new File(path));
	}
	
	public static Ambiente buildFrom(File f) {
		AmbienteBuilder builder = new AmbienteBuilder();
		builder.build(f);
		return builder.sys;
	}
	
	Ambiente sys = Ambiente.getInstance();
	Map<String, Posto> posti = new HashMap<String, Posto>();
	ArrayList<Azione> azioni = new ArrayList<Azione>();

	public void build(File f) {
		try {
			BufferedReader br;

			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)
			));
		
			String line;
			
			while ((line = br.readLine()) != null)   {
				if (!line.startsWith("//"))
					parse(line.toLowerCase());
			}
			
			br.close();
		
		addElementsTo(sys);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void addElementsTo(Ambiente sys) {
		Posto lavoro = posti.remove("work");
		sys.STANDARD_WORKING_PLACE = lavoro;
		
		Posto[] diPosti = {};
		sys.stazioni = posti.values().toArray(diPosti);
		
		Azione[] diAzioni = {};
		sys.mezzi = azioni.toArray(diAzioni);
	}

	private void parse(String line) throws ParseException {
		if (line.indexOf('=') != -1)
			parsePosto(line);
		else
			parseMezzo(line);
	}
	
	private void parsePosto(String line) {
		String name, place;
		int separator = line.indexOf('=');
		name = line.substring(0, separator).trim();
		place = line.substring(separator+1).trim();
		
		separator = place.indexOf(',');
		Posto p = new Posto(
				Integer.parseInt(place.substring(0, separator).trim())
				,
				Integer.parseInt(place.substring(separator+1).trim())
				);
		
		posti.put(name, p);
	}

	private void parseMezzo(String line) throws ParseException {
		Matcher match = Pattern.compile(
				//parsa "Treno from Stazione1 to Stazione2 every 30 takes 30"
				".*?from\\s+?(.+?)\\s.*?to\\s+?(.+?)\\s.*?every\\s+(\\d+).*?takes\\s+(\\d+)"
				).matcher(line);
		
		if (match.matches()) {
			azioni.add(new Treno(
					posti.get(match.group(1)),
					posti.get(match.group(2)),
					Ambiente.ogni(Integer.parseInt(match.group(3))), 
					Integer.parseInt(match.group(4))
					));
		} else if (line.trim().length() > 0)
			throw new ParseException("Line '" + line + "' is wrong", 0);
		
	}



}
