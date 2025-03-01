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
package com.watabou.pixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class PurpleParticle extends PixelParticle {

	public static final Emitter.Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((PurpleParticle)emitter.recycle( PurpleParticle.class )).reset( x, y );
		}
	};


	public static final Emitter.Factory MISSILE = new Factory() {	
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((PurpleParticle)emitter.recycle( PurpleParticle.class )).reset( x, y );
		}
	};
	
	public static final Emitter.Factory BURST = new Factory() {	
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((PurpleParticle)emitter.recycle( PurpleParticle.class )).resetBurst( x, y );
		}
		@Override
		public boolean lightMode() {
			return true;
		}
	};
	
	public PurpleParticle() {
		lifespan = 0.5f;
	}
	
	public void reset( float x, float y ) {
		revive();
		
		setX(x);
		setY(y);
		
		speed.set( Random.Float( -5, +5 ), Random.Float( -5, +5 ) );
		
		left = lifespan;
	}
	
	public void resetBurst( float x, float y ) {
		revive();
		
		setX(x);
		setY(y);
		
		speed.polar( Random.Float( 360 ), Random.Float( 16, 32 ) );
		
		left = lifespan;
	}
	
	@Override
	public void update() {
		super.update();
		// alpha: 1 -> 0; size: 1 -> 5
		size( 5 - (am = left / lifespan) * 4 );
		// color: 0xFF0044 -> 0x220066
		color( ColorMath.interpolate( 0x220066, 0xFF0044, am ) );
	}
}