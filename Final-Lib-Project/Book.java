
public class Book extends Document {
	
	public void setBestSeller(Boolean b) {
		this.isBestSeller = b;
		if(b) {
			maxWeeks = 2;
		}
	}
}
