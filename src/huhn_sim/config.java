package huhn_sim;
public class config {
	public static String TITLE = "Hühner-Simulation";
	public static int HEIGHT = 720;
	public static int WIDTH = 1260;
	public static boolean loadFromOBJFile = true;
	public static String fragShaderPath = "src/shader/staub_frag_shader.fs";
	public static double rainbowMultiplier = 9.0; // um welchen Faktor die Hühner im Rainbow mode schneller sein sollen 
	public static String OBJFilePath = "resources/maennchen.obj";
	public static boolean renderFromQUADS = false; //funktioniert momentan noch nicht naja egal
	public static double HühnerSize = 10.0;
	public static double zOFFSET = 0; //wie tief huhn im boden "steckt" bzw heraus kommt
	public static int AGENTEN_ANZAHL = 2;
	public static int AGENTEN_MASS = 1;
	public static int AGENTEN_MAX_SPEED = 100;
	public static int AGENTEN_MAX_TURN_RATE = 15;
	public static int AGENTEN_SWARM_DISTANZ = 150;
	public static double MAUS_FORCE = 0;
	public static double SEPERATION_FORCE = 5;
	public static double ALIGNMENT_FORCE = 2;
	public static double COHESION_FORCE = 0;
	public static double KOERNER_FORCE = 50; // soll später die FORCE die die hühner zu körnern verspüren darstellen
	public static double SICHTWEITE = 2000; // ab wann die hühner die körner sehen
	public static double SCHNABELWEITE = 10; // distanz die ein huhn zum korn haben muss um es zu schnabulieren
	public static double KORN_SPREAD = 50; // wie weit die körner "gestreut" werden
}
