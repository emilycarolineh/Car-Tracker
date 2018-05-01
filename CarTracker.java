//Emily Higgs

import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class CarTracker {

	//Priority Queue for all cars, based on price.
	private static CarPriorityQueue carPriceQueue;
	//An arry of priority queues based on price, each one for a particular make/model.
	private static CarPriorityQueue[] MMPrices;

	//Priority Queue for all cars, based on mileage.
	private static CarPriorityQueue carMileageQueue;
	//An arry of priority queues based on mileage, each one for a particular make/model.
	private static CarPriorityQueue[] MMMileages;

	//This DLB provides efficient lookup for the PQs based on make/model; the index provides the next available space for a priority queue.
	private static DLBTrie MMLookup;
	private static int MMIndex;

	//The input scanner
	private static Scanner input = new Scanner(System.in);

	public static void main(String[] args) throws FileNotFoundException {

		//Read in the input file from cars.txt.
        readInFile();

		int option = -1;

        while (option != 0) {
            System.out.println();
            System.out.println("Car Tracker Menu");
            System.out.println("1. Add a car");
            System.out.println("2. Update a car");
            System.out.println("3. Remove a specific car from consideration");
            System.out.println("4. Retrieve the lowest price car");
            System.out.println("5. Retrieve the lowest mileage car");
            System.out.println("6. Retrieve the lowest price car by make and model");
            System.out.println("7. Retrieve the lowest mileage car by make and model");
            System.out.println("0. Quit");
            System.out.print("Selection: ");

            try {
                option = input.nextInt();
            } catch (NoSuchElementException e) {
                option = -1;
            } catch (IllegalStateException e) {
                option = -1;
            }
            input.nextLine();

            switch (option) {
                case 1:
                    addCar();
                    break;
                case 2:
                    updateCar();
                    break;
                case 3:
                    removeCar();
                    break;
                case 4:
                    lowPrice();
                    break;
                case 5:
                    lowMile();
                    break;
                case 6:
                    lowPriceMM();
                    break;
                case 7:
                    lowMileMM();
                    break;
                case 0:
                    return;
                default:
                    // Invalid, just ignore and let loop again
                    break;
            }
        }
	}

	protected static void addCar() {

		Scanner s = new Scanner(System.in);

		String vin;
		String make;
		String model;
		double price;
		int mileage;
		String color;
		String makeModel;

		System.out.println("What is the VIN number of this car?");
		vin = input.next();

		System.out.println("What is the make of this car?");
		make = s.next();
		
		System.out.println("What is the model of this car?");
		model = s.next();

		System.out.println("What is the price of this car?");
		price = s.nextDouble();

		System.out.println("What is the mileage on this car?");
		mileage = s.nextInt();

		System.out.println("What is the color of this car?");
		color = s.next();

		makeModel = make + "." + model + "$";

		//Search MMLookup for the makeModel index, adding it if necessary. 
		int newCarIndex = findMakeModelIndex(makeModel);

		if(newCarIndex >= MMPrices.length) {
			resizeMMs();
		}

		//Add the new car to the price and mileage PQs for its make/model. 
		MMPrices[newCarIndex].add(new Car(vin, make, model, price, mileage, color));
		MMMileages[newCarIndex].add(new Car(vin, make, model, price, mileage, color));

		//Add the new car to the price and mileage PQs. 
		carPriceQueue.add(new Car(vin, make, model, price, mileage, color));
		carMileageQueue.add(new Car(vin, make, model, price, mileage, color));


		System.out.println();
		System.out.println("The new " + make + " " + model + " has been added for consideration!");
	}
	protected static void updateCar() {

		System.out.println("What is the VIN number of the car to update?");
		String vin = input.next();

		int priceIndex = carPriceQueue.findIndirectionIndex(vin);
		int mileageIndex = carMileageQueue.findIndirectionIndex(vin);

		//Ascertain that the car to be updated exists already.
		if(priceIndex == -1 ) {
			System.out.println("A car by that VIN number does not yet exist. Add it to the list!");
			return;
		}
		else {

			//Get the make and model of the car to be updated.
			Car tempCar = carPriceQueue.accessCar(priceIndex);
			String makeModel = tempCar.getMake() + "." + tempCar.getModel() + "$";

			int MMTempIndex =  findMakeModelIndex(makeModel);
			int MMPriceIndex = MMPrices[MMTempIndex].findIndirectionIndex(vin);
			int MMMileageIndex = MMMileages[MMTempIndex].findIndirectionIndex(vin);

			int select = -1;

			while(select < 0) {
				
				System.out.println();
            	System.out.println("Which attribute would you like to change?");
            	System.out.println("1. Car price");
            	System.out.println("2. Car mileage");
            	System.out.println("3. Car color");
				System.out.print("Selection: ");

				try {
                	select = input.nextInt();
            	} catch (NoSuchElementException e) {
                	select = -1;
            	} catch (IllegalStateException e) {
                	select = -1;
            	}
            	input.nextLine();

				//This is a flag to tell if the price/mileage increased or decreased.
				boolean increase = true;

            	switch (select) {
                	case 1:
						System.out.println("What should its price be? [Enter as a decimal number.]");
                	    double newPrice = input.nextDouble();
						
						double oldPrice = (carPriceQueue.accessCar(priceIndex)).getPrice();

						if(newPrice < oldPrice)
							increase = false; 
						
						//Update the entry in the price PQ for all cars.
						(carPriceQueue.accessCar(priceIndex)).setPrice(newPrice);
						carPriceQueue.carUpdated(priceIndex, increase);

						//Update the entry in the price PQ for the make/model.
						(MMPrices[MMTempIndex].accessCar(MMPriceIndex)).setPrice(newPrice);
						MMPrices[MMTempIndex].carUpdated(MMPriceIndex, increase);

						//Update the entry in the mileage PQ, both for all cars and for the make/model.
						(MMMileages[MMTempIndex].accessCar(MMMileageIndex)).setPrice(newPrice);
						(carMileageQueue.accessCar(mileageIndex)).setPrice(newPrice);
                	    break;
                	case 2:
						System.out.println("What should its mileage be? [Enter as a whole number.]");
                	    int newMileage = input.nextInt();

						double oldMileage = (carMileageQueue.accessCar(mileageIndex)).getMileage();

						if(newMileage < oldMileage)
							increase = false; 

						//Update the entry in the mileage PQ for all cars.
						(carMileageQueue.accessCar(mileageIndex)).setMileage(newMileage);
						carMileageQueue.carUpdated(mileageIndex, increase);

						//Update the entry in the mileage PQ for the make/model.
						(MMMileages[MMTempIndex].accessCar(MMMileageIndex)).setMileage(newMileage);
						MMMileages[MMTempIndex].carUpdated(MMMileageIndex, increase);

						//Update the entry in the price PQ, both for all cars and for the make/model.
						(carPriceQueue.accessCar(priceIndex)).setMileage(newMileage);
						(MMPrices[MMTempIndex].accessCar(MMPriceIndex)).setMileage(newMileage);
                	    break;
                	case 3:
						System.out.println("What should its color be? ");
                	    String color = input.next();
						
						//Update the color in the price and mileage PQs for all cars.
						(carPriceQueue.accessCar(priceIndex)).setColor(color);
						(carMileageQueue.accessCar(mileageIndex)).setColor(color);

						//Update the color in the price and mileage PQs for the make/model.
						(MMPrices[MMTempIndex].accessCar(MMPriceIndex)).setColor(color);
						(MMMileages[MMTempIndex].accessCar(MMPriceIndex)).setColor(color);
                	    break;
					default:
						break;
				}
			}
		}
	}
	
	//Remove a car from consideration.
	protected static void removeCar() {
	
		System.out.println("What is the VIN number of the car to remove?");
		String vin = input.next();

		int priceIndex = carPriceQueue.findIndirectionIndex(vin);
		int mileageIndex = carMileageQueue.findIndirectionIndex(vin);

		//Ascertain that the car exists.
		if(priceIndex == -1 ) {
			System.out.println("A car by that VIN number does not yet exist. Add it to the list!");
			return;
		}

		//Get the make and model of the car to be updated.
		Car tempCar = carPriceQueue.accessCar(priceIndex);
		String makeModel = tempCar.getMake() + "." + tempCar.getModel() + "$";

		int MMTempIndex =  findMakeModelIndex(makeModel);
		int MMPriceIndex = MMPrices[MMTempIndex].findIndirectionIndex(vin);
		int MMMileageIndex = MMMileages[MMTempIndex].findIndirectionIndex(vin);
		
		//Remove the car from both price and mileage PQs for all cars. 
		carPriceQueue.remove(priceIndex);
		carMileageQueue.remove(mileageIndex);

		//Remove the car from both price and mileage PQs for its make/model.
		MMPrices[MMTempIndex].remove(MMPriceIndex);
		MMMileages[MMTempIndex].remove(MMMileageIndex);

		return;
	}

	//Retrieve the car with the lowest price.
	protected static void lowPrice() {

		Car lowest = carPriceQueue.getMinimumCar(); 

		System.out.println();
		System.out.println("The lowest price car is currently VIN # " + lowest.getVin() + ":");
		System.out.println("It is a " + lowest.getColor() + " " + lowest.getMake() + " " + lowest.getModel() + " with " + lowest.getMileage() + " miles.");
		System.out.println("It is selling for " + lowest.getPrice() + " dollars.");
	}
	
	//Retrieve the car with the lowest mileage.
	protected static void lowMile() {

		Car lowest = carMileageQueue.getMinimumCar(); 

		System.out.println();
		System.out.println("The car with the least number of miles is currently VIN # " + lowest.getVin() + ";");
		System.out.println("It is a " + lowest.getColor() + " " + lowest.getMake() + " " + lowest.getModel()  + " with " + lowest.getMileage() + " miles.");
		System.out.println("It is selling for " + lowest.getPrice() + " dollars.");
	}

	//Retrieve the car of a certain make/model with the lowest price.
	protected static void lowPriceMM() {

		//Prompt the user for the make and model.
		System.out.println("What is the make of the car?");
		String make = input.next();
		System.out.println("What is the model of the car?");
		String model = input.next();

		//Find the PQ dealing with this make and model.
		String makeModel = (make + "." + model + "$");
		int MMTempIndex =  findMakeModelIndex(makeModel);

		Car lowest = MMPrices[MMTempIndex].getMinimumCar(); 

		if(lowest == null) {
			System.out.println();
			System.out.println("There aren't any cars with this make/model in consideration!");
			return;
		}

		System.out.println();
		System.out.println("The lowest price " + make + " " + model + " is currently VIN # " + lowest.getVin() + ";");
		System.out.println("It is a " + lowest.getColor() + " " + lowest.getMake() + " " + lowest.getModel()  + " with " + lowest.getMileage() + " miles.");
		System.out.println("It is selling for " + lowest.getPrice() + " dollars.");
	}

	//Retrieve the car of a certain make/model with the lowest mileage.
	protected static void lowMileMM() {

		//Prompt the user for the make and model.
		System.out.println("What is the make of the car?");
		String make = input.next();
		System.out.println("What is the model of the car?");
		String model = input.next();

		//Find the PQ dealing with this make and model.
		String makeModel = (make + "." + model + "$");
		int MMTempIndex =  findMakeModelIndex(makeModel);
		
		Car lowest = MMMileages[MMTempIndex].getMinimumCar();

		if(lowest == null) {
			System.out.println();
			System.out.println("There aren't any cars with this make/model in consideration!");
			return;
		}

		System.out.println();
		System.out.println("The " + make + " " + model + " with the least number of miles is currently VIN # " + lowest.getVin() + ";");
		System.out.println("It is a " + lowest.getColor() + " " + lowest.getMake() + " " + lowest.getModel()  + " with " + lowest.getMileage() + " miles.");
		System.out.println("It is selling for " + lowest.getPrice() + " dollars.");
	}

	//Read in a cars.txt file.
	protected static void readInFile() throws FileNotFoundException {
		//Create Scanner to read in cars from text file.
        Scanner s = new Scanner(new File("cars.txt"));
		
		
		//Skip past the first line; it is just the pattern of input. 
		s.nextLine();
		//Set the delimiter for s to be ":" or a new line. 
		s.useDelimiter(":|\\n|\\r");


		//Create a PQ for the cars initially. 
		carPriceQueue = new CarPriorityQueue('p');
		carMileageQueue = new CarPriorityQueue('m');

		MMPrices = new CarPriorityQueue[10];
		MMMileages = new CarPriorityQueue[10];
		MMLookup = new DLBTrie();
		MMIndex = 0;

		String vin;
		String make;
		String model;
		double price;
		int mileage;
		String color;
		String makeModel;
		int newCarIndex;

		//So long as there is a next line to the file:
		while(s.hasNextLine()) {

			//Read in the fields.
			vin = s.next();
			make = s.next();
			model = s.next();
			price = s.nextDouble();
			mileage = s.nextInt();
			color = s.next();

			//Skip to the next line for the next iteration's sake.
			s.nextLine();

			makeModel = make + "." + model + "$";

			//Search MMLookup for the makeModel index, adding it if necessary. 
			newCarIndex = findMakeModelIndex(makeModel);

			//Add the new car to the price and mileage PQs for its make/model. 
			MMPrices[newCarIndex].add(new Car(vin, make, model, price, mileage, color));
			MMMileages[newCarIndex].add(new Car(vin, make, model, price, mileage, color));

			//Add the new car to the price and mileage PQs for all cars.
			carPriceQueue.add(new Car(vin, make, model, price, mileage, color));
			carMileageQueue.add(new Car(vin, make, model, price, mileage, color));

		}

		//Now that the entire file has been read in, close the scanner. 
		s.close();
	}
	
	private static int findMakeModelIndex(String makeModel) {
		//Adding each letter besides the '$' is fine, even if that letter already exists. Duplicates will be skipped. 
		for(int i = 0; i < makeModel.length() - 1; i++) {
			MMLookup.add(makeModel.charAt(i), -1);
		}

		//Search for the '$'. If it exists, use its index.
		int potentialIndex = MMLookup.lookupSearch();

		if(potentialIndex > -1)
			return potentialIndex;

		//The make/model does not yet exist. 
		else {
			MMLookup.add('$', MMIndex);

			//Add a new PQ for both price and mileage.
			MMPrices[MMIndex] = new CarPriorityQueue('p');
			MMMileages[MMIndex] = new CarPriorityQueue('m');

			MMIndex++;
			return (MMIndex - 1);
		}
		
	} 
	
	//Resize the arrays dedicated to keeping track of make/model PQs.
	private static void resizeMMs() {
		int oldSize = MMPrices.length;

		CarPriorityQueue[] MMMTemp = new CarPriorityQueue[oldSize * 2];
		CarPriorityQueue[] MMPTemp = new CarPriorityQueue[oldSize * 2];

		//Copy over items.
		for(int i = 0; i < oldSize; i++) {
			MMMTemp[i] = MMMileages[i];
			MMPTemp[i] = MMPrices[i];
		}
		
		MMMileages = MMMTemp;
		MMPrices = MMPTemp;

		return;

	}
}
