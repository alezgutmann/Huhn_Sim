package huhn_sim;

import libs.LineareAlgebra;
import libs.Vektor2D;

public class VerhaltenAgent implements Verhalten {
	private Agent agent;
	private Steuerungsverhalten steering;

	public VerhaltenAgent(Agent agent) {
		this.agent = agent;
		this.steering = new Steuerungsverhalten();
	}

	@Override
	public void update(double time) {
		Vektor2D mausForce = steering.forceMousePosition(agent.position);
		mausForce.mult(config.MAUS_FORCE);
		agent.applyForce(mausForce);
		
		Vektor2D separationForce = steering.separation(agent, agent.SWARM_DISTANZ);
		separationForce.mult(config.SEPERATION_FORCE);
		agent.applyForce(separationForce);
		
		Vektor2D alignmentForce = steering.alignment(agent, agent.SWARM_DISTANZ);
		alignmentForce.mult(config.ALIGNMENT_FORCE);
		agent.applyForce(alignmentForce);
		
		Vektor2D cohesionForce = steering.cohesion(agent, agent.SWARM_DISTANZ);
		cohesionForce.mult(config.COHESION_FORCE);
		agent.applyForce(cohesionForce);
		
		Vektor2D kornForce = steering.kornhesion(agent, config.SICHTWEITE);
		kornForce.mult(config.KOERNER_FORCE);
		agent.applyForce(kornForce);
		
		double homingMultiplier; // je weiter die hühner vom home entfernt sind, desto stärker zieht es sie wieder dahin
		homingMultiplier = LineareAlgebra.euklDistanz(agent.getPosition(), config.HOME_POSITION);
		Vektor2D zuhauseForce = steering.forceSeek(agent.getPosition(), agent.getVelocity(), config.HOME_POSITION);
		zuhauseForce.mult(config.HOMING_FORCE * homingMultiplier);
		agent.applyForce(zuhauseForce);
		
		if (agent.isSchwarzesSchaf == true) {
			Vektor2D randomForce = steering.randomForce();
			randomForce.mult(config.SCHWARZES_SCHAF_FORCE);
			agent.applyForce(randomForce);
		}
		
		// Rotationsrate einbeziehen!
		Vektor2D wunschVelocity = LineareAlgebra.add(agent.getVelocity(), LineareAlgebra.mult(agent.getAccelerationInRespectToMass(), time));
		if (agent.getVelocity().length()!=0 && wunschVelocity.length()!=0) {
			double kosinusFormel = LineareAlgebra.kosinusFormel(wunschVelocity, agent.getVelocity());
			double acos 		 = Math.acos(Math.min(1, kosinusFormel));
			double winkel 		 = LineareAlgebra.radToDegree(acos);
		
			if (winkel < agent.getMaxTurnRate()) {
				agent.setVelocity(LineareAlgebra.truncate(wunschVelocity, agent.getMaxSpeed()));
			} else {
				// mathematisch positiv
				if (LineareAlgebra.sinusFormel(wunschVelocity, agent.getVelocity()) > 0) {
					agent.setVelocity(LineareAlgebra.rotate(agent.getVelocity(),-agent.getMaxTurnRate()));
				} else { // mathematisch negativ
					agent.setVelocity(LineareAlgebra.rotate(agent.getVelocity(), agent.getMaxTurnRate()));
				}
				agent.setVelocity(LineareAlgebra.normalize(agent.getVelocity()));
				agent.setVelocity(LineareAlgebra.mult(agent.getVelocity(), wunschVelocity.length()));
				agent.setVelocity(LineareAlgebra.truncate(agent.getVelocity(), agent.getMaxSpeed()));
			}
		} else 
			agent.setVelocity(LineareAlgebra.truncate(wunschVelocity, agent.getMaxSpeed()));	
		
		agent.setPosition(LineareAlgebra.add(agent.getPosition(), LineareAlgebra.mult(agent.getVelocity(), time)));
		agent.getWegHistorie().addWaypoint(agent.getPosition());
		
		agent.resetAcceleration();
		steering.resetAcceleration();
	}
}
