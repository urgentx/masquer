package com.urgentx.masquer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by Barco on 30-Aug-16.
 */
public class Keeper {

    private int level, hp;
    private float x, y;
    private int xp, xpToLevel;
    private Image sourceImage;
    private Label label;
    private int speed, attackDamage;


    public Keeper(float x, float y, int level, Image sourceImage, Label label){
        this.x = x;
        this.y = y;
        this.level = level;
        xp = 0;
        xpToLevel = 100;
        this.sourceImage = sourceImage;
        this.label = label;
        hp = 100;
        attackDamage = 1;

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getX() {
        return x;
    }

    public void giveXp(int xp){
        if(xp > xpToLevel){
            level++;
            xpToLevel += 200;
        }
        this.xp += xp;
    }

    public void increaseY(float y){
        this.y += y;
    }

    public void increaseX(float x){
        this.x += x;
    }

    public void increaseLevel(){
        level++;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public Image getSourceImage() {
        return sourceImage;
    }

    public void damage(int damage){
        hp -= damage;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setSourceImage(Image sourceImage) {
        this.sourceImage = sourceImage;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }


}
