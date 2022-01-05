# TIS Revalidation Core

## About
Revalidation core service provide trainee information to other microservices.

## TODO
 - Clean up which aspects of revalidation should be fulfilled by this service.
 - The following endpoints are **NOT** actively routed to this service from the integration service:
    - `/revalidation/api/admins` (Currently used by recommendation service)
    - `/revalidation/api/doctors/*` (Currently used by recommendation service)
 - The following endpoints **ARE** actively routed to this service from the integration service:
    - `/revalidation/api/environment` (appears to be superfluous)
    - `/revalidation/api/trainee/*` (used for notes functionality, should this just be a "notes service?")
 - Provide `SENTRY_DSN` and `SENTRY_ENVIRONMENT` as environmental variables
   during deployment and need to make the `SENTRY_ENVIRONMENT` dynamic in the future.
 - Add repository to Dependabot.

## Workflow
The `CI/CD Workflow` is triggered on push to any branch.

![CI/CD workflow](.github/workflows/ci-cd-workflow.svg "CI/CD Workflow")

## Versioning
This project uses [Semantic Versioning](semver.org).

## License
This project is license under [The MIT License (MIT)](LICENSE).

[task-definition]: .aws/task-definition.json
