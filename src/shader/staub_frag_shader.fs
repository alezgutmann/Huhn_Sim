#version 130
uniform int mouseDown;
uniform float time;
varying vec4 color;

void main() {
	if (mouseDown == 1){
		float pi = 3.1415;
        float r = sin(time) * 0.5 + 0.5;
        float g = sin(time - 2.0 * pi / 3.0) * 0.5 + 0.5;
        float b = sin(time - 4.0 * pi / 3.0) * 0.5 + 0.5;
        vec3 regenbogenfarbe = vec3(r, g, b);
        gl_FragColor = vec4(regenbogenfarbe, 1.0);
        
	}
	else{
		gl_FragColor = color;
		//vec4(1, 0.3, 0, 0.3); //hühnerfarbe im normalmodus
	}
	
}