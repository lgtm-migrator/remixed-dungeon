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
package com.watabou.pixeldungeon.windows;

import com.nyrds.pixeldungeon.items.necropolis.BlackSkull;
import com.nyrds.pixeldungeon.mechanics.NamedEntityKind;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.windows.WndHelper;
import com.nyrds.platform.util.StringsManager;
import com.nyrds.util.GuiProperties;
import com.watabou.noosa.Text;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.Statistics;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Ankh;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.Utils;

public class WndResurrect extends Window {

	private static final int BTN_HEIGHT	= 18;

	public static WndResurrect instance;
	public static NamedEntityKind causeOfDeath;

	public WndResurrect( final Ankh ankh, NamedEntityKind causeOfDeath ) {
		super();

		width = WndHelper.getFullscreenWidth();

		instance = this;
		WndResurrect.causeOfDeath = causeOfDeath;
		
		IconTitle titlebar = new IconTitle();
		if (ankh != null){
			titlebar.icon( new ItemSprite( ankh ) );
			titlebar.label( ankh.name() );
		} else {
			titlebar.icon( new ItemSprite( new BlackSkull()) );
            titlebar.label( Utils.capitalize(StringsManager.getVar(R.string.Necromancy_Title)) );
		}

		titlebar.setRect( 0, 0, width, 0 );
		add( titlebar );

        Text message = PixelScene.createMultiline(R.string.WndResurrect_Message, GuiProperties.regularFontSize() );
		message.maxWidth(width);
		message.setY(titlebar.bottom() + GAP);
		add( message );

        RedButton btnYes = new RedButton(R.string.WndResurrect_Yes) {
			@Override
			protected void onClick() {
				hide();
				if(ankh!=null && ankh.valid()) {
					Statistics.ankhsUsed++;
				}
				InterlevelScene.Do(InterlevelScene.Mode.RESURRECT);
			}
		};
		btnYes.setRect( 0, message.getY() + message.height() + GAP, width, BTN_HEIGHT );
		add( btnYes );

        RedButton btnNo = new RedButton(R.string.WndResurrect_No) {
			@Override
			protected void onClick() {
				hide();
				Dungeon.deleteGame(false);
				Hero.reallyDie(Dungeon.hero, WndResurrect.causeOfDeath );
			}
		};
		btnNo.setRect( 0, btnYes.bottom() + GAP, width, BTN_HEIGHT );
		add( btnNo );
		
		resize( width, (int)btnNo.bottom() );
	}
	
	@Override
	public void destroy() {
		super.destroy();
		instance = null;
	}
	
	@Override
	public void onBackPressed() {
	}
}
