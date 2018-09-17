package com.erkan.zombienado2.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.erkan.zombienado2.client.menus.GameLobby;
import com.erkan.zombienado2.client.menus.MainMenu;
import com.erkan.zombienado2.client.menus.Menu;
import com.erkan.zombienado2.client.world.*;
import com.erkan.zombienado2.client.world.World;
import com.erkan.zombienado2.data.world.Map;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.client.networking.ConnectionListener;
import com.erkan.zombienado2.client.networking.ServerProxy;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.networking.ServerHeaders;
import com.badlogic.gdx.graphics.Texture;
import com.erkan.zombienado2.server.misc.FilterConstants;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import static com.erkan.zombienado2.graphics.Transform.*;

public class Client extends ApplicationAdapter implements ConnectionListener, JoinGameListener, ContactListener, InputProcessor {
	private Sound sound_amb;
	Texture ground;
	World world;

	Sound music;

	Menu mainMenu;
	Menu charSelect;

	List<Zombie> zombies = new ArrayList<>();
	int current_wave = 0;

	GameState state = GameState.InMenu;

	BitmapFont font;
	SpriteBatch batch;
	SpriteBatch batch_hud;
	ShapeRenderer debugRenderer;

	Box2DDebugRenderer dDebugRenderer;


	static Vector3 camera_world_coordinates = new Vector3(); //used for sound calculations ://// not nice
	static float camera_zoom = 1f;

	OrthographicCamera camera;
	Self self;
	int my_id;

	float zoom = 1f;
	float zoom_to = 1f;
	float effect_magnification = 1f;

	TeamMate[] teamMates;

	Vector2[][] bullets = new Vector2[0][0];
	Loot loot[] = new Loot[0];

	@Override
	public synchronized void join(String IP, int PORT) {
		ServerProxy.addListener(this);
		ServerProxy.connect(IP, PORT);
		charSelect.create();
	}

	@Override
	public synchronized void ready(String name, String character) {
		ServerProxy.join(name, character);
	}

	@Override
	public void create () {
		mainMenu = new MainMenu(this);
		charSelect = new GameLobby(this);

		world = new World();
		Weapon.init();
		Loot.loadTextures();
 		ground = new Texture("misc/test_ground.png");
 		ground.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sound_amb = Gdx.audio.newSound(Gdx.files.internal("audio/misc/notification_amb.mp3"));

 		Zombie.init();
		PhysicsHandler.init(this);
		NotificationManager.init();
		dDebugRenderer = new Box2DDebugRenderer();
		debugRenderer = new ShapeRenderer();
		font = new BitmapFont();
		//self = new Self("n00b", Character.OFFICER); //PLACEHOLDER
		//zoom_to = self.getWeapon().getWeaponData().scope; //PLACEHOLDER
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = to_screen_space(6f);
		batch = new SpriteBatch();
		batch_hud = new SpriteBatch();

		music = Gdx.audio.newSound(Gdx.files.internal("audio/misc/bm2.mp3"));






		Map.TEST_MAP.getStructures().stream().forEach(structure -> {
			world.add(new Structure(structure.getFirst(), structure.getSecond()));
		});

		Map.TEST_MAP.getBoundaries().stream().forEach(boundary -> {
			world.add(new Wall(boundary.getFirst(), boundary.getSecond()));
		});

		Map.TEST_MAP.getObjs_back().stream().forEach(obj -> {
			world.add_back(obj);
		});

		Map.TEST_MAP.getObjs_front().stream().forEach(obj -> {
			world.add_front(obj);
		});

		Map.TEST_MAP.getObjs_top().stream().forEach(obj -> {
			world.add_top(obj);
		});


		mainMenu.create();

	}

	float testAngle = 0;


	@Override
	public synchronized void render () {
		switch (state){
			case InMenu:
				mainMenu.render();
				return;
			case InCharacterSelect:
				//charSelect.render();
				//return;
			default:
				break;
		}

		if (state.equals(GameState.InGame)) {
			//TEST
			testAngle += 10;

			Zombie.static_update();

			if (zoom < zoom_to * effect_magnification) {
				float delta = zoom_to * effect_magnification - zoom;
				zoom += Math.min(delta / 10, 0.1f);

				if (zoom > zoom_to * effect_magnification)
					zoom = zoom_to * effect_magnification;
			} else if (zoom > zoom_to * effect_magnification) {
				float delta = zoom - zoom_to * effect_magnification;
				zoom -= Math.min(delta / 10, 0.1f);

				if (zoom < zoom_to * effect_magnification)
					zoom = zoom_to * effect_magnification;
			}

			camera.zoom = zoom;

			Vector2 movementvector = new Vector2(0, 0);
			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				movementvector.x--;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				movementvector.x++;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				movementvector.y++;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				movementvector.y--;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.R)) {
				ServerProxy.reload();
			}


			float mouse_dx = Gdx.input.getX() - Gdx.graphics.getWidth() / 2;
			float mouse_dy = -Gdx.input.getY() + Gdx.graphics.getHeight() / 2;
			if (self.isAlive()) {
				float rot = MathUtils.radiansToDegrees * MathUtils.atan2(mouse_dy, mouse_dx);
				self.distance_to_focus = MathUtils.clamp(new Vector2(mouse_dx, mouse_dy).len() / 4 + 300, to_screen_space(4), to_screen_space(10));
				self.rotation = rot;

				self.run(movementvector.cpy());
			} else {
				self.run(new Vector2(0, 0));
			}

			ServerProxy.move(movementvector);
			ServerProxy.rotate(self.rotation);


			if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
				ServerProxy.fire();


			if (self.isAlive()) {
				camera.position.x = to_screen_space(self.position.x) + mouse_dx / 2;
				camera.position.y = to_screen_space(self.position.y) + mouse_dy / 2;
			} else {
				camera.position.x += movementvector.x * 3;
				camera.position.y += movementvector.y * 3;
				camera.zoom = 1f;
			}


		}
		camera.update();
		camera_world_coordinates.x = Transform.scale_to_world(camera.position.x);
		camera_world_coordinates.y = Transform.scale_to_world(camera.position.y);
		camera_zoom = zoom;

		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		batch.begin();
		for (int i = 0; i < 50; i++){
			for (int j = 0; j < 50; j++){
				batch.draw(ground, (i-25)*500, (j-25)*500, 500, 500);
			}
		}

		world.render_back(batch);

		zombies.stream().forEach(zombie -> zombie.render_gore(batch));
		Arrays.stream(loot).forEach(l -> l.draw(batch));
		zombies.stream().forEach(zombie -> zombie.render(batch));

		if (teamMates != null) {
			for (int i = 0; i < teamMates.length; i++) {
				TeamMate tm = teamMates[i];
				if (tm != null)
					tm.render(batch);
			}
		}

		if (state.equals(GameState.InGame))
			self.render(batch);


		world.render_front(batch);


		for (Vector2[] bullets:
				bullets) {
			Sprite b_sprite = new Sprite(Bullet.texture);
			float b_rot = new Vector2(bullets[1].x, bullets[1].y).setLength(1).angle() - 90;
			b_sprite.setRotation(b_rot);

			b_sprite.setCenter(to_screen_space(bullets[0].x) - Bullet.texture.getHeight() * MathUtils.cosDeg(b_rot + 90) / 2, to_screen_space(bullets[0].y)- Bullet.texture.getHeight() * MathUtils.sinDeg(b_rot + 90) / 2);
			//b_sprite.setOrigin(0, 0);
			b_sprite.draw(batch);
		}

		batch.end();
		PhysicsHandler.update();
		PhysicsHandler.getRayHandler().setCombinedMatrix(camera);
		PhysicsHandler.getRayHandler().updateAndRender();
		batch.begin();

		world.render_top(batch);
		batch.end();

		Gdx.gl.glLineWidth(0.2f);
		debugRenderer.setProjectionMatrix(camera.combined);
		/*
		debugRenderer.begin(ShapeRenderer.ShapeType.Line);
		debugRenderer.setColor(new Color(.5f, 1f, .7f, .4f));

		 WorldManager.getNavigationGraph().getNodes().stream().forEach(v -> {
			Array<Connection<Vector2>> edges = WorldManager.getNavigationGraph().getConnections(v);
			edges.forEach(e->{
				debugRenderer.line(to_screen_space(e.getFromNode()), to_screen_space(e.getToNode()));
			});
		});
		debugRenderer.end();

		debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
		debugRenderer.setColor(new Color(.1f, .4f, 1f, .4f));
		WorldManager.getNavigationGraph().getNodes().stream().forEach(v -> {
			WorldManager.getNavigationGraph().getNodes().forEach(p ->{
					debugRenderer.circle(to_screen_space(p.x), to_screen_space(p.y), 3);
			});

		});
		debugRenderer.end();*/
		//dDebugRenderer.render(PhysicsHandler.getWorld(), camera.combined);
		if (state.equals(GameState.InGame)) {

			batch_hud.begin();
			font.setColor(Color.WHITE);
/*
		font.draw(batch_hud, "Position: " + (int)(self.position.x * 10) / 10f + ", " + (int)(self.position.y * 10) / 10f, 5, Gdx.graphics.getHeight() - 5);
		font.draw(batch_hud, "Wave: " + current_wave, 5, Gdx.graphics.getHeight() - 20);
		font.draw(batch_hud, "cursor: " + Transform.scale_to_world(camera.position.x + (Gdx.input.getX() - Gdx.graphics.getWidth()/2)*camera.zoom) + ", " + Transform.scale_to_world(camera.position.y - (Gdx.input.getY() - Gdx.graphics.getHeight()/2)*camera.zoom), 5, Gdx.graphics.getHeight() - 35);
		font.draw(batch_hud, "Health: " + self.getHealth() + "/" + self.MAX_HEALTH, 5, Gdx.graphics.getHeight() - 50);
*/

			if (teamMates != null)
				Arrays.stream(teamMates).forEach(teamMate -> {
					if (teamMate != null) teamMate.hud_draw(batch_hud, font);
				});

			Texture w_tex = new Texture(self.getWeapon().getWeaponData().texture_path);

			batch_hud.draw(w_tex, camera.viewportWidth - 70 - 15, 40);
			GlyphLayout glyphLayout = new GlyphLayout();
			glyphLayout.setText(font, self.getWeapon().getWeaponData().name);
			font.draw(batch_hud, self.getWeapon().getWeaponData().name, camera.viewportWidth - 70 - glyphLayout.width, 90);
			glyphLayout.setText(font, self.getAmmo() + "/" + self.getWeapon().getWeaponData().mag_size);
			font.draw(batch_hud, self.getAmmo() + "/" + (self.getExcessAmmo() == -1 ? "INF" : self.getExcessAmmo()), camera.viewportWidth - 70 - glyphLayout.width, 70);

			font.draw(batch_hud, "Health:", 50, 90);
			int hp_prec = (int) ((self.getHealth() / self.MAX_HEALTH) * 100);
			if (hp_prec < 30)
				font.setColor(Color.RED);
			else if (hp_prec < 65)
				font.setColor(Color.ORANGE);
			font.draw(batch_hud, hp_prec + "%", 50, 70);
			w_tex.dispose();
		} else {
			charSelect.render();
			batch_hud.begin();
		}
		float w = camera.viewportWidth / 2;
		float h = camera.viewportHeight - 100;
		NotificationManager.draw(batch_hud, w, h);
		batch_hud.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//img.dispose();
	}

	@Override
	public synchronized void onMsgReceived(String... args) {

		//System.out.println(args[0]);
		switch (args[0]){
			case ServerHeaders.CONNECT:
				state = GameState.InCharacterSelect;
				break;
			case ServerHeaders.RECONNECT:
				//? //ingame?
				break;
			case ServerHeaders.CONNECT_TO_LOBBY:
				NotificationManager.push_notification(args[1] + " has connected");
				break;
			case ServerHeaders.JOIN_SELF:
				my_id = Integer.parseInt(args[1]);
				System.out.println("My id is: "+my_id);
				teamMates = new TeamMate[Integer.parseInt(args[2])];
				break;
			case ServerHeaders.JOIN_PLAYER:
			{
				int id = Integer.parseInt(args[1]);
				if (id != my_id)
					teamMates[id] = new TeamMate(args[2], Character.getCharacter(args[3])); //TODO: fix
				else
					self = new Self(args[2], Character.getCharacter(args[3]));

				if (state.equals(GameState.InCharacterSelect)){
					((GameLobby)charSelect).addCharacter(args[2], args[3]);
				}

				break;
			}
			case ServerHeaders.LAUNCH_GAME:
			{
				music.loop();
				state = GameState.InGame;
				Gdx.input.setInputProcessor(this);
				NotificationManager.post("Survive");
				break;
			}
			case ServerHeaders.UPDATE_PLAYER: {
				int id = Integer.parseInt(args[1]);
				if (id == my_id) {
					self.position.x = Float.parseFloat(args[2]);
					self.position.y = Float.parseFloat(args[3]);
					self.setHealth(Float.parseFloat(args[5]));
					if (!args[5].equals(self.getWeapon().getWeaponData().toString())){
						WeaponData wd = WeaponData.getWeapon(args[6]);
						self.setWeapon(WeaponData.getWeapon(args[6]));
						zoom_to = wd.scope;
					}
					self.setAmmo(Integer.parseInt(args[7]));
					self.setExcessAmmo(Integer.parseInt(args[8]));
					self.setAlive(Boolean.parseBoolean(args[11]));
					return;
				}
				teamMates[id].position.x = Float.parseFloat(args[2]);
				teamMates[id].position.y = Float.parseFloat(args[3]);
				teamMates[id].rotation = Float.parseFloat(args[4]);
				teamMates[id].setHealth(Float.parseFloat(args[5]));

				if (!args[6].equals(teamMates[id].getWeapon().getWeaponData().toString())){
					WeaponData wd = WeaponData.getWeapon(args[6]);
					teamMates[id].setWeapon(wd);
				}
				teamMates[id].setAmmo(Integer.parseInt(args[7]));
				teamMates[id].setExcessAmmo(Integer.parseInt(args[8]));
				Vector2 movement = new Vector2(Float.parseFloat(args[9]),Float.parseFloat(args[10]));
				teamMates[id].run(movement);
				teamMates[id].setAlive(Boolean.parseBoolean(args[11]));

				break;
			}
			case ServerHeaders.PLAYER_RELOAD:
				if (Integer.parseInt(args[1]) == my_id)
					self.reload();
				else
					teamMates[Integer.parseInt(args[1])].reload();
				break;
			case ServerHeaders.CREATE_BULLET:
				if (Integer.parseInt(args[1]) == my_id)
					self.shoot();
				else
					teamMates[Integer.parseInt(args[1])].shoot();
				// will be used for view stuff
				break;
			case ServerHeaders.UPDATE_BULLETS:
				bullets = new Vector2[(args.length - 1) / 4][2];
				//System.out.println(bullets.length);
				for (int i = 0; i < bullets.length; i++){
					bullets[i][0] = new Vector2(Float.parseFloat(args[4 *i + 1]), Float.parseFloat(args[4 *i + 2]));
					bullets[i][1] = new Vector2(Float.parseFloat(args[4 *i + 3]), Float.parseFloat(args[4 *i + 4]));
				}
				break;
			case ServerHeaders.UPDATE_LOOT:
				loot = new Loot[(args.length - 1) / 3];
				for (int i = 0; i < loot.length; i++){
					loot[i] = new Loot(Float.parseFloat(args[i * 3 + 1]), Float.parseFloat(args[i * 3 + 2]), args[i * 3 + 3]);
				}
				break;
			case ServerHeaders.UPDATE_ZOMBIES:
					for (int i = 0; i < (args.length - 1) / 6; i++) {
						if (zombies.size() <= i) {
							zombies.add(new Zombie(Float.parseFloat(args[6 * i + 4])));
						}

						if (zombies.get(i).isAlive()) {
							zombies.get(i).setPosition(Float.parseFloat(args[6 * i + 1]), Float.parseFloat(args[6 * i + 2]));
							zombies.get(i).setRotation(Float.parseFloat(args[6 * i + 3]));
							zombies.get(i).setHealth(Float.parseFloat(args[6 * i + 4]));

							com.erkan.zombienado2.server.Zombie.Behavior b = args[6 * i + 6].equals("Roaming") ? com.erkan.zombienado2.server.Zombie.Behavior.Roaming : (args[6 * i + 6].equals("Hunting") ? com.erkan.zombienado2.server.Zombie.Behavior.Hunting : com.erkan.zombienado2.server.Zombie.Behavior.Standing);
							zombies.get(i).setBehavior(b);
							//System.out.println(args[6 * i + 6]);
							if (Boolean.parseBoolean(args[6 * i + 5])){
								zombies.get(i).attack();
							}
						}
					}
				break;
			case ServerHeaders.WAVE_END:
				NotificationManager.post("Wave  "+args[1]+"  ended");
				Zombie.fade();
				break;
			case ServerHeaders.WAVE_START:
				SoundManager.addPrioSound(sound_amb, sound_amb.play());
				NotificationManager.post("Wave  "+args[1]+"  started");
				zombies.stream().forEach(zombie -> zombie.destroy()); //Potential problem
				zombies = new ArrayList<>();
				Zombie.reset_fade();
				current_wave = Integer.parseInt(args[1]);
				break;

			case ServerHeaders.GAME_OVER:
				NotificationManager.post("Game over. Final score:   "+Integer.parseInt(args[1]));
				break;
		}
	}

	@Override
	public void beginContact(Contact contact) {
		if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.ROOF_SENSOR){
			Player p = (Player)contact.getFixtureB().getUserData();
			p.setIndoor(true);
			if (p.equals(self))
				((Structure)contact.getFixtureA().getUserData()).hide_roof();
				effect_magnification = 0.65f;
		}
		if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.ROOF_SENSOR){
			Player p = (Player)contact.getFixtureA().getUserData();
			p.setIndoor(true);
			if (p.equals(self))
				((Structure)contact.getFixtureB().getUserData()).hide_roof();
				effect_magnification = 0.65f;
		}
	}

	@Override
	public void endContact(Contact contact) {
		if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.ROOF_SENSOR){
			Player p = (Player)contact.getFixtureB().getUserData();
			p.setIndoor(false);
			if (p.equals(self)) {
				((Structure)contact.getFixtureA().getUserData()).show_roof();
				effect_magnification = 1f;
			}
		}
		if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.ROOF_SENSOR){
			Player p = (Player)contact.getFixtureA().getUserData();
			p.setIndoor(false);
			if (p.equals(self))
				((Structure)contact.getFixtureB().getUserData()).show_roof();
				effect_magnification = 1f;
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

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

		if (character == '1') {
			ServerProxy.switch_weapon();
		}
		if (character == '2') {
			ServerProxy.switch_weapon();
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		ServerProxy.switch_weapon();
		return false;
	}

	private enum GameState {
		InMenu, InCharacterSelect, InGame
	}
}
