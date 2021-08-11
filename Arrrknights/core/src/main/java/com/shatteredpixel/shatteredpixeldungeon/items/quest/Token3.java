package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

import java.util.ArrayList;

public class Token3 extends Item {
    public static String AC_ADD = "ADD";
    {
        image = ItemSpriteSheet.MOULD;

        stackable = true;
        unique = true;
        defaultAction=AC_ADD;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ADD);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);
        if (action.equals(AC_ADD)) {
            GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, "...");
        }
    }

    private final WndBag.Listener itemSelector = new WndBag.Listener()  {
        @Override
        public void onSelect(final Item item) {
            if (item != null) {
                if (item instanceof Shortsword) {
                    GLog.h(Messages.get(Token3.class, "suc"));
                    if (Dungeon.hero.belongings.weapon == item) {
                        Dungeon.hero.belongings.weapon = null;
                    }
                    else item.detach(Dungeon.hero.belongings.backpack);

                    Dungeon.talucount++;
                    detach(curUser.belongings.backpack);
                }
            }
        }};

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}
