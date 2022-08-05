package com.github.klefstad_teaching.cs122b.gateway.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.core.base.ResultResponse;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
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
public class ExpectedModels
{
    private final String validResponseString;
    private final String expiredResponseString;
    private final String invalidResponseString;

    private final ResultResponse validResponse;
    private final ResultResponse expiredResponse;
    private final ResultResponse invalidResponse;

    @Autowired
    public ExpectedModels(ObjectMapper objectMapper)
        throws JsonProcessingException
    {
        this.validResponse = ResultResponse.of(IDMResults.ACCESS_TOKEN_IS_VALID);
        this.validResponseString =
            objectMapper.writeValueAsString(
                ResultResponse.of(IDMResults.ACCESS_TOKEN_IS_VALID)
            );
        this.expiredResponse = ResultResponse.of(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
        this.expiredResponseString =
            objectMapper.writeValueAsString(
                ResultResponse.of(IDMResults.ACCESS_TOKEN_IS_EXPIRED)
            );
        this.invalidResponse = ResultResponse.of(IDMResults.ACCESS_TOKEN_IS_INVALID);
        this.invalidResponseString =
            objectMapper.writeValueAsString(
                ResultResponse.of(IDMResults.ACCESS_TOKEN_IS_INVALID)
            );
    }

    public String getValidResponseString()
    {
        return validResponseString;
    }

    public String getExpiredResponseString()
    {
        return expiredResponseString;
    }

    public String getInvalidResponseString()
    {
        return invalidResponseString;
    }

    public ResultResponse getInvalidResponse()
    {
        return invalidResponse;
    }

    public ResultResponse getValidResponse()
    {
        return validResponse;
    }

    public ResultResponse getExpiredResponse()
    {
        return expiredResponse;
    }

}
