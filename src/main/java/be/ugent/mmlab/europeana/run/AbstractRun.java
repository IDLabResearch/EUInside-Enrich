package be.ugent.mmlab.europeana.run;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by ghaesen on 1/8/14.
 */
public abstract class AbstractRun {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    protected void printHelp(final Options options, final String help) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.getClass().getSimpleName(), help, options, "");
    }

    protected static String getDefaultStorePath() {
        return System.getProperty("java.io.tmpdir") + File.separator + "___TDBStore";
    }
}
