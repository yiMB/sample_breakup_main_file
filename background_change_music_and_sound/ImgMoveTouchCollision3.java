package com.yi.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * this is the main class control and manage the whole game, mostly focus on interaction between object,<br>
 * update each object position, draw object,<br>
 * game rule, when should do what.
 */
public class ImgMoveTouchCollision3 extends ApplicationAdapter {
	SpriteBatch batch;

	FontText fontText;
	Texture background;
	SoundMusic soundMusic;
	String gameState;
	int score;

	// 5 clicks to go through 5 sounds before go to end state
	int countFiveClicks;
	// delay 3 second before allow click from end state to start state
	long lastTimeDelayEnd;

	@Override
	public void create () {
		batch = new SpriteBatch();

		fontText = new FontText();
		background = new Texture("starry_sky_start_game.png");
		soundMusic = new SoundMusic();
		gameState = "start";
		score = 0;

		countFiveClicks = 5;
	}

	@Override
	public void dispose() {
		batch.dispose();
		soundMusic.disposeAllAudios();
		background.dispose();
		fontText.disposeBitmapFont();
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
				gameState = "play";
				countFiveClicks = 5;
			}
		}
		// touch screen to end game
		else if(gameState.equalsIgnoreCase("play")){
			if (Gdx.input.justTouched()){
				if(countFiveClicks == 5){
					soundMusic.playSoundPlayerShot();
					countFiveClicks--;
				}
				else if(countFiveClicks == 4){
					soundMusic.playSoundBossShot();
					countFiveClicks--;
				}
				else if(countFiveClicks == 3){
					soundMusic.playSoundExplosion();
					countFiveClicks--;
				}
				else if(countFiveClicks == 2){
					soundMusic.playSoundPlayerHit();
					countFiveClicks--;
				}
				else if(countFiveClicks == 1){
					soundMusic.playSoundBossHit();
					countFiveClicks--;
				}
				else if(countFiveClicks == 0){
					lastTimeDelayEnd = TimeUtils.nanoTime();
					gameState = "end";
					countFiveClicks = 5;
				}
			}
		}
		// touch screen to start game
		else if(gameState.equalsIgnoreCase("end")){
			background = new Texture("starry_sky_game_over_end_game.png");
			if (Gdx.input.justTouched()){
				if (TimeUtils.nanoTime() - lastTimeDelayEnd > 3000000000l) {
					gameState = "start";
//					Gdx.app.exit();
				}
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
			// draw texts
			fontText.draw(batch, "Score="+score, Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 50);
			fontText.draw(batch, "Life=", 50, Gdx.graphics.getHeight() - 50);
			fontText.draw(batch, "Cheesecake Life=", Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() - 50);
			batch.end();
		}
		// end game state only has a end background image
		else if(gameState.equalsIgnoreCase("end")){
			batch.begin();
			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.end();
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
