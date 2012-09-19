import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;

/**
 * Fun with the webcam, built on JavaCV
 * Tracks a color, as specified by mouse press
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class WebcamColorTracking extends JFrame {
	private CvMat image;				// the image grabbed from the webcam
	private Point where;				// point-tracking location
	private Color trackColor=Color.white; 	// point-tracking target color
	private Canvas canvas;			// drawing component
	private Grabby grabby;			// handles the image grabbing
	private static final int width=300, height=300;		// setup: camera size (keep it small, for processing)
	
	public WebcamColorTracking() {
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
	 * Determines which point is closest to the trackColor, puts it in this.where
	 */
	private void track() {
		int x = 0, y = 0; // coordinates with best matching color
		double closest = 10000; // start with a too-high number so that everything will be smaller
		for (int i = 0; i < image.rows(); i++) {
			for (int j = 0; j < image.cols(); j++) {
				// Euclidean distance squared between colors
				double d = (image.get(i,j,0) - trackColor.getBlue()) * (image.get(i,j,0) - trackColor.getBlue())
									 + (image.get(i,j,1) - trackColor.getGreen()) * (image.get(i,j,1) - trackColor.getGreen())
									 + (image.get(i,j,2) - trackColor.getRed()) * (image.get(i,j,2) - trackColor.getRed());
				if (d < closest) {
					closest = d;
					x = j; y = i;
				}
			}
		}
		where = new Point(x,y);
	}
	
	private class Canvas extends JComponent {
		public Canvas() {
			// Use mouse press to determine when the user wants to set the current image as background.
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					int i = event.getPoint().y;
					int j = event.getPoint().x;
					trackColor = new Color((int)image.get(i, j, 2), (int)image.get(i, j, 1), (int)image.get(i, j, 0));
					System.out.println(trackColor);
				}
			});
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.drawImage(image.asIplImage().getBufferedImage(), 0, 0, null);
			}
			if (where != null) {
				// Draw circle at tracked point, with border in the inverse color
				g.setColor(trackColor);
				g.fillOval(where.x, where.y, 15, 15);
				((Graphics2D)g).setStroke(new BasicStroke(4)); // thick border
				g.setColor(new Color(255-trackColor.getRed(), 255-trackColor.getGreen(), 255-trackColor.getBlue()));
				g.drawOval(where.x, where.y, 15, 15);
			}
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
			}
			catch (Exception e) {
				try {
					if (grabber != null) grabber.release();
					grabber = new OpenCVFrameGrabber(0);
					grabber.setImageWidth(getWidth());
					grabber.setImageHeight(getHeight());
					grabber.start();
					image = grabber.grab().asCvMat();
				}
				catch (Exception e2) {
				}
			}

			// Keep grabbing frames
			while (!isCancelled()) {
				image = grabber.grab().asCvMat();
				track();
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
	 * @param args	ignored
	 */	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WebcamColorTracking cam = new WebcamColorTracking();
			}
		});
	}
}