package me.masstrix.eternallight.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VersionChecker {

    private static ExecutorService task = Executors.newSingleThreadExecutor(r -> new Thread(r, "VersionChecker"));
    private int id;
    private String current;

    public VersionChecker(int resource, String current) {
        id = resource;
        this.current = current;
    }

    /**
     * Connection to the spigot forums and
     *
     * @param callback callback method to be ran when the task is complete.
     */
    public void run(VersionCallback<VersionMeta> callback) {
        task.execute(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) (
                        new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id)).openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                String latest = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
                callback.done(new VersionMeta(current, latest));
            } catch (Exception e) {
                e.printStackTrace();
                callback.done(new VersionMeta(current, "unknown"));
            }
        });
    }

    /**
     * Callback value when checking the plugins version.
     *
     * @param <T> callback value.
     */
    public interface VersionCallback <T extends VersionMeta> {
        void done(T t);
    }

    /**
     * Meta for a plugins version returned in {@link VersionCallback#done(VersionMeta)}.
     */
    public class VersionMeta {
        private String currentStr, latestStr;
        private byte[] current = null, latest = null;
        private PluginVersionState state;

        VersionMeta(String current, String latest) {
            if (latest == null || latest.equalsIgnoreCase("unknown")) {
                state = PluginVersionState.UNKNOWN;
                return;
            }
            this.currentStr = current;
            this.latestStr = latest;
            try {
                this.current = getBytes(current.split("\\."));
                this.latest = getBytes(latest.split("\\."));
            } catch (Exception e) {
                state = PluginVersionState.UNKNOWN;
                return;
            }
            this.state = PluginVersionState.getState(this.current, this.latest);
        }

        private byte[] getBytes(String[] s) {
            byte[] data = new byte[s.length];
            for (int i = 0; i < s.length; i++)
                data[i] = Byte.parseByte(s[i]);
            return data;
        }

        public PluginVersionState getState() {
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

    public enum PluginVersionState {
        DEV_BUILD, LATEST, BEHIND, UNKNOWN;

        public static PluginVersionState getState(byte[] c, byte[] l) {
            if (Arrays.equals(c, l)) return LATEST;
            if (isBehind(c, l)) return BEHIND;
            return DEV_BUILD;
        }

        private static boolean isBehind(byte[] c, byte[] l) {
            int v = c.length > l.length ? c.length : l.length;
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