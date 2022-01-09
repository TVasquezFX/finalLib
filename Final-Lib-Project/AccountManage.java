import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;  

class AccountManage {

	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
	
	public int getOverdueItems(User user) {
		int count = 0;
		
		for (Integer checkoutId : user.checkoutHistory.keySet()) {
			CheckoutInfo ci = user.checkoutHistory.get(checkoutId);
	        int daysLate = this.checkDaysLate(ci);
	        
	        if(daysLate > 0) {
	        	System.out.println(ci.doc.getName() + " needs to be returned/renewed by " + user.getFullName() + ". Days Late: " + daysLate + ", Overdue Fee: $" + this.calculateFine(daysLate, ci.doc.getPrice()));
	        	count++;
	        }	       
		}
		
		return count;
	}
	
	public int checkDaysLate(CheckoutInfo ci) {
		LocalDateTime timeNow = LocalDateTime.now();
		LocalDateTime expiryDate = LocalDateTime.parse(ci.expiryDate, dtf);
		long lateDays = ChronoUnit.DAYS.between(expiryDate, timeNow);
        int daysLate = (int)lateDays;
        
        return daysLate;
	}

    public CheckoutInfo checkOut(User user, Document doc) {
    	CheckoutInfo ci = new CheckoutInfo();
    	ci.bookId = doc.getId();
    	ci.cardNumber = user.getId();
    	ci.doc = doc;
    	
    	LocalDateTime currentDate = LocalDateTime.now();
    	LocalDateTime dueDate;
        if (doc.isBestSeller() == true){
            dueDate = currentDate.plusWeeks(2);
        } else {
            dueDate = currentDate.plusWeeks(3);
        }
        
        ci.checkoutDate = currentDate.format(dtf);
        ci.expiryDate = dueDate.format(dtf);
        
        return ci;
    }

    // check and return a renew date
    public CheckoutInfo renew(User user, CheckoutInfo ci) {
    	LocalDateTime currentDate = LocalDateTime.now();
    	LocalDateTime dueDate;
        if (ci.doc.isBestSeller() == true){
            dueDate = currentDate.plusWeeks(2);
        } else {
            dueDate = currentDate.plusWeeks(3);
        }
        
        ci.checkoutDate = currentDate.format(dtf);
        ci.expiryDate = dueDate.format(dtf);
        
        return ci;
        
    }
    
    public Boolean hasItemInHand(User user, int docId) {
    	for (Integer checkoutId : user.checkoutHistory.keySet()) {
			CheckoutInfo ci = user.checkoutHistory.get(checkoutId);
			if(ci.doc.getId() == docId && ci.returnStatus)  {
		        return true;  
			}
		}
    	return false;
    }
   
	// calculate fine if book not check in on time
    public double calculateFine(int daysLate, double bookValue) {
        double finefee = 0;

        finefee = daysLate * 0.1;
        if (finefee >= bookValue){
            finefee = bookValue;
        }
        return finefee;
    }
}

