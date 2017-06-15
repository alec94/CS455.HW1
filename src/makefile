JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	cs455/overlay/dijkstra/EdgeNode.java \
	cs455/overlay/dijkstra/RoutingCache.java \
	cs455/overlay/dijkstra/ShortestPath.java \
	cs455/overlay/node/MessagingNode.java \
	cs455/overlay/node/NetworkNode.java \
	cs455/overlay/node/Node.java \
	cs455/overlay/node/Registry.java \
	cs455/overlay/transport/ConsoleThread.java \
	cs455/overlay/transport/TCPReceiverThread.java \
	cs455/overlay/transport/TCPSender.java \
	cs455/overlay/transport/TCPServerThread.java \
	cs455/overlay/util/Utils.java \
	cs455/overlay/wireformats/Deregister.java \
	cs455/overlay/wireformats/Event.java \
	cs455/overlay/wireformats/EventFactory.java \
	cs455/overlay/wireformats/LinkWeights.java \
	cs455/overlay/wireformats/Message.java \
	cs455/overlay/wireformats/MessagingNodesList.java \
	cs455/overlay/wireformats/Register.java \
	cs455/overlay/wireformats/TaskComplete.java \
	cs455/overlay/wireformats/TaskInitiate.java \
	cs455/overlay/wireformats/TaskSummaryRequest.java \
	cs455/overlay/wireformats/TaskSummaryResponse.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) -r *.class