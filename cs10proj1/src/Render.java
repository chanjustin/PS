import java.awt.*;
import javax.swing.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * Custom rendering of an image, by drawing "pixels"
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class Render extends JFrame {
	private CvMat image;		// what to display and interact with
	private Canvas canvas;	// our component for handling the graphics

	private static final int pixelSize = 20;	// size of the objects representing the image

	public Render() {
		// Make sure superclass (JFrame) is also properly initialized.
		super("Image Interaction");

		// Read an image and make sure it was successfully loaded.
		image = cvLoadImageM("img/dart0.jpg");
		if (image == null) {
			System.out.println("image not found!");
			System.exit(1);
		}

		// Create our graphics-handling component.
		canvas = new Canvas();

		// Make the window size match the image size.
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
	 * Renders the image as a set of rectangles tiling the window.
	 * @param g
	 */
	private void mosaic(Graphics g) {
		// Usual loops, but step by "pixel" size and draw a rectangle of the appropriate color.
		// Also note <=, to get that last rectangle.
		for (int i = 0; i <= image.rows() - pixelSize; i += pixelSize) {
			for (int j = 0; j <= image.cols() - pixelSize; j += pixelSize) {
				for (int c=0; c<3; c++) {
					g.setColor(new Color((int)image.get(i,j,2), (int)image.get(i,j,1), (int)image.get(i,j,0)));
					g.fillRect(j, i, pixelSize, pixelSize);										
					g.setColor(Color.black);
					g.drawRect(j, i, pixelSize, pixelSize);
				}
			}
		}
	}

	/**
	 * Renders the image as a set of ellipses at random positions.
	 * @param g
	 */
	private void pointillism(Graphics g) {
		// Draw some random number of points determined by the image and "pixel" sizes.
		int numPoints = image.cols() * image.rows() / pixelSize;
		for (int p=0; p<numPoints; p++) {
			// Pick a random position and size
			int i = (int) (Math.random() * image.rows());
			int j = (int) (Math.random() * image.cols());
			int s = (int) (Math.random() * pixelSize) + 1;

			// Draw an ellipse there, colored by the pixel's color
			g.setColor(new Color((int)image.get(i,j,2), (int)image.get(i,j,1), (int)image.get(i,j,0)));
			g.fillOval(j, i, s, s);										
		}
	}

	/**
	 * A component to handle displaying things.
	 */
	private class Canvas extends JComponent {
		/**
		 * Draws this component, by asking the superclass to do its part
		 * and then doing the drawing we want to do.
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// Uncomment one or the other.
			mosaic(g);
			//pointillism(g);
		}
	}

	/**
	 * Gets the ball rolling by creating an instance of our class, 
	 * using SwingUtilities boilerplate to schedule the task of initializing the GUI.
	 * @param args	ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Render frame = new Render();
			}
		});
	}
}