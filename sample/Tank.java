package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;
import java.io.Serializable;

public class Tank extends MyPlayer implements Serializable{
    private final File tankUp = new File("src/images/green-tank-up.gif");
    private final File tankDown = new File("src/images/green-tank-down.gif");
    private final File tankLeft = new File("src/images/green-tank-left.gif");
    private final File tankRight = new File("src/images/green-tank-right.gif");
    private Image image = new Image(tankUp.toURI().toString());
    private final ImageView tankView = new ImageView(image);
    private Position position;
    private int size = 0;
    private final Map map;
    private int direction = 1;
    private int tankLives = 5;
    private TranslateTransition transition;

    Tank(Map map) {
        this.map = map;
        for (int i = 0; i < map.getSize(); i++) {
            for (int j = 0; j < map.getSize(); j++) {
                if (map.getValueAt(i, j) == 'P') {
                    position = new Position(j, i);
                }
            }
        }
    }



    public ImageView getTank() {
        tankView.setFitHeight(size);
        tankView.setFitWidth(size);
        return tankView;
    }

    public void setSize(int x) {
        size = x;
    }

    public int getSize() {
        return size;
    }

    public void moveRight(Tank tank) {
        int x = position.getX();
        int y = position.getY();
        tank.setTankImage(tankRight);
        direction = 2;
        if (x + 1 <= map.getSize() - 1) {
            switch (map.getValueAt(y, x + 1)) {
                case 'T':
                    Platform.runLater(() -> tank.getTank().toBack());
                case '0':
                case 'P':
                    System.out.println("Right");
                    if (map.getValueAt(y, x + 1) != 'T') {
                        Platform.runLater(() -> tank.getTank().toFront());
                    }
                    transition = new TranslateTransition(Duration.millis(100), tank.getTank());
                    transition.setByX(tank.getSize());
                    transition.play();
                    try {
                        Thread.sleep(90);
                        map.setElement('0', y, x);
                        map.setElement('P', y, x + 1);
                        position.setX(x + 1);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Invalid Position!");
            }
        }
    }

    public void moveUp(Tank tank) {
        int x = position.getX();
        int y = position.getY();
        tank.setTankImage(tankUp);
        direction = 1;
        if (y - 1 >= 0) {
            switch (map.getValueAt(y - 1, x)) {
                case 'T':
                    Platform.runLater(() -> tank.getTank().toBack());
                case '0':
                case 'P':
                    System.out.println("Up");
                    if (map.getValueAt(y - 1, x) != 'T') {
                        Platform.runLater(() -> tank.getTank().toFront());
                    }
                    transition = new TranslateTransition(Duration.millis(100), tank.getTank());
                    transition.setByY((-1) * tank.getSize());
                    transition.play();
                    try {
                        Thread.sleep(90);
                        map.setElement('0', y, x);
                        map.setElement('P', y - 1, x);
                        position.setY(y - 1);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Invalid Position!");
            }
        }
    }

    public void moveDown(Tank tank) {
        int x = position.getX();
        int y = position.getY();
        tank.setTankImage(tankDown);
        direction = 3;
        if (y + 1 <= map.getSize() - 1) {
            switch (map.getValueAt(y + 1, x)) {
                case 'T':
                    Platform.runLater(() -> tank.getTank().toBack());
                case '0':
                case 'P':
                    System.out.println("Down");
                    if (map.getValueAt(y + 1, x) != 'T') {
                        Platform.runLater(() -> tank.getTank().toFront());
                    }
                    transition = new TranslateTransition(Duration.millis(100), tank.getTank());
                    transition.setByY(tank.getSize());
                    transition.play();
                    try {
                        Thread.sleep(90);
                        map.setElement('0', y, x);
                        map.setElement('P', y + 1, x);
                        position.setY(y + 1);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Invalid Position!");
            }
        }
    }

    public void moveLeft(Tank tank) {
        int x = position.getX();
        int y = position.getY();
        tank.setTankImage(tankLeft);
        direction = 4;
        if (x - 1 >= 0) {
            switch (map.getValueAt(y, x - 1)) {
                case 'T':
                    Platform.runLater(() -> tank.getTank().toBack());
                case '0':
                case 'P':
                    System.out.println("Left");
                    if (map.getValueAt(y, x - 1) != 'T') {
                        Platform.runLater(() -> tank.getTank().toFront());
                    }
                    transition = new TranslateTransition(Duration.millis(100), tank.getTank());
                    transition.setByX((-1) * tank.getSize());
                    transition.play();
                    try {
                        Thread.sleep(90);
                        map.setElement('0', y, x);
                        map.setElement('P', y, x - 1);
                        position.setX(x - 1);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Invalid Position!");
            }
        }
    }

    private void setTankImage(File file) {
        image = new Image(file.toURI().toString());
        tankView.setImage(image);
    }

    public int getTankDirection() {
        return direction;
    }

    public Position getTankPosition() {
        return position;
    }

    public int getTankLives() {
        return tankLives;
    }

    public Map getMap(){
        return map;
    }

    public void setPosition(Position position){
        this.position = position;
    }

}