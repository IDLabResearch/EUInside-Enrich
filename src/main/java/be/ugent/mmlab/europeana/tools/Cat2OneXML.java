package be.ugent.mmlab.europeana.tools;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.*;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/12/14.
 *
 * Unifies concatenated RDF/XML files (in one file) to one RDF/XML file
 *
 */
public class Cat2OneXML {
    public static void main(String[] args) throws IOException, CompressorException {

        File inputFile = new File(args[0]);

        boolean rdfTagWritten = false;

        // determine output file
        String baseName = FilenameUtils.getBaseName(inputFile.getAbsolutePath());
        if (baseName.endsWith(".xml") || baseName.endsWith("rdf")) {
            baseName = FilenameUtils.getBaseName(baseName);
        }
        File outputFile = new File(inputFile.getParentFile(), baseName + "_merged.rdf.gz");

        CompressorOutputStream cout = new CompressorStreamFactory().createCompressorOutputStream("gz", new BufferedOutputStream(new FileOutputStream(outputFile)));
        CompressorInputStream cin = new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(cin, "UTF-8"));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(cout, "UTF-8"))) {

            writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("<rdf:RDF")) {
                    while (!(line == null || line.endsWith(">"))) {
                        line = line.trim();
                        if (!rdfTagWritten) {
                            writer.append(line).append('\n');
                        }
                        line = reader.readLine();
                    }
                    if (!(rdfTagWritten || line == null)) {
                        writer.append(line.trim()).append('\n');
                    }
                    rdfTagWritten = true;
                } else if (!(line.startsWith("<?xml") || line.startsWith("</rdf"))) {
                    writer.append(line).append('\n');
                }
            }

            writer.append("</rdf:RDF>\n");
        }

    }
}
