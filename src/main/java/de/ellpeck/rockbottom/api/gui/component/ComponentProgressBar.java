/*
 * This file ("ComponentProgressBar.java") is part of the RockBottomAPI by Ellpeck.
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

package de.ellpeck.rockbottom.api.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.function.Supplier;

public class ComponentProgressBar extends GuiComponent{

    private final int progressColor;
    private final int backgroundColor;

    private final Supplier<Float> supplier;
    private final boolean isVertical;

    public ComponentProgressBar(Gui gui, int x, int y, int sizeX, int sizeY, int progressColor, boolean isVertical, Supplier<Float> supplier){
        super(gui, x, y, sizeX, sizeY);
        this.supplier = supplier;
        this.isVertical = isVertical;
        this.progressColor = progressColor;
        this.backgroundColor = Colors.multiply(progressColor, 0.5F, 0.5F, 0.5F, 0.35F);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, int x, int y){
        super.render(game, manager, g, x, y);

        float percent = this.supplier.get();

        g.fillRect(x, y, this.width, this.height, this.backgroundColor);

        if(this.isVertical){
            float height = percent*this.height;
            g.fillRect(x, y+this.height-height, this.width, height, this.progressColor);
        }
        else{
            float width = percent*this.width;
            g.fillRect(x, y, width, this.height, this.progressColor);
        }

        g.drawRect(x, y, this.width, this.height, Colors.BLACK);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("progress_bar");
    }
}
