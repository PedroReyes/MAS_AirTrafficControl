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
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.List;
import java.util.Map;

/**
 * @author Javier Moreno
 */
public class ControlTemporal extends Agent {

    private int timeStep;
    private Escenario escenario;
    private ContainerController home;

    // =========================================================================
    // AGENT
    // =========================================================================
    @Override
    protected void setup() {
        Object[] args = getArguments();
        setEscenario((Escenario) args[0]);
        setContainer((ContainerController) args[1]);

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                // Crear todos los agentes necesarios
                try {
                    // Crear ATC
                    AgentController a = home.createNewAgent("atc", Sistema.ATC.class.getName(), new Object[0]);
                    a.start();
                    
                    // Crear AlmacenDeInformacion
                    a = home.createNewAgent("adi", Sistema.AlmacenDeInformacion.class.getName(), new Object[0]);
                    a.start();
                    
                    // Crear InterfazGrafica
                    a = home.createNewAgent("graph", Output.InterfazGrafica.class.getName(), new Object[0]);
                    a.start();
                    
                    // Crear Logger
                    a = home.createNewAgent("log", Output.Logger.class.getName(), new Object[0]);
                    a.start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
        });

        TickerBehaviour controlTemp;
        controlTemp = new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                timeStep = getTickCount();

                // Miro en el escenario si tengo que generar nuevos aviones en este tick
                Map<Integer, List<Avion>> simulacion = escenario.getEntradaSimuladaAviones();
                List<Avion> aviones = simulacion.get(timeStep);

                // Si la lista no es vacia, inicializo los aviones
                if (!aviones.isEmpty()) {
                    aviones.stream().forEach((avion) -> {
                        inicializacionAgentes(avion);
                    });
                }

                //Mando nuevo timeStep
                System.out.println("Agent " + myAgent.getLocalName() + ": tick=" + timeStep);
                mandarMensajes();
            }
        };

        addBehaviour(controlTemp);
    }

    // =========================================================================
    // AUXILIARY METHODS
    // =========================================================================
    public void mandarMensajes() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(String.valueOf(timeStep));
        msg.addReceiver(new AID("todos los receptores. HUM!!", AID.ISLOCALNAME));
        send(msg);

        /*Se puede hacer por broadcast
          Lo que habria que hacer es poner un template para que
          aquellos que reciben mensajes pero no de este, tal como
          AlmacenDeInformacion o InterfazGrafica, no los tengan en cuenta
         */
    }

    public void inicializacionAgentes(Avion agente) {
        Object[] args = new Object[5];
        args[0] = agente.getID();
        args[1] = agente.getPosicionActual();
        args[2] = agente.getCombustibleActual();
        args[3] = agente.getCombustibleXStep();
        args[4] = timeStep;

        try {
            AgentController a = home.createNewAgent(args[0].toString(), Avion.class.getName(), args);
            a.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // ToString
    // =========================================================================
    @Override
    public String toString() {
        return "ControlTemporal: [" + getEscenario()
                + "," + getTimeStep() + "]\n";
    }

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

    public void setContainer(ContainerController cc) {
        this.home = cc;
    }

    public ContainerController getContainer() {
        return this.home;
    }
}
