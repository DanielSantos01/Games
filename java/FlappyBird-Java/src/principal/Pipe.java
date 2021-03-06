package principal;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Pipe {
    private Image pipeUpImage;
    private Image pipeDownImage;
    private int verifyOut = 0, verifyPoint = 0;
    private final Settings settings;
    private final int[] x = new int[10];
    private final int pipeUpHight[] = {250, 100, 200, 150, 200, 350, 400, 150, 30, 20};
    private final int pipeDownHeight[] = {230, 380, 280, 330, 280, 130, 80, 330, 450, 460};
    private final Bird bird;
    private final Rectangle rect;
    private final Stats stats;
    
    public Pipe(Settings set, Bird brd, Stats stt) throws IOException{
        rect = new Rectangle();
        settings = set;
        configureImages();
        rect.width = pipeUpImage.getWidth(settings.canvas);
        bird = brd;
        stats = stt;
    }
    
    public void draw(Graphics g){
        if(settings.start || settings.gameOver) show(g);
        else if(settings.preStart && settings.configurePipes) configurePipes();
    }
    
    private void configurePipes(){
        
        for(int pipe = 0; pipe <= 9; pipe++) x[pipe] = 450 + 4*pipe*75;
        
        settings.configurePipes = false;
    }
    
    private void show(Graphics g){
        for(int pipe = 0; pipe <= 9; pipe++){
            showDownPipes(g, pipe);
            checkCollision();
            
            showUpPipes(g, pipe);
            checkCollision();
            
            updatePosition(pipe);
        }
        if(settings.start) {
            checkPoint();
            checkPipeOut();
        }
    }
    
    private void updatePosition(int pipe){
        if(!settings.gameOver) x[pipe] -= 5;
    }
    
    private void checkPoint(){
        if(x[verifyPoint] < (settings.screenWidth/2)){
            stats.score++;
            if(verifyPoint++ == 9) verifyPoint = 0;
        }
    }
    
    private void checkPipeOut(){
        if(x[verifyOut] <= -50){
            x[verifyOut] = 2950;
            if(verifyOut++ == 9) verifyOut = 0;
        }
    }
    
    private void showUpPipes(Graphics g, int pipe){
        rect.x = x[pipe];
        rect.y = 680 -110 - pipeUpHight[pipe];
        rect.height = pipeUpHight[pipe];
        g.drawImage(pipeUpImage, x[pipe], rect.y, rect.width, rect.height, settings.canvas);
    }
    
    private void showDownPipes(Graphics g, int pipe){
        rect.x = x[pipe];
        rect.y = 0;
        rect.height = pipeDownHeight[pipe];
        g.drawImage(pipeDownImage, x[pipe], rect.y, rect.width, rect.height, settings.canvas);
    }
    
    private void configureImages() throws IOException{
        pipeUpImage = ImageIO.read(getClass().getResource("..//assets//pipe-green.png"));
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -pipeUpImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        pipeDownImage = op.filter((BufferedImage)pipeUpImage, null);
    }
    
    public void checkCollision(){
        if(rect.intersects(bird.rect)) bird.fall("pipe");
    }
}
