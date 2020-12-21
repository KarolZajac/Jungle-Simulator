package com.karolzajac.world;

import com.karolzajac.world.map.RectangularMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Jungle extends Application {
    private final static int PERIOD = 500;
    VBox box1;
    VBox box2;
    GridPane root;
    GridPane root2;
    List<List<Button>> view1 = new CopyOnWriteArrayList<>();
    List<List<Button>> view2 = new CopyOnWriteArrayList<>();

    public Jungle() {

    }

    private void initWorld(Stage stage, Integer[] args) {
        root = new GridPane();
        root2 = new GridPane();
        Text text = new Text();
        Text text2 = new Text();

        RectangularMap map1 = new RectangularMap(args[0], args[1], args[6], args[3], args[4], args[5]);
        SimulationEngine engine = new SimulationEngine(map1, view1, root, text);
        engine.placePrimaryAnimals(args[2], map1);

        final ScheduledExecutorService[] executorService = {Executors.newSingleThreadScheduledExecutor()};
        executorService[0].scheduleAtFixedRate(engine::runSimulation,
                100, PERIOD, TimeUnit.MILLISECONDS);
        initView(map1, view1, 0, root, engine, executorService);

        addSimulationControl(executorService, engine, 1, root);


        RectangularMap map2 = new RectangularMap(args[0], args[1], args[6], args[3], args[4], args[5]);
        SimulationEngine engine2 = new SimulationEngine(map2, view2, root2, text2);
        engine2.placePrimaryAnimals(args[2], map2);

        final ScheduledExecutorService[] executorService2 = {Executors.newSingleThreadScheduledExecutor()};
        executorService2[0].scheduleAtFixedRate(engine2::runSimulation,
                100, PERIOD, TimeUnit.MILLISECONDS);
        initView(map2, view2, map2.height + 2, root2, engine2, executorService2);

        addSimulationControl(executorService2, engine2, map2.height + 3, root2);


        box1 = new VBox(root, text);
        box2 = new VBox(root2, text2);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(box1);
        ScrollPane scroll2 = new ScrollPane();
        scroll2.setContent(box2);

        Scene scene2 = new Scene(scroll);
        Stage stage2 = new Stage();
        stage2.setTitle("Simulation 2");
        stage2.setScene(scene2);
        stage2.setX(1000);
        stage2.setY(0);
        stage2.show();
        Scene scene = new Scene(scroll2);
        stage.setTitle("Simulation 1");
        stage.setScene(scene);
        stage.setX(0);
        stage.setY(0);
        stage.show();


    }

    private void initForm1(Stage stage) {

        Label mapWidth = new Label("Map Width");
        Label mapHeight = new Label("Map Height");
        Label animalNum = new Label("Number of Animals");
        Label startEnergy = new Label("Start Energy");
        Label moveEnergy = new Label("Move Energy");
        Label plantEnergy = new Label("Plant Energy");
        Label jungleRatio = new Label("Jungle Ratio 1:_");
        TextField tf1 = new TextField();
        TextField tf2 = new TextField();
        TextField tf3 = new TextField();
        TextField tf4 = new TextField();
        TextField tf5 = new TextField();
        TextField tf6 = new TextField();
        TextField tf7 = new TextField();
        Button submit = new Button("Run Simulation");
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Integer[] args = {
                        Integer.parseInt(tf1.getText()),
                        Integer.parseInt(tf2.getText()),
                        Integer.parseInt(tf3.getText()),
                        Integer.parseInt(tf4.getText()),
                        Integer.parseInt(tf5.getText()),
                        Integer.parseInt(tf6.getText()),
                        Integer.parseInt(tf7.getText()),
                };
                if(tf7.getText().equals("0")) throw new IllegalArgumentException("Jungle ratio must be > 0: ");
                initWorld(stage, args);
            }
        });
        root = new GridPane();

        Scene scene = new Scene(root, 400, 500);
        root.addRow(0, mapWidth, tf1);
        root.addRow(1, mapHeight, tf2);
        root.addRow(2, animalNum, tf3);
        root.addRow(3, startEnergy, tf4);
        root.addRow(4, moveEnergy, tf5);
        root.addRow(5, plantEnergy, tf6);
        root.addRow(6, jungleRatio, tf7);
        root.addRow(7, submit);
        stage.setScene(scene);
        root.setAlignment(Pos.CENTER);
        stage.show();
    }

    @Override
    public void start(Stage stage) {
        initForm1(stage);
    }

    private void initView(RectangularMap map, List<List<Button>> view, int startRow, GridPane root,
                          SimulationEngine engine, ScheduledExecutorService[] executorService) {
        for (int i = 0; i <= map.height; i++) {
            List<Button> row = new CopyOnWriteArrayList<>();
            for (int j = 0; j <= map.width; j++) {
                Button pos = new Button("  ");
                row.add(pos);
                int finalI = i;
                int finalJ = j;
                pos.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        if (executorService[0].isShutdown()) {
                            engine.onClickEvent(finalI, finalJ);
                        }
                    }
                });
                root.addRow(i + 1 + startRow, pos);
            }
            view.add(row);
        }
    }


    private void addSimulationControl(ScheduledExecutorService[] executorService, SimulationEngine engine,
                                      int row, GridPane root) {
        Button stop = new Button("Stop");
        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                executorService[0].shutdown();
            }
        });
        root.addRow(row, stop);

        Button start = new Button("Start");
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (executorService[0].isShutdown()) {
                    executorService[0] = Executors.newSingleThreadScheduledExecutor();
                    executorService[0].scheduleAtFixedRate(engine::runSimulation,
                            100, PERIOD, TimeUnit.MILLISECONDS);
                }
            }
        });
        root.addRow(row, start);

        Button save = new Button("Save stats to file");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    engine.writeStatsToFile("statistics.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        root.addRow(row, save);
    }
}