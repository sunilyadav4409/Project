package com.jbhunt.finance.carrierpayment.autopay.configuration;

import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientServletFactory {
    public WebClient createWebClient (String registrationId, String tokenUri, String clientId, String clientSecret, ExchangeStrategies exchangeStrategies) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                getAuthorizedClientManager(registrationId, tokenUri, clientId, clientSecret)
        );
        oauth2.setDefaultClientRegistrationId(registrationId);
        return WebClient.builder()
                .apply(oauth2.oauth2Configuration())
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    private ClientRegistrationRepository getRegistration(String registrationId, String tokenUri, String clientId, String clientSecret) {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId(registrationId)
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        return new InMemoryClientRegistrationRepository(registration);
    }

    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    private OAuth2AuthorizedClientManager getAuthorizedClientManager(String registrationId, String tokenUri, String clientId, String clientSecret) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();
        ClientRegistrationRepository clientRegistrationRepository = getRegistration(registrationId, tokenUri, clientId, clientSecret);
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        oAuth2AuthorizedClientService(clientRegistrationRepository));
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}