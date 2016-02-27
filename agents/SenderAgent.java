package ro.ucv.agents;


import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.*;
/**
 * This agent should be named a1
 * it is hard-wired to send a ping message to an agent named a2 from the same platform
 * @author Sorin
 *
 */
public class SenderAgent extends Agent {

	

	protected void setup() { //this runs once before starting behaviors

		// First set-up answering behaviour
		addBehaviour(new SenderBehaviourReceiveAnswer(this) );

		// Send messages to "a2"
		addBehaviour(new SenderBehaviourSendPing(this));

		// orice alt cod care vreti sa se intample inainte sa porneasca behavior-urile

	}
}