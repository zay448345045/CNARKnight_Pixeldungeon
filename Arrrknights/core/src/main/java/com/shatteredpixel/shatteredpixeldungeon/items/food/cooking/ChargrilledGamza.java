package com.shatteredpixel.shatteredpixeldungeon.items.food.cooking;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ingredients.Potato;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ingredients.SugarFlower;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ChargrilledGamza extends Food {
    {
        image = ItemSpriteSheet.GAMZA_SHIELD;
        energy = Hunger.HUNGRY / 2;

        stackable = true;
    }

    @Override
    public int value() {
        return 20 * quantity;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{SugarFlower.class, Potato.class};
            inQuantity = new int[]{1, 1};
            cost = 1;

            output = ChargrilledGamza.class;
            outQuantity = 1;
        }

    }
}