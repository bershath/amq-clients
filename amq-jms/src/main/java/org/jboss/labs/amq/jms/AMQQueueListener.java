package org.jboss.labs.amq.jms;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;

import javax.jms.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AMQQueueListener implements MessageListener {

    public AMQQueueListener(){}

    static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
    private static Session session;


    public static void main(String[] args) {


        Connection connection = null;
        try {
            Map<String, Object> connectionParams = new HashMap<>();
            connectionParams.put(org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants.PORT_PROP_NAME, 61616);
            TransportConfiguration transportConfiguration =
                    new TransportConfiguration(
                            "org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory", connectionParams);

            ConnectionFactory connectionFactory = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
            connection = connectionFactory.createConnection("admin", "jboss100");
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue queue = session.createQueue("A");


            AMQQueueListener amqQueueListener = new AMQQueueListener();
            MessageConsumer messageConsumer = session.createConsumer(queue);
            messageConsumer.setMessageListener(amqQueueListener);

            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            System.out.println("Message consumer started at "+ dateFormat.format(new Date(System.currentTimeMillis())));
            System.out.println("Enter Q or q to end the application");
            char answer = '\0';

            while (!((answer == 'q') || (answer == 'Q'))) {
                try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (JMSException e){
            e.printStackTrace();
        } finally {
            try{
                if(connection != null)
                    connection.close();
            }catch (JMSException e){
                //do nothing
            }
        }
    }

    public void onMessage(Message message) {
        try {
            if(message instanceof TextMessage){
                System.out.println("String message body received: " + message.getBody(String.class));
                System.out.println("Message Id: " + message.getJMSMessageID());
            } else {
                System.out.println("Message received with ID "+ message.getJMSMessageID());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
