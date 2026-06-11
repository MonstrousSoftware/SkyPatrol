# PixelScaler

Test of pixel perfect upsizing a low resolution screen for a retro theme.

The idea is to render to a low resolution FBO (e.g. 320x240) and then render that full-screen.
Then add some vignette and a CRT shader. (perhaps add some bloom?).

(For context, ZX Spectrum screen resolution was 256x192).

Added loading of GLTF models and converting the model to a wireframe model.

In the conversion we remove lines segments that appear multiple times to hide internal seams.
E.g. a quad is shows with four lines, not as two triangles because the diagonal line appears twice.

We add normals per vertex to hide lines belonging to back faces. There is some logic
in the fragment shader to hide lines of back faces (back face culling doesn't apply to GL_LINES).
The wire frame shader does exactly that. It uses the normal vector per vertex, transforms it
to world space.  In the fragment shader we take the dot product of the normal vector
and the view vector and discard any that are back facing (dot product less than zero).




Mountains on the horizon?


Press CONTROL plus LEFT or RIGHT to strafe instead of turn.



Music: 
    "Game" by Elija_K via Free Music Archive (CC BY)

Font:
    The font file in this archive was created using Fontstruct the free, online
    font-building tool.
    This font was created by “Mark sensen”.
    This font has a homepage where this archive and other versions may be found:
    https://fontstruct.com/fontstructions/show/1662490

    Try Fontstruct at https://fontstruct.com
    It’s easy and it’s fun.

    Fontstruct is copyright ©2019 Rob Meek

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.
