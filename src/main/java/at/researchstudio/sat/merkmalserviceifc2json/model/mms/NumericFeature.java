package at.researchstudio.sat.merkmalserviceifc2json.model.mms;

public class NumericFeature extends Feature {
    private final FeatureType featureType;

    public NumericFeature(String name, String quantityKind, String unit) {
        super(name);
        featureType = new FeatureType(quantityKind, unit);
    }

    private class FeatureType {
        private final String type = "NUMERIC";
        private final String quantityKind;
        private final String unit;

        public FeatureType(String quantityKind, String unit) {
            this.quantityKind = quantityKind;
            this.unit = unit;
        }
    }
}
