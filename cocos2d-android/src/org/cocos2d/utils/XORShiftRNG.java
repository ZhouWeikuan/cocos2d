package org.cocos2d.utils;

import java.util.Random;


/** This is an UNSAFE (ie, the internal state can be changed if used concurrently)
 *  implementation of Uncommon Math's XORShiftRNG.
 *  
 *  It is entirely pseudorandom, but works will for the degree of randomness required
 *  by graphics engines.  Also, it is FAST :D
 *  
 * @author dustinewan
 *
 */
public class XORShiftRNG extends Random {
	private static final long serialVersionUID = -7988866319180656512L;

	private static final int SEED_SIZE_BYTES = 20; // Needs 5 32-bit integers.

    // Previously used an array for state but using separate fields proved to be
    // faster.
	private int state1, state2, state3, state4, state5 = 0;

    private final byte[] seed;

    /**
     * Creates a new RNG and seeds it using the default seeding strategy.
     */
    public XORShiftRNG()
    {
    	this(generatePseudoSeed());
        //this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));    	
    }


    private static byte[] generatePseudoSeed() {
    	/** long is 64 bits */
    	long a = System.currentTimeMillis();
    	long b = System.nanoTime();
    	
    	byte[] seedsample = new byte[SEED_SIZE_BYTES];
    	
    	/** fill the seed */
    	for (int i = 0; i < 10; i++)
    		seedsample[i] = (byte)((a >> i * 8) & 0xFF);
    	
    	for (int i = 0; i < 10; i++)
    		seedsample[i + 10] = (byte)((b >> i * 8) & 0xFF);
    	
    	return seedsample;
	}


	/**
     * Creates an RNG and seeds it with the specified seed data.
     * @param seed The seed data used to initialise the RNG.
     */
    public XORShiftRNG(byte[] seed)
    {
        if (seed == null || seed.length != SEED_SIZE_BYTES)
        {
            throw new IllegalArgumentException("XOR shift RNG requires 160 bits of seed data.");
        }
        this.seed = seed.clone();
        int[] state = convertBytesToInts(seed);
        this.state1 = state[0];
        this.state2 = state[1];
        this.state3 = state[2];
        this.state4 = state[3];
        this.state5 = state[4];
    }

    /**
     * Convert an array of bytes into an array of ints.  4 bytes from the
     * input data map to a single int in the output data.
     * @param bytes The data to read from.
     * @return An array of 32-bit integers constructed from the data.
     * @since 1.1
     */
    public static int[] convertBytesToInts(byte[] bytes)
    {
        if (bytes.length % 4 != 0)
        {
            throw new IllegalArgumentException("Number of input bytes must be a multiple of 4.");
        }
        int[] ints = new int[bytes.length / 4];
        for (int i = 0; i < ints.length; i++)
        {
            ints[i] = convertBytesToInt(bytes, i * 4);
        }
        return ints;
    }
    
    private static final int BITWISE_BYTE_TO_INT = 0x000000FF;
    
    /**
     * Take four bytes from the specified position in the specified
     * block and convert them into a 32-bit int, using the big-endian
     * convention.
     * @param bytes The data to read from.
     * @param offset The position to start reading the 4-byte int from.
     * @return The 32-bit integer represented by the four bytes.
     */
    public static int convertBytesToInt(byte[] bytes, int offset)
    {
        return (BITWISE_BYTE_TO_INT & bytes[offset + 3])
                | ((BITWISE_BYTE_TO_INT & bytes[offset + 2]) << 8)
                | ((BITWISE_BYTE_TO_INT & bytes[offset + 1]) << 16)
                | ((BITWISE_BYTE_TO_INT & bytes[offset]) << 24);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getSeed()
    {
        return seed.clone();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected int next(final int bits)
    {
        final int t = (state1 ^ (state1 >> 7));
        state1 = state2;
        state2 = state3;
//        state3 = state4;
//        state4 = state5;
//        state5 = (state5 ^ (state5 << 6)) ^ (t ^ (t << 13));
//        return ((state2 + state2 + 1) * state5) >>> (32 - bits);
        state3 = (state3 ^ (state3 << 6)) ^ (t ^ (t << 13));
        return ((state2 + state2 + 1) * state3) >>> (32 - bits);
    }
}
