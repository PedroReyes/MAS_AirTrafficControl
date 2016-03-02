/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Output;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @author Javier Moreno
 */
public class Logger extends Agent{
    private FileWriter fichero;
    private PrintWriter pw;

    @Override
    public void setup() {
        try {
            fichero = new FileWriter("log.txt");
            pw = new PrintWriter(fichero);
        } catch (Exception e) {}

        SimpleBehaviour logger = new SimpleBehaviour(this) {
            boolean finished = false;
            int state = 0;

            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    pw.println("Recibido nuevo mensaje"+msg.getContent());
                } else {
                    System.out.println("No recibo nada "+state);
                    //block();
                }
                state++;
            }

            @Override
            public boolean done() {
                try {
                    fichero.close();
                } catch (Exception e2) {}
                return finished;
            }
        };

        addBehaviour(logger);
    }
}
