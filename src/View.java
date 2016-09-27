import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

//the screen
public class View {
	private static Feeder model = null;
	private static SimulationGraphic s = null;
	
	//in case we wanted to have multiple views with their own feeders
	//for this program we will use just one view
	private View(Feeder feeder) {
		model = feeder;
	}
	
	public static void setModel(Feeder f) {
		model = f;
		s = new SimulationGraphic(f);
	}
	
	public static void createAndShowGUI() {
		//I just like how it looks this way, at least on my own PC
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JFrame frame = new JFrame("Cashier Queues Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,1));
		frame.add(panel);
		panel.add(s);
		
		JButton add50 = new JButton("Add 50 customers to balance the queues.");
		add50.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Customer[] c = new Customer[50];
				for(int i=0; i<50; i++) {
					c[i] = new Customer((int) (Math.random() * 6 + 2));
					model.newCustomer(c[i]);
				}
				
			}
			
		});
		panel.add(add50);
		
		JLabel prompt = new JLabel("How would you like customers to switch lines?");
		panel.add(prompt);
		
		JRadioButton[] whichSwitch = new JRadioButton[3];
		
		whichSwitch[0] = new JRadioButton("Switch to the shortest queue.");
		whichSwitch[0].doClick();
		whichSwitch[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.setSwitch(0); //set correct switch behavior
				Log.write("Customers will now switch to the shortest queue.");
			}
			
		});
		
		whichSwitch[1] = new JRadioButton("Switch as a function of cashier speed and queue length.");
		whichSwitch[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.setSwitch(1); //set correct switch behavior
				Log.write("Customers will now switch as a function of cashier speed and queue length.");
			}
			
		});
		
		whichSwitch[2] = new JRadioButton("Switch as a function of cashier speed and number of items held by customers.");
		whichSwitch[2].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.setSwitch(2); //set correct switch behavior
				Log.write("Customers will now switch as a function of cashier speed and number of items held by customers.");
			}
			
		});
		
		ButtonGroup radioButtons = new ButtonGroup();
		
		for(JRadioButton b : whichSwitch) {
			radioButtons.add(b);
			panel.add(b);
		}
		
		JButton saveLog = new JButton("Save Log");
		saveLog.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Log.saveLog();
			}
			
		});
		panel.add(saveLog);
		
		frame.pack();
		frame.setVisible(true);
	
	}
	
	public static void repaint() {
		s.repaint();
	}
	
}
