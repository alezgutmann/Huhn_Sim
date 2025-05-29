package huhn_sim;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import libs.Vektor2D;
import libs.LineareAlgebra;

public class Steuerungsverhalten {
	public Vektor2D acceleration;
	private Random zuf = ThreadLocalRandom.current();

	public Steuerungsverhalten() {
		acceleration = new Vektor2D(0, 0);
	}

	public void resetAcceleration() {
		acceleration.mult(0);
	}

	public void applyForce(Vektor2D force) {
		Vektor2D forceHelp = new Vektor2D(force);
		acceleration.add(forceHelp);
	}

	public Vektor2D randomForce() {
		return new Vektor2D(zuf.nextFloat()*10-5, zuf.nextFloat()*10-5);
	}

	public Vektor2D mousePosition() {
		return new Vektor2D(Mouse.getX(), Display.getDisplayMode().getHeight() - Mouse.getY());
	}
	
	public Vektor2D forceMousePosition(Vektor2D currentPosition) {
		Vektor2D mousePosition = mousePosition();
		mousePosition.sub(currentPosition);
		mousePosition.normalize();
		return mousePosition;
	}
	
	public Vektor2D forceSeek(Vektor2D currentPosition, Vektor2D currentVelocity, Vektor2D zielPosition) {
		Vektor2D zielRichtung = LineareAlgebra.sub(zielPosition, currentPosition);
		Vektor2D zielKraft    = LineareAlgebra.sub(zielRichtung, currentVelocity);
		
		zielKraft.normalize();
		return zielKraft;
	}

	public Vektor2D separation(Agent me, double dist) {
		Vektor2D steeringForce = new Vektor2D(0, 0);
		for (int i = 0; i < me.objektManager.getAgentSize(); i++) {
			if (me.id == i)
				continue;

			BasisObjekt bObj = me.objektManager.getAgent(i);
			if (bObj instanceof Agent) {
				Agent bObjF = (Agent)bObj;
				if (LineareAlgebra.euklDistanz(me.position, bObjF.position) < dist)
					steeringForce.add(LineareAlgebra.sub(me.position, bObjF.position));
			}
		}
		LineareAlgebra.normalize(steeringForce);
		return steeringForce;
	}

	public Vektor2D alignment(Agent me, double dist) {
		Vektor2D steeringForce = new Vektor2D(0, 0);
		for (int i = 0; i < me.objektManager.getAgentSize(); i++) {
			if (me.id == i)
				continue;

			BasisObjekt bObj = me.objektManager.getAgent(i);
			if (bObj instanceof Agent) {
				Agent bObjF = (Agent)bObj;
				if (LineareAlgebra.euklDistanz(me.position, bObjF.position) < dist)
					steeringForce.add(bObjF.velocity);
			}
		}

		LineareAlgebra.normalize(steeringForce);
		return steeringForce;
	}

	public Vektor2D cohesion(Agent me, double dist) {
		Vektor2D steeringForce = new Vektor2D(0, 0);
		for (int i = 0; i < me.objektManager.getAgentSize(); i++) {
			if (me.id == i)
				continue;

			BasisObjekt bObj = me.objektManager.getAgent(i);
			if (bObj instanceof Agent) {
				Agent bObjF = (Agent)bObj;
				if (LineareAlgebra.euklDistanz(me.position, bObjF.position) < dist)
					steeringForce.add(LineareAlgebra.sub(bObjF.position, me.position));
			}
		}

		LineareAlgebra.normalize(steeringForce);
		return steeringForce;
	}
	
	public Vektor2D kornhesion(Agent me, double dist) {
		Vektor2D steeringForce = new Vektor2D(0, 0);
		
		// schaut, welches korn am nächsten ist
		double minDist = Double.MAX_VALUE;
		int closestKorn = -1;
		for (int i = 0; i < me.kornManager.getKoernerCount(); i++) {
			BasisObjekt bObj = me.kornManager.getKorn(i);
			if (bObj instanceof Korn) {
				Korn bObjF = (Korn)bObj;
				if (LineareAlgebra.euklDistanz(me.position, bObjF.position) < minDist) {
					minDist = LineareAlgebra.euklDistanz(me.position, bObjF.position);
					closestKorn = i;
				}
			}
		}
		
		// wenn es ein naechstes Korn gibt, wird dieses verfolgt/ gegessen
		if (closestKorn != -1) {
			BasisObjekt bObj = me.kornManager.getKorn(closestKorn);
			
			if (bObj instanceof Korn) {
				Korn bObjF = (Korn)bObj;
				
				// wenn ein Korn nah genug ist, wird es gegessen
				if (LineareAlgebra.euklDistanz(me.position, bObjF.position) < config.SCHNABELWEITE) {
					me.kornManager.removeKorn(closestKorn);
				}
				
				// ob ein korn gesehen wird
				if (LineareAlgebra.euklDistanz(me.position, bObjF.position) < dist){
					steeringForce.add(LineareAlgebra.sub(bObjF.position, me.position));
				}
			}
		}
		
		/*
		for (int i = 0; i < me.kornManager.getKoernerCount(); i++) {
			BasisObjekt bObj = me.kornManager.getKorn(i);
			
			if (bObj instanceof Korn) {
				Korn bObjF = (Korn)bObj;
				
				// wenn ein Korn nah genug ist, wird es gegessen
				if (LineareAlgebra.euklDistanz(me.position, bObjF.position) < config.SCHNABELWEITE) {
					me.kornManager.removeKorn(i);
					continue;
				}
				
				// ob ein korn gesehen wird
				if (LineareAlgebra.euklDistanz(me.position, bObjF.position) < dist){
					steeringForce.add(LineareAlgebra.sub(bObjF.position, me.position));
					break;
				}
			}
		}*/

		LineareAlgebra.normalize(steeringForce);
		return steeringForce;
	}
}
