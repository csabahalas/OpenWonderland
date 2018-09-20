/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import imi.character.avatar.AvatarContext;
import imi.character.statemachine.GameContext;
import imi.character.statemachine.GameState;
import imi.character.statemachine.corestates.ActionState;
import imi.character.statemachine.corestates.CycleActionState;
import imi.character.statemachine.corestates.IdleState;
import imi.character.statemachine.corestates.SitState;
import imi.scene.animation.AnimationComponent;
import imi.scene.animation.AnimationListener;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.MainFrameImpl;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.AvatarInteractionListener;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.AvatarInteractionListenerRegistrar;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.ShakeHands;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.TouchTo;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.ShakeHandCam;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.GestureConnectionType;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.GestureMessage;

/**
 *
 * Used to send/receive messages for the gestures
 * 
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class GestureConnection extends BaseConnection {

    private static final Logger logger = Logger.getLogger(GestureConnection.class.getName());
    private final ArrayList<String> shakeHandCells = new ArrayList<String>();

    public ConnectionType getConnectionType() {
        return GestureConnectionType.GESTURE_TYPE;
    }

    // send a message to the server.  Note this method
    // creates the message internally
    public void send(GestureMessage message) {
        super.send(message);
    }

    public boolean isShakingHand(String cellId1, String cellId2) {
        logger.log(Level.INFO, "Shake hand cells size= {0}", shakeHandCells.size());
        if (shakeHandCells.contains(cellId1) || shakeHandCells.contains(cellId2)) {
            return true;
        }
        return false;
    }

    public void setShakingHand(String cellId1, String cellId2, boolean shakingHand) {
        if (shakingHand) {
            if (!shakeHandCells.contains(cellId1)) {
                shakeHandCells.add(cellId1);
            }
            if (!shakeHandCells.contains(cellId2)) {
                shakeHandCells.add(cellId2);
            }
        } else {
            if (shakeHandCells.contains(cellId1)) {
                shakeHandCells.remove(cellId1);
            }
            if (shakeHandCells.contains(cellId2)) {
                shakeHandCells.remove(cellId2);
            }
        }
    }

    // handle a message we receive
    public void handleMessage(Message message) {
        if (message instanceof GestureMessage) {
            final ViewCell viewcell = ClientContextJME.getViewManager().getPrimaryViewCell();
            GestureMessage msg = (GestureMessage) message;
            if (msg.getMessageType().equals("ShakeHands")) {
                final Cell cell1 = AvatarClientPlugin.getAvatarCellByCellId(msg.getUserId());
                final Cell cell2 = AvatarClientPlugin.getAvatarCellByCellId(msg.getMessageString());

                setShakingHand(cell1.getCellID().toString(), cell2.getCellID().toString(), true);

                CellRenderer rend1 = cell1.getCellRenderer(Cell.RendererType.RENDERER_JME);
                WlAvatarCharacter avatar1 = ((AvatarImiJME) rend1).getAvatarCharacter();
                CellRenderer rend2 = cell2.getCellRenderer(Cell.RendererType.RENDERER_JME);
                WlAvatarCharacter avatar2 = ((AvatarImiJME) rend2).getAvatarCharacter();

                //Just to position this character at right position in other clients
                if (cell2.getCellID().toString().equals(viewcell.getCellID().toString())) {
                    ((AvatarCell) cell2).triggerGoto(avatar2.getPositionRef(), cell2.getWorldTransform().getRotation(null));
                }

                //play animation
                playAnimation(avatar1, "ShakeHands", false);
                playAnimation(avatar2, "ShakeHands", false);

                //notify
                for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                    lis.avatarsInteract(cell1, cell2, "ShakeHands", true);
                }
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //notify
                        for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                            lis.avatarsInteract(cell1, cell2, "ShakeHands", false);
                        }
                        if (cell1.getCellID().toString().equals(viewcell.getCellID().toString())) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(1500);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            setBestViewCam(cell2, 180, 0, 0, 0, -6.65f, 2.15f);
                            try {
                                TimeUnit.MILLISECONDS.sleep(1500);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            ViewManager.getViewManager().setCameraController(ViewManager.getDefaultCamera());
                            ((AvatarImiJME) cell1.getCellRenderer(Cell.RendererType.RENDERER_JME)).getAvatarCharacter().getContext().triggerPressed(AvatarContext.TriggerNames.Move_Back.ordinal());
                            try {
                                TimeUnit.MILLISECONDS.sleep(170);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            ((AvatarImiJME) cell1.getCellRenderer(Cell.RendererType.RENDERER_JME)).getAvatarCharacter().getContext().triggerReleased(AvatarContext.TriggerNames.Move_Back.ordinal());
                            ((AvatarImiJME) cell1.getCellRenderer(Cell.RendererType.RENDERER_JME)).getAvatarCharacter().getContext().triggerPressed(AvatarContext.TriggerNames.Move_Left.ordinal());
                            try {
                                TimeUnit.MILLISECONDS.sleep(500);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                ((AvatarImiJME) cell1.getCellRenderer(Cell.RendererType.RENDERER_JME)).getAvatarCharacter().getContext().triggerReleased(AvatarContext.TriggerNames.Move_Left.ordinal());
                                setShakingHand(cell1.getCellID().toString(), cell2.getCellID().toString(), false);
                            }
                        } else {
                            try {
                                TimeUnit.MILLISECONDS.sleep(3670);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                setShakingHand(cell1.getCellID().toString(), cell2.getCellID().toString(), false);
                            }
                        }

                    }
                }).start();
            }

            if (!(msg.getUserId().equals(viewcell.getCellID().toString()))) {
                Cell msgUserCell = AvatarClientPlugin.getAvatarCellByCellId(msg.getUserId());
                logger.log(Level.INFO, "msgUserCell : {0}", msgUserCell);
                CellRenderer rend = msgUserCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                WlAvatarCharacter myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
                GameContext context = myAvatar.getContext();
                GameState state = context.getCurrentState();
                if (state instanceof CycleActionState) {
                    if (msg.getMessageType().equals("EndOfCycle")) {
                        state.notifyAnimationMessage(AnimationListener.AnimationMessageType.EndOfCycle, msg.getMessageString());
                    } else if (msg.getMessageType().equals("EndOfCycleWithoutExitAnim")) {
                        state.notifyAnimationMessage(AnimationListener.AnimationMessageType.EndOfCycleWithoutExitAnim, msg.getMessageString());
                    } else if (msg.getMessageType().equals("Restart")) {
                        state.notifyAnimationMessage(AnimationListener.AnimationMessageType.Restart, msg.getMessageString());
                    } else if (msg.getMessageType().equals("RestartAndSave")) {
                        state.notifyAnimationMessage(AnimationListener.AnimationMessageType.RestartAndSave, msg.getMessageString());
                    } else if (msg.getMessageType().equals("ExitAnimation")) {
                        state.notifyAnimationMessage(AnimationListener.AnimationMessageType.ExitAnimation, msg.getMessageString());
                    }
                } else if (msg.getMessageType().equals("TouchArm")
                        || msg.getMessageType().equals("TouchArmReverse")) {
                    final Cell cell1 = msgUserCell;
                    final Cell cell2 = AvatarClientPlugin.getAvatarCellByCellId(msg.getMessageString());
                    CellRenderer rend1 = cell1.getCellRenderer(Cell.RendererType.RENDERER_JME);
                    CellRenderer rend2 = cell2.getCellRenderer(Cell.RendererType.RENDERER_JME);
                    final WlAvatarCharacter avatar1 = ((AvatarImiJME) rend1).getAvatarCharacter();
                    final WlAvatarCharacter avatar2 = ((AvatarImiJME) rend2).getAvatarCharacter();

                    String otherAvatarPosition = "Standing";
                    if (avatar2.getContext().getCurrentState() instanceof IdleState) {
                        otherAvatarPosition = "Standing";
                    } else if (avatar2.getContext().getCurrentState() instanceof SitState) {
                        SitState sitState = (SitState) avatar2.getContext().getCurrentState();
                        if (sitState.isSleeping()) {
                            otherAvatarPosition = "Sleeping";
                        } else {
                            otherAvatarPosition = "Sitting";
                        }
                    }

                    String animName = "";
                    if (otherAvatarPosition.equals("Standing")) {
                        animName = "TouchArmStanding";
                    } else if (otherAvatarPosition.equals("Sitting")) {
                        if (avatar2.getCharacterParams().isMale()) {
                            animName = "TouchArmSittingToMale";
                        } else {
                            animName = "TouchArmSittingToFemale";
                        }
                    } else if (otherAvatarPosition.equals("Sleeping")) {
                        animName = "TouchArmLieDown";
                    }

                    if (otherAvatarPosition.equals("Standing")) {
                        //Just to position this character at right position in other clients
                        if (cell2.getCellID().toString().equals(viewcell.getCellID().toString())) {
                            ((AvatarCell) cell2).triggerGoto(avatar2.getPositionRef(), cell2.getWorldTransform().getRotation(null));
                        }
                    }

                    if (msg.getMessageType().equals("TouchArmReverse")) {
                        playAnimation(avatar1, animName, true);
                    } else {
                        playAnimation(avatar1, animName, false);
                    }

                    //notify
                    for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                        lis.avatarsInteract(cell1, cell2, animName, true);
                    }

                    forExitAnimation(avatar1, animName, true);

                    final String animNameF = animName;
                    new Thread(new Runnable() {

                        public void run() {
                            try {
                                Thread.sleep(TouchTo.getExitAnimTimer());
                            } catch (InterruptedException ex) {
                                Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            //notify
                            for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                                lis.avatarsInteract(cell1, cell2, animNameF, false);
                            }
                        }
                    }).start();
                } else if (msg.getMessageType().equals("TouchShoulder")
                        || msg.getMessageType().equals("TouchShoulderReverse")) {
                    final Cell cell1 = msgUserCell;
                    final Cell cell2 = AvatarClientPlugin.getAvatarCellByCellId(msg.getMessageString());
                    CellRenderer rend1 = cell1.getCellRenderer(Cell.RendererType.RENDERER_JME);
                    CellRenderer rend2 = cell2.getCellRenderer(Cell.RendererType.RENDERER_JME);
                    final WlAvatarCharacter avatar1 = ((AvatarImiJME) rend1).getAvatarCharacter();
                    final WlAvatarCharacter avatar2 = ((AvatarImiJME) rend2).getAvatarCharacter();

                    String otherAvatarPosition = "Standing";
                    if (avatar2.getContext().getCurrentState() instanceof IdleState) {
                        otherAvatarPosition = "Standing";
                    } else if (avatar2.getContext().getCurrentState() instanceof SitState) {
                        SitState sitState = (SitState) avatar2.getContext().getCurrentState();
                        if (sitState.isSleeping()) {
                            otherAvatarPosition = "Sleeping";
                        } else {
                            otherAvatarPosition = "Sitting";
                        }
                    }
                    String animName = "";
                    if (otherAvatarPosition.equals("Standing")) {
                        animName = "TouchShoulderStanding";
                    } else if (otherAvatarPosition.equals("Sitting")) {
                        animName = "TouchShoulderSitting";
                    } else if (otherAvatarPosition.equals("Sleeping")) {
                        animName = "TouchShoulderLieDown";
                    }

                    if (otherAvatarPosition.equals("Standing")) {
                        //Just to position this character at right position in other clients
                        if (cell2.getCellID().toString().equals(viewcell.getCellID().toString())) {
                            ((AvatarCell) cell2).triggerGoto(avatar2.getPositionRef(), cell2.getWorldTransform().getRotation(null));
                        }
                    }

                    if (msg.getMessageType().equals("TouchShoulderReverse")) {
                        playAnimation(avatar1, animName, true);
                    } else {
                        playAnimation(avatar1, animName, false);
                    }

                    //notify
                    for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                        lis.avatarsInteract(cell1, cell2, animName, true);
                    }

                    forExitAnimation(avatar1, animName, true);

                    final String animNameF = animName;
                    new Thread(new Runnable() {

                        public void run() {
                            try {
                                Thread.sleep(TouchTo.getExitAnimTimer());
                            } catch (InterruptedException ex) {
                                Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            //notify
                            for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                                lis.avatarsInteract(cell1, cell2, animNameF, false);
                            }
                        }
                    }).start();
                } else if (msg.getMessageType().equals("Idle")) {
                    Cell cell2 = AvatarClientPlugin.getAvatarCellByCellId(msg.getMessageString());
                    CellRenderer rend2 = cell2.getCellRenderer(Cell.RendererType.RENDERER_JME);
                    WlAvatarCharacter avatar2 = ((AvatarImiJME) rend2).getAvatarCharacter();
                    avatar2.getContext().triggerPressed(AvatarContext.TriggerNames.Idle.ordinal());
                    avatar2.getContext().triggerReleased(AvatarContext.TriggerNames.Idle.ordinal());
                }
                if (msg.getMessageType().equals("RotateEyeBall")) {
                    if (msg.getMessageString().equals("No")) {
                        Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
                        CellRenderer rend2 = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                        WlAvatarCharacter avatarChar = ((AvatarImiJME) rend2).getAvatarCharacter();
                        avatarChar.getEyes().getLeftEyeBall().setRotateEyeBall(false);
                        avatarChar.getEyes().getRightEyeBall().setRotateEyeBall(false);
                    } else {
                        Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
                        CellRenderer rend2 = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                        WlAvatarCharacter avatarChar = ((AvatarImiJME) rend2).getAvatarCharacter();
                        avatarChar.getEyes().getLeftEyeBall().setRotateEyeBall(true);
                        avatarChar.getEyes().getRightEyeBall().setRotateEyeBall(true);
                    }
                }
                if (msg.getMessageType().equals("StartTurnHead")) {
                    Cell thisCell = msgUserCell;
                    Cell targetCell = AvatarClientPlugin.getAvatarCellByCellId(msg.getMessageString());
                    AvatarClientPlugin.lookAtAdvancedLocal(thisCell, targetCell);
                }
            }
        }
    }

    private volatile boolean playExitAnim = true;

    private void forExitAnimation(final imi.character.Character avatar1, final String animName, final boolean reverse) {
        Thread th = new Thread(new Runnable() {

            public void run() {
                try {
                    final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                    ses.scheduleAtFixedRate(new Runnable() {

                        public void run() {
                            if (ActionState.isExitRepeat1(avatar1.getContext())) {
                                ses.shutdown();
                            }
                        }
                    }, 0, 1, TimeUnit.MILLISECONDS);
                    Thread.sleep(TouchTo.getExitAnimTimer());
                    if (playExitAnim) {
                        ses.shutdown();
                        playAnimation(avatar1, animName, reverse);
                    } else {
                        playExitAnim = true;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(GestureConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        th.start();
    }

    /**
     * set besview so that shake hand looks proper
     *
     * @param cell the cell w.r.t we need locate the camera
     * @param angleY1 angle to rotate a vector from avatar's pos
     * @param angleY2 for look direction
     * @param multFactor for changing the magnitude of vector from avatar
     */
    public static void setBestViewCam(Cell cell, float angleY1, float angleY2, float multFactor) {
        setBestViewCam(cell, angleY1, 0, angleY2, 0, multFactor, 1.6f);
    }

    public static void setBestViewCam(Cell cell, float angleY1, float angleX1, float angleY2, float angleX2, float multFactor, float yOffset) {
        setBestViewCam(cell, angleY1, angleX1, angleY2, angleX2, multFactor, false, yOffset);
    }

    /**
     * set besview so that shake hand looks proper
     *
     * @param cell the cell w.r.t we need locate the camera
     * @param angleY1 Y angle to rotate a vector from avatar's pos
     * @param angleX1 X angle to rotate a vector from avatar's pos
     * @param angleY2 Y angle for look direction
     * @param angleX2 X angle for look direction
     * @param multFactor for changing the magnitude of vector from avatar
     * @param yOffset yoffset to add
     */
    public static void setBestViewCam(Cell cell, float angleY1, float angleX1, float angleY2, float angleX2, float multFactor, boolean forSleeping, float yOffset) {
        //create best view camera
        WlAvatarCharacter thisCharacter = ((AvatarImiJME) cell
                .getCellRenderer(Cell.RendererType.RENDERER_JME)).getAvatarCharacter();

        Vector3f targetPos = cell.getWorldTransform().getTranslation(null);
        Quaternion targetRot = ViewManager.getViewManager().getCameraTransform().getRotation(null);

        float[] angles = new float[3];
        angles[0] = (float) Math.toRadians(angleX1);
        angles[1] = (float) Math.toRadians(angleY1);
        Quaternion quat = new Quaternion(angles);
        Vector3f adjVec = thisCharacter.getForwardVector().mult(multFactor).negate();
        adjVec = quat.mult(adjVec);
        targetPos = targetPos.add(adjVec);
        targetPos.y = targetPos.y + yOffset;

        if (forSleeping) {
            targetPos.y = targetPos.y + 1.2f;
        }

        angles = new float[3];
        angles[0] = (float) Math.toRadians(angleX2);
        angles[1] = (float) Math.toRadians(angleY2);
        quat = new Quaternion(angles);
        Vector3f lookAtVec = quat.mult(thisCharacter.getForwardVector());
        if (forSleeping) {
            lookAtVec.y = lookAtVec.y - 0.8f;
        }
        targetRot.lookAt(lookAtVec, new Vector3f(0, 1, 0));

        CellTransform target = new CellTransform(targetRot, targetPos);

        ShakeHands.CameraType type = ShakeHands.CameraType.ZOOM_CAMERA;
        if (MainFrameImpl.getFirstPersonRB().isSelected()) {
            type = ShakeHands.CameraType.FIRST_PERSON;
        }
        if (MainFrameImpl.getFrontPersonRB().isSelected()) {
            type = ShakeHands.CameraType.FRONT_CAMERA;
        }
        if (MainFrameImpl.getThirdPersonRB().isSelected()) {
            type = ShakeHands.CameraType.THIRD_PERSON;
        }
        if (AvatarClientPlugin.getChaseCameraMI().isSelected()) {
            type = ShakeHands.CameraType.CHASE_CAMERA;
        }

        ShakeHandCam camera
                = new ShakeHandCam(ViewManager.getViewManager().getCameraTransform(), target, 0f, 1500, ViewManager.getViewManager().getCameraController(), type, cell);
        ClientContextJME.getViewManager().setCameraController(camera);
    }

    /**
     * playing ShakeHand Animation for other Avatar in remote machine
     *
     * @param character
     */
    public void playAnimation(imi.character.Character character, String animName, boolean reverse) {
        if (character.getCharacterParams().isAnimateBody()) {
            character.getSkeleton().getAnimationState().setTransitionCycleMode(AnimationComponent.PlaybackMode.PlayOnce);
            if (character.getCharacterParams().isMale()) {
                character.getSkeleton().transitionTo("Male_" + animName, reverse);
            } else {
                character.getSkeleton().transitionTo("Female_" + animName, reverse);
            }
        }
    }

}
