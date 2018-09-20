/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.avatarbase.common.cell.messages;

import org.jdesktop.wonderland.common.comms.ConnectionType;

/**
 *
 * @author Abhishek
 */
public class GestureConnectionType extends ConnectionType {
    public static final GestureConnectionType GESTURE_TYPE = new GestureConnectionType("_GestureClient");
    
    private GestureConnectionType(String typeName) {
        super (typeName);
    }
}
