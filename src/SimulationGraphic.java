import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

//the visual model representing the simulation
public class SimulationGraphic extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4223215281512333981L;
	private Feeder model;
	private int numberOfStations;

	public SimulationGraphic(Feeder model) {
		this.model = model;
		numberOfStations = model.getNumberOfStations();
		this.setPreferredSize(new Dimension(800, 500));
	}

	@Override
	public void paintComponent(Graphics g) {
		
		//make the curves look nicer
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double screenWidthChange = this.getParent().getWidth()/800.0; //multiplier that changes based on screen width
		int offset = (int) (50*screenWidthChange); //offset from left side of screen
		int c_radius = (int) (((200.0 / numberOfStations) + 10) * screenWidthChange); // radius of cashier circles
		

		int[] x = new int[numberOfStations]; // x values for each queue
		for (int i = 0; i < numberOfStations; i++) {
			g2d.setFont(g2d.getFont().deriveFont((float) (10 * (12.0/numberOfStations) * screenWidthChange)));
			// draw cashiers distributed evenly - they are represented by dark blue circles
			x[i] = ((this.getWidth() - 50) / numberOfStations) * i + offset;
			g2d.setColor(new Color(0, 51, 51));
			g2d.fillOval(x[i], 75, c_radius, c_radius);
			g2d.setColor(Color.white);
			g2d.drawString(String.valueOf(i), x[i] + (int) (c_radius / 2.5), 75 + (int) (c_radius / 1.5));
			
			g2d.setFont(g2d.getFont().deriveFont((float) (5 * (12.0/numberOfStations) * screenWidthChange)));

			synchronized (model.getQueue(i)) {
				Iterator<Customer> iter = new Iterator<Customer>(model.getQueue(i));
				while (iter.hasNext()) {
					Customer c = null;
					try {
						c = iter.getNext().getData();
					} catch (ElementNotFoundException e) {
						// this will not happen since we checked hasNext()
					}
					if (c.switched()) {
						g2d.setColor(new Color(170,57,57));
					} else {
						g2d.setColor(new Color(34,102,102));
					}
					//draw all customers in the queue as rectangles
					//colored blue if they haven't switched, red if they have
					g2d.fillRect(x[i] - c_radius / 2, iter.getIndex() * 30 + 80 + c_radius, (int) (c_radius * 2.5), 20);
					g2d.setColor(Color.white);
					g2d.drawString(c.getName(), x[i] - (int) (c_radius / 2.5), iter.getIndex() * 30 + 95 + c_radius);
				}
				
				//draw the customer at the cashier as a green rectangle
				Customer c = model.getCashier(i).getCurrentCustomer();
				if(c != null) {
				g2d.setColor(new Color(17,102,17));
				g2d.fillRect(x[i] - c_radius / 2, 50, (int) (c_radius * 2.5), 20);
				g2d.setColor(Color.white);
				g2d.drawString(c.getName(), x[i] - (int) Math.round(c_radius / 2.5), 65);
				}
			} //end of synched statement
		} //end of for loop
		
	} //end of paintComponent method
}
