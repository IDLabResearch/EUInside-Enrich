package be.ugent.mmlab.europeana.run;

//import be.ugent.mmlab.europeana.enrichment.auto.Enricher;
//import be.ugent.mmlab.europeana.enrichment.selecting.UserInterface;
//import be.ugent.mmlab.europeana.kb.TDB.TDBStore;
//import org.apache.commons.cli.*;
//
//import java.io.UnsupportedEncodingException;

/**
 * Created by ghaesen on 1/8/14.
 */
public class PhaseTwo extends AbstractRun {

//    public void disambiguate(final String storePath, final UserInterface userInterface) throws UnsupportedEncodingException {
//        TDBStore store = new TDBStore(storePath);
//        Enricher enricher = new Enricher();
//        enricher.phaseTwo(store.getDataset(), userInterface);
//    }
//
//    public void run(String[] args) throws UnsupportedEncodingException {
//        Options options = new Options();
//        options.addOption("h", "help", false, "Show this help.");
//        Option storeOption = OptionBuilder.withArgName("store path")
//                .withLongOpt("store")
//                .withDescription("The directory of the file based triple store. If not given: " + getDefaultStorePath())
//                .hasArg()
//                .create('s');
//        options.addOption(storeOption);
//        // TODO: option to choose user interface. Default: command line
//
//        boolean printHelp = false;
//        CommandLineParser parser = new BasicParser();
//        try {
//            CommandLine commandLine = parser.parse(options, args);
//            if (commandLine.hasOption('h')) {
//                printHelp = true;
//                return;
//            }
//            String storePath = commandLine.hasOption('s') ? commandLine.getOptionValue('s') : getDefaultStorePath();
//            UserInterface userInterface = new be.ugent.mmlab.europeana.enrichment.selecting.CommandLine();
//            disambiguate(storePath, userInterface);
//        } catch (ParseException e) {
//            System.out.println(e.getMessage());
//            printHelp = true;
//        } finally {
//            if (printHelp) {
//                printHelp(options, "Disambiguates data added in phase one, by user interaction.");
//            }
//        }
//
//    }
//
//    public static void main(String[] args) throws UnsupportedEncodingException {
//        PhaseTwo phaseTwo = new PhaseTwo();
//        phaseTwo.run(args);
//    }
}
