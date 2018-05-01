//Emily Higgs

public class CarPriorityQueue {

	private int nextIndex;
	private char priority;
	private Car[] heap;
	private DLBTrie indirection;


	public CarPriorityQueue(char priorityMode) {
		nextIndex = 0;
		priority = priorityMode;
		heap = new Car[10];
		indirection = new DLBTrie();
	}

	//Add a car to the priority queue. 
	public void add(Car newCar) {

		//If there is not enough room in the heap, then resize it. 
		if(nextIndex == heap.length)
		{
			resize();
		}

		//Place the newest car in the next available index initially. 
		heap[nextIndex] = newCar;

		//Add the new car to the indirection data structure. 
		addToDLB(newCar.getVin(), nextIndex);

		//Swim the car up to its proper place in the heap.
		swim();

		//Increase the next available index for any future adds. 
		nextIndex++;
		
		return;
	}

	//To maintain the heap property, swim an added element up to its proper index. 
	private void swim() {
			
			//If this is the first element being added, there is no parent. Just return.
			if(nextIndex == 0)
				return;

			//Get the index of the current node's parent.
			int parent = (int)Math.floor(nextIndex / 2.0);

			//Use the priority to determine the property that the algorithm will use for comparisons and branch accordingly. 
			if(priority == 'p')
				swimPrice(parent, nextIndex);
			else if(priority == 'm')
				swimMileage(parent, nextIndex);

			return;
	}

	//Recursive method that uses price to restore heap property. 
	private void swimPrice(int parent, int insertionPoint) {
		
		//Base case: If the parent index has run off the front of the array, return. 
		if(parent < 0)
			return;

		//Grab both the current car and its parent's prices. 
		double parentPrice = heap[parent].getPrice();
		double insertionPrice = heap[insertionPoint].getPrice();


		if(parentPrice > insertionPrice) {
			//Swap the cars. 
			Car tempCar = heap[parent];
			heap[parent] = heap[insertionPoint];
			heap[insertionPoint] = tempCar;

			//Update the indirection index of the current and previous parent.
			updateIndirection(heap[insertionPoint].getVin(), insertionPoint);
			updateIndirection(heap[parent].getVin(), parent);

			//Focus on the new spot where the insertion lives.  
			insertionPoint = parent;

			//Calculate the new parent. 
			int newParent = (int)Math.floor(insertionPoint / 2.0);


			//Recurse on these new indices. 
			swimPrice(newParent, insertionPoint);
		}
	}

	//Recursive method that uses mileage to restore heap property. 
	private void swimMileage(int parent, int insertionPoint) {
		
		//Base case: If the parent index has run off the front of the array, return. 
		if(parent < 0)
			return;

		//Grab both the current car and its parent's mileages. 
		int parentMileage = heap[parent].getMileage();
		int insertionMileage = heap[insertionPoint].getMileage();


		if(parentMileage > insertionMileage) {
			//Swap the cars. 
			Car tempCar = heap[parent];
			heap[parent] = heap[insertionPoint];
			heap[insertionPoint] = tempCar;

			//Update the indirection index of the current and previous parent. 
			updateIndirection(heap[insertionPoint].getVin(), insertionPoint);
			updateIndirection(heap[parent].getVin(), parent);

			//Focus on the new spot where the insertion lives.  
			insertionPoint = parent;

			//Calculate the new parent. 
			int newParent = (int)Math.floor(insertionPoint / 2.0);

			//Recurse on these new indices. 
			swimMileage(newParent, insertionPoint);
		}
	}

	//Remove a particular car from consideration.
	public void remove(int index) {

		//If there is only one car in the PQ, just delete it and return. 
		if((nextIndex - 1) == index) {
			nextIndex--;
			updateIndirection(heap[index].getVin(), -1);
			return;
		}
			
		//Swap the car to be removed with the one in the last occupied index.
		nextIndex--;

		Car tempCar = heap[index];
		heap[index] = heap[nextIndex];
		heap[nextIndex] = tempCar;

		//(At this point, nextIndex is the car to be excluded, so consider it an empty cell.)

		//Update other swapped car index and delete entry for removed car (Indirection). 
		updateIndirection(heap[index].getVin(), index);
		updateIndirection(heap[nextIndex].getVin(), -1);

		//Sink the other swapped car down, to maintain heap property. 
		if(priority == 'p')
			sinkPrice(index);
		else if(priority == 'm')
			sinkMileage(index);


		return;
	}

	//Sink a particular entry based on its price attribute. This maintains the heap property. 
	private void sinkPrice(int index) {
		if((index * 2 + 1) >=  nextIndex)
			return;

		int tempIndex = index * 2 + 1;
		double tempPrice = heap[tempIndex].getPrice();
		

		//If a node has two children, store the lowest of their prices.  
		if((index * 2 + 2) < nextIndex) {
			if(tempPrice > heap[index * 2 + 2].getPrice()) {
				tempIndex++;
				tempPrice = heap[tempIndex].getPrice();
			}
		}

		//If a child node has a lower price, swap the current and child nodes. 
		if(tempPrice < heap[index].getPrice()) {
			Car tempCar = heap[tempIndex];
			heap[tempIndex] = heap[index];
			heap[index] = tempCar;


			//Update the indirection index of those cars at "index" and "tempIndex".
			updateIndirection(heap[tempIndex].getVin(), tempIndex);
			updateIndirection(heap[index].getVin(), index);


			//Recurse on the node swapped into.  
			sinkPrice(tempIndex);
		}
	}

	//Sink a particular entry based on its mileage attribute. This maintains the heap property. 
	private void sinkMileage(int index) {
		if((index * 2 + 1) >=  nextIndex)
			return;

		int tempIndex = index * 2 + 1;
		double tempMileage = heap[tempIndex].getMileage();
		

		//If a node has two children, store the lowest of their mileages.  
		if((index * 2 + 2) < nextIndex) {
			if(tempMileage > heap[index * 2 + 2].getMileage()) {
				tempIndex++;
				tempMileage = heap[tempIndex].getMileage();
			}
		}

		//If a child node has a lower mileage, swap the current and child nodes. 
		if(tempMileage < heap[index].getMileage()) {
			Car tempCar = heap[tempIndex];
			heap[tempIndex] = heap[index];
			heap[index] = tempCar;


			//Update the indirection index of those cars at "index" and "tempIndex".
			updateIndirection(heap[tempIndex].getVin(), tempIndex);
			updateIndirection(heap[index].getVin(), index);

			//Recurse on the node swapped into.  
			sinkMileage(tempIndex);
		}
	}

	public Car getMinimumCar() {
	
		return heap[0];
	}

	//Take a VIN/Makemodel and search a DLB for its index number. 
	public int findIndirectionIndex(String vin) {

		boolean found = true;
		int index = 0;

		while(index < 17 && found) {
			found = indirection.search(vin.charAt(index));
			index++;
		}

		if(found) {
			index = indirection.getVinIndex('$');
			indirection.resetWord();
			return index;
		}
		else {
			index = -1;
			indirection.resetWord();
			return index;
		}
	}

	//Return a PQ's priority/which attribute it is sorting on.
	private char getPriority() {
		return priority;
	}

	//Return a car at a particular index.
	public Car accessCar(int index) {
		return heap[index];
	}

	//If the array backing the heap is full, resize it. 
	private void resize() {
		int newSize = heap.length * 2;
		Car[] newHeap = new Car[newSize];


		//For every car in the heap, copy it over to the expanded heap. 
		for(int i = 0; i < heap.length; i++) {
			newHeap[i] = heap[i];
		}

		heap = newHeap;

		return;
	}

	//Take a VIN to add to the indirection data structure, the DLB. 
	private void addToDLB(String vinNumber, int index) {
		
		//Add a '$' to the end of the word. This is a relic from the old DLB trie that allows the program to differentiate the end of a VIN
		//and accordingly store its index. 
		vinNumber += "$";

		//Iterate through every character in the VIN, adding each one to the DLB.  
		for(int i = 0; i < vinNumber.length(); i++) {
			indirection.add(vinNumber.charAt(i), index);
		}
	}

	//Update the DLB, given a car's VIN and its new index.
	private void updateIndirection(String vinNumber, int index) {

		boolean found = true;
		int i = 0;

		while(i < 17 && found) {
			found = indirection.search(vinNumber.charAt(i));
			i++;
		}
		if(found) {
			indirection.setVinIndex('$', index);
		}

		indirection.resetWord();
		return;
	}

	//This method is called when a car has been updated; it will sink or swim that car accordingly, based on
	//the priority that was changed and whether that priority's value increased or decreased.
	public void carUpdated(int index, boolean increase) {

		//If the field updated has increased, then the car might need to sink. 
		if(increase) {
			//Use priority to determine what type of sinking needs to occur. 
			if(getPriority() == 'p')
				sinkPrice(index);
			else if (getPriority() == 'm')
				sinkMileage(index);
		}
		else {

			int parent = (int)Math.floor(index / 2.0);

			//Use priority to determine what type of swimming needs to occur. 
			if(getPriority() == 'p')
				swimPrice(parent, index);
			else if (getPriority() == 'm')
				swimMileage(parent, index);
		}
	}
}
