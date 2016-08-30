package com.urgentx.masquer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * Basic class with information about the Pair (of dancers) objects.
 *
 */
public class Pair {

    int x, y;
    float speedX;
    float speedY;
    private Texture viewCone;
    Rectangle boundingRect;
    Sprite sprite;
    Polygon polygon;

    public Pair(int x, int y, float speedX, float speedY){
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        boundingRect = new Rectangle();
        boundingRect.x = x;
        boundingRect.y = y;
        boundingRect.width = 64;
        boundingRect.height = 64;
        viewCone = new Texture(Gdx.files.internal("viewcone.png"));

        sprite = new Sprite(viewCone);

        polygon = new Polygon(new float[]{0,0,64,0,64,64,0,64});
        polygon.setOrigin(32, 32);
        polygon.setPosition(x, y);
    }


    public void update(){
        x += speedX;
        y += speedY;

        boundingRect.x = x;
        boundingRect.y = y;

        polygon.setPosition(x+50, y+50);

        if(x > 800 - 64 || x < 0){
            speedX = speedX * -1;
            polygon.rotate(45f);
        }

        if(y > 480 - 64 || y < 0){
            speedY = speedY * -1;
            polygon.rotate(-45f);
        }

    }

}
