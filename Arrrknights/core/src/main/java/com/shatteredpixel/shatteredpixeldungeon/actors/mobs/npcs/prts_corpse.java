package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Rose;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DobermannSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreenCatSprite;

public class prts_corpse extends NPC{

    {
        spriteClass = GreenCatSprite.class;
        properties.add(Char.Property.IMMOVABLE);
        properties.add(Property.NPC);
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean interact(Char c) {
        sprite.turnTo(pos, c.pos);

        if(Dungeon.hero.belongings.getItem(Rose.class) != null) {
            Item item = Dungeon.hero.belongings.getItem(Rose.class);
            if (item != null) item.detachAll(Dungeon.hero.belongings.backpack);

            destroy();

            sprite.killAndErase();
            CellEmitter.get( pos ).burst( Speck.factory( Speck.DISCOVER ), 8 );
        }

        return true;
    }
}
