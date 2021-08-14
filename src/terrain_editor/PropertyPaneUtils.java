package terrain_editor;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

public class PropertyPaneUtils {
    public static TextFormatter<Double> createDoubleTextFormatter(double defaultVal) {
        return new TextFormatter<Double>(
                new DoubleStringConverter(),
                defaultVal,
                change -> {
                    if (change.getText().isEmpty()) {
                        return change;
                    }
                    if (change.getText().equals(".")) {
                        return change;
                    }
                    try {
                        Double.parseDouble(change.getText());
                        return change;
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                });
    }

    public static TextFormatter<Long> createLongTextFormatter(long defaultVal) {
        return new TextFormatter<>(
                new LongStringConverter(),
                defaultVal,
                change -> {
                    if (change.getText().isEmpty()) {
                        return change;
                    }
                    try {
                        Long.parseLong(change.getText());
                        return change;
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                });
    }

    public static TextFormatter<Integer> createIntTextFormatter(int defaultVal) {
        return new TextFormatter<>(
                new IntegerStringConverter(),
                defaultVal,
                change -> {
                    /*
                    if (change.isDeleted()) {
                        return change;
                    }

                     */
                    if (change.getText().isEmpty()) {
                        return change;
                    }
                    try {
                        Integer.parseInt(change.getText());
                        return change;
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                });
    }
}
