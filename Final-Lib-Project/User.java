import java.util.ArrayList;
import java.util.HashMap;

public class User {
	
	private int id;
	private String firstName;
	private String lastName;
	private int age;
	private String phone;
	private String address;
	public HashMap<Integer, CheckoutInfo> checkoutHistory;
	public ArrayList<Document> userCart;
	
	public User() {
		checkoutHistory = new HashMap<>();
	}
	
	public void join() {
		userCart = new ArrayList<>();
	}
	
	public void addToCart(Document doc) {
		userCart.add(doc);
	}
	
	public void clearCart() {
		userCart.clear();
	}
	
	public void setId(int i) {
		this.id = i;
	}
	
	public void setFirstName(String n) {
		this.firstName = n;
	}
	
	public void setLastName(String n) {
		this.lastName = n;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public void setAddress(String add) {
		this.address = add;
	}

	public int getId() {
		return this.id;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public String getPhoneNumber() {
		return this.phone;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public int getCheckoutLimit() {
		if(this.age <= 12)
			return 5;
		
		return 6;
	}
	
}
