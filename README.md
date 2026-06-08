# PixelScaler

Test of pixel perfect upsizing a low resolution screen for a retro theme.

The idea is to render to a low resolution FBO (e.g. 320x240) and then render that full-screen.
Then add some vignette and a CRT shader. (perhaps add some bloom?).

(For context, ZX Spectrum screen resolution was 256x192).

Added loading of GLTF models and converting the model to a wireframe model.
(Note: wireframe of triangles, not quads like you may see in Blender).

An idea is to include normals per vertex to hide lines belonging to back faces. This would require some logic
in the shader (back face culling doesn't apply to GL_LINES).
The wire frame shader does exactly that. It uses the normal vector per vertex, transforms it
to world space.  In the fragment shader we take the dot product of the normal vector
and the view vector and discard any that are back facing (dot product less than zero).

To add enemies shooting back.
Multiple levels (after you've eliminated all enemies).
Mountains on the horizon?


Press CONTROL plus LEFT or RIGHT to strafe instead of turn.



## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.
