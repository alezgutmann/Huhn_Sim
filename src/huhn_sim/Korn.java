package huhn_sim;

import static org.lwjgl.opengl.GL11.glTranslated;

import libs.POGL;
import libs.Vektor2D;

public class Korn extends BasisObjekt{
	
	public Korn(Vektor2D position) {
		super(position);
	}
	
	public void render() {
		// glTranslated(0,0, config.zOFFSET);
		// POGL.renderKreis((float)this.position.x,(float) this.position.y,5f,10f);
		POGL.renderObjectWithForces((float)this.position.x, (float)this.position.y,  2, new Vektor2D(0,0), new Vektor2D(0,0));
	}
}
