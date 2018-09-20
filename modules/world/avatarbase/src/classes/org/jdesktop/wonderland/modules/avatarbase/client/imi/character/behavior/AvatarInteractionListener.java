/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior;

import org.jdesktop.wonderland.client.cell.Cell;

/**
 * for multiple avatar interaction notification
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public interface AvatarInteractionListener {

    /**
     *
     * @param sourceCell cell which initiate interaction
     * @param targetCell cell to which the interaction happen
     * @param interaction name of interaction
     * @param started interaction started?
     */
    public void avatarsInteract(Cell sourceCell, Cell targetCell, String interaction, boolean started);

}
