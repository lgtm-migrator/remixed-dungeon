/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.ui;

import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.pixeldungeon.DungeonTilemap;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.PointF;

public class Compass extends Image {

	private static final float RAD_2_G	= 180f / 3.1415926f;
	private static final float RADIUS	= 12;
	
	private int cell;
	private PointF cellCenter;
	private final Level level;

	private final PointF lastScroll = new PointF();
	
	public Compass(int cell, Level level) {
		
		super();
		this.level = level;

		copy( Icons.COMPASS.get() );
        setOrigin( width / 2, RADIUS );
		
		setCell(cell);
		setVisible(false);
	}

	public void setCell(int cell) {
		this.cell = cell;
		cellCenter = DungeonTilemap.tileCenterToWorld( cell );	// Exact location of the center of the tile
	}

	@Override
	public void update() {
		super.update();

		if(level.hasCompassTarget()) {
			setCell(level.getCompassTarget());
		}

		if(!level.cellValid(cell)){
			return;
		}
		
		if (!getVisible()) {
			setVisible(level.visited[cell] || level.mapped[cell]);
		}



		if (getVisible()) {			
			PointF scroll = Camera.main.scroll;
			if (!scroll.equals( lastScroll )) {
				lastScroll.set( scroll );
				PointF center = Camera.main.center().offset( scroll );
				setAngle((float)Math.atan2( cellCenter.x - center.x, center.y - cellCenter.y ) * RAD_2_G);
			}
		}
	}
}
