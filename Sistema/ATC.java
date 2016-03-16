/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sistema;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Auxiliar.Vector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author pedro
 */
public class ATC extends Agent {

	private HashMap<String, Avion> aviones;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Los aviones deberan estar a una distancia prudencial los unos de los
	// otros
	final int riskArea = 2;
	final int radioDelAvion = 1 + riskArea; // MINIMO EL RADIO DEL AVION DEBE
	// SER 1

	// =========================================================================
	// AGENT
	// =========================================================================
	@Override
	public void setup() {
		// Inicializamos aviones, los aviones que se añaden iran viniendo
		// de AlmacenDeInformacion
		aviones = new HashMap<String, Avion>();

		// Que comience el trabajo de este agente...
		CyclicBehaviour almInformacion = new CyclicBehaviour(this) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				// Recibo mensaje de AlmacenDeInformacion
				ACLMessage msg = receive();
				String idAvion = "";
				if (msg != null) {
					// Actualizo el estado de los aviones
					System.out.println("Recibido nuevo mensaje " + msg.getContent());
					idAvion = actualizarInformacion(msg.getContent());
				} else {
					System.out.println("No recibo nada");
					block();
				}

				// ALGORITMO 1: ESTABLECIENDO RUTA PARA ATERRIZAR

				// ALGORITMO 2: EVITAR COLISIONES
				String content = "av (id del avion) empty order";
				content = "MOD/REM (id del avion) (VECTOR DIRECTOR)";
				// Avion que va a ser modificado
				Avion avionModificado = null;
				if (idAvion.isEmpty())
					throw new IllegalArgumentException("Error: no existe o no se pudo acceder al id de este avion");
				else
					avionModificado = aviones.get(idAvion);

				// Vector director final del avion
				Vector vectorDirectorFinal = avionModificado.getVectorDirector();

				// Vectores directores a los que no podemos dirigirnos
				List<Vector> vectoresDirectoresRestringidos = new LinkedList<Vector>();
				List<Vector> posiblesVectoresDirectores = obtenerPosiblesVectoresDirectores();

				// Vemos las colisiones con los otros aviones
				for (String key : aviones.keySet()) {
					// Compruebo posibles colisiones del avion con otros aviones
					if (!key.equals(idAvion)) {
						// Me hago con el avion
						Avion avion = aviones.get(key);

						// Me hago con la posicion siguiente
						Vector avionPosSiguiente = Vector.sum(avion.getPosicionActual(), avion.getVectorDirector());
						Vector avionModificadoPosSiguiente = Vector.sum(avionModificado.getPosicionActual(),
								avionModificado.getVectorDirector());

						//
						if (entranEnAreaDeRiesgoDeColision(avionPosSiguiente, avionModificadoPosSiguiente)) {
							// Añado a vectores directores restringidos el
							// vector director actual
							vectoresDirectoresRestringidos.add(avionModificado.getVectorDirector());

							// Elegir la mejor accion para evitar dicha colision
							for (Vector posibleVector : posiblesVectoresDirectores) {
								if (!vectoresDirectoresRestringidos.contains(posibleVector)) {
									// Consigo la nueva posicion usando el nuevo
									// vector director
									avionModificadoPosSiguiente = Vector.sum(avionModificado.getPosicionActual(),
											avionModificado.getVectorDirector());

									// Compruebo que no haya chocque
									if (!entranEnAreaDeRiesgoDeColision(avionPosSiguiente,
											avionModificadoPosSiguiente)) {
										avionModificado.setVectorDirector(posibleVector);
										vectorDirectorFinal = posibleVector;
										break;
									}
								}
							}
						} else {
							// Siga su rumbo, cambio y corto
						}
					}
				}
				//
				content = "MOD " + idAvion + " " + vectorDirectorFinal;
				informarAlmacenInformacion(idAvion, content);
			}
		};

		addBehaviour(almInformacion);
	}

	/**
	 * Obtiene los posibles vectores a los que se puede dirigir el avion
	 * 
	 * @return
	 */
	private List<Vector> obtenerPosiblesVectoresDirectores() {
		List<Vector> posiblesVectoresDirectores;
		posiblesVectoresDirectores = new LinkedList<Vector>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; i <= 1; i++) {
				posiblesVectoresDirectores.add(new Vector(i, j, null));
			}
		}
		return posiblesVectoresDirectores;
	}

	// =========================================================================
	// AUXILIARY METHODS
	// =========================================================================
	public String actualizarInformacion(String content) {
		String words[] = content.split(" ");
		String vector[] = words[3].split(",");
		switch (words[1]) {
		case "ADD":
			aviones.put(words[2],
					new Avion(words[2],
							new Vector(Integer.parseInt(vector[0].substring(9)),
									Integer.parseInt(vector[1].substring(2)), 0),
							Double.parseDouble(words[4]), Double.parseDouble(words[5])));
			break;
		case "MOD":
			Avion aux = aviones.get(words[2]);
			// Mensaje de Almacen de informacion
			aux.setPosicionActual(
					new Vector(Integer.parseInt(vector[0].substring(9)), Integer.parseInt(vector[1].substring(2)), 0));
			aux.setCombustibleActual(Double.parseDouble(words[4]));
			aux.setCombustibleXStep(Double.parseDouble(words[5]));

			aviones.replace(words[2], aux);
			break;
		default:
			break;
		}
		return words[2]; // devuelve el ID del agente
	}

	public boolean entranEnAreaDeRiesgoDeColision(Vector avion1, Vector avion2) {
		Vector v1 = avion1;// .getPosicionActual();
		Vector v2 = avion2;// .getPosicionActual();

		double aux = Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2);

		return Math.pow(radioDelAvion - radioDelAvion, 2) <= aux
				? (aux <= Math.pow(radioDelAvion + radioDelAvion, 2) ? true : false) : false;
	}

	public void informarAlmacenInformacion(String idAvion, String content) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(content);
		msg.addReceiver(new AID("adi", AID.ISLOCALNAME));
		msg.addReplyTo(new AID(idAvion, AID.ISLOCALNAME));
		send(msg);
	}

}
