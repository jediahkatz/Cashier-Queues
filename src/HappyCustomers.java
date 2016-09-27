//where the happy customers go after they are finished
public class HappyCustomers {
	private static Queue<Customer> happyCustomers = new Queue<Customer>(); //list of customers who have finished
	
	private HappyCustomers() {
	}
	
	//synchronized, because every cashier will be adding to this list at the same time
	public static synchronized void add(Customer c) {
		happyCustomers.enqueue(c);
		Log.write(c.getName() + " is finished and has joined the list of happy customers!");
	}
}
