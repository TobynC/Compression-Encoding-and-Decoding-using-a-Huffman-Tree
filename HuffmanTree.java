package collinsworth_Project4_2015;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Tobyn Collinsworth & rvolkers
 */
public class HuffmanTree
{
    // Huffman tree node class
    // Must store character, count, and 2 node references
    // Only needs one constructor that takes the 4 values
    private class HtNode
    {
        char ch;
        int count;
        HtNode left;
        HtNode right;

        public HtNode(char ch, int count, HtNode left, HtNode right)
        {
            this.ch = ch;
            this.count = count;
            this.left = left;
            this.right = right;
        }
    }

    // Hash table node class for symbol lookup
    // Must store character, count, Huffman code string, and node reference
    // Only needs 1 constructor that accepts the character and reference parameter
    private class LookupNode
    {
        private char ch;
        private int count;
        private String hcode;
        private LookupNode next;

        public LookupNode(char ch, LookupNode next)
        {
            this.ch = ch;
            this.count = 1;
            this.next = next;
        }
    }

    private final int HTSIZE = 1000;
    private HtNode root;                // root of Huffman tree
    private LookupNode[] lookupTable;  // hash table for frequency data and code lookup
    private String fileName;            // name of file to be encoded
    private OrderedLinkedList<HtNode> orderedList; // priority queue based on node's frequency value

    public HuffmanTree(String fname)
    {
        // Save the file name this tree will be build for
        this.fileName = fname;

        // Call method to read data from the file to be processed into a hash table      
        analyzeFile();

        // Call method to create an ordered list of nodes based on node frequency
        createOrderedList();

        // Build the Huffman Tree here in the constructor 
        try
        {
            while (orderedList.listCount() > 1)
            {
                HtNode leftNode = orderedList.deque();
                HtNode rightNode = orderedList.deque();

                HtNode newNode = new HtNode((char) 0, leftNode.count + rightNode.count, leftNode, rightNode);
                orderedList.insert(newNode, newNode.count);
            }
            root = orderedList.deque();
        } catch (Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }

        // Call method to put the Huffman codes into the hash table for encoding
        addCodeToHashTable(root, "");

        // Call method to output the contents of the lookup table
        displayLookupTable();
    }

    // Load the lookup table with frequency data from the specified file
    private void analyzeFile()
    {
        // Create the hash table based on size constant
        lookupTable = new LookupNode[HTSIZE];

        // create a file reader so we can read one character at a time
        try
        {
            try (FileReader fReader = new FileReader(this.fileName))
            {
                int charValue = fReader.read();
                // loop until all characters have been processed
                while (charValue != -1)
                {
                    int hashedIndex = charValue % HTSIZE;

                    // if a list does not exist, just create the node and store the node into the table
                    if (lookupTable[hashedIndex] == null)
                    {
                        lookupTable[hashedIndex] = new LookupNode((char) charValue, null);
                    }
                    // if a list does exist, search list to see if the character is already here
                    else
                    {
                        boolean found = false;
                        LookupNode currentNode = lookupTable[hashedIndex];
                        while (currentNode != null)
                        {
                            // if the character was found just increment its frequency count
                            if (currentNode.ch == (char) charValue)
                            {
                                lookupTable[hashedIndex].count++;
                                found = true;
                            }
                            // if the character was not found insert the new node at the front of the existing list 
                            else if (currentNode.next == null && found == false)
                            {
                                currentNode.next = new LookupNode((char) charValue, lookupTable[hashedIndex]);
                                break;
                            }
                            currentNode = currentNode.next;
                        }
                    }
                    charValue = fReader.read();
                }
                fReader.close();
            }
        } catch (Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }
    }

    // Create the ordered list of Huffman tree nodes from the lookup table data
    private void createOrderedList()
    {
        // Create an ordered linked list of huffman tree nodes
        orderedList = new OrderedLinkedList<>();

        // Create an HtNode for every character found in the hash table and insert
        for (int i = 0; i < lookupTable.length; i++)
        {
            // the tree node into ordered list based on that character's frequency
            // Process every non null list in the hash table
            if (lookupTable[i] != null)
            {
                // Create the Huffman node using the hash table node data
                // Insert the Huffman node based on the frequency of the character
                orderedList.insert(new HtNode(lookupTable[i].ch, lookupTable[i].count, null, null), lookupTable[i].count);

                LookupNode current = lookupTable[i];
                while (current.next != null)
                {
                    orderedList.insert(new HtNode(current.next.ch, current.next.count, null, null), current.next.count);
                    current = current.next;
                }
            }
        }
    }

    //       for every character in the huffman tree
    //      traverse the path to that node, building a string that represents the huffman code for that symbol
    //      when symbol reached, store the string into that symbol's entry in the hash table
    private void addCodeToHashTable(HtNode htnode, String s)
    {
        // Base case - Found a real symbol so add its huffman code to that symbol's entry in the hash table
        if (htnode.right == null && htnode.left == null)
        {
            updateHashTable(htnode.ch, s);
        }
        // Recursive step - No character at this node, need to recursively process the left and right subtrees
        // Add '0' to string when going left, add '1' to string going right
        else
        {
            addCodeToHashTable(htnode.left, s + "0");
            addCodeToHashTable(htnode.right, s + "1");
        }
    }

    // Store the Huffman code string into the hash table node for this character
    private void updateHashTable(char ch, String code)
    {
        // Get the hash table index for this character
        // Get the list from the hash table
        // Search the list for this character and store the code there
        for (int i = 0; i < lookupTable.length; i++)
        {
            if (lookupTable[i] != null)
            {
                if (lookupTable[i].ch == ch)
                {
                    lookupTable[i].hcode = code;
                }
            }
        }
    }

    // Dump the lookup table contents for debugging purposes
    private void displayLookupTable()
    {
        for (LookupNode node : lookupTable)
        {
            if (node != null)
            {
                System.out.println("Node-" + node.toString() + ": " + "char: " + node.ch + ", count: " + node.count + ", code: " + getHuffmanCode(node.ch));
            }
        }
    
    }

    // Create the encoded output file using the Huffman codes stored in the hash table
    public void encodeFile()
    {   
        try
        {
            // Get a FileReader to read the characters from the original file
            try (FileReader fReader = new FileReader("data.txt"))
            {
                // Create a BitStream to write the Huffman code bits to the output file
                BitStream bitStream = new BitStream(new File("data.txt.huff"), "w");
                
                int charValue = fReader.read();
                // For each character from the file, get its Huffman Code and add it to the output stream   
                while (charValue != -1)
                {
                    // Get the huffman code string for the character from the hash table
                    // Build the bits that represent the huffman code into an integer variable
                    // Bit operations needed.....
                    // x << 1 shifts all bits in x one position to the left... 
                    // To set the LSB of a value to 1, just do x |= 1
                    // To clear the LSB of a value to 0, just do x &= 0xfffffffe
                    String hcode = getHuffmanCode((char)charValue);
                    int bits = 0;
                    
                    for (int i = 0; i < hcode.length(); i++)
                    {
                        if (hcode.charAt(i) == '1')
                        {
                            bits <<= 1;
                            bits |= 1;
                        }
                        else
                        {
                            bits <<= 1;
                        }
                    }        
                    // Pass the bit stream writer the integer with the bit code and the number of bits in the code
                    bitStream.writeBits(bits, hcode.length());                    
                    charValue = fReader.read();
                }
                
                // Write any remaining bit information to the file by closing the bit stream
                bitStream.close();
                fReader.close();
            }
        } catch (IOException | NumberFormatException e)
        {
            System.out.println(e);
            System.exit(0);
        }

    }

    // Search the hash table for the Huffman code for the given character
    private String getHuffmanCode(char ch)
    {
        // Get hash value for ch and go to that list in the table        
        int index = ch % lookupTable.length;
        
        if (lookupTable[index].ch == ch)
        {
            return lookupTable[index].hcode;
        }
         // Search the list to find the character
        else
        {
            LookupNode temp = lookupTable[index];
            boolean found = false;
            while (temp.next != null && !found)
            {                
                if (temp.ch == ch)
                {
                    found = true;                    
                }
                temp = temp.next;
            }           
            if (found)
            {
                return temp.hcode;
            }
            else
            {
                return "Item Not Found.";
            }
        }       
    }

    // Recreate a data file from an encoded file
    public void decodeFile()
    {
        try
        {
            // Create a file writer to write characters to the output file
            // Create a bit stream reader to read the bits from the encoded file
            try (FileWriter writer = new FileWriter("decodedOutput.txt"))
            {
                BitStream bitStream = new BitStream(new File("data.txt.huff"), "r");
                
                HtNode currentNode = root;
                int bit;
                
                // Loop as long as there are bits to read
                while (bitStream.hasMoreBits())
                {
                     // Start decoding at root of Huffman tree
                    // When we have reached a leaf with a character, output that character
                    while (currentNode.left != null && currentNode.right != null)
                    {
                        bit = bitStream.readBits(1);
                        
                        // If a bit that is read is 0, traverse left, else traverse right
                        if (bit == 0)
                        {
                            currentNode = currentNode.left;
                        }
                        else
                        {
                            currentNode = currentNode.right;
                        }
                    }
                    writer.write(currentNode.ch);                    
                    currentNode = root;
                }
            }
        } catch (Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }
    }
}
