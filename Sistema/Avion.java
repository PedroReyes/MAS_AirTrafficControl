/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sistema;

import Auxiliar.Vector;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author pedro
 */
public class Avion extends Agent{
    private String id;
    private Vector vectorDirector;
    private Vector posicionActual;
    private int combustibleActual;
    private double combustibleXStep;
    private int timeStep;
    
    public Avion() {
        
    }
    
    public Avion(String id, Vector posicion, int comb, double combXStep) {
        this.id = id;
        this.posicionActual = posicion;
        this.combustibleActual = comb;
        this.combustibleXStep = combXStep;
    }
    
    public Avion(String id, Vector vector, Vector posicion, int comb, double combXStep, int time) {
        this.id = id;
        this.vectorDirector = vector;
        this.posicionActual = posicion;
        this.combustibleActual = comb;
        this.combustibleXStep = combXStep;
        this.timeStep = time;
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
    
    public void setCombustibleActual(int comb) {
        this.combustibleActual = comb;
    }
    
    public int getCombustibleActual() {
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
    
    @Override
    public String toString(){
      return "Avion: ["+getID()+
           ","+getVectorDirector()+
           ","+getPosicionActual()+
           ","+getCombustibleActual()+
           ","+getCombustibleXStep()+
           ","+getTimeStep()+"]\n"; 
    }
}
