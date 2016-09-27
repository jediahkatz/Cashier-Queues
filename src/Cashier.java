//cashier that rings up customers
public class Cashier {
	private int ringupSpeed; // units: milliseconds per item
	private Customer current; // currently ringing up
	private int number; // cashier number
	private Feeder feeder;

	public Cashier(Feeder f) {
		feeder = f;
		ringupSpeed = 2000; // default speed: 2 seconds per item
		number = 1;
	}

	public Cashier(int number, Feeder f) {
		feeder = f;
		ringupSpeed = 2000;
		this.number = number;
	}

	public Cashier(int speed, int number, Feeder f) {
		feeder = f;
		ringupSpeed = speed;
		this.number = number;
	}

	// ring up the current customer
	public void ringUp() {
		if (current == null) {
			feeder.moveUpQueue(number);
		} else {
			while (current.itemsRemaining() > 0) {
				try {
					Thread.sleep(ringupSpeed); // takes this amount of time to
												// ring up 1 item
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				current.scanItem(); // decrement the number of items remaining
			}
			nextCustomer();
		}
	}

	public void nextCustomer() {
		// add current customer to list of happy customers
		HappyCustomers.add(current);
		current = null;
		// change current to the next customer in the correct queue
		feeder.moveUpQueue(number);
	}

	// if there is no current customer, set the current customer to be c
	public void setCurrentCustomer(Customer c) {
		if (current == null) {
			current = c;
		}
	}

	// returns true if this cashier is currently ringing up a customer
	public boolean hasCustomer() {
		return current != null;
	}

	// any customers in this cashier's queue that get fed up will switch lines
	public void customersSwitch() {
		feeder.switchQueues(number);
	}

	public Customer getCurrentCustomer() {
		return current;
	}

	public int getSpeed() {
		return ringupSpeed;
	}
}
