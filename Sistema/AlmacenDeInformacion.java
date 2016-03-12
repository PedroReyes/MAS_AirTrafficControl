/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sistema;

import Auxiliar.Vector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Javier Moreno
 */
public class AlmacenDeInformacion extends Agent {

    private HashMap<String, Avion> aviones;

    // =========================================================================
    // AGENT
    // =========================================================================
    @Override
    public void setup() {
        CyclicBehaviour almInformacion = new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Recibido nuevo mensaje" + msg.getContent());
                    actualizarInformacion(msg.getContent());
                    for (Iterator iter = msg.getAllReplyTo(); iter.hasNext();) {
                        System.out.println(iter.next());
                        informar((AID) iter.next(), msg.getContent());
                    }
                } else {
                    System.out.println("No recibo nada");
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
                aviones.put(words[2], new Avion(words[2], new Vector(Integer.parseInt(words[3]), Integer.parseInt(words[4]), 0), Double.parseDouble(words[5]), Double.parseDouble(words[6])));
                break;
            case "REM":
                aviones.remove(words[2]);
                break;
            case "MOD":
                Avion aux = aviones.get(words[2]);
                if(words.length == 4){ //Mensaje de ATC
                    aux.setVectorDirector(new Vector(Integer.parseInt(words[3]), Integer.parseInt(words[4]), 0));
                } else { //Mensaje de Avion
                    aux.setPosicionActual(new Vector(Integer.parseInt(words[3]), Integer.parseInt(words[4]), 0));
                    aux.setCombustibleActual(Double.parseDouble(words[5]));
                    aux.setCombustibleXStep(Double.parseDouble(words[6]));
                }
                aviones.replace(words[2], aux);
                break;
            default:
                break;
        }
    }

    public void informar(AID agent, String content) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(content);
        msg.addReceiver(agent);
        msg.addReceiver(new AID("gph", AID.ISLOCALNAME));
        msg.addReceiver(new AID("log", AID.ISLOCALNAME));
        send(msg);
    }

    // =========================================================================
    // ToString
    // =========================================================================
    @Override
    public String toString() {
        return "AlmacenDeInformacion: ["
                + aviones.toString() + "]\n";
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
}
