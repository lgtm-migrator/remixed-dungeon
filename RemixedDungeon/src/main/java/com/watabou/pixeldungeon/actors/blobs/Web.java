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
package com.watabou.pixeldungeon.actors.blobs;

import com.nyrds.pixeldungeon.items.common.armor.SpiderArmor;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.util.StringsManager;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Roots;
import com.watabou.pixeldungeon.actors.hero.Belongings;
import com.watabou.pixeldungeon.effects.BlobEmitter;
import com.watabou.pixeldungeon.effects.particles.WebParticle;

public class Web extends Blob {
	
	@Override
	protected void evolve() {
		for (int i=0; i < getLength(); i++) {
			
			int offv = cur[i] > 0 ? cur[i] - 1 : 0;
			off[i] = offv;
			
			if (offv > 0) {
				
				setVolume(getVolume() + offv);

				Char ch = Actor.findChar( i );
				boolean rootable = false;

				if (ch != null) {

					rootable = !ch.immunities().contains(getEntityKind());

					if(ch.getItemFromSlot(Belongings.Slot.ARMOR) instanceof SpiderArmor)
						rootable = false;
					}

					if (rootable){
						Buff.prolong( ch, Roots.class, TICK );
				}
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		
		emitter.pour( WebParticle.FACTORY, 0.4f );
	}
	
	public void seed( int cell, int amount ) {
		checkSeedCell(cell);
		int diff = amount - cur[cell];
		if (diff > 0) {
			cur[cell] = amount;
			setVolume(getVolume() + diff);
		}
	}
	
	@Override
	public String tileDesc() {
        return StringsManager.getVar(R.string.Web_Info);
    }
}
