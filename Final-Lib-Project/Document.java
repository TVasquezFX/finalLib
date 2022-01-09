public class Document {

    //declare attributes

    private double price;
    protected String name;
    protected int id;
    protected String author, genre, subgenre, publisher;
    protected Boolean isBestSeller = false;
    protected int maxWeeks = 3;
    protected Boolean canCheckout = true;
    
    //declare methods

    public Document()
    {
    	
    }
    
    
    public Boolean isBestSeller() {
    	return isBestSeller;
    }
    
    public void setBestSeller(Boolean b) {
    	isBestSeller = b;
    }
    
    public String getAuthor() {
    	return author;
    }
    
    public void setAuthor(String s) {
    	this.author = s;
    }
    
    public String getGenre() {
    	return genre;
    }
    
    public void setGenre(String g) {
    	genre = g;
    }
    
    public String getSubGenre() {
    	return subgenre;
    }
    
    public void setSubGenre(String g) {
    	subgenre = g;
    }
    
    public String getPublisher() {
    	return publisher;
    }
    
    public void setPublisher(String p) {
    	publisher = p;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
