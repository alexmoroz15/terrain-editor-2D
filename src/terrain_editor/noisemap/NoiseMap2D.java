package terrain_editor.noisemap;

import java.util.ArrayList;
import java.util.Random;

public abstract class NoiseMap2D {
    long seed;
    int[] randomVals;
    double frequency;
    InterpMethod interp;

    ArrayList<ChangeListener> changeListeners;

    public NoiseMap2D(long seed, double frequency) {
        changeListeners = new ArrayList<>();
        setSeed(seed);
        this.frequency = frequency;
        interp = new Lerp();
    }

    public NoiseMap2D(int[] randomVals, double frequency, InterpMethod interp) {
        changeListeners = new ArrayList<>();
        this.seed = 0;
        this.randomVals = randomVals;
        this.frequency = frequency;
        this.interp = interp;
    }

    public abstract double get(double x, double y);

    public void setSeed(long seed) {
        this.seed = seed;
        var random = new Random(this.seed);
        randomVals = new int[512];
        for (int i = 0; i < randomVals.length; i++) {
            randomVals[i] = random.nextInt(256);
        }
        commitChanges();
    }
    public long getSeed() { return seed; }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
        commitChanges();
    }

    public double getFrequency() {
        return frequency;
    }

    public void addChangeListener(ChangeListener cl) {
        changeListeners.add(cl);
    }

    protected void commitChanges() {
        for (var cl : changeListeners) {
            cl.handle(this);
        }
    }

    public interface ChangeListener {
        void handle(NoiseMap2D newValue);
    }

    public interface InterpMethod {
        double interp(double y1, double y2, double t);
    }

    public static class Lerp implements InterpMethod {
        public double interp(double y1, double y2, double t) {
            return y1 * (1 - t) + y2 * t;
        }
    }
}
