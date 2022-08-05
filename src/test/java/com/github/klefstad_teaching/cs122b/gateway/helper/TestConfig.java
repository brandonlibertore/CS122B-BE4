package com.github.klefstad_teaching.cs122b.gateway.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.klefstad_teaching.cs122b.core.security.JWTAuthenticationFilter;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

@Component
public class TestConfig
{
    private static final String USERS_FILE_NAME = "users.json";

    private final JSONObject users;

    private final URI idm;
    private final URI movies;
    private final URI billing;

    private final String adminHeader;
    private final String employeeHeader;
    private final String premiumHeader;

    @Autowired
    public TestConfig(@Value("${gateway.idm}") URI idm,
                      @Value("${gateway.movies}") URI movies,
                      @Value("${gateway.billing}") URI billing)
        throws JsonProcessingException
    {
        this.users = createModel(USERS_FILE_NAME);

        this.adminHeader = getAccessToken("Admin@example.com");
        this.employeeHeader = getAccessToken("Employee@example.com");
        this.premiumHeader = getAccessToken("Premium@example.com");

        this.idm = idm;
        this.movies = movies;
        this.billing = billing;
    }

    public URI getIdm()
    {
        return idm;
    }

    public URI getMovies()
    {
        return movies;
    }

    public URI getBilling()
    {
        return billing;
    }

    public String getAdminAccessToken()
    {
        return adminHeader;
    }

    public String getEmployeeAccessToken()
    {
        return employeeHeader;
    }

    public String getPremiumAccessToken()
    {
        return premiumHeader;
    }

    private String getAccessToken(String email)
    {
        return ((JSONObject) this.users.get(email)).getAsString("token");
    }

    private JSONObject createModel(String fileName)
    {
        try {
            File file = ResourceUtils.getFile(
                ResourceUtils.CLASSPATH_URL_PREFIX + fileName
            );

            return (JSONObject) new JSONParser(JSONParser.MODE_STRICTEST)
                                    .parse(new FileReader(file));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String fromHeader(String accessToken)
    {
        JSONObject request = new JSONObject();

        request.put(
            "accessToken",
            accessToken.startsWith(JWTAuthenticationFilter.BEARER_PREFIX) ?
                accessToken.substring(JWTAuthenticationFilter.BEARER_PREFIX.length()) :
                accessToken
        );

        return request.toJSONString();
    }
}
