/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Input;

import java.util.List;
import java.util.Map;

import Auxiliar.Escenario;
import Auxiliar.Vector;
import Sistema.Avion;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * @author Javier Moreno
 */
public class ControlTemporal extends Agent {

	private int timeStep;
	private Escenario escenario;
	private ContainerController home;

	// =========================================================================
	// AGENT
	// =========================================================================
	@Override
	protected void setup() {
		Object[] args = getArguments();
		setEscenario((Escenario) args[0]);
		setContainer((ContainerController) args[1]);

		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				// Crear todos los agentes necesarios
				try {
					// Crear ATC
					Object[] args = new Object[2];
					args[0] = getEscenario().getPistas();
					args[1] = new Vector(getEscenario().getNumeroColumnasAeropuerto(),getEscenario().getNumeroFilasAeropuerto(),0);
					
					AgentController a = home.createNewAgent("atc", Sistema.ATC.class.getName(), args);
					a.start();

					// Crear AlmacenDeInformacion
					a = home.createNewAgent("adi", Sistema.AlmacenDeInformacion.class.getName(), new Object[0]);
					a.start();

					// Crear InterfazGrafica
					args = new Object[1];
					args[0] = getEscenario();
					
					a = home.createNewAgent("gph", Output.InterfazGrafica.class.getName(), args);
					a.start();

					// Crear Logger
					a = home.createNewAgent("log", Output.Logger.class.getName(), new Object[0]);
					a.start();
				} catch (StaleProxyException e) {
					e.printStackTrace();
				}
			}
		});

		TickerBehaviour controlTemp;
		controlTemp = new TickerBehaviour(this, 1000) {
			@Override
			protected void onTick() {
				timeStep = getTickCount();

				// Miro en el escenario si tengo que generar nuevos aviones en
				// este tick
				Map<Integer, List<Avion>> simulacion = escenario.getEntradaSimuladaAviones();
				List<Avion> aviones = simulacion.get(timeStep);

				// Si la lista no es vacia, inicializo los aviones
				if (aviones != null) {
					if (!aviones.isEmpty()) {
						aviones.stream().forEach((avion) -> {
							inicializacionAgentes(avion);
						});
					}
				}

				// Mando nuevo timeStep
				System.out.println("----------------------------------------------------------");
				System.out.println("Agent " + myAgent.getLocalName() + ": tick=" + timeStep);
				System.out.println("----------------------------------------------------------");
				mandarMensajes();
			}
		};

		addBehaviour(controlTemp);
	}

	// =========================================================================
	// AUXILIARY METHODS
	// =========================================================================
	public void mandarMensajes() {
		String prefix = "( agent-identifier :name ";
		AMSAgentDescription[] agents = null;

		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
		}

		for (AMSAgentDescription agent : agents) {
			String agentID = agent.getName().toString();
			if (agentID.startsWith(prefix + "atc") || agentID.startsWith(prefix + "av")) {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent(String.valueOf(timeStep));
				msg.addReceiver(agent.getName());
				send(msg);
			}
		}
	}

	public void inicializacionAgentes(Avion agente) {
		Object[] args = new Object[5];
		args[0] = agente.getID();
		args[1] = agente.getPosicionActual();
		args[2] = agente.getCombustibleActual();
		args[3] = agente.getCombustibleXStep();
		args[4] = timeStep;

		try {
			AgentController a = home.createNewAgent(args[0].toString(), Avion.class.getName(), args);
			a.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================
	// ToString
	// =========================================================================
	@Override
	public String toString() {
		return "ControlTemporal: [" + getEscenario() + "," + getTimeStep() + "]\n";
	}

	// =========================================================================
	// GETTERS & SETTERS
	// =========================================================================
	public void setTimeStep(int time) {
		this.timeStep = time;
	}

	public int getTimeStep() {
		return this.timeStep;
	}

	public void setEscenario(Escenario escenario) {
		this.escenario = escenario;
	}

	public Escenario getEscenario() {
		return this.escenario;
	}

	public void setContainer(ContainerController cc) {
		this.home = cc;
	}

	public ContainerController getContainer() {
		return this.home;
	}
}
