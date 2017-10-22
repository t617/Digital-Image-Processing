import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Result {
  public Result(BufferedImage img) throws IOException {
    int height = img.getHeight();
    int width = img.getWidth();
    // new DiscreteFourierTransform(img);
    new FastFourierTransform(img);
    int[][] paddingImage = FastFourierTransform.getPaddingImage(img);
    double[][][] fourierArray = FastFourierTransform.fft_2d(paddingImage);
    double[][][] afterFilterArray = new FreqencyFiltering().getAfterFilter(7,  fourierArray);
    BufferedImage image = FastFourierTransform.ifft_2d(afterFilterArray, height, width); 
    showImage(image, "filter");
  }
  public static void main(String[] args) throws IOException {
    BufferedImage img = ImageIO.read(new File("img/78.png"));
    new Result(img);
  }
  public void showImage(BufferedImage img, String s) throws IOException {
    ImageIO.write(img, "png", new FileOutputStream("img/" + s + ".png"));
    System.out.println("OK");
  }
}
