import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;

/**
 * Catch flying objects by engulfing them in a detected region
 * Scaffold for PS-1
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class Catch extends JFrame {
	// You may change the parameters and instance variables as appropriate
	private static final int width=300, height=300;		// camera size (keep it small, for processing)
	private static final int minRegion = 50; 					// how many points in a region to be worth considering
	private static final int maxColorDiff = 20;				// how similar each color channel must be for colors to be considered similar
	private static final int speed = 5;								// flier step size control

	private CvMat image;									// the image grabbed from the webcam
	private Canvas canvas;								// drawing component
	private Grabby grabby;								// handles the image grabbing
	private Color trackColor = null;			// color of regions of interest (set by mouse press)
	private ArrayList<Region> regions;		// regions detected in the image
	private Flier flier = null;						// it's flying once mouse is pressed

	public Catch() {
		super("Webcam");

		// Create our graphics-handling component.
		canvas = new Canvas();

		// Set the size as specified.
		setSize(width, height);		

		// Create a grabber.
		grabby = new Grabby();
		grabby.execute();

		// Boilerplate to finish initializing the GUI.
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		getContentPane().add(canvas);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * An animated object flying across the scene
	 * Detects when it is in a region or has left the window
	 */
	private class Flier {
		// YOUR CODE HERE
	}

	/**
	 * A region is just a list of points here
	 */
	private class Region {
		public ArrayList<Point> points;
		public Region() {
			points = new ArrayList<Point>();
		}

		public boolean contains(int x, int y) {
			for (Point p : points) {
				if (p.x == x && p.y == y) {
					return true;
				}
			}
			return false;
		}
	}	

	/**
	 * Updates regions with those detected in the current image according to the current tracking color.
	 */
	private void findRegions() {
		// YOUR CODE HERE
	}

	/**
	 * Recolors image so that each region is a random uniform color, so we can see where they are
	 */
	private void recolor() {
		// YOUR CODE HERE
	}

	private class Canvas extends JComponent {
		public Canvas() {
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					// Set tracking color
					int i = event.getPoint().y;
					int j = event.getPoint().x;
					trackColor = new Color((int)image.get(i,j,2), (int)image.get(i,j,1), (int)image.get(i,j,0));
					// Start object flying
					// YOUR CODE HERE
				}
			});
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image == null) return;

			g.drawImage(image.asIplImage().getBufferedImage(), 0, 0, null);
			// Draw flying object
			// YOUR CODE HERE
		}
	}

	/**
	 * Handles grabbing an image from the webcam (following JavaCV examples)
	 * storing it in grabbed, and calling any processing function.
	 */
	private class Grabby extends SwingWorker<Void, Void> {
		private FrameGrabber grabber;
		protected Void doInBackground() throws Exception {
			// Set up webcam
			try {
				grabber = FrameGrabber.createDefault(0);
				grabber.setImageWidth(width);
				grabber.setImageHeight(height);
				grabber.start();
				image = grabber.grab().asCvMat();	
			} catch (Exception e) {
				try {
					if (grabber != null) grabber.release();
					grabber = new OpenCVFrameGrabber(0);
					grabber.setImageWidth(getWidth());
					grabber.setImageHeight(getHeight());
					grabber.start();
					image = grabber.grab().asCvMat();
				} catch (Exception e2) {
				}
			}
			while (!isCancelled()) {
				image = grabber.grab().asCvMat();
				// Detect regions, fly the object, etc.
				if (trackColor != null) {
					// YOUR CODE HERE
				}
				canvas.repaint();
				// Slow down the grabbing
				Thread.sleep(100);
			}
			// All done; clean up
			grabber.stop();
			grabber.release();
			grabber = null;
			return null;
		}
	}

	/**
	 * Gets the ball rolling by creating an instance of our class, 
	 * using SwingUtilities boilerplate to schedule the task of initializing the GUI.
	 * @param args ignored
	 */	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Catch cam = new Catch();
			}
		});
	}
}