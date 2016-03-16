/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sistema;

import Auxiliar.Escenario;
import Auxiliar.Vector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ContainerController;

/**
 *
 * @author pedro
 */
public class ATC extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Mis argumentos de entrada
	private String pathToSimulation;
	private ContainerController home;

	// Los aviones deberan estar a una distancia prudencial los unos de los
	// otros
	final int riskArea = 2;
	final int radioDelAvion = 1;

	// Lo que envio
	private Escenario escenario;

	@Override
	protected void setup() {
		Object[] args = getArguments();
		// setID((String) args[0]);

		addBehaviour(new OneShotBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				// msg.setContent(getTimeStep() + " " + "ADD " + getID() + " " +
				// getPosicionActual() + " "+ getCombustibleActual() + " " +
				// getCombustibleXStep());
				msg.addReceiver(new AID("adi", AID.ISLOCALNAME));
				send(msg);
			}
		});

		MessageTemplate mControlTemp = MessageTemplate.MatchSender(new AID("tmp", AID.ISLOCALNAME));
		MessageTemplate mAlmacenDInf = MessageTemplate.MatchSender(new AID("adi", AID.ISLOCALNAME));

		addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				// Primero se bloquea esperando un mensaje de ControlTemporal
				ACLMessage msg = myAgent.blockingReceive(mControlTemp);
				// setTimeStep(Integer.parseInt(msg.getContent()));
				/*
				 * By using blockingReceive(), the receiving agent suspends all
				 * its activities until a message arrives:
				 * 
				 * ACLMessage msg = blockingReceive(); Optional arguments:
				 * 
				 * pattern object: to select the kinds of message that we want
				 * to receive. timeout: if the timeout expires before a desired
				 * message arrives, the method returns null.
				 */

				// Actualiza su posicion y combustible
				// actualizarPosicion();
				// mandarNuevaPosicion();

				// Espera una correccion de vuelo
				msg = myAgent.blockingReceive(mAlmacenDInf);
				// actualizarInformacion(msg.getContent());
			}
		});
	}

	public boolean siChocan(Avion avion1, Avion avion2) {
		Vector v1 = avion1.getPosicionActual();
		Vector v2 = avion2.getPosicionActual();

		double aux = Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2);

		return Math.pow(radioDelAvion - radioDelAvion, 2) <= aux
				? (aux <= Math.pow(radioDelAvion + radioDelAvion, 2) ? true : false) : false;
	}

}
