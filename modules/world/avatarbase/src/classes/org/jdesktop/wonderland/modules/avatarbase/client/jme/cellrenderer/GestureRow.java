/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import imi.character.statemachine.GameState;
import imi.character.statemachine.GameStateChangeListener;
import imi.character.statemachine.GameStateChangeListenerRegisterar;
import imi.character.statemachine.corestates.CycleActionState;
import imi.character.statemachine.corestates.IdleState;
import imi.character.statemachine.corestates.SitState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.Gesture;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.Gesture.GestureType;

/**
 * This will be added to main gesture panel
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class GestureRow extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(GestureRow.class.getName());
    private boolean gesturePlaying = false;
    private final Gesture gesture;
    private final GesturePanel gesturePanel;
    private HoverListener hoverListener = null;
    private static Popup tooltipPopup = null;
    private int intensityLevel = 1;
    private boolean tooltipOpen = false;
    private boolean clickedForPlaying = false;
    private static boolean loading = false;
    private boolean lisForPlayDirectlyNeeded = true;

    public GestureRow(Gesture gesture, GesturePanel gesturePanel) {
        this.gesture = gesture;
        this.gesturePanel = gesturePanel;
        initComponents();
        if (gesture.getLabelName().equals("")) {
            playStopLbl.setVisible(false);
            upLabel.setVisible(false);
            downLabel.setVisible(false);
        } else {
            gestureNameLabel.setText(gesture.getLabelName());
        }
        upLabel.setVisible(false);
        downLabel.setVisible(false);
    }

    void playGestureActual(final boolean forIntensity) {
        playStopLbl.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/org/jdesktop/wonderland/modules/avatarbase/client/resources/stop.png")));

        //visible intensity up,down label if gesture has intensity levels
        if (gesture.hasIntensityLevel() && !forIntensity) {
            upLabel.setVisible(true);
            downLabel.setEnabled(false);
            downLabel.setVisible(true);
        }

        gesturePanel.playGesture(gesture, intensityLevel, forIntensity);
        gesturePlaying = true;
        loading = false;
    }

    void playGesture(final boolean forIntensity) {
        lisForPlayDirectlyNeeded = true;
        clickedForPlaying = true;
        GameStateChangeListener lisForPlayDirectly = new GameStateChangeListener() {

            public void enterInState(GameState gs) {
                String thisUserId = ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString();
                String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
                if (thisUserId.equals(stateChangeUserId)) {
                    if (gs instanceof IdleState || (gs instanceof SitState)) {
                        //HACK! put some delay before playing.
                        //Expecting that all other cliets will finish transition to Idle/Sit state in one second
                        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                        ses.scheduleWithFixedDelay(new Runnable() {

                            public void run() {
                                playGestureActual(forIntensity);
                                ses.shutdown();
                            }
                        }, 2, 10, TimeUnit.SECONDS);
                        GameStateChangeListenerRegisterar.deRegisterListener(this);
                    }
                }
            }

            public void exitfromState(GameState gs) {

            }

            public void changeInState(GameState gs, String string, boolean bln, String string1) {

            }
        };
        GameStateChangeListener lisForCombineGesture = new GameStateChangeListener() {

            public void enterInState(GameState gs) {

            }

            public void exitfromState(GameState gs) {

            }

            public void changeInState(GameState gs, String string, boolean bln, String string1) {
                lisForPlayDirectlyNeeded = false;
                String thisUserId = ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString();
                String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
                if (thisUserId.equals(stateChangeUserId)) {
                    if (gs instanceof CycleActionState) {
                        //HACK! put some delay before playing.
                        //Expecting that all other cliets will finish transition to Idle/Sit state in two second
                        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                        ses.scheduleWithFixedDelay(new Runnable() {

                            public void run() {
                                playGestureActual(forIntensity);
                                ses.shutdown();
                            }
                        }, 2, 10, TimeUnit.SECONDS);
                        GameStateChangeListenerRegisterar.deRegisterListener(this);
                    }
                }
            }
        };
        GameStateChangeListenerRegisterar.registerListener(lisForCombineGesture);
        boolean wait = false;

        //HACK for the case in which user play loop gesture while the other two gestures in running
        //So stop first then second and then play gesture
        if ((gesture.getGestureType() == GestureType.TORSO || gesture.getGestureType() == GestureType.HEADTORSO)
                && gesturePanel.getAnimCounter() == 2 && gesture.isLooping() && !forIntensity) {
            loading = true;
            playStopLbl.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/org/jdesktop/wonderland/modules/avatarbase/client/resources/loading.gif")));
            logger.info("Gesture Row - stopAllGestures");
            gesturePanel.stopCombineGestures();
            wait = true;
        } else {
            if (!forIntensity) {
                loading = true;
                playStopLbl.setIcon(new javax.swing.ImageIcon(getClass()
                        .getResource("/org/jdesktop/wonderland/modules/avatarbase/client/resources/loading.gif")));
                wait = gesturePanel.stopOtherGesture(gesture);
            }
        }

        GameState gs = gesturePanel.getAvatarCharacter().getContext().getCurrentState();
        if ((gs instanceof IdleState) || (gs instanceof SitState) || !wait) {
            GameStateChangeListenerRegisterar.deRegisterListener(lisForCombineGesture);
            playGestureActual(forIntensity);
        } else {
            GameStateChangeListenerRegisterar.deRegisterListener(lisForCombineGesture);
            if (lisForPlayDirectlyNeeded) {
                GameStateChangeListenerRegisterar.registerListener(lisForPlayDirectly);
            }
        }

    }

    void stopGesture(final boolean stopAnim, boolean toPlayOtherGesture) {
        //hide intensity up,down label if gesture has intensity levels
        if (gesture.hasIntensityLevel()) {
            upLabel.setVisible(false);
            upLabel.setEnabled(true);
            downLabel.setEnabled(true);
            downLabel.setVisible(false);
        }

        playStopLbl.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/org/jdesktop/wonderland/modules/avatarbase/client/resources/play.png")));
        if (stopAnim) {
            gesturePanel.stopGesture(gesture, intensityLevel, toPlayOtherGesture);
        }
        intensityLevel = 1;
        gesturePlaying = false;
        clickedForPlaying = false;
    }

    private void playStopLblClicked() {
        GameState gs = null;
        if (gesturePanel.getAvatarCharacter() != null) {
            if (gesturePanel.getAvatarCharacter().getContext() != null) {
                if (gesturePanel.getAvatarCharacter().getContext().getCurrentState() != null) {
                    gs = gesturePanel.getAvatarCharacter().getContext().getCurrentState();
                } else {
                    return;
                }
            } else {
                return;
            }
        } else {
            return;
        }

        if (gs == null) {
            return;
        }
        if (gs instanceof SitState) {
            SitState ss = (SitState) gs;
            if (ss.isSleeping()) {
                JOptionPane.showMessageDialog(JmeClientMain.getFrame().getCanvas(), "Gestures can't be played in sleeping position.", "Gesture", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (gesture.getAnimName().equals("")) {
            return;
        }
        if (gesturePlaying) {
            stopGesture(true, false);
            intensityLevel = 1;
        } else {
            if (!clickedForPlaying && !loading) {
                playGesture(false);
            }
        }
    }

    boolean isGesturePlaying() {
        return gesturePlaying;
    }

    Gesture getGesture() {
        return gesture;
    }

    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);

        gestureNameLabel.setEnabled(enable);
        playStopLbl.setVisible(enable);
        validate();
    }

    /**
     * remove mouse listener
     */
    public void removeMouseListenerForTooltip() {
        if (hoverListener != null) {
            gestureNameLabel.removeMouseListener(hoverListener);
            removeMouseListener(hoverListener);
            tooltipPopup = null;
            hoverListener = null;
        }
    }

    /**
     * add mouse listener
     */
    public void addMouseListenerForTooltip() {
        if (hoverListener == null) {
            String msg = "<html><body>Seated-only leg gesture <br />"
                    + "not available while <br />"
                    + "avatar is standing.</body></html>";
            if (gesture.getGestureType() == GestureType.HEAD) {
                msg = "<html><body>Head gesture not available <br />"
                        + "while avatar is playing gesture<br />"
                        + "which includes head movement.</body></html>";
            }

            hoverListener = new HoverListener(gesturePanel, msg);
            gestureNameLabel.addMouseListener(hoverListener);
            addMouseListener(hoverListener);
        }
    }

    /**
     * on hover open a popup and show text in it.
     */
    private class HoverListener extends MouseAdapter {

        private String text = "";
        private Component parentComp = null;

        public HoverListener(Component comp, String text) {
            this.text = text;
            this.parentComp = comp;
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    if (!tooltipOpen) {
                        if (tooltipPopup != null) {
                            tooltipPopup.hide();
                            tooltipPopup = null;
                        }
                        JPanel panel = new JPanel();
                        panel.setLayout(new BorderLayout(2, 2));
                        panel.setBackground(new Color(240, 220, 130));
                        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
                        JLabel l = new JLabel(text);
                        panel.add(l);
                        Point location = MouseInfo.getPointerInfo().getLocation();
                        tooltipPopup = PopupFactory.getSharedInstance()
                                .getPopup(parentComp, panel, location.x - 50, location.y);
                        tooltipPopup.show();
                        tooltipOpen = true;
                    }
                }
            });
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (tooltipPopup != null) {
                tooltipPopup.hide();
                tooltipOpen = false;
            }
        }
    }

    private void changeIntensity(boolean up) {
        if (up) {
            intensityLevel++;
        } else {
            intensityLevel--;
        }
        playGesture(true);
        if (intensityLevel == 1) {
            upLabel.setEnabled(true);
            downLabel.setEnabled(false);
        } else if (intensityLevel == 2) {
            upLabel.setEnabled(true);
            downLabel.setEnabled(true);
        } else if (intensityLevel == 3) {
            upLabel.setEnabled(false);
            downLabel.setEnabled(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gestureNameLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        upLabel = new javax.swing.JLabel();
        downLabel = new javax.swing.JLabel();
        playStopLbl = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(175, 32));
        setPreferredSize(new java.awt.Dimension(200, 32));
        setSize(new java.awt.Dimension(200, 32));

        gestureNameLabel.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        gestureNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        gestureNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gestureNameLabelMouseClicked(evt);
            }
        });

        jPanel1.setMinimumSize(new java.awt.Dimension(37, 34));
        jPanel1.setSize(new java.awt.Dimension(35, 32));

        upLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/avatarbase/client/resources/up1.png"))); // NOI18N
        upLabel.setMinimumSize(new java.awt.Dimension(15, 15));
        upLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                upLabelMouseClicked(evt);
            }
        });

        downLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/avatarbase/client/resources/down1.png"))); // NOI18N
        downLabel.setMinimumSize(new java.awt.Dimension(15, 15));
        downLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                downLabelMouseClicked(evt);
            }
        });

        playStopLbl.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        playStopLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        playStopLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/avatarbase/client/resources/play.png"))); // NOI18N
        playStopLbl.setToolTipText("");
        playStopLbl.setMaximumSize(new java.awt.Dimension(18, 32));
        playStopLbl.setMinimumSize(new java.awt.Dimension(18, 32));
        playStopLbl.setPreferredSize(new java.awt.Dimension(18, 32));
        playStopLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playStopLblMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(playStopLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(upLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(downLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(playStopLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(upLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(downLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(gestureNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(gestureNameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void playStopLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playStopLblMouseClicked
        // TODO add your handling code here:
        if (isEnabled()) {
            playStopLblClicked();
        }
    }//GEN-LAST:event_playStopLblMouseClicked

    private void downLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_downLabelMouseClicked
        // TODO add your handling code here:
        if (downLabel.isEnabled()) {
            changeIntensity(false);
        }
    }//GEN-LAST:event_downLabelMouseClicked

    private void upLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_upLabelMouseClicked
        // TODO add your handling code here:
        if (upLabel.isEnabled()) {
            changeIntensity(true);
        }
    }//GEN-LAST:event_upLabelMouseClicked

    private void gestureNameLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gestureNameLabelMouseClicked
        // TODO add your handling code here:
        if (isEnabled()) {
            playStopLblClicked();
        }
    }//GEN-LAST:event_gestureNameLabelMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel downLabel;
    private javax.swing.JLabel gestureNameLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel playStopLbl;
    private javax.swing.JLabel upLabel;
    // End of variables declaration//GEN-END:variables
}
