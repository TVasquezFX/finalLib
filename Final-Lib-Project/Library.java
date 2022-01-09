
public class Library {
	
	public static void main(String[] args) {
		LibraryManage lm = new LibraryManage();
		
		//might be good to say that we are assuming anyone that uses this library has registered
		//and has an active membership
		
		//Current Library Member checks out book
		User rachelUser = lm.userList.get(9012); //get a user by library card number, for demo
		rachelUser.join(); //user joins the library
		
		User craigUser = lm.userList.get(4081);
		craigUser.join();
		
		/*lm.addDocumentToCart(rachelUser, 10); //user try to add Book by ID to cart
		lm.addDocumentToCart(rachelUser, 32);
		lm.addDocumentToCart(rachelUser, 8);
		lm.addDocumentToCart(rachelUser, 5);
		lm.addDocumentToCart(rachelUser, 23);
		
		lm.startCheckout(rachelUser);
		
		System.out.println("\n");
		lm.addDocumentToCart(craigUser, 10);
		lm.requestDocument(craigUser, 10);
		
		lm.renewDocument(rachelUser, 10);
		
		System.out.println("\n");
		//lm.addDocumentToCart(rachelUser, 10);
		//lm.startCheckout(rachelUser);
		
		lm.addDocumentToCart(craigUser, 10);
		lm.startCheckout(craigUser);*/
		
		lm.renewDocument(craigUser, 10);
	}
	
}
