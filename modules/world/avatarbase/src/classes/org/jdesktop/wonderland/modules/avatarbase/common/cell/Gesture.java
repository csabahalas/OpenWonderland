/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.common.cell;

/**
 * This class has all the data for a gesture and will be used in Gesture panel
 * 
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class Gesture {
    private String animName;
    private String labelName;
    private boolean visible;
    private GesturePosition gesturePosition;
    private GestureType gestureType;
    /**
     * separate files for sitting and standing positions?
     */
    private boolean separateAnimFiles = false;
    /**
     * has intensity levels?
     */
    private boolean intensityLevel = false;
    /**
     * does animation plays in loop?
     */
    private boolean looping = false;
    /**
     * need exit animation?
     */
    private boolean needExitAnim = true;
    public enum GestureType {
        HEAD,
        HEADTORSO,
        TORSO,
        LEG,
        OTHER
    }
    public enum GesturePosition {
        STANDING,
        SITTING,
        ANY
    }

    public Gesture(String animName, String labelName, GestureType gestureType
                        , boolean visible, GesturePosition gesturePosition
                        ,boolean separateAnimFiles, boolean intensityLevel
            , boolean looping, boolean needExitAnim) {
        this.animName = animName;
        this.labelName = labelName;
        this.gestureType = gestureType;
        this.visible = visible;
        this.gesturePosition = gesturePosition;
        this.intensityLevel = intensityLevel;
        this.separateAnimFiles = separateAnimFiles;
        this.looping = looping;
        this.needExitAnim = needExitAnim;
    } 
    
    public Gesture() {
        this.animName = "";
        this.labelName = "";
        this.gestureType = GestureType.OTHER;
        this.visible = true;
    }
    
    public String getAnimName() {
        return animName;
    }

    public void setAnimName(String animName) {
        this.animName = animName;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public GestureType getGestureType() {
        return gestureType;
    }

    public void setGestureType(GestureType gestureType) {
        this.gestureType = gestureType;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public GesturePosition getGesturePosition() {
        return gesturePosition;
    }

    public void setGesturePosition(GesturePosition gesturePosition) {
        this.gesturePosition = gesturePosition;
    }

    public boolean hasIntensityLevel() {
        return intensityLevel;
    }

    public void setIntensityLevel(boolean intensityLevel) {
        this.intensityLevel = intensityLevel;
    }

    public boolean hasSeparateAnimFiles() {
        return separateAnimFiles;
    }

    public void setSeparateAnimFiles(boolean separateAnimFiles) {
        this.separateAnimFiles = separateAnimFiles;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public boolean isNeedExitAnim() {
        return needExitAnim;
    }

    public void setNeedExitAnim(boolean needExitAnim) {
        this.needExitAnim = needExitAnim;
    }

}
