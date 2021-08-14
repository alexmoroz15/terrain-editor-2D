package terrain_editor;

import javafx.scene.image.Image;

public class PreviewPaneParams {
    public int numRows;
    public int numColumns;
    public double xOffset;
    public double yOffset;
    public Image tileImage;
    public double minAmplitude;
    public double maxAmplitude;
    public boolean minStrict;
    public boolean maxStrict;

    public PreviewPaneParams() {
        this.numRows = 20;
        this.numColumns = 20;
        this.xOffset = 0.0;
        this.yOffset = 0.0;
        this.tileImage = new Image("/images/swamp_ground.png");
        this.minAmplitude = 0.0;
        this.maxAmplitude = 1.0;
        this.minStrict = false;
        this.maxStrict = false;
    }
}
