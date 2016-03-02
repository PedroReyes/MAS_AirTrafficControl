/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sistema;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Javier Moreno
 */
public class AlmacenDeInformacion extends Agent{
    private List<Avion> aviones;
    private List<Boolean> modificado;
    
    // =========================================================================
    // AGENT
    // =========================================================================
    @Override
    public void setup() {
        SimpleBehaviour almInformacion = new SimpleBehaviour(this) {
            boolean finished = false;
            int state = 0;

            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Recibido nuevo mensaje"+msg.getContent());
                    
                    switch (msg.getContent()) {
                        case "ADD":
                            actualizarInformacion();
                            informar("ATC/Aviones");
                            informar("Logger");
                            break;
                        case "REM":
                            actualizarInformacion();
                            informar("ATC/Aviones");
                            informar("Logger");
                            break;
                        case "MOD":
                            actualizarInformacion();
                            informar("ATC/Aviones");
                            informar("Logger");
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.println("No recibo nada "+state);
                    //block();
                }
                state++;
            }

            @Override
            public boolean done() {
                return finished;
            }
        };

        addBehaviour(almInformacion);
    }
    
    // =========================================================================
    // ToString
    // =========================================================================
    @Override
    public String toString(){
      return "AlmacenDeInformacion: ["+
           " "+"]\n";
    }
    
    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public void setAviones(List<Avion> avion) {
        this.aviones = avion;
    }
    
    public List<Avion> getAviones() {
        return this.aviones;
    }
    
    public void actualizarInformacion(){
        
    }
    
    public void informar(String agente){
        
    }
}
