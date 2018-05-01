//Emily Higgs
//This is a version of the DLB Trie I used previously, modified specifically for use with CarTracker.java and project 3.

public class DLBTrie {

	Node current;
	Node first;
	int numberOfWords;

	public DLBTrie() {
		numberOfWords = 0;
		first = new Node('^');
		current = first;
	}

	private class Node {
		char value;
		Node nextLetter;
		Node nextOption;
		int index;

		private Node(char contents) {
			value = contents;
			nextLetter = null;
			nextOption = null;
			index = -1;
		}
		private Node(char contents, int i) {
			value = contents;
			nextLetter = null;
			nextOption = null;
			index = i;
		}

		private void setNextLetter(Node next) {
			nextLetter = next;
		}

		private void setNextOption(Node next) {
			nextOption = next;
		}

		private void setValue(char val) {
			value = val;
		}
		
		private char getValue() {
			return value;
		}

		private Node getNextLetter() {
			return nextLetter;
		}

		private Node getNextOption() {
			return nextOption;
		}

		private void setIndex(int i) {
			index = i;
		}

		private int getIndex() {
			return index;
		}
	}

	//Add a character of the VIN. This method should be called in a loop such that the entire VIN is being handled character at a time,
	//as this method will reset the current character after a '$' character. 
	protected void add(char letter, int index) {

		char comparison = current.getValue();

		while(comparison != '^') {
			//If the values match, move to the next letter.
			if(comparison == letter && letter != '$') {
				current = current.getNextLetter();
				return;
			}
			//If the values match and the word is ending, reset. No VIN needs a duplicate.
			else if(comparison == letter && letter == '$') {
				current = first;
				return;
			}
			//In this case, values are not equal. Move the linked list forward.
			else {
				if(current.getNextOption() == null && letter != '$') {
					current.setNextOption(new Node(letter));
					current = current.getNextOption();

					current.setNextLetter(new Node('^'));
					current = current.getNextLetter();
					return;
				}
				else if (current.getNextOption() == null && letter == '$') {
					current.setNextOption(new Node(letter, index));
					
					current = first;
					numberOfWords++;
					return;
				}
				else {
					current = current.getNextOption();
					comparison = current.getValue();
				}
			}
		}

		//If this is the very first VIN added to the DLBTrie, start the first letter off. 
		if(comparison == '^' && first.getValue() == '^') {
			first.setValue(letter);
			
			first.setNextLetter(new Node('^'));
			current = first.getNextLetter();
		}
		//If you're at the end of the word, add it and reset. 
		else if(comparison == '^' && letter == '$') {
			current.setValue(letter);
			current.setIndex(index);

			current = first;
			numberOfWords++;
		}
		//If the letter in question currently doesn't exist in this linked list, add it.
		else if(comparison == '^') {
			current.setValue(letter);
			current.setNextLetter(new Node('^'));
			current = current.getNextLetter();
		}
	}

	//Searches for the character at a given level of the trie. If found, moves along and returns true.
	protected boolean search(char letter) {
		Node searching;

		//Search until until the letter is found.
		while(current.getValue() != letter) {
			searching = current.getNextOption();
			
			//If there is a next option, move to it.
			if(searching != null)
				current = searching;
			//If no next option exists, the word won't be found in the trie at all. Return false.
			else
				return false;
		}
		//If this point is reached, the letter has been found. Move current deeper in the trie, in preparation for the next letter.
		current = current.getNextLetter();
		return true;
	}

	//Specialized search intended for MMLookup use only. Searches for a '$' character and returns its index if found.
	public int lookupSearch() {
		Node searching;

		//Search  until the letter is found.
		while(current.getValue() != '$') {
			searching = current.getNextOption();
			
			//If there is a next option, move to it.
			if(searching != null)
				current = searching;
			//If no next option exists, the make/model has yet to be added. Return -1
			else
				return -1;
		}
		//If this point is reached, the make/model has been added already. Grab its index.
		int result = current.getIndex();
		resetWord();
		return result;
	}

	//This method is called once it is confirmed that a word exists in the DLB. A dollar sign should be passed in and
	//the VIN's index returned. 
	protected int getVinIndex(char character) {
		
		//If for some reason an error contains -3 as an out-of-bounds index, the issue can likely be tracked back to this point. 
		int result = -3;

		if(current.getValue() == character) 
			result = current.getIndex();
		
		resetWord();
		return result;

	}

	protected void setVinIndex(char character, int index) {

		if(current.getValue() == character) 
			current.setIndex(index);
		
		resetWord();
		return;
	}

	protected void resetWord() {
		current = first;
	}
}
