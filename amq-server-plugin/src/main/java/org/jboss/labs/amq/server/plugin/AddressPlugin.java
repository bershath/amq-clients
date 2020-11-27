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
package org.jboss.labs.amq.server.plugin;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.core.server.impl.AddressInfo;
import org.apache.activemq.artemis.core.server.plugin.ActiveMQServerPlugin;
import org.jboss.logging.Logger;

/**
 * @author  : Tyronne
 * @since   : 26-11-2020
 * @version : 1.0
 *
 * This class implements the ActiveMQServerPlugin interface and override beforeAddAddress() to prevent messaging
 * clients creating addresses that contain an asterisk ('*')
 *
 * Broker configuration, in broker.xml:
 *      <broker-plugins>
 *    	    <broker-plugin class-name="org.jboss.labs.amq.server.plugin.AddressPlugin"/>
 * 	    </broker-plugins>
 *
 */
public class AddressPlugin implements ActiveMQServerPlugin {
    private static final Logger log = Logger.getLogger(AddressPlugin.class);
    @Override
    public void beforeAddAddress(AddressInfo addressInfo, boolean reload) throws ActiveMQException {
        if(addressInfo.getName().contains('*')){
            log.warn("Attempting to create "+ addressInfo.getName() +" address. Denied!");
            throw new ActiveMQException("Address "+ addressInfo.getName() + " creation not permitted. Address name must not contain an asterisk ('*') ");
        }
    }
}
