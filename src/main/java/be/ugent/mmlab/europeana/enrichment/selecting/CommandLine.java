package be.ugent.mmlab.europeana.enrichment.selecting;

import java.io.IOException;
import java.util.List;

/**
 * Created by ghaesen on 1/8/14.
 */
public class CommandLine implements UserInterface {
    @Override
    public String makeSelection(final String subject, final List<String> resources) {
        System.out.println("**** Command line interface for selection ****");
        System.out.println("**** Subject: " + subject);
        for(int i = 0; i < resources.size(); i++) {
            System.out.println("  " + i + ". " + resources.get(i));
        }
        System.out.println("  " + resources.size() + ". Stop");
        System.out.println("Make your choice: ");

        int choice = -1;
        try {
            String input = readFromInputStream();
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (choice >= 0 && choice < resources.size()) {
            return resources.get(choice);
        }
        // stop the process
        return null;
    }

    private String readFromInputStream() {
        StringBuilder str = new StringBuilder();
        try {
            int input;
            while ((input = System.in.read()) != -1) {
                char c = (char)input;
                if (c == '\n' || c == 'r') {
                    break;
                }
                str.append(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
