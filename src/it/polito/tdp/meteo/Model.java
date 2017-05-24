package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {
	
	private final static int COSTO_CAMBIO = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO meteoDAO;
	private List<Citta> cities;
    
	private double bestScore;
    private List<SimpleCity> solutions; 

	public Model() {
		
		meteoDAO = new MeteoDAO();

		cities = new ArrayList<Citta>();
		for(String s : meteoDAO.getAllLocalita())
			cities.add(new Citta(s));

	}
	
	public String getUmiditaMedia(int mese) {
		double umidita = 0.0;
		String risultato = "";
		
		for(Citta c : cities) {
			umidita = meteoDAO.getAvgRilevamentiLocalitaMese(mese, c.getNome());
			risultato += "L'umidità media per questo mese a "+c.getNome()+" è "+umidita+"\n";
		}
		return risultato;
	}
	
	public List<SimpleCity> trovaSequenza(int mese) {
		
		bestScore = Double.MAX_VALUE;
		solutions = new ArrayList<SimpleCity>();
		
		this.resetCities(mese);

		int step = 0;
		recursive(solutions,step);		
		
		return solutions;

	}
	
	private void resetCities(int mese) {
		for (Citta c : cities) {
			c.setCounter(0);
			c.setRilevamenti(meteoDAO.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
	}
	
	private void recursive(List<SimpleCity> parziale, int step) {

		if (step >= NUMERO_GIORNI_TOTALI) {

			double score = this.punteggioSoluzione(parziale);

			if (score < bestScore) {
				bestScore = score;
				solutions = new ArrayList<SimpleCity>(parziale);
			}
			return;
		}

		for (Citta citta : cities) {

			SimpleCity sc = new SimpleCity(citta.getNome(), citta.getRilevamenti().get(step).getUmidita());

			parziale.add(sc);
			citta.increaseCounter();

			if (controllaParziale(parziale))
				recursive(parziale, step + 1);

			parziale.remove(step);
			citta.decreaseCounter();
		}
	}
		
	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		if (soluzioneCandidata == null || soluzioneCandidata.size() == 0)
			return Double.MAX_VALUE;

		for (Citta c : cities) {
			if (!soluzioneCandidata.contains(new SimpleCity(c.getNome())))
				return Double.MAX_VALUE;
		}

		SimpleCity previous = soluzioneCandidata.get(0);
		double score = 0.0;

		for (SimpleCity sc : soluzioneCandidata) {
			if (!previous.equals(sc)) {
				score += COSTO_CAMBIO;
			}
			previous = sc;
			score += sc.getCosto();
		}

		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {

		if (parziale == null)
			return false;

		if (parziale.size() == 0)
			return true;

		for (Citta citta : cities) {
			if (citta.getCounter() > NUMERO_GIORNI_CITTA_MAX)
				return false;
		}

		SimpleCity previous = parziale.get(0);
		int counter = 0;

		for (SimpleCity sc : parziale) {
			if (!previous.equals(sc)) {
				if (counter < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
					return false;
				}
				counter = 1;
				previous = sc;
			} else {
				counter++;
			}
		}

		return true;
	}
	
	public List<Integer> getAllMesi() {
		return meteoDAO.getAllMesi();
	}

}
