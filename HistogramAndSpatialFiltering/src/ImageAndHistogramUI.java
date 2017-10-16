import java.awt.EventQueue;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;

class ProcessImageFrame extends JFrame {
  public BufferedImage image;
  
  private JLabel label; 
  private JFileChooser chooser; 
  private static final int DEFAULT_WIDTH = 500;
  private static final int DEFAULT_HEIGHT = 500;
  private String imagePath;
  private String fileName; 

  public ProcessImageFrame() throws IOException {
    new HistogramAndSpatialImage();
    
    setTitle("HistogramAndSpatialImage");
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

    label = new JLabel();
    add(label);
    chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File(".")); 

    JMenuBar menubar = new JMenuBar(); 
    setJMenuBar(menubar);
    /*center*/
    setLocationRelativeTo(null);
    AddFileMenu(menubar);
    AddHistogramMenu(menubar);
  }

  public BufferedImage ToBufferedImage(String imagePath) throws IOException {
    return ImageIO.read(new File(imagePath));
  }

  public void AddFileMenu(JMenuBar menubar) {
    JMenu menu = new JMenu("File"); 
    menubar.add(menu);

    JMenuItem openItem = new JMenuItem("OpenFile");
    JMenuItem exitItem = new JMenuItem("Close");

    openItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int result = chooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
          String imagePathName = chooser.getSelectedFile().getPath();
          try {
            // System.out.println(imagePathName);
            image = ToBufferedImage(imagePathName);
          } catch (IOException e) {
          // TODO Auto-generated catch block
            e.printStackTrace();
          }
          imagePath = imagePathName.substring(0, imagePathName.lastIndexOf('\\') + 1);
          fileName = imagePathName.substring(imagePath.lastIndexOf('\\') + 1);
          label.setIcon(new ImageIcon(imagePathName));
        }
      }
    });

    exitItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        System.exit(0);
      }
    });
    menu.add(openItem);
    menu.add(exitItem);
  }

  public void AddHistogramMenu(JMenuBar menubar) {
    JMenu menu = new JMenu("HistogramImage"); 
    menubar.add(menu);

    JMenuItem histogram = new JMenuItem("Histogram");

    histogram.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        try {
			    new DrawHistogramImage(image, fileName);
		    } catch (IOException e) {
			  // TODO Auto-generated catch block
			    e.printStackTrace();
		    }
        label.setIcon(new ImageIcon("img/Histogram" + fileName));
      }
    });
    menu.add(histogram);
  }
}

public class ImageAndHistogramUI {
  public static void main(String[] args) throws IOException {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame frame = null;
        try {
          frame = new ProcessImageFrame();
        } catch (IOException e) {
          e.printStackTrace();
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
      }
    });
  }
}