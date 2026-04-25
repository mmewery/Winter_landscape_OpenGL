# Winter Landscape — Implementation Checklist

> **Project**: Interactive 3D Winter Landscape (Java, OpenGL fixed pipeline)
> **Package**: `winterlandscape` (app classes) + `winterlandscape.global` (framework)
> **Entry point**: `winterlandscape.App` → creates `Renderer` inside `LwjglWindow`

---

## Phase 1: Renderer Foundation (Renderer.java)

Get a working window with a camera before building any scene entities.

### 1.1 Basic Init & Display Loop
- [ ] In `init()`: call `super.init()`
- [ ] In `init()`: enable depth test → `glEnable(GL_DEPTH_TEST)`
- [ ] In `init()`: set clear color to dark winter blue → `glClearColor(0.05f, 0.05f, 0.15f, 1f)`
- [ ] In `init()`: enable smooth shading → `glShadeModel(GL_SMOOTH)`
- [ ] In `display()`: clear both buffers → `glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)`
- [ ] In `display()`: set viewport → `glViewport(0, 0, width, height)`
- [ ] **Run the app** — verify a dark blue window opens without errors

### 1.2 Projection Matrix
- [ ] In `display()`: switch to projection matrix → `glMatrixMode(GL_PROJECTION)` then `glLoadIdentity()`
- [ ] Call `GluUtils.gluPerspective(60, (double) width / height, 0.1, 500)` for perspective projection
- [ ] Switch back to modelview → `glMatrixMode(GL_MODELVIEW)` then `glLoadIdentity()`

### 1.3 First-Person Camera (GLCamera)
- [ ] Declare field: `private GLCamera camera`
- [ ] In `init()`: create camera → `camera = new GLCamera()`
- [ ] Set camera to first-person → `camera.setFirstPerson(true)`
- [ ] Set initial position → `camera.setPosition(new Vec3D(0, 5, 20))`
- [ ] Set initial radius → `camera.setRadius(1)`
- [ ] In `display()` after `glLoadIdentity()`: call `camera.setMatrix()` to apply view transform

### 1.4 Mouse Look (Camera Rotation)
- [ ] Declare fields: `private boolean mousePressed = false`, `private double lastMouseX, lastMouseY`
- [ ] Override `glfwMouseButtonCallback`: on LMB press set `mousePressed = true` and record cursor position; on LMB release set `mousePressed = false`
- [ ] Override `glfwCursorPosCallback`: if `mousePressed`, compute dx/dy from last position, call `camera.addAzimuth(dx * 0.01)` and `camera.addZenith(dy * 0.01)`, update last position

### 1.5 Keyboard Movement (WASD)
- [ ] Declare a key state map or individual booleans for W/A/S/D keys
- [ ] Override `glfwKeyCallback`:
  - [ ] On `GLFW_KEY_ESCAPE` release → close window
  - [ ] Track press/release state of W, A, S, D, and other control keys
- [ ] In `display()` (each frame): check key states and call:
  - [ ] W → `camera.forward(speed)` (e.g., speed = 0.5)
  - [ ] S → `camera.backward(speed)`
  - [ ] A → `camera.left(speed)`
  - [ ] D → `camera.right(speed)`
- [ ] **Run & test**: verify you can look around with LMB drag and move with WASD

### 1.6 Scroll Zoom
- [ ] Override `glfwScrollCallback`: call `camera.addRadius(dy * -0.5)` or `camera.forward(dy * 2)`

---

## Phase 2: Terrain (TerrainPlane.java)

### 2.1 Load Snow Texture
- [ ] In `init()`: load texture inside try/catch → `snowTexture = new OGLTexture2D("textures/snow_positive_y.jpg")`
- [ ] Print error to `System.err` on `IOException`

### 2.2 Render Textured Ground Quad
- [ ] In `render()`: enable texturing → `glEnable(GL_TEXTURE_2D)`
- [ ] Bind snow texture → `snowTexture.bind()`
- [ ] Set white material color → `glColor3f(1, 1, 1)`
- [ ] `glBegin(GL_QUADS)`
  - [ ] Set upward normal → `glNormal3f(0, 1, 0)`
  - [ ] Vertex 1: `glTexCoord2f(0, 0); glVertex3f(-SIZE, Y, -SIZE)`
  - [ ] Vertex 2: `glTexCoord2f(TILE_COUNT, 0); glVertex3f(SIZE, Y, -SIZE)`
  - [ ] Vertex 3: `glTexCoord2f(TILE_COUNT, TILE_COUNT); glVertex3f(SIZE, Y, SIZE)`
  - [ ] Vertex 4: `glTexCoord2f(0, TILE_COUNT); glVertex3f(-SIZE, Y, SIZE)`
- [ ] `glEnd()`
- [ ] Disable texturing → `glDisable(GL_TEXTURE_2D)`

### 2.3 Wire into Renderer
- [ ] In `Renderer`: declare `private TerrainPlane terrain`
- [ ] In `Renderer.init()`: create and init → `terrain = new TerrainPlane(); terrain.init()`
- [ ] In `Renderer.display()` (after camera): call `terrain.render()`
- [ ] **Run & test**: verify you see a white/textured plane at Y=0 when looking down

---

## Phase 3: Lighting (SceneLighting.java)

### 3.1 Initialize OpenGL Lighting
- [ ] In `init()`: `glEnable(GL_LIGHTING)`
- [ ] `glEnable(GL_LIGHT0)`
- [ ] `glEnable(GL_COLOR_MATERIAL)` — allows `glColor3f` to affect material
- [ ] `glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE)`
- [ ] `glEnable(GL_NORMALIZE)` — ensures normals stay unit-length after scaling

### 3.2 Apply Light Parameters
- [ ] In `apply()`: use `glLightfv()` with `FloatBuffer` or `float[]` for:
  - [ ] `GL_POSITION` → `lightPosition`
  - [ ] `GL_AMBIENT` → `ambientColor` (scaled by `intensity`)
  - [ ] `GL_DIFFUSE` → `diffuseColor` (scaled by `intensity`)
  - [ ] `GL_SPECULAR` → `specularColor`
- [ ] Set global ambient → `glLightModelfv(GL_LIGHT_MODEL_AMBIENT, ...)` with low winter blue

### 3.3 Wire into Renderer
- [ ] In `Renderer`: declare `private SceneLighting lighting`
- [ ] In `Renderer.init()`: `lighting = new SceneLighting(); lighting.init()`
- [ ] In `Renderer.display()` (after `glLoadIdentity()` + camera): call `lighting.apply()`
  - ⚠️ Light position is transformed by current modelview matrix — call `apply()` after `camera.setMatrix()` for world-space light
- [ ] **Run & test**: the terrain should now be lit (shading visible when moving camera)

---

## Phase 4: House Model (House.java)

### 4.1 Walls
- [ ] Create `private void drawWalls()`:
  - [ ] `glPushMatrix()`
  - [ ] Translate to center of walls → `glTranslatef(0, 2.5f, 0)` (half height above ground)
  - [ ] Set wall color → `glColor3f(0.85f, 0.75f, 0.6f)` (warm wood)
  - [ ] Scale to wall dimensions → `glScalef(8, 5, 6)`
  - [ ] Draw → `glutSolidCube(1)`
  - [ ] `glPopMatrix()`

### 4.2 Roof
- [ ] Create `private void drawRoof()`:
  - [ ] `glPushMatrix()`
  - [ ] Translate above walls → `glTranslatef(0, 5f, 0)`
  - [ ] Set roof color → `glColor3f(0.6f, 0.2f, 0.15f)` (dark red/brown)
  - [ ] Build triangular prism using `GL_TRIANGLES` + `GL_QUADS`:
    - [ ] Two triangular end faces (gable ends) at z = ±3
    - [ ] Two sloped rectangular faces connecting ridge to eaves
  - [ ] `glPopMatrix()`
  - ⚠️ Set normals for each face for correct lighting!

### 4.3 Door
- [ ] Create `private void drawDoor()`:
  - [ ] `glPushMatrix()`
  - [ ] Position on front wall face → `glTranslatef(0, 1.2f, 3.01f)` (slightly in front of wall)
  - [ ] Set door color → `glColor3f(0.4f, 0.25f, 0.1f)` (dark wood)
  - [ ] Draw thin quad (e.g., 1.5 × 2.4 using `GL_QUADS`)
  - [ ] `glPopMatrix()`

### 4.4 Windows
- [ ] Create `private void drawWindow(float offsetX)`:
  - [ ] `glPushMatrix()`
  - [ ] Position on front wall → `glTranslatef(offsetX, 3f, 3.01f)`
  - [ ] Set window color → `glColor3f(0.6f, 0.8f, 1.0f)` (light blue glass)
  - [ ] Draw quad (e.g., 1.2 × 1.2)
  - [ ] Optionally draw pane dividers (thin brown lines with `GL_LINES`)
  - [ ] `glPopMatrix()`

### 4.5 Chimney (Optional)
- [ ] Create `private void drawChimney()`:
  - [ ] `glPushMatrix()`
  - [ ] Translate to roof → `glTranslatef(2f, 7f, 0)`
  - [ ] Set color → `glColor3f(0.5f, 0.3f, 0.25f)` (brick)
  - [ ] Scale and draw → `glScalef(1, 2, 1); glutSolidCube(1)`
  - [ ] `glPopMatrix()`

### 4.6 Assemble render()
- [ ] In `render()`:
  - [ ] `glEnable(GL_LIGHTING)`
  - [ ] `glDisable(GL_TEXTURE_2D)` (pure material colors for house)
  - [ ] `glPushMatrix()`
  - [ ] Call `drawWalls()`, `drawRoof()`, `drawDoor()`, `drawWindow(-2f)`, `drawWindow(2f)`, `drawChimney()`
  - [ ] `glPopMatrix()`

### 4.7 Wire into Renderer
- [ ] In `Renderer`: declare `private House house`
- [ ] In `Renderer.init()`: `house = new House()`
- [ ] In `Renderer.display()`: call `house.render()`
- [ ] **Run & test**: verify the house is visible, lit, and positioned correctly on the terrain

---

## Phase 5: Snow Particle System

### 5.1 Spawn Logic (SnowParticleSystem.java)
- [ ] Create `private void spawnParticle()`:
  - [ ] Random X in `[-spawnAreaSize, +spawnAreaSize]`
  - [ ] Y = `spawnHeight` (fixed spawn height above terrain)
  - [ ] Random Z in `[-spawnAreaSize, +spawnAreaSize]`
  - [ ] Random downward velocity: Y component `[-1.5, -0.5]`, small XZ drift
  - [ ] Random size in `[1.5, 3.5]`
  - [ ] Create `new SnowParticle(position, velocity, size)` and add to list

### 5.2 Physics Update
- [ ] In `update(float deltaTime)`:
  - [ ] Count active particles; while less than `maxParticles`, call `spawnParticle()`
  - [ ] For each particle in list:
    - [ ] If not active → skip
    - [ ] Add gravity to velocity: `p.velocity = p.velocity.add(gravity.mul(deltaTime))`
    - [ ] Add wind to velocity: `p.velocity = p.velocity.add(wind.mul(deltaTime))`
    - [ ] Update position: `p.position = p.position.add(p.velocity.mul(deltaTime))`
    - [ ] Update lifetime: `p.lifetime += deltaTime`

### 5.3 Collision Detection
- [ ] **Terrain collision**: if `p.position.getY() <= 0` → `recycleParticle(p)` or `p.active = false`
- [ ] **Roof collision** (basic AABB):
  - [ ] Define house roof bounding box (e.g., X: [-4, 4], Y: [5, 7.5], Z: [-3, 3])
  - [ ] If particle is within X/Z range AND Y drops below roof top → deactivate/recycle
- [ ] Create `private void recycleParticle(SnowParticle p)`:
  - [ ] Reset position to random spawn point above scene
  - [ ] Reset velocity to random downward
  - [ ] Reset lifetime to 0
  - [ ] Keep `active = true`

### 5.4 Rendering
- [ ] In `render()`:
  - [ ] `glDisable(GL_LIGHTING)` — particles don't need lighting
  - [ ] `glEnable(GL_POINT_SMOOTH)` — round points
  - [ ] `glEnable(GL_BLEND); glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)` — soft edges
  - [ ] `glColor4f(1f, 1f, 1f, 0.9f)` — white with slight transparency
  - [ ] `glBegin(GL_POINTS)`:
    - [ ] For each active particle: `glPointSize(p.size)` (note: must be set outside begin/end, so either use uniform size or batch by size)
    - [ ] `glVertex3d(p.position.getX(), p.position.getY(), p.position.getZ())`
  - [ ] `glEnd()`
  - [ ] `glDisable(GL_BLEND)`
  - [ ] `glDisable(GL_POINT_SMOOTH)`
  - [ ] `glEnable(GL_LIGHTING)` — re-enable for other objects

> ⚠️ `glPointSize()` cannot be called inside `glBegin/glEnd`. Either use one fixed size for all, or sort particles by size and draw in batches.

### 5.5 Wire into Renderer
- [ ] In `Renderer`: declare `private SnowParticleSystem snow`
- [ ] In `Renderer.init()`: `snow = new SnowParticleSystem()`
- [ ] In `Renderer.display()`:
  - [ ] Compute deltaTime (store `lastFrameTime`, compute diff with `System.nanoTime()`)
  - [ ] Call `snow.update(deltaTime)`
  - [ ] Call `snow.render()`
- [ ] **Run & test**: verify snowflakes fall from above and disappear at ground level

---

## Phase 6: SkyBox (SkyBox.java)

### 6.1 Load 6 Textures
- [ ] In `init()`: allocate `faces = new OGLTexture2D[6]`
- [ ] Load each face in try/catch:
  - [ ] `faces[0] = new OGLTexture2D("textures/snow_positive_x.jpg")` → right
  - [ ] `faces[1] = new OGLTexture2D("textures/snow_negative_x.jpg")` → left
  - [ ] `faces[2] = new OGLTexture2D("textures/snow_positive_y.jpg")` → top
  - [ ] `faces[3] = new OGLTexture2D("textures/snow_negative_y.jpg")` → bottom
  - [ ] `faces[4] = new OGLTexture2D("textures/snow_positive_z.jpg")` → front
  - [ ] `faces[5] = new OGLTexture2D("textures/snow_negative_z.jpg")` → back

### 6.2 Render Skybox Cube
- [ ] In `render(eyeX, eyeY, eyeZ)`:
  - [ ] `glDisable(GL_LIGHTING)` — skybox is fullbright
  - [ ] `glDisable(GL_DEPTH_TEST)` — skybox is always behind everything
  - [ ] `glEnable(GL_TEXTURE_2D)`
  - [ ] `glPushMatrix()`
  - [ ] `glTranslated(eyeX, eyeY, eyeZ)` — follow camera
  - [ ] `float s = 200f` — large size
  - [ ] For each of 6 faces: bind texture, draw `GL_QUADS` with correct vertices and tex coords (0,0)→(1,1)
    - [ ] Right face (+X): vertices at (s, -s, -s), (s, -s, s), (s, s, s), (s, s, -s)
    - [ ] Left face (-X): vertices at (-s, -s, s), (-s, -s, -s), (-s, s, -s), (-s, s, s)
    - [ ] Top face (+Y): vertices at (-s, s, -s), (s, s, -s), (s, s, s), (-s, s, s)
    - [ ] Bottom face (-Y): vertices at (-s, -s, s), (s, -s, s), (s, -s, -s), (-s, -s, -s)
    - [ ] Front face (+Z): vertices at (-s, -s, s), (s, -s, s), (s, s, s), (-s, s, s) → wait, check orientation!
    - [ ] Back face (-Z): similar with -Z
  - [ ] `glPopMatrix()`
  - [ ] `glEnable(GL_DEPTH_TEST)` — restore
  - [ ] `glEnable(GL_LIGHTING)` — restore
  - [ ] `glDisable(GL_TEXTURE_2D)`

### 6.3 Wire into Renderer
- [ ] In `Renderer`: declare `private SkyBox skyBox`
- [ ] In `Renderer.init()`: `skyBox = new SkyBox(); skyBox.init()`
- [ ] In `Renderer.display()`: render skybox **first** (before terrain/house), pass camera eye position
  - [ ] Get eye: `Vec3D eye = camera.getEye()`
  - [ ] `skyBox.render(eye.getX(), eye.getY(), eye.getZ())`
- [ ] **Run & test**: verify sky environment surrounds the scene

---

## Phase 7: GUI / HUD Controls

### 7.1 Keyboard Controls in Renderer
- [ ] In `glfwKeyCallback`:
  - [ ] `+` / `=` key → increase snow intensity: `snow.setMaxParticles(snow.getMaxParticles() + 100)`
  - [ ] `-` key → decrease snow intensity: `snow.setMaxParticles(Math.max(0, snow.getMaxParticles() - 100))`
  - [ ] `←` arrow → wind left: modify wind X component by -0.5
  - [ ] `→` arrow → wind right: modify wind X component by +0.5
  - [ ] `↑` arrow → wind forward: modify wind Z component by -0.5
  - [ ] `↓` arrow → wind backward: modify wind Z component by +0.5
  - [ ] `L` key → cycle light intensity (e.g., 0.3 → 0.6 → 1.0 → 0.3)
  - [ ] `C` key → cycle light color (white → warm yellow → cool blue → white)
  - [ ] `P` key → toggle light position (e.g., rotate around scene)
  - [ ] `R` key → reset all parameters to defaults
  - [ ] `1`/`2` keys → switch camera first-person / third-person mode

### 7.2 HUD Text Overlay
- [ ] In `Renderer.display()` at the end (after all 3D rendering):
  - [ ] Use `textRenderer.clear()` then multiple `textRenderer.addStr2D(x, y, text)` calls
  - [ ] Display:
    - [ ] `"Snow: " + snow.getActiveCount() + " / " + snow.getMaxParticles()`
    - [ ] `"Wind: (" + wind.x + ", " + wind.z + ")"`
    - [ ] `"Light intensity: " + lighting.getIntensity()`
    - [ ] `"Controls: WASD=move | Mouse=look | +/-=snow | Arrows=wind | L=light"`
  - [ ] Call `textRenderer.draw()`

### 7.3 Final Verification
- [ ] **Run & test all controls**:
  - [ ] WASD movement works smoothly
  - [ ] LMB + drag rotates camera
  - [ ] +/- changes particle count visibly
  - [ ] Arrow keys change wind direction (snow drifts sideways)
  - [ ] L cycles light intensity
  - [ ] HUD text displays current values

---

## Phase 8: Polish & Optimization

### 8.1 Performance
- [ ] Limit max particles to a reasonable cap (e.g., 5000) to maintain 60 FPS
- [ ] Use `GL_POINTS` batching (one `glBegin/glEnd` block, not per-particle)
- [ ] Consider display lists (`glGenLists`) for static objects (house, terrain)

### 8.2 Visual Polish
- [ ] Add snow on roof: white quads slightly above roof surface
- [ ] Add subtle fog for winter atmosphere → `glEnable(GL_FOG)`, `glFogf(GL_FOG_START, ...)`, `glFogfv(GL_FOG_COLOR, ...)` 
- [ ] Add house texture (e.g., `bricks.jpg` for walls) — optional
- [ ] Vary snowflake alpha based on distance from camera for depth effect

### 8.3 Correct Transform Stacking
- [ ] Verify every `glPushMatrix()` has a matching `glPopMatrix()`
- [ ] Verify house sub-components (walls, roof, door, windows) each use their own push/pop pair
- [ ] Verify skybox doesn't pollute the modelview matrix

### 8.4 Collision Refinement
- [ ] Test snow accumulates on roof (particles stop at roof level, not just at Y=0)
- [ ] Test snow stops correctly on terrain plane
- [ ] Verify no particles "leak" through the ground at high wind speeds (clamp Y)

### 8.5 Code Cleanup
- [ ] Remove all remaining `// TODO:` comments as features are implemented
- [ ] Verify no stale imports
- [ ] Test ESC properly closes the window
- [ ] Final run: verify no OpenGL errors in console

---

## Summary: File → Responsibility

| File | Phase | What to implement |
|------|-------|-------------------|
| [Renderer.java](file:///d:/Docs/!Study/2LS/PGRF2/c01_maslova_maria/src/winterlandscape/global/Renderer.java) | 1, 7 | Camera, projection, display loop, keyboard/mouse, HUD |
| [TerrainPlane.java](file:///d:/Docs/!Study/2LS/PGRF2/c01_maslova_maria/src/winterlandscape/TerrainPlane.java) | 2 | Textured ground quad |
| [SceneLighting.java](file:///d:/Docs/!Study/2LS/PGRF2/c01_maslova_maria/src/winterlandscape/SceneLighting.java) | 3 | GL_LIGHT0 setup + apply |
| [House.java](file:///d:/Docs/!Study/2LS/PGRF2/c01_maslova_maria/src/winterlandscape/House.java) | 4 | Geometric house with push/pop |
| [SnowParticle.java](file:///d:/Docs/!Study/2LS/PGRF2/c01_maslova_maria/src/winterlandscape/SnowParticle.java) | 5 | Data class (done ✅) |
| [SnowParticleSystem.java](file:///d:/Docs/!Study/2LS/PGRF2/c01_maslova_maria/src/winterlandscape/SnowParticleSystem.java) | 5 | Spawn, physics, collision, render |
| [SkyBox.java](file:///d:/Docs/!Study/2LS/PGRF2/c01_maslova_maria/src/winterlandscape/SkyBox.java) | 6 | 6-face textured cube |
| [App.java](file:///d:/Docs/!Study/2LS/PGRF2/c01_maslova_maria/src/winterlandscape/App.java) | — | Entry point (done ✅) |
