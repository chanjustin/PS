import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * Presentation of a list of images.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class Slideshow extends JFrame {
	private ArrayList<CvMat> images;	// the slides
	private Canvas canvas; 						// our component for handling the graphics
	private int curr = 0;							// current slide number

	public Slideshow() {
		// Make sure superclass (JFrame) is also properly initialized.
		super("SlideShow");

		// Read the images, named dart0.jpg ... dart8.jpg, and store in list.
		images = new ArrayList<CvMat>();
		for (int i = 0; i <= 8; i++) {
			CvMat image = cvLoadImageM("img/dart" + i + ".jpg");
			if (image == null) {
				System.out.println("image " + i + " not found!");
				System.exit(1);
			}
			images.add(image);
		}

		// Create our graphics-handling component.
		canvas = new Canvas();

		// Make the window size match the image size. Assumes they're all the same size.
		setSize(images.get(0).cols(), images.get(0).rows());

		// Boilerplate to finish initializing the GUI.
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		getContentPane().add(canvas);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Advances to the next slide.
	 */
	private void advance() {
		curr = (curr + 1) % images.size(); // use modular arithmetic to wrap around to 0
		canvas.repaint();
	}

	/**
	 * A component to handle displaying and interacting with images (and other stuff).
	 */
	private class Canvas extends JComponent {
		public Canvas() {
			// Listen for mouse presses.
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					advance();
				}
			});
		}

		/**
		 * Draws this component, by asking the superclass to do its part and then
		 * doing the drawing we want to do.
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// Draw whichever image is #curr in the list.
			g.drawImage(images.get(curr).asIplImage().getBufferedImage(), 0, 0, null);
		}
	}

	/**
	 * Gets the ball rolling by creating an instance of our class, using
	 * SwingUtilities boilerplate to schedule the task of initializing the GUI.
	 * @param args	ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Slideshow frame = new Slideshow();
			}
		});
	}
}