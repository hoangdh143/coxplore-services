package co.pailab.lime.helper.HttpRequest;

import co.pailab.lime.model.GoogleInfo;
import co.pailab.lime.model.User;
import co.pailab.lime.security.SecurityConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class HttpRequest {

    @Autowired
    private SecurityConstants securityConstants;

    public HttpRequest() {
    }

    public GoogleInfo getGoogleUserInfo(String accessToken) throws IOException {
        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Sending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        // read the response
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
            response.append(inputLine);
        }
        in.close();

        ObjectMapper mapper = new ObjectMapper();
        GoogleInfo googleInfo = mapper.readValue(response.toString(), GoogleInfo.class);

        //print result
        System.out.println(response.toString());

        return googleInfo;
    }
}
