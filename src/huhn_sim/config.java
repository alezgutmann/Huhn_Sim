package huhn_sim;

public class config {
	public static String TITLE = "Hühner-Simulation";
	public static int HEIGHT = 720;
	public static int WIDTH = 1260;
	public static boolean loadFromOBJFile = true;
	public static String fragShaderPath = "src/shader/staub_frag_shader.fs";
	public static String OBJFilePath = "resources/maennchen.obj";
	public static double HühnerSize = 3.0;
	public static int AGENTEN_ANZAHL = 20;
	public static double MAUS_FORCE = 100;
	public static double SEPERATION_FORCE = 0.8;
	public static double ALIGNMENT_FORCE = 0.05;
	public static double COHESION_FORCE = 0.5;
	public static double KOERNER_FORCE = 1; //soll später die FORCE die die hühner zu körnern verspüren darstellen
}
