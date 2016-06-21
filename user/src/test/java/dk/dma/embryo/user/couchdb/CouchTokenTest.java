package dk.dma.embryo.user.couchdb;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jesper Tejlgaard on 6/21/16.
 */
public class CouchTokenTest {

    @Test
    public void testGenerate(){
        CouchToken couchToken = new CouchToken("hep hey. I am the secret");

        String result = couchToken.generate("JohnDoe");

        Assert.assertNotEquals("JohnDoe", result);
        Assert.assertNotEquals("hep hey. I am the secret", result);
        // The expected value has been taken from the calculation and is as such a bad expectation value
        // The calculated values does in the application however match the calculated values produced by CouchDB.
        Assert.assertEquals("697f494fa41ad6cbf6f56f90d6614d8645daa95a", result);
    }
}
