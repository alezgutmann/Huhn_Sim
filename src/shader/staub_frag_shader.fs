#version 130

uniform sampler2D tex2;
uniform vec2 s;

void main() {
	//gl_FragColor = (texture2D(tex2, gl_TexCoord[0].st + vec2( s.x, 0)) +texture2D(tex2, gl_TexCoord[0].st + vec2(-s.x, 0)) +texture2D(tex2, gl_TexCoord[0].st + vec2( 0, s.y)) +texture2D(tex2, gl_TexCoord[0].st + vec2( 0, -s.y)) +texture2D(tex2, gl_TexCoord[0].st)) * 0.2 * 0.992;
	gl_FragColor = vec4(1.0, 1.0, 0, 1);
}