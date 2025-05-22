package huhn_sim;

import libs.Model;
import libs.POGL;
import libs.Vektor2D;

public class Agent extends BewegendesObjekt {
	private static int objCounter = 0;
	public ObjektManager objektManager;

	public Agent(Vektor2D position, Vektor2D velocity, int radius, float r, float g, float b) {
		super(position, velocity);
		this.id = ++objCounter;

		setMass(config.AGENTEN_MASS);
		setMaxSpeed(config.AGENTEN_MAX_SPEED);
		setMaxTurnRate(config.AGENTEN_MAX_TURN_RATE);
		setSwarmDistanz(config.AGENTEN_SWARM_DISTANZ);

		setWegHistorie(new Weg2DDynamisch(20));
	}

	public void setObjektManager(ObjektManager objektManager) {
		this.objektManager = objektManager;
	}

	
	public void render(Model object) {
		POGL.renderSwarmObjectWithForces((float) position.x, (float) position.y, 10, velocity, getLastAcceleration(), object);
	}
	
	@Override
	public void render() {
		POGL.renderSwarmObjectWithForces((float) position.x, (float) position.y, 10, velocity, getLastAcceleration());
	}
}
