package com.yi.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * this is the main class control and manage the whole game, mostly focus on interaction between object,<br>
 * update each object position, draw object,<br>
 * game rule, when should do what.
 */
public class ImgMoveTouchCollision3 extends ApplicationAdapter {
	SpriteBatch batch;

	TextureDroplet textureDroplet;

	TextureCookieControl textureCookieControl;
	long lastTimeCookie;
	long timeGapCookie;

	Texture background;
	String gameState;

	@Override
	public void create () {
		batch = new SpriteBatch();

		textureDroplet = new TextureDroplet();

		textureCookieControl = new TextureCookieControl();
		lastTimeCookie = TimeUtils.nanoTime();
		timeGapCookie = 200000000l;

		background = new Texture("starry_sky_start_game.png");
		gameState = "start";
	}

	@Override
	public void dispose() {
		batch.dispose();
		textureDroplet.textureDroplet.dispose();
		background.dispose();
		textureCookieControl.disposeCookies();
	}

	@Override
	public void render() {
		update();
		draw();
	}

	/**
	 * part of render() method, update() method,<br>
	 * check and change for current state of the game, update data of that state.<br>
	 * on play state:<br>
	 * -check finger touch to move player and shot.<br>
	 * -update player position.<br>
	 * -check enemies respawn according to score.<br>
	 * -update enemies position.<br>
	 * -update player bullet position.<br>
	 * -check boss spawn according to score.<br>
	 * -update boss position.<br>
	 * -update boss bullet position.
	 */
	public void update() {
		// touch screen to play game
		if(gameState.equalsIgnoreCase("start")){
			background = new Texture("starry_sky_start_game.png");
			if (Gdx.input.justTouched()){
				resetGame();
			}
		}
		// win or lose game to end game
		else if(gameState.equalsIgnoreCase("play")){
			// touch input control player and bullet
			Gdx.input.setInputProcessor(new InputProcessor() {
				// move player, dt times 2 to make single touch to match drag speed
				@Override
				public boolean touchDown(int screenX, int screenY, int pointer, int button) {
					textureDroplet.positionUpdate(screenX, Gdx.graphics.getDeltaTime() * 2);
					return true;
				}
				// player shot, bullet limited to 0.2 second apart
				@Override
				public boolean touchUp(int screenX, int screenY, int pointer, int button) {
					if (TimeUtils.nanoTime() - lastTimeCookie > timeGapCookie) {
						lastTimeCookie = TimeUtils.nanoTime();
						textureCookieControl.addTextureCookie(
								new Vector2(textureDroplet.rectDropletBorder.x + textureDroplet.rectDropletBorder.width / 2 - TextureCookie.spriteCookieWidth / 2,
										textureDroplet.rectDropletBorder.y + textureDroplet.spriteDropletHeight));
					}
					return true;
				}
				// move player
				@Override
				public boolean touchDragged(int screenX, int screenY, int pointer) {
					textureDroplet.positionUpdate(screenX, Gdx.graphics.getDeltaTime());
					return true;
				}
				@Override
				public boolean keyDown(int keycode) {
					return false;
				}
				@Override
				public boolean keyUp(int keycode) {
					return false;
				}
				@Override
				public boolean keyTyped(char character) {
					return false;
				}
				@Override
				public boolean mouseMoved(int screenX, int screenY) {
					return false;
				}
				@Override
				public boolean scrolled(int amount) {
					return false;
				}
			});
			textureCookieControl.update();
		}
		// touch screen to start game
		else if(gameState.equalsIgnoreCase("end")){
			background = new Texture("starry_sky_game_over_end_game.png");
			if (Gdx.input.justTouched()){
				gameState = "start";
			}
		}
	}

	/**
	 * part of render() method, draw() method,<br>
	 * check and change for current state of the game, update data of that state.<br>
	 * on play state:<br>
	 * -draw any object that should be draw on the screen.<br>
	 * -detect collision, such as between enemy and player bullet, enemy and player, boss and player bullet, boss bullet and player.<br>
	 * -remove object from their Array when you design fit, such as when object collide.<br>
	 * -change score.<br>
	 * -change enemy speed.<br>
	 * -change and add any game play related which you design fit.
	 */
	public void draw(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// start game state only has a start background image
		if(gameState.equalsIgnoreCase("start")){
			batch.begin();
			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.end();
		}
		// play state I did not use background image because it lags the game, and I don't know why
		// draw texts of player and boss life and score, location just hardcoded in
		// draw array of player bullets, boss bullets, enemies, and bosses
		// draw 1 player
		else if(gameState.equalsIgnoreCase("play")){
			batch.begin();
			// draw player bullets
			textureCookieControl.draw(batch);
			// draw 1 player
			textureDroplet.draw(batch);
			batch.end();
		}
		// end game state only has a end background image
		else if(gameState.equalsIgnoreCase("end")){
			batch.begin();
			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.end();
		}
	}

	/**
	 * reset all data to start of the game,<br>
	 * use when start and restart the game
	 */
	public void resetGame(){
		gameState = "play";
		textureCookieControl.arrTextureCookies.clear();
		textureDroplet.reset();
	}
}
