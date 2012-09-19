import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;

/**
 * Fun with the webcam, built on JavaCV
 * This is just the core functionality upon which we will build
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class WebcamLoop extends JFrame {
	private CvMat image;				// the image grabbed from the webcam
	private Canvas canvas;			// drawing component
	private Grabby grabby;			// handles the image grabbing
	private static final int width=300, height=300;		// setup: camera size (keep it small, for processing)
	private boolean recording = true;
	private ArrayList<CvMat> frames = new ArrayList<CvMat>();
	private int frame = 0;
	
	public WebcamLoop() {
		super("Webcam");

		// Create our graphics-handling component.
		canvas = new Canvas();

		// Set the size as specified.
		setSize(width, height);

		// Create a grabber.
		grabby = new Grabby();
		grabby.execute();

		System.out.println("recording:"+recording);

		// Boilerplate to finish initializing the GUI.
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		getContentPane().add(canvas);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private class Canvas extends JComponent {
		private Canvas() {
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					// Toggle record/play and go back to frame 0 (with cleared frames if recording)
					recording = !recording;
					frame = 0;
					if (recording) {
						frames = new ArrayList<CvMat>();
					}
					System.out.println("recording:"+recording);
				}
			});
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (recording) {
				if (image != null) {
					g.drawImage(image.asIplImage().getBufferedImage(), 0, 0, null);
				}
			}
			else {
				g.drawImage(frames.get(frame).asIplImage().getBufferedImage(), 0, 0, null);
				frame--;
				if (frame < 0) frame = frames.size() - 1;
				System.out.println(frame);
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
				if (recording) {
					frames.add(image.clone()); // have to make a copy since the grabbing "reuses" it					
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
	 * @param args	ignored
	 */	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WebcamLoop cam = new WebcamLoop();
			}
		});
	}
}