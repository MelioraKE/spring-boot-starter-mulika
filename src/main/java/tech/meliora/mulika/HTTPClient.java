package tech.meliora.mulika;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 *
 * @author kamochu
 */
public class HTTPClient {

    public static HTTPResponse send(String url, String request, String method, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException {

        URL obj = new URL(url);

        StringBuilder response;

        //read the response
        int responseCode;

//        https = (url != null && url.contains("https"));
        if (url.contains("https")) {
            // https

            HttpsURLConnection connection;

            connection = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }

            // Send post request
            connection.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(request);
                wr.flush();
            }

            responseCode = connection.getResponseCode();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        } else {
            // https

            HttpURLConnection connection;

            connection = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }

            // Send post request
            connection.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(request);
                wr.flush();
            }

            responseCode = connection.getResponseCode();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        }

        return new HTTPResponse(responseCode, response.toString());

    }

    public static HTTPResponse get(String url, Map<String, String> headers, int connectTimeout, int readTimeout)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {

        URL obj = new URL(url);

        StringBuilder response;

        //read the response
        int responseCode;

        HttpURLConnection connection;

//        https = (url != null && url.contains("https"));
        if (url.startsWith("https")) {
            // https


            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            connection = (HttpsURLConnection) obj.openConnection();
        } else {
            // https
            connection = (HttpURLConnection) obj.openConnection();
        }

        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);

        if (headers != null) {
            headers.forEach(connection::setRequestProperty);
        }

        responseCode = connection.getResponseCode();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return new HTTPResponse(responseCode, response.toString());

    }


}

 