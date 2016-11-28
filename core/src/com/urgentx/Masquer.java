package com.urgentx.masquer;


import com.badlogic.gdx.Game;

import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Masquer extends Game {

    //initialise environment objects and game variables

    private OrthographicCamera camera;
    public SpriteBatch batch;
    public BitmapFont font;


    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();

        this.setScreen(new MainMenuScreen(this));


    }


    @Override
    public void render() {

        super.render();

    }


    @Override
    public void dispose() {
        batch.dispose();


    }


}
