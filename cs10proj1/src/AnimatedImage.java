import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * Custom rendering of an image, by animated agents.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class AnimatedImage extends JFrame {
	private CvMat image;							// what to display and interact with
	private Canvas canvas;						// our component for handling the graphics
	private ArrayList<Agent> agents;	// the agents representing the picture
	private static final int radius = 5;				// setup: agent size
	private static final int numToMove = 1000;	// setup: how many agents to animate each frame
	private int width, height;				// size of window, based on image size and agent size
	
	public AnimatedImage() {
		// Make sure superclass (JFrame) is also properly initialized.
		super("Animated Picture");

		// Read an image and make sure it was successfully loaded.
		// Use a small one!
		image = cvLoadImageM("img/baker-200-150.jpeg");
		if (image == null) {
			System.out.println("image not found!");
			System.exit(1);
		}

		// Create our graphics-handling component.
		canvas = new Canvas();

		// Make the window size match the image size * the agent size.
		width = image.cols() * radius;
		height = image.rows() * radius;
		setSize(width, height);

		// Create one agent per pixel.
		agents = new ArrayList<Agent>();
		for (int i = 0; i < image.rows(); i++) {
			for (int j = 0; j < image.cols(); j++) {
				Color color = new Color((int) image.get(i, j, 2), 
										(int) image.get(i, j, 1), 
										(int) image.get(i, j, 0));
				agents.add(new Wanderer(j * radius, i * radius, radius, color));
			}
		}

		// Move some random agents every 100 milliseconds
		Timer timer = new Timer(100, new AbstractAction("update") {
			public void actionPerformed(ActionEvent e) {
				for (int a = 0; a < numToMove; a++) {
					// Pick a random agent and ask it to move.
					agents.get((int) (Math.random() * agents.size())).move();
				}
				canvas.repaint();
			}
		});

		timer.start();

		// Boilerplate to finish initializing the GUI.
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		getContentPane().add(canvas);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * An animated agent, much like in Agents.java but also including a color.
	 * Step size is proportional to radius
	 */
	private class Agent {
		protected double x, y;
		protected int r;
		protected Color color;

		public Agent(double x, double y, int r, Color color) {
			this.x = x;
			this.y = y;
			this.r = r;
			this.color = color;
		}

		public void move() {
		}

		public void draw(Graphics g) {
			g.setColor(color);
			g.fillOval((int) x - r, (int) y - r, 2 * r, 2 * r);
		}

		public boolean contains(double x2, double y2) {
			return (x - x2) * (x - x2) + (y - y2) * (y - y2) <= r * r;
		}
	}

	private class Wanderer extends Agent {
		public Wanderer(double x, double y, int r, Color color) {
			super(x, y, r, color);
		}

		public void move() {
			x += r * 2 * (Math.random() - 0.5);
			y += r * 2 * (Math.random() - 0.5);
		}
	}

	/**
	 * A component to handle custom displaying and interacting.
	 */
	private class Canvas extends JComponent {
		public Canvas() {
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					// See who (if anyone) was clicked on.
					for (Agent agent : agents) {
						if (agent.contains(event.getPoint().x, event.getPoint().y)) {
							agent.r *= 2; // pump it up
						}
					}
				}
			});
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// Ask all the agents to draw themselves.
			for (Agent agent : agents) {
				agent.draw(g);
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
				AnimatedImage frame = new AnimatedImage();
			}
		});
	}
}