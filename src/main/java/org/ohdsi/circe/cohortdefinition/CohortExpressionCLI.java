package org.ohdsi.circe.cohortdefinition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.ohdsi.sql.SqlRender;

public class CohortExpressionCLI {

  public static void main(String[] args) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
    StringBuilder sb = new StringBuilder();
    while (scanner.hasNextLine()) {
      sb.append(scanner.nextLine()).append('\n');
    }
    scanner.close();

    JsonNode input = mapper.readTree(sb.toString());

    CohortExpression expression = mapper.treeToValue(input.get("expression"), CohortExpression.class);

    CohortExpressionQueryBuilder.BuildExpressionQueryOptions options =
        new CohortExpressionQueryBuilder.BuildExpressionQueryOptions();
    options.cohortId = input.get("cohortId").asInt();
    options.cdmSchema = input.get("cdmSchema").asText();
    options.targetTable = input.get("targetTable").asText();
    options.resultSchema = input.get("resultSchema").asText();
    options.vocabularySchema = input.get("vocabularySchema").asText();
    options.generateStats = false;

    String sql = new CohortExpressionQueryBuilder().buildExpressionQuery(expression, options);

    // Evaluate {@cond}?{block} conditionals left in the output
    sql = SqlRender.renderSql(sql, new String[]{}, new String[]{});

    System.out.print(sql);
  }
}
