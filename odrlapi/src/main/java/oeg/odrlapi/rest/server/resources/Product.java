package oeg.odrlapi.rest.server.resources;

public class Product {

	String name;
	int qty;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQty() {
            qty+=3;
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	@Override
	public String toString() {
		return "Product [name=" + name + ", qty=" + qty + "]";
	}

}