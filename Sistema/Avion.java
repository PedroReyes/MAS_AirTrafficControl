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
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author Javier Moreno
 */
public class Avion extends Agent {

    private String id;
    private Vector vectorDirector;
    private Vector posicionActual;
    private double combustibleActual;
    private double combustibleXStep;
    private int timeStep;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================
    public Avion() {

    }

    public Avion(String id, Vector posicion, int comb, double combXStep) {
        this.id = id;
        this.posicionActual = posicion;
        this.combustibleActual = comb;
        this.combustibleXStep = combXStep;
    }

    public Avion(String id, Vector vector, Vector posicion, double comb, double combXStep, int time) {
        this.id = id;
        this.vectorDirector = vector;
        this.posicionActual = posicion;
        this.combustibleActual = comb;
        this.combustibleXStep = combXStep;
        this.timeStep = time;
    }

    // =========================================================================
    // AGENT
    // =========================================================================
    @Override
    protected void setup() {
        Object[] args = getArguments();
        setID((String) args[0]);
        setPosicionActual((Vector) args[1]);
        setCombustibleActual((int) args[2]);
        setCombustibleXStep((double) args[3]);
        setTimeStep((int) args[4]);

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("ADD " + getID() + " " + getPosicionActual() + " " + getCombustibleActual() + " " + getCombustibleXStep());
        msg.addReceiver(new AID("adi", AID.ISLOCALNAME));
        send(msg);

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Primero se bloquea esperando un mensaje de ControlTemporal
                addBehaviour(new ReceiveBehaviour(this.myAgent, "temporal"));
                
                // Actualiza su posicion y combustible
                actualizarPosicion();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("MOD " + getID() + " " + getPosicionActual() + " " + getCombustibleActual() + " " + getCombustibleXStep());
                msg.addReceiver(new AID("adi", AID.ISLOCALNAME));
                send(msg);

                // Espera una correccion de vuelo
                addBehaviour(new ReceiveBehaviour(this.myAgent, "adi"));
            }
        }
        );
    }

    // =========================================================================
    // AUXILIARY BEHAVIOUR
    // =========================================================================
    class ReceiveBehaviour extends OneShotBehaviour {
        private final Agent padre;
        private final String sender;

        public ReceiveBehaviour(Agent avion, String sender) {
            this.padre = avion;
            this.sender = sender;
        }

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchSender(new AID(sender, AID.ISLOCALNAME));
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                if (sender.equals("temporal")){
                    ((Avion) padre).setTimeStep(Integer.parseInt(msg.getContent()));  
                } else {
                    ((Avion) padre).actualizarInformacion(msg.getContent());
                }
            } else {
                block();
            }
        }
    }
    
    // =========================================================================
    // AUXILIARY METHODS
    // =========================================================================
    public void actualizarInformacion(String contenido) {

    }

    public void actualizarPosicion() {
        this.posicionActual.x = this.posicionActual.x + this.vectorDirector.x;
        this.posicionActual.y = this.posicionActual.y + this.vectorDirector.y;

        this.combustibleActual = this.combustibleActual - this.combustibleXStep;
    }

    public void mandarNuevaPosicion(String receiver) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("");
        msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
        send(msg);
    }

    // =========================================================================
    // ToString
    // =========================================================================
    @Override
    public String toString() {
        return "Avion: [" + getID()
                + "," + getVectorDirector()
                + "," + getPosicionActual()
                + "," + getCombustibleActual()
                + "," + getCombustibleXStep()
                + "," + getTimeStep() + "]\n";
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return this.id;
    }

    public void setVectorDirector(Vector vector) {
        this.vectorDirector = vector;
    }

    public Vector getVectorDirector() {
        return this.vectorDirector;
    }

    public void setPosicionActual(Vector posicion) {
        this.posicionActual = posicion;
    }

    public Vector getPosicionActual() {
        return this.posicionActual;
    }

    public void setCombustibleActual(double comb) {
        this.combustibleActual = comb;
    }

    public double getCombustibleActual() {
        return this.combustibleActual;
    }

    public void setCombustibleXStep(double combXStep) {
        this.combustibleXStep = combXStep;
    }

    public double getCombustibleXStep() {
        return this.combustibleXStep;
    }

    public void setTimeStep(int time) {
        this.timeStep = time;
    }

    public int getTimeStep() {
        return this.timeStep;
    }
}
