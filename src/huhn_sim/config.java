package huhn_sim;

import libs.Vektor2D;
import libs.Vektor3D;

public class config {
	public static boolean debug = false;
	
	//FENSTER
	public static String TITLE = "Hühner-Simulation";
	public static int HEIGHT = 720;
	public static int WIDTH = 1260;
	
	//SHADER
	public static String fragShaderPath = "src/shader/regenbogen_frag_shader.fs";
	public static String vertShaderPath = "src/shader/tiefe_vert_shader.vs";
	public static double rainbowMultiplier = 20.0; // um welchen Faktor die Hühner im Rainbow mode schneller sein sollen 
	
	//MODELS
	public static boolean loadFromOBJFile = true;
	public static String OBJFilePath = "resources/huhn_weiss.obj";
	public static Vektor3D OBJRotation = new Vektor3D(90,0,90); // um wie viel grad pro achse gedreht werden soll
	public static boolean renderFromQUADS = false; //funktioniert momentan noch nicht naja egal
	public static double HühnerSize = 3.0;
	public static double zOFFSET = 0; //wie tief huhn im boden "steckt" bzw heraus kommt
	
	// PARAMETER FÜR SCHWARMSIMULATION
	public static int AGENTEN_ANZAHL = 40;
	public static int AGENTEN_MASS = 1;
	public static int AGENTEN_MAX_SPEED = 100;
	public static int AGENTEN_MAX_TURN_RATE = 25;
	public static int AGENTEN_SWARM_DISTANZ = 150;
	public static Vektor2D HOME_POSITION = new Vektor2D(WIDTH/2,HEIGHT/2); // alle hühner versuchen zu dieser HomePosition zurückzukehren
	
	// PARAMETER FÜR KRÄFTE
	public static double MAUS_FORCE = 0;
	public static double SEPERATION_FORCE = 2;//^2
	public static double ALIGNMENT_FORCE = 2;
	public static double COHESION_FORCE = 3;
	public static double KOERNER_FORCE = 3; //^2 // soll später die FORCE die die hühner zu körnern verspüren darstellen
	public static double HOMING_FORCE = 0.7;//^2
	public static double SCHWARZES_SCHAF_FORCE = 1000;
	public static double SICHTWEITE = 200; // ab wann die hühner die körner sehen
	public static double SCHNABELWEITE = 30; // distanz die ein huhn zum korn haben muss um es zu schnabulieren
	public static double KORN_SPREAD = 100; // wie weit die körner "gestreut" werden
}
