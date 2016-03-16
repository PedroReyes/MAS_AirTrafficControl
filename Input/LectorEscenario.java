/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Input;

import Auxiliar.Escenario;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Hola, soy un agente y mi nombre será siempre "LectorEscenario"
 *
 * @author pedro
 */
public class LectorEscenario extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Mis argumentos de entrada
	private String pathToSimulation;
	private ContainerController home;

	// Lo que envio
	private Escenario escenario;

	protected void setup() { // this runs once before starting behaviors
		Object[] args = getArguments();
		setPathToSimulation((String) args[0]);
		setHome((ContainerController) args[1]);

		// Creo el escenario
		this.escenario = new Escenario();
		this.escenario.readEscenario(pathToSimulation);
		System.out.println(this.escenario);

		// First set-up answering behaviour
		Object[] argsSend = new Object[2];
		argsSend[0] = getEscenario();
		argsSend[1] = getHome();
		try {
			AgentController ac = getHome().createNewAgent("tmp", Input.ControlTemporal.class.getName(), argsSend);
			ac.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// =================
	// SETTERS & GETTERS
	// =================
	public String getPathToSimulation() {
		return pathToSimulation;
	}

	public void setPathToSimulation(String pathToSimulation) {
		this.pathToSimulation = pathToSimulation;
	}

	public ContainerController getHome() {
		return home;
	}

	public void setHome(ContainerController home) {
		this.home = home;
	}

	public Escenario getEscenario() {
		return escenario;
	}

	public void setEscenario(Escenario escenario) {
		this.escenario = escenario;
	}

}
