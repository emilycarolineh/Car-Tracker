# Car-Tracker
This program was originally submitted as a classwork assignment focusing on mastery of advanced application of priority queues- specifically, priority queues supported by heaps with indirection. At the highest level, this program is a tool to aid in the considerations of car buying through its sorting cars by price and mileage. 'documentation.txt' contains justifications for various implementation decisions, including runtime complexity considerations. 'cars.txt' is the default text file read in by the program and contains the initial cars added to the priority queue; for purposes of testing, it contains an example of the format of cars to be loaded.

## Program Behavior
The terminal-based menu found in CarTracker.java provides options to: add a car to consideration, update attributes of a car already in consideration, remove a car from consideration, retrieve the lowest price car (overall or by make/model), or retrieve the lowest mileage car (overall or by make/model). An updated version of 'cars.txt' is not written back to the file after the program terminates.

## How to Run
To run this program, all files must reside in the same directory. Compile on the command line with 'javac CarTracker.java' and run with 'java CarTracker'.
