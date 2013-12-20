package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.run.AutoEnrich;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

/**
 * Created by ghaesen on 12/9/13.
 */
public class TestGalileo {

    private final File tempTDBDir;

    public TestGalileo() {
        tempTDBDir = new File(System.getProperty("java.io.tmpdir"), "___TestTDBStore");
        System.out.println("Temporary TDB store: " + tempTDBDir);
    }



    @Test
    public void testAutoEnricher() {

        AutoEnrich autoEnrich = new AutoEnrich();
        URL resourceFileUrl = getClass().getClassLoader().getResource("one_example.rdf");
        assertNotNull(resourceFileUrl);
        String inputFile = resourceFileUrl.getFile();
        autoEnrich.enrich(inputFile, tempTDBDir.getPath(), true);
    }
}
