package Output.auxiliar;

import java.io.IOException;

import Auxiliar.Vector;

public class Main {

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		// ===============================
		// Ejecutar el programa (cada evento durante la ejecución se guarda en
		// un archivo .dgs que es el que se ejecuta posteriormente en la
		// simulación)
		// ===============================
		int nRow = 20;
		int nCol = 20;

		// Se indica el fichero donde se salvará la simulación así como el
		// estilo de la ejecución
		String dgsFilePath = "";
		if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
			dgsFilePath = "/Users/pedro/Dropbox/Vida_laboral/Master/ProgramacionSistemasMultiagentes/TrabajoSegundaParte/MAS_AirTrafficControl/Output/FicherosDgs/";
		} else {
			dgsFilePath = "javi";
		}
		String nameDgsFile = "Simulacion" + nRow + "_" + nCol;
		String stylesheetFilePath = "";
		if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
			stylesheetFilePath = "/Users/pedro/Dropbox/Vida_laboral/Master/ProgramacionSistemasMultiagentes/TrabajoSegundaParte/MAS_AirTrafficControl/Output/Stylesheets/";
		} else {
			stylesheetFilePath = "javi";
		}
		String initialStylesheetName = "initialStylesheet.css";

		// =======================
		// Ejecutar main normal
		// =======================
		// para ejecutar una simulación primero se ha debido correr el programa
		boolean ejecutarSimulacion = false;
		long speedness = 1000;
		if (!ejecutarSimulacion) {
			// Creamos el mapa
			Map map = new Map(nRow, nCol, dgsFilePath, nameDgsFile, stylesheetFilePath, initialStylesheetName);
			map.createMap();

			// Añadir un objeto al mapa (mas eventos)
			map.addNewObjectTo(1, "Avion1", 1, 1, 0, "avion");
			map.addNewObjectTo(2, "Pista1", 10, 5, 0, "pista");

			// Mover un objeto por el mapa (mas eventos)
			// --------> No implementado aún // falta hacer esto para .dgs
			map.moveObjectFromTo(3, "Avion1", new Vector(1, 1, 0), new Vector(2, 2, 0), 5);
			Thread.sleep(speedness);
			map.moveObjectFromTo(8, "Avion1", new Vector(2, 2, 0), new Vector(3, 3, 0), 5);
			Thread.sleep(speedness);
			map.moveObjectFromTo(10, "Avion1", new Vector(3, 3, 0), new Vector(3, 3, 0), 5);
			Thread.sleep(speedness);
			map.moveObjectFromTo(12, "Avion1", new Vector(3, 3, 0), new Vector(3, 3, 0), 5);
			Thread.sleep(speedness);

			// map.re
			map.removeObject(16, "Avion1");

			System.out.println("END");
		} else {
			// =======================
			// Ejecutar la simulación
			// =======================
			// Creamos el mapa
			Map map = new Map(nRow, nCol, dgsFilePath, nameDgsFile, stylesheetFilePath, initialStylesheetName);
			map.executeSimulation(dgsFilePath, nameDgsFile, speedness, false);
		}
	}

}
