package at.researchstudio.sat.merkmalserviceifc2json.commands;

import at.researchstudio.sat.merkmalserviceifc2json.logic.*;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

@Command(name = "directory", description = "Parse ifc files in specified directory")
public class ExtractFromDirectory extends AbstractCommand {
    @Arguments(
            description = "Specify folder to parse ifc files in, defaults to current Directory",
            required = true)
    public List<String> directoryNames;


    public void run() {
        if (Objects.isNull(directoryNames) || directoryNames.isEmpty()) {
            System.err.println("ERR: No directorie(s) specified");
        } else {
            List<File> ifcFiles = new ArrayList<>();

            for (String directoryName : directoryNames) {
                File f = new File(directoryName);

                if (f.exists()) {
                    if (f.isFile()) {
                        System.err.println(
                                "Specified Directory '" + directoryName + "' is a File use command file instead");
                    } else {
                        System.out.println("Searching for IFC Files within Directory: " + f.getAbsolutePath());
                        ifcFiles.addAll(
                                Arrays.asList(
                                        Objects.requireNonNull(
                                                f.listFiles((FileFilter) new SuffixFileFilter(".ifc")))));
                    }
                } else {
                    System.err.println("Specified Directory '" + directoryName + "' does not exist");
                }
            }
            System.out.println("Found " + ifcFiles.size() + " IFC File(s)");

            PropertyExtractor.parseIfcFilesToJsonFeatures(this.keepTempFiles, this.outputFileName, ifcFiles);
        }
    }
}
