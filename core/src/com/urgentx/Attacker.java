package com.urgentx.masquer;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by Barco on 04-Sep-16.
 */
public class Attacker {

    private int level, hp, attackDamage;
    private float x, y;
    private int xp, xpToLevel;
    private Image sourceImage;
    private double speedX, speedY;

    public Attacker(float x, float y, Image sourceImage){
        this.x = x;
        this.y = y;
        //xp = 0;
        //xpToLevel = 100;
        this.sourceImage = sourceImage;
        hp = 20;
        speedX = 0.3;
        speedY = 0;
        attackDamage = 1;
        hp = 50;
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

    public void update(){
        x+= speedX;
        y += speedY;
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

    public void damage(int damage){
        hp -= damage;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public Image getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(Image sourceImage) {
        this.sourceImage = sourceImage;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}




