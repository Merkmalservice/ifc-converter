package at.researchstudio.sat.merkmalserviceifc2json.model.mms;

public class ReferenceFeature extends Feature {
    private final FeatureType featureType = new FeatureType();

    public ReferenceFeature(String name) {
        super(name);
    }


    private class FeatureType {
        private final String type = "REFERENCE";
    }
}
