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

package org.jboss.labs.amq.intercept;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.protocol.amqp.broker.AMQPMessage;
import org.apache.activemq.artemis.protocol.amqp.broker.AmqpInterceptor;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.jboss.logging.Logger;


/**
 * @author  Tyronne W
 * @since   20-01-2026
 * @version 1.0
 *
 * This class would add a custom String property to a message, which would allow the use of a
 * Queue filter. The use case is to add a custom property 'protocol' and bind it to the 'AMQP'.
 * There's no need to check the protocol using the RemotingConnection.getProtocolName() as this is
 * an AMQP Interceptor, which is meant to intercept messages sent via AMQP.
 *
 * Deployment instructions: please add the following interceptor code in the broker.xml
 *
 *      <remoting-incoming-interceptors>
 *          <class-name>org.jboss.labs.amq.intercept.ProtocolCheckerInterceptor</class-name>
 *      </remoting-incoming-interceptors>
 *
 * Deploy the binary distribution, the packaged interceptor to the <pre>$BROKER_INSTANCE_HOME/lib</pre> folder
 *
 */

public class ProtocolCheckerInterceptor implements AmqpInterceptor {
    private static final Logger log = Logger.getLogger(ProtocolCheckerInterceptor.class);

    /**
     * @param packet     the packet being received
     * @param connection the connection the packet was received on
     * @return {@code true} to process the next interceptor and handle the packet,
     * {@code false} to abort processing of the packet
     * @throws ActiveMQException
     */

    static {
        log.info("Constructing ProtocolCheckerInterceptor");
    }

    @Override
    public boolean intercept(AMQPMessage packet, RemotingConnection connection) throws ActiveMQException {
        log.trace("RemotingConnection: " + connection.getRemoteAddress() + " with client ID = " + connection.getID() + " , Protocol: " + connection.getProtocolName());
        packet.putStringProperty("protocol","AMQP");
        return true;
    }
}
