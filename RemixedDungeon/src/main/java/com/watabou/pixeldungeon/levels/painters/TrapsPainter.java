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

import com.nyrds.pixeldungeon.items.DummyItem;
import com.nyrds.pixeldungeon.items.Treasury;
import com.nyrds.pixeldungeon.levels.objects.LevelObjectsFactory;
import com.watabou.pixeldungeon.items.Heap;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.potions.PotionOfLevitation;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Room;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.utils.Random;

public class TrapsPainter extends Painter {

	public static void paint( Level level, Room room ) {
		 
		Integer[] traps = {
			Terrain.TOXIC_TRAP, Terrain.TOXIC_TRAP, Terrain.TOXIC_TRAP, 
			Terrain.PARALYTIC_TRAP, Terrain.PARALYTIC_TRAP, Terrain.CHASM, Terrain.SUMMONING_TRAP };
		fill( level, room, Terrain.WALL );
		fill( level, room, 1, Random.element( traps ) );
		
		Room.Door door = room.entrance(); 
		door.set( Room.Door.Type.REGULAR );
		
		int lastRow = level.map[room.left + 1 + (room.top + 1) * level.getWidth()] == Terrain.CHASM ? Terrain.CHASM : Terrain.EMPTY;

		int x = -1;
		int y = -1;
		if (door.x == room.left) {
			x = room.right - 1;
			y = room.top + room.height() / 2;
			fill( level, x, room.top + 1, 1, room.height() - 1 , lastRow );
		} else if (door.x == room.right) {
			x = room.left + 1;
			y = room.top + room.height() / 2;
			fill( level, x, room.top + 1, 1, room.height() - 1 , lastRow );
		} else if (door.y == room.top) {
			x = room.left + room.width() / 2;
			y = room.bottom - 1;
			fill( level, room.left + 1, y, room.width() - 1, 1 , lastRow );
		} else if (door.y == room.bottom) {
			x = room.left + room.width() / 2;
			y = room.top + 1;
			fill( level, room.left + 1, y, room.width() - 1, 1 , lastRow );
		}
		
		int pos = x + y * level.getWidth();
		if (Random.Int( 3 ) == 0) {
			if (lastRow == Terrain.CHASM) {
				set( level, pos, Terrain.EMPTY );
			}
			level.drop( prize( level ), pos, Heap.Type.CHEST);
		} else {
			set( level, pos, Terrain.EMPTY );
			level.putLevelObject( LevelObjectsFactory.PEDESTAL, pos);
			level.drop( prize( level ), pos, Heap.Type.HEAP );
		}
		
		level.addItemToSpawn( new PotionOfLevitation() );
	}
	
	private static Item prize( Level level ) {
		
		Item prize = level.itemToSpanAsPrize();
		if (! (prize instanceof DummyItem)) {
			return prize;
		}
		
		prize = Treasury.weaponOrArmorPrize(3);
		
		return prize;
	}
}
