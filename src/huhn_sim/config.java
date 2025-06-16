package huhn_sim;

import libs.Vektor2D;
import libs.Vektor3D;

public class config {
	public static boolean debug = false;
	
	public static String TITLE = "Hühner-Simulation";
	public static int HEIGHT = 720;
	public static int WIDTH = 1260;
	public static boolean loadFromOBJFile = true;
	public static String fragShaderPath = "src/shader/staub_frag_shader.fs";
	public static double rainbowMultiplier = 9.0; // um welchen Faktor die Hühner im Rainbow mode schneller sein sollen 
	public static String OBJFilePath = "resources/huhn_weiss.obj";
	public static Vektor3D OBJRotation = new Vektor3D(90,0,90); // um wie viel grad pro achse gedreht werden soll
	public static boolean renderFromQUADS = false; //funktioniert momentan noch nicht naja egal
	public static double HühnerSize = 3.0;
	public static double zOFFSET = 0; //wie tief huhn im boden "steckt" bzw heraus kommt
	public static int AGENTEN_ANZAHL = 20;
	public static int AGENTEN_MASS = 1;
	public static int AGENTEN_MAX_SPEED = 100;
	public static int AGENTEN_MAX_TURN_RATE = 15;
	public static int AGENTEN_SWARM_DISTANZ = 150;
	public static Vektor2D HOME_POSITION = new Vektor2D(WIDTH/2,HEIGHT/2); // alle hühner versuchen zu dieser HomePosition zurückzukehren
	public static double MAUS_FORCE = 0;
	public static double SEPERATION_FORCE = 5;
	public static double ALIGNMENT_FORCE = 2;
	public static double COHESION_FORCE = 1;
	public static double KOERNER_FORCE = 3; // soll später die FORCE die die hühner zu körnern verspüren darstellen
	public static double HOMING_FORCE = 0.1;
	public static double SCHWARZES_SCHAF_FORCE = 100;
	public static double SICHTWEITE = 100; // ab wann die hühner die körner sehen
	public static double SCHNABELWEITE = 20; // distanz die ein huhn zum korn haben muss um es zu schnabulieren
	public static double KORN_SPREAD = 50; // wie weit die körner "gestreut" werden
}
