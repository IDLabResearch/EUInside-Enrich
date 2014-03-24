package be.ugent.mmlab.europeana.tools;

import be.ugent.mmlab.europeana.enrichment.misc.Normalizer;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.StringTokenizer;
import java.util.concurrent.*;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/20/14.
 */
public class LuceneIndexWriter {
    private static Version lVersion = Version.LUCENE_47;

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final int nrProcessors = Runtime.getRuntime().availableProcessors();
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(nrProcessors + 1);
    private final ExecutorService executorService = Executors.newFixedThreadPool(nrProcessors);
    private final String luceneIndexPath = "tmp/luceneIndex";

    public LuceneIndexWriter() {
        startProcessingThreads();
    }

    public void process(final BufferedReader in) throws IOException, InterruptedException {
        File indexDir = new File(luceneIndexPath);
        if (indexDir.exists()) {
            FileUtils.cleanDirectory(indexDir);
        }

        long lineNr = 0;
        String line;
        StringBuilder str = new StringBuilder(100000);
        while ((line = in.readLine()) != null) {
            if (!line.startsWith("#")) {
                lineNr++;
                str.append(line).append('\n');
                if (lineNr % 10000 == 0) {
                    queue.put(str.toString());
                    str = new StringBuilder(100000);
                    logger.debug("Sumitted {} lines for processing", lineNr);
                }
            }
        }
        queue.put(str.toString());
        for (int i = 0; i < nrProcessors; i++) {
            queue.put("STOP");
        }

        executorService.shutdown();
        if (executorService.awaitTermination(10, TimeUnit.DAYS)) {
            merge();
        }
        logger.debug("Done!");
    }

    private void startProcessingThreads() {
        for (int i = 0; i < nrProcessors; i++) {
            executorService.submit(new Indexer(i));
        }
    }

    private class Indexer implements Runnable {
        private final int threadNr;

        private Indexer(int partCount) {
            this.threadNr = partCount;
        }

        @Override
        public void run() {
            IndexWriter writer = null;
            try {
                int count = 0;
                int lineCount = 0;
                String str;
                while (!(str = queue.take()).equals("STOP")) {

                    logger.debug("Indexer {}: processing part {}", threadNr, count);
                    IndexWriterConfig config = getIndexWriterConfig();
                    Directory indexDir = FSDirectory.open(new File(luceneIndexPath, threadNr + "_" + count));
                    writer = new IndexWriter(indexDir, config);
                    Document document = new Document();
                    Field labelField = new TextField("label", "", Field.Store.NO);
                    Field resourceField = new StoredField("resource", "");

                    // split on newlines
                    StringTokenizer lineTokenizer = new StringTokenizer(str, "\n");
                    while (lineTokenizer.hasMoreTokens()) {
                        lineCount++;
                        String triple = lineTokenizer.nextToken();
                        StringTokenizer wordTokenizer = new StringTokenizer(triple);
                        String resource = wordTokenizer.nextToken();

                        wordTokenizer.nextToken();
                        StringBuilder labelStr = new StringBuilder(wordTokenizer.nextToken());
                        while (wordTokenizer.hasMoreTokens()) {
                            labelStr.append(' ').append(wordTokenizer.nextToken());
                        }
                        String label = labelStr.toString();

                        // get rid of language in label, and normalize
                        int indexOfLastQuote = label.indexOf('"', 1);
                        label = label.substring(1, indexOfLastQuote);
                        label = Normalizer.normalizeForIndexing(label);

                        labelField.setStringValue(label);
                        document.add(labelField);

                        // omit first part of uri
                        resource = resource.substring(29, resource.length() - 1);
                        resourceField.setStringValue(resource);
                        document.add(resourceField);

                        writer.addDocument(document);
                        if (lineCount % 1000 == 0) {
                            logger.debug("part {}: {} lines.", threadNr, lineCount);
                        }
                    }
                    writer.close();
                    count++;
                }
                logger.debug("Indexer {} stopped.", threadNr);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private IndexWriterConfig getIndexWriterConfig() {
        IndexWriterConfig config = new IndexWriterConfig(lVersion, new WhitespaceAnalyzer(lVersion));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setRAMBufferSizeMB(4 * 1024d);
        config.setMaxBufferedDocs(IndexWriterConfig.DISABLE_AUTO_FLUSH);
        return config;
    }

    private void merge() throws IOException {
        IndexWriter writer = null;
        try {
            IndexWriterConfig config = getIndexWriterConfig();
            Directory indexDir = FSDirectory.open(new File(luceneIndexPath, "merged"));
            writer = new IndexWriter(indexDir, config);

            File indexRoot = new File(luceneIndexPath);
            File[] indicesToMerge = indexRoot.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains("_");
                }
            });

            Directory[] indexDirsToMerge = new Directory[indicesToMerge.length];
            for (int i = 0; i < indicesToMerge.length; i++) {
                File file = indicesToMerge[i];
                indexDirsToMerge[i] = FSDirectory.open(file);
            }
            logger.debug("Merging {}", indicesToMerge);
            writer.addIndexes(indexDirsToMerge);
            logger.debug("Optimizing");
            writer.forceMerge(1);
            writer.close();
            logger.debug("Merging done! Cleaning up temporary files...");

            // clean up temporary indices
            for (File file : indicesToMerge) {
                FileUtils.deleteDirectory(file);
            }

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) throws IOException, CompressorException, InterruptedException {
        String nTriplesPath = args[0];

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(new FileInputStream(nTriplesPath)))))) {
            LuceneIndexWriter luceneIndex = new LuceneIndexWriter();
            luceneIndex.process(in);
        }
    }

}
