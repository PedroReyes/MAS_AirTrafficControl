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
class ReceiverBehaviourReceivePing extends SimpleBehaviour{
	boolean finished = false;
	MessageManager messageHandler;

	public ReceiverBehaviourReceivePing(Agent a) {
		super(a);
		messageHandler=new MessageManager(myAgent);
	}

	/**
	 * this is the method that defines what the behaviour does
	 * it listenes to messages with the conversation id "mesaj" and replies with the message "Pong"
	 */
	public void action() {
		ACLMessage msg = myAgent.receive(MessageTemplate.MatchConversationId("mesaj"));
		if (msg != null) {//sometimes the message queue might be empty for example the first time this behavior runs

			System.out.println("Receiver: I am  " + myAgent.getLocalName()
					+ " and I have received: " + msg.getContent() +" from "+msg.getSender().getLocalName());


			//now we send a reply cause we are polite
			messageHandler.SendReply(msg,"Pong");

		}
		else{
			block();//free the thread until a new message appears in the message queue
		}
	}

	@Override
	public boolean done() {//when this method returns true the behavior is no longer activated when a message is received
		return finished;
	}

}