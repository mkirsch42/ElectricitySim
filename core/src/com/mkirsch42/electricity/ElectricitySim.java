package com.mkirsch42.electricity;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class ElectricitySim extends Game implements InputProcessor {

    // Displays particles, vectors, and paths
    private OrthographicCamera camera;
    // Displays pause symbol and help menu
    private OrthographicCamera hud;

    // Logic variables
    // List of particles in simulation
    private ArrayList<Particle> particles;
    // Distance between field vectors
    private int res = 10;
    // Maximum rendered vector
    private int maxVec = 100;
    // Vector render toggle
    private boolean drawVec = true;
    // Pause toggle
    private boolean paused = false;
    // Render new particle toggle
    private boolean showNewParticle = false;
    // The previously variables are used to hold previous states of paused
    // and showNewParticle while dragging
    private boolean previouslyShowNewParticle = false;
    private boolean previouslyPaused = false;
    // Represents a particle to be added with a click
    private Particle addParticle = new Particle(0, 0, 1000, 100);
    // Fullscreen toggle
    private boolean fullscreen = false;
    // Render paths toggle
    private boolean showPaths = true;
    // Whether a particle is being added
    // Used to hide paused symbol even though the sim is paused while adding a
    // new particle
    // Also used to draw velocity vector while adding new particle
    private boolean adding = false;
    // Default k value for rendering
    private double K_DEF = 500D;
    // Current k value - changes while zooming
    private double K = K_DEF;
    // Tutorial window toggle
    private boolean showTut = false;
    public static double gefX = 0;
    public static double gefY = 0;
    // Default camera velocity
    private final float CAM_V = 10;
    private final int[] SPEEDS = { 1, 5, 10, 20, 50, 100, 200, 1000, 100000 };
    private int speed = 3;
    private boolean limitPath = true;
    private Vector3 v;
    private ShapeRenderer sr;
    private SpriteBatch sb;
    private BitmapFont bf;

    @Override
    public void create() {
	// Set up cameras
	camera = new OrthographicCamera();
	camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	camera.update();
	hud = new OrthographicCamera();
	hud.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	hud.update();
	sr = new ShapeRenderer();
	sb = new SpriteBatch();
	bf = new BitmapFont();
	// Set up inputs
	Gdx.input.setInputProcessor(this);
	// Initialize simulation
	addParticles(0);
    }

    // Resets particle list and camera
    private void clearParticles() {
	gefX = 0;
	gefY = 0;
	camera.zoom = 1;
	K = K_DEF;
	camera.update();
	camera.position.set(new Vector3(camera.viewportWidth / 2, camera.viewportHeight / 2, 0));
	particles = new ArrayList<>();
    }

    private void moveCamera() {

	float x = (Gdx.input.isKeyPressed(Keys.A) ? -CAM_V : 0) + (Gdx.input.isKeyPressed(Keys.D) ? CAM_V : 0);
	float y = (Gdx.input.isKeyPressed(Keys.W) ? -CAM_V : 0) + (Gdx.input.isKeyPressed(Keys.S) ? CAM_V : 0);
	camera.translate(x * camera.zoom, y * camera.zoom);
	camera.zoom *= (Gdx.input.isKeyPressed(Keys.APOSTROPHE) ? 1 / 1.05 : 1)
		* (Gdx.input.isKeyPressed(Keys.SEMICOLON) ? 1.05 : 1);
	camera.update();
	K = K_DEF / camera.zoom;
	// The mouse is moving in the game world, but not on the screen so
	// mouseMoved isn't triggered. But only trigger it if you aren't
	// already adding the particle since you already set the particle's
	// position by that point.
	if (!Gdx.input.isButtonPressed(Buttons.LEFT))
	    mouseMoved(Gdx.input.getX(), Gdx.input.getY());
    }

    // Reset the simulation and set a given preset
    private void addParticles(int i) {
	if (i < 0 || i >= ParticlePresets.PRESETS.length)
	    return;
	float width = camera.viewportWidth;
	float height = camera.viewportHeight;
	clearParticles();
	ParticlePresets.PRESETS[i].apply(particles, width, height);
    }

    // Calculate the electric field at (x, y). If rendering, increase
    // vector length by K and possibly preview addParticle.
    public VectorPoint calcVP(double x, double y, boolean rendering) {
	double k = rendering ? K : 1;
	// Field vector
	double fx = 0;
	double fy = 0;
	for (Particle p : particles) {
	    double px = p.x;
	    double py = p.y;
	    // Project coordinates onto camera if rendering. Only do this
	    // during render because otherwise you lose precision when
	    // converting to floats which breaks the calculations.
	    if (rendering) {
		v = new Vector3((float) p.x, (float) p.y, 0f);
		v = camera.project(v);
		px = (double) v.x;
		py = camera.viewportHeight - (double) v.y;
	    }
	    // Distance squared
	    double d = (x - px) * (x - px) + (y - py) * (y - py);
	    // Fixes artifacts while rendering
	    if (d <= 0.001) {
		continue;
	    }
	    // Magnitude of total electric field
	    double q = k * p.q() / d;
	    double sqd = Math.sqrt(d);
	    // Projects field onto x and y components
	    fx += q * (x - px) / sqd;
	    fy += q * (y - py) / sqd;
	}
	// Take addParticle into account if we are rendering and we want to
	// The math is the same as above
	if (rendering && showNewParticle) {
	    v = camera.project(new Vector3((float) addParticle.x, (float) addParticle.y, 0f));
	    double d = (x - v.x) * (x - v.x) + (y - v.y) * (y - v.y);
	    double q = k * addParticle.q / d;
	    double sqd = Math.sqrt(d);
	    fx += q * (x - v.x) / sqd;
	    fy += q * (y - v.y) / sqd;
	}
	fx += gefX;
	fy += gefY;
	return new VectorPoint(fx, fy);
    }

    // Do not bypass pause
    private void update(double delta) {
	update(delta, false);
    }

    // Updates particles. Delta gives fluctuation between
    // iterations, but I only ever set it to 1 because
    // otherwise it introduces random errors. Force
    // allows bypassing paused (for stepping).
    private void update(double delta, boolean force) {
	if (paused && !force)
	    return;
	// Update iteration counter
	for (Particle p : particles) {
	    p.t++;
	}
	// Update acceleration vectors
	// Do not update positions until all accelerations
	// are calculated since everything happens simultaneously.
	for (Particle p : particles) {
	    VectorPoint ef = calcVP(p.x, p.y, false);
	    // Multiply field vector by this particles charge
	    // to get the force, divide by mass to get acceleration.
	    p.vx += delta * ef.x * p.q() / p.m;
	    p.vy += delta * ef.y * p.q() / p.m;
	}
	// Update all positions and paths
	for (Particle p : particles) {
	    p.x += delta * p.vx;
	    p.y += delta * p.vy;
	    p.pushPath();
	}
    }

    public void truncatePaths() {
	if(Runtime.getRuntime().freeMemory() < Runtime.getRuntime().totalMemory() / 3) {
	    for(Particle p : particles) {
		p.truncatePath();
	    }
	}
    }
    
    // Graphics
    @Override
    public void render() {
	moveCamera();
	truncatePaths();
	// Update simulation
	int iters = SPEEDS[speed];
	for (int i = 0; i < iters; i++) {
	    update(0.05);
	}
	// Clear screen
	Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	sr.setProjectionMatrix(camera.combined);
	// Draw field vectors
	sr.begin(ShapeType.Line);
	if (drawVec) {
	    for (int x = 0; x <= camera.viewportWidth; x += res) {
		for (int y = 0; y <= camera.viewportHeight; y += res) {
		    drawVP(sr, x, y, calcVP(x, y, true));
		}
	    }
	}
	sr.end();
	// Draw paths
	if (showPaths) {
	    sr.begin(ShapeType.Line);
	    for (Particle p : particles) {
		sr.setColor(p.pathColor());
		float[][] paths = p.path(limitPath);
		for (float[] path : paths) {
		    try {
			sr.polyline(path);
		    } catch (IllegalArgumentException e) {
		    }
		}
	    }
	    sr.end();
	}
	sr.begin(ShapeType.Filled);
	// Draw particles
	for (Particle p : particles) {
	    sr.setColor(p.color());
	    sr.circle((float) p.x, (float) p.y, 6f * camera.zoom);
	}
	// Draw velocity vector for addParticle
	if (adding) {
	    v = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
	    sr.rectLine((float) addParticle.x, (float) addParticle.y, v.x, v.y, 3 * camera.zoom, addParticle.color(),
		    Color.BLUE);
	}
	sr.end();
	sr.setProjectionMatrix(hud.combined);
	// Draw pause symbol
	sr.begin(ShapeType.Filled);
	if (paused && (!adding || previouslyPaused)) {
	    sr.setColor(Color.WHITE);
	    sr.rect(35, hud.viewportHeight - 125, 25, 100);
	    sr.rect(85, hud.viewportHeight - 125, 25, 100);
	}
	// Draw tutorial window
	if (showTut) {
	    sr.setColor(Color.LIGHT_GRAY);
	    sr.rect(20, 20, 450, 350);
	    sr.end();
	    sb.setProjectionMatrix(hud.combined);
	    sb.begin();
	    bf.setColor(Color.BLACK);
	    bf.draw(sb,
		    "Electricity Simulator\n" + "  Controls:\n" + "    WASD\n" + "    [ ]\n" + "    ; '\n" + "    , .\n"
			    + "    /\n" + "    N\n" + "    Space\n" + "    \\\n" + "    M\n" + "    Scroll\n"
			    + "    Left Ctrl\n" + "    Middle click\n" + "    Left click\n" + "    Backspace\n"
			    + "    0-9\n" + "    F1",
		    40, 350);
	    bf.draw(sb, "\n\n" + "Move Camera\n" + "Increase/decrease resolution\n" + "Zoom in/out\n"
		    + "Increase/decrease max vector length\n" + "Toggle field vectors\n" + "Toggle particle paths\n"
		    + "Pause/resume\n" + "Step while paused\n" + "Toggle new particle preview\n"
		    + "Set new particle charge\n" + "Hold for precise values\n" + "Flip charge\n"
		    + "Add new particle, drag for velocity vector\n" + "Clear all particles and reset view\n"
		    + "Load presets\n" + "Show/hide this window", 150, 350);
	    sb.end();
	}
	sr.end();

    }

    public void drawVP(ShapeRenderer sr, int x, int y, VectorPoint vp) {
	// Limit the vector length to maxVec
	v = new Vector3((float) vp.x, (float) vp.y, 0).limit2(maxVec * maxVec);
	double vx = v.x;
	double vy = v.y;
	// c1 is the "positive-facing" endpoint, c2 is the "negative-facing"
	// endpoint
	Color c1 = Color.RED;
	Color c2 = Color.GREEN;
	// Draw a white dot if the vector would otherwise not be rendered
	if (Math.abs(vx) < 1 && Math.abs(vy) < 1) {
	    c1 = c2 = Color.WHITE;
	    vx = 1;
	    vy = 1;
	}
	// Set endpoints and draw them on the camera, not the game world
	double x1 = x - vx / 2f;
	double y1 = y - vy / 2f;
	v = camera.unproject(new Vector3((float) x1, (float) y1, 0f));
	x1 = v.x;
	y1 = v.y;
	double x2 = x + vx / 2f;
	double y2 = y + vy / 2f;
	v = camera.unproject(new Vector3((float) x2, (float) y2, 0f));

	sr.line((float) x1, (float) y1, v.x, v.y, c1, c2);
    }

    @Override
    public void dispose() {

    }

    // Update viewport sizes
    @Override
    public void resize(int width, int height) {
	camera.setToOrtho(true, width, height);
	camera.update();
	hud.setToOrtho(false, width, height);
	hud.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    // Input handling

    // Move on WASD down
    @Override
    public boolean keyDown(int keycode) {
	return false;
    }

    @Override
    public boolean keyUp(int keycode) {
	switch (keycode) {
	case Keys.SLASH:
	    drawVec = !drawVec;
	    break;
	case Keys.COMMA:
	    maxVec -= Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? 10 : 50;
	    if (maxVec < 0)
		maxVec = 0;
	    break;
	case Keys.PERIOD:
	    maxVec += Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? 10 : 50;
	    break;
	case Keys.LEFT_BRACKET:
	    res += 1;
	    break;
	case Keys.RIGHT_BRACKET:
	    res -= 1;
	    break;
	case Keys.SPACE:
	    paused = !paused;
	    if (particles.size() > 0)
		System.out.println(particles.get(0).t);
	    break;
	case Keys.M:
	    showNewParticle = !showNewParticle;
	    break;
	case Keys.BACKSPACE:
	    clearParticles();
	    break;
	case Keys.F11:
	    fullscreen = !fullscreen;
	    if (fullscreen) {
		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
	    } else {
		Gdx.graphics.setWindowedMode(1280, 720);
	    }
	    break;
	case Keys.N:
	    showPaths = !showPaths;
	    break;
	case Keys.BACKSLASH:
	    if (paused)
		for (int i = 0; i < 5; i++)
		    update(.02, true);
	    break;
	case Keys.F1:
	    showTut = !showTut;
	    break;
	case Keys.K:
	    speed--;
	    if (speed < 0)
		speed = 0;
	    break;
	case Keys.L:
	    speed++;
	    if (speed >= SPEEDS.length)
		speed = SPEEDS.length - 1;
	    break;
	case Keys.P:
	    limitPath = !limitPath;
	    break;
	// Get preset index and set it up if it's a number key
	default:
	    int preset = keycode - Keys.NUM_1;
	    if (preset >= 0 && preset < 10) {
		addParticles(preset);
	    }
	}
	return false;
    }

    // Allows for hold to zoom
    @Override
    public boolean keyTyped(char character) {
	return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	if (button != Buttons.LEFT)
	    return false;
	// Add particle at mouse
	v = camera.unproject(new Vector3(screenX, screenY, 0));
	addParticle.x = v.x;
	addParticle.y = v.y;
	// Store previous state, pause sim, and stop showing new particle
	// as it's already in the world
	previouslyPaused = paused;
	paused = true;
	previouslyShowNewParticle = showNewParticle;
	showNewParticle = false;
	particles.add(new Particle(addParticle));
	adding = true;
	return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	// Flip charge
	if (button == Buttons.MIDDLE)
	    addParticle.q *= -1;
	// Need to check adding because otherwise mouseUp is triggered when
	// double-clicking to maximize window
	if (button != Buttons.LEFT || !adding)
	    return false;
	// Get new particle and set its velocity
	Particle p = particles.get(particles.size() - 1);
	v = camera.unproject(new Vector3(screenX, screenY, 0));
	p.vx = (v.x - p.x) / 40d;
	p.vy = (v.y - p.y) / 40d;
	// Restore previous states
	paused = previouslyPaused;
	showNewParticle = previouslyShowNewParticle;
	adding = false;
	return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
	return false;
    }

    // Set addParticle's coordinates to the mouse
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
	v = camera.unproject(new Vector3(screenX, camera.viewportHeight - screenY, 0));
	addParticle.x = v.x;
	addParticle.y = v.y;
	return false;
    }

    @Override
    public boolean scrolled(int amount) {
	addParticle.q -= amount * (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? 10 : 100);
	return false;
    }

}
