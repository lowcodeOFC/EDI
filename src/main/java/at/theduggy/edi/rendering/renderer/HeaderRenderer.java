/*
 * EDI: Extended Debug Info
 * Copyright (C) 2022  Georg Kollegger(TheDuggy/CoderTheDuggy)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.theduggy.edi.rendering.renderer;

import at.theduggy.edi.EDIManager;
import at.theduggy.edi.Main;
import at.theduggy.edi.settings.OptionManager;
import at.theduggy.edi.settings.options.Option;
import org.bukkit.ChatColor;

public class HeaderRenderer{
    private final EDIManager ediManager;
    private boolean headerRendered = false;

    public HeaderRenderer(EDIManager ediManager) {
        this.ediManager = ediManager;
    }

    public void render(){
        OptionManager optionManager = ediManager.getOptionManager();
        if (optionManager.isHeaderEnabled()){
            headerRendered = true;
            StringBuilder header = new StringBuilder();
            header.append(Main.logo + ChatColor.RESET);

            for (int i = ediManager.getOptionManager().getDisplayIndexList().size()-1;i>=0;i--){
                Option o = ediManager.getOptionManager().getDisplayIndexList().get(i);
                if (o.isHeader()){
                    header.append((o.isShowKeys()?"\n" + o.getKeyFontData().format(o.getDisplayName()  )+ o.getSeparatorFontData().format(": ") : "\n") + o.getValueFontData().format(o.getValue(ediManager.getPlayer())));
                }
            }
            ediManager.getPlayer().setPlayerListHeader(header.toString());
        }else {
            if (headerRendered){
                headerRendered = false;
                ediManager.getPlayer().setPlayerListHeader(null);
            }
        }
    }

    public void reset(){
        System.out.println("awd");
        ediManager.getPlayer().setPlayerListFooter(null);
    }

}
