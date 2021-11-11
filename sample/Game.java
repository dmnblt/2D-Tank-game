package sample;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Game {
    private final Map map;
    static Scene sceneMain;
    private final Tank tank;
    final GridPane fieldPane = new GridPane();
    final Barriers barriers = new Barriers();
    Bullet bullet;
    Bot bot;
    Socket socket = new Socket("localhost", 8000);
    ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
    ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());


    Game(Map map, Tank tank) throws IOException {
        this.tank = tank;
        this.map = map;
        bullet = new Bullet(map, tank.getTankDirection());
    }

    public VBox startVBox(Stage primaryStage) throws FileNotFoundException {
        VBox start = new VBox(20);
        start.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        start.setSpacing(50);
        Text text = new Text("Tanki online");
        text.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 104));
        text.setStroke(Color.CORNFLOWERBLUE);
        text.setFill(Color.WHITE);
        Text strBtn = new Text("Start");
        strBtn.setFill(Color.WHITE);
        strBtn.setFont(Font.font(String.valueOf(FontWeight.BOLD),80));
        strBtn.setLayoutX(200);
        strBtn.setLayoutY(90);
        Button multiplayerButton = new Button("Multiplayer");
        multiplayerButton.setMaxHeight(90);
        multiplayerButton.setMaxWidth(200);
        multiplayerButton.setMinHeight(90);
        multiplayerButton.setMinWidth(200);
        start.setAlignment(Pos.CENTER);
        start.getChildren().addAll(text, multiplayerButton,strBtn);
        sceneMain = new Scene(gameVBox(), 650, 600);

        strBtn.setOnMouseClicked(e -> {
            primaryStage.setScene(sceneMain);
            addPlayerActions();
            addBotsActions();
        });
        multiplayerButton.setOnMouseClicked(e -> {
            primaryStage.setScene(sceneMain);
            addPlayerActions();
            try {
                multiplayer();
            } catch (ClassNotFoundException | IOException ioException) {
                ioException.printStackTrace();
            }
        });
        return start;
    }

    public BorderPane gameVBox() {
        BorderPane pane = new BorderPane();
        pane.setMaxSize(650, 600);
        pane.setMinSize(650, 600);
       //Text text = new Text("TANK:\n" + new Date().getDate());
       Text text = new Text("TANK:\n" + "num1\nMaxBots: "+ 5);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        text.setTextAlignment(TextAlignment.CENTER);
        fieldPane.setMaxSize(500, 500);
        fieldPane.setMinSize(500, 500);
        pane.setCenter(fieldPane);
        pane.setRight(text);
        fieldPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        addElements();
        return pane;
    }

    public void addPlayerActions() {
        sceneMain.setOnKeyPressed(e -> {
            Runnable movement = null;
            String action = "";
            switch (e.getCode()) {
                case UP:
                case W:
                    movement = new TankMovement(tank, "up");
                    action = "up";
                    break;
                case DOWN:
                case S:
                    movement = new TankMovement(tank, "down");
                    action = "down";
                    break;
                case LEFT:
                case A:
                    movement = new TankMovement(tank, "left");
                    action = "left";
                    break;
                case RIGHT:
                case D:
                    movement = new TankMovement(tank, "right");
                    action = "right";
                    break;
                case SPACE:
                    bullet.fire(tank, fieldPane);
                    System.out.println("Fire!");
                    break;
            }

            try {
                toServer.writeObject(action);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            Thread thread1 = new Thread(movement);
            if (thread1.isAlive()) {
                thread1.interrupt();
            }
            thread1.start();
        });
        sceneMain.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                bullet.fire(tank, fieldPane);
                System.out.println("Fire!");
            }
        });
    }

    public void addBotsActions() {
        bot = new Bot(map);
        bot.setSize(barriers.getSize());
        BotBullet botBullet = new BotBullet(map, bot.getBotDirection(), bot, fieldPane, tank);
        fieldPane.add(bot.getBotTank(), bot.getPosition().getX(), bot.getPosition().getY());
        Runnable runnable = new BotMovement(bot, tank);
        Thread threadBot = new Thread(runnable);
        threadBot.start();
        botBullet.start();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Bot newBot = new Bot(map);
                newBot.setSize(barriers.getSize());
                BotBullet botBullet = new BotBullet(map, newBot.getBotDirection(), newBot, fieldPane, tank);
                Platform.runLater(() -> fieldPane.add(newBot.getBotTank(), newBot.getPosition().getX(), newBot.getPosition().getY()));
                BotMovement movement = new BotMovement(newBot, tank);
                movement.start();
                botBullet.start();
            }
        }, 20000, 20000);
    }

    public void addElements() {
        barriers.setSize(map.getSize());
        tank.setSize(barriers.getSize());
        for (int i = 0; i < map.getSize(); i++) {
            for (int j = 0; j < map.getSize(); j++) {
                switch (map.getValueAt(i, j)) {
                    case 'S':
                        fieldPane.add(barriers.getSteel(), j, i);
                        break;
                    case 'B':
                        fieldPane.add(barriers.getBrick(), j, i);
                        break;
                    case 'W':
                        fieldPane.add(barriers.getWater(), j, i);
                        break;
                    case 'T':
                        fieldPane.add(barriers.getTrees(), j, i);
                        break;
                    case '0':
                        fieldPane.add(barriers.getTransparent(), j, i);
                        break;
                    case 'P':
                        fieldPane.add(tank.getTank(), j, i);
                        break;
                }
            }
        }
    }

    public void multiplayer() throws IOException, ClassNotFoundException{

        toServer.writeObject(map);
        toServer.writeObject(tank.getTankPosition());
        toServer.flush();
        int tanksNumber = fromServer.readInt();

    }

    public void addPlayer() throws IOException, ClassNotFoundException {
        Tank newTank = new Tank(map);
        newTank.setPosition((Position) fromServer.readObject());
        newTank.setSize(barriers.getSize());
        map.setElement('P', newTank.getPosition().getY(), newTank.getPosition().getX());
        fieldPane.add(newTank.getTank(), newTank.getPosition().getX(), newTank.getPosition().getY());
    }

}
