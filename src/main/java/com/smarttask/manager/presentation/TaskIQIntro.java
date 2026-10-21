package com.smarttask.manager.presentation;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;

public class TaskIQIntro {
    private static final String START_COLOR = "#0D89FF";
    private static final String END_COLOR = "#190DFF";
    private Runnable onFinished;

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    public Parent createRoot() {
        StackPane root = new StackPane();
        root.setPrefSize(800, 800);
        root.setStyle("-fx-background-color: white;");

        Group logoGroup = new Group();
        logoGroup.setScaleX(0.8);
        logoGroup.setScaleY(0.8);

        LinearGradient logoGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(START_COLOR)),
                new Stop(1, Color.web(END_COLOR)));

        String[] pathData = {
                "M298.993 144.32H273.028L273.131 144.906L293.718 263.275L246.633 207.806L246.249 207.354L245.868 207.808L199.294 263.246L220.366 144.908L220.472 144.32H194.002L246.252 74.8291L298.993 144.32Z",
                "M180.108 7.5C94.9388 56.4418 86.2158 154.779 129.5 212.166V263.985L74.5 221.753V58.5H0.782227L24.3203 7.5H180.108Z",
                "M248 0.5C321.178 0.5 380.5 59.8223 380.5 133C380.5 185.034 350.505 230.062 306.861 251.739L295.05 192.808C312.988 178.614 324.5 156.652 324.5 132C324.5 89.1979 289.802 54.5 247 54.5C204.198 54.5 169.5 89.1979 169.5 132C169.5 156.655 181.014 178.62 198.956 192.813L188.624 251.482C145.259 229.708 115.5 184.829 115.5 133C115.5 59.8223 174.822 0.5 248 0.5Z",
                "M382.23 7.5L405.999 59H370.467C358.049 39.2294 340.041 21.3773 315.892 7.5H382.23Z"
        };

        double[] pathLengths = { 614.5, 808.6, 1213.1, 234.4 };
        List<Animation> anims = new ArrayList<>();
        Duration initialDelay = Duration.seconds(2);

        for (int i = 0; i < pathData.length; i++) {
            SVGPath path = new SVGPath();
            path.setContent(pathData[i]);
            path.setStroke(logoGradient);
            path.setStrokeWidth(2.0);
            path.setFill(Color.TRANSPARENT);
            path.setOpacity(0);

            path.getStrokeDashArray().add(pathLengths[i]);
            path.setStrokeDashOffset(pathLengths[i]);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), path);
            fadeIn.setToValue(1);
            fadeIn.setDelay(initialDelay);

            Timeline draw = new Timeline(
                    new KeyFrame(Duration.seconds(3), new KeyValue(path.strokeDashOffsetProperty(), 0, Interpolator.EASE_BOTH))
            );
            draw.setDelay(initialDelay.add(Duration.millis(300)));

            Timeline fillTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(path.fillProperty(), Color.TRANSPARENT)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(path.fillProperty(), logoGradient))
            );
            fillTimeline.setDelay(initialDelay.add(Duration.seconds(2.5)));

            anims.add(fadeIn);
            anims.add(draw);
            anims.add(fillTimeline);
            logoGroup.getChildren().add(path);
        }

        root.getChildren().add(logoGroup);

        ParallelTransition main = new ParallelTransition(anims.toArray(new Animation[0]));

        main.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(ev -> {
                if (onFinished != null) onFinished.run();
            });
            pause.play();
        });

        main.play();
        return root;
    }

}