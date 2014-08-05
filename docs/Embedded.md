# Embedding the Enrichment Tool

The enrichment tool can be embedded in your code. This allows your code to bypass the REST API, and address enrichment
functionality directly.

## Preparation

Building the code, configuring the properties and building an index is done the same way as described in the 
`WebServiceSetup.md` document.

## Example code
### Initialization

First create an EnrichService object. This should only be done once. 

    // initialize the configuration
    Config.init("path/to/enrich.properties");
    
    // create an EnrichService instance
    EnrichService enrichService = EnrichServiceFactory.create();
    
### Enriching a record

Now [EDM](http://pro.europeana.eu/edm-documentation) records can be enriched, one at a time. A bulk enrichment version
is underway.

    // an EDM record, in RDF-XML format
    String edmRecord = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
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

    // Find candidate resources for every field to be enriched.
    PhaseOneResult searchResult = enrichService.phaseOne(edmRecord);
    
    // The searchResult contains a mapping from field value to possible resources.  
    Map<String, List<String>> fieldValueToPossibleCandidates = searchResult.getObjectToPossibleURIs();
    
    // Select for every key (field value) one of the possible candidates (= disambiguation step).
    ...
    
    // Now compose a map that with these selections; it maps a field value to one of the candidates. 
    Map<String, String> fieldValueToSelectedCandidate = new HashMap<>();
    map.put(aFieldValue, aCandidate);
    ...
    
    // The searchResult contains a reference the record to be enriched. This will be passed to the enrich method (phaseTwo)
    long reference = searchResult.getReference();
    
    // Now perform the enrichment itself. The result is the enriched record in RDF-XML format.
    String enrichedEDMRecord = enrichService.phaseTwo(reference, fieldValueToSelectedCandidate);

### Retrieving an enriched record

Previously enriched records are kept in a cache for one day. They can be retrieved with their reference

    String previouslyEnrichedRecord = enrichService.getFromCache(reference);
