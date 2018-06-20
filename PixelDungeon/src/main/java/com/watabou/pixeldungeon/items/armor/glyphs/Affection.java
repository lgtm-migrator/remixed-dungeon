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
package com.watabou.pixeldungeon.items.armor.glyphs;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Charm;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.armor.Armor.Glyph;
import com.watabou.pixeldungeon.items.quest.DriedRose;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class Affection extends Glyph {

	private static ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xFF4488);

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		int level = (int) GameMath.gate(0, armor.level(), 6);

		if (Dungeon.level.adjacent(attacker.getPos(), defender.getPos()) && Random.Int(level / 2 + 5) >= 4) {

			int duration = Random.IntRange(2, 5);

			float attackerFactor = 1;
			float defenderFactor = 1;

			if(defender.hasBuff(DriedRose.OneWayLoveBuff.class)) {
				attackerFactor *= 2;
				defenderFactor *= 0;
			}

			if(defender.hasBuff(DriedRose.OneWayCursedLoveBuff.class)) {
				attackerFactor *= 0;
				defenderFactor *= 2;
			}

			Buff.affect(attacker, Charm.class, Charm.durationFactor(attacker) * duration * attackerFactor);
			attacker.getSprite().centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);

			Buff.affect(defender, Charm.class, Random.Float(Charm.durationFactor(defender) * duration / 2, duration) * defenderFactor);
			defender.getSprite().centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
		}

		return damage;
	}

	@Override
	public String name(String weaponName) {
		return Utils.format(Game.getVar(R.string.Affection_Txt), weaponName);
	}

	@Override
	public Glowing glowing() {
		return PINK;
	}
}
