package at.researchstudio.sat.merkmalserviceifc2json.outsource.model.mms;

public abstract class Feature {
    private final String name;

    public Feature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
