package net.axeora.eternallight.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VersionChecker {

    private static ExecutorService task = Executors.newFixedThreadPool(1);
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
                        new URL("http://www.spigotmc.org/api/general.php")).openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.getOutputStream().write((
                        "key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&" +
                                "resource=" + id).getBytes("UTF-8"));
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
        private byte[] current = null, latest = null;
        private PluginVersionState state;

        public VersionMeta(String current, String latest) {
            if (latest.equalsIgnoreCase("unknown")) {
                state = PluginVersionState.UNKNOWN;
                return;
            }
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

        public byte[] getCurrent() {
            return current;
        }

        public byte[] getLatest() {
            return latest;
        }
    }

    public enum PluginVersionState {
        DEV_BUILD, LATEST, BEHIND, UNKNOWN;

        public static PluginVersionState getState(byte[] c, byte[] l) {
            if (c != l && !isBehind(c, l)) return DEV_BUILD;
            if (c == l) return LATEST;
            return BEHIND;
        }

        private static boolean isBehind(byte[] c, byte[] l) {
            int v = c.length >= l.length ? l.length : c.length;
            for (int i = 0; i < v; i++) {
                if (c[i] < l[i]) return true;
            }
            return false;
        }
    }
}
