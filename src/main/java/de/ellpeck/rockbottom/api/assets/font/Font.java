/*
 * This file ("Font.java") is part of the RockBottomAPI by Ellpeck.
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

package de.ellpeck.rockbottom.api.assets.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.tex.Texture;
import de.ellpeck.rockbottom.api.util.Pos2;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Font{

    private final String name;
    private final Texture image;

    private final Map<Character, Pos2> characters;

    private final int charWidth;
    private final int charHeight;

    public Font(String name, Texture image, int widthInChars, int heightInChars, Map<Character, Pos2> characters){
        this.name = name;
        this.image = image;
        this.characters = characters;

        this.charWidth = image.getWidth()/widthInChars;
        this.charHeight = image.getHeight()/heightInChars;
    }

    public static Font fromStream(InputStream imageStream, InputStream infoStream, String name) throws Exception{
        Texture image = new Texture(imageStream, name, false);
        image.setFilter(Texture.FILTER_NEAREST);

        JsonParser parser = new JsonParser();
        JsonObject main = parser.parse(new InputStreamReader(infoStream)).getAsJsonObject();

        int width = 0;
        Map<Character, Pos2> characters = new HashMap<>();

        JsonArray rows = main.getAsJsonArray("data");
        for(int y = 0; y < rows.size(); y++){
            JsonArray row = rows.get(y).getAsJsonArray();

            int length = row.size();
            if(length > width){
                width = length;
            }

            for(int x = 0; x < length; x++){
                char c = row.get(x).getAsCharacter();
                characters.put(c, new Pos2(x, y));
            }
        }

        int height = rows.size();
        Log.debug("Loaded font "+name+" with dimensions "+width+"x"+height+" and the following character map: "+characters);

        return new Font(name, image, width, height, characters);
    }

    public void drawStringFromRight(float x, float y, String s, float scale){
        this.drawString(x-this.getWidth(s, scale), y, s, scale);
    }

    public void drawCenteredString(float x, float y, String s, float scale, boolean centeredOnY){
        this.drawString(x-this.getWidth(s, scale)/2F, centeredOnY ? (y-this.getHeight(scale)/2F) : y, s, scale);
    }

    public void drawFadingString(float x, float y, String s, float scale, float fadeTotal, float fadeInEnd, float fadeOutStart){
        Color color = new Color(Color.white);

        if(fadeTotal <= fadeInEnd){
            color.a *= fadeTotal/fadeInEnd;
        }
        else if(fadeTotal >= fadeOutStart){
            color.a *= 1F-(fadeTotal-fadeOutStart)/(1F-fadeOutStart);
        }

        this.drawString(x, y, s, scale, color);
    }

    public void drawString(float x, float y, String s, float scale){
        this.drawString(x, y, s, scale, Color.white);
    }

    public void drawString(float x, float y, String s, float scale, Color color){
        this.drawString(x, y, s, 0, s.length(), scale, color);
    }

    public void drawCutOffString(float x, float y, String s, float scale, int length, boolean fromRight, boolean basedOnCharAmount){
        int strgLength = s.length();

        if((basedOnCharAmount ? strgLength : this.getWidth(s, scale)) <= length){
            this.drawString(x, y, s, scale);
        }

        int amount = 0;
        String accumulated = "";

        for(int i = 0; i < strgLength; i++){
            if(fromRight){
                accumulated = s.charAt(strgLength-1-i)+accumulated;
            }
            else{
                accumulated += s.charAt(i);
            }

            amount++;

            if((basedOnCharAmount ? this.removeFormatting(accumulated).length() : this.getWidth(accumulated, scale)) >= length){
                break;
            }
        }

        if(fromRight){
            this.drawString(x, y, s, strgLength-amount, strgLength, scale, Color.white);
        }
        else{
            this.drawString(x, y, s, 0, amount, scale, Color.white);
        }
    }

    public void drawSplitString(float x, float y, String s, float scale, int length){
        List<String> split = this.splitTextToLength(length, scale, true, s);

        for(String string : split){
            this.drawString(x, y, string, scale);
            y += this.getHeight(scale);
        }
    }

    private void drawString(float x, float y, String s, int drawStart, int drawEnd, float scale, Color color){
        float initialAlpha = color.a;
        float xOffset = 0F;

        char[] characters = s.toCharArray();
        for(int i = 0; i < Math.min(drawEnd, characters.length); i++){
            FormattingCode code = FormattingCode.getFormat(s, i);
            if(code != FormattingCode.NONE){
                Color formatColor = code.getColor();
                if(formatColor != null){
                    if(initialAlpha != formatColor.a){
                        color = new Color(formatColor.r, formatColor.g, formatColor.b, initialAlpha);
                    }
                    else{
                        color = formatColor;
                    }
                }

                i += code.getLength()-1;
                continue;
            }

            if(i >= drawStart){
                this.drawCharacter(x+xOffset, y, characters[i], scale, color);
                xOffset += (float)this.charWidth*scale;
            }
        }
    }

    public void drawCharacter(float x, float y, char character, float scale, Color color){
        if(character != ' '){
            Pos2 pos = this.characters.get(character);

            if(pos == null){
                pos = new Pos2(-1, -1);
                this.characters.put(character, pos);

                Log.warn("Character "+character+" is missing from font with name "+this.name+"!");
            }

            if(pos.getX() >= 0 && pos.getY() >= 0){
                int srcX = pos.getX()*this.charWidth;
                int srcY = pos.getY()*this.charHeight;

                float x2 = x+(float)this.charWidth*scale;
                float y2 = y+(float)this.charHeight*scale;

                float shadowOffset = 2F*scale;
                Color shadowColor = color.a != 1F ? new Color(0F, 0F, 0F, color.a) : Color.black;
                this.image.draw(x+shadowOffset, y+shadowOffset, x2+shadowOffset, y2+shadowOffset, srcX, srcY, srcX+this.charWidth, srcY+this.charHeight, shadowColor);

                this.image.draw(x, y, x2, y2, srcX, srcY, srcX+this.charWidth, srcY+this.charHeight, color);
            }
            else{
                RockBottomAPI.getGame().getAssetManager().getMissingTexture().draw(x, y, this.charWidth*scale, this.charHeight*scale);
            }
        }
    }

    public String removeFormatting(String s){
        String newString = "";
        for(int i = 0; i < s.length(); i++){
            FormattingCode code = FormattingCode.getFormat(s, i);
            if(code != FormattingCode.NONE){
                i += code.getLength()-1;
            }
            else{
                newString += s.charAt(i);
            }
        }
        return newString;
    }

    public float getWidth(String s, float scale){
        return (float)this.charWidth*(float)this.removeFormatting(s).length()*scale;
    }

    public float getHeight(float scale){
        return (float)this.charHeight*scale;
    }

    public List<String> splitTextToLength(int length, float scale, boolean wrapFormatting, String... lines){
        return this.splitTextToLength(length, scale, wrapFormatting, Arrays.asList(lines));
    }

    public List<String> splitTextToLength(int length, float scale, boolean wrapFormatting, List<String> lines){
        List<String> result = new ArrayList<>();
        String accumulated = "";

        for(String line : lines){
            FormattingCode trailingCode = FormattingCode.NONE;

            for(String subLine : line.split("[\\\\&]n")){
                String[] words = subLine.split(" ");

                for(String word : words){
                    if(wrapFormatting){
                        for(int i = 0; i < word.length()-1; i++){
                            FormattingCode format = FormattingCode.getFormat(word, i);
                            if(format != FormattingCode.NONE){
                                trailingCode = format;
                            }
                        }
                    }

                    if(this.getWidth(accumulated+word, scale) >= length){
                        result.add(accumulated.trim());
                        accumulated = trailingCode+word+" ";
                    }
                    else{
                        accumulated += word+" ";
                    }
                }

                result.add(accumulated.trim());
                accumulated = trailingCode.toString();
            }

            accumulated = "";
        }

        return result;
    }
}
