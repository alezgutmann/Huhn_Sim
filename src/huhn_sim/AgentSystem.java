package huhn_sim;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
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
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import libs.POGL;
import libs.LWJGLBasisFenster;
import libs.Vektor2D;
import libs.ShaderUtilities;

public class AgentSystem extends LWJGLBasisFenster {
	private ObjektManager agentenSpielwiese;
	private double runningAverageFrameTime = 1 / 60, avgRatio = 0.75;
	private long last = System.nanoTime();

	// ------------------------
	// shader gewidmeter code:
	private int staubShader;
	private int visualisationShader;

	public static String fragShaderCode;
	public static String visualisationShaderCode;

	private int uniform_fragShaderBlureffect_s, uniform_fragShaderBlureffect_tex2;
	private int uniform_fragShaderBlureffectVisualisation_tex1;

	private int blurTexture1, blurTexture1FB;
	private int blurTexture2, blurTexture2FB;
	final int WB = config.WIDTH / 4, HB = config.HEIGHT / 4;
	
	private int huhnTexture;
	// -------------------------

	public AgentSystem(String title, int width, int height) {
		super(title, width, height);
		initDisplay();
		shader_setup();
		agentenSpielwiese = ObjektManager.getExemplar();
		erzeugeAgenten(config.AGENTEN_ANZAHL);

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

	public void prepareZweiRotierendeFrameBuffer() {
		blurTexture1 = glGenTextures();
		blurTexture1FB = glGenFramebuffers();
		glBindTexture(GL_TEXTURE_2D, blurTexture1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, WB, HB, 0, GL_BGRA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glBindFramebuffer(GL_FRAMEBUFFER, blurTexture1FB);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, blurTexture1, 0);

		blurTexture2 = glGenTextures();
		blurTexture2FB = glGenFramebuffers();
		glBindTexture(GL_TEXTURE_2D, blurTexture2);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, WB, HB, 0, GL_BGRA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glBindFramebuffer(GL_FRAMEBUFFER, blurTexture2FB);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, blurTexture2, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		glBindTexture(GL_TEXTURE_2D, 0); // steht nur hier, damit die Bl�cke austauschbar bleiben
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
			
//			// ------ zum FrameBuffer debuggen
//			glBindFramebuffer(GL_FRAMEBUFFER, 0);
//			glViewport(0, 0, WIDTH, HEIGHT);
//			glClearColor(0.95f, 0.95f, 0.95f, 1.0f);
//			glClear(GL_COLOR_BUFFER_BIT);
//
//			glEnable(GL_TEXTURE_2D);
//			glBindTexture(GL_TEXTURE_2D, blurTexture1);
//
//			POGL.renderViereckMitTexturbindung();
//
//			glBindTexture(GL_TEXTURE_2D, 0);
//			glDisable(GL_TEXTURE_2D);
//			// ------
			
			glDisable(GL_DEPTH_TEST);
			int helpFB = blurTexture1FB;
			int helpT = blurTexture1;
			blurTexture1FB = blurTexture2FB;
			blurTexture1 = blurTexture2;
			blurTexture2FB = helpFB;
			blurTexture2 = helpT;

			long now = System.nanoTime();
			double diff = (now - last) / 1e9;
			runningAverageFrameTime = avgRatio * runningAverageFrameTime + (1 - avgRatio) * diff;
			last = now;

			// Framebuffer zurücksetzen
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			//glBindFramebuffer(GL_FRAMEBUFFER, blurTexture2FB);
			glUseProgram(0);
			//glViewport(0, 0, config.WIDTH, config.HEIGHT);
			

			// Matrix zurücksetzen
			glClearColor(0.95f, 0.95f, 0.95f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, WIDTH, HEIGHT, 0, 0, 1);
			glMatrixMode(GL_MODELVIEW);
			
			
			for (int i = 1; i <= agentenSpielwiese.getAgentSize(); i++) {
				Agent aktAgent = agentenSpielwiese.getAgent(i);

				aktAgent.render();
				aktAgent.update(diff);
			}
			
			// Framebuffer zurücksetzen
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			
			
			
			//glViewport(0, 0, config.WIDTH, config.HEIGHT);
			glClearColor(1, 1, 1, 1);
			POGL.renderViereckMitTexturbindung();			

			// **************************************
			// Der Shader staubShader erzeugt aus der zweiten
			// Texture visualisationShader den BlurEffekt und speichert diesen in FrameBuffer
			// blurTexture1FB.
			glBindFramebuffer(GL_FRAMEBUFFER, blurTexture1FB);
			glUseProgram(staubShader);
			glViewport(0, 0, WB, HB);

			glClearColor(1, 1, 1, 1);
			glClear(GL_COLOR_BUFFER_BIT);

			glLoadIdentity();

			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, blurTexture2);

			POGL.renderViereckMitTexturbindung();

			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, 0);
			// **************************************

			// **************************************
			// Der Blureffekt wird jetzt von Frame zu
			// Frame berechnet und das im nicht-sichtbaren
			// Bereich. Um das Ganze jetzt anschaulich zu
			// machen verwenden wir den FrameBuffer, der den
			// Ausgabebereich definiert, in unserem Fall das
			// erzeugte Canvasobjekt im JFrame.
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			glUseProgram(visualisationShader);
			glViewport(0, 0, WIDTH, HEIGHT);

			glEnable(GL_TEXTURE_2D);
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, blurTexture1);

			POGL.renderViereckMitTexturbindung();

			glDisable(GL_TEXTURE_2D);

			// Gebundene Texturen in der umgekehrten Reihenfolge
			// frei geben.
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, 0);

			glUseProgram(0);
			// **************************************
			Display.update();
		}
	}

	public void shader_setup() {
		prepareZweiRotierendeFrameBuffer();
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
		    throw new RuntimeException("Framebuffer is not complete!");
		}
		try {
			fragShaderCode = ShaderUtilities.readShadercode(config.fragShaderPath);
			visualisationShaderCode = ShaderUtilities.readShadercode(config.visualisationShaderPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		staubShader = glCreateProgram();

		int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragShader, fragShaderCode);
		glCompileShader(fragShader);
		System.out.println(glGetShaderInfoLog(fragShader, 1024));
		glAttachShader(staubShader, fragShader);

		glLinkProgram(staubShader);
		uniform_fragShaderBlureffect_s = glGetUniformLocation(staubShader, "s");
		uniform_fragShaderBlureffect_tex2 = glGetUniformLocation(staubShader, "tex2");
		glUseProgram(staubShader);

		glUniform2f(uniform_fragShaderBlureffect_s, 1.0f / WB, 1.0f / HB);
		glUniform1i(uniform_fragShaderBlureffect_tex2, 0);

		ShaderUtilities.testShaderProgram(staubShader);

		visualisationShader = glCreateProgram();

		glShaderSource(fragShader, visualisationShaderCode);
		glCompileShader(fragShader);
		System.out.println(glGetShaderInfoLog(fragShader, 1024));
		glAttachShader(visualisationShader, fragShader);

		glLinkProgram(visualisationShader);
		uniform_fragShaderBlureffectVisualisation_tex1 = glGetUniformLocation(visualisationShader, "tex1");
		glUseProgram(visualisationShader);
		glUniform1i(uniform_fragShaderBlureffectVisualisation_tex1, 0);

		ShaderUtilities.testShaderProgram(visualisationShader);
	}

	public static void main(String[] args) {
		new AgentSystem(config.TITLE, config.WIDTH, config.HEIGHT).start();
	}
}
