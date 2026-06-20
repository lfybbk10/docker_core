package ru.mentee.power;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("Тестирование Ingress маршрутизации")
class IngressTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private final String SHOP_URL = "https://shop.mentee-power.ru";
    private final String ADMIN_URL = "https://admin.mentee-power.ru";

    @BeforeAll
    static void trustSelfSignedCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        HostnameVerifier allowAllHosts = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allowAllHosts);
    }

    @Test
    @DisplayName("Должен корректно маршрутизировать запросы к user-service")
    void shouldRouteToUserService() {
        // given
        String endpoint = SHOP_URL + "/api/users";

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);

        // then
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно маршрутизировать запросы к product-service")
    void shouldRouteToProductService() {
        // given
        String endpoint = SHOP_URL + "/api/products";

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);

        // then
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Должен редиректить HTTP на HTTPS")
    void shouldRedirectHttpToHttps() {
        // given
        String httpEndpoint = "http://shop.mentee-power.ru/api/users";

        // when
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Proto", "http");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                httpEndpoint,
                HttpMethod.GET,
                entity,
                String.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PERMANENT_REDIRECT);
        assertThat(response.getHeaders().getLocation().toString())
                .startsWith("https://");
    }

    @Test
    @DisplayName("Должен требовать авторизацию для admin панели")
    void shouldRequireAuthForAdmin() {
        // given
        String endpoint = ADMIN_URL + "/";

        // when
        ResponseEntity<String> unauthorizedResponse = restTemplate.getForEntity(endpoint, String.class);
        TestRestTemplate authorizedRestTemplate = restTemplate.withBasicAuth("admin", "admin123");
        ResponseEntity<String> authorizedResponse = authorizedRestTemplate.getForEntity(endpoint, String.class);

        // then
        assertThat(unauthorizedResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(authorizedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(authorizedResponse.getBody()).contains("admin-service");
    }

    @Test
    @DisplayName("Должен поддерживать CORS для shop домена")
    void shouldSupportCorsForShop() {
        // given
        String endpoint = SHOP_URL + "/api/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin("https://frontend.mentee-power.ru");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                .isEqualTo("*");
    }
}
