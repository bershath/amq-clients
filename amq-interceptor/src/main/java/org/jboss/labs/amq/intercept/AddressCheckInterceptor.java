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
import org.apache.activemq.artemis.api.core.Interceptor;
import org.apache.activemq.artemis.core.protocol.core.Channel;
import org.apache.activemq.artemis.core.protocol.core.Packet;
import org.apache.activemq.artemis.core.protocol.core.impl.RemotingConnectionImpl;
import org.apache.activemq.artemis.core.protocol.core.impl.wireformat.ActiveMQExceptionMessage;
import org.apache.activemq.artemis.core.protocol.core.impl.wireformat.CreateAddressMessage;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.jboss.logging.Logger;

/**
 * @author  Tyronne W
 * @since   30-11-2020
 * @version 1.0
 * This class would not allow creating or accessing an address carrying an asterisk ('*').
 * Broker configuration:
 *      <remoting-incoming-interceptors>
 *          <class-name>org.jboss.labs.amq.intercept.AddressCheckInterceptor</class-name>
 *      </remoting-incoming-interceptors>
 *
 *
 *
 */

public class AddressCheckInterceptor implements Interceptor {

    private static final Logger log = Logger.getLogger(AddressCheckInterceptor.class);

    public boolean intercept(final Packet packet, final RemotingConnection connection) throws ActiveMQException {
        log.trace(Interceptor.class.getName() + " called");
        log.trace("Processing packet: " + packet.getClass().getName() + " that came from " + connection.getRemoteAddress() +".");
        log.trace("RemotingConnection: " + connection.getRemoteAddress() + " with client ID = " + connection.getID());


        //This condition would prevent clients creating new addresses matching the given condition.

        if(packet instanceof CreateAddressMessage){
            CreateAddressMessage cam = (CreateAddressMessage) packet;
            if(cam.getAddress().contains('*')){
                RemotingConnectionImpl remotingConnectionImpl = (RemotingConnectionImpl) connection;
                Channel channel = remotingConnectionImpl.getChannel(packet.getChannelID(), -1);
                log.info("A client from "+ connection.getRemoteAddress() + " tried to create " + cam.getAddress() +  ". Denied permission "  );
                ActiveMQException exception = new ActiveMQException("Address " + cam.getAddress() + " creation not permitted. Address name must not contain an asterisk ('*') ");
                Packet exceptionPacket = new ActiveMQExceptionMessage(exception);
                channel.sendAndFlush(exceptionPacket);
                connection.destroy();
                return false;
            }
        }
    return true;
    }
}
