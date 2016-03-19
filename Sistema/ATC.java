/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sistema;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Auxiliar.Pista;
import Auxiliar.Vector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author pedro
 */
public class ATC extends Agent {

	private HashMap<String, Avion> aviones;
	private List<Pista> pistas;
	private Vector dimensionesDelMapa;

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
	@SuppressWarnings("unchecked")
	@Override
	public void setup() {
		Object[] args = getArguments();
		setPistas((List<Pista>) args[0]);
		setDimensionesDelMapa((Vector) args[1]);

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
				MessageTemplate mAlmacenDInf = MessageTemplate.MatchSender(new AID("adi", AID.ISLOCALNAME));
				ACLMessage msg = myAgent.blockingReceive(mAlmacenDInf);
				String idAvion = "";

				// Actualizo el estado de los aviones
				// System.out.println("ATC: Recibe: " + msg.getContent());
				idAvion = actualizarInformacion(msg.getContent());

				// Avion el cual estamos tratando actualmente y que sera
				// posiblemente modficados
				Avion avionModificado = null;
				if (idAvion.isEmpty())
					throw new IllegalArgumentException("Error: no existe o no se pudo acceder al id de este avion");
				else
					avionModificado = aviones.get(idAvion);

				// Vector director final del avion
				Vector vectorDirectorFinal = avionModificado.getVectorDirector();
				System.out.println("Vector inicial del avion: " + vectorDirectorFinal);

				// ===============================
				// ALGORITMO 1: ESTABLECIENDO RUTA PARA ATERRIZAR
				// ===============================
				// Obtenemos la pista mas cercana
				Pista mejorPista = null;
				double bestDistance = Double.MAX_VALUE;
				for (Pista pista : pistas) {
					Vector entrada = new Vector(pista.getCoordenadaX(), pista.getCoordenadaY(), pista.getCoordenadaX());
					double auxDistance = Vector.distance(entrada, avionModificado.getPosicionActual());
					if (auxDistance < bestDistance) {
						bestDistance = auxDistance;
						mejorPista = pista;
					}
				}
				System.out.println("MEJOR PISTA: " + mejorPista);

				// Establecemos que su vector director final es el punto de
				// entrada de la pista mas cercana
				if (false) {
					Vector entrada = new Vector(mejorPista.getEntradaX(), mejorPista.getEntradaX(),
							mejorPista.getCoordenadaX());
					vectorDirectorFinal = getBestDirectorVectorToPosition(avionModificado.getPosicionActual(), entrada);
					System.out.println("Al dirigirse a la entrada: " + vectorDirectorFinal);
				}

				// Si su posicion actual es el punto de entrada, le indicamos
				// qeu su vector director es hacia la pista
				Vector posMejorPista = new Vector(mejorPista.getCoordenadaX(), mejorPista.getCoordenadaY(),
						mejorPista.getCoordenadaX());
				System.out.println("Vector pista del avion: " + vectorDirectorFinal);
				if (!avionModificado.getPosicionActual().equals(posMejorPista)) {
					System.out.println("MODIFICADO VECTOR DIRECTOR...");
					vectorDirectorFinal = getBestDirectorVectorToPosition(avionModificado.getPosicionActual(),
							posMejorPista);
				}
				System.out.println("Vector pista del avion: " + vectorDirectorFinal);

				// Si su posicion actual es la pista de aterrizaje, lo
				// eliminamos del mapa
				boolean eliminarAvion = false;
				if (avionModificado.getPosicionActual().equals(posMejorPista)) {
					// mando mensaje para que se elimine este avion
					eliminarAvion = true;
				}

				// ===============================
				// ALGORITMO 2: EVITAR COLISIONES
				// ===============================
				String content = "OPERACION (id del avion) (empty order)";
				content = "0 MOD " + idAvion + " " + vectorDirectorFinal;
				if (false) {
					if (!eliminarAvion) {
						// Vectores directores a los que no podemos dirigirnos
						List<Vector> vectoresDirectoresRestringidos = new LinkedList<Vector>();
						List<Vector> posiblesVectoresDirectores = obtenerPosiblesVectoresDirectores();

						// Posicion siguiente en la que se encontraría el avion
						// si
						// siguiera la ruta fijada
						Vector avionModificadoPosSiguiente = Vector.sum(avionModificado.getPosicionActual(),
								avionModificado.getVectorDirector());

						// Vemos las colisiones con los otros aviones
						for (String key : aviones.keySet()) {
							// Compruebo posibles colisiones del avion con otros
							// aviones
							if (!key.equals(idAvion)) {
								// Me hago con el avion
								Avion avion = aviones.get(key);

								// Me hago con la posicion siguiente
								Vector avionPosSiguiente = Vector.sum(avion.getPosicionActual(),
										avion.getVectorDirector());

								//
								if (entranEnAreaDeRiesgoDeColision(avionPosSiguiente, avionModificadoPosSiguiente)) {
									// Añado a vectores directores restringidos
									// el
									// vector director actual
									vectoresDirectoresRestringidos.add(avionModificado.getVectorDirector());

									// Elegir la mejor accion para evitar dicha
									// colision
									for (Vector posibleVector : posiblesVectoresDirectores) {
										if (!vectoresDirectoresRestringidos.contains(posibleVector)) {
											// Consigo la nueva posicion usando
											// el
											// nuevo vector director
											avionModificadoPosSiguiente = Vector.sum(
													avionModificado.getPosicionActual(),
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

									System.out.println("Vector evitar colision del avion: " + vectorDirectorFinal);
								} else {
									// Siga su rumbo, cambio y corto
								}
							}
						}

						// ===============================
						// ALGORITMO 3: LIMITES DEL MAPA
						// ===============================
						if (false) {
							Vector mapaDimensiones = getDimensionesDelMapa();
							vectorDirectorFinal = new Vector(
									avionModificadoPosSiguiente.x < 1 ? +1
											: (avionModificadoPosSiguiente.x > mapaDimensiones.getX() ? -1
													: avionModificado.getVectorDirector().x),
									avionModificadoPosSiguiente.y < 1 ? +1
											: (avionModificadoPosSiguiente.y > mapaDimensiones.getY() ? -1
													: avionModificado.getVectorDirector().y),
									0);

							System.out.println("Vector limites del avion: " + vectorDirectorFinal);

						}
						// ===============================
						// Envio la informacion al Almacen
						// ===============================
						content = "0 MOD " + idAvion + " " + vectorDirectorFinal;
					} else {
						// ===============================
						// Envio la informacion al Almacen
						// ===============================
						System.out.println("====================");
						System.out.println("Eliminar avion!!!!!");
						System.out.println("====================");
						content = "0 REM " + idAvion + " " + " ";
					}
				}
				informarAlmacenInformacion(idAvion, content);
			}
		};

		addBehaviour(almInformacion);
	}

	/**
	 * Devuelve el vector director al que se tendría que dirigirse desde
	 * posOrigen a posDestino
	 * 
	 * @param pos
	 * @return
	 */
	private Vector getBestDirectorVectorToPosition(Vector posOrigen, Vector posDestino) {
		// TODO Auto-generated method stub
		if (posOrigen.x == posDestino.x) {
			// estan en la misma fila, voy hacia derecha o hacia izquierda
			return new Vector(0, posDestino.y - posOrigen.y > 0 ? 1 : -1, posOrigen.z);
		} else if (posOrigen.y == posDestino.y) {
			// estan en la misma columna, voy hacia abajo o hacia arriba
			return new Vector(posDestino.x - posOrigen.x > 0 ? 1 : -1, 0, posOrigen.z);
		} else {
			if (posDestino.x > posOrigen.x && posDestino.y > posOrigen.y) {
				return new Vector(1, 1, 0);
			} else if (posDestino.x > posOrigen.x && posDestino.y < posOrigen.y) {
				return new Vector(1, -1, 0);
			} else if (posDestino.x < posOrigen.x && posDestino.y > posOrigen.y) {
				return new Vector(-1, 1, 0);
			} else if (posDestino.x < posOrigen.x && posDestino.y < posOrigen.y) {
				return new Vector(-1, -1, 0);
			} else {
				throw new IllegalArgumentException("Mierda, algo no he tenido en cuenta.");
			}
		}
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

	// =========================================================================
	// GETTERS & SETTERS
	// =========================================================================
	public void setAviones(HashMap<String, Avion> avion) {
		this.aviones = avion;
	}

	public HashMap<String, Avion> getAviones() {
		return this.aviones;
	}

	public void setPistas(List<Pista> pista) {
		this.pistas = pista;
	}

	public List<Pista> getPistas() {
		return this.pistas;
	}

	public Vector getDimensionesDelMapa() {
		return dimensionesDelMapa;
	}

	public void setDimensionesDelMapa(Vector dimensionesDelMapa) {
		this.dimensionesDelMapa = dimensionesDelMapa;
	}

}
