package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;

/**
 * This agent should be named a2 it listenes to messages with the conversation
 * id "mesaj"
 *
 * @author Sorin
 *
 */
public class ReceiverAgent extends Agent {

    protected void setup() {
        Object[] args = getArguments();
        String id = (String) args[0];
        double combustible = (double) args[1];
        String accion = (String) args[2];
        double combustibleXStep = (double) args[3];
        
        System.out.println("Argumentos:");
        System.out.println(id+" -- "+combustible+" -- "+accion+" -- "+combustibleXStep);
        
        addBehaviour(new ReceiverBehaviourReceivePing(this));

    }
}
