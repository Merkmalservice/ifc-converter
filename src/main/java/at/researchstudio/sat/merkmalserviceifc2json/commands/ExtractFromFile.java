package at.researchstudio.sat.merkmalserviceifc2json.commands;

import at.researchstudio.sat.merkmalserviceifc2json.logic.*;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Command(name = "file", description = "Parse specific ifc files or directories")
public class ExtractFromFile extends AbstractCommand {
    @Arguments(description = "Specify ifc file(s) to parse, if used overrides any specified Directory", required = true)
    public List<String> fileNames;

    public void run() {
        if (Objects.isNull(fileNames) || fileNames.isEmpty()) {
            System.err.println("ERR: No file(s) specified");
        } else {
            List<File> ifcFiles = new ArrayList<>();

            for(String fileName : fileNames) {
                File f = new File(fileName);
                if (f.exists() && f.isFile() && f.getName().endsWith(".ifc")) {
                    ifcFiles.add(f);
                }
            }

            PropertyExtractor.parseIfcFilesToJsonFeatures(this.keepTempFiles, this.outputFileName, ifcFiles);
        }
    }
}