import java.awt.Color;  
import java.awt.Graphics2D;  
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO; 

public class DrawHistogramImage {
  private BufferedImage image;
  private BufferedImage histogramImage;  
  private int size = 270;

  public DrawHistogramImage(BufferedImage img, String s) throws IOException {
    image = img;
    getHistogram();
    showImage(histogramImage, s);
  }
  public void getHistogram() {
    int height = image.getHeight(), width = image.getWidth();
    int[] intensity = new int[256];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int gray = image.getRGB(j, i) & 0xFF;
        intensity[gray]++;
      }
    }
    histogramImage = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g2d = histogramImage.createGraphics();  
    g2d.setPaint(Color.BLACK);
    g2d.fillRect(0, 0, size, size);
    g2d.setPaint(Color.WHITE);
    g2d.drawLine(5, 250, 265, 250);
    g2d.drawLine(5, 250, 5, 5);
    // scale to 200  
    g2d.setPaint(Color.GREEN);
    int max = findMaxValue(intensity);
    float rate = 200.0f/((float)max);  
    int offset = 2;  
    for(int i = 0; i < intensity.length; i++) {
      int frequency = (int)(intensity[i] * rate);
      g2d.drawLine(5 + offset + i, 250, 5 + offset + i, 250 - frequency);
    }
      
    // X Axis Gray intensity  
    g2d.setPaint(Color.RED);
    g2d.drawString("Gray Intensity", 100, 270);
  }

  private int findMaxValue(int[] intensity) {
    int max = -1;
    for(int i = 0; i < intensity.length; i++) {
      if(max < intensity[i]) {
        max = intensity[i];
      }
    }
    return max;
  }
  public void showImage(BufferedImage img, String s) throws IOException {
    OutputStream output = new FileOutputStream("img/Histogram" + s);
    ImageIO.write(img, "png", output);
  }
  // public static void main(String[] args) throws IOException {
  //   new DrawHistogramImage(ImageIO.read(new File("E:\\78.png")));
  // }
}
