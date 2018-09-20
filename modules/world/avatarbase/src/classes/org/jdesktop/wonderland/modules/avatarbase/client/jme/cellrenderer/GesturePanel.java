/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import imi.character.avatar.AvatarContext;
import imi.character.statemachine.GameState;
import imi.character.statemachine.GameStateChangeListener;
import imi.character.statemachine.GameStateChangeListenerRegisterar;
import imi.character.statemachine.corestates.ActionState;
import imi.character.statemachine.corestates.CycleActionState;
import imi.character.statemachine.corestates.IdleState;
import imi.character.statemachine.corestates.SitState;
import imi.scene.animation.AnimationListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarClientPlugin;
import org.jdesktop.wonderland.modules.avatarbase.client.GestureConnection;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.Gesture;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.Gesture.GestureType;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.GestureMessage;

/**
 *
 * panel with new gestures
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class GesturePanel extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(GesturePanel.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle");
    private WlAvatarCharacter avatar = null;
    private MyGameStateChangeListener stateChangeListener = null;
    private int animCounter = 0;
    private int visibleGesture = 0;
    private GestureConnection gestureConn;
    private static boolean selected1 = false;

    public GesturePanel(WlAvatarCharacter avatar, GestureConnection gestureConn) {
        this.avatar = avatar;
        this.gestureConn = gestureConn;
        initComponents();
        initials();
    }

    private void initials() {
        List<Gesture> gestureList = new LinkedList<Gesture>();
        boolean head = false;

        String gender = "Male";
        if (!avatar.getCharacterParams().isMale()) {
            gender = "Female";
        }
        if (avatar.getCharacterParams().isAnimateBody()) {
            gestureRowListPanel.removeAll();

            Map<String, Boolean> gestureMap = AvatarClientPlugin.getGestureMap();
            gestureList.add(new Gesture(gender + "_Yes", BUNDLE.getString("Yes"), Gesture.GestureType.HEAD, gestureMap.get("Yes") == null ? true : gestureMap.get("Yes"), Gesture.GesturePosition.ANY, false, false, true, false));
            gestureList.add(new Gesture(gender + "_No", BUNDLE.getString("No"), Gesture.GestureType.HEAD, gestureMap.get("No") == null ? true : gestureMap.get("No"), Gesture.GesturePosition.ANY, false, false, true, false));
            gestureList.add(new Gesture(gender + "_Wink", BUNDLE.getString("Wink"), Gesture.GestureType.HEAD, gestureMap.get("Wink") == null ? true : gestureMap.get("Wink"), Gesture.GesturePosition.ANY, false, false, true, true));
            if (selected1) {
                showShortListCB.setSelected(true);
            }
            if (showShortListCB.isSelected() || selected1) {
                boolean selected = false;
                for (Gesture g : gestureList) {
                    if (g.isVisible()) {
                        selected = true;
                    }
                }
                if (selected) {
                    head = true;
                    gestureList.add(new Gesture());
                } else {
                    //not add
                }
            } else {
                gestureList.add(new Gesture());
            }
            //torso
            gestureList.add(new Gesture(gender + "_AnswerCell", BUNDLE.getString("AnswerCell"), Gesture.GestureType.HEADTORSO, gestureMap.get("AnswerCell") == null ? true : gestureMap.get("AnswerCell"), Gesture.GesturePosition.ANY, true, false, true, true));
            gestureList.add(new Gesture(gender + "_Bow", BUNDLE.getString("Bow"), Gesture.GestureType.HEADTORSO, gestureMap.get("Bow") == null ? true : gestureMap.get("Bow"), Gesture.GesturePosition.STANDING, false, false, true, false));
            gestureList.add(new Gesture(gender + "_Breathing", BUNDLE.getString("Breath"), Gesture.GestureType.HEADTORSO, gestureMap.get("Breath") == null ? true : gestureMap.get("Breath"), Gesture.GesturePosition.ANY, true, true, true, false));
            gestureList.add(new Gesture(gender + "_Cheer", BUNDLE.getString("Cheer"), Gesture.GestureType.HEADTORSO, gestureMap.get("Cheer") == null ? true : gestureMap.get("Cheer"), Gesture.GesturePosition.ANY, true, false, true, true));
            gestureList.add(new Gesture(gender + "_Clap", BUNDLE.getString("Clap"), Gesture.GestureType.TORSO, gestureMap.get("Clap") == null ? true : gestureMap.get("Clap"), Gesture.GesturePosition.ANY, true, false, true, true));
            gestureList.add(new Gesture(gender + "_CrossHands", BUNDLE.getString("CrossHands"), Gesture.GestureType.TORSO, gestureMap.get("CrossHands") == null ? true : gestureMap.get("CrossHands"), Gesture.GesturePosition.SITTING, false, false, false, true));
            gestureList.add(new Gesture(gender + "_Crying", BUNDLE.getString("Crying"), Gesture.GestureType.HEADTORSO, gestureMap.get("Crying") == null ? true : gestureMap.get("Crying"), Gesture.GesturePosition.ANY, true, true, true, true));
            gestureList.add(new Gesture(gender + "_FoldArms", BUNDLE.getString("FoldArms"), Gesture.GestureType.TORSO, gestureMap.get("FoldArms") == null ? true : gestureMap.get("FoldArms"), Gesture.GesturePosition.ANY, true, false, false, true));
            gestureList.add(new Gesture(gender + "_Follow", BUNDLE.getString("Follow"), Gesture.GestureType.HEADTORSO, gestureMap.get("Follow") == null ? true : gestureMap.get("Follow"), Gesture.GesturePosition.STANDING, false, false, true, false));
            gestureList.add(new Gesture(gender + "_Gesticulating", BUNDLE.getString("Gesticulate"), Gesture.GestureType.HEADTORSO, gestureMap.get("Gesticulate") == null ? true : gestureMap.get("Gesticulate"), Gesture.GesturePosition.ANY, true, true, true, false));
            gestureList.add(new Gesture(gender + "_Hunch", BUNDLE.getString("Hunch"), Gesture.GestureType.HEADTORSO, gestureMap.get("Hunch") == null ? true : gestureMap.get("Hunch"), Gesture.GesturePosition.ANY, true, false, true, true));
            gestureList.add(new Gesture(gender + "_Laugh", BUNDLE.getString("Laugh"), Gesture.GestureType.HEADTORSO, gestureMap.get("Laugh") == null ? true : gestureMap.get("Laugh"), Gesture.GesturePosition.ANY, true, false, true, true));
            gestureList.add(new Gesture(gender + "_PublicSpeaking", BUNDLE.getString("PublicSpeaking"), Gesture.GestureType.HEADTORSO, gestureMap.get("PublicSpeaking") == null ? true : gestureMap.get("PublicSpeaking"), Gesture.GesturePosition.ANY, true, false, true, false));
            gestureList.add(new Gesture(gender + "_RaiseHand", BUNDLE.getString("RaiseHand"), Gesture.GestureType.TORSO, gestureMap.get("RaiseHand") == null ? true : gestureMap.get("RaiseHand"), Gesture.GesturePosition.ANY, true, false, false, true));
            gestureList.add(new Gesture(gender + "_Wave", BUNDLE.getString("Wave"), Gesture.GestureType.TORSO, gestureMap.get("Wave") == null ? true : gestureMap.get("Wave"), Gesture.GesturePosition.ANY, true, false, true, true));

            if (showShortListCB.isSelected() || selected1) {
                boolean selected = false;
                for (int i = 0; i < gestureList.size(); i++) {
                    if (head) {
                        if (i == 0 || i == 1 || i == 2 || i == 3) {

                        } else {
                            if (gestureList.get(i).isVisible()) {
                                selected = true;
                            }
                        }
                    } else {
                        if (i == 0 || i == 1 || i == 2) {

                        } else {
                            if (gestureList.get(i).isVisible()) {
                                selected = true;
                            }
                        }
                    }
                }
                if (selected) {
                    gestureList.add(new Gesture());
                } else {
                    //not add
                }
            } else {
                gestureList.add(new Gesture());
            }

            //leg
            gestureList.add(new Gesture(gender + "_CrossAnkles", BUNDLE.getString("CrossAnkles"), Gesture.GestureType.LEG, gestureMap.get("CrossAnkles") == null ? true : gestureMap.get("CrossAnkles"), Gesture.GesturePosition.SITTING, false, false, false, true));
            gestureList.add(new Gesture(gender + "_CrossLegs", BUNDLE.getString("CrossLegs"), Gesture.GestureType.LEG, gestureMap.get("CrossLegs") == null ? true : gestureMap.get("CrossLegs"), Gesture.GesturePosition.SITTING, false, false, false, true));

            if (showShortListCB.isSelected() || selected1) {
                boolean selected = false;
                for (int i = 0; i < gestureList.size(); i++) {
                    if (i == (gestureList.size() - 1) || i == (gestureList.size() - 2)) {
                        if (gestureList.get(i).isVisible()) {
                            selected = true;
                        }
                    }
                }
                if (selected) {
                    // gestureList.add(new Gesture());                    
                } else {
                    //not add
                }
            } else {
                gestureList.add(new Gesture());
            }
        }

        if (gestureList.isEmpty()) {
            //show no gesture message
            showShortListCB.setVisible(false);
            gestureRowListPanel.setLayout(new BorderLayout());
            JTextArea jta = new JTextArea(3, 30);
            jta.setEditable(false);
            jta.setLineWrap(true);
            jta.setWrapStyleWord(true);
            jta.setFont(new Font(null, Font.PLAIN, 16));
            jta.setText(BUNDLE.getString("Gestures_Not_Supported"));
            JScrollPane jsp = new JScrollPane(jta);
            gestureRowListPanel.add(jsp, BorderLayout.CENTER);
        } else {
            //add gesture rows      
            GridLayout gl;
            if (showShortListCB.isSelected() || selected1) {
                for (Gesture g : gestureList) {
                    if (g.isVisible()) {
                        visibleGesture++;
                    }
                }
                gl = new GridLayout(visibleGesture, 1);
                visibleGesture = 0;
            } else {
                gl = new GridLayout(gestureList.size(), 1);
            }
            //GridLayout gl = new GridLayout(gestureList.size(), 1);            
            gestureRowListPanel.setLayout(gl);
            for (Gesture g : gestureList) {
                if (showShortListCB.isSelected() || selected1) {
                    if (g.isVisible()) {
                        GestureRow row = new GestureRow(g, this);
                        gestureRowListPanel.add(row);
                    }
                } else {
                    GestureRow row = new GestureRow(g, this);
                    gestureRowListPanel.add(row);
                }
            }

            //game state change listener
            if (stateChangeListener == null) {
                stateChangeListener = new MyGameStateChangeListener();
                GameStateChangeListenerRegisterar.registerListener(stateChangeListener);
            }

            //enable or disable sitting gesture depends upon the current state
            if ((avatar == null) || (avatar.getContext() == null) || ((avatar.getContext().getCurrentState()) == null)) {
                if (avatar == null) {
                    logger.warning("avatar == null");
                } else if (avatar.getContext() == null) {
                    logger.warning("avatar.getContext() == null");
                } else if (avatar.getContext().getCurrentState() == null) {
                    logger.warning("avatar.getContext().getCurrentState() == null");
                }
                //This is to handle null pointer " + "avatar " + avatar  + "avatar.getContext() " + avatar.getContext()  + "avatar.getContext().getCurrentState() " + avatar.getContext().getCurrentState()
            } else {
                if (avatar.getContext().getCurrentState() instanceof SitState) {
                    enableSittingGestures(true);
                    enableStandingGestures(false);
                } else {
                    enableSittingGestures(false);
                    enableStandingGestures(true);
                }
            }

        }

    }

    /**
     * remove game state change listener
     */
    void removeStateChangeListener() {
        if (stateChangeListener != null) {
            GameStateChangeListenerRegisterar.deRegisterListener(stateChangeListener);
            stateChangeListener = null;
        }
    }

    /**
     * MyGameStateChangeListener
     */
    private class MyGameStateChangeListener implements GameStateChangeListener {

        public void enterInState(GameState gs) {
            String thisUserId = ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString();
            String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
            if (thisUserId.equals(stateChangeUserId)) {
                if (gs instanceof SitState) {
                    enableSittingGestures(true);
                    enableStandingGestures(false);
                } else if (gs instanceof IdleState) {
                    enableStandingGestures(true);
                    enableSittingGestures(false);
                } else {
                    if (avatar != null && avatar.getContext() != null && !avatar.getContext().isGesturePlayingInSitting()) {
                        enableSittingGestures(false);
                    }
                }
            }
        }

        public void exitfromState(GameState gs) {
            String thisUserId = ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString();
            String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
            if (thisUserId.equals(stateChangeUserId)) {
                if ((gs instanceof ActionState || gs instanceof CycleActionState)) {
                    if (animCounter != 0) {
                        stopAllGestures(false);
                        //because we have not called the stopGesture of this class
                        animCounter = 0;
                    }
                }
            }
        }

        public void changeInState(GameState gs, String string, boolean bln, String string1) {

        }

    }

    /**
     * Enable gestures
     *
     * @param enable
     */
    void enableSittingGestures(boolean enable) {
        for (Component c : gestureRowListPanel.getComponents()) {
            GestureRow gr = (GestureRow) c;
            if (gr.getGesture().getGesturePosition() == Gesture.GesturePosition.SITTING) {
                if (enable) {
                    gr.removeMouseListenerForTooltip();
                } else {
                    gr.addMouseListenerForTooltip();
                }
                gr.setEnabled(enable);
            }
        }
    }

    void enableStandingGestures(boolean enable) {
        for (Component c : gestureRowListPanel.getComponents()) {
            GestureRow gr = (GestureRow) c;
            if (gr.getGesture().getGesturePosition() == Gesture.GesturePosition.STANDING) {
                if (enable) {
                    gr.removeMouseListenerForTooltip();
                } else {
                    gr.addMouseListenerForTooltip();
                }
                gr.setEnabled(enable);
            }
        }
    }

    boolean stopCombineGestures() {
        for (Component c : gestureRowListPanel.getComponents()) {
            GestureRow gr = (GestureRow) c;
            if (gr.isGesturePlaying()) {
                gr.stopGesture(false, false);
            }
        }
        ViewCell thisCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        GestureMessage msg = new GestureMessage();
        msg.setUserId(thisCell.getCellID().toString());
        msg.setForGesture(true);
        msg.setMessageString("");
        GameState gs = avatar.getContext().getCurrentState();
        gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.EndOfCycle, "");
        msg.setMessageType("EndOfCycle");
        gestureConn.send(msg);
        animCounter = 0;
        return true;
    }

    /**
     * Stop other playing gesture with same gesture type
     *
     * @param gestureToPlay
     */
    boolean stopOtherGesture(Gesture gestureToPlay) {
        boolean wait = false;
        if (gestureToPlay.getGesturePosition() == Gesture.GesturePosition.STANDING) {
            logger.info("Gesture Panel - stopOtherGesture - stopAllGestures");
            stopAllGestures(true);
            avatar.getContext().setGesturePlayingInSitting(false);
            return true;
        }

        for (Component c : gestureRowListPanel.getComponents()) {
            GestureRow gr = (GestureRow) c;
            GestureType grType = gr.getGesture().getGestureType();
            GestureType gestureToPlayType = gestureToPlay.getGestureType();
            if (grType == Gesture.GestureType.HEADTORSO) {
                grType = Gesture.GestureType.TORSO;
            }
            if (gestureToPlayType == Gesture.GestureType.HEADTORSO) {
                gestureToPlayType = Gesture.GestureType.TORSO;
            }
            if (!gr.getGesture().equals(gestureToPlay) && gr.isGesturePlaying()) {
                //temporary disable the combine gesture
//                gr.stopGesture(true, true);
//                wait = true;
                logger.log(Level.INFO, "grType : {0}", grType);
                logger.log(Level.INFO, "gestureToPlayType : {0}", gestureToPlayType);

                if (grType.equals(gestureToPlayType)) {
                    gr.stopGesture(true, true);
                    wait = true;
                } else if (gr.getGesture().isLooping() && gestureToPlay.isLooping()) {
                    gr.stopGesture(true, true);
                    wait = true;
                } else if (grType != GestureType.HEAD && gestureToPlayType != GestureType.HEAD) {
                    gr.stopGesture(true, true);
                    wait = true;
                }
                if (gr.getGesture().getAnimName().contains("Wink")) {
                    wait = false;
                }
            }
        }
        return wait;
    }

    public int getAnimCounter() {
        return animCounter;
    }

    void stopAllGestures(boolean stopAnim) {
        for (Component c : gestureRowListPanel.getComponents()) {
            GestureRow gr = (GestureRow) c;
            if (gr.isGesturePlaying()) {
                gr.stopGesture(stopAnim, false);
            }
        }
    }

    /**
     * Play gesture First build gesture name Also restart if already in
     * CycleActionState
     *
     * @param gestureToPlay
     * @param intensityLevel
     * @param forIntensity
     */
    void playGesture(Gesture gestureToPlay, int intensityLevel, boolean forIntensity) {

        String gestureName = gestureToPlay.getAnimName();
        if (gestureName.equals("Male_Sit") || gestureName.equals("Female_Sit")) {
            doSitGesture(avatar);
        } else {

            //prepare gesture name
            if (gestureToPlay.getGesturePosition() == Gesture.GesturePosition.ANY
                    && gestureToPlay.hasSeparateAnimFiles()) {
                if (avatar.getContext().isGesturePlayingInSitting()
                        || (avatar.getContext().getCurrentState() instanceof SitState)) {
                    gestureName = gestureName + "Sitting";
                } else {
                    gestureName = gestureName + "Standing";
                }
            }
            if (gestureToPlay.hasIntensityLevel()) {
                switch (intensityLevel) {
                    case 1:
                        gestureName = gestureName + "Low";
                        break;
                    case 2:
                        gestureName = gestureName + "Medium";
                        break;
                    case 3:
                        gestureName = gestureName + "High";
                        break;
                }
            }

            ViewCell thisCell = ClientContextJME.getViewManager().getPrimaryViewCell();
            GestureMessage msg = new GestureMessage();
            msg.setUserId(thisCell.getCellID().toString());
            msg.setForGesture(true);
            msg.setMessageString("");

            logger.log(Level.INFO, "Curr State : {0}", avatar.getContext().getCurrentState());

            //restart the cycle action state if already in that state
            if (avatar.getContext().getCurrentState() instanceof CycleActionState) {
                CycleActionState cas = (CycleActionState) avatar.getContext().getCurrentState();
                if (animCounter > 0 && !forIntensity) {
                    cas.notifyAnimationMessage(AnimationListener.AnimationMessageType.RestartAndSave);
                    msg.setMessageType("RestartAndSave");
                    gestureConn.send(msg);
                } else {
                    if (animCounter != 0) {
                        animCounter--;
                    }
                    cas.notifyAnimationMessage(AnimationListener.AnimationMessageType.Restart);
                    msg.setMessageType("Restart");
                    gestureConn.send(msg);
                }
            }

            logger.info("\n----------------------");
            logger.log(Level.INFO, "playGesture()**GestureName : {0}", gestureName);
            logger.log(Level.INFO, "playGesture()**animCounter : {0}", animCounter);

            animCounter++;

            //play gesture 
            avatar.playAnimation(gestureName);
        }
    }

    public WlAvatarCharacter getAvatarCharacter() {
        return avatar;
    }

    /**
     * stop current gesture by sending animation message
     */
    void stopGesture(Gesture gestureToStop, int intensityLevel, boolean toPlayOtherGesture) {

        //prepare gesture name
        String gestureName = gestureToStop.getAnimName();
        if (gestureToStop.getGesturePosition() == Gesture.GesturePosition.ANY
                && gestureToStop.hasSeparateAnimFiles()) {
            if (avatar.getContext().isGesturePlayingInSitting()
                    || (avatar.getContext().getCurrentState() instanceof SitState)) {
                gestureName = gestureName + "Sitting";
            } else {
                gestureName = gestureName + "Standing";
            }
        }
        if (gestureToStop.hasIntensityLevel()) {
            switch (intensityLevel) {
                case 1:
                    gestureName = gestureName + "Low";
                    break;
                case 2:
                    gestureName = gestureName + "Medium";
                    break;
                case 3:
                    gestureName = gestureName + "High";
                    break;
            }
        }

        ViewCell thisCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        GestureMessage msg = new GestureMessage();
        msg.setUserId(thisCell.getCellID().toString());
        msg.setForGesture(true);
        msg.setMessageString("");

        animCounter--;
        GameState gs = avatar.getContext().getCurrentState();
        logger.info("\n----------------------");
        logger.log(Level.INFO, "stopGesture()**GestureName : {0}", gestureName);
        logger.log(Level.INFO, "stopGesture()**animCounter : {0}", animCounter);
        if (gs instanceof CycleActionState) {
            CycleActionState cas = (CycleActionState) gs;
            if (cas.isSimpleAction()) {
                gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.PlayOnceComplete);
                msg.setMessageType("RestartAndSave");
                gestureConn.send(msg);
            } else {
                if (animCounter == 0) {
                    if (toPlayOtherGesture) {
                        //it was EndOfCycleWithoutExitAnim, but as Nicole said we change to EndOfCycle. So play exit animation
                        if (gestureToStop.isNeedExitAnim()) {
                            gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.EndOfCycle, gestureName);
                            msg.setMessageType("EndOfCycle");
                            msg.setMessageString(gestureName);
                            gestureConn.send(msg);
                        } else {
                            gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.EndOfCycleWithoutExitAnim, gestureName);
                            msg.setMessageType("EndOfCycleWithoutExitAnim");
                            msg.setMessageString(gestureName);
                            gestureConn.send(msg);
                        }
                    } else {
                        if (gestureToStop.isNeedExitAnim()) {
                            gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.EndOfCycle, gestureName);
                            msg.setMessageType("EndOfCycle");
                            msg.setMessageString(gestureName);
                            gestureConn.send(msg);
                        } else {
                            gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.EndOfCycleWithoutExitAnim, gestureName);
                            msg.setMessageType("EndOfCycleWithoutExitAnim");
                            msg.setMessageString(gestureName);
                            gestureConn.send(msg);
                        }
                    }
                } else {
                    if (gestureToStop.getGestureType() == Gesture.GestureType.TORSO
                            || gestureToStop.getGestureType() == Gesture.GestureType.HEADTORSO) {
                        if (gestureToStop.isNeedExitAnim()) {
                            gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.ExitAnimation, gestureName + "|idle|true");
                            msg.setMessageType("ExitAnimation");
                            msg.setMessageString(gestureName + "|idle|true");
                            gestureConn.send(msg);
                        } else {
                            gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.ExitAnimation, gestureName + "|idle|false");
                            msg.setMessageType("ExitAnimation");
                            msg.setMessageString(gestureName + "|idle|false");
                            gestureConn.send(msg);
                        }

                    } else {
                        if (gestureToStop.isNeedExitAnim()) {
                            gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.ExitAnimation, gestureName + "|noidle|true");
                            msg.setMessageType("ExitAnimation");
                            msg.setMessageString(gestureName + "|noidle|true");
                            gestureConn.send(msg);
                        } else {
                            gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.ExitAnimation, gestureName + "|noidle|false");
                            msg.setMessageType("ExitAnimation");
                            msg.setMessageString(gestureName + "|noidle|false");
                            gestureConn.send(msg);
                        }
                    }
                }
            }
        }
    }

    /**
     * get gesture row object
     *
     * @param animName
     * @return
     */
    private GestureRow getGestureRow(String animName) {
        for (Component c : gestureRowListPanel.getComponents()) {
            if (c instanceof GestureRow) {
                GestureRow gr = (GestureRow) c;
                if ((!gr.getGesture().getAnimName().equals(""))
                        && animName.contains(gr.getGesture().getAnimName())) {
                    return gr;
                }
            }
        }
        return null;
    }

    /**
     * Invoke the Sit gesture.
     */
    private void doSitGesture(final WlAvatarCharacter avatar) {
        // Create a thread that sleeps and tells the sit action to stop.
        final Runnable stopSitRunnable = new Runnable() {

            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    logger.log(Level.INFO, "Sleep failed.", ex);
                }
                avatar.triggerActionStop(AvatarContext.TriggerNames.SitOnGround);
            }
        };

        // Spawn a thread to start the animation, which then spawns a thread
        // to stop the animation after a small sleep.
        new Thread() {

            @Override
            public void run() {
                avatar.triggerActionStart(AvatarContext.TriggerNames.SitOnGround);
                new Thread(stopSitRunnable).start();
            }
        }.start();
    }

    //this will call only when HUD closed by user for refreshed GP
    public void closeEventHUD() {
        stopAllGestures(true);
        gestureRowListPanel.revalidate();
        gestureRowListPanel.repaint();
        initials();
        this.repaint();
        this.revalidate();
    }

    public GestureConnection isConnectionNull() {
        return gestureConn;
    }

    public void setConnection(GestureConnection gestureConn) {
        this.gestureConn = gestureConn;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gestureRowListPanel = new javax.swing.JPanel();
        showShortListCB = new javax.swing.JCheckBox();

        gestureRowListPanel.setPreferredSize(new java.awt.Dimension(2, 2));

        javax.swing.GroupLayout gestureRowListPanelLayout = new javax.swing.GroupLayout(gestureRowListPanel);
        gestureRowListPanel.setLayout(gestureRowListPanelLayout);
        gestureRowListPanelLayout.setHorizontalGroup(
            gestureRowListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 158, Short.MAX_VALUE)
        );
        gestureRowListPanelLayout.setVerticalGroup(
            gestureRowListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 39, Short.MAX_VALUE)
        );

        showShortListCB.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        showShortListCB.setText("Show Short List");
        showShortListCB.setName(""); // NOI18N
        showShortListCB.setPreferredSize(null);
        showShortListCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showShortListCBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(showShortListCB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gestureRowListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(gestureRowListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showShortListCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showShortListCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showShortListCBActionPerformed
        // TODO add your handling code here:
        selected1 = showShortListCB.isSelected();
        stopAllGestures(true);
        gestureRowListPanel.revalidate();
        gestureRowListPanel.repaint();
        initials();
    }//GEN-LAST:event_showShortListCBActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel gestureRowListPanel;
    private javax.swing.JCheckBox showShortListCB;
    // End of variables declaration//GEN-END:variables

}
