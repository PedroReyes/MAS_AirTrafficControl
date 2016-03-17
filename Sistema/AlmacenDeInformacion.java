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
    	aviones = new HashMap<String, Avion>();
        CyclicBehaviour almInformacion = new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    //System.out.println("ADI: Recibe: " + msg.getContent());
                    actualizarInformacion(msg.getContent());
                    for (Iterator iter = msg.getAllReplyTo(); iter.hasNext();) {
                    	Object obj = iter.next();
                        //System.out.println(obj);
                        informar((AID) obj, msg.getContent());
                    }
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
        String words[] = content.split(" ");
        switch (words[1]) {
            case "ADD":
                String vectorADD[] = words[3].split(",");
                aviones.put(words[2], new Avion(words[2], new Vector(Integer.parseInt(vectorADD[0].substring(9)), Integer.parseInt(vectorADD[1].substring(2)), 0), Double.parseDouble(words[4]), Double.parseDouble(words[5])));
                break;
            case "REM":
                aviones.remove(words[2]);
                break;
            case "MOD":
                String vectorMOD[] = words[3].split(",");
                Avion aux = aviones.get(words[2]);
                if(words.length == 4){ //Mensaje de ATC
                    aux.setVectorDirector(new Vector(Integer.parseInt(vectorMOD[0].substring(9)), Integer.parseInt(vectorMOD[1].substring(2)), 0));
                } else { //Mensaje de Avion
                    aux.setPosicionActual(new Vector(Integer.parseInt(vectorMOD[0].substring(9)), Integer.parseInt(vectorMOD[1].substring(2)), 0));
                    aux.setCombustibleActual(Double.parseDouble(words[4]));
                    aux.setCombustibleXStep(Double.parseDouble(words[5]));
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
