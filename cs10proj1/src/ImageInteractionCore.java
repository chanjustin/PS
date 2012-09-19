import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * Some interaction with images, built on JavaCV representations.
 * This is just the core functionality upon which we will build
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class ImageInteractionCore extends JFrame {
	private CvMat image;		// what to display and interact with
	private Canvas canvas;	// our component for handling the graphics
	
	public ImageInteractionCore() {
		// Make sure superclass (JFrame) is also properly initialized.
		super("Image Interaction");

		// Read an image and make sure it was successfully loaded.
		image = cvLoadImageM("img/baker.jpg");
		if (image == null) {
			System.out.println("image not found!");
			System.exit(1);
		}

		// Create our graphics-handling component.
		canvas = new Canvas();
    
		// Make the window size match the image size.
		System.out.println(image.cols() + "*" + image.rows());
		setSize(image.cols(), image.rows());
 
		// Boilerplate to finish initializing the GUI.
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		getContentPane().add(canvas);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * A component to handle displaying and interacting with images (and other stuff).
	 */
	private class Canvas extends JComponent {
		public Canvas()
		{
			addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					System.out.println(e);
				}
			});
			
			addMouseMotionListener(new MouseAdapter()
			{
				public void mouseMoved(MouseEvent e)
				{
					System.out.println(e);
				}
			});
			
			
		}
		/**
		 * Draws this component, by asking the superclass to do its part
		 * and then doing the drawing we want to do.
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image.asIplImage().getBufferedImage(), 0, 0, null);
		}
	}
	
	/**
	 * Gets the ball rolling by creating an instance of our class, 
	 * using SwingUtilities boilerplate to schedule the task of initializing the GUI.
	 * @param args	ignored
	 */
	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				ImageInteractionCore frame = new ImageInteractionCore();
			}
		});
	}
}
