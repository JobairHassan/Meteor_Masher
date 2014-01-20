package jobair.hassan.libgdx.meteormasher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import jobair.hassan.libgdx.meteormasher.managers.GameInputProcessor;
import jobair.hassan.libgdx.meteormasher.managers.GameKeys;
import jobair.hassan.libgdx.meteormasher.managers.GameStateManager;

public class MeteorMasherMain implements ApplicationListener {
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static OrthographicCamera cam;
	
	private GameStateManager gsm;
	
	public void create() {
		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		
		Gdx.input.setInputProcessor(
			new GameInputProcessor()
		);
		
		gsm = new GameStateManager();
		
	}
	
	public void render() {
		
		// clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();
		
		GameKeys.update();
		
	}
	
	public void resize(int width, int height) {}
	public void pause() {}
	public void resume() {}
	public void dispose() {}
	
}

