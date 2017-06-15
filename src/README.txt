Files:
cs455/overlay/dijkstra/EdgeNode.java: Used to hold node and edge weight information in the routing cache
cs455/overlay/dijkstra/RoutingCache.java: Holds the paths to all nodes from source node
cs455/overlay/dijkstra/ShortestPath.java: Calculates the routing cache
cs455/overlay/node/MessagingNode.java: Messaging node that forms the network
cs455/overlay/node/NetworkNode.java: base class extended by MessagingNode and Register, contains all the networking code
cs455/overlay/node/Node.java: Interface used by the nodes
cs455/overlay/node/Registry.java: Registery which creates the overlay
cs455/overlay/transport/ConsoleThread.java: Reads input from the console and passes it to a node
cs455/overlay/transport/TCPReceiverThread.java: Receives data from other nodes and passes it to the event factory
cs455/overlay/transport/TCPSender.java: Senders data to other nodes
cs455/overlay/transport/TCPServerThread.java: Endpoint where other nodes connect
cs455/overlay/util/Utils.java: Contains utility functions
cs455/overlay/wireformats/Deregister.java: Deregistration message sent by messaging node
cs455/overlay/wireformats/Event.java: Base interface used by events
cs455/overlay/wireformats/EventFactory.java: Translates byte arrays into objects
cs455/overlay/wireformats/LinkWeights.java: Link weights message sent by Registery
cs455/overlay/wireformats/Message.java: Message which is passed between MessagingNodes during the rounds
cs455/overlay/wireformats/MessagingNodesList.java: List of messaging nodes to connect to to form the overlay
cs455/overlay/wireformats/Register.java: Registration message sent by the MessagingNode to the registery on start up
cs455/overlay/wireformats/TaskComplete.java: Sent to Registery when all messages have been sent
cs455/overlay/wireformats/TaskInitiate.java: Sent to Messaging node to start rounds
cs455/overlay/wireformats/TaskSummaryRequest.java: Sent to MessagingNode to request the task summary
cs455/overlay/wireformats/TaskSummaryResponse.java Sent to Registery containing task summary

