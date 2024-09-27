import java.awt.*;

public class Bird {
    int birdXpos;
    int birdYpos;
    int birdHeight = 24;
    int birdWidth = 34;
    Image birdImg;

    Bird(int boardWidth, int boradHeight, Image img){
        birdImg = img;
        birdXpos = boardWidth/8;
        birdYpos = boradHeight/2;
    }
}
