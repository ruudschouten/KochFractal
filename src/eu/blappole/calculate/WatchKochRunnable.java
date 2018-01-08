package eu.blappole.calculate;

import eu.blappole.ui.FilenameGetter;
import eu.blappole.ui.controllers.Controller;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static com.sun.jmx.mbeanserver.Util.cast;
import static java.nio.file.StandardWatchEventKinds.*;

public class WatchKochRunnable implements Runnable {
    private WatchService watchService;
    private Map<WatchKey, Path> keys;
    private Controller controller;

    public WatchKochRunnable(Path dir, Controller controller) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.controller = controller;
        register(dir);

    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(this.watchService, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    @Override
    public void run() {
        while (true) {
            WatchKey key = null;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Path dir = keys.get(key);
            if (dir == null) continue;
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent<Path> watchEvent = cast(event);
                Path changed = watchEvent.context();
                if (changed.toString().equals(FilenameGetter.FILENAME)) {
                    try {
                        controller.loadKoch(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

}