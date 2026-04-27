package com.courier.config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * DataSourceConfig
 * ----------------
 * Ensures the "data/" directory exists before Hibernate/SQLite tries to
 * open the DB file. Without this, SQLite throws a "unable to open database"
 * error on a fresh machine because the parent folder doesn't exist.
 *
 * The DB file path is:  <project-root>/data/courier.db
 * This is an absolute-ish path via ${user.dir} so the SAME file is always
 * used regardless of IDE, terminal, or working directory.
 */
@Configuration
public class DataSourceConfig {

    @PostConstruct
    public void ensureDataDirectoryExists() {
        File dataDir = new File(System.getProperty("user.dir"), "data");
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("[DataSourceConfig] Created persistent DB directory: " + dataDir.getAbsolutePath());
            }
        } else {
            System.out.println("[DataSourceConfig] Using persistent DB at: " + dataDir.getAbsolutePath() + "/courier.db");
        }
    }
}
