/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior;

import java.util.LinkedList;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class AvatarInteractionListenerRegistrar {

    static final LinkedList<AvatarInteractionListener> registeredListeners = new LinkedList<AvatarInteractionListener>();

    public static void registerListener(AvatarInteractionListener toAdd) {
        registeredListeners.add(toAdd);
    }

    public static void deRegisterListener(AvatarInteractionListener toAdd) {
        registeredListeners.remove(toAdd);
    }

    public static void deRegisterListenerAll() {
        registeredListeners.clear();
    }

    public static LinkedList<AvatarInteractionListener> getRegisteredListeners() {
        //return clone of linkedlist otherwise it might throw concurrent modification error
        return (LinkedList<AvatarInteractionListener>) registeredListeners.clone();
    }

}
