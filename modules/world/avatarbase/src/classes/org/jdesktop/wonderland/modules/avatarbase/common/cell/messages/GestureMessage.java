/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.avatarbase.common.cell.messages;

import org.jdesktop.wonderland.common.messages.Message;


/**
 *
 * @author Abhishek
 */
public class GestureMessage extends Message {
    
    private String userId;
    private boolean forGesture = false;
    private String messageType;
    private String messageString;

    public boolean isForGesture() {
        return forGesture;
    }

    public void setForGesture(boolean forGesture) {
        this.forGesture = forGesture;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
}
