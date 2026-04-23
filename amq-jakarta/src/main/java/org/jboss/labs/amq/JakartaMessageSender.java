package org.jboss.labs.amq;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import java.util.Properties;


public class JakartaMessageSender {

    public static Context context;
    public static ConnectionFactory connectionFactory;
    public static final String connectionFactoryStr = "ConnectionFactory";
    private static final String queueNameStr = "A";
    private static final String userName = "admin";
    private static final String password = "jboss100";
    private static final String url = "tcp://localhost:61616?type=CF";


    public JakartaMessageSender() {
        try(JMSContext jmsContext = getConnectionFactory().createContext(userName, password)){
            Queue queue = jmsContext.createQueue(queueNameStr);
            JMSProducer jmsProducer = jmsContext.createProducer();
            TextMessage textMessage = jmsContext.createTextMessage();
            textMessage.setText("Message from Jakarta Message Sender ");
            jmsProducer.send(queue, textMessage);
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
        context = new InitialContext(props);
        return context;
    }

    private ConnectionFactory getConnectionFactory() throws NamingException {
        connectionFactory = (ConnectionFactory) getContext().lookup(connectionFactoryStr);
        return connectionFactory;
    }

    public static void main(String[] args) throws NamingException, JMSException {
        new JakartaMessageSender();
    }

}
