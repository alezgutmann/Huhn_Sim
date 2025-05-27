#version 130
uniform int mouseDown;
uniform float time;

vec3 hsv2rgb_converter(vec3 c) {
    vec4 K = vec4(1.0, 2.0/3.0, 1.0/3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
	if ( mouseDown == 1){
		//Hühnerfarbe im Psycho Modus
		float hue = time * 0.4;
        vec3 color = hsv2rgb_converter(vec3(hue, 1.0, 1.0));
        gl_FragColor = vec4(color, 1.0);
	}
	else{
		gl_FragColor = vec4(1, 0.3, 0, 0.3); //Hühnerfarbe im Normalmodus
	}
	
}