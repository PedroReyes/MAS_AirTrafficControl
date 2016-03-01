/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sistema;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.awt.Point;
import java.util.Vector;

/**
 *
 * @author pedro
 */
public class Avion extends Agent{
    private Vector<Integer> vectorDirector;
    private Point posicionActual;
    private int timeStep;
    
    public Avion() {
        
    }
    
    public Avion(Vector<Integer> vector, Point posicion, int time) {
        this.vectorDirector = vector;
        this.posicionActual = posicion;
        this.timeStep = time;
    }
    
    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public void setVectorDirector(Vector<Integer> vector) {
        this.vectorDirector = vector;
    }
    
    public Vector<Integer> getVectorDirector() {
        return this.vectorDirector;
    }
    
    public void setPosicionActual(Point posicion) {
        this.posicionActual = posicion;
    }
    
    public Point getPosicionActual() {
        return this.posicionActual;
    }
    
    public void setTimeStep(int time) {
        this.timeStep = time;
    }
    
    public int getTimeStep() {
        return this.timeStep;
    }
    
    public void actualizarInformacion(){
        
    }
    
    public void actualizarPosicion(){
        
    }
    
    public void mandarNuevaPosicion(String receiver){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("");
        msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
        send(msg);
    }
}
