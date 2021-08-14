package terrain_editor;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import terrain_editor.noisemap.FractalMap2D;

import static terrain_editor.PropertyPaneUtils.*;

public class MapPropertyPane extends VBox {
    public MapPropertyPane(FractalMap2D noiseMap) {
        var seedField = new TextField();
        seedField.setTextFormatter(createLongTextFormatter(noiseMap.getSeed()));
        seedField.setOnAction(actionEvent -> noiseMap.setSeed((long) seedField.getTextFormatter().getValue()));
        seedField.setPrefColumnCount(20);
        var seedLabel = new Label("Seed: ");
        var seedHolder = new HBox(seedLabel, seedField);

        var freqField = new TextField();
        freqField.setTextFormatter(createDoubleTextFormatter(noiseMap.getFrequency()));
        freqField.setOnAction(actionEvent -> noiseMap.setFrequency((double) freqField.getTextFormatter().getValue()));
        var freqLabel = new Label("Frequency: ");
        var freqHolder = new HBox(freqLabel, freqField);

        var lacField = new TextField();
        lacField.setTextFormatter(createDoubleTextFormatter(noiseMap.getLacunarity()));
        lacField.setOnAction(actionEvent -> noiseMap.setLacunarity((double) lacField.getTextFormatter().getValue()));
        var lacLabel = new Label("Lacunarity: ");
        var lacHolder = new HBox(lacLabel, lacField);

        var gainField = new TextField();
        gainField.setTextFormatter(createDoubleTextFormatter(noiseMap.getGain()));
        gainField.setOnAction(actionEvent -> noiseMap.setGain((double) gainField.getTextFormatter().getValue()));
        var gainLabel = new Label("Gain: ");
        var gainHolder = new HBox(gainLabel, gainField);

        var numLayersField = new TextField();
        numLayersField.setTextFormatter(createIntTextFormatter(noiseMap.getNumLayers()));
        numLayersField.setOnAction(actionEvent -> noiseMap.setNumLayers((int) numLayersField.getTextFormatter().getValue()));
        var numLayersLabel = new Label("Number of layers: ");
        var numLayersHolder = new HBox(numLayersLabel, numLayersField);

        getChildren().addAll(seedHolder, freqHolder, lacHolder, gainHolder, numLayersHolder);
    }
}
