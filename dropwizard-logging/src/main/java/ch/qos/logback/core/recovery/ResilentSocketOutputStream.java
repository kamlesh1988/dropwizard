package ch.qos.logback.core.recovery;

import javax.net.SocketFactory;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ResilentSocketOutputStream extends ResilientOutputStreamBase {

    private final String host;
    private final int port;
    private final int connectionTimeoutMs;
    private final SocketFactory socketFactory;

    public ResilentSocketOutputStream(String host, int port, int connectionTimeoutMs,
                                      SocketFactory socketFactory) {
        this.host = host;
        this.port = port;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.socketFactory = socketFactory;
        try {
            super.os = openNewOutputStream();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create a TCP connection to " + host + ":" + port, e);
        }
        this.presumedClean = true;
    }

    @Override
    String getDescription() {
        return "tcp [" + host + ":" + port + "]";
    }

    @Override
    OutputStream openNewOutputStream() throws IOException {
        final Socket socket = socketFactory.createSocket();
        socket.setKeepAlive(true);
        socket.connect(new InetSocketAddress(InetAddress.getByName(host), port), connectionTimeoutMs);
        return new BufferedOutputStream(socket.getOutputStream());
    }
}
