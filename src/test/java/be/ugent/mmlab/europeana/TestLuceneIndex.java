package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.enrichment.misc.Normalizer;
import be.ugent.mmlab.europeana.tools.LuceneIndexWriter;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/21/14.
 */
public class TestLuceneIndex {

    @Test
    public void testWriteAndQuery() throws IOException, ParseException, CompressorException, InterruptedException {
        String[] args = {"/home/ghaesen/data/dbPedia/labels_adams.nt.gz"};
        LuceneIndexWriter.main(args);
        testReadAndQuery();
    }

    @Test
    public void testReadAndQuery() throws IOException, ParseException {
        String searchTerm = "Euclides";
        searchTerm = Normalizer.normalize(searchTerm);

        String indexDir = "tmp/luceneIndex/merged";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_47);
        //searcher.
        QueryParser parser = new QueryParser(Version.LUCENE_47, "label", analyzer);
        Query query = parser.parse(searchTerm);
        System.out.println("query = " + query.toString());


        System.out.println("Searching for [" + searchTerm + "]");
        TopDocs foundDocs = searcher.search(query, 20);
        //ScoreDoc[] hits = foundDocs.scoreDocs;
        int counter = 1;
        for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(counter + "\t" + doc.get("resource"));
            counter++;
        }
    }
}
