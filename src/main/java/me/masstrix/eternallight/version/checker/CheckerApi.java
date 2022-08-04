package me.masstrix.eternallight.version.checker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.masstrix.eternallight.version.Version;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public enum CheckerApi {
    SPIGET {
        @Override
        public void run(int id, String current, VersionCallback callback) {
            try {
                URL url = new URL(SPIGET_URL + id + LATEST_VERSION + "?" + System.currentTimeMillis());
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setUseCaches(true);
                conn.addRequestProperty("User-Agent", "Eternal Systems");
                conn.setDoOutput(true);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String input;
                StringBuilder content = new StringBuilder();
                while ((input = br.readLine()) != null) {
                    content.append(input);
                }
                br.close();
                JsonObject statistics;
                try {
                    statistics = gson.fromJson(content.toString(), JsonObject.class);
                    callback.done(new VersionCheckInfo(current, statistics.get("name").getAsString()));
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    callback.done(new VersionCheckInfo(current, Version.UNKNOWN));
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.done(new VersionCheckInfo(current, Version.UNKNOWN));
            }
        }
    },
    SPIGOT_LEGACY {
        @Override
        public void run(int id, String current, VersionCallback callback) {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setUseCaches(true);
                conn.setDoOutput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = reader.readLine();


                callback.done(new VersionCheckInfo(current, "1.0"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private static final String SPIGET_URL = "https://api.spiget.org/v2/resources/";
    private static final String LATEST_VERSION = "/versions/latest";
    private static final Gson gson = new Gson();

    public void run(int id, String current, VersionCallback callback) {

    }
}
