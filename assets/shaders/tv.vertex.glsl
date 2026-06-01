#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

// attributes of this vertex
attribute vec3 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_colour;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoord0;

void main() {
	v_color = a_colour;
	v_texCoord0 = a_texCoord0;
	gl_Position = u_projTrans * vec4(a_position, 1.0);
}
