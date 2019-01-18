# Proof-of-Concept for using TestContainers

## Troubleshooting

### Could not find a valid Docker environment.

When first starting a test on Windows I got the following error messages from
`org.testcontainers.dockerclient.DockerClientProviderStrategy`:

```text
Could not find a valid Docker environment. Please check configuration. Attempted configurations were:
    NpipeSocketClientProviderStrategy: failed with exception InvalidConfigurationException (ping failed). Root cause TimeoutException (null)
    WindowsClientProviderStrategy: failed with exception TimeoutException (Timeout waiting for result with exception). Root cause ConnectException (Connection refused: connect)
    DockerMachineClientProviderStrategy: failed with exception InvalidConfigurationException (Exception when executing docker-machine status )
As no valid configuration was found, execution cannot continue
```

Problem (in this case) was, that [Docker Desktop][docker-desktop] was not running
after an update. Thus, ensure Docker Desktop is installed and running.

### ClassNotFoundException: com.mysql.jdbc.Driver

When doing the first MySQL test, it took a considerable amount of time until
it failed miserably. Reason was, that even though we were not using MySQL
classes directly (at that point in time),
`org.testcontainers.containers.JdbcDatabaseContainer` relies on the availability
of the JDBC driver. Unfortunately, it does not fail immediately but only after
several retries.

[docker-desktop]: <https://www.docker.com/products/docker-desktop> "Docker Desktop | Docker"
