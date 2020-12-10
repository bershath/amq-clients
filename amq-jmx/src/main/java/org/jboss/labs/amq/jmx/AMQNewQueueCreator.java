package org.jboss.labs.amq.jmx;

import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.management.ActiveMQServerControl;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;

public class AMQNewQueueCreator {
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
            
            //String queue = activeMQServerControl.createQueue(String.valueOf(queueConfiguration), true);// createQueue(String queueConfiguration, boolean ignoreIfExists)
//activeMQServerControl.addSecuritySettings("FirstAddress","amq","","amq","","amq","","");
            //System.out.println("Created  address " + activeMQServerControl.getAddressInfo("FirstAddress"));
            jmxConnector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
