package at.researchstudio.sat.merkmalserviceifc2json.commands;

import io.airlift.airline.Option;
import io.airlift.airline.OptionType;

public abstract class AbstractCommand implements Runnable {
    @Option(type= OptionType.GLOBAL, name = {"-o", "--outputFile"}, description = "Specified outputFile path and File Name, defaults to ./extracted-features.json")
    public String outputFileName = "extracted-features.json";

    @Option(type= OptionType.GLOBAL, name = {"-k", "--keepTemp"}, description = "If set, does not delete the temp ttl files from the system")
    public boolean keepTempFiles;
}
