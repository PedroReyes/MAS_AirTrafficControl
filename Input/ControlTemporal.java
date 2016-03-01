/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Input;

import Auxiliar.Escenario;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
/**
 *
 * @author pedro
 */
public class ControlTemporal extends Agent{
    private int timeStep;
    private Escenario escenario;

    public void setTimeStep(int time) {
        this.timeStep = time;
    }
    
    public void setEscenario(Escenario escenario) {
        this.escenario = escenario;
    }
    
    public void mandarMensajes() {

    }

    public void inicializacionAgentes(Agent agente) {
        if(agente instanceof Sistema.Avion) {
            
        }
    }

    @Override
    protected void setup() {
        //Leo el escenario y me cojo el map
        
        TickerBehaviour controlTemp = new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                //Miro en el map que tengo en este tick
                //Si tengo que generar nuevos aviones
                //inicializacionAgentes();
                
                //Mando nuevo timeStep
                timeStep = getTickCount();
                System.out.println("Agent " + myAgent.getLocalName() + ": tick=" + timeStep);
                mandarMensajes();
                
            }
        };

        addBehaviour(controlTemp);
    }
}
