/*
 * This file ("InteractionEvent.java") is part of the RockBottomAPI by Ellpeck.
 * View the source code at <https://github.com/Ellpeck/RockBottomAPI>.
 *
 * The RockBottomAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The RockBottomAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the RockBottomAPI. If not, see <http://www.gnu.org/licenses/>.
 */

package de.ellpeck.rockbottom.api.event.impl;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.Event;
import de.ellpeck.rockbottom.api.world.TileLayer;

import java.util.List;

/**
 * This event is fired when an {@link AbstractEntityPlayer} interacts with the world
 * <br> Cancelling it will cancel the interaction
 */
public class InteractionEvent extends Event{

    public final AbstractEntityPlayer player;
    public List<Entity> entities;
    public TileLayer layer;
    public int x;
    public int y;
    public double mouseX;
    public double mouseY;

    public InteractionEvent(AbstractEntityPlayer player, List<Entity> entities, TileLayer layer, int x, int y, double mouseX, double mouseY){
        this.player = player;
        this.entities = entities;
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}