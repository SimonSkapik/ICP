package pkg.skapik.icp.func;

public class FPSCounter {

private long lastTime = System.nanoTime();

private double tickRate;
private double tickCheck;
private double tickDelta;

private double frameRate;
private double frameCheck;
private double frameDelta;

private int updates;
private int frames;
private long timer;

private long nanosPerFrame;
private long nanosPerUpdate;


////// Constructor ///////

public FPSCounter(double frameRate, double tickRate) {

    this.frameRate = frameRate;
    frameCheck = 1_000_000_000 / this.frameRate;
    frameDelta = 0;

    this.tickRate = tickRate;
    tickCheck = 1_000_000_000 / this.tickRate;
    tickDelta = 0;

    updates = 0;
    frames = 0;
    timer = System.currentTimeMillis();

}


////// find delta //////

public void findDeltas() {
    long now = System.nanoTime();
    tickDelta += now - lastTime;
    frameDelta += now - lastTime;
    lastTime = now;
}


////// Delta Check //////

public boolean checkTickDelta() {
    if (tickDelta >= tickCheck)  {
        tickDelta = 0;
        return true;
    }
    return false;
}

public boolean checkFrameDelta() {
    if (frameDelta >= frameCheck)  {
        frameDelta = 0;
        return true;
    }
    return false;
}


////// Second Check //////

public void checkPassingSecond() {
    if (System.currentTimeMillis() - timer > 1000) {
        System.out.println(updates + " updates, fps is " + frames);
        timer += 1000;
        frames = 0;
        updates = 0;
    }
}


////// Game Loop Methods ///////

public void render(long before) {
    long after = System.nanoTime();
    nanosPerFrame = after - before;
    frames++;
}

public void tick(long before) {
    long after = System.nanoTime();
    nanosPerUpdate = after - before;
    updates++;
}
}