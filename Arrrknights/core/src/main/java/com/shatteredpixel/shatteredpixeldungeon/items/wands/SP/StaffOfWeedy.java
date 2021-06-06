package com.shatteredpixel.shatteredpixeldungeon.items.wands.SP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class StaffOfWeedy extends DamageWand {
    {
        image = ItemSpriteSheet.WAND_BLAST_WAVE;

        collisionProperties = Ballistica.PROJECTILE;
    }

    public int min(int lvl){ return 1+lvl;
    }

    public int max(int lvl){
        return 1+2*lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Sample.INSTANCE.play( Assets.Sounds.BLAST );
        StaffOfWeedy.BlastWave.blast(bolt.collisionPos);

        //presses all tiles in the AOE first, with the exception of tengu dart traps
        for (int i : PathFinder.NEIGHBOURS9){
            if (!(Dungeon.level.traps.get(bolt.collisionPos+i) instanceof TenguDartTrap)) {
                Dungeon.level.pressCell(bolt.collisionPos + i);
            }
        }

        //throws other chars around the center.
        for (int i  : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(bolt.collisionPos + i);

            if (ch != null){
                processSoulMark(ch, chargesPerCast());
                if (ch.alignment != Char.Alignment.ALLY) {
                    if (ch == curUser) {
                        int dmg = (int) Math.round(damageRoll() * Math.pow(1.077f, curCharges));
                        ch.damage(dmg, this);
                    }
                    else ch.damage(damageRoll(), this);

                }

                if (ch.isAlive() && ch.pos == bolt.collisionPos + i) {
                    Ballistica trajectory = new Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT);
                    int strength = 1 + Math.round(buffedLvl() / 2f) + Math.round(curCharges / 4);
                    throwChar(ch, trajectory, strength, false);
                } else if (ch == Dungeon.hero){
                    Dungeon.fail( getClass() );
                    GLog.n( Messages.get( this, "ondeath") );
                }
            }
        }

        //throws the char at the center of the blast
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null){
            processSoulMark(ch, chargesPerCast());
            int dmg = (int)Math.round(damageRoll() * Math.pow(1.3f, curCharges));
            ch.damage(dmg, this);

            if (ch.isAlive() && bolt.path.size() > bolt.dist+1 && ch.pos == bolt.collisionPos) {
                Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1), Ballistica.MAGIC_BOLT);
                int strength = buffedLvl() + 3 + (curCharges * 2);
                throwChar(ch, trajectory, strength, false);
            }
        }

        Camera.main.shake(curCharges * 0.65f, 0.25f);

       curCharges = 1;

    }

    public static void throwChar(final Char ch, final Ballistica trajectory, int power){
        throwChar(ch, trajectory, power, true);
    }

    public static void throwChar(final Char ch, final Ballistica trajectory, int power,
                                 boolean closeDoors) {
        throwChar(ch, trajectory, power, closeDoors, true);
    }

    public static void throwChar(final Char ch, final Ballistica trajectory, int power,
                                 boolean closeDoors, boolean collideDmg){
        if (ch.properties().contains(Char.Property.BOSS)) {
            power /= 2;
        }

        int dist = Math.min(trajectory.dist, power);

        boolean collided = dist == trajectory.dist;

        if (dist == 0 || ch.properties().contains(Char.Property.IMMOVABLE)) return;

        //large characters cannot be moved into non-open space
        if (Char.hasProp(ch, Char.Property.LARGE)) {
            for (int i = 1; i <= dist; i++) {
                if (!Dungeon.level.openSpace[trajectory.path.get(i)]){
                    dist = i-1;
                    collided = true;
                    break;
                }
            }
        }

        if (Actor.findChar(trajectory.path.get(dist)) != null){
            dist--;
            collided = true;
        }

        if (dist < 0) return;

        final int newPos = trajectory.path.get(dist);

        if (newPos == ch.pos) return;

        final int finalDist = dist;
        final boolean finalCollided = collided && collideDmg;
        final int initialpos = ch.pos;

        Actor.addDelayed(new Pushing(ch, ch.pos, newPos, new Callback() {
            public void call() {
                if (initialpos != ch.pos) {
                    //something caused movement before pushing resolved, cancel to be safe.
                    ch.sprite.place(ch.pos);
                    return;
                }
                int oldPos = ch.pos;
                ch.pos = newPos;
                if (finalCollided && ch.isAlive()) {
                    ch.damage(Random.NormalIntRange(finalDist, 2*finalDist), this);
                    Paralysis.prolong(ch, Paralysis.class, 1 + finalDist/2f);
                }
                if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
                    Door.leave(oldPos);
                }
                Dungeon.level.occupyCell(ch);
                if (ch == Dungeon.hero){
                    //FIXME currently no logic here if the throw effect kills the hero
                    Dungeon.observe();
                }
            }
        }), -1);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        new Elastic().proc(staff, attacker, defender, damage);
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.FORCE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color( 0x664422 ); particle.am = 0.6f;
        particle.setLifespan(3f);
        particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
        particle.setSize( 1f, 2f);
        particle.radiateXY(2.5f);
    }

    public static class BlastWave extends Image {

        private static final float TIME_TO_FADE = 0.2f;

        private float time;

        public BlastWave(){
            super(Effects.get(Effects.Type.RIPPLE));
            origin.set(width / 2, height / 2);
        }

        public void reset(int pos) {
            revive();

            x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
            y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

            time = TIME_TO_FADE;
        }

        @Override
        public void update() {
            super.update();

            if ((time -= Game.elapsed) <= 0) {
                kill();
            } else {
                float p = time / TIME_TO_FADE;
                alpha(p);
                scale.y = scale.x = (1-p)*3;
            }
        }

        public static void blast(int pos) {
            Group parent = Dungeon.hero.sprite.parent;
            StaffOfWeedy.BlastWave b = (StaffOfWeedy.BlastWave) parent.recycle(StaffOfWeedy.BlastWave.class);
            parent.bringToFront(b);
            b.reset(pos);
        }

    }
}