/**
 * Project 4 for CS 1181
 * Creates a huffman tree based on character frequency from a file and then 
 * uses a bitstream to encode the data into a new file. The encoded file is then
 * decoded using the values found in the huffman tree.
 */
package collinsworth_Project4_2015;

/**
 * @author Tobyn Collinsworth & rvolkers
 * CS1181
 * Instructor: R. Volkers
 * TA: R. Brant
 */
public class TestDriver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        HuffmanTree ht = new HuffmanTree("data.txt");
        ht.encodeFile();
        System.out.println("Encoding done");
        ht.decodeFile();
        System.out.println("Decoding done");            
    }    
}
