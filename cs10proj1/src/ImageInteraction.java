import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * Some interaction with images, built on JavaCV representations.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class ImageInteraction extends JFrame {
	private CvMat image; 											// what to display and interact with
	private Canvas canvas; 										// our component for handling the graphics
	private int mi = 0, mj = 0; 							// last observed mouse coordinates
	private static final int lensRadius = 50; // for lens effect

	public ImageInteraction() {
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
		// (Here, add 70 extra pixels on rows for RGB display.)
		setSize(image.cols(), image.rows() + 70);

		// Boilerplate to finish initializing the GUI.
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		getContentPane().add(canvas);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Returns a value that is one of val (if it's between min or max) or min or
	 * max (if it's outside that range).
	 * 
	 * @param val
	 * @param min
	 * @param max
	 * @return constrained value
	 */
	private static double constrain(double val, double min, double max) {
		if (val < min) {
			return min;
		}
		else if (val > max) {
			return max;
		}
		return val;
	}

	/**
	 * Updates the image with a lens magnifying effect
	 * 
	 * @param row		center row of lens
	 * @param col		center column of lens
	 */
	private void lens(int row, int col) {
		// Start with a copy of the image.
		CvMat result = image.clone();
		for (int i = 0; i < image.rows(); i++) {
			for (int j = 0; j < image.cols(); j++) {
				// Only do lens out to specified radius.
				double dist = Math.sqrt((i - row) * (i - row) + (j - col) * (j - col)) / lensRadius;
				if (dist <= 1) {
					for (int c = 0; c < 3; c++) {
						// Determine neighbor row (ni) and column (nj) by lens function,
						// but constrain to image size.
						int ni = (int) constrain(row + ((i - row) * dist), 0, image.rows() - 1);
						int nj = (int) constrain(col + ((j - col) * dist), 0, image.cols() - 1);
						result.put(i, j, c, image.get(ni, nj, c));
					}
				}
			}
		}
		image = result;
	}

	/**
	 * A component to handle displaying and interacting with images (and other stuff).
	 */
	private class Canvas extends JComponent {
		public Canvas() {
			// Listen for mouse presses.
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					System.out.println(event);
					// Print out color at position
					// Note swapping order between (x,y) and (i,j)
					int i = event.getPoint().y;
					int j = event.getPoint().x;
					// Recall BGR order of colors.
					System.out.println(image.get(i, j, 0) + "," + image.get(i, j, 1) + "," + image.get(i, j, 2));
					// Lens effect
					lens(i, j);
					// Need to redraw since image is modified
					repaint();
				}
			});

			// Listen for mouse motion.
			addMouseMotionListener(new MouseAdapter() {
				public void mouseMoved(MouseEvent event) {
					// Remember where the mouse is, and then update the display.
					mi = event.getPoint().y;
					mj = event.getPoint().x;
					repaint();
				}
			});
		}

		/**
		 * Draws this component, by asking the superclass to do its part and then
		 * doing the drawing we want to do.
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// The image.
			g.drawImage(image.asIplImage().getBufferedImage(), 0, 0, null);
			// The moused-over color and its BGR breakdown.
			// Note that Color wants RGB
			g.setColor(new Color((int) image.get(mi, mj, 2), (int) image.get(mi, mj, 1), (int) image.get(mi, mj, 0)));
			g.fillRect(10, image.rows() + 10, 50, 50);
			g.setColor(new Color(0, 0, (int) image.get(mi, mj, 0)));
			g.fillRect(110, image.rows() + 10, 50, 50);
			g.setColor(new Color(0, (int) image.get(mi, mj, 1), 0));
			g.fillRect(210, image.rows() + 10, 50, 50);
			g.setColor(new Color((int) image.get(mi, mj, 2), 0, 0));
			g.fillRect(310, image.rows() + 10, 50, 50);
		}
	}

	/**
	 * Gets the ball rolling by creating an instance of our class, using
	 * SwingUtilities boilerplate to schedule the task of initializing the GUI.
	 * 
	 * @param args	ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ImageInteraction frame = new ImageInteraction();
			}
		});
	}
}