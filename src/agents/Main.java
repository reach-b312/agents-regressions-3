package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import regressions.Dataset;
public class Main extends Agent {
    private AID[] agentesProveedores;
    String tipoServicio = null;
    protected void setup() {
        System.out.println("Agente "+getLocalName()+" iniciado.");

        if (Files.exists(Paths.get("dataset.csv"))) {

            tipoServicio = determinarTipoServicio("dataset.csv");
            addBehaviour(new TickerBehaviour(this, 3000) {

                protected void onTick(){
                    System.out.println("Intentando conectar con el proveedor de servicios de"+tipoServicio);
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType(tipoServicio);
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println("Se encontraron "+result.length+" agentes que ofrecen el servicio de "+tipoServicio);
                        agentesProveedores = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            agentesProveedores[i] = result[i].getName();
                            System.out.println(agentesProveedores[i].getName());
                        }
                    }
                    catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    myAgent.addBehaviour(new RequestPerformer());
                }
            });
        }
        else {
            System.out.println("No se encontró el archivo dataset.csv");
            doDelete();
        }

    }

    private class RequestPerformer extends Behaviour {
        private int step = 0;
        private ACLMessage msg;

        public void action() {
            switch (step) {
                case 0:
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(agentesProveedores[0]);
                    msg.setContent("Solicitud de servicio de regresión: " + tipoServicio);

                    msg.setConversationId("regresion");
                    msg.setReplyWith("request" + System.currentTimeMillis());
                    myAgent.send(msg);
                    step = 1;
                    break;
                case 1:
                    // Recibir respuesta de los agentes proveedores
                    ACLMessage reply = myAgent.receive();
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // El agente proveedor ha enviado el resultado
                            System.out.println("Resultado recibido de " + reply.getSender().getName() + ": " + reply.getContent());
                            step = 2;
                        } else {
                            System.out.println("El agente proveedor " + reply.getSender().getName() + " no pudo completar la solicitud");
                            step = 4;
                        }
                    } else {
                        block();
                    }
                    break;
                //case 2
                case 2:
                    File datasetFile = new File("dataset.csv");
                    Dataset genericModel = new Dataset(datasetFile, tipoServicio);
                    // Enviar el dataset al agente proveedor
                    ACLMessage datos = new ACLMessage(ACLMessage.REQUEST);
                    datos.addReceiver(agentesProveedores[0]);
                    try {
                        datos.setContentObject(genericModel);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    datos.setConversationId("dataset");
                    datos.setReplyWith("request" + System.currentTimeMillis());
                    myAgent.send(datos);
                    step = 3;
                    break;
                //case 3 recibe la respuesta del agente
                case 3:
                    ACLMessage weights = myAgent.receive();
                    if (weights != null) {
                        if (weights.getPerformative() == ACLMessage.INFORM) {
                            // El agente proveedor ha enviado el resultado
                            System.out.println("Resultado recibido de " + weights.getSender().getName() + ": " + weights.getContent());
                            step = 4;
                        } else {
                            System.out.println("El agente proveedor " + weights.getSender().getName() + " no pudo completar la solicitud");
                            step = 4;
                        }
                    } else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            return (step == 4);
        }
        public int onEnd() {
            myAgent.removeBehaviour(this);
            return super.onEnd();
        }

    }

    protected void takeDown() {
        System.out.println("Agente "+getLocalName()+" terminado.");
    }

    private String determinarTipoServicio(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line = br.readLine(); // Leer la primera línea para obtener los encabezados
            int numColumns = line.split(",").length;
            boolean isCategorical = false;
            boolean isPolynomial = false;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // Verificar si la última columna es categórica
                if (data[data.length - 1].matches("0|1")) {
                    isCategorical = true;
                    break;
                }
                // Verificar si la última columna es un número discreto (grado del polinomio)
                try {
                    int degree = Integer.parseInt(data[data.length - 1]);
                    if (degree > 0 && degree < 10) {
                        isPolynomial = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                    // No es un número, continuar con la siguiente línea
                }
            }

            if (isCategorical) {
                return "LOG"; // Regresión Logística
            } else if (isPolynomial) {
                return "POLY"; // Regresión Polinomial
            } else if (numColumns == 2) {
                return "SLR"; // Regresión Lineal Simple
            } else {
                return "MLR"; // Regresión Lineal Múltiple
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // En caso de error
    }




}
