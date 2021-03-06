package at.researchstudio.sat.merkmalserviceifc2json.model.ifc;

import at.researchstudio.sat.merkmalserviceifc2json.model.ifc.vocab.IfcPropertyType;
import at.researchstudio.sat.merkmalserviceifc2json.model.ifc.vocab.IfcUnitMeasure;
import at.researchstudio.sat.merkmalserviceifc2json.model.ifc.vocab.IfcUnitType;
import at.researchstudio.sat.merkmalserviceifc2json.utils.Utils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IfcProperty {
  private final String name;
  private final IfcPropertyType type;

  private IfcUnitMeasure measure;

  public IfcProperty(QuerySolution qs, Map<IfcUnitType, List<IfcUnit>> projectUnits) {
    this(qs.getLiteral("propName"), qs.getResource("propType"));

    if (this.type.isMeasureType()) {
      // TODO: update query and add optional propMeasure to add the ifc unit to the query output
      // (not yet possible since our ifc-files do not have a specific unit attached to the
      // properties)
      Resource unitMeasure = qs.getResource("propMeasure");

      IfcUnitMeasure tempMeasure = IfcUnitMeasure.UNKNOWN;
      if (Objects.nonNull(unitMeasure)) {
        try {
          tempMeasure = IfcUnitMeasure.fromResource(unitMeasure);
        } catch (IllegalArgumentException e) {
          System.err.println(e.getMessage());
        }
        this.measure = tempMeasure;
      } else if (Objects.nonNull(projectUnits)) {
        IfcUnitType tempUnitType = this.type.getUnitType();
        List<IfcUnit> units = projectUnits.get(tempUnitType);

        if (Objects.nonNull(units)) {
          if (units.size() == 1) {
            IfcUnit ifcUnit = units.get(0);
            this.measure = ifcUnit.getMeasure();
          } else {
            System.out.println("More than one unit present, leaving it empty");
            units.forEach(System.out::println);
          }
        }
      }
    }
  }

  private IfcProperty(Literal name, Resource type) {
    this.name = Utils.convertIFCStringToUtf8(name.toString());

    IfcPropertyType tempType = IfcPropertyType.UNKNOWN;
    try {
      tempType = IfcPropertyType.fromResource(type);
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
    }

    this.type = tempType;
  }

  public String getName() {
    return name;
  }

  public IfcPropertyType getType() {
    return type;
  }

  public IfcUnitMeasure getMeasure() {
    return measure;
  }

  @Override
  public String toString() {
    return "IfcProperty{"
        + "name='"
        + name
        + '\''
        + ", type="
        + type
        + ", measure="
        + measure
        + '}';
  }
}
