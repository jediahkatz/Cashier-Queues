//a customer who a cashier will ring up
public class Customer {
	public final int numberOfItems;
	private int itemsRemaining;
	private String name;
	private boolean switched = false; //if this customer has switched lines
	private static int id = 0; //static count of all customers
	
	public Customer(String name, int nItems) {
		this.name = name;
		numberOfItems = nItems;
		itemsRemaining = nItems;
		id++;
	}
	
	public Customer(int nItems) { //if no name specified, use ID
		this("Customer " + id, nItems);
	}
	
	public Customer(String name) {
		this(name, 5); //5 items default
	}
	
	public int itemsRemaining() {
		return itemsRemaining;
	}
	
	public void scanItem() {
		itemsRemaining--;
	}
	
	public String getName() {
		return name + " (" + itemsRemaining + ")";
	}
	
	public String toString() {
		return getName();
	}
	
	public void setSwitched(boolean b) {
		switched = b;
	}
	
	public boolean switched() {
		return switched;
	}
}
