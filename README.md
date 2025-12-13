# GoblinGame

GoblinGame es un juego 2D para Android desarrollado en **Kotlin**.  
El jugador controla a un goblin situado en la parte inferior de la pantalla y se mueve de izquierda a derecha para evitar bombas que caen desde la parte superior a intervalos regulares. Cuando una bomba colisiona con el goblin, se muestra una explosión y la partida termina.

## Características

- Movimiento horizontal sencillo del personaje (sin saltos ni disparos).
- Bombas que aparecen periódicamente y caen verticalmente.
- Detección de colisiones entre goblin y bombas con animación de explosión.
- Pantalla de inicio con botón **Start Game** y pantalla de juego.
- Sistema de puntuación basado en tiempo de supervivencia.
- Aumento progresivo de dificultad incrementando velocidad y frecuencia de bombas.
- Música de fondo en bucle y efecto de sonido de explosión.

## Tecnologías

- **Lenguaje y plataforma**
    - **Kotlin**.
    - **Android SDK** (Android Studio).

- **Gráficos 2D**
    - `SurfaceView` y `SurfaceHolder.Callback` como lienzo dedicado al juego.
    - `Canvas`, `Bitmap` y `Paint` para dibujar fondo, goblin, bombas, HUD y explosión en cada frame.
    - Modelo de sprites separado en clases (`Goblin`, `Bomb`, `Explosion`).

- **Lógica de juego y bucle principal**
    - Clase `GameView` como vista principal del juego.
    - Clase `GameThread` (derivada de `Thread`) que ejecuta el bucle:
        - `update()` para lógica (movimiento, generación de bombas, colisiones, dificultad).
        - `draw(canvas)` para redibujar toda la escena.

- **Entrada de usuario**
    - Manejo de toques con `MotionEvent` y `onTouchEvent()`.
    - Movimiento del goblin según la posición horizontal del toque (izquierda/derecha).

- **Sonido**
    - `MediaPlayer` para la música de fondo en bucle.
    - `SoundPool` para efectos de sonido de baja latencia (explosión).
    - Clase `SoundManager` para:
        - `startMusic()`, `pauseMusic()`, `stopAndRelease()`.
        - `playExplosion()` al producirse la colisión bomba–goblin.

- **Colisiones y tiempo**
    - Detección de colisiones mediante rectángulos (`RectF.intersects`) entre goblin y bombas.
    - Clase `SurvivalTimer`:
        - `start()` guarda el instante inicial con `System.nanoTime()`.
        - `update()` calcula segundos transcurridos y los expone como puntuación.
        - El tiempo de supervivencia se usa también para escalar dificultad (velocidad de bombas).

- **Recursos**
    - `res/drawable`: sprites del goblin, bombas, explosión, fondo del bosque, imágenes de Game Over, formas de botones.
    - `res/raw`: música de fondo (MP3) y sonido de explosión (WAV).
    - `res/layout`: pantallas XML (especialmente `activity_main.xml` para el menú principal).

## Estructura del proyecto

- `AndroidManifest.xml`
    - Declara la aplicación y las activities.
    - `MainActivity` se marca como activity de inicio con un *intent-filter* (`MAIN` + `LAUNCHER`) para abrir la app desde el icono.
    - `GameActivity` se registra como activity interna en orientación vertical y se abre mediante un intent explícito desde `MainActivity`.

- `MainActivity.kt`
    - Activity inicial del juego.
    - Muestra el menú con botón **Start Game** y botón **Exit** (`activity_main.xml`).
    - Usa `findViewById(...)` para obtener los botones.
    - Al pulsar **Start Game**:
        - Crea un `Intent` explícito hacia `GameActivity`.
        - Lanza la partida con `startActivity(intent)`.
    - Al pulsar **Exit**:
        - Llama a `finish()` para cerrar la Activity principal.

- `GameActivity.kt`
    - Actúa como contenedor de la lógica de juego.
    - En `onCreate()`:
        - Instancia `GameView` y la establece como vista principal (`setContentView(gameView)`).
    - En `onPause()` y `onResume()`:
        - Llama a `gameView.pause()` y `gameView.resume()` para pausar/reanudar el hilo de juego y la música sin bloquear la UI.

- `game/GameView.kt`
    - Corazón del juego: se dibuja todo y se ejecuta la lógica frame a frame.
    - Hereda de `SurfaceView` e implementa `SurfaceHolder.Callback`:
        - `getHolder().addCallback(this)` registra la vista para recibir eventos de creación/cambio/destrucción de la superficie.
        - En `surfaceCreated()` se arranca el `GameThread`.
        - En `surfaceDestroyed()` se detiene el hilo de forma segura.
    - Inicializa:
        - Sprites (`Goblin`, lista de `Bomb`, `Explosion`).
        - `SoundManager` para música y efectos.
        - `SurvivalTimer` para el tiempo de supervivencia.
    - `update()`:
        - Mueve el goblin según la entrada guardada.
        - Genera nuevas bombas (más frecuentes/rápidas con el tiempo).
        - Actualiza la posición de todas las bombas.
        - Comprueba colisiones goblin–bomba:
            - Obtiene `goblin.getRect()`.
            - Recorre las bombas activas y compara `bomb.getRect()` con el del goblin usando `RectF.intersects`.
            - Si se solapan:
                - Marca `isGameOver = true`.
                - Desactiva la bomba (`bomb.active = false`).
                - Lanza `explosion.trigger(goblin.x, goblin.y)`.
                - Reproduce `soundManager.playExplosion()`.
        - Actualiza el `SurvivalTimer` y la puntuación mostrada.
    - `draw(canvas: Canvas)`:
        - Dibuja, en orden:
            1. Fondo del bosque.
            2. Bombas.
            3. Goblin.
            4. Explosión (si está activa).
            5. HUD con tiempo de supervivencia (texto con `Paint`).
    - `onTouchEvent(event: MotionEvent)`:
        - Ignora toques si `isGameOver` es `true`.
        - Si el evento es `ACTION_DOWN` o `ACTION_MOVE`:
            - Calcula un paso (`step`) en función del ancho de la pantalla.
            - Si `event.x` está a la izquierda de la mitad, llama a `goblin.moveLeft(step)`.
            - Si está a la derecha, llama a `goblin.moveRight(step)`.
        - Esto permite un control táctil sencillo: tocar izquierda/derecha para mover al personaje.

- `game/GameThread.kt`
    - Hilo dedicado al bucle principal del juego.
    - Recibe un `SurfaceHolder` y una referencia a `GameView`.
    - Mientras `running` es `true`:
        - Bloquea el lienzo con `surfaceHolder.lockCanvas()`.
        - Dentro de un bloque sincronizado sobre el `SurfaceHolder`:
            - Llama a `gameView.update()`.
            - Llama a `gameView.draw(canvas)`.
        - Publica el frame en pantalla con `surfaceHolder.unlockCanvasAndPost(canvas)`.

- `game/SoundManager.kt`
    - Clase que centraliza la gestión de audio.
    - Usa `MediaPlayer` para la música de fondo en bucle (`isLooping = true`).
    - Usa `SoundPool` para efectos (explosión) de baja latencia.
    - Métodos principales:
        - `startMusic()`, `pauseMusic()`, `stopAndRelease()`.
        - `playExplosion()`.
    - Carga los recursos con el `Context` de la app (`R.raw.music`, `R.raw.explosion`) y ajusta volumen con `setVolume()`.

- `game/SurvivalTimer.kt`
    - Encapsula el cálculo del tiempo de supervivencia.
    - `start()`:
        - Guarda el instante de inicio usando `System.nanoTime()`.
    - `update()`:
        - Calcula la diferencia con el tiempo actual.
        - Convierte los nanosegundos a segundos enteros.
        - Expone `seconds` para que `GameView` lo pinte en el HUD.
    - Se reinicia al comenzar una nueva partida y se usa para controlar dificultad progresiva.

- `game/model/Goblin.kt`
    - Modelo del personaje principal.
    - Carga un `Bitmap` desde `R.drawable.goblin`.
    - Mantiene:
        - Coordenadas `x`, `y`.
        - Anchura y altura del sprite.
    - Métodos:
        - `draw(canvas)` para dibujar con `canvas.drawBitmap(bitmap, x, y, null)`.
        - `moveLeft(step)` y `moveRight(step)` para mover horizontalmente respetando los límites de la pantalla.
        - `getRect()` que devuelve un `RectF` usado para colisiones.

- `game/model/Bomb.kt`
    - Representa cada bomba que cae.
    - Carga el sprite desde `R.drawable.bomba` en un `Bitmap`.
    - Mantiene:
        - Posición `x`, `y`.
        - Velocidad de caída `speed`.
        - Estado `active`.
    - `update(deltaSec)`:
        - Incrementa `y` en función de `speed` y del tiempo.
    - `draw(canvas)`:
        - Dibuja el bitmap de la bomba en su posición actual.
    - `getRect()`:
        - Devuelve un `RectF` alrededor del bitmap para las colisiones.

- `game/model/Explosion.kt`
    - Gestiona la animación de explosión.
    - Carga el bitmap desde `R.drawable.explosion`.
    - Lo escala al tamaño del goblin con `Bitmap.createScaledBitmap(...)`.
    - Métodos:
        - `trigger(x, y)` fija la posición y activa la animación.
        - `update(deltaSec)` avanza el tiempo interno o frame de animación.
        - `draw(canvas)` dibuja la explosión solo mientras está activa.

- `res/drawable/`
    - Sprites y gráficos del juego:
        - `goblin.png`
        - `bomba.png`
        - `explosion.png`
        - Fondos (`bosque.jpg`)
        - Imágenes de Game Over (`game_over.png`, `game_over01.png`)
        - Formas como `btn_start.xml` para estilizar botones.

- `res/raw/`
    - Recursos de audio:
        - Música de fondo (`music.mp3`).
        - Efecto de explosión (`explosion.wav`).

- `res/layout/`
    - Diseños XML:
        - `activity_main.xml` para la pantalla de inicio:
            - `ConstraintLayout` con fondo del bosque.
            - Botón **Start Game**.
            - Botón **Exit** en la parte inferior derecha.
        - (Opcional) Otro layout para `GameActivity` si se usa, o se establece directamente `GameView` como contenido.

## Gameflow

1. **Inicio – Menú principal**
    - La app arranca en `MainActivity` (intent con `MAIN` + `LAUNCHER`).
    - Se muestra el fondo del bosque y los botones **Start Game** y **Exit**.
    - El jugador pulsa **Start Game**:
        - Se crea un `Intent` explícito hacia `GameActivity`.
        - Comienza la partida.
    - Si pulsa **Exit**, la actividad se cierra y se sale de la app.

2. **Juego en marcha**
    - `GameActivity` crea `GameView` y la establece como contenido.
    - `GameView` arranca el `GameThread` y comienza el bucle:
        - `update()`:
            - Mueve goblin y bombas.
            - Genera nuevas bombas y ajusta velocidad/dificultad.
            - Actualiza el `SurvivalTimer`.
            - Comprueba colisiones goblin–bomba.
        - `draw(canvas)`:
            - Dibuja fondo, bombas, goblin, explosión (si la hay) y HUD.
    - El jugador controla el goblin con toques a izquierda/derecha.

3. **Colisión y Game Over**
    - Cuando una bomba activa intersecta con el rectángulo del goblin:
        - Se marca `isGameOver`.
        - Se desactiva la bomba.
        - Se lanza la animación de explosión en la posición del goblin.
        - Se reproduce el sonido de explosión.
        - Se congela la lógica de movimiento, manteniendo la escena visible.
    - En pantalla se sigue mostrando el último valor de tiempo de supervivencia.

4. **Fin de partida y regreso al menú**
    - Tras unos instantes en estado de “Game Over” o tras una interacción (por ejemplo, un toque extra):
        - `GameActivity` vuelve a `MainActivity` (finalizando la activity o lanzando un nuevo intent).
    - El menú se muestra de nuevo, con el juego listo para otra partida.