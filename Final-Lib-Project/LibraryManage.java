import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

class LibraryManage {
	
	private String DATA_PATH = "/home/bhootz/Desktop/Final-Project-main/"; //CHANGE PATH TO WHERE CSV FILES ARE
	private AccountManage am;
	
	public HashMap<Integer, LibraryCard> userList;
	public HashMap<Integer, Document> documents;
	public HashMap<Integer, CheckoutInfo> checkouts;
	public HashMap<Integer, Request> requests;
	public HashMap<Integer, ArrayList<Copies>> copySetByIds;
	
	public LibraryManage() {
		am = new AccountManage();
		
		userList = new HashMap<>();
		documents = new HashMap<>();
		checkouts = new HashMap<>();
		requests = new HashMap<>();
		copySetByIds = new HashMap<>();
		
		this.loadUsers();
		this.loadDocuments();
		this.loadCheckouts();
		this.loadRequests();
	}
	
	/* Normal Library Activity */
	public void getAllOverdueItems() {
		for (Integer userId : userList.keySet()) {
			User user = userList.get(userId);
			am.getOverdueItems(user);
		}
	}
	
	public void viewUserCheckouts(User user) {
		for (Integer checkoutId : user.checkoutHistory.keySet()) {
			CheckoutInfo ci = user.checkoutHistory.get(checkoutId);
			
			System.out.println(user.getFullName() + " has checked out " + ci.doc.getName() + " on " + ci.checkoutDate + ". Expiry: " + ci.expiryDate + ", Returned: " + ci.returnStatus);
		}
	}
	
	/* Normal Member Activity */
	public Boolean addDocumentToCart(User user, int documentId) {
		if(copySetByIds.containsKey(documentId)) {
			Document doc = documents.get(documentId);
			ArrayList<Copies> copySet = copySetByIds.get(documentId);
			
			if(doc.canCheckout) {
				if(copySet.size() > 0) {
					if(am.hasItemInHand(user, documentId)) {
						System.out.println(user.getFullName() + " already has checked out " + doc.getName() + " before and not returned.");
						return false;
					}
					
					if(user.userCart.contains(doc)) {
						System.out.println(user.getFullName() + " already has " + doc.getName() + " in cart.");
						return false;
					}
					
					Request req = this.getRequestForDocument(documentId);
					if(req != null) {
						if(req.cardNumber != user.getId()) {
							System.out.println("Someone has already placed a request on this item. You may request it too");
							return false;
						}
					}
					
					System.out.println(user.getFullName() + " has added " + doc.getName() + "(" + doc.getClass().getTypeName() + ") to cart!");
					user.addToCart(doc);
					return true;
				} else {
					System.out.println("No more copies available for " + doc.getName() + ", but " + user.getFullName() + " can request for it.");
				}
			} else {
				System.out.println(user.getFullName() + " can not checkout reference books or magazines.");
			}
		} else {
			System.out.println("Document doesn't exist in system");
		}
		return false;
	}
	
	public Boolean startCheckout(User user) {
		Boolean canCheckout = false;
		if(user.getAge() <= 12 && user.userCart.size() != 5) {
			System.out.println(user.getFullName() + " is 12 years or under. Must checkout 5 total items");
		} else {
			if(user.userCart.size() == 0)
				return false;
			
			for (Document doc : user.userCart) {
				CheckoutInfo ci = am.checkOut(user, doc);
				ci.id = this.getLastCheckoutId() + 1;
				userList.get(ci.cardNumber).checkoutHistory.put(ci.id, ci);
				checkouts.put(ci.id, ci);
				copySetByIds.get(doc.getId()).remove(0);
				
				Request req = getRequestForDocument(doc.id);
				if(req != null) {
					if(req.cardNumber == user.getId()) {
						requests.remove(req.id);
					}
				}
				
				System.out.println(doc.getName() + " now has " + copySetByIds.get(doc.getId()).size() + " copies.");
			}
			this.saveRequests();
			this.saveCheckouts();
		}
		return canCheckout;
	}
	
	public Boolean requestDocument(User user, int documentId) {
		if(copySetByIds.containsKey(documentId)) {
			Document doc = documents.get(documentId);
			ArrayList<Copies> copySet = copySetByIds.get(documentId);
			if(doc.canCheckout) {
				if(copySet.size() == 0) {
					Request req = new Request();
					req.id = this.getLastRequestId() + 1;
					req.bookId = documentId;
					req.cardNumber = user.getId();
					requests.put(req.id, req);
					this.saveRequests();
					System.out.println("A request for " + doc.getName() + " for " + user.getFullName() + " has been logged.");
					return true;
				} else {
					System.out.println("There are copies available for " + doc.getName() + " for " + user.getFullName());
				}
			} else {
				System.out.println(user.getFullName() + " can not checkout reference books or magazines.");
			}
		} else {
			System.out.println("Document doesn't exist in system");
		}
		return false;
	}
	
	public Boolean renewDocument(User user, int documentId) {
		if(copySetByIds.containsKey(documentId)) {
			for (Integer checkoutId : user.checkoutHistory.keySet()) {
				CheckoutInfo ci = user.checkoutHistory.get(checkoutId);
				if(ci.bookId == documentId && ci.returnStatus == false) {
					if(this.getRequestForDocument(documentId) != null) {
						System.out.println("Someone has placed a request on this item. Item must be returned.");
						this.returnDocument(user, documentId);
						return false;
					} else {
						if(ci.renewCount > 0) {
							System.out.println("Item can only be renewed once, so it will be returned.");
							this.returnDocument(user, documentId);
							return false;
						} else {
							int daysLate = am.checkDaysLate(ci);
							if(daysLate > 0) {
								System.out.println(user.getFullName() + " has paid an overdue fee of: $" + am.calculateFine(daysLate, ci.doc.getPrice()));
							}
							
							ci.renewCount += 1;
							am.renew(user, ci);
							saveCheckouts();
							System.out.println(ci.doc.getName() + " has been renewed till " + ci.expiryDate);
							return true;
						}
					}
				}			
			}
			System.out.println(user.getFullName() + " already returned or never checked out this item.");
		} else {
			System.out.println("Document doesn't exist in system");
		}
		return false;
	}
	
	public Boolean returnDocument(User user, int documentId) {
		if(copySetByIds.containsKey(documentId)) {
			for (Integer checkoutId : user.checkoutHistory.keySet()) {
				CheckoutInfo ci = user.checkoutHistory.get(checkoutId);
				if(ci.bookId == documentId && ci.returnStatus == false) {
					int daysLate = am.checkDaysLate(ci);
					if(daysLate > 0) {
						System.out.println(user.getFullName() + " has paid an overdue fee of: $" + am.calculateFine(daysLate, ci.doc.getPrice()));
					}
					ci.returnStatus = true;
					
					Copies copy = new Copies();
					copy.document = ci.doc;
					copy.copyId = copySetByIds.get(ci.doc.getId()).size();
					copySetByIds.get(ci.doc.getId()).add(copy);
					
					saveCheckouts();
					System.out.println(user.getFullName() + " has returned " + ci.doc.getName());
					return true;
				}			
			}
			System.out.println(user.getFullName() + " already returned or never checked out this document.");
		} else {
			System.out.println("Document doesn't exist in system");
		}
		return false;
	}
	
	/* Load Data Functions */
	public void loadCheckouts() {
		String line = "";
		String splitBy = ",";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(DATA_PATH + "checkouts.csv"));
			int c = -1;
			while ((line = br.readLine()) != null) {
				c++;
				if (c == 0) continue;
				if(line.trim().isEmpty()) continue;
				
				String[] checkoutData = line.split(splitBy);
				CheckoutInfo ci = new CheckoutInfo();
				ci.id = Integer.parseInt(checkoutData[0]);
				ci.bookId = Integer.parseInt(checkoutData[1]);
				ci.doc = documents.get(ci.bookId);
				ci.cardNumber = Integer.parseInt(checkoutData[2]);
				ci.checkoutDate = checkoutData[3];
				ci.expiryDate = checkoutData[4];
				ci.returnStatus = Boolean.parseBoolean(checkoutData[5]);
				ci.renewCount = Integer.parseInt(checkoutData[6]);
				
				checkouts.put(ci.id, ci);
				userList.get(ci.cardNumber).checkoutHistory.put(ci.id, ci);
				if(ci.returnStatus == false) {
					copySetByIds.get(ci.bookId).remove(0);
				}
			}
			br.close();
		}	
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadRequests() {
		String line = "";
		String splitBy = ",";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(DATA_PATH + "docRequests.csv"));
			int c = -1;
			while ((line = br.readLine()) != null) {
				c++;
				if (c == 0) continue;
				if(line.trim().isEmpty()) continue;
				
				String[] reqData = line.split(splitBy);
				Request req = new Request();
				req.id = Integer.parseInt(reqData[0]);
				req.bookId = Integer.parseInt(reqData[1]);
				req.cardNumber = Integer.parseInt(reqData[2]);
				requests.put(req.id, req);
			}
			br.close();
		}	
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadDocuments() {
		String line = "";
		String splitBy = ",";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(DATA_PATH + "Small_book_data.csv"));
			int c = -1;
			while ((line = br.readLine()) != null) {
				c++;
				if (c == 0) continue;
				if(line.trim().isEmpty()) continue;
				
				String[] bookData = line.split(splitBy);
				Document tempDoc = null;
				if(bookData[6].equals("Book")) {
					tempDoc = new Book();
				} else if(bookData[6].equals("magazines")) {
					tempDoc = new Magazine();
				} else if(bookData[6].equals("reference book")) {
					tempDoc = new RefBook();
				} else if(bookData[6].equals("Audio/video")) {
					tempDoc = new AudioVideo();
				}
				
				if(bookData[9].equals("TRUE")) {
					tempDoc.setBestSeller(true);
				} else {
					tempDoc.setBestSeller(false);
				}
				tempDoc.setId(Integer.parseInt(bookData[0]));
				tempDoc.setName(bookData[1]);
				tempDoc.setAuthor(bookData[2]);
				tempDoc.setGenre(bookData[3]);
				tempDoc.setSubGenre(bookData[4]);
				tempDoc.setPrice(Double.parseDouble(bookData[8]));
				
				if(!copySetByIds.containsKey(tempDoc.getId())) {
					ArrayList<Copies> tempList = new ArrayList<>();
					copySetByIds.put(tempDoc.getId(), tempList);
				}
				for(int i = 0; i < Integer.parseInt(bookData[7]); i++) {
					Copies copy = new Copies();
					copy.document = tempDoc;
					copy.copyId = copySetByIds.get(tempDoc.getId()).size();
					copySetByIds.get(tempDoc.getId()).add(copy);
				}
				documents.put(tempDoc.getId(), tempDoc);
			}
			br.close();
		}	
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadUsers(){
		String line = "";
		String splitBy = ",";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(DATA_PATH + "username.csv"));
			int c = -1;
			while ((line = br.readLine()) != null) {
				c++;
				if (c == 0) continue;
				if(line.trim().isEmpty()) continue;
				
				String[] user = line.split(splitBy);
				LibraryCard tempCard = new LibraryCard();
				tempCard.setId(Integer.parseInt(user[0]));
				tempCard.setCardNumber(Integer.parseInt(user[0]));
				tempCard.setFirstName(user[1]);
				tempCard.setLastName(user[2]);
				tempCard.setAge(Integer.parseInt(user[3]));
				tempCard.setAddress(user[4]);
				tempCard.setPhone(user[5]);
				userList.put(Integer.parseInt(user[0]), tempCard);
				
			}
			br.close();
		}	
		catch (IOException e){
			e.printStackTrace();
		}
	} 
	
	public void saveCheckouts() {
		try {
			String content = "ID,BookID,LibraryCardNumber,CheckoutDate,ExpiryDate,Returned,RenewCount\n";
			for (CheckoutInfo ci : checkouts.values()) {
	            content += ci.id + "," + ci.bookId + "," + ci.cardNumber + "," + ci.checkoutDate + "," + ci.expiryDate + "," + ci.returnStatus + "," + ci.renewCount + "\n";
	        }
			BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_PATH + "checkouts.csv"));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            System.out.println("IOException: saveCheckouts()");
        }
	}
	
	public void saveRequests() {
		try {
			String content = "ID,BookID,CardNumber\n";
			for (Request req : requests.values()) {
	            content += req.id + "," + req.bookId + "," + req.cardNumber + "\n";
	        }
			BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_PATH + "docRequests.csv"));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            System.out.println("IOException: saveCheckouts()");
        }
	}
	
	private int getLastCheckoutId() { //
		int count = 0;
		for (Entry<Integer, CheckoutInfo> entry : checkouts.entrySet()) {
            if (count == checkouts.size()) {
                return count;
            }
            count++;
        }
		return count;
    }
	
	private int getLastRequestId() {
		int count = 0;
		for (Entry<Integer, Request> entry : requests.entrySet()) {
            if (count == requests.size()) {
                return count;
            }
            count++;
        }
		return count;
    }
	
	private Request getRequestForDocument(int docId) {
		for (Entry<Integer, Request> entry : requests.entrySet()) {
            if(entry.getValue().bookId == docId) return entry.getValue();
        }
		return null;
	}
}
