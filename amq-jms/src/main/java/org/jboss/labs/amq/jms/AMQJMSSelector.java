package org.jboss.labs.amq.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
/**
 * @author  : Tyronne W
 * @since   : 23-04-2021
 * @version : 1.0
 *
**/

public class AMQJMSSelector {
    public static Context context;
    public static ConnectionFactory connectionFactory;
    public static final String connectionFactoryStr = "ConnectionFactory";
    private static final String amqPhysicalQueueName = "queue.A";
    private static final String queueNameStr = "A";
    private static final String userName = "admin";
    private static final String password = "jboss100";
    private static final String url = "tcp://localhost:61616?type=CF";



    public AMQJMSSelector(){
        try(JMSContext jmsContext = getConnectionFactory().createContext(userName, password)){
            Queue queue = (Queue) getContext().lookup(queueNameStr);
            String selectorString = "JMSMessageID='ID:2342881b-a22e-11eb-9564-b42e99ea6f5c'";  // Please change <--- HERE -->
            JMSConsumer jmsConsumer = jmsContext.createConsumer(queue,selectorString);
            //Not checking the type of the message, please implement should that be necessary
            TextMessage receivedMessage = (TextMessage) jmsConsumer.receive(30000);
            if(receivedMessage != null){
                System.out.println("Received message with the ID: " + receivedMessage.getJMSMessageID());
                System.out.println("Message body: " + receivedMessage.getBody(String.class));
            } else {
                System.out.println("Did not receive a message with ID: " + selectorString);
            }
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
        new AMQJMSSelector();
    }


}
