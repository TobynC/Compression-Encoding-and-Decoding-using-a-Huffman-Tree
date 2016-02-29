package collinsworth_Project4_2015;

/**
 * @author rvolkers
 * @param <T>
 */
public class OrderedLinkedList<T>
{

    private class Node<T>
    {

        // NOTE: The member variables are public so the 
        // methods of the OrderedLinkedList class have direct access to them
        // NO setters and getters are needed - more efficient access.

        public T payload;
        public int keyValue;
        public Node next;

        // Explicit value constructor for the Node class

        public Node(T payload, int value)
        {
            this.payload = payload;
            keyValue = value;
            next = null;
        }
    }

    private Node<T> first;
    private int count = 0;

    public void insert(T payload, int key)
    {
        Node<T> node = new Node<>(payload, key);

        if (first == null || key < first.keyValue)
        {
            node.next = first;
            first = node;
        }
        else
        {
            Node<T> temp = first;
            while (temp.next != null && key > temp.next.keyValue)
            {
                temp = temp.next;
            }
            node.next = temp.next;
            temp.next = node;
        }
        count++;
    }

    // Simply remove the first item from the list and return the payload
    public T deque() throws Exception
    {
        if (first == null)
        {
            throw new Exception("Queue is empty");
        }
        T item = first.payload;
        first = first.next;
        count--;
        return item;
    }

    // Returns the number of nodes in the list.    
    public int listCount()
    {
        return count;
    }

    // Get a value based on position. The first item is considered position 1

    public T getValue(int pos)
    {
        Node temp = first;

        if (pos <= 0 || pos > count)
        {
            return null;
        }
        else
        {
            for (int i = 1; i < pos; i++)
            {
                temp = temp.next;
            }
            return (T) temp.payload;
        }
    }
}
