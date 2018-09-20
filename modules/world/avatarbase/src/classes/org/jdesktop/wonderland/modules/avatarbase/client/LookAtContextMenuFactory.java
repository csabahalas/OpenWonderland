/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */

package org.jdesktop.wonderland.modules.avatarbase.client;

import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.annotation.ContextMenuFactory;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@ContextMenuFactory
public class LookAtContextMenuFactory implements ContextMenuFactorySPI {

    public ContextMenuItem[] getContextMenuItems(ContextEvent event) {
        return new ContextMenuItem[]{
            new SimpleContextMenuItem("Look At (Press '1' + Left Click)", new ContextMenuActionListener() {
                public void actionPerformed(ContextMenuItemEvent event) {
                    Cell cell = event.getCell();
                    AvatarClientPlugin.lookAtAdvanced(cell);

                    //notify listeners
                    AvatarClientPlugin.notifyLookAtListener(cell);
                }
            })
        };
    }
}
