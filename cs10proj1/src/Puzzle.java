import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * Simple puzzle of rectangular fragments from an image. Click on a pair of pieces to swap.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class Puzzle extends JFrame {
	private CvMat image;							// source of puzzle pieces
	private ArrayList<CvMat> pieces;	// the small images broken out from the whole thing
	private Canvas canvas; 						// our component for handling the graphics
	private int selected = -1; 				// selected piece; -1 for none
	private int pieceWidth, pieceHeight; // size of pieces (computed)
	private static final int prows = 3, pcols = 4; // puzzle setup: number of pieces per row and column
	
	public Puzzle() {
		// Make sure superclass (JFrame) is also properly initialized.
		super("Puzzle");

		// Read an image and make sure it was successfully loaded.
		image = cvLoadImageM("img/baker.jpg");
		if (image == null) {
			System.out.println("image not found!");
			System.exit(1);
		}

		// Create our graphics-handling component.
		canvas = new Canvas();

		// New thing in puzzle: make and shuffle the pieces.
		createPieces();
		shufflePieces();
		
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
	 * Creates the pieces list, fragments from the image.
	 */
	private void createPieces() {
		// Compute piece size according to how many have to fit in image and image's size.
		pieceWidth = image.cols() / pcols;
		pieceHeight = image.rows() / prows;
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
	 * Shuffles the pieces array
	 */
	private void shufflePieces() {
		// Simple shuffle: swap each piece with some other one
		for (int i = 0; i < pieces.size(); i++) {
			int j = (int) (Math.random() * pieces.size());
			CvMat pi = pieces.get(i);
			pieces.set(i, pieces.get(j));
			pieces.set(j, pi);
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
			CvMat p = pieces.get(selected);
			pieces.set(selected, pieces.get(curr));
			pieces.set(curr, p);
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
			((Graphics2D) g).setStroke(new BasicStroke(4)); // thick border
			// Lay out the pieces in a matrix.
			int c = 0, r = 0;
			for (int p = 0; p < pieces.size(); p++) {
				// Piece image.
				g.drawImage(pieces.get(p).asIplImage().getBufferedImage(), c * pieceWidth, r * pieceHeight, null);
				// Border (green if selected).
				if (p == selected) {
					g.setColor(Color.green);
				}
				else {
					g.setColor(Color.black);
				}
				g.drawRect(c * pieceWidth, r * pieceHeight, pieceWidth - 2, pieceHeight - 2);
				// Advance piece, column, and maybe row.
				p++;
				c++;
				if (c == pcols) {
					c = 0;
					r++;
				}
			}
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
				Puzzle frame = new Puzzle();
			}
		});
	}
}