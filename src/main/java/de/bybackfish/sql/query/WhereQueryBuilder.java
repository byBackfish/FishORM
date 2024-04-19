package de.bybackfish.sql.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WhereQueryBuilder {
    private final List<Condition> conditions;
    private final List<WhereQueryBuilder> nestedBuilders;
    private String joinOperator;

    public WhereQueryBuilder() {
        conditions = new ArrayList<>();
        nestedBuilders = new ArrayList<>();
    }

    public WhereQueryBuilder and(String condition, Object... params) {
        conditions.add(new Condition("AND", condition, params));
        return this;
    }

    public WhereQueryBuilder or(String condition, Object... params) {
        conditions.add(new Condition("OR", condition, params));
        return this;
    }

    public WhereQueryBuilder nested(WhereQueryBuilder nestedBuilder) {
        nestedBuilders.add(nestedBuilder);
        return this;
    }

    public WhereQueryBuilder and() {
        joinOperator = "AND";
        return this;
    }

    public WhereQueryBuilder or() {
        joinOperator = "OR";
        return this;
    }

    public AbstractQueryBuilder.QueryNode buildNode() {
        StringBuilder whereClause = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        // Append conditions
        if (!conditions.isEmpty()) {
            for (int i = 0; i < conditions.size(); i++) {
                Condition condition = conditions.get(i);
                if (i > 0) {
                    whereClause.append(" ").append(condition.joinOperator).append(" ");
                }
                whereClause.append("(").append(condition.condition).append(")");
                if (condition.params != null) {
                    parameters.addAll(Arrays.asList(condition.params));
                }
            }
        }

        // Append nested builders
        if (!nestedBuilders.isEmpty()) {
            if (!conditions.isEmpty()) {
                whereClause.append(" ").append(joinOperator).append(" ");
            }
            for (int i = 0; i < nestedBuilders.size(); i++) {
                WhereQueryBuilder nestedBuilder = nestedBuilders.get(i);
                if (i > 0) {
                    whereClause.append(" ").append(joinOperator).append(" ");
                }
                whereClause.append("(").append(nestedBuilder.buildNode().sql()).append(")");
                parameters.addAll(Arrays.asList(nestedBuilder.buildNode().params()));
            }
        }

        return new AbstractQueryBuilder.QueryNode(STR."WHERE \{whereClause.toString()}", 1, parameters.toArray());
    }

    private static class Condition {
        String joinOperator;
        String condition;
        Object[] params;

        public Condition(String joinOperator, String condition, Object[] params) {
            this.joinOperator = joinOperator;
            this.condition = condition;
            this.params = params;
        }
    }
}
