package ro.ucv.agents;

import ro.ucv.bootstrap.MessageManager;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * this is a simple behavior and it will run until it is done
 */
class SenderBehaviourReceiveAnswer extends SimpleBehaviour{
	boolean finished = false;
	MessageManager messageHandler;

	public SenderBehaviourReceiveAnswer(Agent a){
		super(a);
		messageHandler=new MessageManager(myAgent);

	}
	
	/**
	 * this is the method that defines what the behaviour does
	 * it listenes to messages with the conversation id "mesaj"
	 */
	public void action() {

		ACLMessage msg = myAgent.receive(MessageTemplate.MatchConversationId("mesaj"));

		if (msg != null) {//sometimes the message queue might be empty for example the first time this behavior runs

			System.out.println("Sender:I am "+myAgent.getLocalName()+ " and I received: "
					+ msg.getContent() + " from "
					+ msg.getSender().getLocalName());
			//if you want to do something when a command arrives do it here
		}
		else { 
			block();//free the thread until a new message appears in the message queue
		}
	}

	@Override
	public boolean done() {//when this method returns true the behavior is no longer activated when a message is received
		return finished;
	}

}
