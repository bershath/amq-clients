package org.jboss.labs.amq.jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;

public class AMQMessageProducerTopic {
    public static Context context;
    public static ActiveMQConnectionFactory connectionFactory;
    public static final String topicNameStr = "TestTopic";
    public static final String connectionFactoryStr = "myFactoryLookup";
    public static final String userName = "admin";
    public static final String password = "jboss100";
    public static final String url = "tcp://localhost:61616?jms.watchTopicAdvisories=false&maxReconnectAttempts=3";
    public static final int numOfMessages = 2;

    public static void main(String[] args) {
        new AMQMessageProducerTopic();
    }

    public AMQMessageProducerTopic() {
        try(JMSContext jmsContext = getConnectionFactory().createContext(userName, password)){
            Topic topic = jmsContext.createTopic(topicNameStr);
            JMSProducer jmsProducer = jmsContext.createProducer();
            for (int i = 1; i <= numOfMessages; i++) {
                TextMessage textMessage = jmsContext.createTextMessage();
                textMessage.setText("Message #  " + i);
                jmsProducer.send(topic, textMessage);
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