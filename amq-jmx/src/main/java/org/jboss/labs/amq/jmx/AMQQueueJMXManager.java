package org.jboss.labs.amq.jmx;

import org.apache.activemq.artemis.api.core.management.QueueControl;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;

/**
 * @author  : Tyronne
 * @since   : 26-11-2020
 * @version : 1.0
 *
 * Please enable jmx-management-enabled property in the broker.xml file:
 * <jmx-management-enabled>true</jmx-management-enabled>
 *
 * Please configure the connector-host and connector-port in the management.xml file:
 * <connector connector-host="$JMS_BROKER_HOST" connector-port="1099"/>
 */
public class AMQQueueJMXManager {
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    private static  final String queueName = "exampleQueue";

    public static void main(String[] args){
        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL(JMX_URL);
            HashMap credentialsMap = new HashMap();
            String[] userCredentials = {"admin", "jboss100"};
            credentialsMap.put(JMXConnector.CREDENTIALS, userCredentials);
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, credentialsMap);
            MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
            ObjectName objectName  =  ObjectName.getInstance("org.apache.activemq.artemis:broker=\"0.0.0.0\",component=addresses,address=\""+queueName+"\",subcomponent=queues,routing-type=\"anycast\",queue=\""+queueName+"\"");

            QueueControl queueControl = MBeanServerInvocationHandler.newProxyInstance(connection,objectName,QueueControl.class,false);
            System.out.println("Number of messages in the destination : " + queueControl.countMessages());
            System.out.println("Invoking \"queueControl.removeAllMessages()\". Number of messages removed: " + queueControl.removeAllMessages());
            System.out.println("Number of messages in the destination : " + queueControl.countMessages());
            queueControl.getMessageCount();
            queueControl.getMessagesAdded();
            queueControl.getMessagesAcknowledged();
            System.out.println("Message counter : " + queueControl.listMessageCounter());

            jmxConnector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}