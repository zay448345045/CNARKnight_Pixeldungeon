package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NewTengu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.BloodMagister;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GreenCat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PinkPig;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.Group;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;
import java.util.Arrays;

// 다른 맵 베이스는 이거로!!
public class GroundLevel extends Level
{
    {
        color1 = 0x801500;
        color2 = 0xa68521;

        viewDistance = 12;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_PRISON;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_PRISON;
    }

    @Override
    public void create() {
        super.create();
        for (int i=0; i < length(); i++) {
            int flags = Terrain.flags[map[i]];
            if ((flags & Terrain.PIT) != 0){
                passable[i] = avoid[i] = false;
                solid[i] = true;
            }
        }
        for (int i = (height-ROOM_TOP+2)*width; i < length; i++){
            passable[i] = avoid[i] = false;
            solid[i] = true;
        }
        for (int i = (height-ROOM_TOP+1)*width; i < length; i++){
            if (i % width < 4 || i % width > 12 || i >= (length-width)){
                discoverable[i] = false;
            } else {
                visited[i] = true;
            }
        }
    }

    private static final int ROOM_TOP = 6;
    public int PingPig = 392;

    @Override
    protected boolean build() {

        setSize(24, 24);
        Arrays.fill( map, Terrain.WALL );

        final int MID = width/2;

        Painter.fill( this, MID - 6, 8, 12, 10, Terrain.EMPTY);
        Painter.fill( this, MID - 4, 8, 1, 10, Terrain.WALL);
        Painter.fill( this, MID - 4, 16, 1, 1, Terrain.DOOR);

        entrance = (height-ROOM_TOP) * width() + MID - 6;
        exit = 8*(width()) + MID - 6; // 세로, 가로, 중앙기준 -6

        map[entrance] = Terrain.ENTRANCE;

        feeling = Feeling.NONE;

        return true;
    }

    @Override
    protected void createMobs() {
    }

    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
        GreenCat.spawn(this);
        PinkPig.spawn(this,PingPig);

        //장난용
        /*
        PinkPig.spawn(this,372);
        PinkPig.spawn(this,395);
        PinkPig.spawn(this,232);
        PinkPig.spawn(this,228);
        PinkPig.spawn(this,304);
        drop(new Gold(1), 418 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 369 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 329 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 348 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 322 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 300 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 325 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 302 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 351 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 399 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 423 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 421 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 274 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 256 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 230 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 225 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 203 ).type = Heap.Type.SKELETON; // 시체
        drop(new Gold(1), 208 ).type = Heap.Type.SKELETON; // 시체*/
    }

    @Override
    public int randomRespawnCell( Char ch ) {
        int cell;
        do {
            cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
        } while (!passable[cell]
                || (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
                || Actor.findChar(cell) != null);
        return cell;
    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(HallsLevel.class, "water_name");
            case Terrain.GRASS:
                return Messages.get(HallsLevel.class, "grass_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(HallsLevel.class, "high_grass_name");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(HallsLevel.class, "statue_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(HallsLevel.class, "water_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(HallsLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(HallsLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc( tile );
        }
    }

    @Override
    public Group addVisuals () {
        super.addVisuals();
        HallsLevel.addHallsVisuals(this, visuals);
        return visuals;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        for (int i=0; i < length(); i++) {
            int flags = Terrain.flags[map[i]];
            if ((flags & Terrain.PIT) != 0){
                passable[i] = avoid[i] = false;
                solid[i] = true;
            }
        }
        for (int i = (height-ROOM_TOP+2)*width; i < length; i++){
            passable[i] = avoid[i] = false;
            solid[i] = true;
        }
        for (int i = (height-ROOM_TOP+1)*width; i < length; i++){
            if (i % width < 4 || i % width > 12 || i >= (length-width)){
                discoverable[i] = false;
            } else {
                visited[i] = true;
            }
        }
    }
}
