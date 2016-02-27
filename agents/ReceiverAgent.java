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

        addBehaviour(new ReceiverBehaviourReceivePing(this));

    }
}
