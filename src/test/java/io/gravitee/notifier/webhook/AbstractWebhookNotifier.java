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

import io.vertx.junit5.VertxTestContext;
import java.util.function.BiFunction;

/**
 * @author Aurelien PACAUD (aurelien.pacaud at graviteesource.com)
 * @author GraviteeSource Team
 */
public class AbstractWebhookNotifier {

    protected static BiFunction<Void, Throwable, Object> success(VertxTestContext testContext) {
        return (unused, throwable) -> {
            if (throwable != null) {
                testContext.failNow(throwable);
            } else {
                testContext.completeNow();
            }

            return null;
        };
    }

    protected static BiFunction<Void, Throwable, Object> error(VertxTestContext testContext) {
        return (unused, throwable) -> {
            if (throwable != null) {
                testContext.completeNow();
            } else {
                testContext.failNow(new IllegalStateException("An error was expected but none has been thrown"));
            }

            return null;
        };
    }

    protected static String buildHttpWebhookURL(int port, String path) {
        return buildHttpWebhookURL("http", port, path);
    }

    protected static String buildHttpsWebhookURL(int port, String path) {
        return buildHttpWebhookURL("https", port, path);
    }

    private static String buildHttpWebhookURL(String scheme, int port, String path) {
        return scheme + "://127.0.0.1:" + port + "/" + path;
    }
}
