package org.jboss.labs.amq.jmx;

import org.apache.activemq.artemis.api.core.management.AddressControl;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;

/**
 * @author  : Tyronne
 * @since   : 27-09-2021
 * @version : 1.0
 *
 * Please enable jmx-management-enabled property in the broker.xml file:
 * <jmx-management-enabled>true</jmx-management-enabled>
 *
 * Please configure the connector-host and connector-port in the management.xml file:
 * <connector connector-host="$JMS_BROKER_HOST" connector-port="1099"/>
 *
 * This class returns the list of roles associated with a destination in JSON format.
 *
 */

public class AMQJMXAddressManager {
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    private static  final String addressName = "exampleQueue";
    private static  final String userName = "admin";
    private static  final String password = "jboss100";
    private static  final String jmxObjectName = "org.apache.activemq.artemis:broker=\"0.0.0.0\",component=addresses,address=\""+addressName+"\"";

    public static void main(String[] args){
        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL(JMX_URL);
            HashMap credentialsMap = new HashMap();
            String[] userCredentials = {userName, password};
            credentialsMap.put(JMXConnector.CREDENTIALS, userCredentials);
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, credentialsMap);
            MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
            ObjectName objectName  =  ObjectName.getInstance(jmxObjectName);
            AddressControl addressControl = MBeanServerInvocationHandler.newProxyInstance(connection,objectName,AddressControl.class,false);
            System.out.println("Address getRolesAsJSON() :"+ addressControl.getRolesAsJSON());
            jmxConnector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
