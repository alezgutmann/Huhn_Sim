import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.Display;

public class main extends LWJGLBasisFenster{
	private ObjektManager huhns;
	
	public main() {
		super("Mirko meddl und moininger!", 800, 450);
		huhns = ObjektManager.getExemplar();
	    erzeugeFlummies(500);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new main().start();
	}

	@Override
	public void renderLoop() {
	      glEnable(GL_DEPTH_TEST);

	      while (!Display.isCloseRequested()) {
	         POGL.clearBackgroundWithColor(0.95f, 0.95f, 0.95f, 1.0f);

	         glLoadIdentity();
	         glOrtho(0, WIDTH, HEIGHT, 0, 0, 1);
	         glDisable(GL_DEPTH_TEST);

	         for (int i = 1; i <= huhns.getFlummiSize(); i++) {
	            Flummi aktFlummi = huhns.getFlummi(i);
	            aktFlummi.render();
	            aktFlummi.update();
	         }

	         Display.update();
	         }
	      
		}
	
	
	
	private void erzeugeFlummies(int anz) {
	      Random rand = ThreadLocalRandom.current();
	      for (int i = 0; i < anz; i++) {
	         huhns.registriereFlummi(new Flummi(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), rand.nextFloat() + 1,
	               rand.nextInt(3) + 1, rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
	      }
	   }

}



