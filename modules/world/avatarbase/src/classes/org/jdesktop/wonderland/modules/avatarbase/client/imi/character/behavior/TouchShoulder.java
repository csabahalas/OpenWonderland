/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import imi.character.statemachine.GameContext;
import imi.scene.animation.AnimationComponent;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.avatarbase.client.GestureConnection;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.GestureMessage;

/**
 *
 * Touch shoulder gesture task
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class TouchShoulder extends TouchTo {

    public TouchShoulder(Cell targetCell, GestureConnection gestureConn, GameContext context) {
        super(targetCell, gestureConn, context);
    }

    public void playTouchAnimation(final imi.character.Character character, boolean reverse) {
        if (character.getCharacterParams().isAnimateBody()) {
            character.getSkeleton().getAnimationState()
                    .setTransitionCycleMode(AnimationComponent.PlaybackMode.PlayOnce);

            String animName = "";
            if (character.getCharacterParams().isMale()) {
                animName = animName + "Male_TouchShoulder";
            } else {
                animName = animName + "Female_TouchShoulder";
            }
            if (avatarPosition.equals("Standing")) {
                animName = animName + "Standing";
            } else if (avatarPosition.equals("Sitting")) {
                animName = animName + "Sitting";
            } else if (avatarPosition.equals("Sleeping")) {
                animName = animName + "LieDown";
            }
            character.getSkeleton().transitionTo(animName, reverse);
            //notify
            for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                lis.avatarsInteract(thisCell, targetCell, animName, !reverse);
            }
        } else {
            status = "finished";
        }
    }

    @Override
    protected void sendMessageToOtherAvatar(String thisId, String otherId, boolean reverse) {
        GestureMessage msg = new GestureMessage();
        msg.setUserId(thisId);
        msg.setMessageString(otherId);
        if (reverse) {
            msg.setMessageType("TouchShoulderReverse");
        } else {
            msg.setMessageType("TouchShoulder");
        }
        gestureConn.send(msg);
    }

    @Override
    protected float getMinDistance() {
        //distance will be different for male/female in sitting/stading/sleeping position
        float distance = 0;
        if (avatarPosition.equals("Standing")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.69f;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.71f;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.54f;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.56f;//DONE
            }
        } else if (avatarPosition.equals("Sitting")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.35f;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.50f;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.38f;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.5f;//DONE
            }
        } else if (avatarPosition.equals("Sleeping")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.05f;
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.0001f;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.01f;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.01f;
            }
        }
        return distance;
    }

    @Override
    public float getVectorRotationFactor() {
        float angle = 0;
        if (avatarPosition.equals("Standing")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 48;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 57;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 40;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 47;//DONE
            }
        } else if (avatarPosition.equals("Sitting")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 71;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 99;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 85;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 102;//DONE
            }
        } else if (avatarPosition.equals("Sleeping")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 300;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 180;
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 270;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 270;
            }
        }
        return angle;
    }

    @Override
    public float getLookRotationFactor() {
        float angle = 0;
        if (avatarPosition.equals("Standing")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 96;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 99;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 90;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 90;//DONE
            }
        } else if (avatarPosition.equals("Sitting")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 92;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 101;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 103;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 105;//DONE
            }
        } else if (avatarPosition.equals("Sleeping")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 325;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 355;
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 340;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 0;
            }
        }
        return angle;
    }

    @Override
    protected CellTransform getTargetTransform() {
        Quaternion viewRotation = targetCell.getWorldTransform().getRotation(null);
        WlAvatarCharacter character = ((AvatarImiJME) targetCell
                .getCellRenderer(Cell.RendererType.RENDERER_JME)).getAvatarCharacter();

        Vector3f lookAt = new Vector3f(0f, 0f, 1f);
        viewRotation.multLocal(lookAt);
        lookAt.normalizeLocal();

        float[] angles = new float[3];
        angles[1] = (float) Math.toRadians(getVectorRotationFactor());
        Quaternion vecRot = new Quaternion(angles);
        lookAt = vecRot.mult(lookAt);

        Vector3f targetPosition = character.getPositionRef()
                .add(lookAt.mult(getMinDistance()));

        angles = new float[3];
        angles[1] = (float) Math.toRadians(getLookRotationFactor());
        Quaternion adjust = new Quaternion(angles);

        Quaternion rotation = new Quaternion();
        rotation.lookAt(adjust.mult(character.getForwardVector()), new Vector3f(0, 1, 0));

        return new CellTransform(rotation, targetPosition);
    }
}
