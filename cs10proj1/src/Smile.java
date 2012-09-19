import com.googlecode.javacv.CanvasFrame;
import static com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

/**
 * Load and display an image with JavaCV
 * Based on code at http://code.google.com/p/javacv/wiki/OpenCV2_Cookbook_Examples
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class Smile 
{
    public static void main(String[] args) 
    { 	
        // Read an image.
        IplImage image = cvLoadImage("img/smiley.png");
        
        // Make sure it was successfully loaded.
        if (image == null) 
        {
	        	System.out.println("image not found!");
	        	System.exit(1);
        }
        
        // Create image window named "Hello!".  (1 indicates no gamma correction.)
        CanvasFrame canvas = new CanvasFrame("Hello!", 1);
        
        // Request closing of the application when the image window is closed.
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                
        // Show image in window.
        canvas.showImage(image);
    }
}