package com.github.klefstad_teaching.cs122b.gateway.helper;

import com.github.klefstad_teaching.cs122b.core.security.JWTAuthenticationFilter;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public final class MockServices
{
    private final WireMockServer mockIdm;
    private final WireMockServer mockMovies;
    private final WireMockServer mockBilling;

    private final TestConfig     testConfig;
    private final ExpectedModels expectedModels;

    public MockServices(TestConfig testConfig, ExpectedModels expectedModels)
    {
        this.testConfig = testConfig;
        this.expectedModels = expectedModels;
        this.mockIdm = new WireMockServer(testConfig.getIdm().getPort());
        this.mockMovies = new WireMockServer(testConfig.getMovies().getPort());
        this.mockBilling = new WireMockServer(testConfig.getBilling().getPort());

        buildIdmStubs();
        buildMoviesStubs();
        buildBillingStubs();

        this.mockIdm.start();
        this.mockMovies.start();
        this.mockBilling.start();
    }

    private void buildIdmStubs()
    {
        mockIdm.stubFor(
            post("/authenticate")
                .withRequestBody(
                    matchingJsonPath("accessToken", matching(testConfig.getAdminAccessToken()))
                )
                .willReturn(
                    aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(expectedModels.getValidResponseString())
                        .withStatus(expectedModels.getValidResponse().getResult().status().value())
                )
        );

        mockIdm.stubFor(
            post("/authenticate")
                .withRequestBody(
                    matchingJsonPath("accessToken", matching(testConfig.getEmployeeAccessToken()))
                )
                .willReturn(
                    aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(expectedModels.getInvalidResponseString())
                        .withStatus(expectedModels.getInvalidResponse().getResult().status().value())
                )
        );

        mockIdm.stubFor(
            post("/authenticate")
                .withRequestBody(
                    matchingJsonPath("accessToken", matching(testConfig.getPremiumAccessToken()))
                )
                .willReturn(
                    aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(expectedModels.getExpiredResponseString())
                        .withStatus(expectedModels.getExpiredResponse().getResult().status().value())
                )
        );
    }

    private void buildMoviesStubs()
    {
        mockMovies.stubFor(
            get("/movie/search")
                .withHeader(
                    HttpHeaders.AUTHORIZATION,
                    matching(JWTAuthenticationFilter.BEARER_PREFIX +
                             testConfig.getAdminAccessToken())
                )
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        );
    }

    private void buildBillingStubs()
    {
        mockBilling.stubFor(
            get("/cart/retrieve")
                .withHeader(
                    HttpHeaders.AUTHORIZATION,
                    matching(JWTAuthenticationFilter.BEARER_PREFIX +
                             testConfig.getAdminAccessToken())
                )
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        );
    }
}
