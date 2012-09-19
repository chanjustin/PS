import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMat;

/**
 * Simple puzzle of rectangular fragments from an image. Click on a pair of pieces to swap.
 * Input is now from webcam rather than an image
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class WebcamPuzzle extends JFrame {
	private CvMat image;							// source of puzzle pieces
	private ArrayList<CvMat> pieces;	// the small images broken out from the whole thing
	private ArrayList<Integer> order;	// the order of the pieces
	private Canvas canvas; 						// our component for handling the graphics
	private int selected = -1; 				// selected piece; -1 for none
	private int pieceWidth, pieceHeight; // size of pieces (computed)
	private static final int prows = 3, pcols = 4; // puzzle setup: number of pieces per row and column
	private Grabby grabby;						// handles the image grabbing
	private static final int width=300, height=300;		// setup: camera size (keep it small, for processing)

	public WebcamPuzzle() {
		// Make sure superclass (JFrame) is also properly initialized.
		super("Webcam Puzzle");

		// Create our graphics-handling component.
		canvas = new Canvas();

		// Set the size as specified.
		setSize(width, height);

		// Create a grabber.
		grabby = new Grabby();
		grabby.execute();

		// Compute piece size according to how many have to fit in image and image's size.
		pieceWidth = width / pcols;
		pieceHeight = height / prows;

		// Make and shuffle the piece ordering.
		order = new ArrayList<Integer>();
		for (int p = 0; p < prows * pcols; p++) {
			order.add(p);
		}
		shuffleOrder();

		// Make the window size match the image size.
		setSize(width, height);

		// Boilerplate to finish initializing the GUI.
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		getContentPane().add(canvas);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates the pieces list, fragments from the image.
	 */
	private void createPieces() {
		// Create and fill up the array, iterating by piece row and piece column.
		pieces = new ArrayList<CvMat>();
		for (int pi = 0; pi < prows; pi++) {
			for (int pj = 0; pj < pcols; pj++) {
				CvMat piece = CvMat.create(pieceHeight, pieceWidth, image.type());
				pieces.add(piece);
				// Copy pixels from image to piece (as usual, do it by hand rather than OpenCV).
				for (int i = 0; i < pieceHeight; i++) {
					for (int j = 0; j < pieceWidth; j++) {
						for (int c = 0; c < 3; c++) {
							piece.put(i, j, c, image.get(i + pi * pieceHeight, j + pj * pieceWidth, c));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Shuffles the order array
	 */
	private void shuffleOrder() {
		// Simple shuffle: swap each piece with some other one
		for (int i = 0; i < order.size(); i++) {
			int j = (int) (Math.random() * order.size());
			Integer pi = order.get(i);
			order.set(i, order.get(j));
			order.set(j, pi);
		}
	}

	/**
	 * Handles clicking on a piece, by selecting/deselecting/swapping
	 * @param x		x coordinate of click
	 * @param y		y coordinate of click
	 */
	private void clickPiece(int x, int y) {
		// Determine which piece it was.
		int c = x / pieceWidth;
		int r = y / pieceHeight;
		int curr = r * pcols + c;
		if (selected == -1) {
			// First piece to be selected -> remember
			selected = curr;
		}
		else if (selected == curr) {
			// Same piece -> deselect
			selected = -1;
		}
		else {
			// Second piece -> swap
			Integer p = order.get(selected);
			order.set(selected, order.get(curr));
			order.set(curr, p);
			selected = -1;
		}
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
					clickPiece(event.getPoint().x, event.getPoint().y);
				}
			});
		}

		/**
		 * Draws this component, by asking the superclass to do its part and then
		 * doing the drawing we want to do.
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image == null) return;
			((Graphics2D) g).setStroke(new BasicStroke(4)); // thick border
			createPieces();
			// Lay out the pieces in a matrix.
			int c = 0, r = 0;
			for (int p = 0; p < order.size(); p++) {
				// Piece image.
				CvMat piece = pieces.get(order.get(p));
				g.drawImage(piece.asIplImage().getBufferedImage(), c * pieceWidth, r * pieceHeight, null);
				// Border (green if selected).
				if (p == selected) {
					g.setColor(Color.green);
				}
				else {
					g.setColor(Color.black);
				}
				g.drawRect(c * pieceWidth, r * pieceHeight, pieceWidth - 2, pieceHeight - 2);
				// Advance column, and maybe row.
				c++;
				if (c == pcols) {
					c = 0;
					r++;
				}
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
	 * Gets the ball rolling by creating an instance of our class, using
	 * SwingUtilities boilerplate to schedule the task of initializing the GUI.
	 * @param args	ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WebcamPuzzle frame = new WebcamPuzzle();
			}
		});
	}
}