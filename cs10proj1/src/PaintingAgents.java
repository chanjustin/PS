import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * Animated agents.
 * 
 * @author Justin Chan, Dartmouth CS 10, Fall 2012
 */
public class PaintingAgents extends JFrame {
	private Canvas canvas;										// our component for handling the graphics
	private ArrayList<Agent> agents;					// list of all the agents to handle
	private static int width,height;	// setup: size of canvas
	private static final int nagents = 100;					// setup: how many agents to create
	private static CvMat blank, image;
	
	public PaintingAgents() {
		super("Painters");
		
		image = cvLoadImageM("img/dart0.jpg");
		width = image.cols();
		height = image.rows();
		blank = CvMat.create(height,width,image.type());

		
		// Create our graphics-handling component.
		canvas = new Canvas();

		// Set the size as specified.
		setSize(width,height);
		
		// Create all the agents (various versions are commented out; could
		// uncomment, mix & match).
		agents = new ArrayList<Agent>();
		for (int i = 0; i < nagents; i++) {
			agents.add(new Bouncer());
		}
		
		// Move every 100 milliseconds.
		Timer timer = new Timer(100, new AbstractAction("update") {
			public void actionPerformed(ActionEvent e) {
				// Ask all the agents to draw themselves.
				for (Agent agent : agents) {
					Color color = new Color((int)image.get((int)agent.getY(), (int)agent.getX(), 2), 
											(int)image.get((int)agent.getY(), (int)agent.getX(), 1), 
											(int)image.get((int)agent.getY(), (int)agent.getX(), 0));
					agent.setColor(color);
					agent.move();
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
	 * Animated agent, defined by a position and size, 
	 * and the ability to move, draw itself, and see if a point is inside.
	 */
	private class Agent {
		protected double x, y;	// position
		protected int r=10;			// radius; defaults to 5
		protected Color color;

		/**
		 * Initializes at random position (and default radius)
		 */
		public Agent() {
			x = Math.random()*width;
			y = Math.random()*height;
		}

		/**
		 * Following are auto-generated getters & setters for position, for demonstration
		 */

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}
		
		public Color getColor() {
			return color;
		}
		
		public void setColor(Color color) {
			this.color = color;
		}

		/**
		 * Updates the position.
		 * Default: stay still.
		 */
		public void move() {
			int newX = (int)Math.min(getX(),width-1);
			int newY = (int)Math.min(getY(),height-1);
			for(int i = 0; i < r; i++)
			{
				for(int j = 0; j < r; j++)
				{
					for(int c = 0; c < 3; c++)
					{
						blank.put(newY,newX,c,image.get(newY,newX,c));
					}
					newX++;
				}
				newY = (int)Math.min(newY+1,height-1);
				newX = (int)Math.min(getX(),width-1);
			}
		}

		/**
		 * Draws the agent on the graphics.
		 * @param g
		 */
		public void draw(Graphics g) {
			g.setColor(getColor());
			g.fillRect((int)Math.min(x,width),(int)Math.min(y,height),r,r);
		}

		/**
		 * Tests whether the point is inside the agent.
		 * @param x2
		 * @param y2
		 * @return		is (x2,y2) in the agent?
		 */
		public boolean contains(double x2, double y2) {
			return (x-x2)*(x-x2) + (y-y2)*(y-y2) <= r*r;
		}
	}
	
	/**
	 * An agent that moves in a particular direction, but bounces off the walls.
	 */
	private class Bouncer extends Agent {
		private double dx, dy;	// step size in x and y
		
		public Bouncer() {
			// Step size randomly between -5 and +5
			dx = 10 * (Math.random() - 0.5);
			dy = 10 * (Math.random() - 0.5);
		}
		
		public void move() {
			super.move();
			x += dx;
			y += dy;
			// Handle the bounce, accounting for radius.
			if (x > width - r) {
				x = width - r;
				dx = -dx;
			}
			else if (x < r) {
				x = r;
				dx = -dx;
			}
			if (y > height - r) {
				y = height - r;
				dy = -dy;
			}
			else if (y < r) {
				y = r;
				dy = -dy;
			}
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
							System.out.println("got me!");
							// This only works if we change the type of agents to ArrayList<Teleporter>
							// (and only put teleporters in it). Why?
							// agent.teleport();
						}
					}
				}
			});
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(blank.asIplImage().getBufferedImage(), 0, 0, null);
			// Ask all the agents to draw themselves.
			for (Agent agent : agents) {
				agent.draw(g);
			}
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
				PaintingAgents frame = new PaintingAgents();
			}
		});
	}
}