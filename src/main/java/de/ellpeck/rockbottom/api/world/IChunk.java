/*
 * This file ("IChunk.java") is part of the RockBottomAPI by Ellpeck.
 * View the source code at <https://github.com/Ellpeck/RockBottomAPI>.
 *
 * The RockBottomAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The RockBottomAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the RockBottomAPI. If not, see <http://www.gnu.org/licenses/>.
 */

package de.ellpeck.rockbottom.api.world;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.MutableInt;

import java.util.List;
import java.util.Map;

public interface IChunk extends IChunkOrWorld{

    List<AbstractEntityPlayer> getPlayersInRange();

    List<AbstractEntityPlayer> getPlayersLeftRange();

    Map<AbstractEntityPlayer, MutableInt> getLeftPlayerTimers();

    int getGridX();

    int getGridY();

    IWorld getWorld();

    int getX();

    int getY();

    Tile getTileInner(TileLayer layer, int x, int y);

    Tile getTileInner(int x, int y);

    byte getMetaInner(TileLayer layer, int x, int y);

    void setTileInner(int x, int y, Tile tile, int meta);

    void setTileInner(int x, int y, Tile tile);

    void setTileInner(TileLayer layer, int x, int y, Tile tile);

    void setTileInner(TileLayer layer, int x, int y, Tile tile, int meta);

    void setMetaInner(TileLayer layer, int x, int y, int meta);

    byte getSkylightInner(int x, int y);

    void setSkylightInner(int x, int y, byte light);

    byte getArtificialLightInner(int x, int y);

    void setArtificialLightInner(int x, int y, byte light);

    void setGenerating(boolean generating);

    boolean needsSave();

    boolean shouldUnload();

    void save(DataSet set);

    void update(IGameInstance game);

    byte getCombinedLightInner(int x, int y);

    int getScheduledUpdateAmount();
}
