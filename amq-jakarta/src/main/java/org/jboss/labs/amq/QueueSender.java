package org.jboss.labs.amq;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class QueueSender {

    public static final String connectionFactoryStr = "ConnectionFactory";
    private static final String userName = "admin";
    private static final String password = "jboss100";
    private static final String queueJNDIName = "queue/A";

    public QueueSender() throws NamingException {

        InitialContext initialContext = new InitialContext();
        ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup(connectionFactoryStr);
        try(JMSContext jmsContext = connectionFactory.createContext(userName, password)){
            Queue queue = (Queue) initialContext.lookup(queueJNDIName);
            JMSProducer jmsProducer = jmsContext.createProducer();
            TextMessage textMessage = jmsContext.createTextMessage();
            textMessage.setText("Message from Queue Sender");
            jmsProducer.send(queue, textMessage);
            JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
            TextMessage receivedMessage = (TextMessage) jmsConsumer.receive(30000);
            System.out.println(receivedMessage.getBody(String.class));
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        } finally {
            if(initialContext != null)
                initialContext.close();
        }
    }

    public static void main(String[] args) throws NamingException {
        QueueSender queueSender = new QueueSender();
    }
}
