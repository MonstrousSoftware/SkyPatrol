// vignette


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
uniform float u_time;       // not used

varying vec4 v_color;
varying vec2 v_texCoord0;



void main()
{


	vec4 color = texture2D(u_texture, v_texCoord0);

    // vignette effect
    vec2 dist = v_texCoord0 * (1.0 - v_texCoord0.yx);
    float vigExtent = 45.0;
    float vig = dist.x*dist.y * vigExtent; // multiply with sth for intensity
    float vigPower = 0.45;
    vig = pow(vig, vigPower); // change pow for modifying the extent of the  vignette
    color.rgb = mix(color.rgb, color.rgb*vig, 0.9);

    // increase contrast
    color.rgb = (color.rgb - 0.5) * 1.2 + 0.5;

    gl_FragColor = color;
}
