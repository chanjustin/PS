import com.googlecode.javacv.CanvasFrame;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import java.util.*;

/**
 * A basic class to load, display, and process an image, built on JavaCV
 * representations (though not utilizing the OpenCV image processing operations)
 * This is just the core functionality upon which we will build
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class ImageProcessingCore {
	private CvMat image; 				// the image being processed, an instance of a JavaCV class
	private CvMat original;			// the image as initially loaded
	private CvMat mixin;				// another image (of the same dimensions), to be blended
	private CanvasFrame canvas; // a frame to display the image, an instance of a JavaCV class

	public ImageProcessingCore() {
		// Read images, directly getting pixel matrices.
		image = cvLoadImageM("img/baker.jpg");
		mixin = cvLoadImageM("img/rain.jpg");

		// Make sure they were successfully loaded.
		if (image == null) {
			System.out.println("original image not found!");
			System.exit(1);
		}
		if (mixin == null) {
			System.out.println("mixin image not found!");
			System.exit(1);
		}
		
		// Keep a copy of the original, so can revert.
		original = image.clone();
		
		// Create JavaCV image window. (1 indicates no gamma correction.)
		canvas = new CanvasFrame("Image", 1);

		// Request closing of the application when the image window is closed.
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Main loop: gets user input as to how to modify the image.
	 */
	public void run() {
		Scanner in = new Scanner(System.in);

		while (true) {	// Loop forever
			// Display image.
			canvas.showImage(image.asIplImage());
			// Get operation and dispatch to function to process it.
			// Note that there are some magic numbers here that you can play with.
			// (Having magic numbers buried like this is not generally good practice,
			// but this is a hodge-podge of examples.)
			System.out.println("Operation >");
			String op = in.nextLine();
			if (op.isEmpty()) {
				continue;
			}
			else if (op.equals("o")) {
				image = original.clone();
			}
			else if (op.equals("s")) {
				cvSaveImage("img/snapshot.jpg", image.asIplImage());
			}
			else {
				System.out.println("Unknown operation");
			}
		}
	}

	/**
	 * Creates the image processing instance and start it running.
	 * @param args	ignored
	 */
	public static void main(String[] args) {
		ImageProcessingCore proc = new ImageProcessingCore();
		proc.run();
	}
}
