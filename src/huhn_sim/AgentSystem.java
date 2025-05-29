package huhn_sim;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.input.Keyboard;

import libs.LWJGLBasisFenster;
import libs.Model;
import libs.POGL;
import libs.Vektor2D;
import libs.ShaderUtilities;

public class AgentSystem extends LWJGLBasisFenster {
	private ObjektManager agentenSpielwiese;
	private double runningAverageFrameTime = 1 / 60, avgRatio = 0.75;
	private long last = System.nanoTime();
	private int staubShader;

	public static String fragShaderCode;
	
	Model object = null;
	
	boolean MousebuttonDown = false;
	
	KoernerManager kornManager = new KoernerManager();

	public AgentSystem(String title, int width, int height) {
		super(title, width, height);
		initDisplay();
		shader_setup();
		loadObject(config.OBJFilePath);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		agentenSpielwiese = ObjektManager.getExemplar();
		erzeugeAgenten(config.AGENTEN_ANZAHL);

	}
	
	public boolean loadObject(String fileName) {
		try {
			object = POGL.loadModel(new File(fileName));
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void erzeugeAgenten(int anz) {
		Random rand = ThreadLocalRandom.current();

		for (int i = 0; i < anz; i++) {
			Agent agent = new Agent(new Vektor2D(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)),
					new Vektor2D(rand.nextFloat() * 20, 0), 10, 1f, 1f, 1f);
			agent.setVerhalten(new VerhaltenAgent(agent));
			agent.setObjektManager(agentenSpielwiese);
			agent.setKoernerManager(kornManager);
			agentenSpielwiese.registrierePartikel(agent);
		}
	}

	public int getCurrFPS() {
		return (int) (1 / runningAverageFrameTime);
	}

	@Override
	public void renderLoop() {
		glEnable(GL_DEPTH_TEST);

		while (!Display.isCloseRequested()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			long now = System.nanoTime();
			double diff = (now - last) / 1e9;
			runningAverageFrameTime = avgRatio * runningAverageFrameTime + (1 - avgRatio) * diff;
			last = now;
			glUseProgram(staubShader);

			glClearColor(0.2f, 0.95f,0, 0.8f); //Hintergrundfarbe
			glClear(GL_COLOR_BUFFER_BIT);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, WIDTH, HEIGHT, 0, -100, 100);
			glMatrixMode(GL_MODELVIEW);
			glDisable(GL_DEPTH_TEST);
			
			if (Mouse.isButtonDown(0)) {
				MousebuttonDown = true;	
			}
			else {
				MousebuttonDown = false;
			}
			
			// Inside your renderLoop, after processing mouse input:
			if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
			    // Example: Place Korn at mouse position or center
			    int x = Mouse.getX();
			    int y = HEIGHT - Mouse.getY(); // Convert to window coordinates if needed
			    Korn korn = new Korn(new Vektor2D(x,y));
			    kornManager.addKorn(korn);
			}
			
			// holen der uniform locations und anschliessendes setzen
			
			int timeLocation = glGetUniformLocation(staubShader, "time");
			glUniform1f(timeLocation, (float)(System.nanoTime() / 1e9));
			int mouseDownLocation = glGetUniformLocation(staubShader,"mouseDown");
			glUniform1i(mouseDownLocation, MousebuttonDown ? 1 : 0);
			
			kornManager.render();
			for (int i = 1; i <= agentenSpielwiese.getAgentSize(); i++) {
				Agent aktAgent = agentenSpielwiese.getAgent(i);

				
				// Rainbow Mode
				if (MousebuttonDown) {
					aktAgent.MAX_SPEED = config.AGENTEN_MAX_SPEED * config.rainbowMultiplier / 2;
					aktAgent.MAX_TURN_RATE = config.AGENTEN_MAX_TURN_RATE * config.rainbowMultiplier * 100;
					aktAgent.MASS = config.AGENTEN_MASS / config.rainbowMultiplier;
					//aktAgent.acceleration.mult(config.rainbowMultiplier);
				}
				else {
					aktAgent.MAX_SPEED = config.AGENTEN_MAX_SPEED;
					aktAgent.MAX_TURN_RATE = config.AGENTEN_MAX_TURN_RATE;
					aktAgent.MASS = config.AGENTEN_MASS;
				}
				
				aktAgent.render(object);
				// nächstes Korn ermitteln
				
				// wenn kein korn vorhanden dann halt nur nach maus gehen
				aktAgent.update(diff);
			}

			Display.update();
		}
	}

	public void shader_setup() {
		try {
			fragShaderCode = ShaderUtilities.readShadercode(config.fragShaderPath);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		staubShader = glCreateProgram();

		int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragShader, fragShaderCode);
		glCompileShader(fragShader);
		System.out.println(glGetShaderInfoLog(fragShader, 1024));
		glAttachShader(staubShader, fragShader);

		glLinkProgram(staubShader);
		glUseProgram(staubShader);

		ShaderUtilities.testShaderProgram(staubShader);
	}

	public static void main(String[] args) {
		new AgentSystem(config.TITLE, config.WIDTH, config.HEIGHT).start();
	}
}
