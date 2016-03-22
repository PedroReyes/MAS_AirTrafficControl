/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Output;

import java.io.IOException;
import java.util.HashMap;

import Auxiliar.Escenario;
import Auxiliar.Pista;
import Auxiliar.Vector;
import Output.auxiliar.Map;
import Sistema.Avion;
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
		System.out.println(nRow + " : " + nCol);

		aviones = new HashMap<String, Avion>();

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

			int pistaN = 0;
			for (Pista pista : getEscenario().getPistas()) {
				map.addNewObjectTo(1, "Pista" + pistaN, pista.getCoordenadaX(), pista.getCoordenadaY(), 0, "pista");
				pistaN++;
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
		// System.out.println("MENSAJE: " + content);
		if (true) {
			String words[] = content.split(" ");
			int step = Integer.valueOf(words[0]);
			String objectId = words[2];
			switch (words[1]) {
			case "ADD":
				String vectorADD[] = words[3].split(",");
				map.addNewObjectTo(Integer.parseInt(words[0]), words[2], Integer.parseInt(vectorADD[0].substring(9)),
						Integer.parseInt(vectorADD[1].substring(2)), 0, "avion");
				aviones.put(objectId,
						new Avion(objectId,
								new Vector(Integer.parseInt(vectorADD[0].substring(9)),
										Integer.parseInt(vectorADD[1].substring(2)), 0),
								Double.parseDouble(words[4]), Double.parseDouble(words[5])));
				break;
			case "REM":
				map.removeObject(step, objectId);
				aviones.remove(objectId);
				break;
			case "MOD":
				if (words.length != 4) { // Mensaje de Avion
					String vectorMOD[] = words[3].split(",");
					Avion aux = aviones.get(objectId);

					Vector nuevaPosicion = new Vector(Integer.parseInt(vectorMOD[0].substring(9)),
							Integer.parseInt(vectorMOD[1].substring(2)), 0);
					System.out.println();
					System.out.println(objectId + " posicion actual > " + aux.getPosicionActual());
					map.moveObjectFromTo(step, objectId, aux.getPosicionActual(), nuevaPosicion, 5);

					aux.setPosicionActual(new Vector(Integer.parseInt(vectorMOD[0].substring(9)),
							Integer.parseInt(vectorMOD[1].substring(2)), 0));
					aviones.replace(objectId, aux);
				}
				break;
			default:
				break;
			}
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
