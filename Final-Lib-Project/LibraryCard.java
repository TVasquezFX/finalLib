
public class LibraryCard extends User {
	
	private int cardNumber;
	private Boolean renewed = false;
	
	public LibraryCard() {
		
	}
	
	public void setCardNumber(int cn) {
		this.cardNumber = cn;
	}
	
	public void setRenewed(Boolean n) {
		this.renewed = n;
	}
	
	public int getCardNumber() {
		return this.cardNumber;
	}
	
	public Boolean isRenewed() {
		return this.renewed;
	}
}
