/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */

package org.jdesktop.wonderland.modules.avatarbase.client.jme;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.CameraNode;
import imi.character.behavior.Task;
import imi.character.statemachine.GameContext;
import java.awt.event.MouseWheelEvent;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.utils.CellPlacementUtils;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassFocusListener;
import org.jdesktop.wonderland.client.jme.CameraController;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.MainFrameImpl;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseWheelEvent3D;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarClientPlugin;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.ShakeHands;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.TouchTo;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;

/**
 * best view controller for shake hand and other gestures
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class ShakeHandCam extends EventClassFocusListener implements CameraController {

    private final WorldManager wm;
    private final CellTransform start;
    private final CellTransform target;
    private long startTime;
    private final long moveTime;
    private volatile boolean doneMoving = false;
    private final float distance;
    private final CameraController prevCam;
    private final ShakeHands.CameraType prevCameraType;

    private CellTransform transform;
    private CameraNode cameraNode;
    private float zoom = 0;
    private Cell cell = null;

    public ShakeHandCam(CellTransform start, CellTransform target, float distance, long moveTime, CameraController prevCam, ShakeHands.CameraType prevCameraType, Cell cell) {
        this.start = start;
        this.target = target;
        this.distance = distance;
        this.moveTime = moveTime;
        this.wm = WorldManager.getDefaultWorldManager();
        this.prevCam = prevCam;
        this.prevCameraType = prevCameraType;
        this.cell = cell;
    }

    public void setEnabled(boolean enabled, CameraNode cameraNode) {
        if (enabled) {
            MainFrameImpl.getBestViewRB().setVisible(true);
            MainFrameImpl.getBestViewRB().setSelected(true);
            setCameraNode(cameraNode);
            setStartTime(System.currentTimeMillis());
            ClientContextJME.getInputManager().addGlobalEventListener(this);
        } else {
            setCameraNode(null);
            ClientContextJME.getInputManager().removeGlobalEventListener(this);
        }
    }

    public void compute() {
        if (doneMoving) {
            return;
        }
        // get the current time and location
        long relativeTime = System.currentTimeMillis() - getStartTime();
        float amt = (float) relativeTime / (float) moveTime;
        if (amt >= 1.0) {
            amt = 1.0f;
            doneMoving = true;
        }
        Quaternion t = target.getRotation(null);
        Vector3f distVec = CellPlacementUtils.getLookDirection(t, null);
        distVec.multLocal(distance);
        Vector3f origin = target.getTranslation(null);
        origin.subtractLocal(distVec);
        Vector3f st = start.getTranslation(null);
        st.interpolate(origin, amt);
        Quaternion sq = start.getRotation(null);
        sq.slerp(t, amt);
        transform = new CellTransform(sq, st);
    }

    public void commit() {
        if (transform != null) {
            CameraNode camera = getCameraNode();
            // apply zoom
            Vector3f loc = transform.getTranslation(null);
            Quaternion look = transform.getRotation(null);
            Vector3f z = look.mult(new Vector3f(0, 0, zoom));
            loc.addLocal(z);
            camera.setLocalRotation(look);
            camera.setLocalTranslation(loc);
            wm.addToUpdateList(camera);
        }
    }

    public void viewMoved(CellTransform worldTransform) {
        if (doneMoving) {

            boolean resetCam = true;
            Cell viewCell = ViewManager.getViewManager().getPrimaryViewCell();
            GameContext context = ((AvatarImiJME) viewCell.getCellRenderer(Cell.RendererType.RENDERER_JME)).getAvatarCharacter().getContext();

            Task task = context.getBehaviorManager().getCurrentTask();
            if (task instanceof ShakeHands
                    && ((ShakeHands) task).getTargetCell().getCellID().toString()
                    .equals(cell.getCellID().toString())) {
                resetCam = false;
            } else if (task instanceof TouchTo
                    && ((TouchTo) task).getTargetCell().getCellID().toString()
                    .equals(cell.getCellID().toString())) {
                resetCam = false;
            }

            if (resetCam) {
                ClientContextJME.getViewManager().setCameraController(prevCam);

                MainFrameImpl.getBestViewRB().setVisible(false);
                MainFrameImpl.getBestViewRB().setSelected(false);

                switch (prevCameraType) {
                    case CHASE_CAMERA:
                        AvatarClientPlugin.getChaseCameraMI().setSelected(true);
                        break;
                    case FIRST_PERSON:
                        MainFrameImpl.getFirstPersonRB().setSelected(true);
                        break;
                    case THIRD_PERSON:
                        MainFrameImpl.getThirdPersonRB().setSelected(true);
                        break;
                    case FRONT_CAMERA:
                        MainFrameImpl.getFrontPersonRB().setSelected(true);
                        break;
                    default:
                        MainFrameImpl.getZoomRB().setSelected(true);

                }
            }
        }
    }

    private synchronized CameraNode getCameraNode() {
        return cameraNode;
    }

    private synchronized void setCameraNode(CameraNode cameraNode) {
        this.cameraNode = cameraNode;
    }

    private synchronized void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private synchronized long getStartTime() {
        return startTime;
    }

    @Override
    public Class[] eventClassesToConsume() {
        return new Class[]{MouseWheelEvent3D.class};
    }

    @Override
    public void commitEvent(Event event) {
        MouseWheelEvent me = (MouseWheelEvent) ((MouseEvent3D) event).getAwtEvent();
        int clicks = me.getWheelRotation();
        zoom -= clicks * 0.2f;
    }

    public boolean doneMoving() {
        return doneMoving;
    }

}
