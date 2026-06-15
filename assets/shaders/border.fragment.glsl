// border patterns based on ZX Spectrum loading

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


uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform float u_time;
uniform sampler2D u_pattern;
//uniform int u_pattern[128];
//uniform vec3 u_colors[3];

varying vec4 v_color;
varying vec2 v_texCoord0;

float borderWidth = 0.05;
float borderHeight = 0.05;


void main()
{
    vec2 uv = v_texCoord0.xy;
    // scale to fit the original texture inside the borders
    vec3 color = texture2D(u_texture, vec2(-0.06, -0.06)+uv*1.2).rgb;

    // is this pixel in the border?
    if(uv.y < borderHeight || uv.y > 1.0-borderHeight || uv.x < borderWidth || uv.x > 1.0-borderWidth){


        //color = vec3(1, 0, 0);
        color = texture2D(u_pattern, uv).rgb;

//        int line = int(uv.y*128.0);
//        //int b = int(mod(line, 3.0));
//        int b = u_pattern[line];
//        color = u_colors[b];  // black, yellow or blue
        //color = vec3(1-b, 1-b, b);
    }

    gl_FragColor = vec4(color, 1.0);
}
