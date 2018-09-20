/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.server;

import java.io.Serializable;
import java.util.Properties;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.GestureConnectionType;
import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class GestureClientConnectionHandler implements ClientConnectionHandler, Serializable {
    
    public ConnectionType getConnectionType() { 
        return GestureConnectionType.GESTURE_TYPE;
    }
  
    // notification that this plugin is registered
    public void registered(WonderlandClientSender sender) {
        
    }
   
    public void clientConnected(WonderlandClientSender sender, WonderlandClientID clientID, Properties properties) {
        
    }

    public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID, Message message) {
        // echo message to all clients
        sender.send(message);
    }

    public void clientDisconnected(WonderlandClientSender sender, WonderlandClientID clientID) {
        
    }
}
