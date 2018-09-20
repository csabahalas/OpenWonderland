/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import imi.character.behavior.GoTo;
import imi.character.behavior.Task;
import imi.character.statemachine.GameContext;
import imi.character.statemachine.GameState;
import imi.character.statemachine.GameStateChangeListener;
import imi.character.statemachine.GameStateChangeListenerRegisterar;
import imi.character.statemachine.corestates.ActionState;
import imi.character.statemachine.corestates.IdleState;
import imi.character.statemachine.corestates.SitState;
import imi.objects.SpatialObject;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.avatarbase.client.GestureConnection;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.ShakeHandCam;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.GestureMessage;

/**
 *
 * Shake hand gesture task
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class ShakeHands implements Task {

    private String status = "idle";
    private GestureConnection gestureConn = null;
    private Cell targetCell = null;
    private Cell thisCell = null;
    private CellTransform targetTransform = null;
    private GoTo goTo = null;
    private Vector3f previousPosition = null;
    private Vector3f currentPosition = null;
    private int counter = 0;

    public enum CameraType {

        FIRST_PERSON,
        THIRD_PERSON,
        FRONT_CAMERA,
        CHASE_CAMERA,
        ZOOM_CAMERA
    };

    public ShakeHands(Cell targetCell, GestureConnection gestureConn, GameContext context) {
        this.targetCell = targetCell;
        this.thisCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        this.gestureConn = gestureConn;

        CellRenderer rend1 = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        WlAvatarCharacter otherAvatar = ((AvatarImiJME) rend1).getAvatarCharacter();
        previousPosition = otherAvatar.getController().getPosition();
        CellRenderer thisRend = thisCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        WlAvatarCharacter thisAvatar = ((AvatarImiJME) thisRend).getAvatarCharacter();

        this.targetTransform = getTargetTransform(targetCell, getDistance(thisAvatar, otherAvatar));
        float dist = 1.3f;
        this.goTo = new GoTo(targetTransform.getTranslation(null), otherAvatar.getForwardVector(), context);
        this.goTo.setApprovedDistanceFromGoal(dist);
    }

    public static float getDistance(imi.character.Character thisAvatar, imi.character.Character otherAvatar) {

        //distance will be different for male and female
        float distance = 0.86f;
        if (!thisAvatar.getCharacterParams().isMale()
                && !otherAvatar.getCharacterParams().isMale()) {
            distance = 0.57f;
        } else if (!thisAvatar.getCharacterParams().isMale()
                || !otherAvatar.getCharacterParams().isMale()) {
            distance = 0.74f;
        }
        return distance;
    }

    public void sendMessageForShakeHands(String thisId, String otherId) {
        GestureMessage msg = new GestureMessage();
        msg.setUserId(thisId);
        msg.setMessageString(otherId);
        msg.setMessageType("ShakeHands");
        gestureConn.send(msg);
    }

    public void sendMessageToOtherAvatarForIdle(String thisId, String otherId) {
        GestureMessage msg = new GestureMessage();
        msg.setUserId(thisId);
        msg.setMessageString(otherId);
        msg.setMessageType("Idle");
        gestureConn.send(msg);
    }

    public static Vector3f getLookDirection(Quaternion rotation, Vector3f v) {
        if (v == null) {
            v = new Vector3f(0, 0, 1);
        } else {
            v.set(0, 0, 1);
        }
        rotation.multLocal(v);
        v.normalizeLocal();
        return v;
    }

    private void done() {
        //place avatar properly
        Vector3f targetPos = new Vector3f(targetTransform.getTranslation(null));
        targetPos.setY(thisCell.getWorldTransform().getTranslation(null).y);
        ((AvatarCell) thisCell).triggerGoto(targetPos, targetTransform.getRotation(null));
    }

    public static CellTransform getTargetTransform(Cell targetCell, float distance) {
        Vector3f viewPosition = targetCell.getWorldTransform().getTranslation(null);
        Quaternion viewRotation = targetCell.getWorldTransform().getRotation(null);

        Vector3f lookAt = new Vector3f(0, 0, 1);
        viewRotation.multLocal(lookAt);
        lookAt.normalizeLocal();

        Vector3f translation = lookAt.mult(distance);
        translation = translation.add(viewPosition);

        Quaternion rotation = new Quaternion();
        rotation.lookAt(lookAt.negate(), new Vector3f(0, 1, 0));

        return new CellTransform(rotation, translation);
    }

    public Cell getTargetCell() {
        return targetCell;
    }

    public String getDescription() {
        return "Shake hand with other avatar";
    }

    public String getStatus() {
        return status;
    }

    public boolean verify() {
        if (status.equals("finished")) {
            return false;
        }
        return true;
    }

    public void onHold() {
        status = "on holds";
    }

    public SpatialObject getGoal() {
        return null;
    }

    public void update(float deltaTime) {
        if (goTo.verify()) {
            status = "goto";
            goTo.update(deltaTime);
        } else {
            if (status.equals("goto")) {
                //Just to finishe go to properly and then start shake hand
                counter++;
                if (counter == 25) {
                    status = "idle";
                }
            } else if (status.equals("idle")) {
                //place avatar properly
                done();
                GestureConnection.setBestViewCam(targetCell, 300, 290, 3f);
                status = "animating-camera";
            } else if (status.equals("animating-camera")) {
                ShakeHandCam camera = (ShakeHandCam) ViewManager.getViewManager()
                        .getCameraController();
                if (!camera.doneMoving()) {
                    return;
                }
                CellRenderer rend = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                final WlAvatarCharacter otherCharacter = ((AvatarImiJME) rend).getAvatarCharacter();
                currentPosition = otherCharacter.getController().getPosition();
                CellRenderer thisRend = thisCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                final WlAvatarCharacter thisCharacter = ((AvatarImiJME) thisRend).getAvatarCharacter();

                if ((otherCharacter.getContext().getCurrentState() instanceof SitState)
                        && (currentPosition.equals(previousPosition))) {
                    GameStateChangeListenerRegisterar.registerListener(new GameStateChangeListener() {
                        String prevState = "";

                        public void enterInState(GameState gs) {
                            String otherCharacterUserId = otherCharacter.getCharacterParams().getId();
                            String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
                            if (otherCharacterUserId.equals(stateChangeUserId)) {
                                if (gs.getName().equals("Idle") && prevState.equals("Sit")) {
                                    //other user changed from Sit To Idle 
                                    sendMessageForShakeHands(thisCharacter.getCharacterParams().getId(),
                                            otherCharacter.getCharacterParams().getId());
                                    GameStateChangeListenerRegisterar.deRegisterListener(this);
                                    status = "done";
                                }
                            }
                        }

                        public void exitfromState(GameState gs) {
                            String otherCharacterUserId = otherCharacter.getCharacterParams().getId();
                            String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
                            if (otherCharacterUserId.equals(stateChangeUserId)) {
                                prevState = gs.getName();
                            }
                        }

                        public void changeInState(GameState gs, String string, boolean bln, String string1) {
                        }
                    });

                    //pressing Idle Trigger for Sit To Stand (Other Avatar)
                    sendMessageToOtherAvatarForIdle(thisCharacter.getCharacterParams().getId(),
                            otherCharacter.getCharacterParams().getId());
                } else if ((otherCharacter.getContext().getCurrentState() instanceof IdleState) && (currentPosition.equals(previousPosition))) {
                    sendMessageForShakeHands(thisCharacter.getCharacterParams().getId(),
                            otherCharacter.getCharacterParams().getId());
                    status = "done";
                }
            } else if (status.equals("done")) {
                CellRenderer thisRend = thisCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                final WlAvatarCharacter thisCharacter = ((AvatarImiJME) thisRend).getAvatarCharacter();
                if (ActionState.isExitRepeat(thisCharacter.getContext())) {
                    status = "finished";
                }
            }
        }
    }
}
