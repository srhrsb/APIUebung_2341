package com.brh.interpol_api;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class APIRequest {

    private Consumer<ArrayList<Person> > onSuccessCallback;

    public void getData(String name, String forename, String freeSearch, Consumer< ArrayList<Person> > onSuccessCallback ){
        //Callback zwischenspeichern für spätere Verwendung
        this.onSuccessCallback = onSuccessCallback;
        String urlString = createURL(name, forename, freeSearch);
        sendRequest(urlString);

    }

    private String createURL(String name, String forename, String freeSearch){
        String urlString ="https://ws-public.interpol.int/notices/v1/red?page=1&resultPerPage=10000";

        if(!name.isEmpty())
            urlString+="&name="+name;
        if(!forename.isEmpty())
            urlString+="&forename="+forename;
        if(!freeSearch.isEmpty())
            urlString+="&freeText="+freeSearch;
        return urlString;
    }

    //ToDO Überladung von CreateURL, bei der page und resultPerPage angegeben werden können
    private String createURL(String name, String forename, String freeSearch, int page, int resultPerPage){
        String urlString ="https://ws-public.interpol.int/notices/v1/red?page="+page+"&resultPerPage="+resultPerPage;

        if(!name.isEmpty())
            urlString+="&name="+name;
        if(!forename.isEmpty())
            urlString+="&forename="+forename;
        if(!freeSearch.isEmpty())
            urlString+="&freeText="+freeSearch;
        return urlString;
    }

    private void sendRequest( String urlString ){

        try{
            //dient dazu die asynchron zukünftig empfangene Antwort der API weiterzuleiten
            CompletableFuture<HttpResponse<String>> future;

            //Objekt dass den Request ausführt
            try( HttpClient client = HttpClient.newHttpClient() ){

                //Erzeugung des Client war erfolgreich -> Request wird erzeugt
                //unter Verwendung der Abfrage-URL
                HttpRequest request = HttpRequest.newBuilder(URI.create(urlString))
                        .setHeader("accept-encoding","gzip, deflate, br, zstd")
                        .setHeader("Accept","text/html")
                        .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36")
                        .build();

                //Request wird asynchron gesendet -> Response wird im Future zugewiesen
                future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            }

            //wenn die Daten (Response) empfangen wurden, gibt das Future-Callback
            //diese an die Methode "handleApiResponse" weiter
            future.thenAccept(this::handleApiResponse );
        }
        catch(Exception e){
            System.err.println( "Error: "+e.getMessage() );
        }
    }

    private void handleApiResponse( HttpResponse<String> response ){
        //ist der Response erfolgreich?
        if(response.statusCode()==200) {
            System.out.println(response.body());
            var personList = parseJson( response.body() );

            //Consumer ausgelöst durch accept -> Daten sind da
            //und werden an die Callback-Methode geschickt
            onSuccessCallback.accept( personList );
        }
        else{
            System.err.println("API response failed: "+response.statusCode());
        }
    }

    private ArrayList<Person> parseJson( String json ){

        ArrayList<Person> personList = new ArrayList<>();

        try{
            //Daten aus dem JSON parsen
            JSONParser parse = new JSONParser();
            JSONObject jsonObject = (JSONObject)parse.parse( json );
            JSONObject embeded = (JSONObject)jsonObject.get("_embedded" );

            JSONArray notices = (JSONArray)embeded.get("notices");

            for( var item : notices){

                JSONObject notice = (JSONObject)item;
                JSONObject _links = (JSONObject)notice.get("_links");
                JSONObject thumbnail = (JSONObject)_links.get("thumbnail");

                String image = null;
                if( thumbnail != null ){
                    image = thumbnail.get("href").toString();
                }

                //Objekt Person aus den geparsten Daten erzeugen
                Person person = new Person( notice.get("name").toString(),
                        notice.get("forename").toString(),
                        notice.get("date_of_birth").toString(),
                        image
                );

                //Person zur List hinzufügen
                personList.add(person);
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        return personList;
    }
}
