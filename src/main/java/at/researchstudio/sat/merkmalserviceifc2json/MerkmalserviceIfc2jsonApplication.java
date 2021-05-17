package at.researchstudio.sat.merkmalserviceifc2json;

import at.researchstudio.sat.merkmalserviceifc2json.commands.ExtractFromDirectory;
import at.researchstudio.sat.merkmalserviceifc2json.commands.ExtractFromFile;
import io.airlift.airline.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MerkmalserviceIfc2jsonApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(MerkmalserviceIfc2jsonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("ifc2json")
                .withDescription("Extracts Features within an IFC File or the IFC Files specified in a Directory to json")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class, ExtractFromFile.class, ExtractFromDirectory.class);

        Cli<Runnable> gitParser = builder.build();

        gitParser.parse(args).run();
    }
}
