# PixelScaler

Test of pixel perfect upsizing a low resolution screen for a retro theme.

The idea is to render to a low resolution FBO (e.g. 640x480) and then render that full-screen.
Then add some vignette and a CRT shader. (perhaps add some bloom?)

Added loading of GLTF models and converting the model to a wireframe model.
(Note: wireframe of triangles, not quads like you may see in Blender).
An idea is to include normals per vertex to hide lines belonging to back faces. This would require some logic
in the shader (back face culling doesn't apply to GL_LINES).

bug:teavm version has very blurry text (scaling?)

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.
