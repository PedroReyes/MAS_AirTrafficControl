/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Auxiliar.Escenario;
import Auxiliar.Pista;
import Auxiliar.Vector;
import Output.auxiliar.Map;
import Sistema.Avion;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author pedro
 */
public class InterfazGrafica extends Agent {
	private HashMap<String, Avion> aviones;
	private Escenario escenario;
	private Map map;
	
    // =========================================================================
    // AGENT
    // =========================================================================
    @Override
    public void setup() {
    	Object[] args = getArguments();
        setEscenario((Escenario) args[0]);
        
        int nRow = getEscenario().getNumeroFilasAeropuerto();
        int nCol = getEscenario().getNumeroColumnasAeropuerto();
    	
    	String dgsFilePath = "";
		if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
			dgsFilePath = "/Users/pedro/Dropbox/Vida_laboral/Master/ProgramacionSistemasMultiagentes/TrabajoSegundaParte/MAS_AirTrafficControl/Output/FicherosDgs/";
		} else {
			dgsFilePath = "C:\\Users\\j_sto\\Documents\\GitHub\\MAS_AirTrafficControl\\Output\\FicherosDgs\\";
		}
		String nameDgsFile = "Simulacion" + nRow + "_" + nCol;
		String stylesheetFilePath = "";
		if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
			stylesheetFilePath = "/Users/pedro/Dropbox/Vida_laboral/Master/ProgramacionSistemasMultiagentes/TrabajoSegundaParte/MAS_AirTrafficControl/Output/Stylesheets/";
		} else {
			stylesheetFilePath = "C:\\Users\\j_sto\\Documents\\GitHub\\MAS_AirTrafficControl\\Output\\Stylesheets\\";
		}
		String initialStylesheetName = "initialStylesheet.css";
    	
		try {
			map = new Map(nRow, nCol, dgsFilePath, nameDgsFile, stylesheetFilePath, initialStylesheetName);
			map.createMap();
			
			for(Pista pista : getEscenario().getPistas()){
				map.addNewObjectTo(1, "Pista", pista.getCoordenadaX(), pista.getCoordenadaY(), 0, "pista");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        CyclicBehaviour almInformacion = new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Recibido nuevo mensaje " + msg.getContent());
                    actualizarInformacion(msg.getContent());
                } else {
                    block();
                }
            }
        };

        addBehaviour(almInformacion);
    }
    
 // =========================================================================
    // AUXILIARY METHODS
    // =========================================================================
    public void actualizarInformacion(String content) {
        String words[] = content.split(" ");
        switch (words[1]) {
            case "ADD":
                String vectorADD[] = words[3].split(",");
                map.addNewObjectTo(Integer.parseInt(words[0]), words[2], Integer.parseInt(vectorADD[0].substring(9)), Integer.parseInt(vectorADD[1].substring(2)), 0, "avion");
                aviones.put(words[2], new Avion(words[2], new Vector(Integer.parseInt(vectorADD[0].substring(9)), Integer.parseInt(vectorADD[1].substring(2)), 0), Double.parseDouble(words[4]), Double.parseDouble(words[5])));
                break;
            case "REM":
                //aviones.remove(words[2]);
                break;
            case "MOD":
                if(words.length != 4){ //Mensaje de Avion
                	String vectorMOD[] = words[3].split(",");
                    Avion aux = aviones.get(words[2]);
                	
                	Vector nuevaPosicion = new Vector(Integer.parseInt(vectorMOD[0].substring(9)), Integer.parseInt(vectorMOD[1].substring(2)), 0);
                	map.moveObjectFromTo(3, words[2], aux.getPosicionActual(), nuevaPosicion, 5);
                	
                	aux.setPosicionActual(new Vector(Integer.parseInt(vectorMOD[0].substring(9)), Integer.parseInt(vectorMOD[1].substring(2)), 0));
                	aviones.replace(words[2], aux);
                }
                aviones.replace(words[2], aux);
                break;
            default:
                break;
        }
    }
    
    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public Escenario getEscenario() {
		return escenario;
	}

	public void setEscenario(Escenario escenario) {
		this.escenario = escenario;
	}
}
