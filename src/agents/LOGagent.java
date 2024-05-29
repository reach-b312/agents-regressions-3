package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.Arrays;

import jade.lang.acl.UnreadableException;
import regressions.Dataset;
import regressions.LogisticRegression; // Assuming you have this class implemented

public class LOGagent extends Agent {
    private AID[] agentesProveedores;
    String tipoServicio = "LOG";
    protected void setup() {
        System.out.println("Agente "+getLocalName()+" iniciado.");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipoServicio);
        sd.setName("JADE-regression-service");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new OfferRequestsServer());

        addBehaviour(new RegressionServer());
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Agente "+getLocalName()+" terminado.");
    }

    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Solicitud de servicio de regresión: " + tipoServicio + " recibida");
                    myAgent.send(reply);
                }
                else {
                    block();
                }
            }
        }
    }

    private class RegressionServer extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        Dataset dataset = (Dataset) msg.getContentObject();
                        LogisticRegression lr = new LogisticRegression(dataset);
                        double[] coeficientes = lr.fit(0.001, 0.1, 0);;
                        System.out.println("Coeficientes de la regresión logística:");
                        System.out.println(Arrays.toString(coeficientes));
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContentObject(coeficientes);
                        myAgent.send(reply);
                    }
                    catch (UnreadableException | IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    block();
                }
            }
        }
    }
}