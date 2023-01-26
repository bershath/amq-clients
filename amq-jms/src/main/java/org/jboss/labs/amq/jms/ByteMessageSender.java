package org.jboss.labs.amq.jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;

public class ByteMessageSender {

    public static final String userName = "admin";
    public static final String password = "jboss100";
    public static final String url = "tcp://localhost:61616?jms.watchTopicAdvisories=false&maxReconnectAttempts=3";

    public static void main(String[] args){
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(url,userName,password);
        try(Connection connection = activeMQConnectionFactory.createConnection()){
            Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("A"); //This would not create a new destination but offers a reference to an existing destination
            MessageProducer messageProducer = session.createProducer(queue);
            byte[] ba = new byte[102400];
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.writeBytes(ba);
            messageProducer.send(bytesMessage);
        } catch(JMSException e){
            e.printStackTrace();
        }
    }
}