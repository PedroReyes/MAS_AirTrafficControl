/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Input;

import Auxiliar.Escenario;
import Sistema.Avion;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.List;
import java.util.Map;
/**
 *
 * @author pedro
 */
public class ControlTemporal extends Agent{
    private int timeStep;
    private Escenario escenario;

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public void setTimeStep(int time) {
        this.timeStep = time;
    }
    
    public int getTimeStep() {
        return this.timeStep;
    }
    
    public void setEscenario(Escenario escenario) {
        this.escenario = escenario;
    }
    
    public Escenario getEscenario() {
        return this.escenario;
    }
    
    public void mandarMensajes() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(String.valueOf(timeStep));
        msg.addReceiver(new AID("todos los receptores. HUM!!", AID.ISLOCALNAME));
        send(msg);
    }

    public void inicializacionAgentes(Agent agente) {
        if(agente instanceof Sistema.Avion) {
            
        }
    }

    @Override
    protected void setup() {
        //Leo el escenario y me cojo el map
        
        TickerBehaviour controlTemp;
        controlTemp = new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                timeStep = getTickCount();
                
                // Miro en el escenario si tengo que generar nuevos aviones en este tick
                Map<Integer, List<Avion>> simulacion = escenario.getEntradaSimuladaAviones();
                List<Avion> aviones = simulacion.get(timeStep);
                
                // Si la lista no es vacia, inicializo los aviones
                if(!aviones.isEmpty()){
                    aviones.stream().forEach((avion) -> {
                        inicializacionAgentes(avion);
                    });
                }
                //inicializacionAgentes();
                
                //Mando nuevo timeStep
                System.out.println("Agent " + myAgent.getLocalName() + ": tick=" + timeStep);
                mandarMensajes();
                
            }
        };

        addBehaviour(controlTemp);
    }
    
    @Override
    public String toString(){
      return "ControlTemporal: ["+getEscenario()+
           ","+getTimeStep()+"]\n";
    }
}
