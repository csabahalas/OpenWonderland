/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior;

import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import imi.character.behavior.Task;
import imi.character.statemachine.corestates.ActionState;
import imi.objects.SpatialObject;
import imi.scene.PJoint;
import imi.scene.PNode;
import imi.scene.SkinnedMeshJoint;
import java.util.logging.Level;
import java.util.logging.Logger;
import javolution.util.FastTable;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.MainFrameImpl;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarClientPlugin;
import org.jdesktop.wonderland.modules.avatarbase.client.GestureConnection;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.ShakeHandCam;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.GestureMessage;

/**
 *
 * Turn head by changing the rotation of head using slerp concept
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class TurnHeadAdvanced implements Task {

    private static Logger logger = Logger.getLogger(TurnHeadAdvanced.class.getName());
    private String status = "idle";
    private Cell targetCell = null;
    private Cell thisCell = null;
    private imi.character.Character thisCharacter = null;
    private SkinnedMeshJoint jointToRotate = null;
    private PJoint hairJoint = null;
    private double targetAngleY = 0;
    private final double factorY = 4;
    private final double factorX = 2;
    private double angleY = 0;
    private double angleX = 0;
    private double targetAngleX = 0;
    private double targetAngleXOrig = 0;
    private TargetCellChangeListener cellChangeListener = null;
    private final float defaultX;
    private final float defaultY;
    private final float defaultZ;
    private boolean rotateHeadJoint = false;

    GestureConnection gestureConn;

    public TurnHeadAdvanced(final Cell thisCell, Cell targetCell, GestureConnection gestureConn) {
        this.thisCell = thisCell;
        CellRenderer thisRend = thisCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        this.thisCharacter = ((AvatarImiJME) thisRend).getAvatarCharacter();
        this.targetCell = targetCell;
        this.gestureConn = gestureConn;

        for (SkinnedMeshJoint joint : thisCharacter.getSkeleton().getSkinnedMeshJoints()) {
            if (hasHairAttachJoint(joint.getChildren())) {
                rotateHeadJoint = true;
                break;
            }
        }

        if (rotateHeadJoint) {
            this.jointToRotate = thisCharacter.getSkeleton().getSkinnedMeshJoint("Head");
            this.hairJoint = (PJoint) thisCharacter.getSkeleton().findChild("HairAttachmentJoint");
            if (hairJoint == null) {
                this.hairJoint = (PJoint) thisCharacter.getSkeleton().findChild("HairAttach");
            }
        } else {
            this.jointToRotate = thisCharacter.getSkeleton().getSkinnedMeshJoint("Neck");
        }

        Quaternion oldRot = jointToRotate.getBindPoseRef().getRotation();
        float[] angles = new float[3];
        angles = oldRot.toAngles(angles);
        defaultX = angles[0];
        defaultY = angles[1];
        defaultZ = angles[2];
    }

    private boolean hasHairAttachJoint(FastTable<PNode> ft) {
        for (PNode pn : ft) {
            if (pn.getName().equals("HairAttach")) {
                return true;
            }
        }
        return false;
    }

    private void display(FastTable<PNode> ft) {
        for (PNode pn : ft) {
            System.out.println(" >joint : " + pn.getName() + "|" + pn.getChildrenCount());
        }
    }

    public void restart(Cell newTargetCell) {
        //notify
        String targetName = "";
        if (targetCell instanceof AvatarCell) {
            CellRenderer rend = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            WlAvatarCharacter charr = ((AvatarImiJME) rend).getAvatarCharacter();
            targetName = charr.getName();
        } else {
            targetName = targetCell.getName();
        }
        for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
            lis.avatarsInteract(thisCell, targetCell, "Look At " + targetName, false);
        }

        targetCell.removeTransformChangeListener(cellChangeListener);
        cellChangeListener = null;
        targetCell = newTargetCell;
        status = "idle";
    }

    private void rotateEyeBall(boolean rotate) {
        thisCharacter.getEyes().getLeftEyeBall().setRotateEyeBall(rotate);
        thisCharacter.getEyes().getRightEyeBall().setRotateEyeBall(rotate);
        GestureMessage msg = new GestureMessage();
        msg.setUserId(thisCell.getCellID().toString());
        msg.setForGesture(true);
        msg.setMessageType("RotateEyeBall");
        if (rotate) {
            msg.setMessageString("Yes");
        } else {
            msg.setMessageString("No");
        }
        gestureConn.send(msg);
    }

    private Plane.Side getSideOfTargetCell(Vector3f currentAvatarVec) {
        Vector3f normal = currentAvatarVec.cross(Vector3f.UNIT_Y);
        float constant = thisCharacter.getController().getPosition().dot(normal);
        Plane yzPlane = new Plane(normal, constant);
        Vector3f pos = null;
        if (targetCell instanceof AvatarCell) {
            CellRenderer rend = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            WlAvatarCharacter charr = ((AvatarImiJME) rend).getAvatarCharacter();
            pos = charr.getSkeleton().getSkinnedMeshJoint("Head").getTransform()
                    .getWorldMatrix(false).getTranslation();
        } else {
            pos = targetCell.getWorldTransform().getTranslation(null);
        }

        return yzPlane.whichSide(pos);
    }

    private void calculateAnglesToLookAt() {
        Vector3f currentAvatarVec = thisCharacter.getSkeleton().getSkinnedMeshJoint("Head").getTransform().getWorldMatrix(false).getTranslation();
        Vector3f targetAvatarVec = null;
        if (targetCell instanceof AvatarCell) {
            CellRenderer rend = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            WlAvatarCharacter charr = ((AvatarImiJME) rend).getAvatarCharacter();
            Vector3f pos = charr.getSkeleton().getSkinnedMeshJoint("Head").getTransform()
                    .getWorldMatrix(false).getTranslation();
            targetAvatarVec = pos.subtract(currentAvatarVec);
        } else {
            targetAvatarVec = targetCell.getWorldTransform().getTranslation(null).subtract(currentAvatarVec);
        }

        Quaternion targetQ = new Quaternion();
        targetQ.lookAt(targetAvatarVec, Vector3f.ZERO);
        float[] angles = new float[3];
        targetQ.toAngles(angles);
        logger.log(Level.INFO, "Target angles : {0} | {1} | {2}", new Object[]{Math.toDegrees(angles[0]), Math.toDegrees(angles[1]), Math.toDegrees(angles[2])});

        Quaternion sourceQ = thisCharacter.getController().getQuaternion();
        float[] angles1 = new float[3];
        sourceQ.toAngles(angles1);
        logger.log(Level.INFO, "Source angles : {0} | {1} | {2}", new Object[]{Math.toDegrees(angles1[0]), Math.toDegrees(angles1[1]), Math.toDegrees(angles1[2])});

        targetAngleY = Math.toDegrees(angles[1]) - Math.toDegrees(angles1[1]);
        targetAngleX = -(Math.toDegrees(angles[0]) - Math.toDegrees(angles1[0]));
        targetAngleXOrig = -(Math.toDegrees(angles[0]) - Math.toDegrees(angles1[0]));

        //max threshould if 45 degree
        if (rotateHeadJoint) {
            if (targetAngleX <= -30) {
                targetAngleX = -30;
            } else if (targetAngleX >= 30) {
                targetAngleX = 30;
            }
        } else {
            if (targetAngleX <= -30) {
                targetAngleX = -30;
            } else if (targetAngleX >= 30) {
                targetAngleX = 30;
            }
        }

        if (targetAngleY > 90) {
            targetAngleY = 360 - targetAngleY;
            targetAngleY = -targetAngleY;
        } else if (targetAngleY < -90) {
            targetAngleY = targetAngleY + 360;
        }

        logger.log(Level.INFO, "targetAngleY : {0}", targetAngleY);
        logger.log(Level.INFO, "targetAngleX : {0}", targetAngleX);
    }

    private void addBound(Entity en, Vector3f loc, int corner) {
        Node rootNode = new Node("Bounds Viewer Node");
        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        RenderComponent rc = rm.createRenderComponent(rootNode);
        en.addComponent(RenderComponent.class, rc);

        // Set the Z-buffer state on the root node
        ZBufferState zbuf = (ZBufferState) rm.createRendererState(RenderState.StateType.ZBuffer);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        rootNode.setRenderState(zbuf);

        // Set the wireframe state on the root node
        WireframeState wf = (WireframeState) rm.createRendererState(RenderState.StateType.Wireframe);
        wf.setEnabled(true);
        rootNode.setRenderState(wf);

        Sphere sphere = new Sphere("Sphere", loc, corner, corner, 0.01f);
        rootNode.attachChild(sphere);

        WorldManager wm = ClientContextJME.getWorldManager();
        wm.addEntity(en);
    }

    private void updateHeadRotation(boolean reverse) {
        if (jointToRotate == null) {
            logger.warning("Head joint not found !!");
            status = "finished";
            return;
        }

        if (Math.abs(angleY - targetAngleY) >= factorY) {
            if (targetAngleY > angleY) {
                angleY = angleY + factorY;
            } else if (targetAngleY < angleY) {
                angleY = angleY - factorY;
            } else {

            }
        }

        if (Math.abs(angleX - targetAngleX) >= factorX) {
            if (targetAngleX > angleX) {
                angleX = angleX + factorX;
            } else if (targetAngleX < angleX) {
                angleX = angleX - factorX;
            } else {

            }
        }

        if (rotateHeadJoint) {
            float[] newAnglesForHead = new float[3];
            newAnglesForHead[0] = (float) Math.toRadians(angleX);
            newAnglesForHead[1] = (float) Math.toRadians(angleY);
            jointToRotate.getBindPoseRef().setRotation(new Quaternion(newAnglesForHead));

            float[] newAnglesForHair = new float[3];
            newAnglesForHair[0] = (float) Math.toRadians(angleX);
            newAnglesForHair[1] = (float) Math.toRadians(angleY);

            if (hairJoint != null) {
                hairJoint.getTransform().setLocalMatrix(new Quaternion(newAnglesForHair), Vector3f.ZERO);
            }
        } else {
            float[] newAnglesForNeck = new float[3];
            newAnglesForNeck[0] = (float) Math.toRadians(angleX) + defaultX;
            newAnglesForNeck[1] = defaultY;
            newAnglesForNeck[2] = (float) Math.toRadians(angleY) + defaultZ;
            jointToRotate.getBindPoseRef().setRotation(new Quaternion(newAnglesForNeck));
        }

        if (Math.abs(angleY - targetAngleY) <= factorY
                && Math.abs(angleX - targetAngleX) <= factorX) {
            if (reverse) {
                status = "finished";
                //notify
                String targetName = "";
                if (targetCell instanceof AvatarCell) {
                    CellRenderer rend = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                    WlAvatarCharacter charr = ((AvatarImiJME) rend).getAvatarCharacter();
                    targetName = charr.getName();
                } else {
                    targetName = targetCell.getName();
                }
                for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                    lis.avatarsInteract(thisCell, targetCell, "Look At " + targetName, false);
                }
            } else {
                status = "done";
            }
        }
    }

    public String getDescription() {
        return "";
    }

    public String getStatus() {
        return status;
    }

    public void onHold() {
        targetAngleY = 0;
        targetAngleX = 0;
        targetAngleXOrig = 0;
        status = "reverse";
        targetCell.removeTransformChangeListener(cellChangeListener);
        cellChangeListener = null;
        rotateEyeBall(true);
        forceCompleteTask();
    }

    public SpatialObject getGoal() {
        return null;
    }

    private class TargetCellChangeListener implements TransformChangeListener {

        public void transformChanged(Cell cell, ChangeSource source) {
            targetAngleY = 0;
            targetAngleX = 0;
            status = "reverse";
        }
    }

    public Cell getThisCell() {
        return thisCell;
    }

    public boolean verify() {
        if (status.equals("finished")) {
            targetCell.removeTransformChangeListener(cellChangeListener);
            cellChangeListener = null;
            rotateEyeBall(true);
            return false;
        } else {
            return true;
        }
    }

    public void update(float deltaTime) {
        if (status.equals("idle")) {
            rotateEyeBall(false);

            //calculate angles
            calculateAnglesToLookAt();

            //If object out of reach then just rotate at max angle
            Plane.Side sideXY = getSideOfTargetCell(thisCharacter.getController().getRightVector());
            if (sideXY.ordinal() == Plane.Side.POSITIVE.ordinal()) {
                Plane.Side sideYZ = getSideOfTargetCell(thisCharacter.getController().getForwardVector());
                if (sideYZ.ordinal() == Plane.Side.NEGATIVE.ordinal()) {
                    targetAngleY = -90;
                } else {
                    targetAngleY = 90;
                }
            }

            status = "animating";
            cellChangeListener = new TargetCellChangeListener();
            targetCell.addTransformChangeListener(cellChangeListener);
            updateHeadRotation(false);

            logger.log(Level.INFO, "targetAngleY : {0}", targetAngleY);
            logger.log(Level.INFO, "targetAngleX : {0}", targetAngleX);
            logger.log(Level.INFO, "angleY : {0}", angleY);
            logger.log(Level.INFO, "angleX : {0}", angleX);

            //check if the target cell has bestview then use it
            CellComponent comp = null;
            try {
                comp = targetCell.getComponent((Class<CellComponent>) Class
                        .forName("org.jdesktop.wonderland.modules.bestview.client.BestViewComponent"));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TurnHeadAdvanced.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (comp == null && thisCell.getCellID().toString().equals(ViewManager.getViewManager().getPrimaryViewCell().getCellID().toString())) {
                //target cell doen't have bestview so create new one and set it
                Vector3f currentAvatarVec = thisCharacter.getSkeleton().getSkinnedMeshJoint("Head").getTransform().getWorldMatrix(false).getTranslation();
                Vector3f targetAvatarVec = null;
                if (targetCell instanceof AvatarCell) {
                    CellRenderer rend = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                    WlAvatarCharacter charr = ((AvatarImiJME) rend).getAvatarCharacter();
                    Vector3f pos = charr.getSkeleton().getSkinnedMeshJoint("Head").getTransform()
                            .getWorldMatrix(false).getTranslation();
                    targetAvatarVec = pos.subtract(currentAvatarVec);
                } else {
                    targetAvatarVec = targetCell.getWorldTransform().getTranslation(null).subtract(currentAvatarVec);
                }
                setBestViewCam(thisCell, targetAvatarVec, currentAvatarVec);
            }

        } else if (status.equals("animating")) {
            updateHeadRotation(false);
        } else if (status.equals("done")) {
            if (ActionState.isExitRepeat1(thisCharacter.getContext())) {
                targetAngleY = 0;
                targetAngleX = 0;
                status = "reverse";
            }
        } else if (status.equals("reverse")) {
            updateHeadRotation(true);
        }
    }

    /**
     * set besview so that shake hand looks proper
     *
     * @param cell the cell w.r.t we need locate the camera
     * @param lookVec vector to look at
     * @param targetPos position for camera
     */
    public static void setBestViewCam(Cell cell, Vector3f lookVec, Vector3f targetPos) {
        //create best view camera
        Quaternion targetRot = ViewManager.getViewManager().getCameraTransform().getRotation(null);
        targetRot.lookAt(lookVec, new Vector3f(0, 1, 0));
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
     * force this task to finish
     */
    public void forceCompleteTask() {
        if (rotateHeadJoint) {
            jointToRotate.getBindPoseRef().setRotation(new Quaternion());
            if (hairJoint != null) {
                hairJoint.getTransform().setLocalMatrix(new Quaternion(), Vector3f.ZERO);
            }
        } else {
            jointToRotate.getBindPoseRef().setRotation(new Quaternion());
        }
        //notify
        String targetName = "";
        if (targetCell instanceof AvatarCell) {
            CellRenderer rend = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            WlAvatarCharacter charr = ((AvatarImiJME) rend).getAvatarCharacter();
            targetName = charr.getName();
        } else {
            targetName = targetCell.getName();
        }
        for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
            lis.avatarsInteract(thisCell, targetCell, "Look At " + targetName, false);
        }
        status = "finished";
    }
}
