package be.ugent.mmlab.europeana.run;

import be.ugent.mmlab.europeana.enrichment.Enricher;
import be.ugent.mmlab.europeana.kb.TDB.TDBStore;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by ghaesen on 12/20/13.
 */
public class AutoEnrich {

    Logger logger = LogManager.getLogger(this.getClass().getName());

    public void enrich(final String inputFile, final String storePath, final boolean overwrite) {
        logger.debug("input file: [{}]; store path: [{}], overwrite: [{}].", inputFile, storePath, overwrite);

        File storeDir = new File(storePath);
        if (overwrite && storeDir.exists()) {
            try {
                FileUtils.deleteDirectory(storeDir);
            } catch (IOException e) {
                System.err.println("Could not delete " + storeDir);
                System.out.println(e.getMessage());
                return;
            }
        }

        // determine type of file, or the language of RDF encoding
        String inputLanguage = determineInput(inputFile);

        // add triples to store
        TDBStore store = new TDBStore(storePath);
        try {
            store.addFromFile(inputFile, inputLanguage);
            logger.debug("Triples loaded!");
        } catch (IOException e) {
            logger.error("Could not store triples", e);
            return;
        }

        Enricher enricher = new Enricher();
        enricher.enrich(store.getDataset());
    }

    private String determineInput(String inputFile) {
        String type = "N-TRIPLE";
        if (inputFile.contains(".rdf")) type = "RDF/XML";
        else if (inputFile.contains(".n3")) type = "N3";
        else if (inputFile.contains(".ttl")) type = "TURTLE";
        return type;
    }

    public static void printHelp(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("AutoEnrich", "Enriches Europeana metadata. Reads metadata from a given RDF file in a triple store. Then it automatically queries DBPedia, and links to this data. The data is then stored in the triple store, but it still needs to be disambiguated.", options, "");
    }

    public static void main(String[] args) {

        String defaultStorePath = System.getProperty("java.io.tmpdir") + File.separator + "___TDBStore";

        Options options = new Options();
        options.addOption("h", "help", false, "Show this help.");
        options.addOption("o", "overwrite", false, "Overwrite the directory of the file based triple store, if it exists.");
        //options.addOption("s", "store", true, "The directory of the file based triple store. If not given: " + defaultStorePath);

        Option inputOption = OptionBuilder.withArgName("input file")
                .withLongOpt("input")
                .withDescription("Required. The input RDF file. Use extension .xml for xml format, .n3 for n triple format or .ttl for turtle format.")
                .isRequired(true)
                .hasArg()
                .create('i');
        options.addOption(inputOption);

        Option storeOption = OptionBuilder.withArgName("store path")
                .withLongOpt("store")
                .withDescription("The directory of the file based triple store. If not given: " + defaultStorePath)
                .hasArg()
                .create('s');
        options.addOption(storeOption);

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                printHelp(options);
                return;
            }
            String inputPath = commandLine.getOptionValue('i');
            String storePath = commandLine.hasOption('s') ? commandLine.getOptionValue('s') : defaultStorePath;
            boolean overwrite = commandLine.hasOption('o');

            AutoEnrich autoEnrich = new AutoEnrich();
            autoEnrich.enrich(inputPath, storePath, overwrite);

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            printHelp(options);
        }
    }
}
