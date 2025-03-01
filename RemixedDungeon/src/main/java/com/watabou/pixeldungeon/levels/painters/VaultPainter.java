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
package com.watabou.pixeldungeon.levels.painters;

import com.nyrds.pixeldungeon.items.Treasury;
import com.nyrds.pixeldungeon.levels.objects.LevelObjectsFactory;
import com.watabou.pixeldungeon.items.Heap.Type;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.keys.GoldenKey;
import com.watabou.pixeldungeon.items.keys.IronKey;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Room;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.utils.Random;

public class VaultPainter extends Painter {

	public static void paint( Level level, Room room ) {

		fill( level, room, Terrain.WALL );
		fill( level, room, 1, Terrain.EMPTY );
		
		int cx = (room.left + room.right) / 2;
		int cy = (room.top + room.bottom) / 2;
		int c = cx + cy * level.getWidth();
		
		switch (Random.Int( 3 )) {
		
		case 0:
			level.drop( prize(level), c, Type.LOCKED_CHEST);
			level.addItemToSpawn( new GoldenKey() );
			break;
			
		case 1:
			Item i1, i2;
			do {
				i1 = prize(level);
				i2 = prize(level);
			} while (i1.getClass() == i2.getClass());
			level.drop( i1, c, Type.CRYSTAL_CHEST);
			level.drop( i2, c + Level.NEIGHBOURS8[Random.Int( 8 )], Type.CRYSTAL_CHEST);
			level.addItemToSpawn( new GoldenKey() );
			break;
			
		case 2:
			level.drop( prize(level), c, Type.HEAP );
			level.putLevelObject( LevelObjectsFactory.PEDESTAL, c);
			break;
		}
		
		room.entrance().set( Room.Door.Type.LOCKED );
		level.addItemToSpawn( new IronKey() );
	}
	
	private static Item prize(Level level) {
		return Treasury.getLevelTreasury().random( Random.oneOf(
			Treasury.Category.WAND,
			Treasury.Category.RING
		) );
	}
}
