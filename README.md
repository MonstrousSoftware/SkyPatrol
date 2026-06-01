# PixelScaler

Test of pixel perfect upsizing a low resolution screen for a retro theme.

The idea is to render to a low resolution FBO (e.g. 640x480) and then render that full-screen.
Then maybe add some bloom and a CRT shader.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.
