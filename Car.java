//Emily Higgs



public class Car {

	private String vin;
	private String make;
	private String model;
	private double price;
	private int mileage;
	private String color;

	public Car() {
		vin = "";
		make = "";
		model = "";
		price = 0.0;
		mileage = 0;
		color = "";
	}

	public Car(String vi, String mak, String mode, double pric, int mileag, String colo) {
		vin = vi;
		make = mak;
		model = mode;
		price = pric;
		mileage = mileag;
		color = colo;
	}

	public String getVin() {
		return vin;
	}

	public String getMake() {
		return make;
	}
	
	public String getModel() {
		return model;
	}

	public double getPrice() {
		return price;
	}

	public int getMileage() {
		return mileage;
	}

	public String getColor() {
		return color;
	}

	public void setVin(String input) {
		vin = input;
	}

	public void setMake(String input) {
		make = input;
	}

	public void setModel(String input) {
		model = input;
	}
	
	public void setPrice(double input) {
		price = input;
	}

	public void setMileage(int input) {
		mileage = input;
	}


	public void setColor(String input) {
		color = input;
	}
}


