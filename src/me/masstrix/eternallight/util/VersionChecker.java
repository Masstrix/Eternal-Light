package me.masstrix.eternallight.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;

public class VersionChecker {

    private static final String SPIGET_URL = "https://api.spiget.org/v2/resources/";
    private static final String LATEST_VERSION = "/versions/latest";
    private static final Gson gson = new Gson();
    private final int ID;
    private final String CURRENT_VER;
    private VersionMeta meta = null;

    public VersionChecker(int resource, String current) {
        ID = resource;
        this.CURRENT_VER = current;
    }

    public VersionChecker(int resource, JavaPlugin plugin) {
        ID = resource;
        CURRENT_VER = plugin.getDescription().getVersion();
    }

    /**
     * @return if the checker has run and successfully got the latest version.
     */
    public boolean hasCheckedVersion() {
        return meta != null;
    }

    /**
     * @return the last run meta. This meta is against the plugins version.
     */
    public VersionMeta getVersionMeta() {
        return meta;
    }

    /**
     * Connection to the spigot forums and
     *
     * @param callback callback method to be ran when the task is complete.
     */
    public VersionChecker run(VersionCallback callback) {
        new Thread(() -> {
            try {
                // Attempt to query to spiget.org
                URL url = new URL(SPIGET_URL + ID + LATEST_VERSION + "?" + System.currentTimeMillis());
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setUseCaches(true);
                conn.addRequestProperty("User-Agent", "Eternal Systems");
                conn.setDoOutput(true);

                // Read the json data sent from spiget
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
                    VersionMeta meta = new VersionMeta(CURRENT_VER, statistics.get("name").getAsString());
                    this.meta = meta;
                    callback.done(meta);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    callback.onError("Failed to parse json");
                }
            } catch (SocketException e) {
                callback.onTimeout();
                callback.onError("Connection Timeout");
            } catch (SSLException e) {
                callback.onError("SSLException");
            } catch (Exception e) {
                callback.onError("Exception");
            }
        }, "VersionChecker").start();
        return this;
    }

    /**
     * Callback value when checking the plugins version.
     */
    public static abstract class VersionCallback {
        abstract public void done(VersionMeta meta);

        public void onTimeout() {}

        public void onError(String msg) {}
    }

    /**
     * Meta for a plugins version returned in {@link VersionCallback#done(VersionMeta)}.
     * Versions are formatted x.x.x... and are compared against each other. Version checking
     * does not support extra text other than build numbers seperated by full stops, it compares
     * versions from first to last value in the array.
     */
    public static class VersionMeta {
        private String currentStr, latestStr;
        private byte[] current = null, latest = null;
        private VersionState state;

        /**
         * Creates a new instance with the current and latest versions added.
         *
         * @param current current version of the plugin.
         * @param latest latest version of the plugin.
         */
        public VersionMeta(String current, String latest) {
            if (latest == null || latest.equalsIgnoreCase("unknown")) {
                state = VersionState.UNKNOWN;
                return;
            }
            this.currentStr = current;
            this.latestStr = latest;
            try {
                this.current = getBytesFromVersion(current);
                this.latest = getBytesFromVersion(latest);
            } catch (Exception e) {
                state = VersionState.UNKNOWN;
                return;
            }
            this.state = VersionState.getState(this.current, this.latest);
        }

        /**
         * Converts and returns a version build array to an array of bytes.
         *
         * @param version version to convert to array of bytes.
         * @return an array of bytes parsed from version.
         */
        private byte[] getBytesFromVersion(String version) {
            String[] s = version.split("\\.");
            byte[] data = new byte[s.length];
            for (int i = 0; i < s.length; i++)
                data[i] = Byte.parseByte(s[i]);
            return data;
        }

        public boolean isLatest() {
            return state == VersionState.LATEST;
        }

        public boolean isOutdated() {
            return state == VersionState.BEHIND;
        }

        public boolean isDevBuild() {
            return state == VersionState.DEV_BUILD;
        }

        public boolean isUnknown() {
            return state == VersionState.UNKNOWN;
        }

        /**
         * @return the state of this version meta.
         */
        public VersionState getState() {
            return state;
        }

        public byte[] getCurrentBytes() {
            return current;
        }

        public byte[] getLatestBytes() {
            return latest;
        }

        public String getCurrentVersion() {
            return currentStr;
        }

        public String getLatestVersion() {
            return latestStr;
        }

        private String bytesToVer(byte[] version) {
            StringBuilder builder = new StringBuilder();
            for (byte build : version) {
                builder.append(build).append(".");
            }
            return builder.substring(0, builder.length() - 1);
        }
    }

    /**
     * State of a version.
     */
    public enum VersionState {
        DEV_BUILD, LATEST, BEHIND, UNKNOWN;

        public static VersionState getState(byte[] c, byte[] l) {
            if (Arrays.equals(c, l)) return LATEST;
            if (isBehind(c, l)) return BEHIND;
            return DEV_BUILD;
        }

        private static boolean isBehind(byte[] c, byte[] l) {
            int v = Math.max(c.length, l.length);
            for (int i = 0; i < v; i++) {
                byte cu = c.length > i ? c[i] : -1;
                byte la = l.length > i ? l[i] : -1;
                if (cu != -1 && la != -1 && (cu < la)) return true;
                if (la != -1 && cu == -1) return true;
            }
            return false;
        }
    }
}