package com.nyrds.pixeldungeon.game;

import android.opengl.GLES20;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.nyrds.LuaInterface;
import com.nyrds.platform.EventCollector;
import com.nyrds.platform.audio.Music;
import com.nyrds.platform.audio.Sample;
import com.nyrds.platform.game.Game;
import com.nyrds.platform.gfx.SystemText;
import com.nyrds.platform.util.TrackedRuntimeException;
import com.nyrds.util.ModdingMode;
import com.nyrds.util.ReportingExecutor;
import com.watabou.input.Keys;
import com.watabou.input.Touchscreen;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.Scene;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.utils.SystemTime;

import org.luaj.vm2.LuaError;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import lombok.SneakyThrows;

public class GameLoop {

    private final Executor executor = new ReportingExecutor();
    private final ConcurrentLinkedQueue<Runnable> uiTasks = new ConcurrentLinkedQueue<>();

    // New scene class
    private Class<? extends Scene> sceneClass;
    protected static int difficulty = Integer.MAX_VALUE;

    private static float timeScale = 1f;
    public static float elapsed = 0f;

    public int framesSinceInit;

    private static GameLoop instance;

    // Current scene
    public Scene scene;
    // true if scene switch is requested
    protected boolean requestedReset = true;

    // Current time in milliseconds
    private long now;
    // Milliseconds passed since previous update
    private long step;


    public Runnable doOnResume;

    // Accumulated touch events
    public final ArrayList<MotionEvent> motionEvents = new ArrayList<>();

    // Accumulated key events
    public final ArrayList<KeyEvent> keysEvents = new ArrayList<>();

    private final ArrayList<MotionEvent> motionEventsCopy = new ArrayList<>();
    private final ArrayList<KeyEvent>    keyEventsCopy    = new ArrayList<>();

    public GameLoop(Class<? extends Scene> c) {
        super();
        instance = this;
        sceneClass = c;
    }

    static public GameLoop instance() {
        return instance;
    }

    static public void pushUiTask(Runnable task) {
        instance().uiTasks.add(task);
    }

    static public void execute(Runnable task) {
        instance().executor.execute(task);
    }

    public static void setNeedSceneRestart() {
        if (!(instance().scene instanceof InterlevelScene)) {
            instance().requestedReset = true;
        }
    }

    public static float getDifficultyFactor() {
        switch (getDifficulty()) {
            case 0:
                return 1f;
            case 1:
            case 2:
                return 1.5f;
            case 3:
                return 2;
            default:
                return 1;
        }
    }

    @LuaInterface
    public static int getDifficulty() {
        return difficulty;
    }

    public static void addToScene(Gizmo gizmo) {
        Scene scene = scene();
        if(scene!=null) {
            scene.add(gizmo);
        }
    }

    public static Scene scene() {
        return instance().scene;
    }

    public static void switchScene(Class<? extends Scene> c) {
        instance().sceneClass = c;
        instance().requestedReset = true;
    }

    @LuaInterface
    public static void resetScene() {
        switchScene(instance().sceneClass);
    }

    public void shutdown() {
        if (instance().scene != null) {
            instance().scene.pause();
            instance().scene.destroy();
        }
    }

    public void onResume() {
        now = 0;

        SystemTime.tick();
        SystemTime.updateLastActionTime();

        Music.INSTANCE.resume();
        Sample.INSTANCE.resume();

        if (doOnResume != null) {
            GameLoop.pushUiTask( () -> {
                        doOnResume.run();
                        doOnResume = null;
                    }
            );
        }
    }


    public void onFrame() {
        SystemTime.tick();
        long rightNow = SystemTime.now();
        step = Math.min((now == 0 ? 0 : rightNow - now),250);
        now = rightNow;

        framesSinceInit++;


        if (framesSinceInit>2) {
            Runnable task;
            while ((task = uiTasks.poll()) != null) {
                task.run();
            }

            if (!Game.softPaused) {
                try {
                    step();
                } catch (LuaError e) {
                    throw ModdingMode.modException(e);
                } catch (Exception e) {
                    throw new TrackedRuntimeException(e);
                }
            }

            NoosaScript.get().resetCamera();

            GLES20.glScissor(0, 0, Game.width(), Game.height());
            GLES20.glClearColor(0, 0, 0, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            if (scene != null) {
                scene.draw();
            }
        }
    }

    @SneakyThrows
    public void step() {

        if (requestedReset) {
            requestedReset = false;
            switchScene(sceneClass.newInstance());
            return;
       }

        elapsed = timeScale * step * 0.001f;

        synchronized (motionEvents) {
            motionEventsCopy.addAll(motionEvents);
            motionEvents.clear();
        }

        Touchscreen.processTouchEvents(motionEventsCopy);
        motionEventsCopy.clear();


        synchronized (keysEvents) {
            keyEventsCopy.addAll(keysEvents);
            keysEvents.clear();
        }

        Keys.processTouchEvents(keyEventsCopy);
        keyEventsCopy.clear();


        scene.update();
        Camera.updateAll();
    }

    private void switchScene(Scene requestedScene) {

        SystemText.invalidate();
        Camera.reset();

        if (scene != null) {
            EventCollector.setSessionData("pre_scene",scene.getClass().getSimpleName());
            scene.destroy();
        }
        scene = requestedScene;
        scene.create();
        EventCollector.setSessionData("scene",scene.getClass().getSimpleName());

        elapsed = 0f;
        timeScale = 1f;

        Game.syncAdsState();
    }

    public static void setDifficulty(int difficulty) {
        GameLoop.difficulty = difficulty;
    }
}
