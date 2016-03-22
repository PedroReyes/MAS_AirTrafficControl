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
	private HashMap<String, Boolean> avionesPorPuntoEntrada;
	private List<Pista> pistas;
	private Vector dimensionesDelMapa;
	private HashMap<String, Pista> pistasAsignadas;
	private HashMap<String, Integer> stepsDeEspera;

	private static int contadorMensajes = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Los aviones deberan estar a una distancia prudencial los unos de los
	// otros
	final int riskArea = 1;
	final int radioDelAvion = 0 + riskArea; // MINIMO EL RADIO DEL AVION DEBE
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
		avionesPorPuntoEntrada = new HashMap<String, Boolean>();
		pistasAsignadas = new HashMap<>();
		stepsDeEspera = new HashMap<>();

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

				// Actualizo contador
				contadorMensajes++;

				// Actualizar contador de mensajes
				System.out.println("Contador: " + contadorMensajes);
				if (contadorMensajes == aviones.size()) {
					// Asignamos a cada avion su pista correspondiente

					// No hacemos con los id de los aviones
					String[] idAviones = aviones.keySet().toArray(new String[aviones.keySet().size()]);
					for (int i = 0; i < idAviones.length; i++) {
						idAvion = idAviones[i];

						// Avion el cual estamos tratando actualmente y que sera
						// posiblemente modficados
						Avion avionModificado = null;
						if (idAvion.isEmpty())
							throw new IllegalArgumentException(
									"Error: no existe o no se pudo acceder al id de este avion");
						else
							avionModificado = aviones.get(idAvion);

						// Vector director final del avion
						Vector vectorDirectorFinal = avionModificado.getVectorDirector();
						System.out.println(idAvion + " - " + "Vector inicial del avion: " + vectorDirectorFinal);

						// ===============================
						// ALGORITMO 1: ESTABLECIENDO RUTA PARA ATERRIZAR
						// ===============================
						// Obtenemos la pista mas cercana
						Pista mejorPista = pistasAsignadas.get(idAvion);
						double bestDistance = Double.MAX_VALUE;
						for (Pista pista : pistas) {
							Vector entrada = new Vector(pista.getCoordenadaX(), pista.getCoordenadaY(),
									pista.getCoordenadaZ());
							double auxDistance = Vector.distance(entrada, avionModificado.getPosicionActual());
							if (auxDistance < bestDistance) {
								bestDistance = auxDistance;
								mejorPista = pista;
							}
						}
						// System.out.println(idAvion + " - " + "MEJOR PISTA: " +mejorPista);

						// Entrada de la pista a la que se dirige este avion
						Vector posEntradaPista = new Vector(mejorPista.getEntradaX(), mejorPista.getEntradaY(),
								mejorPista.getCoordenadaZ());

						// Posicion de la pista a la que se dirige este avion
						Vector posMejorPista = new Vector(mejorPista.getCoordenadaX(), mejorPista.getCoordenadaY(),
								mejorPista.getCoordenadaZ());

						// Si su posicion actual es la pista de aterrizaje, lo
						// eliminamos del mapa
						boolean eliminarAvion = false;

						// El avion puede estar en la pista, estar parado, pero aun puede no ser eliminado
						// ya que no han pasado los timeStep necesarios para retirar ese avion de la pista
						boolean avionParado = false;

						// Confirmamos si el avión ha pasado por el punto de entrada de la pista
						if (avionModificado.getPosicionActual().equals(posEntradaPista)) {
							System.out.println(idAvion + " - " + "I WANT TO BE FREEE!!!!");
							avionesPorPuntoEntrada.replace(avionModificado.getID(), true);
						}

						// Establecemos que su vector director final es el punto de entrada de la pista mas cercana
						if (!avionesPorPuntoEntrada.get(avionModificado.getID())) {
							vectorDirectorFinal = getBestDirectorVectorToPosition(avionModificado.getPosicionActual(),
									posEntradaPista);
							System.out.println(idAvion + " - " + "Al dirigirse a la entrada: " + posEntradaPista);
						} else {

							// Si su posicion actual es el punto de entrada, le indicamos qeu su vector director es hacia la pista
							System.out.println(idAvion + " - " + "MODIFICADO VECTOR DIRECTOR...");
							vectorDirectorFinal = getBestDirectorVectorToPosition(avionModificado.getPosicionActual(),
									posMejorPista);

							if (avionModificado.getPosicionActual().equals(posMejorPista)) {
								Integer stepsEsperando = stepsDeEspera.get(idAvion);
								if (stepsEsperando != null) {
									if (stepsEsperando == mejorPista.getStepsAterrizaje()) {
										// mando mensaje para que se elimine este avion
										eliminarAvion = true;
										aviones.remove(idAvion);
										avionesPorPuntoEntrada.remove(idAvion);
									} else {
										avionParado = true;
										stepsDeEspera.replace(idAvion, stepsEsperando + 1);
									}
								} else {
									stepsDeEspera.put(idAvion, 1);
								}
							}
						}

						// ===============================
						// ALGORITMO 2: EVITAR COLISIONES
						// ===============================
						String content = "OPERACION (id del avion) (empty order)";
						content = "0 MOD " + idAvion + " " + vectorDirectorFinal;
						if (avionParado) {
							// ===============================
							// Envio la informacion al Almacen
							// ===============================
							content = "0 MOD " + idAvion + " " + (new Vector(0, 0, 0));
						} else if (!eliminarAvion) {
							if (true) {
								// Vectores directores a los que no podemos
								// dirigirnos
								List<Vector> vectoresDirectoresRestringidos = new LinkedList<Vector>();
								List<Vector> posiblesVectoresDirectores = obtenerPosiblesVectoresDirectores();

								// Posicion siguiente en la que se encontraría
								// el
								// avion si siguiera la ruta fijada
								Vector avionModificadoPosSiguiente = Vector.sum(avionModificado.getPosicionActual(),
										avionModificado.getVectorDirector());

								// Vemos las colisiones con los otros aviones
								for (String key : aviones.keySet()) {
									// Compruebo posibles colisiones del avion
									// con
									// otros
									// aviones
									if (!key.equals(idAvion)) {
										// Me hago con el avion
										Avion otroAvion = aviones.get(key);

										if (entranEnAreaDeRiesgoDeColision(otroAvion, avionModificado)) {
											// Añado a vectores directores
											// restringidos
											// elvector director actual
											vectoresDirectoresRestringidos.add(avionModificado.getVectorDirector());

											// Elegir la mejor accion para
											// evitar
											// dicha
											// colision
											for (Vector posibleVector : posiblesVectoresDirectores) {
												if (!vectoresDirectoresRestringidos.contains(posibleVector)) {
													// Consigo la nueva posicion
													// usando
													// el nuevo vector director
													avionModificado.setVectorDirector(posibleVector);

													// Compruebo que no haya
													// chocque
													if (!entranEnAreaDeRiesgoDeColision(otroAvion, avionModificado)) {
														System.out.println(
																idAvion + " - " + "Vector final: " + posibleVector);
														vectorDirectorFinal = posibleVector;
														break;
													} else {
														System.out.println(idAvion + " - " + "Vector descartadp: "
																+ posibleVector);
														vectoresDirectoresRestringidos
																.add(avionModificado.getVectorDirector());
													}
												}
											}

											System.out.println(idAvion + " - " + "Vector evitar colision del avion: "
													+ vectorDirectorFinal);
										} else {
											// Siga su rumbo, cambio y corto
										}
									}
								}
								avionModificado.setVectorDirector(vectorDirectorFinal);

								// ===============================
								// ALGORITMO 3: LIMITES DEL MAPA
								// ===============================
								if (true) {
									Vector mapaDimensiones = getDimensionesDelMapa();
									avionModificadoPosSiguiente = Vector.sum(avionModificado.getPosicionActual(),
											avionModificado.getVectorDirector());

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

							}

							// Establecemos en el mapa el vector director del
							// avion
							// que
							// estamos tratando
							avionModificado.setVectorDirector(vectorDirectorFinal);
							aviones.replace(avionModificado.getID(), avionModificado);

							// ===============================
							// Envio la informacion al Almacen
							// ===============================
							content = "0 MOD " + idAvion + " " + vectorDirectorFinal;
						} else {
							// ===============================
							// Envio la informacion al Almacen
							// ===============================
							System.out.println("====================");
							System.out.println(idAvion + " - " + "Eliminar avion!!!!!");
							System.out.println("====================");
							content = "0 REM " + idAvion + " " + " ";
						}

						// Se informa al agente central
						informarAlmacenInformacion(idAvion, content);
					}

					// Actualizo contador
					contadorMensajes = 0;
				}
			}

		};

		addBehaviour(almInformacion);
	}

	/**
	 * Devuelve el vector director al que se tendría que dirigirse desde posOrigen a posDestino
	 * 
	 * @param pos
	 * @return
	 */
	private Vector getBestDirectorVectorToPosition(Vector posOrigen, Vector posDestino) {
		// TODO Auto-generated method stub
		return new Vector(posDestino.x == posOrigen.x ? 0 : (posDestino.x > posOrigen.x ? 1 : -1),
				posDestino.y == posOrigen.y ? 0 : (posDestino.y > posOrigen.y ? 1 : -1), 0);
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

			avionesPorPuntoEntrada.put(words[2], false);
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

	/**
	 * Detecta si dos aviones entran en area de riesgo de colision
	 * 
	 * @param avion1
	 * @param avion2
	 * @return
	 */
	public boolean entranEnAreaDeRiesgoDeColision(Avion avion1, Avion avion2) {
		Vector avion1PosActual = avion1.getPosicionActual();
		Vector avion2PosActual = avion2.getPosicionActual();

		// Me hago con la posicion siguiente
		Vector avion1PosSiguiente = Vector.sum(avion1.getPosicionActual(), avion1.getVectorDirector());
		Vector avion2PosSiguiente = Vector.sum(avion2.getPosicionActual(), avion2.getVectorDirector());

		double avion1AvgPosX = (avion1PosActual.x + avion1PosSiguiente.x) / 2.0;
		double avion1AvgPosY = (avion1PosActual.y + avion1PosSiguiente.y) / 2.0;

		double avion2AvgPosX = (avion2PosActual.x + avion2PosSiguiente.x) / 2.0;
		double avion2AvgPosY = (avion2PosActual.y + avion2PosSiguiente.y) / 2.0;

		System.out.println("Avion 1 pos: " + avion1PosActual + " > " + avion1PosSiguiente);
		System.out.println("Avion 2 pos: " + avion2PosActual + " > " + avion2PosSiguiente);
		System.out.println("Avion 1 avg pos:(" + avion1AvgPosX + "," + avion1AvgPosY + ")");
		System.out.println("Avion 2 avg pos:(" + avion2AvgPosX + "," + avion2AvgPosY + ")");

		if (avion1PosSiguiente.equals(avion2PosSiguiente)
				|| (avion1AvgPosX == avion2AvgPosX && avion1AvgPosY == avion2AvgPosY)) {
			System.out.println("Se entra en riesgo de colision");
			return true;
		} else {
			System.out.println("No se entra en riesgo de colision");
			return false;
		}
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
