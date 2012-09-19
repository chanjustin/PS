import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;

/**
 * Fun with the webcam, built on JavaCV
 * Replaces background (as denoted by mouse press) with scenery
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class WebcamBg extends JFrame {
	private CvMat image;				// the image grabbed from the webcam
	private CvMat background;		// the stored background grabbed from the webcam
	private CvMat scenery;			// the replacement background
	private Canvas canvas;			// drawing component
	private Grabby grabby;			// handles the image grabbing
	private static final int width=300, height=300;		// setup: camera size (keep it small, for processing)
	private static final int bgDiff=250;							// setup: threshold for considering a pixel to be background
	
	public WebcamBg() {
		super("Webcam");

		// Create our graphics-handling component.
		canvas = new Canvas();

		// Load replacement background image.
		scenery = cvLoadImageM("img/baker.jpg");
		if (scenery == null) {
			System.out.println("image not found!");
			System.exit(1);
		}

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
	 * Updates image so that pixels that look like background are replaced with scenery.
	 */
	private void bgSubtract() {
		if (background != null) {
			for (int i = 0; i < image.rows(); i++) {
				for (int j = 0; j < image.cols(); j++) {
					double d = 0;
					// Euclidean distance squared between colors
					for (int c = 0; c < 3; c++) {
						d += (image.get(i, j, c) - background.get(i, j, c))
								* (image.get(i, j, c) - background.get(i, j, c));
					}
					if (d < bgDiff) {
						// Close enough to background, so replace
						for (int c = 0; c < 3; c++) {
							image.put(i, j, c, scenery.get(i, j, c));
						}
					}
				}
			}
		}
	}
	
	private class Canvas extends JComponent {
		public Canvas() {
			// Use mouse press to determine when the user wants to set the current image as background.
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					System.out.println("background set!");
					background = image.clone();
				}
			});
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.drawImage(image.asIplImage().getBufferedImage(), 0, 0, null);
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
				bgSubtract();
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
				WebcamBg cam = new WebcamBg();
			}
		});
	}
}