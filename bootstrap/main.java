package bootstrap;

import Input.LectorEscenario;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// double click the RunJade constructor to see the java doc asociated
		// with it (that includes the parameter significance)
		// pentru a rula un container auxiliar comenteaza urmatoarea linie
		RunJade r = new RunJade(true, "30000");// asta porneste un main
												// container pe masina curenta
												// portul 30000

		// pentru a rula un container auxiliar: decomenteaza linia urmatoare si
		// scrie ip-ul MainContainer-ului (asa cum e acum nu va merge sigur din
		// cauza lui "ceva")
		// RunJade r=new RunJade("193.226.37.ceva", "30000","30000");
		/**
		 * daca rulati un container auxiliar si in MainContainer ati rulat
		 * aceiasi agenti va aparea o eroare "name-clash" aceasta se datoreaza
		 * faptului ca ati incercat sa creati 2 agenti cu acelasi nume unul in
		 * MainContainer si unul in Containerul auxiliar (mai exact "a1" si
		 * "a2") SOLUTIE: aveti grija ca numele sa fie unice in intreaga
		 * platforma.
		 */
		// you will need this pointer to the created container in order to be
		// able to create agents
		ContainerController home = r.getHome();

		///////////////////////////////////////////////////////////////////////////
		// crearea propriu-zisa a unui agent
		try {
			Object[] arguments = new Object[2];
			if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
				// Mac OS X
				arguments[0] = "/Users/pedro/Dropbox/Vida_laboral/Master/ProgramacionSistemasMultiagentes/TrabajoSegundaParte/MAS_AirTrafficControl/Auxiliar/Simulacion.txt";
			} else {
				// WINDOWS
				arguments[0] = "JAVI AQUI VA LA URL A LA SIMULACIÓN EN TU ORDENADOR";
			}
			arguments[1] = home;

			AgentController a = home.createNewAgent("LectorEscenario", LectorEscenario.class.getName(), arguments);
			a.start();

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

}
