import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * Thumbnail display of a set of slides.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class Thumbnails extends JFrame {
	private ArrayList<CvMat> images; 	// the slides
	private ArrayList<CvMat> thumbs; 	// the slides
	private int zoomed = -1; 					// zoomed-in slide number; -1 indicates thumbnails
	private Canvas canvas; 						// our component for handling the graphics
	private static final int trows = 3, tcols = 3; // setup: number of thumbnails per row and column
	private static int thumbWidth, thumbHeight; // scaled size of thumbnails (computed)
	
	public Thumbnails() 
	{
		// Make sure superclass (JFrame) is also properly initialized.
		super("Thumbnails");

		// Read the images, named dart0.jpg ... dart8.jpg, and store in list.
		images = new ArrayList<CvMat>();
		for (int i = 0; i <= 8; i++) 
		{
			CvMat image = cvLoadImageM("img/dart" + i + ".jpg");
			if (image == null) 
			{
				System.out.println("image " + i + " not found!");
				System.exit(1);
			}
			images.add(image);
		}
		createThumbs();
		thumbWidth = images.get(0).cols()/tcols;
		thumbHeight = images.get(0).rows()/trows;
		// Create our graphics-handling component.
		canvas = new Canvas();

		// Make the window size match the image size. Assumes they're all the same size.
		// Also assumes that size matches the layout of the thumbnails.
		//setSize(images.get(0).cols(), images.get(0).rows());
		setSize(thumbWidth*tcols,thumbHeight*trows);
		
		// Boilerplate to finish initializing the GUI.
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		getContentPane().add(canvas);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	/**
	 * Creates the thumbs list, with scaled-down versions of the images.
	 */
	private void createThumbs() 
	{
		thumbs = new ArrayList<CvMat>();
		thumbWidth = images.get(0).cols()/tcols;
		thumbHeight = images.get(0).rows()/trows;
		for (CvMat image : images) 
		{
			thumbs.add(scale(image, 1.0/tcols, 1.0/trows)); // note use of 1.0 instead of 1, to force floating point
		}
	}

	/**
	 * Crude scaling of an image by a factor.
	 * @param image		image to scale
	 * @param scaleC	how much to scale columns by (between 0 and 1)
	 * @param scaleR	how much to scale rows by (between 0 and 1)
	 * @return				scaled image
	 */
	private static CvMat scale(CvMat image, double scaleC, double scaleR) {
		CvMat piece = CvMat.create(thumbHeight, thumbWidth, image.type());
		for(int i = 0; i < image.cols(); i++) //800
		{
			for(int j = 0; j < image.rows(); j++) //600
			{
				for(int c = 0; c < 3; c++)
				{
					int temp1 = Math.min((int)(j*scaleR),thumbHeight-1);
					int temp2 = Math.min((int)(i*scaleC),thumbWidth-1);
					piece.put(temp1,temp2,c,image.get(j,i,c));
				}
			}
		}
		return piece;
	}

	/**
	 * Handle click on image, either selecting a thumbnail or going back to the thumbnails
	 * @param x
	 * @param y
	 */
	private void clicked(int x, int y) 
	{
		if (zoomed != -1) //we are in full picture mode
		{
			// Zoom back out
			zoomed = -1;
		}
		else //we are in thumbnail mode
		{
			// Zoom in to whichever thumbnail it was
			int vertical = x/thumbWidth;
			int horizontal = y/thumbHeight;
			zoomed = horizontal * trows + vertical;
		}
		repaint();
	}

	/**
	 * A component to handle displaying and interacting with images (and other stuff).
	 */
	private class Canvas extends JComponent 
	{
		Canvas() 
		{
			// Listen for mouse presses.
			addMouseListener(new MouseAdapter() 
			{
				public void mousePressed(MouseEvent event) 
				{
					clicked(event.getPoint().x, event.getPoint().y);
				}
			});
		}

		/**
		 * Draws this component, by asking the superclass to do its part
		 * and then doing the drawing we want to do.
		 */
		public void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			if (zoomed != -1) 
			{
				// Show the selected slide.
				g.drawImage(images.get(zoomed).asIplImage().getBufferedImage(), 0, 0, null);
			}
			else 
			{
				// Lay out the thumbnails
				int count = 0;
				
				for(int i = 0; i < images.get(0).cols()/thumbWidth; i++)
				{
					for(int j = 0; j < images.get(0).rows()/thumbHeight; j++)
					{
						g.drawImage(thumbs.get(count).asIplImage().getBufferedImage(),i*thumbWidth,j*thumbHeight,null);
						count++;
					}
				}
			}
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
				Thumbnails frame = new Thumbnails();
			}
		});
	}
}