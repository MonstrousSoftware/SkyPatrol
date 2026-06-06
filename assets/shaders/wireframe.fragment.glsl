#ifdef GL_ES
precision mediump float;
#endif


varying vec4 v_color;
varying vec3 v_normal;
varying vec3 v_view;

void main() {

    // discard back facing fragments
    if( dot(v_normal, v_view) < 0)
        discard;

    gl_FragColor = v_color;
}
