import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Quantize {
  public BufferedImage image;
  public Quantize(BufferedImage img, String outputImagePath, int level) throws IOException {
    testExample(img, outputImagePath, level);
  }
  public int changeLevelARGBToGray(int gray, int gap, int level) {
    /*保存透明度通道值*/
    int A = (gray >> 24) & 0xFF;
    int temp = gray & 0xFF;
    double n = 256 / level;
    temp = (int)(temp / n) * gap;
    /*还原灰度图*/
    int grayARGB = A << 24 | temp << 16 | temp << 8| temp;
    return grayARGB;
  }
  public BufferedImage quantizeImage(BufferedImage img, int level) throws IOException {
    int gap = (int) (255 / (level - 1));
    int width = img.getWidth();
    int height = img.getHeight();
    
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int grayARGB = img.getRGB(j, i);
        img.setRGB(j, i, changeLevelARGBToGray(grayARGB, gap, level));
      }
    }
    return img;
  }
  public void testExample(BufferedImage img, String outputImagePath, int level) throws IOException {
    BufferedImage t1 = quantizeImage(img, level);
    outputImage(t1, outputImagePath, level);
  }
  public void outputImage(BufferedImage img, String outputImagePath, int level) throws IOException {
    OutputStream output = new FileOutputStream(outputImagePath + String.valueOf(level)+ "-level.png");
    ImageIO.write(img, "png", output);
  }
}