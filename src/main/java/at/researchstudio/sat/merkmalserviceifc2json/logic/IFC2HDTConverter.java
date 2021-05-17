package at.researchstudio.sat.merkmalserviceifc2json.logic;

import be.ugent.IfcSpfReader;
import org.apache.commons.io.FilenameUtils;
import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.exceptions.ParserException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTSpecification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IFC2HDTConverter {
    private static final String BASE_URI = "https://researchstudio.at/";

    public static List<HDT> readFromFiles(boolean keepTempFiles, List<File> ifcFiles) {
        List<HDT> hdtData = new ArrayList<>();

        int i = 0;
        for (File ifcFile : ifcFiles) {
            File tempOutputFile =
                    new File(
                            "temp_ttl_"
                                    + FilenameUtils.removeExtension(ifcFile.getName())
                                    + ".ttl");
            try {
                hdtData.add(readFromFile(keepTempFiles, ifcFile, tempOutputFile));
                System.out.println("Converted " + (++i) + "/" + ifcFiles.size() + " Files to HDT");
            } catch (Exception e) {
                System.err.println(
                        "Can't convert file: "
                                + ifcFile.getAbsolutePath()
                                + " Reason: "
                                + e.getMessage());
            }
        }

        return hdtData;
    }
    private static HDT readFromFile(boolean keepTempFiles, File ifcFile, File outputFile)
            throws IOException, ParserException {
        IfcSpfReader r = new IfcSpfReader();

        r.setup(ifcFile.getAbsolutePath());
        r.convert(ifcFile.getAbsolutePath(), outputFile.getAbsolutePath(), BASE_URI);

        HDT hdt =
                HDTManager.generateHDT(
                        outputFile.getAbsolutePath(),
                        BASE_URI,
                        RDFNotation.TURTLE,
                        new HDTSpecification(),
                        null);

        if (!keepTempFiles && !outputFile.delete()) {
            System.err.println(
                    "Could not delete temp-file: "
                            + outputFile.getAbsolutePath()
                            + " try removing it manually later...");
        }
        return hdt;
    }
}
