package org.jboss.labs.amq.jmx;

import org.apache.activemq.artemis.api.core.management.ActiveMQServerControl;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;

/**
 * @author  : Tyronne W
 * @since   : 26-11-2020
 * @version : 1.0
 * This class would create a durable destination using the Artemis API and assigns security by role
 * Please enable jmx-management-enabled property in the broker.xml file:
 * <jmx-management-enabled>true</jmx-management-enabled>
 *
 * Please configure the connector-host and connector-port in the management.xml file:
 * <connector connector-host="$JMS_BROKER_HOST" connector-port="1099"/>
 *
 */
public class AMQJMXManager {
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    public static void main(String[] args){
        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL(JMX_URL);
            HashMap credentialsMap = new HashMap();
            String[] userCredentials = {"admin", "jboss100"};
            credentialsMap.put(JMXConnector.CREDENTIALS, userCredentials);
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, credentialsMap);
            MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
            ObjectName objectName  =  ObjectName.getInstance("org.apache.activemq.artemis:broker=\"0.0.0.0\"");
            ActiveMQServerControl activeMQServerControl = MBeanServerInvocationHandler.newProxyInstance(connection,objectName,ActiveMQServerControl.class,false);
            activeMQServerControl.createQueue("FirstAddress","FirstQueue",null, true, "ANYCAST");
            activeMQServerControl.addSecuritySettings("FirstAddress","amq","","amq","","amq","","");
            System.out.println("Created  address " + activeMQServerControl.getAddressInfo("FirstAddress"));
            jmxConnector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
