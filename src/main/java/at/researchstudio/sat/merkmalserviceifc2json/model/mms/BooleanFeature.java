package at.researchstudio.sat.merkmalserviceifc2json.model.mms;

public class BooleanFeature extends Feature {
    private final FeatureType featureType;

    public BooleanFeature(String name) {
        super(name);
        this.featureType = new FeatureType();
    }

    private class FeatureType {
        private final String type = "BOOLEAN";
    }
}
