package it.polito.tdp.meteo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class MeteoController {
	
	private Model model;
	
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<Integer> boxMese;

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;

	public void setModel(Model model) {
	    this.model = model;
	    
	    List<Integer> mesi = model.getAllMesi();
	    boxMese.getItems().addAll(mesi);
    }
	
	@FXML
	void doCalcolaSequenza(ActionEvent event) {
		
		txtResult.clear();
		
		try {
		
			int mese = boxMese.getValue();
			
			if( boxMese.getValue() == null ) {
				txtResult.setText("Selezionare un mese.");
				return;
			}
		
			String sequenza = model.trovaSequenza(mese).toString();
			txtResult.setText(sequenza);
		
		} catch (RuntimeException re) {
			txtResult.setText(re.getMessage());
		}

	}

	@FXML
	void doCalcolaUmidita(ActionEvent event) {
		
		txtResult.clear();
		try {
			
			int mese = boxMese.getValue();
			
			if( boxMese.getValue() == null ) {
				txtResult.setText("Selezionare un mese.");
				return;
			}
			
			String umiditaMedia = model.getUmiditaMedia(mese);	
	    	txtResult.setText(umiditaMedia);
		
		} catch (RuntimeException re) {
			txtResult.setText(re.getMessage());
		}

	}

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";
	}

}
