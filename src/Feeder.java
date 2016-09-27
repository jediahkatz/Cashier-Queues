//feeds Customers into cashier queues and allows them to leave and rejoin
public class Feeder {
	private Cashier[] cashiers;
	private CashierThread[] threads;
	private Queue<Customer>[] queues;
	private int numberOfStations;
	private int whichSwitch = 0;

	@SuppressWarnings("unchecked")
	public Feeder(int numberOfStations) {
		this.numberOfStations = numberOfStations;
		cashiers = new Cashier[numberOfStations];
		threads = new CashierThread[numberOfStations];
		queues = new Queue[numberOfStations];

		for (int i = 0; i < numberOfStations; i++) {
			// random cashier speed between 0.2 and 1 seconds per item
			int randomSpeed = (int) (Math.random() * 800) + 200;
			cashiers[i] = new Cashier(randomSpeed, i, this);
			threads[i] = new CashierThread(cashiers[i]);
			queues[i] = new Queue<Customer>();
		}
	}

	// start all the threads
	public void start() {
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		SwitchLinesThread switchLines = new SwitchLinesThread(cashiers);
		switchLines.start();
	}

	// a new customer wants to join a queue - they will join the shortest queue
	// to start
	public void newCustomer(Customer c) {
		int shortestLineIndex = 0;
		// find which queue is the shortest
		int atCashier = cashiers[0].hasCustomer() ? 1 : 0;
		int shortest = queues[0].size() + atCashier;
		for (int i = 1; i < queues.length; i++) {
			atCashier = cashiers[i].hasCustomer() ? 1 : 0;

			if (queues[i].size() + atCashier < shortest) {
				shortestLineIndex = i;
				shortest = queues[i].size() + atCashier;
			}
		}

		queues[shortestLineIndex].enqueue(c);
		Log.write(c.getName() + " has begun waiting at queue " + shortestLineIndex + ".");
		
		// if that customer is the only one in the queue, they can go right to
		// to the cashier
		if (queues[shortestLineIndex].size() <= 1) {
			moveUpQueue(shortestLineIndex);
		}
	}

	// the first customer on the specified queue moves to the cashier, the rest
	// of the queue moves up
	public void moveUpQueue(int number) {
		if (!cashiers[number].hasCustomer()) { // don't want to call dequeue if
												// there's a customer already
			try {
				Customer c = queues[number].dequeue();
				cashiers[number].setCurrentCustomer(c);
				Log.write("Cashier " + number + " is now ringing up " + c.getName() + ".");
			} catch (ElementNotFoundException e) {
				// if the queue is empty, do nothing, and then this method
				// will be called again when a customer joins this queue
			}
		}
	}

	// any customers in the specified queue who would like to switch queues can do so
	public synchronized void switchQueues(int number) {
		if (queues[number].size() > 0) {
			switch (whichSwitch) {
			case 0: // 0 = shortestLineSwitch
				while (shortestLineSwitch(number)) {
				} // keep calling until no more customers want to switch
				break;
			case 1: // 1 = fastestCashierSwitch
				fastestCashierSwitch(number);
				break;
			case 2: // 2 = fastestToCashierSwitch
				fastestToCashierSwitch(number);
				break;
			}
		}

	}

	// a customer at the back of a queue will switch whenever there is a queue
	// with >1 fewer people
	// the customer will switch to the shortest queue
	// returns true if a customer switched
	private boolean shortestLineSwitch(int number) {
		Queue<Customer> currentQueue = queues[number]; // the current queue

		int shortestSize = queues[0].size();
		int shortestIndex = 0;

		for (int i = 1; i < queues.length; i++) {
			Queue<Customer> q = queues[i];

			if (q != currentQueue) { // can't switch to the queue we're already
										// in
				if (q.size() < shortestSize) {
					shortestIndex = i;
					shortestSize = q.size();
				}
			}
		}

		boolean switched = false;

		// only switch if more than 1 fewer people in line
		if (currentQueue.size() - queues[shortestIndex].size() > 1) {
			try {
				Customer c = currentQueue.removeTail();
				queues[shortestIndex].enqueue(c);
				switched = true;
				c.setSwitched(true);
				Log.write(c.getName() + " switched from queue " + number + " to queue " + shortestIndex + ".");
			} catch (ElementNotFoundException e) {
				e.printStackTrace();
				// if this queue is empty this method would not have been called
				// in the first place
			}
		}

		return switched;
	}

	// a customer at back of the queue will switch to a queue whose (cashier
	// speed * (number of people + 1))
	// is less than the current queue's (cashier speed * number of people)
	// returns true if a customer switched
	private boolean fastestCashierSwitch(int number) {
		Queue<Customer> currentQueue = queues[number];

		// cashier speed * number of people
		int currentSpeedXPeople = currentQueue.size() * cashiers[number].getSpeed();
		int shortestSpeedXPeople = queues[0].size() * cashiers[0].getSpeed();
		int shortestIndex = 0;

		for (int i = 1; i < queues.length; i++) {
			Queue<Customer> q = queues[i];
			Cashier c = cashiers[i];

			if (q != currentQueue) { // can't switch to the queue we're already
										// in
				if (((q.size() + 1) * c.getSpeed()) < shortestSpeedXPeople) {
					shortestIndex = i;
					shortestSpeedXPeople = (q.size() + 1) * c.getSpeed();
				}
			}
		}

		boolean switched = false;

		// only switch shortestSpeed
		if (shortestSpeedXPeople < currentSpeedXPeople) {
			try {
				Customer c = currentQueue.removeTail();
				queues[shortestIndex].enqueue(c);
				switched = true;
				c.setSwitched(true);
				Log.write(c.getName() + " switched from queue " + number + " to queue " + shortestIndex + ".");
			} catch (ElementNotFoundException e) {
				e.printStackTrace();
				// if this queue is empty this method would not have been called
				// in the first place
			}
		}

		return switched;
	}

	// a customer at the back of a queue will switch so that he gets to the
	// cashier fastest
	// this is calculated as a function of cashier speed and the cumulative
	// number of items
	// that each customer is checking out
	// returns true if a customer switched
	private boolean fastestToCashierSwitch(int number) {
		Queue<Customer> currentQueue = queues[number];

		int nItems = 0;

		Iterator<Customer> iter = new Iterator<Customer>(currentQueue);
		while (iter.hasNext()) {
			try {
				nItems += iter.getNext().getData().itemsRemaining();
			} catch (ElementNotFoundException e) {
				// this won't happen because we checked hasNext
			}
		}
		// cashier speed * number of items in queue
		int currentSpeedXItems = nItems * cashiers[number].getSpeed();
		int lowestSpeedXItems = Integer.MAX_VALUE;
		int shortestIndex = number;

		for (int i = 0; i < queues.length; i++) {
			Queue<Customer> q = queues[i];
			Cashier c = cashiers[i];
			nItems = 0;

			if (q != currentQueue) { // can't switch to the queue we're already
										// in
				iter = new Iterator<Customer>(q);
				while (iter.hasNext()) {
					try {
						nItems += iter.getNext().getData().itemsRemaining();
					} catch (ElementNotFoundException e) {
						// this won't happen because we checked hasNext
					}
				}

				nItems += 7; // accounting for the max items this customer could
								// have

				if (c.getSpeed() * nItems < lowestSpeedXItems) {
					lowestSpeedXItems = c.getSpeed() * nItems;
					shortestIndex = i;
				}
			}
		} // end of for loop

		boolean switched = false;

		// only switch shortestSpeed
		if (lowestSpeedXItems < currentSpeedXItems) {
			try {
				Customer c = currentQueue.removeTail();
				queues[shortestIndex].enqueue(c);
				switched = true;
				c.setSwitched(true);
				Log.write(c.getName() + " switched from queue " + number + " to queue " + shortestIndex + ".");
			} catch (ElementNotFoundException e) {
				e.printStackTrace();
				// if this queue is empty this method would not have been called
				// in the first place
			}
		}

		return switched;
	}

	public int getNumberOfStations() {
		return numberOfStations;
	}

	public Queue<Customer> getQueue(int n) {
		return queues[n];
	}

	public Cashier getCashier(int n) {
		return cashiers[n];
	}
	
	public void setSwitch(int n) {
		if(n <= 2 && n >= 0) {
			whichSwitch = n;
		} else {
			whichSwitch = 0;
		}
	}
}