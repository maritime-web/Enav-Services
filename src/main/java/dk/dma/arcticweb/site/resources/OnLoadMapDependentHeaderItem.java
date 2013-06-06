/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.arcticweb.site.resources;

import java.util.Arrays;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;

import dk.dma.arcticweb.site.pages.main.panel.MapPanel;

public class OnLoadMapDependentHeaderItem extends OnLoadHeaderItem {

    public OnLoadMapDependentHeaderItem(CharSequence javaScript) {
        super(javaScript);
    }

    @Override
    public Iterable<? extends HeaderItem> getDependencies() {
        return Arrays.asList(MapPanel.MAP_INIT);
    }

    public static OnLoadMapDependentHeaderItem forScript(CharSequence javaScript) {
        return new OnLoadMapDependentHeaderItem(javaScript);
    }

}