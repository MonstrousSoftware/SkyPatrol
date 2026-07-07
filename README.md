# SkyPatrol

Entry for LibGDX game jame #37 (June 2026)

Theme: 8 bit game (one life only).

![image](screenshot3.png)

This started as a test of pixel perfect upsizing a low resolution screen for a retro theme and developed into a vector graphics game, a little in the style of [Battlezone](https://en.wikipedia.org/wiki/Battlezone_(1980_video_game)) although that arcade game 
relied on special vectorization hardware instead of rasterization.

The idea is to render to a low resolution FBO (e.g. 320x240) and then render that full-screen.
Then add some vignette and a CRT shader. (For context, ZX Spectrum screen resolution was 256x192).

## Coping with low resolution
Once you have a wire frame renderer in low resolution, you realise how low the resolution really is and how easy the screen gets cluttered making the visuals hard to interpret, especially at distance.

I load the models from a GLTF file using gdx-gltf and convert each model to a wireframe model using MeshBuilder.

The models would be easier to understand if they could be simplified. 

For one thing the back faces should be culled, to show only the front faces.  We cannot use OpenGL culling for this because we are not rendering triangles but lines (mode is GL_LINES).  
But we can do a similar thing in fragment shader code to hide lines belonging to back faces: if the normal points away from the camera position, discard the fragment.  It uses the normal vector per vertex, transforms it
to world space.  In the fragment shader we take the dot product of the normal vector and the view vector (i.e. towards the camera) and discard any that are back facing (dot product less than zero).

Another mesh simplification is to merge triangles into quads or higher-level polygons if they are adjacent and lie in the same plane.
In the conversion we remove lines segments that appear multiple times in order to hide internal seams.
E.g. a quad is shows with four lines, not as two triangles because the diagonal line appears twice.
This simplifies the wire frame and makes the visuals easier to "read".

It also means the scene needs to stay simple. That's why we use a simple flat grid as ground surface instead of using some kind of height map.

## Pixel scaling
To get accurate pixel scaling the frame needs to be scaled by an integer amount, e.g. 2x, 3x or 4x.
Texture Filtering (magnification) should be nearest neighbour rather than linear interpolation to preserve the blockiness of the pixels.
The idea is that the scene is rendered to a low-resolution frame buffer (e.g. 320x240) which is then scaled up to fit the canvas (with possibly some border remaining).
In other words, the FBO texture is scaled by some integer factor and centred on the actual screen.
It means in the render loop the batches (ModelBatch, SpriteBatch, etc.) need to be configured with the 320x240 viewport.
It also means the font size needs to be minimal (8x8 pixels per character).
(An alternative is to render at screen size but capture this in a low-resolution frame buffer which will reduce the solution by minification filtering. But this seems worse in terms of controlling pixel perfect scaling).

Post processing effects (TV effect, vignette) should be done on full screen resolution. E.g. TV raster lines should extend to the borders.
This means e.g. the 320x240 fbo is rendered to a full screen fbo (e.g. 1280x1200) and then forwarded to postprocessing.

## Chip tunes
Could not find any project to play a chip tune other than playing a chiptune mp3 file.
It would be much more disk space efficient if we could play some kind of tracker file and synthesize the sound. 
Audio differs per platform.  On desktop you can synthesize a PCM waveform using an AudioDevice. So, you can create a sine wave or square wave in code. On web (teavm) however, AudioDevice is not supported; you can only play sounds and music from a sound file not from a float buffer.
In the end, I am simply playing music from an mp3 file. Not even a real chip tune but something that is a bit easier on the ears.

## Loading Effect
On startup the game will simulate loading the game from tape inspired by how it was done on the ZX Spectrum with coloured stripes in the border.
The sound effect actually contains data, and the code actually decodes the sound, but the data is just dummy data.  It would take far too long to really load the opening screen or anything useful from the audio file (the ZX Spectrum would load about 200 bytes per second). 
For the stripy border effect, I originally had a shader and I would pass an array of colour codes to the shader to set the border colour depending on pixel height.  However, when running this on the web version, this runs into a shader compile error.  Annoyingly, in WebGL, an array can only be indexed by a constant.  
So instead, I now do the effect without any shader, just with an ordinary sprite batch.  I use one texture for the border and a smaller texture for the screen inside the border.  The border texture is continually updated during the loading effect to show stripes.  We use a border texture that is only one pixel wide and then stretch it across the screen.

## One life
Playing with one life only can be quite brutal because sometimes the bad guys get a lucky shot and it feels unfair.
But we had to have it because it was the theme of the game jam.

There is however an option in the game to have some level of health instead. Press 8 in the game to use a health indicator (20% damage per hit, restored to 100% at each new level).
If you use this mode, your score in the hi score table won't appear with the "one life only" asterisk.
(revealing you to all who play this single player game as a cheater).

Play it here:
    https://monstrous-software.itch.io/sky-patrol

Music: 
    "Game" by Elija_K via Free Music Archive (CC BY)

Font:
    The font file in this archive was created using Fontstruct the free, online
    font-building tool.
    This font was created by “Mark sensen”.
    This font has a homepage where this archive and other versions may be found:
    https://fontstruct.com/fontstructions/show/1662490

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.


## Post game jam
The version that was submitted to the game jam is that of June 17, which you can find in the commit history.

After the jam, I used this code as a platform to try some code refactoring.

In particular, I was wondering if an ECS type approach would be a good fit for this game.  It is really not necessary for performance as the game is
very simple, but it bugs me a little to have game object type specific code in World.

To compare performance I added a Frame Rate gadget to show FPS in-game.
It is activated with key 9.

To run a performance test, you can change the start level in GameScreen.show().  This will increase the number of enemies (tanks and jets).
You also have to set the boolean `invincible` to true for testing to avoid getting killed.

Here are the results of a first basic implementation (v1) of an ECS system which relies heavily on HashMaps to store Components using an Entity identifier as key.

| level | frame rate (fps) | using ECS approach v1 |
|-------|------------------|--------------------|
| 0     | 2700             | 2600               |
| 20    | 800              | 980                |
| 40    | 460              | 600                |
| 60    | 300              | 400                |
| 100 | 200              | 231                |
| 200   | 95               | 117                |
| 500   | 48               | 51                 |
| 1000  | 26               | 30                 |



## Updated ECS

I reworked the ECS system, removing the HashMaps and taking some ideas from Artemis-odb.

After some consideration, I completely removed the Entity class. Entities only exist as an integer number starting from zero.  When entities
are destroyed their identifier can be reused for a new entity. This helps to keep the number values as
low as possible since we use their value directly as array index in the component mappers.

The class Component is an abstract class without contents from which to subclass the application specific component classes.

The System concept is captured in the class EntitySystem (to avoid a name clash with the Java System class).
Each EntitySystem defines a list of required components and based on this maintains a list of related
entities.  Whenever an entity is created or destroyed the list is updated.

For each type of Component there is a ComponentMapper that is used to find the Component for an Entity.
There are two ways to define a component: addComponent or createComponent.  The latter uses a pool of 
destroyed components for reuse and is recommended for components of short-lived entities because it reduces allocation and garbage collection. 

The main class for the ECS is Engine. Whenever Engine#update() is called, all EntitySystems which are marked for autoUpdate are updated.
This means update is called for each entity that is related to the EntitySystem.

## Frustum culling
An important speedup takes place in the RenderSystem with the following code: 
```            
    if(camera.frustum.sphereInFrustum(pos, renderComponent.radius))
       instances.add(renderComponent.modelInstance);
```
This increase the frame rate by about a factor 4 as it only puts items in the render list that are visible from the camera.


| level  | frame rate (fps) | using ECS approach v1 | using ECS approach v2 | 
|--------|------------------|--------------------|-----------------------|
| 0      | 2700             | 2600               | 2660                  |
| 20     | 800              | 980                | 2200                  |
| 40     | 460              | 600                | 1600                  |
| 60     | 300              | 400                | 1200                  |
| 100    | 200              | 231                | 900                   |
| 200    | 95               | 117                | 480                   |
| 500    | 48               | 51                 | 320                   |
| 1000   | 26               | 30                 | 170                  |


