package org.jboss.labs.amq.jms;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AMQTopicSubscriber implements MessageListener {

    public AMQTopicSubscriber(){

    }

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
            connection.setClientID("World");
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic("TestTopic");


            AMQTopicSubscriber httpQueueConsumer = new AMQTopicSubscriber();
            MessageConsumer messageConsumer = session.createDurableConsumer(topic, "InMotion");
            messageConsumer.setMessageListener(httpQueueConsumer);

            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            System.out.println("message consumer started at "+ dateFormat.format(new Date(System.currentTimeMillis())));
            System.out.println("enter Q or q to end the application");
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
            TextMessage textMessage = (TextMessage) message;
            System.out.println(textMessage.getBody(String.class));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}