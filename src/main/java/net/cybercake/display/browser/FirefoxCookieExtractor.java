package net.cybercake.display.browser;

import net.cybercake.display.utils.Log;
import org.cef.network.CefCookie;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

public class FirefoxCookieExtractor extends CookieExtractor {

    private static final FilenameFilter CHECK_DATABASE = (folder, file) -> file.equalsIgnoreCase("cookies.sqlite");

    public FirefoxCookieExtractor() throws IOException {
        Log.debug("|- Extracting cookies from firefox...");
        this.copyDatabase();
        this.cookies = collectCookies();

        Log.debug("|- Extracted " + this.getCookies().size() + " cookies from firefox!");
    }

    @Override
    public List<CefCookie> getCookies() {
        return this.cookies;
    }

    private void copyDatabase() throws IOException {
        File databaseFile = newestCookieFileFrom(browserDirectories());
        Log.debug("| |- Copying database from " + databaseFile.getAbsolutePath() + "...");

        File tempFile = File.createTempFile("cookies", ".sqlite");

        Files.copy(databaseFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Log.debug("| | (stored in temporary file: " + tempFile.getPath() + ")");

        File finalLocation = new File(DB_PATH);
        if (!finalLocation.exists()) {
            if (finalLocation.getParentFile().mkdirs() && !finalLocation.createNewFile()) {
                throw new IllegalStateException("File does not exist and failed to create");
            }
        }
        Files.copy(tempFile.toPath(), finalLocation.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Log.debug("| |- Database copied to " + finalLocation.getAbsolutePath());
    }

    private File newestCookieFileFrom(List<String> directories) {
        File newest = null;
        long newestTime = 0;
        Log.debug("| |- Searching for newest 'cookies.sqlite' file...");
        for (String dir : directories) {
            List<File> files = new ArrayList<>();
            File directory = new File(dir);
            if (!directory.exists()) {
                continue;
            }

            File[] forCookies = directory.listFiles(CHECK_DATABASE);
            if (forCookies == null || forCookies.length == 0) {
                File[] profiles = new File(dir).listFiles((c, n) -> new File(c, n).isDirectory());
                if (profiles == null) {
                    continue;
                }

                for (File profile : profiles) {
                    if (!profile.isDirectory()) {
                        continue;
                    }

                    Log.debug("| | Checking profile " + profile.getName() + "...");
                    File[] expectedCookies = new File(profile.getAbsolutePath()).listFiles(CHECK_DATABASE);
                    if (expectedCookies == null) {
                        continue;
                    }
                    files.addAll(Arrays.stream(expectedCookies).toList());
                }
            } else {
                files.addAll(Arrays.stream(forCookies).toList());
            }


            for (File file : files) {
                if (file.lastModified() > newestTime) {
                    newestTime = file.lastModified();
                    newest = file;
                }

            }
        }

        if (newest == null) {
            Log.debug("| |- Unable to find newest cookies.sqlite file!");
            NullPointerException nullPointerException = new NullPointerException("newest");
            throw new IllegalStateException("Unable to find newest Firefox cookies database from " + directories, nullPointerException);
        }

        Log.debug("| |- Found a viable database.");

        return newest;
    }

    private List<String> browserDirectories() {
        List<String> paths = new ArrayList<>();
        String os = getProperty("os.name").toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            paths.add(getenv("APPDATA") + "**Mozilla**Firefox**Profiles");
            paths.add(getenv("LOCALAPPDATA") + "**Packages**Mozilla.Firefox_n80bbvh6b1yt2**LocalCache**Roaming**Mozilla**Firefox**Profiles");
            paths = finalize(paths);
            Log.debug("| Running WINDOWS [" + os + "], paths=" + paths);
        } else {
            paths.add(getProperty("user.home") + "**.mozilla**firefox");
            paths.add(getProperty("user.home") + "**snap**firefox**common**.mozilla**firefox");
            paths.add(getProperty("user.home") + "**.var**app**org.mozilla.firefox**.mozilla**firefox");
            paths = finalize(paths);
            Log.debug("| Running OTHER [" + os + "], paths=" + paths);
        }

        return paths;
    }

    private List<String> finalize(List<String> list) {
        return list.stream().map(s -> s.replace("**", File.separator)).toList();
    }

    private List<CefCookie> collectCookies() {
        List<CefCookie> cookies = new ArrayList<>();
        Log.debug("| Collecting from firefox's database...");
        try (Connection connection = DriverManager.getConnection(URL)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT host, name, value, path, expiry, isSecure, isHttpOnly FROM moz_cookies"
            )) {
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    CefCookie cookie = new CefCookie(
                            result.getString("name"),
                            result.getString("value"),
                            result.getString("host"),
                            result.getString("path"),
                            result.getInt("isSecure") == 1,
                            result.getInt("isHttpOnly") == 1,
                            new Date(),
                            new Date(),
                            true,
                            new Date(result.getLong("expiry"))
                    );
                    cookies.add(cookie);
                }
            }
        } catch (Exception exception) {
            throw new SQLiteDatabaseCompilationFailure(exception);
        }
        return cookies;
    }


    static class SQLiteDatabaseCompilationFailure extends IllegalStateException {

        SQLiteDatabaseCompilationFailure(Exception cause) {
            super("Failed to compile cookies from FIREFOX: " + cause, cause);
        }

    }

}
