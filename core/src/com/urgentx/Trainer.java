package com.urgentx.masquer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Stack;

/**
 * Created by Barco on 30-Aug-16.
 */
public class Trainer {


    private int x;
    private long lastTick;
    private boolean hasKeeper;
    private Stack<Keeper> keepers; //quick access and easy removal of current Keeper
    private Keeper keeper;

    public Trainer(int x){
        lastTick = 0;
        this.x = x;
        keepers = new Stack<Keeper>();
    }

    public void update(){
        if(TimeUtils.nanoTime() - lastTick > 10000000) tick();
    }

    public void levelUp(){
        keepers.peek().increaseLevel();
        keepers.peek().setY(80);
    }

    public void tick(){
        if(hasKeeper){
            Gdx.app.log("mytag", " lvl: " + keeper.getLevel());
            keeper.giveXp(1);
            keeper.increaseY(1);
            if(keeper.getY() > 400){
                levelUp();
            }
        }
        lastTick = TimeUtils.nanoTime();
    }

    public void setKeeper(Keeper keeper){
        keeper.setX(x-20);
        keeper.setY(80);
        keepers.push(keeper);
        this.keeper = keepers.peek();
        hasKeeper = true;
    }

    public Keeper getKeeper(){
        return keeper;
    }

    public void clearKeeper(Keeper keeper){
        if(!keepers.isEmpty()) {
            if (this.keeper == keeper) {
                keeper.setX(500);
                keepers.pop();
                hasKeeper = false;
            }
        }
    }

    public void clearKeeper(){
        if(!keepers.isEmpty()) {
            Gdx.app.log("mytag1", "keeper x: " + keeper.getX());
            keeper.setX(500);
            keepers.pop();
            keeper = null;
            hasKeeper = false;
        }
    }

    public int getX(){
        return x;
    }


}
