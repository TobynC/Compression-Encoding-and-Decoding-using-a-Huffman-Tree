package collinsworth_Project4_2015;

import java.util.*;
import java.io.*;

/**
 * This class takes care of the details of reading and writing bits to a file.
 * To be correctly read by a read instance of this class, the file must have
 * been written by a write instance of this class, and the output stream that
 * wrote it must have been closed by calling {@code close()}.
 *
 * @author Nikos
 */
public class BitStream
{

    /**
     * Buffer that temporarily stores bits and is flushed to the file when
     * enough bits are around
     */
    private ArrayList<Integer> stream;

    /**
     * The underlying file
     */
    private RandomAccessFile raf;

    /**
     * The mode with which the file was opened: "r" = read, "w" = write
     */
    private String mode;

    /**
     * The length in bits of the file (not necessarily a multiple of 8)
     */
    private int bitlength;

    /**
     * Creates a BitStream object and initializes the fields appropriately.
     *
     * @param file to be read/written
     * @param mode open mode: can be either "r" (read) or "w" (write)
     * @throws java.io.IOException
     *
     */
    public BitStream(File file, String mode) throws IOException
    {
        bitlength = 0;
        this.mode = new String(mode);
        stream = new ArrayList<>();

        if (mode.equals("w"))
        {
            raf = new RandomAccessFile(file, "rw");
            raf.writeInt(0); // save room for length later
        }
        else if (mode.equals("r"))
        {
            raf = new RandomAccessFile(file, "r");
            bitlength = raf.readInt();
            //sanity check -- reported file length should not be a lie
            long fileLength = file.length();
            if ((bitlength + 7) / 8 > fileLength - 4)
            {
                throw new IllegalArgumentException("Corrupt input file");
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "Mode should be either \"r\" or \"w\"");
        }
    }

    /**
     * Writes an integer as bits to the {@code BitStream}.
     *
     * @param data the int containing the bits to be written
     * @param n the number of bits to be written
     * @throws IOException if the bits cannot be written
     */
    public void writeBits(int data, int n) throws IOException
    {
        bitlength += n;
        for (int i = n - 1; i >= 0; i--)
        {
            stream.add((data >> i) & 1);
        }
        while (stream.size() >= 8)
        {
            raf.writeByte(bitsToInt(stream, 8));
        }
    }

    /**
     * Writes all the bits in the buffer to the file, padding with zeros if
     * necessary.
     *
     * @throws IOException if the stream cannot be flushed
     */
    private void flushBits() throws IOException
    {
        while (stream.size() % 8 != 0)
        {
            stream.add(0);
        }
        while (stream.size() >= 8)
        {
            raf.writeByte(bitsToInt(stream, 8));
        }
    }

    /**
     * Finalizes a bitstream. This method must be called in order that a read
     * instance can subsequently read the bitstream.
     *
     * @throws IOException if the stream cannot be closed
     */
    public void close() throws IOException
    {
        if (mode.equals("w"))
        {
            flushBits();
            raf.seek(0);
            raf.writeInt(bitlength);
        }
        raf.close();
    }

    /**
     * Reads a specified number of bits from the Bitstream.
     *
     * @param n the number of bits to be read
     * @return the bits actually read
     * @throws IOException if n bits are not available
     */
    public int readBits(int n) throws IOException
    {
        int bits;
        try
        {
            while (stream.size() < n)
            {
                byte b = raf.readByte();
                for (int i = 7; i >= 0; i--)
                {
                    stream.add((b >> i) & 1);
                }
            }
        } catch (EOFException e)
        {
            throw new IOException("Unexpected end of file");
        }
        bits = bitsToInt(stream, n);
        bitlength -= n;
        return bits;
    }

    /**
     * Converts a sequence of n bits to an int.
     *
     * @param bits is the sequence of bits.
     * @param n is the number of bits to use from this sequence.
     * @return an n-bit int from the bits of the sequence.
     */
    private static int bitsToInt(List<Integer> bits, int n)
    {
        int ret = 0;
        for (int i = 0; i < n; i++)
        {
            ret = (ret << 1) | bits.get(0);
            bits.remove(0);
        }
        return ret;
    }

    /**
     * For a read bitstream, returns {@code true} if the bitstream is not
     * exhausted.
     *
     * @return {@code true} if the bitstream is not exhausted
     */
    public boolean hasMoreBits()
    {
        return bitlength > 0;
    }

}
