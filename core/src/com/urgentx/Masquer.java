package com.urgentx.masquer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import sun.rmi.runtime.Log;

public class Masquer extends ApplicationAdapter {

    //initialise backend objects and game variables

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture dropImage, bucketImage, trainerImage, keeperImage, attackerImage, baseImage, attackerSheet;
    TextureRegion[] attackerAnimationFrames;
    Animation animation;

    private Sound dropSound;
    private Music music;

    private Polygon bucket; //better to use Polygon than rect (rotation easy)
    ShapeRenderer shapeRenderer; //to render our polygons

    //test objects
    private Trainer trainer1;
    private Trainer trainer2;
    private Keeper keeper1;
    private Attacker attacker1;

    float closestDistance;

    private int baseHp = 500;
    private Label baseHpLabel;
    private boolean keepersDefending;
    private int keeperSpawn; //increment with each tick()

    private ArrayList<Keeper> keepers; //could use LGX List
    private ArrayList<Attacker> attackers;
    private ArrayList<Label> labels;

    Vector3 touchPos = new Vector3();
    private float easing = 0.05f;

    private Image keeperSourceImage1;


    private long lastTick;
    static Timer timer;

    ////////////////////////////


    Stage stage;
    private Skin skin;

    @Override
    public void create() {
        batch = new SpriteBatch();
        //load images
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        trainerImage = new Texture(Gdx.files.internal("trainer.png"));
        keeperImage = new Texture(Gdx.files.internal("keeper.png"));
        attackerImage = new Texture(Gdx.files.internal("attacker.png"));
        baseImage = new Texture(Gdx.files.internal("base.png"));
        attackerSheet = new Texture(Gdx.files.internal("base.png"));

        TextureRegion [] [] tmpFrames = TextureRegion.split(attackerSheet, 25, 25);

        //initialize animation
        attackerAnimationFrames = new TextureRegion[4];

        int index = 0;
        for(int i = 0 ; i < 2; i++){
            for(int j = 0; j <2; j++){
                attackerAnimationFrames[index++] = tmpFrames[j][i];
            }
        }

        animation = new Animation(1f/4f, attackerAnimationFrames);

        //load sounds
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        //start bg music
        music.setLooping(true);
        music.play();


        ////////////////////////////////////////////////////

        trainer1 = new Trainer(600);
        trainer2 = new Trainer(700);

        keepers = new ArrayList<Keeper>();
        attackers = new ArrayList<Attacker>();

        keeperSpawn = 0;


        stage = new Stage(new StretchViewport(800, 480)); //scene2d handles camera perspective
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();  //Skin holds our style options
        skin.add("default", new Label.LabelStyle(new BitmapFont(), Color.BLUE));
        skin.add("trainer", trainerImage);
        skin.add("keeper", keeperImage);
        skin.add("attacker", attackerImage);
        skin.add("base", baseImage);

        //create actors
        Image validTargetImage = new Image(skin, "trainer");
        validTargetImage.setBounds(700, 80, 10, 300);
        stage.addActor(validTargetImage);

        Image baseActorImage = new Image (skin, "base");
        baseActorImage.setBounds(500, 100, 50, 200);
        stage.addActor(baseActorImage);

        Image invalidTargetImage = new Image(skin, "trainer");
        invalidTargetImage.setBounds(600, 80, 10, 300);
        stage.addActor(invalidTargetImage);

        baseHpLabel = new Label("Base HP: " + baseHp, skin);
        baseHpLabel.setBounds(400, 450, 40, 30);
        stage.addActor(baseHpLabel);

       // keeper1 = new Keeper(0, 0, 1, keeperSourceImage1);


        closestDistance = 1000; //default distance to closest defender for attackers


        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        }, new Date(), 10000); //tick at regular rate
    }


    @Override
    public void render() {

        keepersDefending = false;

        Iterator<Keeper> keeperIterator = keepers.iterator();
        while(keeperIterator.hasNext()) {       //iterate through all Keepers
            Keeper keeper = keeperIterator.next();

            keeper.update();
            keeper.getSourceImage().setX(keeper.getX());    //update Actor location
            keeper.getSourceImage().setY(keeper.getY());

            keeper.getLabel().setX(keeper.getX() + 20);
            keeper.getLabel().setY(keeper.getY() + 18);
            keeper.getLabel().setText(Integer.toString(keeper.getLevel()));

            if(keeper.getHp() <= 0){        //check for death
                keeper.getSourceImage().remove();
                keeper.getLabel().remove();
                keeper = null;
                keeperIterator.remove();
            } else {

                if (keeper.getX() < 500) {      //check if Keeper in defending zone
                    keepersDefending = true;
                }
            }
        }


        Iterator<Attacker>  attackerIterator = attackers.iterator();
        while(attackerIterator.hasNext()){      //iterate through all Attackers

            Attacker attacker = attackerIterator.next();

            attacker.update();
            attacker.getSourceImage().setX(attacker.getX());        //update Actor location
            attacker.getSourceImage().setY(attacker.getY());



            if(attacker.getHp() <= 0){      //check for death
                attackerIterator.remove();
                attacker.getSourceImage().remove();
                attacker = null;
            } else {
                if (keepersDefending) {
                    if (! keepers.isEmpty()) {       //check for collisions

                        Keeper closest = null;
                        closestDistance = 10000000; //reset so that all keepers are considered once more

                        for (Keeper keeper : keepers) {   //traverse keepers

                            float keeperX = keeper.getX();
                            float keeperY = keeper.getY();

                            float xd = Math.abs(keeperX - attacker.getX()); //X distance from keeper to attacker
                            float yd = Math.abs(keeperY - attacker.getY()); //Y distance from keeper to attacker
                            float distance = (float) Math.sqrt(xd * xd + yd * yd);
                            Gdx.app.log("mytag1", "distance = " + distance + "keeperX = " + keeper.getX());
                            if (distance < closestDistance) {
                                closest = keeper;
                                closestDistance = distance;
                            }
                        }

                        if(closest != null) {

                            //Gdx.app.log("mytag1", " --" + closest.getX());
                            //move attacker toward keeper
                            float xSpeed = (closest.getX() - attacker.getX()) / 500;
                            float ySpeed = (closest.getY() - attacker.getY()) / 500;
                            float factor = 0.3f / (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);

                            attacker.setSpeedX(xSpeed * factor);
                            attacker.setSpeedY(ySpeed * factor);

                            //check for collision, deal damage
                            if (Math.abs(attacker.getX() - closest.getX()) < 10 && Math.abs(attacker.getY() - closest.getY()) < 10) {
                                closest.damage(attacker.getAttackDamage());
                                attacker.damage(closest.getAttackDamage());

                            }
                        }


                    }
                } else {        //else move attacker toward base

                    float attackerX = attacker.getX(); //move these to global variables
                    float attackerY = attacker.getY();
                    float keeperX;
                    float keeperY;
                    float xd, yd;  //x distance, y distance from keeper
                    float distance;

                    float xSpeed = (525 - attacker.getX()) / 500;
                    float ySpeed = (200 - attacker.getY()) / 500;


                    attacker.setSpeedX(xSpeed);
                    attacker.setSpeedY(ySpeed);

                    if (attackerX > 480) {      //damage base
                        baseHp -= 1;
                    }

                }
            }

        }


        trainer1.update();
        trainer2.update();

        baseHpLabel.setText("Base HP: " + baseHp);

       // if (TimeUtils.nanoTime() - lastTick > 1000000000 * 100) tick();


        //keeperSourceImage1.setX(keeper1.getX());
       // keeperSourceImage1.setY(keeper1.getY());

        Gdx.gl.glClearColor(1f, 0.9f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }


    @Override
    public void dispose() {
        batch.dispose();
        dropImage.dispose();
        dropSound.dispose();
        music.dispose();
        bucketImage.dispose();


    }

    public void tick() {
        Gdx.app.log("mytag", "ks: " + keeperSpawn);
        if(keeperSpawn % 3 == 0 && keepers.size() < 3) {
            spawnKeeper();
        }
        spawnAttacker();
        lastTick = TimeUtils.nanoTime();
        keeperSpawn++;

    }

    public void spawnKeeper() {

        //create Scene2D Actor for each Keeper
        final Image sourceImage = new Image(skin, "keeper");
        int yPos = MathUtils.random(50,400);
        int xPos = MathUtils.random(400,500);
        sourceImage.setBounds(xPos, yPos, 50, 50);
        stage.addActor(sourceImage);

        Label label = new Label("1", skin);

        stage.addActor(label);

        final Keeper keeper = new Keeper(xPos, yPos, 1, sourceImage, label);

        label.setX(keeper.getX() + 20);
        label.setY(keeper.getY() + 18);

        //set up Listener for each Keeper
        sourceImage.addListener(new DragListener() {

            //follow touch when dragged
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                //keeperSourceImage1.moveBy(x-keeperSourceImage1.getWidth()/2,y-keeperSourceImage1.getHeight()/2);
                keeper.increaseX(x - sourceImage.getWidth() / 2);
                keeper.increaseY(y - sourceImage.getHeight() / 2);

            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            //add/remove Keeper to/from Trainer
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (event.getStageX() > 700 && event.getStageX() < 740) {
                    trainer2.clearKeeper();
                    trainer2.setKeeper(keeper);
                } else if (event.getStageX() > 600 && event.getStageX() < 640) {
                    trainer1.clearKeeper();
                    trainer1.setKeeper(keeper);
                } else {
                    trainer1.clearKeeper(keeper);
                    trainer2.clearKeeper(keeper);
                }
            }
        });

        keepers.add(keeper);
    }

    public void spawnAttacker() {


        //create Scene2D Actor for each Attacker, no need for Listeners.
        final Image sourceImage = new Image(skin, "attacker");
        sourceImage.setBounds(10,200,25,25);
        stage.addActor(sourceImage);

        final Attacker attacker = new Attacker(10, 200, sourceImage);
        attackers.add(attacker);
    }

}
