import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

interface SessionListener {
    void onDisconnect(Session session);
}

public class Session extends Thread {
    private Socket socket;
    private SessionListener listener;
    private Broadcaster broadcaster;

    Session(Socket socket, Broadcaster broadcaster) {
        this.socket = socket;
        this.broadcaster = broadcaster;
    }

    void setSessionListener(SessionListener listener) {
        this.listener = listener;
    }

    OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();

            byte[] buf = new byte[256];
            BufferedReader bufferedReader = null;
            Reader reader = new InputStreamReader(is);
            while (true) {
                int len = is.read(buf);
                if (len == -1)
                    break;


                String message = new String(buf, 0, len);
                broadcaster.sendPosition(message);
            }

            if (listener != null)
                listener.onDisconnect(this);

            System.out.println("Connection Thread end");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
