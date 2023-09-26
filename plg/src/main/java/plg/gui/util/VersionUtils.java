package plg.gui.util;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;

public class VersionUtils {

    public static final String PLG_VERSION = readVersionFromPomFile();

    private static String readVersionFromPomFile() {
        try {
            // Create a MavenXpp3Reader to parse the pom.xml file
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model;

            try (FileReader fileReader = new FileReader("pom.xml")) {
                model = reader.read(fileReader);
            }
            return model.getVersion();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERR";
    }
}
