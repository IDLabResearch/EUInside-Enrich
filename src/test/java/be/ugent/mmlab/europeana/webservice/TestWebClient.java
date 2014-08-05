package be.ugent.mmlab.europeana.webservice;

import be.ugent.mmlab.europeana.enrichment.oneRecord.PhaseOneResult;
import be.ugent.mmlab.europeana.webservice.client.WebClient;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/17/14.
 */
public class TestWebClient {

    private final String baseUri = "http://localhost:8080/enrich";
	//private final String baseUri = "http://yuca.test.iminds.be:8915/enrich";
    private WebClient client = new WebClient(baseUri);

    @Test
    public void testSendOneRecord() throws IOException {
        final String record = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<rdf:RDF xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xmlns:crm=\"http://www.cidoc-crm.org/rdfs/cidoc_crm_v5.0.2_english_label.rdfs#\"\n" +
                "         xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                "         xmlns:dcterms=\"http://purl.org/dc/terms/\"\n" +
                "         xmlns:edm=\"http://www.europeana.eu/schemas/edm/\"\n" +
                "         xmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n" +
                "         xmlns:ore=\"http://www.openarchives.org/ore/terms/\"\n" +
                "         xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                "         xmlns:rdaGr2=\"http://rdvocab.info/ElementsGr2/\"\n" +
                "         xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                "         xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                "         xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\"\n" +
                "         xmlns:wgs84=\"http://www.w3.org/2003/01/geo/wgs84_pos#\"\n" +
                "         xmlns:xalan=\"http://xml.apache.org/xalan\">\n" +
                "   <edm:ProvidedCHO rdf:about=\"http://SET_PROVIDEDCHO_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR\">\n" +
                "      <dc:creator xml:lang=\"fr\">Hankar, Paul</dc:creator>\n" +
                "      <!--dc:creator>George Adams Junior</dc:creator-->\n" +
                "      <dc:description xml:lang=\"fr\">number-of-objects: 01 </dc:description>\n" +
                "      <dc:format>Matière manufacturée &gt; Végétal &gt; Papier</dc:format>\n" +
                "      <dc:format>Arts graphiques &gt; Peinture (art graphique) &gt; Lavis &gt; Encre de chine</dc:format>\n" +
                "      <dc:format>Arts graphiques &gt; Peinture (art graphique) &gt; Lavis</dc:format>\n" +
                "      <dc:identifier>http://www.carmentis.be_item_145970</dc:identifier>\n" +
                "      <dc:identifier>FH.005.97-1.03</dc:identifier>\n" +
                "      <dc:identifier>145970</dc:identifier>\n" +
                "      <dc:identifier>RMAH-145970-FR</dc:identifier>\n" +
                "      <dc:rights>Musées Royaux d'Art et d'Histoire</dc:rights>\n" +
                "      <dc:title xml:lang=\"fr\">Dossier exposition du Congo, Tervueren - Disposition générale des pilastres en maçonneries</dc:title>\n" +
                "      <dc:type xml:lang=\"fr\">Plan</dc:type>\n" +
                "      <dc:type xml:lang=\"fr\">Collection Art déco du XXe siècle</dc:type>\n" +
                "      <dcterms:created>1897</dcterms:created>\n" +
                "      <dcterms:extent>Dimensions H x L x P: 770 x 1800 mm</dcterms:extent>\n" +
                "      <dcterms:spatial>Europe &gt; Europe occidentale &gt; Belgique &gt; Bruxelles</dcterms:spatial>\n" +
                "      <edm:type>IMAGE</edm:type>\n" +
                "   </edm:ProvidedCHO>\n" +
                "   <edm:WebResource rdf:about=\"http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution\"/>\n" +
                "   <edm:WebResource rdf:about=\"http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=lowImageResolution\">\n" +
                "      <edm:rights rdf:resource=\"http://www.europeana.eu/rights/rr-f/\"/>\n" +
                "   </edm:WebResource>\n" +
                "   <ore:Aggregation rdf:about=\"http://SET_AGGREGATION_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR\">\n" +
                "      <edm:aggregatedCHO rdf:resource=\"http://SET_PROVIDEDCHO_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR\"/>\n" +
                "      <edm:dataProvider>Catalogue en ligne Carmentis</edm:dataProvider>\n" +
                "      <edm:isShownAt rdf:resource=\"http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ExternalInterface&amp;module=collection&amp;lang=fr&amp;objectId=145970\"/>\n" +
                "      <edm:isShownBy rdf:resource=\"http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution\"/>\n" +
                "      <edm:object rdf:resource=\"http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution\"/>\n" +
                "      <edm:provider>SET_PROVIDER</edm:provider>\n" +
                "      <edm:rights rdf:resource=\"http://www.europeana.eu/rights/rr-f/\"/>\n" +
                "   </ore:Aggregation>\n" +
                "</rdf:RDF>\n";

        PhaseOneResult phaseOneResult = client.postOneRecordPhaseOne(record);
        // choose first result
        final Map<String, String> objectToURI = new HashMap<>();
        for (Map.Entry<String, List<String>> objectToPossibleUIs : phaseOneResult.getObjectToPossibleURIs().entrySet()) {
            String object = objectToPossibleUIs.getKey();
            String choiceURI = objectToPossibleUIs.getValue().get(0);
            objectToURI.put(object, choiceURI);
        }

        String enrichedRecord = client.postOneRecordPhaseTwo(phaseOneResult.getReference(), objectToURI);
        System.out.println("====== enrichedRecord:\n" + enrichedRecord);

    }

    @Test
    public void testBulkPhaseOne() throws IOException {
        File recordsFile = new File("/home/ghaesen/data/europeana/edm/all_merged_corrected.rdf.gz");
        String reference = client.postBulkPhaseOne(recordsFile);
        System.out.println("reference = " + reference);
    }


}
