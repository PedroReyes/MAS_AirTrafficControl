package ro.ucv.agents;

import ro.ucv.bootstrap.MessageManager;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * this is a one shot behavior it will run once and stop for all eternity
 */
class SenderBehaviourSendPing extends OneShotBehaviour{

	MessageManager messageHandler;

	public SenderBehaviourSendPing(Agent a){
		super(a);
		messageHandler=new MessageManager(myAgent);
	}
	
	
	/**
	 * this is the method that defines what the behaviour does
	 * it initiates a conversation with agent a2 by sending it the message "Ping" with the conversation id "mesaj"
	 */
	public void action() {		
		System.out.println("Sender: I am "+myAgent.getLocalName()+" and i am sending ping");
		messageHandler.SendMessage("a2","Ping","mesaj");
		//yes , I could have done this in the setup but I wanted to demonstrate OneShotBehavior	
	}


}
