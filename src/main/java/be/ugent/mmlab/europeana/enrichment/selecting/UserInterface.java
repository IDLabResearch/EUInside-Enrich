package be.ugent.mmlab.europeana.enrichment.selecting;

import java.util.Set;

/**
 * Created by ghaesen on 1/8/14.
 */
public interface UserInterface {

    /**
     * As long as there is need for disambiguation, this method is called. It is to disambiguate one statement.
     * @param subject   The subject; what needs to be disambiguated.
     * @param resources The possible options.
     * @return One of the given options. To stop before the process is done, an implementation can return null.
     */
    public String makeSelection(final String subject, final Set<String> resources);
}
