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
import io.gravitee.notifier.webhook.configuration.WebhookNotifierConfiguration;
import io.vertx.core.Vertx;
import io.vertx.junit5.RunTestOnContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @author Aurelien PACAUD (aurelien.pacaud at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(VertxExtension.class)
public class HttpsWebhookNotifierTest extends AbstractWebhookNotifier {

    @RegisterExtension
    private static final RunTestOnContext runTestOnContext = new RunTestOnContext();

    private Vertx vertx;

    private static final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicHttpsPort().httpDisabled(true));

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

    @Test
    public void webhookHttpsUrl(VertxTestContext testContext) {
        wireMockServer.stubFor(post(urlPathEqualTo("/webhook")).willReturn(ok()));

        WebhookNotifierConfiguration webhookNotifierConfiguration = new WebhookNotifierConfiguration();
        webhookNotifierConfiguration.setUrl(buildHttpsWebhookURL(wireMockServer.httpsPort(), "webhook"));
        webhookNotifierConfiguration.setMethod(HttpMethod.POST);

        new WebhookNotifier(webhookNotifierConfiguration).doSend(new Notification(), Map.of()).handle(success(testContext));
    }
}
