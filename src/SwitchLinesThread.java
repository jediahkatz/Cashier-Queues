//prompts customers to switch lines periodically
public class SwitchLinesThread extends Thread {
	private Cashier[] cashiers;
	
	public SwitchLinesThread(Cashier[] cashiers) {
		this.cashiers = cashiers;
	}
	
	@Override
	public void run() {
		while(true) {
			for(Cashier c : cashiers) {
				c.customersSwitch();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

}
