/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.hud.CompassLayout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.modules.avatarbase.client.GestureConnection;

/**
 * A HUD display for avatar gestures
 *
 * @author nsimpson
 * @author ronny.standtke@fhnw.ch
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class GestureHUD {

    private static final Logger logger = Logger.getLogger(GestureHUD.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle");

    private final JCheckBoxMenuItem gestureMI;

    private boolean visible = false;
    // maps GUI visible gesture names to non-visible action names
    private HUDComponent gestureHud;
    private HUD mainHUD;
    private GesturePanel panelForHUD = null;

    /**
     * creates a new GestureHUD
     */
    public GestureHUD(JCheckBoxMenuItem gestureMI) {
        this.gestureMI = gestureMI;
        //setAvatarCharacter(null, false, null);
    }

    public void refresh() {
        if (panelForHUD == null) {
            logger.warning("panelForHUD is null in HUD class");
        } else {
            panelForHUD.closeEventHUD();
        }
    }

    /**
     * maximizes the gesture HUD
     * making the HUD visible may require
     * to maximize it if it was minimized
     * introduced for fixing issue #174 hud visibility management
     */
    public void setMaximized() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gestureHud.setMaximized();
            }
        });
    }

    /**
     * shows or hides the gesture HUD
     * @param visible if <tt>true</tt>, the HUD is shown, otherwise hidden
     */
    public void setVisible(final boolean show) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                GestureHUD.this.visible = show;
                gestureHud.setVisible(show);
                return;
            }
        });
    }

    /**
     * returns <tt>true</tt>, when the HUD is visible, otherwise false
     * @return <tt>true</tt>, when the HUD is visible, otherwise false
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * sets the avatar and activates supported gestures
     * @param avatar the avatar to set
     * @param show stating if the HUD should be visible
     */
    public void setAvatarCharacter(final WlAvatarCharacter avatar, final boolean show, final GestureConnection gestureConn) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (mainHUD == null) {
                    mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
                }

                if (gestureHud != null) {
                    gestureHud.setVisible(false);
                    mainHUD.removeComponent(gestureHud);
                    gestureHud = null;
                    panelForHUD.removeStateChangeListener();
                    panelForHUD = null;
                }

                // If we don't have an avatar, then just return
                if (avatar == null) {
                    logger.warning("Gesture HUD: avatar character is null.");
                    return;
                }
                panelForHUD = new GesturePanel(avatar, gestureConn);

                if (gestureHud == null) {
                    gestureHud = mainHUD.createComponent(panelForHUD);
                    //gestureHud.setDecoratable(false);
                    gestureHud.setName(BUNDLE.getString("Gesture_UI"));
//                    gestureHud.setLocation(leftMargin, bottomMargin);
                    gestureHud.setPreferredLocation(CompassLayout.Layout.NORTHEAST);
                    gestureHud.setIcon(new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/avatarbase/client/resources/" +
                "Gesture-Icon-32x32.png")));
                    mainHUD.addComponent(gestureHud);

                    // issue #174 hud visibility management
                    gestureHud.addEventListener(new HUDEventListener() {
                        public void HUDObjectChanged(HUDEvent event) {
                            HUDEventType hudEventType = event.getEventType();
                            //only on closing of HUD It will refresh the GP
                            //(In Male To Female Avatar change it is not required to refresh)
                            if (event.getEventType() == HUDEventType.CLOSED) {
                                panelForHUD.stopAllGestures(true);
                                gestureMI.setSelected(false);
                                panelForHUD.closeEventHUD();
                            }
                            if (event.getEventType() == HUDEventType.DISAPPEARED
                                    || hudEventType == HUDEventType.MINIMIZED) {
                                //When gesture playing and we do avatar change then it Affect a problem
                                // panelForHUD.stopAllGestures(true);
                                gestureMI.setSelected(false);
                            } else if (event.getEventType() == HUDEventType.APPEARED
                                    || hudEventType == HUDEventType.MAXIMIZED) {
                                gestureMI.setSelected(true);
                            }
                        }
                    });
                }

                GestureHUD.this.visible = show;
                gestureHud.setVisible(show);
            }
        });
    }
    
    public GestureConnection isConnectionNull() {
        if(panelForHUD==null)
            return null;
        return panelForHUD.isConnectionNull();
    }
    
    public void setConnection(GestureConnection connection) {
        panelForHUD.setConnection(connection);
    }
    
    public Dimension getGesturePanelSize() {
        if(panelForHUD!=null) {
            return panelForHUD.getSize();
        } else {
            return new Dimension(0, 0);
        }
    }
    
    public Point getGesturePanelHUDLocation() {
        if(panelForHUD!=null) {
            return gestureHud.getLocation();
        } else {
            return new Point(0, 0);
        }
    }
    
}
