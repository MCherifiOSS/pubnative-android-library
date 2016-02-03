package net.pubnative.library.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PubnativeAPIResponse {

    public String result;
    public Exception error;

    public void setResult(String result) {
        this.result = result;
    }

    public void setResult(InputStream is) {

        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        try {

            int result = bis.read();
            while(result != -1) {
                byte b = (byte)result;
                buf.write(b);
                result = bis.read();
            }
            this.result = buf.toString();

        } catch (IOException e) {
            e.printStackTrace();
            this.error = e;
        }
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public interface Listener {
        void onResponse(String response);

        void onErrorResponse(Exception error);
    }

    public boolean isSuccess() {
        return this.error == null;
    }

}
