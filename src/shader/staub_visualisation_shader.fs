#version 130

uniform sampler2D tex1;

void main (void) {
	//gl_FragColor = texture2D(tex1, gl_TexCoord[0].st) * vec4(1.0, 1.0, 0.0, 1.0) * 5.0;
	gl_FragColor = vec4(1.0, 1.0, 0, 1);
}