// tv distortion
// Based on ShaderToy: Distorted TV (Fast), Created by RafaSKB in 2015-11-05
//
        //This shader a modification of ehj1's shader (www.shadertoy.com/view/ldXGW4) with a faster algorithm to generate noise.
        //Note the original version is much better than this one, but also a bit slower on old computers


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


float vertJerkOpt = 1.0;
float vertMovementOpt = 0.0;
float scanlinesOpt = 2.0;
float rgbOffsetOpt = 0.2;
float horzFuzzOpt = 1.0;
float bottomStaticOpt = 0.0;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoord0;


float fnoise(vec2 v) {
    return fract(sin(dot(v, vec2(12.9898, 78.233))) * 43758.5453) * 0.55;
}

float staticV(vec2 uv) {
    float staticHeight = fnoise(vec2(9.0,u_time*1.2+3.0))*0.3+5.0;
    float staticAmount = fnoise(vec2(1.0,u_time*1.2-6.0))*0.1+0.3;
    float staticStrength = fnoise(vec2(-9.75,u_time*0.6-3.0))*2.0+2.0;
    return (1.0-step(fnoise(vec2(5.0*pow(u_time,2.0)+pow(uv.x*7.0,1.2),pow((mod(u_time,100.0)+100.0)*uv.y*0.3+3.0,staticHeight))),staticAmount))*staticStrength;
}


void main()
{
    vec2 pix = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);

    vec2 uv = v_texCoord0.xy;

    float fuzzOffset = fnoise(vec2(u_time*15.0,uv.y*80.0))*pix.x*2.0;
    float largeFuzzOffset = fnoise(vec2(u_time*1.0,uv.y*25.0))*pix.x*10.0;
    float xOffset = (fuzzOffset + largeFuzzOffset) * horzFuzzOpt;

    float vertMovementOn = (1.0-step(fnoise(vec2(u_time*0.2,8.0)),0.4))*vertMovementOpt;
    float vertJerk = (1.0-step(fnoise(vec2(u_time*1.5,5.0)),0.6))*vertJerkOpt;
    float vertJerk2 = (1.0-step(fnoise(vec2(u_time*5.5,5.0)),0.2))*vertJerkOpt;
    float yOffset = abs(sin(u_time)*4.0)*vertMovementOn+vertJerk*vertJerk2*0.3;
    float y = mod(uv.y+yOffset,1.0);

    float staticVal = 0.0;

    float red 	=   texture2D(	u_texture, 	vec2(uv.x + xOffset -pix.x*rgbOffsetOpt,y)).r+staticVal;
    float green = 	texture2D(	u_texture, 	vec2(uv.x + xOffset,	  y)).g+staticVal;
    float blue 	=	texture2D(	u_texture, 	vec2(uv.x + xOffset +pix.x*rgbOffsetOpt,y)).b+staticVal;

    vec3 color = vec3(red,green,blue);

    float scanline = sin(uv.y*u_resolution.y/2.0)*0.002*scanlinesOpt;
    color -= scanline;

    // vignette effect
    vec2 dist = v_texCoord0 * (1.0 - v_texCoord0.yx);
    float vigExtent = 45.0;
    float vig = dist.x*dist.y * vigExtent; // multiply with sth for intensity
    float vigPower = 0.15;
    vig = pow(vig, vigPower); // change pow for modifying the extent of the  vignette
    color.rgb = mix(color.rgb, color.rgb*vig, 0.9);

    gl_FragColor = vec4(color, 1.0);
}
