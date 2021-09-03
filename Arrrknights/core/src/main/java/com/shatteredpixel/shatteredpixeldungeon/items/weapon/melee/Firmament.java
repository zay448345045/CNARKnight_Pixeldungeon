/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Talu_BlackSnake;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Firmament extends MeleeWeapon {

    {
        image = ItemSpriteSheet.FIRMAMENT;
        hitSound = Assets.Sounds.HIT_DUALSTRIKE;
        hitSoundPitch = 1.1f;

        tier = 2;
    }
    private boolean doubleattack = true;


    @Override
    public int max(int lvl) {
        return  2+(tier+5) +    //9 + 2. 공식상 2회 타격
                lvl*(tier);   //scaling unchanged
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (doubleattack) {
            doubleattack = false;
            if (!attacker.attack(defender)) {
                doubleattack = true; }
            else {
                if (attacker instanceof Hero && Dungeon.hero.subClass == HeroSubClass.GLADIATOR) {
                    Buff.affect(attacker, Combo.class).hit(defender);
                }
                defender.sprite.bloodBurstA( defender.sprite.center(), 4 );
                defender.sprite.flash();
            }
        }
        else doubleattack = true;


        if (defender instanceof YogDzewa) {
            damage = Random.IntRange(3232,8876);
        }
        else if (defender instanceof Talu_BlackSnake) {
            damage *= 1.5f;
        }

        return super.proc(attacker, defender, damage);
    }

}

