# Enrichment Tool Web Service API

We define `enrichURI` as the the root of the Enrichment Tool Web Service.
All `HTTP` requests are start with this URI. For instance, for the demo
service, this is `http://yuca.test.iminds.be:8915/enrich/`.

## 1. Post a record for disambiguation
Use this method to post a valid EDM record to the web service. The resonse
is a JSON document that contains a unique reference for future use and a
mapping of objects to possible URI's. The posted document stays one day
available for use on the server.

### request
    method        post
    uri           enrichURI/record
    content type  application/rdf+xml
    encoding      UTF-8
    entity        a valid EDM record in XML format.

#### example entity
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <rdf:RDF xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:crm="http://www.cidoc-crm.org/rdfs/cidoc_crm_v5.0.2_english_label.rdfs#"
         xmlns:dc="http://purl.org/dc/elements/1.1/"
         xmlns:dcterms="http://purl.org/dc/terms/"
         xmlns:edm="http://www.europeana.eu/schemas/edm/"
         xmlns:foaf="http://xmlns.com/foaf/0.1/"
         xmlns:ore="http://www.openarchives.org/ore/terms/"
         xmlns:owl="http://www.w3.org/2002/07/owl#"
         xmlns:rdaGr2="http://rdvocab.info/ElementsGr2/"
         xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
         xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
         xmlns:skos="http://www.w3.org/2004/02/skos/core#"
         xmlns:wgs84="http://www.w3.org/2003/01/geo/wgs84_pos#"
         xmlns:xalan="http://xml.apache.org/xalan">
      <edm:ProvidedCHO rdf:about="http://SET_PROVIDEDCHO_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR">
        <dc:creator xml:lang="fr">Hankar, Paul</dc:creator>
        <dc:description xml:lang="fr">number-of-objects: 01 </dc:description>
        <dc:format>Matière manufacturée &gt; Végétal &gt; Papier</dc:format>
        <dc:format>Arts graphiques &gt; Peinture (art graphique) &gt; Lavis &gt; Encre de chine</dc:format>
        <dc:format>Arts graphiques &gt; Peinture (art graphique) &gt; Lavis</dc:format>
        <dc:identifier>http://www.carmentis.be_item_145970</dc:identifier>
        <dc:identifier>FH.005.97-1.03</dc:identifier>
        <dc:identifier>145970</dc:identifier>
        <dc:identifier>RMAH-145970-FR</dc:identifier>
        <dc:rights>Musées Royaux d'Art et d'Histoire</dc:rights>
        <dc:title xml:lang="fr">Dossier exposition du Congo, Tervueren - Disposition générale des pilastres en maçonneries</dc:title>
        <dc:type xml:lang="fr">Plan</dc:type>
        <dc:type xml:lang="fr">Collection Art déco du XXe siècle</dc:type>
        <dcterms:created>1897</dcterms:created>
        <dcterms:extent>Dimensions H x L x P: 770 x 1800 mm</dcterms:extent>
        <dcterms:spatial>Europe &gt; Europe occidentale &gt; Belgique &gt; Bruxelles</dcterms:spatial>
        <edm:type>IMAGE</edm:type>
       </edm:ProvidedCHO>
     <edm:WebResource rdf:about="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution"/>
     <edm:WebResource rdf:about="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=lowImageResolution">
       <edm:rights rdf:resource="http://www.europeana.eu/rights/rr-f/"/>
     </edm:WebResource>
     <ore:Aggregation rdf:about="http://SET_AGGREGATION_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR">
       <edm:aggregatedCHO rdf:resource="http://SET_PROVIDEDCHO_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR"/>
       <edm:dataProvider>Catalogue en ligne Carmentis</edm:dataProvider>
       <edm:isShownAt rdf:resource="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ExternalInterface&amp;module=collection&amp;lang=fr&amp;objectId=145970"/>
       <edm:isShownBy rdf:resource="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution"/>
       <edm:object rdf:resource="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution"/>
       <edm:provider>SET_PROVIDER</edm:provider>
       <edm:rights rdf:resource="http://www.europeana.eu/rights/rr-f/"/>
     </ore:Aggregation>
    </rdf:RDF>

### response OK
    code          200 OK
    encoding      UTF-8
    content type  application/json

#### example entity
    {"reference":1407150140099,
     "objectToPossibleURIs": {
       "http://localhost/agents/Hankar,%20Paul":[
          "http://dbpedia.org/resource/Paul_Hankar",
          "http://wikidata.dbpedia.org/resource/Q1075644"
        ]
       }
     }

### response Unsupported Media Type
    code          415
This is returned when the content-type header is not set to
`application/x-www-form-urlencoded` or `application/rdf+xml`.

## 2. Post a disambiguation choice <a name="2"/>
Use this method to inform the server which URI to use for enrichment of
a given object. The response is the enriched EDM record, based on the
choices given in the request.

### request
    method        post
    uri           enrichURI/record/<reference>
    content type  application/json
    encoding      UTF-8
    entity        a mapping of objects to their (chosen) URI, in JSON format.

#### example entity
    {"http://localhost/agents/Hankar,%20Paul":"http://dbpedia.org/resource/Paul_Hankar"}

### response OK
    code          200 OK
    encoding      UTF-8
    content type  application/rdf+xml

#### example entity
    <rdf:RDF
        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
        xmlns:wgs84="http://www.w3.org/2003/01/geo/wgs84_pos#"
        xmlns:edm="http://www.europeana.eu/schemas/edm/"
        xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
        xmlns:crm="http://www.cidoc-crm.org/rdfs/cidoc_crm_v5.0.2_english_label.rdfs#"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:owl="http://www.w3.org/2002/07/owl#"
        xmlns:rdaGr2="http://rdvocab.info/ElementsGr2/"
        xmlns:ore="http://www.openarchives.org/ore/terms/"
        xmlns:skos="http://www.w3.org/2004/02/skos/core#"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:foaf="http://xmlns.com/foaf/0.1/" > 
      <rdf:Description rdf:about="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=lowImageResolution">
        <edm:rights rdf:resource="http://www.europeana.eu/rights/rr-f/"/>
        <rdf:type rdf:resource="http://www.europeana.eu/schemas/edm/WebResource"/>
      </rdf:Description>
      <rdf:Description rdf:about="http://SET_AGGREGATION_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR">
        <edm:rights rdf:resource="http://www.europeana.eu/rights/rr-f/"/>
        <edm:provider>SET_PROVIDER</edm:provider>
        <edm:object rdf:resource="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution"/>
        <edm:isShownBy rdf:resource="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution"/>
        <edm:isShownAt rdf:resource="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ExternalInterface&amp;module=collection&amp;lang=fr&amp;objectId=145970"/>
        <edm:dataProvider>Catalogue en ligne Carmentis</edm:dataProvider>
        <edm:aggregatedCHO rdf:resource="http://SET_PROVIDEDCHO_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR"/>
        <rdf:type rdf:resource="http://www.openarchives.org/ore/terms/Aggregation"/>
      </rdf:Description>
      <rdf:Description rdf:about="http://carmentis.kmkg-mrah.be/eMuseumPlus?service=ImageAsset&amp;module=collection&amp;objectId=145970&amp;resolution=superImageResolution">
        <rdf:type rdf:resource="http://www.europeana.eu/schemas/edm/WebResource"/>
      </rdf:Description>
      <rdf:Description rdf:about="http://localhost/agents/Hankar,%20Paul">
        <skos:note xml:lang="it">Ebbe una formazione come scultore e studiò presso l'Accademia reale di belle arti di Bruxelles (Académie royale des beaux-arts de Bruxelles), dove strinse amicizia con Victor Horta e dove si impratichì della tecnica del ferro battuto, in seguito ampiamente utilizzata nei suoi progetti. Nel 1888 iniziò la propria attività di architetto e di creatore di oggetti d'arredamento, collaborando con Adolphe Crespin, decoratore di interni e specialista della tecnica dello sgraffito. Nel 1889 redasse il progetto per il palazzo Chávarri di Bilbao, per l'imprenditore basco Victor Chávarri. Nel 1893 costruì la propria casa, considerata il primo esempio di un edificio di Art Nouveau a Bruxelles, insieme al palazzo Tassel di Victor Horta. Nel 1896 redasse il progetto per la "Città degli artisti", con laboratori e abitazioni per una cooperativa di artisti, presso la stazione balneare di Westende; il progetto non venne mai realizzato, ma ispirò la Künsterkolonie di Darmstadt e la Secessione viennese. In occasione dell'Esposizione universale di Bruxelles del 1897, fu incaricato con Henry Van de Velde, con Gustave Serrurier-Bovy e con Georges Hobé del esposizione coloniale a Tervuren, come coordinatore dei lavori dei diversi artigiani e fabbricanti di arredi. La maggior parte delle sue costruzioni si trovano nella città di Bruxelles: casa Zegers-Regnard; casa e farmacia Peeters; palazzo Renkin (1897, oggi scomparso); palazzo Ciamberlani (1897); casa del pittore René Janssens (1898; palazzo Kleyer; casa-atelier Bartholomé; camiceria Niguet . È seppellito presso il cimitero del Dieweg a Uccle, presso Bruxelles.</skos:note>
        <skos:note xml:lang="sv">Paul Hankar, född 11 december 1859 i Frameries, död 17 januari 1901 i Bryssel, var en belgisk arkitekt och designer. Han var en av de mer kända arkitekterna inom jugendstilen.</skos:note>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Paul_Hankar"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Gustave_Strauven"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Rombout_II_Keldermans"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Victor_Bourgeois"/>
        <skos:note xml:lang="fr">Paul Hankar, architecte et designer belge né le 11 décembre 1859 à Frameries et mort le 17 janvier 1901 à Bruxelles, a été l’un des principaux représentants de l’Art nouveau à Bruxelles.</skos:note>
        <edm:end rdf:datatype="http://www.w3.org/2001/XMLSchema#date">1901-01-15Z</edm:end>
        <skos:note xml:lang="it">Ebbe una formazione come scultore e studiò presso l'Accademia reale di belle arti di Bruxelles (Académie royale des beaux-arts de Bruxelles), dove strinse amicizia con Victor Horta e dove si impratichì della tecnica del ferro battuto, in seguito ampiamente utilizzata nei suoi progetti. Nel 1888 iniziò la propria attività di architetto e di creatore di oggetti d'arredamento, collaborando con Adolphe Crespin, decoratore di interni e specialista della tecnica dello sgraffito.</skos:note>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Jules_Brunfaut"/>
        <edm:begin rdf:datatype="http://www.w3.org/2001/XMLSchema#date">1859-12-09Z</edm:begin>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Xaveer_De_Geyter"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/William_Walcot"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Victor_Horta"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Raimondo_Tommaso_D&apos;Aronco"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Henry_van_de_Velde"/>
        <skos:note xml:lang="en">Paul Hankar (11 December 1859 - 17 January 1901) was a Belgian architect and designer who, along with Victor Horta and Henry Van de Velde, is considered one of the principal architects to work in the Art Nouveau style in Brussels at the turn of the twentieth century.</skos:note>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Victor_Schr%C3%B6ter"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Vittorio_Meano"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Raymond_Charles_P%C3%A9r%C3%A9"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Simone_Guilissen"/>
        <rdf:type rdf:resource="http://www.europeana.eu/schemas/edm/Agent"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Joseph_Bascourt"/>
        <skos:note xml:lang="pl">Paul Hankar - belgijski architekt, tworzący w stylu secesji. Oprócz budynków projektował także wnętrza.</skos:note>
        <skos:note xml:lang="nl">Paul Hankar was een Belgisch architect. Hij was een pionier en sleutelfiguur voor de Belgische art nouveau. Hij ontwikkelde parallel met Victor Horta een nieuwe stijl die lokale bouwtradities combineert met het Franse rationalisme.</skos:note>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Virginio_Colombo"/>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Renaat_Braem"/>
        <skos:prefLabel xml:lang="fr">Paul Hankar</skos:prefLabel>
        <skos:prefLabel xml:lang="es">Paul Hankar</skos:prefLabel>
        <skos:prefLabel xml:lang="nl">Paul Hankar</skos:prefLabel>
        <owl:sameAs rdf:resource="http://dbpedia.org/resource/Paul_Hankar"/>
        <skos:prefLabel xml:lang="it">Paul Hankar</skos:prefLabel>
        <skos:prefLabel xml:lang="pl">Paul Hankar</skos:prefLabel>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Rupert_Carabin"/>
        <skos:note xml:lang="es">Paul Hankar fue un arquitecto y diseñador belga. Está considerado, junto con Victor Horta y Henry van de Velde, como uno de los principales estandartes del estilo Art Nouveau a principios del siglo XX. En 1889 recibió el encargo de proyectar el Palacio Chávarri en Bilbao, para el empresario local Víctor Chávarri. Realizó la casa del pintor Ciamberlani, con piezas acristaladas y gran interés por el cromatismo.</skos:note>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Sidnei_Tendler"/>
        <skos:prefLabel xml:lang="en">Paul Hankar</skos:prefLabel>
        <skos:prefLabel xml:lang="sv">Paul Hankar</skos:prefLabel>
        <edm:isRelatedTo rdf:resource="http://dbpedia.org/resource/Tilman-Fran%C3%A7ois_Suys"/>
      </rdf:Description>
      <rdf:Description rdf:about="http://SET_PROVIDEDCHO_BASEURL/Catalogue en ligne Carmentis/RMAH-145970-FR">
        <dcterms:spatial>Europe &gt; Europe occidentale &gt; Belgique &gt; Bruxelles</dcterms:spatial>
        <dc:identifier>FH.005.97-1.03</dc:identifier>
        <dc:format>Arts graphiques &gt; Peinture (art graphique) &gt; Lavis</dc:format>
        <dc:identifier>145970</dc:identifier>
        <dc:format>Matière manufacturée &gt; Végétal &gt; Papier</dc:format>
        <rdf:type rdf:resource="http://www.europeana.eu/schemas/edm/ProvidedCHO"/>
        <dcterms:created>1897</dcterms:created>
        <dc:type xml:lang="fr">Plan</dc:type>
        <dc:title xml:lang="fr">Dossier exposition du Congo, Tervueren - Disposition générale des pilastres en maçonneries</dc:title>
        <dc:identifier>RMAH-145970-FR</dc:identifier>
        <dc:type xml:lang="fr">Collection Art déco du XXe siècle</dc:type>
        <dc:description xml:lang="fr">number-of-objects: 01 </dc:description>
        <edm:type>IMAGE</edm:type>
        <dc:format>Arts graphiques &gt; Peinture (art graphique) &gt; Lavis &gt; Encre de chine</dc:format>
        <dcterms:extent>Dimensions H x L x P: 770 x 1800 mm</dcterms:extent>
        <dc:rights>Musées Royaux d'Art et d'Histoire</dc:rights>
        <dc:creator rdf:resource="http://example.com/resources/agents/Hankar,%20Paul"/>
        <dc:identifier>http://www.carmentis.be_item_145970</dc:identifier>
      </rdf:Description>
    </rdf:RDF>

### response Internal Server Error
    code          500
This response is returned when the processing of the request went wrong.

## 3. Get a previously enriched record
It is possible to get a previously enriched record, as long as it is in
the cache of the server (by default one day).

### request
    method        get
    uri           enrichURI/record/<reference>

### response OK
    code          200 OK
    encoding      UTF-8
    content type  application/rdf+xml
    
The entity has the same format as the OK response in 
[the previous section](#2).

### response Not Found
    code          404 Not Found
This response is returned if the document has never been enriched, or
when it is not cached anymore.

### response Bad Request
    code          400 Bad Request
This response is returned if no reference was given.
