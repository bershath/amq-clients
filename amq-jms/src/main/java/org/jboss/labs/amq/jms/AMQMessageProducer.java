package org.jboss.labs.amq.jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.Context;

public class AMQMessageProducer {
    public static Context context;
    public static ActiveMQConnectionFactory connectionFactory;
    public static final String queueNameStr = "AdfDB*";
    public static final String connectionFactoryStr = "myFactoryLookup";
    public static final String userName = "admin";
    public static final String password = "jboss100";
    public static final String url = "tcp://localhost:61616?jms.watchTopicAdvisories=false&maxReconnectAttempts=3";
    public static final int numOfMessages = 1;

    public static void main(String[] args) {
        new AMQMessageProducer();
    }

    public AMQMessageProducer() {
        try(JMSContext jmsContext = getConnectionFactory().createContext(userName, password)){
            Queue queue = jmsContext.createQueue(queueNameStr);
            JMSProducer jmsProducer = jmsContext.createProducer();
            for (int i = 1; i <= numOfMessages; i++) {
                TextMessage textMessage = jmsContext.createTextMessage();
                textMessage.setText("Message #  " + i);
                jmsProducer.send(queue, textMessage);
                System.out.println("Message sent with the ID # " + textMessage.getJMSMessageID() );
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private ConnectionFactory getConnectionFactory() {
        connectionFactory = new ActiveMQConnectionFactory(url);
        return connectionFactory;
    }
}
