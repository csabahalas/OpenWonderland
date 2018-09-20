/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import imi.character.avatar.AvatarContext;
import imi.character.behavior.GoTo;
import imi.character.behavior.Task;
import imi.character.statemachine.GameContext;
import imi.character.statemachine.corestates.ActionState;
import imi.character.statemachine.corestates.IdleState;
import imi.character.statemachine.corestates.SitState;
import imi.objects.SpatialObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.avatarbase.client.GestureConnection;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.ShakeHandCam;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;

/**
 * Basic task to touch to avatar's body part
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public abstract class TouchTo implements Task {

    protected String status = "idle";
    protected GestureConnection gestureConn = null;
    protected Cell targetCell = null;
    protected Cell thisCell = null;
    protected WlAvatarCharacter targetCharacter = null;
    protected WlAvatarCharacter thisCharacter = null;
    protected CellTransform targetTransform = null;
    protected Vector3f previousPosition = null;
    protected Vector3f currentPosition = null;
    protected String avatarPosition = "Standing";
    private TargetCellChangeListener cellChangeListener = null;
    private GoTo goTo = null;
    private int counter = 0;
    private static final int exitAnimTimer = 3000;
    private volatile boolean playExitAnim = true;

    public TouchTo(Cell targetCell, GestureConnection gestureConn, GameContext context) {
        this.targetCell = targetCell;
        this.thisCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        this.gestureConn = gestureConn;

        CellRenderer rend1 = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        targetCharacter = ((AvatarImiJME) rend1).getAvatarCharacter();
        previousPosition = targetCharacter.getController().getPosition();
        CellRenderer thisRend = thisCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        thisCharacter = ((AvatarImiJME) thisRend).getAvatarCharacter();

        if (targetCharacter.getContext().getCurrentState() instanceof IdleState) {
            avatarPosition = "Standing";
        } else if (targetCharacter.getContext().getCurrentState() instanceof SitState) {
            SitState sitState = (SitState) targetCharacter.getContext().getCurrentState();
            if (sitState.isSleeping()) {
                avatarPosition = "Sleeping";
            } else {
                avatarPosition = "Sitting";
            }
        }

        this.targetTransform = getTargetTransform();

        this.goTo = new GoTo(targetTransform.getTranslation(null), context);
        this.goTo.setApprovedDistanceFromGoal(1.3f);
    }

    public String getDescription() {
        return "basic touch task";
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

    public void update(float deltaTime) {

        if ((this instanceof TouchArm) && avatarPosition.equals("Sleeping")) {
            status = "finishing";
            return;
        }

        if (goTo.verify()) {
            status = "goto";
            goTo.update(deltaTime);
        } else {
            if (status.equals("goto")) {
                //Just to finishe go to properly and then start touch
                counter++;
                if (counter == 25) {
                    status = "idle";
                    thisCharacter.getContext().triggerReleased(AvatarContext.TriggerNames.Move_Left.ordinal());
                    thisCharacter.getContext().triggerReleased(AvatarContext.TriggerNames.Move_Right.ordinal());
                }
            } else if (status.equals("idle")) {
                done();
                if (avatarPosition.equals("Standing")) {
                    GestureConnection.setBestViewCam(targetCell, 300, 290, 3f);
                } else if (avatarPosition.equals("Sitting")) {
                    GestureConnection.setBestViewCam(targetCell, 0, 0, 3f);
                } else if (avatarPosition.equals("Sleeping")) {
                    GestureConnection.setBestViewCam(targetCell, 135, 300, 135, 300, 3f, true, 0f);
                }
                status = "animating-camera";
            } else if (status.equals("animating-camera")) {
                ShakeHandCam camera = (ShakeHandCam) ViewManager.getViewManager()
                        .getCameraController();
                if (camera.doneMoving()) {
                    currentPosition = targetCharacter.getController().getPosition();
                    if (currentPosition.equals(previousPosition)) {
                        playTouchAnimation(thisCharacter, false);
                        sendMessageToOtherAvatar(thisCharacter.getCharacterParams().getId(),
                                targetCharacter.getCharacterParams().getId(), false);

                    }
                    status = "animating";
                }
            } else if (status.equals("animating")) {
                status = "finishing";
                Thread th = new Thread(new Runnable() {

                    public void run() {
                        try {
                            Thread.sleep(getExitAnimTimer());
                            if (playExitAnim()) {
                                playTouchAnimation(thisCharacter, true);
                            }
                            status = "finished";
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TouchTo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                th.start();
            } else if (status.equals("finishing")) {
                if (ActionState.isExitRepeat1(thisCharacter.getContext())) {
                    playExitAnim = false;
                    status = "finished";
                }
            }
        }
    }

    public boolean playExitAnim() {
        return playExitAnim;
    }

    public static int getExitAnimTimer() {
        return exitAnimTimer;
    }

    public void onHold() {
        status = "on holds";
        targetCell.removeTransformChangeListener(cellChangeListener);
        cellChangeListener = null;
    }

    public SpatialObject getGoal() {
        return null;
    }

    private void done() {
        //place avatar properly
        Vector3f targetPos = new Vector3f(targetTransform.getTranslation(null));
        targetPos.setY(thisCell.getWorldTransform().getTranslation(null).y);
        AvatarImiJME rend = (AvatarImiJME) thisCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        rend.triggerGotoOnly(targetPos, targetTransform.getRotation(null));
    }

    protected abstract void sendMessageToOtherAvatar(String thisId, String otherId, boolean reverse);

    protected abstract void playTouchAnimation(final imi.character.Character character, boolean reverse);

    protected abstract float getMinDistance();

    protected abstract float getVectorRotationFactor();

    protected abstract float getLookRotationFactor();

    private static Vector3f getLookDirection(Quaternion rotation, Vector3f v) {
        if (v == null) {
            v = new Vector3f(0, 0, 1);
        } else {
            v.set(0, 0, 1);
        }
        rotation.multLocal(v);
        v.normalizeLocal();
        return v;
    }

    protected CellTransform getTargetTransform() {
        Vector3f viewPosition = targetCell.getWorldTransform().getTranslation(null);
        Quaternion viewRotation = targetCell.getWorldTransform().getRotation(null);

        Vector3f lookAt = new Vector3f(1f, 0f, 0f);
        viewRotation.multLocal(lookAt);
        lookAt.normalizeLocal();

        Vector3f translation = lookAt.mult(getMinDistance());
        translation = translation.add(viewPosition);

        Quaternion rotation = new Quaternion();
        rotation.lookAt(lookAt.negate(), new Vector3f(0, 1, 0));
        return new CellTransform(rotation, translation);
    }

    private class TargetCellChangeListener implements TransformChangeListener {

        public void transformChanged(Cell cell, TransformChangeListener.ChangeSource source) {
            playTouchAnimation(thisCharacter, true);
            sendMessageToOtherAvatar(thisCharacter.getCharacterParams().getId(),
                    targetCharacter.getCharacterParams().getId(), true);

            status = "finished";
        }

    }

    public Cell getTargetCell() {
        return targetCell;
    }

}
