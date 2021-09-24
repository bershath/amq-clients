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
 * @author  : Tyronne
 * @since   : 11-12-2020
 * @version : 1.2
 *
 * Please enable jmx-management-enabled property in the broker.xml file:
 * <jmx-management-enabled>true</jmx-management-enabled>
 *
 * Please configure the connector-host and connector-port in the management.xml file:
 * <connector connector-host="$JMS_BROKER_HOST" connector-port="1099"/>
 */

public class AMQNewQueueCreator {
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    private static final String addressName = "testQueue";
    private static final String queueName = "{\"name\":"+"\""+addressName+"\"" +",\"routing-type\":\"ANYCAST\"}";


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
            activeMQServerControl.createQueue(queueName, true);
            //activeMQServerControl.addSecuritySettings(addressName,"amq","","amq","","amq","","");
            //addSecuritySettings(String addressMatch, String sendRoles, String consumeRoles, String createDurableQueueRoles, String deleteDurableQueueRoles, String createNonDurableQueueRoles, String deleteNonDurableQueueRoles, String manageRoles, String browseRoles, String createAddressRoles, String deleteAddressRoles)
            activeMQServerControl.addSecuritySettings(addressName, "amq", "amq","amq","amq","amq", "amq", "amq","amq", "amq","amq");
            jmxConnector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
