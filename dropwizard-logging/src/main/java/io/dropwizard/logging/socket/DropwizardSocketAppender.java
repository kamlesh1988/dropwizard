package io.dropwizard.logging.socket;

import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.recovery.ResilentSocketOutputStream;
import ch.qos.logback.core.spi.DeferredProcessingAware;

import javax.net.SocketFactory;
import java.io.OutputStream;

public class DropwizardSocketAppender<E extends DeferredProcessingAware> extends OutputStreamAppender<E> {

    private final String host;
    private final int port;
    private final int connectionTimeoutMs;
    private SocketFactory socketFactory;

    public DropwizardSocketAppender(String host, int port, int connectionTimeoutMs, SocketFactory socketFactory) {
        this.host = host;
        this.port = port;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.socketFactory = socketFactory;
    }

    @Override
    public void start() {
        setOutputStream(socketOutputStream());
        super.start();
    }

    protected OutputStream socketOutputStream() {
        return new ResilentSocketOutputStream(host, port, connectionTimeoutMs, socketFactory);
    }
}

