package org.jboss.labs.amq.jmx;

import org.apache.activemq.artemis.api.core.management.AcceptorControl;
import org.apache.activemq.artemis.api.core.management.ActiveMQServerControl;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
 * @author  : Tyronne
 * @since   : 26-02-2021
 * @version : 1.0
 *
 * Please enable jmx-management-enabled property in the broker.xml file:
 * <jmx-management-enabled>true</jmx-management-enabled>
 *
 * Please configure the connector-host and connector-port in the management.xml file:
 * <connector connector-host="$JMS_BROKER_HOST" connector-port="1099"/>
 */
public class AMQAcceptorManager {
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    public static void main(String[] args){
        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL(JMX_URL);
            HashMap credentialsMap = new HashMap();
            String[] userCredentials = {"admin", "jboss100"};
            credentialsMap.put(JMXConnector.CREDENTIALS, userCredentials);
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, credentialsMap);
            MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
            //ObjectName objectName  =  ObjectName.getInstance("org.apache.activemq.artemis:broker=\"0.0.0.0\"");
            //org.apache.activemq.artemis:broker="0.0.0.0",component=acceptors,name="artemis"
            ObjectName objectName  =  ObjectName.getInstance("org.apache.activemq.artemis:broker=\"0.0.0.0\",component=acceptors,name=\"artemis\"");
            AcceptorControl acceptorControl = MBeanServerInvocationHandler.newProxyInstance(connection,objectName,AcceptorControl.class,false);
            System.out.println(acceptorControl.getParameters());
            Map acceptorParameters = acceptorControl.getParameters();
            Set acceptorKeys = acceptorParameters.keySet();
            Iterator i = acceptorKeys.iterator();
            while (i.hasNext()){
                String keyName = (String) i.next();
                System.out.println(keyName);

            }
            System.out.println(acceptorControl.getName());
            jmxConnector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
