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
 * Touch arm task
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class TouchArm extends TouchTo {

    public TouchArm(Cell targetCell, GestureConnection gestureConn, GameContext context) {
        super(targetCell, gestureConn, context);
    }

    @Override
    public void playTouchAnimation(final imi.character.Character character, boolean reverse) {
        if (character.getCharacterParams().isAnimateBody()) {
            character.getSkeleton().getAnimationState()
                    .setTransitionCycleMode(AnimationComponent.PlaybackMode.PlayOnce);

            String animName = "";
            if (character.getCharacterParams().isMale()) {
                animName = animName + "Male_TouchArm";
            } else {
                animName = animName + "Female_TouchArm";
            }
            if (avatarPosition.equals("Standing")) {
                animName = animName + "Standing";
            } else if (avatarPosition.equals("Sitting")) {
                WlAvatarCharacter achar = ((AvatarImiJME) targetCell
                        .getCellRenderer(Cell.RendererType.RENDERER_JME))
                        .getAvatarCharacter();
                animName = animName + "Sitting";
                if (achar.getCharacterParams().isMale()) {
                    animName = animName + "ToMale";
                } else {
                    animName = animName + "ToFemale";
                }
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
            msg.setMessageType("TouchArmReverse");
        } else {
            msg.setMessageType("TouchArm");
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
                distance = 0.70f;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.68f;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.50f;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.57f;//DONE
            }
        } else if (avatarPosition.equals("Sitting")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.35f;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.5f;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.35f;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.48f;//DONE
            }
        } else if (avatarPosition.equals("Sleeping")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.7f;
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.7f;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                distance = 0.7f;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                distance = 0.7f;
            }
        }
        return distance;
    }

    @Override
    public float getLookRotationFactor() {
        float angle = 0;
        if (avatarPosition.equals("Standing")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 77;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 49;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 45;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 38;//DONE
            }
        } else if (avatarPosition.equals("Sitting")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 45;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 55;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 56;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 63;//DONE
            }
        } else if (avatarPosition.equals("Sleeping")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 45;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 45;
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 40;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 45;
            }
        }
        return angle;
    }

    @Override
    public float getVectorRotationFactor() {
        float angle = 0;
        if (avatarPosition.equals("Standing")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 30;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 14;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 20;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 27;//DONE
            }
        } else if (avatarPosition.equals("Sitting")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 70;//DONE
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 100;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 80;//DONE
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 105;//DONE
            }
        } else if (avatarPosition.equals("Sleeping")) {
            if (thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 45;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && !targetCharacter.getCharacterParams().isMale()) {
                angle = 45;
            } else if (thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 40;
            } else if (!thisCharacter.getCharacterParams().isMale()
                    && targetCharacter.getCharacterParams().isMale()) {
                angle = 45;
            }
        }
        return angle;
    }

    @Override
    protected CellTransform getTargetTransform() {
        Vector3f viewPosition = targetCell.getWorldTransform().getTranslation(null);
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
