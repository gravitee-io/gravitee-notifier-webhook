/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.notifier.webhook;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.gravitee.common.http.HttpMethod;
import io.gravitee.notifier.api.Notification;
import io.gravitee.notifier.webhook.configuration.HttpHeader;
import io.gravitee.notifier.webhook.configuration.WebhookNotifierConfiguration;
import io.vertx.core.Vertx;
import io.vertx.junit5.RunTestOnContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Aurelien PACAUD (aurelien.pacaud at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(VertxExtension.class)
public class HttpWebhookNotifierTest extends AbstractWebhookNotifier {

    @RegisterExtension
    private static final RunTestOnContext runTestOnContext = new RunTestOnContext();

    private Vertx vertx;

    private static final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());

    @BeforeAll
    static void beforeAll() {
        wireMockServer.start();
    }

    @BeforeEach
    void prepare(VertxTestContext testContext) {
        vertx = runTestOnContext.vertx();
        testContext.completeNow();
    }

    @AfterEach
    void cleanUp(VertxTestContext testContext) {
        wireMockServer.resetMappings();
        testContext.completeNow();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    private static Stream<Integer> httpStatus2xxProvider() {
        return IntStream.range(200, 300).boxed();
    }

    private static Stream<Integer> httpStatus4xxAnd5xxProvider() {
        return IntStream.range(400, 600).boxed();
    }

    @ParameterizedTest
    @MethodSource("httpStatus2xxProvider")
    public void webhookUrlSuccessStatuses(int httpStatus, VertxTestContext testContext) {
        wireMockServer.stubFor(post(urlPathEqualTo("/successStatuses")).willReturn(aResponse().withStatus(httpStatus)));

        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpWebhookURL(wireMockServer.port(), "successStatuses"));
        webhookNotifierConfiguration.setMethod(HttpMethod.POST);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(success(testContext));
    }

    @ParameterizedTest
    @MethodSource("httpStatus4xxAnd5xxProvider")
    public void webhookUrlErrorStatuses(int httpStatus, VertxTestContext testContext) {
        wireMockServer.stubFor(post(urlPathEqualTo("/errorStatuses")).willReturn(aResponse().withStatus(httpStatus)));

        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpWebhookURL(wireMockServer.port(), "errorStatuses"));
        webhookNotifierConfiguration.setMethod(HttpMethod.POST);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(error(testContext));
    }

    @Test
    public void webhookUrlWithoutQueryParam(VertxTestContext testContext) {
        wireMockServer.stubFor(post(urlPathEqualTo("/withoutQueryParam")).willReturn(ok()));

        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpWebhookURL(wireMockServer.port(), "withoutQueryParam"));
        webhookNotifierConfiguration.setMethod(HttpMethod.POST);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(success(testContext));
    }

    @Test
    public void webhookUrlWithQueryParam(VertxTestContext testContext) {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/withQueryParam"))
                .withQueryParam("param1", equalTo("param1"))
                .withQueryParam("param2", equalTo("param2"))
                .willReturn(ok())
        );

        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpWebhookURL(wireMockServer.port(), "withQueryParam?param1=param1&param2=param2"));
        webhookNotifierConfiguration.setMethod(HttpMethod.POST);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(success(testContext));
    }

    @Test
    public void webhookWithHeader(VertxTestContext testContext) {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/header"))
                .withHeader("header1", equalTo("value1"))
                .withHeader("header2", equalTo("value2"))
                .willReturn(ok())
        );

        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpWebhookURL(wireMockServer.port(), "header"));
        webhookNotifierConfiguration.setHeaders(List.of(new HttpHeader("header1", "value1"), new HttpHeader("header2", "value2")));
        webhookNotifierConfiguration.setMethod(HttpMethod.POST);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(success(testContext));
    }

    @Test
    public void webhookWithBody(VertxTestContext testContext) {
        wireMockServer.stubFor(post(urlPathEqualTo("/body")).withRequestBody(equalToJson("{ \"name\":\"value\" }")).willReturn(ok()));

        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpWebhookURL(wireMockServer.port(), "body"));
        webhookNotifierConfiguration.setBody("{ \"name\":\"value\" }");
        webhookNotifierConfiguration.setMethod(HttpMethod.POST);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(success(testContext));
    }

    @Test
    public void webhookUrlNotReachable(VertxTestContext testContext) {
        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpWebhookURL(wireMockServer.port(), "notReachable"));
        webhookNotifierConfiguration.setMethod(HttpMethod.POST);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(error(testContext));
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, names = "CONNECT", mode = EnumSource.Mode.EXCLUDE)
    public void webhookWithSpecificHttpMethod(HttpMethod method, VertxTestContext testContext) {
        wireMockServer.stubFor(request(method.name(), urlPathEqualTo("/" + method.name())).willReturn(ok()));

        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpWebhookURL(wireMockServer.port(), method.name()));
        webhookNotifierConfiguration.setMethod(method);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(success(testContext));
    }
}
