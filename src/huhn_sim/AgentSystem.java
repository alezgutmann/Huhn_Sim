package huhn_sim;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPolygonMode;
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
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.Display;

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

			glClearColor(0.95f, 0.95f, 0.95f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, WIDTH, HEIGHT, 0, 0, 1);
			glMatrixMode(GL_MODELVIEW);
			glDisable(GL_DEPTH_TEST);

			for (int i = 1; i <= agentenSpielwiese.getAgentSize(); i++) {
				Agent aktAgent = agentenSpielwiese.getAgent(i);

				aktAgent.render(object);
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
