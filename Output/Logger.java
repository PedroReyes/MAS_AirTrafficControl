/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author Javier Moreno
 */
public class Logger extends Agent {

	static String info = "";
	static String path = System.getProperty("os.name").equalsIgnoreCase("Mac OS X")
			? "/Users/pedro/Dropbox/Vida_laboral/Master/ProgramacionSistemasMultiagentes/TrabajoSegundaParte/MAS_AirTrafficControl/Output/log.txt"
			: "javi";

	@Override
	public void setup() {

		MessageTemplate mAlmacenDInf = MessageTemplate.MatchSender(new AID("adi", AID.ISLOCALNAME));

		addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(mAlmacenDInf);
				if (msg != null) {
					info = info + msg.getContent();
					try {
						Files.write(Paths.get(Logger.path), info.getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					block();
				}
			}
		});
	}
}
