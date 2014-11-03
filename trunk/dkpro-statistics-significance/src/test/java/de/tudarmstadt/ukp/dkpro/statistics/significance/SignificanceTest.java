package de.tudarmstadt.ukp.dkpro.statistics.significance;

import static org.junit.Assert.*;

import org.apache.commons.math.MathException;
import org.junit.Test;

public class SignificanceTest
{

    @Test
    public void testTestCorrelations() throws MathException
    {
        assertTrue(Significance.testCorrelations(0.5, 0.74, 68, 68, 0.2));
        assertTrue(Significance.testCorrelations(0.5, 0.74, 68, 68, 0.1));
        assertTrue(Significance.testCorrelations(0.5, 0.74, 68, 68, 0.05));
        assertFalse(Significance.testCorrelations(0.5, 0.74, 68, 68, 0.01));

        assertTrue(Significance.testCorrelations(0.5, 0.7, 100, 100, 0.1));
        assertTrue(Significance.testCorrelations(0.5, 0.7, 100, 100, 0.05));
        assertFalse(Significance.testCorrelations(0.5, 0.7, 100, 100, 0.01));
    
        assertFalse(Significance.testCorrelations(0.5, 0.5, 100, 100, 0.99));
}

}
