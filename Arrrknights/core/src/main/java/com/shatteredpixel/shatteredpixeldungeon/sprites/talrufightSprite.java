package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class talrufightSprite extends MobSprite {

    public talrufightSprite() {
        super();

        texture( Assets.Sprites.TALRU_FIGHT );

        TextureFilm frames = new TextureFilm( texture, 32, 32 );

        idle = new MovieClip.Animation( 2, true );
        idle.frames( frames, 50 );

        run = new MovieClip.Animation( 15, false );
        run.frames( frames, 50 );

        attack = new MovieClip.Animation( 15, false );
        attack.frames( frames, 50 );

        zap = attack.clone();

        die = new MovieClip.Animation( 8, false );
        die.frames( frames, 50 );

        play( idle.clone() );
    }

    @Override
    public void idle() {
        isMoving = false;
        super.idle();
    }
}