import java.awt.*;
import javax.swing.*;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;

/**
 * Fun with the webcam, built on JavaCV
 * Just applies one of our image processing methods to the webcam image
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class WebcamProcessing extends JFrame {
	private CvMat image;				// the image grabbed from the webcam
	private Canvas canvas;			// drawing component
	private Grabby grabby;			// handles the image grabbing
	private static final int width=300, height=300;		// setup: camera size (keep it small, for processing)
	private static final int pixelSize = 10;	// size of the objects representing the image

	public WebcamProcessing() {
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
	 * Code from Render.java to render the image as a set of rectangles.
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
	
	private class Canvas extends JComponent {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				// change how to display image -- do a mosaic instead of drawing it
				mosaic(g);
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
				// could do any kind of image processing here
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
				WebcamProcessing cam = new WebcamProcessing();
			}
		});
	}
}