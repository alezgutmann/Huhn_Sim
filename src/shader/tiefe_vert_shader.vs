#version 130
uniform int mouseDown;
uniform float time;
varying vec4 color;

void main() {
           // anpassen auf Orthografische Projektion deswegen y wert statt z wert genommen
		   float zVal  = 1.0f - (gl_ModelViewProjectionMatrix * gl_Vertex).y*0.3f;
		   color       = vec4(zVal,0.3 * zVal, 0, 0.3);
		   gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
		   if (mouseDown == 1){
		       if (sin(time) > 0){
		         gl_Position *= 100 * sin(time);
		       }
		   }
		};