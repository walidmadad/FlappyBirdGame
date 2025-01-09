import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;
    double score = 0;

    //Images
    Image backgroundImg, birdImg, bottomPipeImg, topPipeImg;

    //game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver= false;
    boolean isGameStarted = false;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird = new Bird(boardWidth, boardHeight, birdImg);
        pipes = new ArrayList<Pipe>();
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer

        gameLoop = new Timer(1000/60,this);
        gameLoop.start();

    }
    public void placePipes(){
        // (0-1) * pipeHeight/2 -> (0-256)
        //128
        // 0- 128 - (0-256 --> pipeHeight/4 -> 3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace ;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // Draw bird
        g.drawImage(bird.birdImg, bird.birdXpos, bird.birdYpos, bird.birdWidth, bird.birdHeight, null);

        // Draw pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (!isGameStarted) {
            g.drawString("Press SPACE to start", boardWidth / 8, boardHeight / 2);
        } else if (gameOver) {
            g.drawString("Score: " + (int) score, boardWidth / 8, boardHeight / 2 - 50);
            g.drawString("Press SPACE to play", boardWidth / 10, boardHeight / 2 + 50);
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.PLAIN, 45));
            g.drawString("Game Over", boardWidth / 8, boardHeight / 2);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }


    public void move(){
        velocityY += gravity;
        bird.birdYpos += velocityY;
        bird.birdYpos = Math.max(bird.birdYpos, 0);


        for (int i = 0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.birdXpos > pipe.x + pipe.width){
                pipe.passed=true;
                score += 0.5;
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.birdYpos >boardHeight){
            gameOver = true;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(isGameStarted) {
            move();
            repaint();
        }
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    public boolean collision(Bird a, Pipe b){
        return  a.birdXpos < b.x + b.width &&
                a.birdXpos + a.birdWidth > b.x &&
                a.birdYpos < b.y + b.height &&
                a.birdYpos + a.birdHeight > b.y;

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!isGameStarted) {
                isGameStarted = true;
            }
            if (!gameOver) {
                velocityY = -9;
            } else {
                bird.birdYpos = boardWidth / 2;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                isGameStarted = true;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
