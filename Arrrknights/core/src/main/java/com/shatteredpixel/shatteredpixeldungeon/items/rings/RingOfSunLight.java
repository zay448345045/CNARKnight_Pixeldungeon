package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class RingOfSunLight extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_SUNLIGHT;
    }

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (Math.pow(1.14f, soloBuffedBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(14));//change from budding
        }
    }

    @Override
    protected RingBuff buff() {
        return new Chargeup();
    }

    public static float SPBonus( Char target ){
        return (float)(Math.pow(1.14f, getBonus(target, RingOfSunLight.Chargeup.class)));
    }


    public class Chargeup extends RingBuff {
    }
}



