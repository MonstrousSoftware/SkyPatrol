// Gaussian blur
// ThinMatrix OpenGL 3D Game Tutorial 44: Gaussian Blur
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

uniform vec2 u_resolution;
uniform sampler2D u_texture;

varying vec2 v_blurTexCoords[11];

void main()
{
	vec4 color = vec4(vec3(0.0), 1.0);
    color += texture2D(u_texture, v_blurTexCoords[0]) * 0.0093;
    color += texture2D(u_texture, v_blurTexCoords[1]) * 0.028002;
    color += texture2D(u_texture, v_blurTexCoords[2]) * 0.065984;
    color += texture2D(u_texture, v_blurTexCoords[3]) * 0.121703;
    color += texture2D(u_texture, v_blurTexCoords[4]) * 0.175713;
    color += texture2D(u_texture, v_blurTexCoords[5]) * 0.198596;
    color += texture2D(u_texture, v_blurTexCoords[6]) * 0.175713;
    color += texture2D(u_texture, v_blurTexCoords[7]) * 0.121703;
    color += texture2D(u_texture, v_blurTexCoords[8]) * 0.065984;
    color += texture2D(u_texture, v_blurTexCoords[9]) * 0.028002;
    color += texture2D(u_texture, v_blurTexCoords[10]) * 0.0093;

    color.a = 1.0;

    gl_FragColor = color;
}
