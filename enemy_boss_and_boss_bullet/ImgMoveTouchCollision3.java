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

	TextureBugControl textureBugControl;
	// scoreAllowBugRespawn and scoreAllowCheesecakeToMove should be the same number
	int numberOfBugs, scoreAllowBugRespawn;
	float increaseVelocity;

	TextureCheesecakeControl textureCheesecakeControl;
	int scoreAllowCheesecakeToMove;

	TextureMoonControl textureMoonControl;
	long lastTimeMoon;
	long timeGapMoon;

	Texture background;
	String gameState;
	int score, maxScore;

	@Override
	public void create () {
		batch = new SpriteBatch();

		textureBugControl = new TextureBugControl();
		numberOfBugs = 5;
		scoreAllowBugRespawn = 10;
		increaseVelocity = 1;
		textureBugControl.addNTextureBug(numberOfBugs);

		textureCheesecakeControl = new TextureCheesecakeControl();
		textureCheesecakeControl.addNTextureCheesecake(1);
		scoreAllowCheesecakeToMove = -10;

		textureMoonControl = new TextureMoonControl();
		lastTimeMoon = TimeUtils.nanoTime();
		timeGapMoon = 2000000000l;

		background = new Texture("starry_sky_start_game.png");
		gameState = "start";
		score = 0;
		maxScore = 15;
	}

	@Override
	public void dispose() {
		batch.dispose();
		background.dispose();
		textureBugControl.disposeBugs();
		textureCheesecakeControl.disposeCheesecakes();
		textureMoonControl.disposeMoons();
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
			// check enemies respawn
			if(shouldRespawnBugs(scoreAllowBugRespawn)){
				respawnBugs();
			}
			textureBugControl.update();
			if(score >= scoreAllowCheesecakeToMove){
				textureCheesecakeControl.update();
				// boss shot, bullet limited to 2 second apart
				if (TimeUtils.nanoTime() - lastTimeMoon > timeGapMoon) {
					lastTimeMoon = TimeUtils.nanoTime();
					for(TextureCheesecake textureCheesecake : textureCheesecakeControl.arrTextureCheesecakes){
						textureMoonControl.add3TextureMoons(
								new Vector2(textureCheesecake.rectCheesecakeBorder.x +
										textureCheesecake.rectCheesecakeBorder.width / 2 - TextureMoon.spriteMoonWidth / 2,
										textureCheesecake.rectCheesecakeBorder.y - TextureMoon.spriteMoonHeight / 2));
					}
				}
				textureMoonControl.update();
			}
			if(shouldGameover(maxScore)){
				gameState = "end";
			}
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
			// draw boss bullets
			textureMoonControl.draw(batch);
			// draw enemies
			textureBugControl.draw(batch);

			// draw bosses
			if(score >= scoreAllowCheesecakeToMove){
				textureCheesecakeControl.draw(batch);
			}
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
	 * check when should enemy respawn
	 */
	public boolean shouldRespawnBugs(int scoreAllowBugRespawn){
		if(textureBugControl.arrTextureBugs.size == 0 && score < scoreAllowBugRespawn){
			return true;
		}
		return false;
	}

	/**
	 * respawn enemies with speed change
	 */
	public void respawnBugs(){
		textureBugControl.arrTextureBugs.clear();
		textureBugControl.addNTextureBug(numberOfBugs, increaseVelocity);
	}

	/**
	 * game over rule,<br></>
	 * not yet set detail rule, just score reach 10 and kill boss
	 */
	public boolean shouldGameover(int maxScore){
		if(score == maxScore){
			return true;
		}
		return false;
	}

	/**
	 * reset all data to start of the game,<br>
	 * use when start and restart the game
	 */
	public void resetGame(){
		gameState = "play";
		textureMoonControl.arrTextureMoons.clear();
		textureBugControl.arrTextureBugs.clear();
		textureBugControl.addNTextureBug(numberOfBugs);
		textureCheesecakeControl.arrTextureCheesecakes.clear();
		textureCheesecakeControl.addNTextureCheesecake(1);
		score=0;
		increaseVelocity=1;
	}
}
