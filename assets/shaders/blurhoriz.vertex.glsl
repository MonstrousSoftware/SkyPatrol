// attributes of this vertex
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

attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform vec2 u_resolution;
uniform mat4 u_projTrans;


varying vec2 v_blurTexCoords[11];

void main() {

	gl_Position = u_projTrans * vec4(a_position, 1.0);
	float pixelSize = 1.0 / u_resolution.x;
    float offset = -5.0;
	for(int i = -5; i <= 5; i++) {
		v_blurTexCoords[i+5] = 	a_texCoord0 + vec2(pixelSize*offset, 0.0);
        offset += 1.0;
	}

}
