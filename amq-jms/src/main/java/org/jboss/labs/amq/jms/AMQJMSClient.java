package org.jboss.labs.amq.jms;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class AMQJMSClient {

    public static Context context;
    public static ConnectionFactory connectionFactory;
    public static final int numOfMessages = 1;
    public static final String connectionFactoryStr = "ConnectionFactory";
    private static final String amqPhysicalQueueName = "queue.A";
    private static final String queueNameStr = "A";
    private static final String userName = "admin";
    private static final String password = "jboss100";
    private static final String url = "tcp://localhost:61616?type=CF";

    public AMQJMSClient() {
        try(JMSContext jmsContext = getConnectionFactory().createContext(userName, password)){
            Queue queue = (Queue) getContext().lookup(queueNameStr);
            JMSProducer jmsProducer = jmsContext.createProducer();
            for (int i = 1; i <= numOfMessages; i++) {
                TextMessage textMessage = jmsContext.createTextMessage();
                textMessage.setText("Message MyKey");
                jmsProducer.send(queue, textMessage);
            }


            JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
            TextMessage receivedMessage = (TextMessage) jmsConsumer.receive(30000);
            System.out.println(receivedMessage.getBody(String.class));

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }


    private Context getContext() throws NamingException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        props.put(Context.PROVIDER_URL, url);
        props.put(Context.SECURITY_PRINCIPAL, userName);
        props.put(Context.SECURITY_CREDENTIALS, password);
        props.put(amqPhysicalQueueName, queueNameStr);
        context = new InitialContext(props);
        return context;
    }

    private ConnectionFactory getConnectionFactory() throws NamingException {
        connectionFactory = (ConnectionFactory) getContext().lookup(connectionFactoryStr);
        return connectionFactory;
    }

    public static void main(String[] args) {
        new AMQJMSClient();
    }
}