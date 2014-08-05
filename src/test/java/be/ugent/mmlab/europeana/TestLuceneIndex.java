package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.enrichment.dataset.LabelIndex;
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
import java.util.List;

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
        String searchTerm = "Michel-Ferdinand d'Albert d'Ailly Duc de Chaulnes";
        searchTerm = Normalizer.normalizeForIndexing(searchTerm);
        //searchTerm = "henry~1 velde~1";

        String indexDir = "/home/ghaesen/data/dbPedia_luceneindex/merged";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_48);
        //searcher.
        QueryParser parser = new QueryParser(Version.LUCENE_48, "label", analyzer);
        Query query = parser.parse(searchTerm);
        System.out.println("query = " + query.toString());

        //Query

        System.out.println("Searching for [" + searchTerm + "]");
        TopDocs foundDocs = searcher.search(query, 20);
        //ScoreDoc[] hits = foundDocs.scoreDocs;
        int counter = 1;
        for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(counter + "\t" + doc.get("resource") + "\t" + scoreDoc.score);
            counter++;
        }
    }

    @Test
    public void testLabelIndex() throws IOException {
        String name = "George Adams Junior";

        LabelIndex labelIndex = LabelIndex.getInstance(Version.LUCENE_48,
                "/home/ghaesen/data/dbPedia_luceneindex/merged",
                30);

        List<String> hits = labelIndex.searchSubject(name);
        int counter = 1;
        for (String hit : hits) {
            System.out.println(counter + "\t" + hit);
            counter++;
        }
    }
}
