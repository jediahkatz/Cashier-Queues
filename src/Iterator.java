
public class Iterator<E> {
	private Node<E> current;
	private int index = -1;
	
	public Iterator(Queue<E> list) {
		current = list.getHead();
	}
	
	//move to the next Node in the list and return it
	public Node<E> getNext() throws ElementNotFoundException {
		if(!hasNext()) throw new ElementNotFoundException();
		Node<E> temp = current;
		current = current.getNext();
		index++;
		return temp;
	}
	
	//returns false if the current Node is the last one
	public boolean hasNext() {
		if(current == null) return false;
		return true;
	}
	
	//index of the last element called
	public int getIndex() {
		return index;
	}
	
}