/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Output;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @author Javier Moreno
 */
public class Logger extends Agent {

    private FileWriter fichero;
    private PrintWriter pw;

    @Override
    public void setup() {
        try {
            fichero = new FileWriter("log.txt");
            pw = new PrintWriter(fichero);
        } catch (Exception e) {
        }

        MessageTemplate mAlmacenDInf = MessageTemplate.MatchSender(new AID("adi", AID.ISLOCALNAME));

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(mAlmacenDInf);
                if (msg != null) {
                    pw.println(msg.getContent());
                } else {
                    block();
                }
            }
        });
    }
}
