package br.com.ciandt.endpoint;

import br.com.ciandt.model.Comics;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:secrets.properties")
@PropertySource("classpath:marvelapi.properties")
public class ComicsResource {

    public static final String TS = "1";

    @Value("${marvel.public.key}")
    public String publicKey;

    @Value("${marvel.private.key}")
    public String privateKey;

    @Value("${marvel.base.uri}")
    private String baseUri;

    @Value("${marvel.comics.resource}")
    private String comicsResource;

    public ComicsResource() {
    }

    public String geraHash() {
        String hashKey = null;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            String key = TS + privateKey + publicKey;
            hashKey = new HexBinaryAdapter().marshal(messageDigest.digest(key.getBytes())).toLowerCase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashKey;
    }


    public List<Comics> getComics() {

        Client client = Client.create();

        WebResource webResource = client
                .resource(baseUri)
                .path(comicsResource)
                .queryParam("ts", TS)
                .queryParam("apikey", publicKey)
                .queryParam("hash", geraHash());

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println(output);

        JsonNode nameNode = parseJson(output);

        List<Comics> comics = converterJsonDTOComics(nameNode);

        return comics;
    }

    public List<Comics> getComicsId(Integer id) {

        Client client = Client.create();

        WebResource webResource = client
                .resource(baseUri)
                .path(comicsResource)
                .queryParam("ts", TS)
                .queryParam("apikey", publicKey)
                .queryParam("hash", geraHash())
                .queryParam("id", String.valueOf(id));

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println(output);

        JsonNode nameNode = parseJson(output);

        List<Comics> comics = converterJsonDTOComics(nameNode);

        return comics;
    }

    public JsonNode parseJson(String response) {

        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(Include.NON_NULL);

        JsonNode rootNode;
        JsonNode nameNode = null;

        try {
            rootNode = mapper.readTree(response);
            nameNode = rootNode.get("data").get("results");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return nameNode;
    }

    public List<Comics> converterJsonDTOComics(JsonNode nameNode) {

        List<Comics> comics = new ArrayList<Comics>();

        for (int i = 0; i < nameNode.size(); i++) {

            Comics comic = new Comics();

            if (!nameNode.get(i).get("id").isNull()) {
                comic.setId((Integer) nameNode.get(i).get("id").numberValue());
            }
            if (!nameNode.get(i).get("title").isNull()) {
                comic.setTitle(nameNode.get(i).get("title").textValue());
            }
            if (!nameNode.get(i).get("description").isNull()) {
                comic.setDescription(nameNode.get(i).get("description").textValue());
            }
            if (!nameNode.get(i).get("variantDescription").isNull()) {
                comic.setVariantDescription(nameNode.get(i).get("variantDescription").textValue());
            }

            comics.add(comic);
        }

        return comics;

    }

}