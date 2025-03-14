package org.bershath.labs.jms.camel;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author  : Tyronne
 * @since   : 14-03-2025
 * @version : 1.0
 *
 *
 */


public class CamelRouteExample {

    final static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
    final static String userName = "admin";
    final static String password = "jboss100";
    final static String url = "tcp://localhost:61616?jms.watchTopicAdvisories=false&maxReconnectAttempts=3";

    public static void main(String[] args)  {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url,userName,password);
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("amq", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    from("amq:queue:A")
                            .to("amq:queue:B");
                }
            });
            camelContext.start();
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            System.out.println("Camel Route started at "+ dateFormat.format(new Date(System.currentTimeMillis())));
            System.out.println("Enter Q or q to end the application");
            char answer = '\0';
            while (!((answer == 'q') || (answer == 'Q'))) {
                answer = (char) inputStreamReader.read();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                camelContext.stop();
                System.out.println("Stopping the camel context");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
