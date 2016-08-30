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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Masquer extends ApplicationAdapter {

	//initialise backend objects and game variables

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture dropImage;
	private Texture bucketImage;

	private Sound dropSound;
	private Music music;

	private Polygon bucket; //better to use Polygon than rect (rotation easy)
	ShapeRenderer shapeRenderer; //to render our polygons

	private Array<Pair> pairs; //holds our dancing pairs
	private long lastPairTime;
	Vector3 touchPos = new Vector3();
	private float easing = 0.05f;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		//load images
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		//load sounds
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

		//start bg music
		music.setLooping(true);
		music.play();

		//create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		batch = new SpriteBatch();

		//build game objects
		bucket = new Polygon(new float[]{0,0,64,0,64,64,0,64});
		bucket.setOrigin(32,32);
		bucket.setPosition(800/2 - 64/2,20);


		pairs = new Array<Pair>();
		spawnPair();

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(Color.GREEN);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.3f, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined); //adapt our game world to screen size

		batch.begin();
		batch.draw(bucketImage, bucket.getX(), bucket.getY());
		for(Pair pair : pairs){
			batch.draw(dropImage,pair.x, pair.y);

					}
		batch.end();

		shapeRenderer.setProjectionMatrix(camera.combined);	//adapt our game world to screen size
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for(Pair pair : pairs){
			shapeRenderer.polygon(pair.polygon.getTransformedVertices());
		}
		shapeRenderer.end();


		if(Gdx.input.isTouched()){ //move bucket toward touch position with easing function
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.setPosition(bucket.getX()+(touchPos.x - 64/2 - bucket.getX()) * easing,bucket.getY()+ (touchPos.y - 64/2 - bucket.getY()) * easing );

		}

		if(TimeUtils.nanoTime() - lastPairTime > 1000000000) spawnPair(); //time to spawn another pair

		Iterator<Pair> iter = pairs.iterator(); //an iterator makes for more efficient traversal
		while(iter.hasNext()){
			Pair pair = iter.next();
			pair.update();
			if(Intersector.overlapConvexPolygons(pair.polygon, bucket)){ //collision detection
				dropSound.play();
				iter.remove();
			}
		}

	}



	@Override
	public void dispose () {
		batch.dispose();
		dropImage.dispose();
		dropSound.dispose();
		music.dispose();
		bucketImage.dispose();
		shapeRenderer.dispose();
		batch.dispose();
	}

	private void spawnPair(){
		Pair pair = new Pair(MathUtils.random(0,800-64), MathUtils.random(0, 480-64), MathUtils.random(1, 4), MathUtils.random(1,3));
		pairs.add(pair);
		lastPairTime = TimeUtils.nanoTime();
	}
}
