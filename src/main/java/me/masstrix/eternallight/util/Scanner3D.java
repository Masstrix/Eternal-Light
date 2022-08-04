package me.masstrix.eternallight.util;

public class Scanner3D {

    private int x, y, z;
    private ScannerCallback callback;

    public void listen(ScannerCallback callback) {
        this.callback = callback;
    }

    public void scan() {
        scan(callback);
    }

    public void scan(ScannerCallback callback) {
        if (callback == null) return;


    }

    public interface ScannerCallback {

        /**
         * @param x
         * @param y
         * @param z
         * @return
         */
        boolean onBlockScanned(int x, int y, int z);
    }
}
