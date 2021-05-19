package at.researchstudio.sat.merkmalserviceifc2json.logic;

import at.researchstudio.sat.merkmalserviceifc2json.model.ifc.IfcProperty;
import at.researchstudio.sat.merkmalserviceifc2json.model.ifc.IfcUnit;
import at.researchstudio.sat.merkmalserviceifc2json.model.ifc.vocab.IfcPropertyType;
import at.researchstudio.sat.merkmalserviceifc2json.model.ifc.vocab.IfcUnitType;
import at.researchstudio.sat.merkmalserviceifc2json.outsource.model.mms.BooleanFeature;
import at.researchstudio.sat.merkmalserviceifc2json.outsource.model.mms.Feature;
import at.researchstudio.sat.merkmalserviceifc2json.outsource.model.mms.NumericFeature;
import at.researchstudio.sat.merkmalserviceifc2json.outsource.model.mms.StringFeature;
import at.researchstudio.sat.merkmalserviceifc2json.utils.Utils;
import at.researchstudio.sat.merkmalserviceifc2json.vocab.qudt.QudtQuantityKind;
import at.researchstudio.sat.merkmalserviceifc2json.vocab.qudt.QudtUnit;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdtjena.HDTGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static at.researchstudio.sat.merkmalserviceifc2json.outsource.utils.Utils.writeToJson;

public class PropertyExtractor {
    public static void parseIfcFilesToJsonFeatures(boolean keepTempFiles, String outputFileName, List<File> ifcFiles) {
        List<HDT> hdtData = IFC2HDTConverter.readFromFiles(keepTempFiles, ifcFiles);
        List<Feature> extractedFeatures = new ArrayList<>();
        int extractedIfcProperties = 0;
        for (HDT hdt : hdtData) {
            try {
                Map<IfcUnitType, List<IfcUnit>> extractedProjectUnitMap = extractProjectUnits(hdt);
                Map<IfcPropertyType, List<IfcProperty>> extractedPropertyMap = extractPropertiesFromHdtData(
                                hdt, extractedProjectUnitMap);
                extractedIfcProperties += extractedPropertyMap.values().stream()
                                .mapToInt(Collection::size)
                                .sum();
                extractedFeatures.addAll(extractFeaturesFromProperties(extractedPropertyMap));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Extracted " + extractedIfcProperties + " out of the " + ifcFiles.size() + " ifcFiles");
        System.out.println("Parsed " + extractedFeatures.size() + " jsonFeatures");
        System.out.println("into File: " + new File(outputFileName).getAbsolutePath());
        System.out.println("-------------------------------------------------------------------------------");
        try {
            writeToJson(outputFileName, extractedFeatures);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        System.out.println("EXITING, converted " + hdtData.size() + "/" + ifcFiles.size());
        if (hdtData.size() != ifcFiles.size()) {
            System.err.println(
                            "Not all Files could be converted, look in the log above to find out why");
        }
    }

    private static Map<IfcUnitType, List<IfcUnit>> extractProjectUnits(HDT hdtData)
                    throws IOException {
        HDTGraph graph = new HDTGraph(hdtData);
        Model model = ModelFactory.createModelForGraph(graph);
        String extractPropNamesQuery = Utils.readFileToString("classpath:extract_projectunits.rq");
        try (QueryExecution qe = QueryExecutionFactory.create(extractPropNamesQuery, model)) {
            ResultSet rs = qe.execSelect();
            List<IfcUnit> extractedUnits = new ArrayList<>();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                extractedUnits.add(new IfcUnit(qs.getResource("unitType"), qs.getResource("unitMeasure")));
            }
            return extractedUnits.stream().collect(Collectors.groupingBy(IfcUnit::getType));
        }
    }

    private static Map<IfcPropertyType, List<IfcProperty>> extractPropertiesFromHdtData(
                    HDT hdtData, Map<IfcUnitType, List<IfcUnit>> projectUnits) throws IOException {
        HDTGraph graph = new HDTGraph(hdtData);
        Model model = ModelFactory.createModelForGraph(graph);
        String extractPropNamesQuery = Utils.readFileToString("classpath:extract_properties.rq");
        try (QueryExecution qe = QueryExecutionFactory.create(extractPropNamesQuery, model)) {
            ResultSet rs = qe.execSelect();
            List<IfcProperty> extractedProperties = new ArrayList<>();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                extractedProperties.add(new IfcProperty(qs, projectUnits));
            }
            return extractedProperties.stream().collect(Collectors.groupingBy(IfcProperty::getType));
        }
    }

    private static List<Feature> extractFeaturesFromProperties(
                    Map<IfcPropertyType, List<IfcProperty>> extractedProperties) {
        List<Feature> extractedFeatures = new ArrayList<>();
        for (Map.Entry<IfcPropertyType, List<IfcProperty>> entry : extractedProperties.entrySet()) {
            IfcPropertyType ifcPropertyType = entry.getKey();
            String logString = entry.getValue().size() + " " + ifcPropertyType + " Properties";
            switch (ifcPropertyType) {
                case EXPRESS_BOOL:
                case BOOL:
                    System.out.println(logString);
                    extractedFeatures.addAll(
                                    entry.getValue().stream()
                                                    .map(ifcProperty -> new BooleanFeature(ifcProperty.getName()))
                                                    .collect(Collectors.toList()));
                    break;
                case TEXT:
                case LABEL:
                    System.out.println(logString);
                    extractedFeatures.addAll(
                                    entry.getValue().stream()
                                                    .map(ifcProperty -> new StringFeature(ifcProperty.getName()))
                                                    .collect(Collectors.toList()));
                    break;
                case VOLUME_MEASURE:
                    System.out.println(logString);
                    extractedFeatures.addAll(
                                    entry.getValue().stream()
                                                    .map(
                                                                    ifcProperty ->
                                                                                    new NumericFeature(
                                                                                                    ifcProperty.getName(),
                                                                                                    QudtQuantityKind.VOLUME,
                                                                                                    QudtUnit.getUnitBasedOnIfcUnitMeasureLengthBasedOnName(
                                                                                                                    ifcProperty.getMeasure())))
                                                    .collect(Collectors.toList()));
                    break;
                case AREA_MEASURE:
                    System.out.println(logString);
                    extractedFeatures.addAll(
                                    entry.getValue().stream()
                                                    .map(
                                                                    ifcProperty ->
                                                                                    new NumericFeature(
                                                                                                    ifcProperty.getName(),
                                                                                                    QudtQuantityKind.AREA,
                                                                                                    QudtUnit.getUnitBasedOnIfcUnitMeasureLengthBasedOnName(
                                                                                                                    ifcProperty.getMeasure())))
                                                    .collect(Collectors.toList()));
                    break;
                case LENGTH_MEASURE:
                case POSITIVE_LENGTH_MEASURE:
                    System.out.println(logString);
                    extractedFeatures.addAll(
                                    entry.getValue().stream()
                                                    .map(
                                                                    ifcProperty ->
                                                                                    new NumericFeature(
                                                                                                    ifcProperty.getName(),
                                                                                                    QudtQuantityKind.getQuantityKindLengthBasedOnName(
                                                                                                                    ifcProperty.getName()),
                                                                                                    QudtUnit.getUnitBasedOnIfcUnitMeasureLengthBasedOnName(
                                                                                                                    ifcProperty.getMeasure())))
                                                    .collect(Collectors.toList()));
                    break;
                default:
                    System.err.println(logString + ", will be ignored, no matching Feature-Type determined yet for:");
                    entry.getValue().forEach(System.err::println);
                    System.err.println("-------------------------------------------------------------------------");
                    break;
            }
        }
        return extractedFeatures;
    }
}
