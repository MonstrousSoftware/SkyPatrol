// shader for rendering wire frames (GL_LINES) with back face culling
//

// attributes of this vertex
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;


uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform mat3 u_normalMatrix;
uniform vec4 u_cameraPosition;

varying vec4 v_color;
varying vec3 v_normal;
varying vec3 v_view;

void main() {
	vec4 worldPos = u_worldTrans * a_position;

	// normal vector of vertex in world space
	v_normal = normalize(u_normalMatrix * a_normal);

	v_color = a_color;

	// view vector from camera to this vertex
	v_view = normalize(u_cameraPosition.xyz - worldPos.xyz);

   	gl_Position = u_projViewTrans * worldPos;
}
