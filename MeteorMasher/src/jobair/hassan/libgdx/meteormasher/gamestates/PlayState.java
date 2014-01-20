package jobair.hassan.libgdx.meteormasher.gamestates;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import jobair.hassan.libgdx.meteormasher.entities.Asteroid;
import jobair.hassan.libgdx.meteormasher.entities.Bullet;
import jobair.hassan.libgdx.meteormasher.entities.Particle;
import jobair.hassan.libgdx.meteormasher.entities.Player;
import jobair.hassan.libgdx.meteormasher.entities.SpaceObject;
import jobair.hassan.libgdx.meteormasher.MeteorMasherMain;
import jobair.hassan.libgdx.meteormasher.managers.GameKeys;
import jobair.hassan.libgdx.meteormasher.managers.GameStateManager;


import java.util.ArrayList;

public class PlayState extends GameState {
	
	private SpriteBatch sb;
	private ShapeRenderer sr;
	
	private BitmapFont font;
	
	private Player player;
	private ArrayList<Bullet> bullets;
	private ArrayList<Asteroid> asteroids;
	
	private ArrayList<Particle> particles;
	
	private int level;
	private int totalAsteroids;
	private int numAsteroidsLeft;
	
	public PlayState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {
		
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		
		/*This not working; causes program to crash: Find out why and fix
		 * FreeTypeFontGenerator gen = 
			new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/Hyperspace Bold.ttf")
			);
		font = gen.generateFont(20);*/
		
		bullets = new ArrayList<Bullet>();
		
		player = new Player(bullets);
		
		asteroids = new ArrayList<Asteroid>();
		
		particles = new ArrayList<Particle>();
		
		level = 1;
		spawnAsteroids();
		
	}
	
	private void createParticles(float x, float y) {
		for(int i = 0; i < 6; i++) {
			particles.add(new Particle(x, y));
		}
	}
	
	private void splitAsteroids(Asteroid a) {
		createParticles(a.getx(), a.gety());
		numAsteroidsLeft--;
		if(a.getType() == Asteroid.LARGE) {
			asteroids.add(
				new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
			asteroids.add(
				new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
		}
		if(a.getType() == Asteroid.MEDIUM) {
			asteroids.add(
				new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
			asteroids.add(
				new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
		}
	}
	
	private void spawnAsteroids() {
		
		asteroids.clear();
		
		int numToSpawn = 4 + level - 1;
		totalAsteroids = numToSpawn * 7;
		numAsteroidsLeft = totalAsteroids;
		
		for(int i = 0; i < numToSpawn; i++) {
			
			float x = MathUtils.random(MeteorMasherMain.WIDTH);
			float y = MathUtils.random(MeteorMasherMain.HEIGHT);
			
			float dx = x - player.getx();
			float dy = y - player.gety();
			float dist = (float) Math.sqrt(dx * dx + dy * dy);
			
			while(dist < 100) {
				x = MathUtils.random(MeteorMasherMain.WIDTH);
				y = MathUtils.random(MeteorMasherMain.HEIGHT);
				dx = x - player.getx();
				dy = y - player.gety();
				dist = (float) Math.sqrt(dx * dx + dy * dy);
			}
			
			asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
			
		}
		
	}
	
	public void update(float dt) {
		
		// get user input
		handleInput();
		
		// next level
		if(asteroids.size() == 0) {
			level++;
			spawnAsteroids();
		}
		
		// update player
		player.update(dt);
		if(player.isDead()) {
			player.reset();
			player.loseLife();
			return;
		}
		
		// update player bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update(dt);
			if(bullets.get(i).shouldRemove()) {
				bullets.remove(i);
				i--;
			}
		}
		
		// update asteroids
		for(int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).update(dt);
			if(asteroids.get(i).shouldRemove()) {
				asteroids.remove(i);
				i--;
			}
		}
		
		// update particles
		for(int i = 0; i < particles.size(); i++) {
			particles.get(i).update(dt);
			if(particles.get(i).shouldRemove()) {
				particles.remove(i);
				i--;
			}
		}
		
		// check collision
		checkCollisions();
		
	}
	
	private void checkCollisions() {
		
		// player-asteroid collision
		if(!player.isHit()) {
			for(int i = 0; i < asteroids.size(); i++) {
				Asteroid a = asteroids.get(i);
				if(a.intersects(player)) {
					player.hit();
					asteroids.remove(i);
					i--;
					splitAsteroids(a);
					break;
				}
			}
		}
		
		// bullet-asteroid collision
		for(int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			for(int j = 0; j < asteroids.size(); j++) {
				Asteroid a = asteroids.get(j);
				if(a.contains(b.getx(), b.gety())) {
					bullets.remove(i);
					i--;
					asteroids.remove(j);
					j--;
					splitAsteroids(a);
					
					break;
				}
			}
		}
		
	}
	
	public void draw() {
		
		// draw player
		player.draw(sr);
		
		// draw bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(sr);
		}
		
		// draw asteroids
		for(int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).draw(sr);
		}
		
		// draw particles
		for(int i = 0; i < particles.size(); i++) {
			particles.get(i).draw(sr);
		}
		
		/* draw score after fixing font generator
		sb.setColor(1, 1, 1, 1);
		sb.begin();
		font.draw(sb, Long.toString(player.getScore()), 40, 390);
		sb.end(); */
		
	}
	
	public void handleInput() {
		player.setLeft(GameKeys.isDown(GameKeys.LEFT));
		player.setRight(GameKeys.isDown(GameKeys.RIGHT));
		player.setUp(GameKeys.isDown(GameKeys.UP));
		if(GameKeys.isPressed(GameKeys.SPACE)) {
			player.shoot();
		}
	}
	
	public void dispose() {}
	
}
