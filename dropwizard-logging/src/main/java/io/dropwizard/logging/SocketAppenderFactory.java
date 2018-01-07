package io.dropwizard.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import io.dropwizard.logging.socket.DropwizardSocketAppender;
import io.dropwizard.util.Duration;

import javax.net.SocketFactory;

@JsonTypeName("tcp")
public class SocketAppenderFactory<E extends DeferredProcessingAware> extends AbstractAppenderFactory<E> {

    private String host;
    private int port = 4560;
    private Duration connectionTimeout = Duration.milliseconds(500);

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    @JsonProperty
    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public Appender<E> build(LoggerContext context, String applicationName, LayoutFactory<E> layoutFactory,
                             LevelFilterFactory<E> levelFilterFactory, AsyncAppenderFactory<E> asyncAppenderFactory) {
        final DropwizardSocketAppender<E> appender = new DropwizardSocketAppender<>(host, port, (int) connectionTimeout
            .toMilliseconds(), SocketFactory.getDefault());
        appender.setContext(context);
        appender.setName("socket-appender");

        final LayoutWrappingEncoder<E> layoutEncoder = new LayoutWrappingEncoder<>();
        layoutEncoder.setLayout(buildLayout(context, layoutFactory));
        appender.setEncoder(layoutEncoder);

        appender.addFilter(levelFilterFactory.build(threshold));
        getFilterFactories().forEach(f -> appender.addFilter(f.build()));
        appender.start();
        return appender;
    }

}
