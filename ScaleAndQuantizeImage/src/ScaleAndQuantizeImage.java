import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;

class ProcessImageFrame extends JFrame {
  public Scale imageScale;
  public Quantize imageQuantize;
  public BufferedImage image;
  
  private JLabel label; 
  private JFileChooser chooser; 
  private String imagePath;
  private String fileName; 
  private static final int DEFAULT_WIDTH = 600;
  private static final int DEFAULT_HEIGHT = 600;

  public ProcessImageFrame() throws IOException {
    
    setTitle("Scale and Quantize Image");
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
    ProcessImage(menubar);
  }

  public BufferedImage ToBufferedImage(String imagePath) throws IOException {
    return ImageIO.read(new File(imagePath));
  }

  public void SaveScaleImage(String width, String height) throws IOException {
    int w = Integer.parseInt(width);
    int h = Integer.parseInt(height);
    imageScale = new Scale(image, imagePath, w, h);
    label.setIcon(new ImageIcon(imagePath + width + "-" + height + ".png"));
  }

  public void SaveQuantizeImage(String level) throws IOException {
    int l = Integer.parseInt(level);
    imageQuantize = new Quantize(image, imagePath, l);
    label.setIcon(new ImageIcon(imagePath + level + "-level.png"));
  }

  public void AddFileMenu(JMenuBar menubar) {
    JMenu menu = new JMenu("File"); 
    menubar.add(menu);

    JMenuItem openItem = new JMenuItem("Open");
    JMenuItem exitItem = new JMenuItem("Close");

    openItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int result = chooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION){
          String imagePathName = chooser.getSelectedFile().getPath();
          try {
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
  public void ProcessImage(JMenuBar menubar) {
    JMenu menu = new JMenu("ProcessImage"); 
    menubar.add(menu);
    JMenuItem scaleItem = new JMenuItem("Scale");
    JMenuItem quantizeItem = new JMenuItem("Quantize");

    scaleItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        JDialog jdl = new JDialog();
        jdl.setBounds(700, 400, 150, 150);
        jdl.setTitle("setSize");
        jdl.getContentPane().setLayout(new GridLayout(3, 2));
        jdl.add(new JLabel("Width"));
        JTextField jtfW = new JTextField(60);
        jdl.add(jtfW);
        jdl.add(new JLabel("Height"));
        JTextField jtfH = new JTextField(60);
        jdl.add(jtfH);
        JButton jButton = new JButton("Ok");

        jButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            String width = jtfW.getText();
            String height = jtfH.getText();
            try {
				      SaveScaleImage(width, height);
			      } catch (IOException e) {
				      // TODO Auto-generated catch block
				      e.printStackTrace();
			      }
            jdl.dispose();
          }
        });
        jdl.add(jButton);
        jdl.setModal(true);
        jdl.setVisible(true);
      }
    });

    quantizeItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        JDialog jdl = new JDialog();
        jdl.setBounds(700, 400, 150, 150);
        jdl.setTitle("setLevel");
        jdl.getContentPane().setLayout(new GridLayout(2, 2));
        jdl.add(new JLabel("Level"));
        JTextField jtfL = new JTextField(60);
        jdl.add(jtfL);
        
        JButton jButton = new JButton("Ok");

        jButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            String level = jtfL.getText();
            try {
				      SaveQuantizeImage(level);
			      } catch (IOException e) {
				    // TODO Auto-generated catch block
				      e.printStackTrace();
			      }
            jdl.dispose();
          }
        });
        jdl.add(jButton);
        jdl.setModal(true);
        jdl.setVisible(true);
      }
    });
    menu.add(scaleItem);
    menu.add(quantizeItem);
  }
}

public class ScaleAndQuantizeImage {
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