/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.labs.amq.jmx;

import org.apache.activemq.artemis.api.core.management.QueueControl;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;

/**
 * @author  : Tyronne W
 * @since   : 20-01-2021
 * @version : 1.0
 * This class would expire messages on demand, using the internal API, provided by the
 * message broker.
 *
 * Please enable jmx-management-enabled property in the broker.xml file:
 * <jmx-management-enabled>true</jmx-management-enabled>
 *
 * Please configure the connector-host and connector-port in the management.xml file:
 * <connector connector-host="$JMS_BROKER_HOST" connector-port="1099"/>
 *
 */

public class ExpireMessageJMX {
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    private static  final long numOfDays = 30;
    private static  final String queueName = "exampleQueue";
    private static  final String userName = "admin";
    private static  final String password = "jboss100";

    public static void main(String[] args){
        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL(JMX_URL);
            HashMap credentialsMap = new HashMap();
            String[] userCredentials = {userName, password};
            credentialsMap.put(JMXConnector.CREDENTIALS, userCredentials);
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, credentialsMap);
            MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
            ObjectName objectName  =  ObjectName.getInstance("org.apache.activemq.artemis:broker=\"0.0.0.0\",component=addresses,address=\""+queueName+"\",subcomponent=queues,routing-type=\"anycast\",queue=\""+queueName+"\"");
            QueueControl queueControl = MBeanServerInvocationHandler.newProxyInstance(connection,objectName,QueueControl.class,false);
            System.out.println("Number of messages in the destination : " + queueControl.countMessages());
            CompositeData[] messages = queueControl.browse();
            for (int x = 0; x < messages.length; x++) {
                CompositeData message = messages[x];
                String messageID = (String) message.get("messageID");
                long timeStamp = (long) message.get("timestamp");
                //milliseconds for a day : 86400000
                timeStamp = timeStamp + (86400000 * numOfDays);

                if(timeStamp <= System.currentTimeMillis()){
                    queueControl.expireMessage(Long.parseLong(messageID));
                    System.out.println("Message with id " + messageID + " is older than the specified period, expired");
                } else{
                    System.out.println("Message is new, skipping");
                }
            }
            System.out.println("Number of messages in the destination : " + queueControl.countMessages());
            jmxConnector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
