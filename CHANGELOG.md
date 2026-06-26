# [3.0.0](https://github.com/gravitee-io/gravitee-notifier-webhook/compare/2.0.0...3.0.0) (2026-06-26)


* feat!: upgrade to Vert.x 5 ([c0f1bb4](https://github.com/gravitee-io/gravitee-notifier-webhook/commit/c0f1bb42b0d35f0d437db3077164725ac482445b))


### BREAKING CHANGES

* migrate the webhook notifier to Vert.x 5. The HTTP client
pool is now configured through PoolOptions (setHttp1MaxSize/setHttp2MaxSize)
instead of HttpClientOptions.setMaxPoolSize.

Align Gravitee dependencies to released versions:
- gravitee-bom: 9.0.0
- gravitee-common: 5.0.0
- gravitee-node: 9.0.0
- gravitee-notifier-api: 2.0.0
- gravitee-parent: 24.0.2 (keep Java 21 baseline, do not compile in Java 25)

# [2.0.0](https://github.com/gravitee-io/gravitee-notifier-webhook/compare/1.1.3...2.0.0) (2026-03-16)


### chore

* update project configuration ([b2628ba](https://github.com/gravitee-io/gravitee-notifier-webhook/commit/b2628bab9b705a17229500d0bfaa8d5b7de1adcf))


### BREAKING CHANGES

* requires JDK 21 and notifier-api 2.0.0

## [1.1.3](https://github.com/gravitee-io/gravitee-notifier-webhook/compare/1.1.2...1.1.3) (2023-08-28)


### Bug Fixes

* accept 2xx status instead of only 200 ([e348a30](https://github.com/gravitee-io/gravitee-notifier-webhook/commit/e348a30dec842755f6219010200056ccc844cbbd))

## [1.1.2](https://github.com/gravitee-io/gravitee-notifier-webhook/compare/1.1.1...1.1.2) (2023-08-16)


### Bug Fixes

* http headers with default constructor for serialization ([caa7f25](https://github.com/gravitee-io/gravitee-notifier-webhook/commit/caa7f257df9e9658457a23e22111441078d2df0f))

## [1.1.1](https://github.com/gravitee-io/gravitee-notifier-webhook/compare/1.1.0...1.1.1) (2023-07-05)


### Bug Fixes

* keep the query params when the webhook is called ([4a510ef](https://github.com/gravitee-io/gravitee-notifier-webhook/commit/4a510efe4f71a55aee277a161fb162d63a0d6fa5))
