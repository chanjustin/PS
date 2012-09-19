import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Animated agents.
 * This is just the core functionality upon which we will build
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 */
public class AgentsCore extends JFrame {
	private Canvas canvas;										// our component for handling the graphics
	private ArrayList<Agent> agents;					// list of all the agents to handle
	private static final int width=800, height=600;	// setup: size of canvas
	private static final int nagents = 10;					// setup: how many agents to create
	
	public AgentsCore() {
		super("Agents");

		// Create our graphics-handling component.
		canvas = new Canvas();

		// Set the size as specified.
		setSize(width, height);

		// Create all the agents.
		agents = new ArrayList<Agent>();
		for (int i = 0; i < nagents; i++) {
			agents.add(new Agent());
		}

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
		protected int r=5;			// radius; defaults to 5

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

		/**
		 * Updates the position.
		 * Default: stay still.
		 */
		public void move() {
		}

		/**
		 * Draws the agent on the graphics.
		 * @param g
		 */
		public void draw(Graphics g) {
			g.fillOval((int) x - r, (int) y - r, 2 * r, 2 * r);
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
	 * A component to handle custom displaying and interacting.
	 */
	private class Canvas extends JComponent {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
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
				AgentsCore frame = new AgentsCore();
			}
		});
	}
}