// package byzcast;

// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import org.javatuples.Pair;
// import util.ArgsParser;
// import util.FileManager;
// import base.Host;
// import base.Node;
// import byzcast.messages.ByzCastMessage;
// import byzcast.messages.ByzCastMessage.Split;
// import byzcast.messages.LightMessage;
// import byzcast.messages.LightMessagesList;
// import byzcast.proxies.ByzCastServerProxy;

// public class ByzCastNode extends ByzCastServerProxy {
//     protected int numNodes;
//     protected FileManager files;
//     private LightMessagesList history = new LightMessagesList();
//     private List<Node> children = new ArrayList<>();
//     private List<Node> connected = new ArrayList<>();
//     private ArrayList<String[]> mappings = new ArrayList<>();
//     private int msgsTotal=0, msgsToMe=0, testO=0, testP=0;

//     public ByzCastNode(short id, ArgsParser args){
//         super(id, args.getClientCount());
//         this.files = new FileManager();
//         List<Node> nodes = files.loadHosts();
//         numNodes = nodes.size();
//         Host thisHost = null;
//         for(Node n : nodes){
//             if(n.getId() == id){
//                 thisHost = n.getHost();
//                 break;
//             }
//         }
//         setHost(thisHost);
//         print(this, "ByzCast Node - Start listening ...");
//         // sets connection to all children nodes
//         for(Pair<Short, Short> p : files.loadByzCastTree(mappings, getId())){
//             if (p.getValue0() == getId()){
//                 for(Node node : nodes){
//                     if(node.getId() < getId()){
//                         connectTo(node);
//                         connected.add(node);
//                         if(p.getValue1() == node.getId()){
//                             children.add(node);
//                         }
//                     }
//                 }
//             }        
//         }
//     }

//     @Override
//     protected void receiveMsg(ByzCastMessage m){
//         // print("Received message", m);
//         ByzCastMessage[] newMsgs;

//         msgsTotal++;
//         if(m.isAddressedTo(getId())) msgsToMe++;

//         Set<Short> sent = new HashSet<>();

//         //Fazer uma segunda estrutura que liga o nó com todos em baixo na árvore
//         // for(Node node : nodes){
//         //     if(node.getId() > getId()){
//         //         connectTo(node);
//         //     }
//         // }

//         //Conferir se é o LCA
//         //if (getId() == m.getLca() && test == 0){
//         //     print("It works");
//         //     test++;
//         // }
//         // else{
//         //     if(test == 0){
//         //         print(String.valueOf(getId()));
//         //         print(String.valueOf(m.getLca()));
//         //         test++;
//         //     }
//         // }

//         if (getId() == m.getLca()){
//             //Caso seja, separa payload de msg Ordem
//             // separar a msg na normal e em uma que é só o payload (sabe o id da de Ordem) -> randPayload
//             newMsgs = m.splitSelf(m);

//             //Encaminha msg DEST para os destinos conforme a árvore
//             for(Node n : connected){
//                 if(m.isAddressedTo(n.getId())){
//                     send(newMsgs[1], n.getId());
//                     sent.add(n.getId());
//                 }
//             }

//             //Mensagem de Oredm vai só para os filhos
//             // for(String[] map : mappings){
//             //     if(m.isAddressedTo(Short.valueOf(map[1])) && !sent.contains(Short.valueOf(map[2]))){
//             //         // print("Will send to child",Short.valueOf(map[2]), "via mapping, for node", Short.valueOf(map[1]));
//             //         send(newMsgs[0], Short.valueOf(map[2]));
//             //         sent.add(Short.valueOf(map[2]));
//             //     }
//             // }
//         }

//         //Mensagens de ordem espera a payload para entregar caso seja destino
//         // if msg.type == Order and if(m.isAddressedTo(getId())){
//         // guardar na estrutura de ordem FIFO uma msg.type == Payload
//         // quando chega msg nova de Payload, que equivale ao primeiro do fifo,
//         // entrega a mensagem e confere se o próximo na fila já tem payload armazernado

//         // NOTA: as mensagens de payload podem ver de qualquer ordem, mas as de ordem
//         // tem que chegar em ordem


//         //Mensagens de payload aguarda a mensagem de ordem para entregar caso seja destino
//         // if msg.type == Payload {
//         // guardar na estrutura de List uma msg.type == Order
//         // quando chega msg nova de Order, confere se tem a outra metade na memória

//         //======================================================
//         if(m.getSplit() == Split.ORD){
//             if(testO < 10){
//                 print("Got my Order Split from");
//                 print(m.getId());
//                 print(m.getLca());
//                 print(m.getDst());
//                 testO++;
//             }
//             // send to its children
//             for(Node n : children){
//                 if(m.isAddressedTo(n.getId())){
//                     // print("Will send to child", n.getId());
//                     send(m, n.getId());
//                     sent.add(n.getId());
//                 }
//             }

//             // for each mapping, send to the children in the mapping, if not sent yet
//             for(String[] map : mappings){
//                 if(m.isAddressedTo(Short.valueOf(map[1])) && !sent.contains(Short.valueOf(map[2]))){
//                     // print("Will send to child",Short.valueOf(map[2]), "via mapping, for node", Short.valueOf(map[1]));
//                     send(m, Short.valueOf(map[2]));
//                     sent.add(Short.valueOf(map[2]));
//                 }
//             }
//         }

//         if(m.getSplit() == Split.PAY && testP < 10){
//             print("Got my Payload split from");
//             print(m.getId());
//             print(m.getLca());
//             print(m.getDst());
//             testP++;
//         }

//         if(m.isAddressedTo(getId())){
//             deliver(m);
//             // print("Delivered message", m);
//         }
//     }

//     private void deliver(ByzCastMessage m) {
//         history.add(new LightMessage(m.getId(), m.getDst()));
//         sendReply(m);
//         // print("Delivered message", m);
//     }

//     protected void finish(){
//         if(bufferQueue.size() > 0){
//             print("Queue is not empty !!! ");
//             files.stop();
//             exit();
//         }
//         print("Queue is empty ! =]");
//         files.persistMessages(history, getId(), false, false);
//         print("-------------------------------------");
//         print("Total msgs in the history:", history.size());
//         print("Total local msgs received:", localMsgs);
//         print("Total msgs received:", msgsTotal);
//         print("Total msgs to me received:", msgsToMe);
//         print("% of overhead:", 100-((msgsToMe*100)/msgsTotal));
//         //printF("Avg msg size", Stats.of(getSizes()).mean());
//         files.persistMsgSizes(getSizes(), getId());
//         print("-------------------------------------");
//         files.nodeFinished(getId());
//         exit();
//     }
// }
