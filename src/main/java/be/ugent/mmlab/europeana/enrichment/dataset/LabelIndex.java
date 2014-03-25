package be.ugent.mmlab.europeana.enrichment.dataset;

import be.ugent.mmlab.europeana.enrichment.misc.Normalizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/25/14.
 */
public class LabelIndex {
    private static LabelIndex instance;

    private final Logger logger = LogManager.getLogger(getClass());
    private final QueryParser queryParser;
    private final IndexSearcher searcher;
    private final int maxResults;

    public static LabelIndex getInstance(final Version luceneVersion, final String indexDir, final int maxResults) throws IOException {
        if (instance == null) {
            instance = new LabelIndex(luceneVersion, indexDir, maxResults);
        }
        return instance;
    }

    private LabelIndex (final Version luceneVersion, final String indexDir, final int maxResults) throws IOException {
        final IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
        searcher = new IndexSearcher(reader);
        final Analyzer analyzer = new WhitespaceAnalyzer(luceneVersion);
        queryParser = new QueryParser(luceneVersion, "label", analyzer);
        this.maxResults = maxResults;
    }

    public List<String> searchSubject(final String subject) {
        logger.debug("Subject to search: [{}]", subject);
        final List<String> matches = new ArrayList<>();
        final String normalizedSubject = Normalizer.normalizeForQuerying(subject);
        try {
            final Query query = queryParser.parse(normalizedSubject);
            logger.debug("Lucene Query: [{}]", query.toString());
            TopDocs topDocs = searcher.search(query, maxResults);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                matches.add("http://dbpedia.org/resource/" + document.get("resource"));
            }
        } catch (ParseException | IOException e) {
            logger.error("Could not search [{}]", subject, e);
            logger.debug("normalized query: [{}]", normalizedSubject);
        }
        return matches;
    }

}
