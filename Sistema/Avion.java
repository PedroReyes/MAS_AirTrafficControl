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

    public Avion(String id, Vector posicion, double comb, double combXStep) {
        this.id = id;
        this.posicionActual = posicion;
        this.combustibleActual = comb;
        this.combustibleXStep = combXStep;
        this.vectorDirector = new Vector(1,-1,0);
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
        setCombustibleActual((double) args[2]);
        setCombustibleXStep((double) args[3]);
        setTimeStep((int) args[4]);

        MessageTemplate mControlTemp = MessageTemplate.MatchSender(new AID("tmp", AID.ISLOCALNAME));
        MessageTemplate mAlmacenDInf = MessageTemplate.MatchSender(new AID("adi", AID.ISLOCALNAME));

        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                mandarMensaje("ADD");
                
                // Espera una primera direccion de vuelo
                ACLMessage msg = myAgent.blockingReceive(mAlmacenDInf);
                actualizarInformacion(msg.getContent());
            }
        });

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Primero se bloquea esperando un mensaje de ControlTemporal
                ACLMessage msg = myAgent.blockingReceive(mControlTemp);
                setTimeStep(Integer.parseInt(msg.getContent()));
                
                // Actualiza su posicion y combustible
                actualizarPosicion();
                mandarMensaje("MOD");

                // Espera una correccion de vuelo
                msg = myAgent.blockingReceive(mAlmacenDInf);
                actualizarInformacion(msg.getContent());
            }
        }
        );
    }

    // =========================================================================
    // AUXILIARY METHODS
    // =========================================================================
    public void actualizarInformacion(String content) {
        System.out.println("AVN: Recibe: " + content);
    	String words[] = content.split(" ");
        switch (words[1]) {
            case "MOD":
                String vector[] = words[3].split(",");
                Vector aux = new Vector(Integer.parseInt(vector[0].substring(9)), Integer.parseInt(vector[1].substring(2)), 0);
                setVectorDirector(aux);
                break;
            case "REM":
                this.doDelete();
                break;
            default:
                break;
        }
    }

    public void actualizarPosicion() {
        this.posicionActual.x = this.posicionActual.x + this.vectorDirector.x;
        this.posicionActual.y = this.posicionActual.y + this.vectorDirector.y;

        this.combustibleActual = this.combustibleActual - this.combustibleXStep;
    }

    public void mandarMensaje(String orden) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(getTimeStep() + " " + orden + " " + getID() + " " + getPosicionActual() + " " + getCombustibleActual() + " " + getCombustibleXStep());
        msg.addReceiver(new AID("adi", AID.ISLOCALNAME));
        msg.addReplyTo(new AID("atc", AID.ISLOCALNAME));
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
