package terrain_editor;

public class FractalMapParams {
    public long seed;
    public double frequency;
    public double lacunarity;
    public double gain;
    public int numLayers;

    public FractalMapParams() {
        this.seed = 0;
        this.frequency = 1.0;
        this.lacunarity = 1.0;
        this.gain = 1.0;
        this.numLayers = 1;
    }
}
