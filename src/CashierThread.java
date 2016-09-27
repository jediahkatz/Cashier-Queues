
public class CashierThread extends Thread {
	private Cashier cashier; //the cashier
	
	public CashierThread(Cashier c) {
		cashier = c;
	}
	
	@Override
	public void run() {
		while(true) {
			cashier.ringUp();
			View.repaint();
		}
	}
}
