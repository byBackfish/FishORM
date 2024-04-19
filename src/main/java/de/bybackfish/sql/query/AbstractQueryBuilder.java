package de.bybackfish.sql.query;

import de.bybackfish.sql.core.DatabaseModel;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.core.FishSQLException;
import de.bybackfish.sql.util.JointClasses;
import de.bybackfish.sql.util.ObjectMapper;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AbstractQueryBuilder {
    protected List<QueryNode> nodes;

    public AbstractQueryBuilder() {
        nodes = new ArrayList<>();
    }

    protected AbstractQueryBuilder sql(String condition, int priority, Object... params) {
        nodes.add(new QueryNode(condition, priority, params));
        return this;
    }

    public AbstractQueryBuilder where(WhereQueryBuilder whereQueryBuilder) {
        nodes.add(whereQueryBuilder.buildNode());
        return this;
    }

    public AbstractQueryBuilder where(Function<WhereQueryBuilder, WhereQueryBuilder> whereQueryBuilderFunction) {
        return where(whereQueryBuilderFunction.apply(new WhereQueryBuilder()));
    }

    public AbstractQueryBuilder orderBy(String column, OrderDirection direction) {
        nodes.add(new QueryNode(STR."ORDER BY \{column} \{direction.name()}", Integer.MIN_VALUE + 1));
        return this;
    }

    public AbstractQueryBuilder orderBy(String column) {
        return orderBy(column, OrderDirection.ASC);
    }

    public AbstractQueryBuilder limit(int limit) {
        nodes.add(new QueryNode(STR."LIMIT \{limit}", Integer.MIN_VALUE));
        return this;
    }

    public BuiltQuery build(FishDatabase fishDatabase) throws FishSQLException {
        List<QueryNode> sorted = nodes.stream().sorted(Comparator.comparingInt(QueryNode::priority).reversed()).toList();
        String buildSql = sorted.stream().map(QueryNode::sql).collect(Collectors.joining(" "));
        Object[] params = sorted.stream().map(QueryNode::params).flatMap(Arrays::stream).toArray();

        try {
            return new BuiltQuery(fishDatabase.prepareStatement(buildSql, params));
        } catch (SQLException e) {
            throw new FishSQLException(e);
        }
    }

    public enum OrderDirection {
        ASC,
        DESC
    }

    public record QueryNode(
            String sql,
            int priority,
            Object... params
    ) {
    }

    public record BuiltQuery(PreparedStatement statement) {
        public ResultSet execute() throws FishSQLException {
            try {
                return statement.executeQuery();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
        }

        public void executeUpdate() throws FishSQLException {
            try {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
        }

        public <T extends DatabaseModel> List<T> unwrap(Class<T> clazz) throws FishSQLException {
            ObjectMapper mapper = new ObjectMapper(clazz);
            return mapper.map(execute());
        }

        public <T extends DatabaseModel, U extends DatabaseModel> List<JointClasses.JointPair<T, U>> unwrap(Class<T> clazz1, Class<U> clazz2) throws FishSQLException {
            ObjectMapper first = new ObjectMapper(clazz1);
            ObjectMapper second = new ObjectMapper(clazz2);

            ResultSet resultSet = execute();

            List<T> firstTable = first.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<U> secondTable = second.map(resultSet);

            if (firstTable.size() != secondTable.size()) {
                throw new RuntimeException(STR."Length of first and second table are not the same: \{firstTable.size()} and \{secondTable.size()}");
            }

            List<JointClasses.JointPair<T, U>> jointPairs = new ArrayList<>();
            for (int i = 0; i < firstTable.size(); i++) {
                jointPairs.add(new JointClasses.JointPair<>(firstTable.get(i), secondTable.get(i)));
            }

            return jointPairs;
        }

        public <T extends DatabaseModel, U extends DatabaseModel, V extends DatabaseModel> List<JointClasses.JointTriple<T, U, V>> unwrap(Class<T> clazz1, Class<U> clazz2, Class<V> clazz3) throws FishSQLException {
            ObjectMapper first = new ObjectMapper(clazz1);
            ObjectMapper second = new ObjectMapper(clazz2);
            ObjectMapper third = new ObjectMapper(clazz3);

            ResultSet resultSet = execute();

            List<T> firstTable = first.map(resultSet);
                try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<U> secondTable = second.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<V> thirdTable = third.map(resultSet);

            if (firstTable.size() != secondTable.size()) {
                throw new RuntimeException(STR."Length of first and second table are not the same: \{firstTable.size()} and \{secondTable.size()}");
            }
            if (firstTable.size() != thirdTable.size()) {
                throw new RuntimeException(STR."Length of first and third table are not the same: \{firstTable.size()} and \{thirdTable.size()}");
            }

            List<JointClasses.JointTriple<T, U, V>> jointTriples = new ArrayList<>();
            for (int i = 0; i < firstTable.size(); i++) {
                jointTriples.add(new JointClasses.JointTriple<>(firstTable.get(i), secondTable.get(i), thirdTable.get(i)));
            }

            return jointTriples;
        }

        public <T extends DatabaseModel, U extends DatabaseModel, V extends DatabaseModel, W extends DatabaseModel> List<JointClasses.JointQuad<T, U, V, W>> unwrap(Class<T> clazz1, Class<U> clazz2, Class<V> clazz3, Class<W> clazz4) throws SQLException {
            ObjectMapper first = new ObjectMapper(clazz1);
            ObjectMapper second = new ObjectMapper(clazz2);
            ObjectMapper third = new ObjectMapper(clazz3);
            ObjectMapper fourth = new ObjectMapper(clazz4);

            ResultSet resultSet = execute();

            List<T> firstTable = first.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<U> secondTable = second.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<V> thirdTable = third.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<W> fourthTable = fourth.map(resultSet);

            if (firstTable.size() != secondTable.size()) {
                throw new RuntimeException(STR."Length of first and second table are not the same: \{firstTable.size()} and \{secondTable.size()}");
            }
            if (firstTable.size() != thirdTable.size()) {
                throw new RuntimeException(STR."Length of first and third table are not the same: \{firstTable.size()} and \{thirdTable.size()}");
            }
            if (firstTable.size() != fourthTable.size()) {
                throw new RuntimeException(STR."Length of first and fourth table are not the same: \{firstTable.size()} and \{fourthTable.size()}");
            }

            List<JointClasses.JointQuad<T, U, V, W>> jointQuads = new ArrayList<>();
            for (int i = 0; i < firstTable.size(); i++) {
                jointQuads.add(new JointClasses.JointQuad<>(firstTable.get(i), secondTable.get(i), thirdTable.get(i), fourthTable.get(i)));
            }

            return jointQuads;
        }

        public <T extends DatabaseModel, U extends DatabaseModel, V extends DatabaseModel, W extends DatabaseModel, X extends DatabaseModel> List<JointClasses.JointQuint<T, U, V, W, X>> unwrap(Class<T> clazz1, Class<U> clazz2, Class<V> clazz3, Class<W> clazz4, Class<X> clazz5) throws SQLException {
            ObjectMapper first = new ObjectMapper(clazz1);
            ObjectMapper second = new ObjectMapper(clazz2);
            ObjectMapper third = new ObjectMapper(clazz3);
            ObjectMapper fourth = new ObjectMapper(clazz4);
            ObjectMapper fifth = new ObjectMapper(clazz5);

            ResultSet resultSet = execute();

            List<T> firstTable = first.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<U> secondTable = second.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<V> thirdTable = third.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<W> fourthTable = fourth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<X> fifthTable = fifth.map(resultSet);

            if (firstTable.size() != secondTable.size()) {
                throw new RuntimeException(STR."Length of first and second table are not the same: \{firstTable.size()} and \{secondTable.size()}");
            }
            if (firstTable.size() != thirdTable.size()) {
                throw new RuntimeException(STR."Length of first and third table are not the same: \{firstTable.size()} and \{thirdTable.size()}");
            }
            if (firstTable.size() != fourthTable.size()) {
                throw new RuntimeException(STR."Length of first and fourth table are not the same: \{firstTable.size()} and \{fourthTable.size()}");
            }
            if (firstTable.size() != fifthTable.size()) {
                throw new RuntimeException(STR."Length of first and fifth table are not the same: \{firstTable.size()} and \{fifthTable.size()}");
            }

            List<JointClasses.JointQuint<T, U, V, W, X>> jointQuints = new ArrayList<>();
            for (int i = 0; i < firstTable.size(); i++) {
                jointQuints.add(new JointClasses.JointQuint<>(firstTable.get(i), secondTable.get(i), thirdTable.get(i), fourthTable.get(i), fifthTable.get(i)));
            }

            return jointQuints;
        }

        public <T extends DatabaseModel, U extends DatabaseModel, V extends DatabaseModel, W extends DatabaseModel, X extends DatabaseModel, Y extends DatabaseModel> List<JointClasses.JointSext<T,
                U, V, W, X, Y>> unwrap(Class<T> clazz1, Class<U> clazz2, Class<V> clazz3, Class<W> clazz4, Class<X> clazz5, Class<Y> clazz6) throws SQLException {
            ObjectMapper first = new ObjectMapper(clazz1);
            ObjectMapper second = new ObjectMapper(clazz2);
            ObjectMapper third = new ObjectMapper(clazz3);
            ObjectMapper fourth = new ObjectMapper(clazz4);
            ObjectMapper fifth = new ObjectMapper(clazz5);
            ObjectMapper sixth = new ObjectMapper(clazz6);

            ResultSet resultSet = execute();

            List<T> firstTable = first.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<U> secondTable = second.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<V> thirdTable = third.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<W> fourthTable = fourth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<X> fifthTable = fifth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<Y> sixthTable = sixth.map(resultSet);

            if (firstTable.size() != secondTable.size()) {
                throw new RuntimeException(STR."Length of first and second table are not the same: \{firstTable.size()} and \{secondTable.size()}");
            }
            if (firstTable.size() != thirdTable.size()) {
                throw new RuntimeException(STR."Length of first and third table are not the same: \{firstTable.size()} and \{thirdTable.size()}");
            }
            if (firstTable.size() != fourthTable.size()) {
                throw new RuntimeException(STR."Length of first and fourth table are not the same: \{firstTable.size()} and \{fourthTable.size()}");
            }
            if (firstTable.size() != fifthTable.size()) {
                throw new RuntimeException(STR."Length of first and fifth table are not the same: \{firstTable.size()} and \{fifthTable.size()}");
            }
            if (firstTable.size() != sixthTable.size()) {
                throw new RuntimeException(STR."Length of first and sixth table are not the same: \{firstTable.size()} and \{sixthTable.size()}");
            }

            List<JointClasses.JointSext<T, U, V, W, X, Y>> jointSexts = new ArrayList<>();
            for (int i = 0; i < firstTable.size(); i++) {
                jointSexts.add(new JointClasses.JointSext<>(firstTable.get(i), secondTable.get(i), thirdTable.get(i), fourthTable.get(i), fifthTable.get(i), sixthTable.get(i)));
            }

            return jointSexts;
        }

        public <T extends DatabaseModel, U extends DatabaseModel, V extends DatabaseModel, W extends DatabaseModel, X extends DatabaseModel, Y extends DatabaseModel, Z extends DatabaseModel> List<JointClasses.JointSept<T, U, V, W, X, Y, Z>> unwrap(Class<T> clazz1, Class<U> clazz2, Class<V> clazz3, Class<W> clazz4, Class<X> clazz5, Class<Y> clazz6, Class<Z> clazz7) throws SQLException {
            ObjectMapper first = new ObjectMapper(clazz1);
            ObjectMapper second = new ObjectMapper(clazz2);
            ObjectMapper third = new ObjectMapper(clazz3);
            ObjectMapper fourth = new ObjectMapper(clazz4);
            ObjectMapper fifth = new ObjectMapper(clazz5);
            ObjectMapper sixth = new ObjectMapper(clazz6);
            ObjectMapper seventh = new ObjectMapper(clazz7);

            ResultSet resultSet = execute();

            List<T> firstTable = first.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<U> secondTable = second.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<V> thirdTable = third.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<W> fourthTable = fourth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<X> fifthTable = fifth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<Y> sixthTable = sixth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<Z> seventhTable = seventh.map(resultSet);

            if (firstTable.size() != secondTable.size()) {
                throw new RuntimeException("Length of first and second table are not the same: " + firstTable.size() + " and " + secondTable.size());
            }
            if (firstTable.size() != thirdTable.size()) {
                throw new RuntimeException("Length of first and third table are not the same: " + firstTable.size() + " and " + thirdTable.size());
            }
            if (firstTable.size() != fourthTable.size()) {
                throw new RuntimeException("Length of first and fourth table are not the same: " + firstTable.size() + " and " + fourthTable.size());
            }
            if (firstTable.size() != fifthTable.size()) {
                throw new RuntimeException("Length of first and fifth table are not the same: " + firstTable.size() + " and " + fifthTable.size());
            }
            if (firstTable.size() != sixthTable.size()) {
                throw new RuntimeException("Length of first and sixth table are not the same: " + firstTable.size() + " and " + sixthTable.size());
            }
            if (firstTable.size() != seventhTable.size()) {
                throw new RuntimeException("Length of first and seventh table are not the same: " + firstTable.size() + " and " + seventhTable.size());
            }

            List<JointClasses.JointSept<T, U, V, W, X, Y, Z>> jointSepts = new ArrayList<>();
            for (int i = 0; i < firstTable.size(); i++) {
                jointSepts.add(new JointClasses.JointSept<>(firstTable.get(i), secondTable.get(i), thirdTable.get(i), fourthTable.get(i), fifthTable.get(i), sixthTable.get(i), seventhTable.get(i)));
            }

            return jointSepts;
        }

        public <T extends DatabaseModel, U extends DatabaseModel, V extends DatabaseModel, W extends DatabaseModel, X extends DatabaseModel, Y extends DatabaseModel, Z extends DatabaseModel, A extends DatabaseModel> List<JointClasses.JointOct<T,
                U, V, W, X, Y, Z, A>> unwrap(Class<T> clazz1, Class<U> clazz2, Class<V> clazz3, Class<W> clazz4, Class<X> clazz5, Class<Y> clazz6, Class<Z> clazz7, Class<A> clazz8) throws SQLException {
            ObjectMapper first = new ObjectMapper(clazz1);
            ObjectMapper second = new ObjectMapper(clazz2);
            ObjectMapper third = new ObjectMapper(clazz3);
            ObjectMapper fourth = new ObjectMapper(clazz4);
            ObjectMapper fifth = new ObjectMapper(clazz5);
            ObjectMapper sixth = new ObjectMapper(clazz6);
            ObjectMapper seventh = new ObjectMapper(clazz7);
            ObjectMapper eighth = new ObjectMapper(clazz8);

            ResultSet resultSet = execute();

            List<T> firstTable = first.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<U> secondTable = second.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<V> thirdTable = third.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<W> fourthTable = fourth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<X> fifthTable = fifth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<Y> sixthTable = sixth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<Z> seventhTable = seventh.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<A> eighthTable = eighth.map(resultSet);

            if (firstTable.size() != secondTable.size()) {
                throw new RuntimeException(STR."Length of first and second table are not the same: \{firstTable.size()} and \{secondTable.size()}");
            }
            if (firstTable.size() != thirdTable.size()) {
                throw new RuntimeException(STR."Length of first and third table are not the same: \{firstTable.size()} and \{thirdTable.size()}");
            }
            if (firstTable.size() != fourthTable.size()) {
                throw new RuntimeException(STR."Length of first and fourth table are not the same: \{firstTable.size()} and \{fourthTable.size()}");
            }
            if (firstTable.size() != fifthTable.size()) {
                throw new RuntimeException(STR."Length of first and fifth table are not the same: \{firstTable.size()} and \{fifthTable.size()}");
            }
            if (firstTable.size() != sixthTable.size()) {
                throw new RuntimeException(STR."Length of first and sixth table are not the same: \{firstTable.size()} and \{sixthTable.size()}");
            }
            if (firstTable.size() != seventhTable.size()) {
                throw new RuntimeException(STR."Length of first and seventh table are not the same: \{firstTable.size()} and \{seventhTable.size()}");
            }
            if (firstTable.size() != eighthTable.size()) {
                throw new RuntimeException(STR."Length of first and eighth table are not the same: \{firstTable.size()} and \{eighthTable.size()}");
            }

            List<JointClasses.JointOct<T, U, V, W, X, Y, Z, A>> jointOcts = new ArrayList<>();
            for (int i = 0; i < firstTable.size(); i++) {
                jointOcts.add(new JointClasses.JointOct<>(firstTable.get(i), secondTable.get(i), thirdTable.get(i), fourthTable.get(i), fifthTable.get(i), sixthTable.get(i), seventhTable.get(i), eighthTable.get(i)));
            }

            return jointOcts;
        }

        public <T extends DatabaseModel, U extends DatabaseModel, V extends DatabaseModel, W extends DatabaseModel, X extends DatabaseModel, Y extends DatabaseModel, Z extends DatabaseModel, A extends DatabaseModel, B extends DatabaseModel> List<JointClasses.JointNon<T,
                U, V, W, X, Y, Z, A, B>> unwrap(Class<T> clazz1, Class<U> clazz2, Class<V> clazz3, Class<W> clazz4, Class<X> clazz5, Class<Y> clazz6, Class<Z> clazz7, Class<A> clazz8, Class<B> clazz9) throws SQLException {
            ObjectMapper first = new ObjectMapper(clazz1);
            ObjectMapper second = new ObjectMapper(clazz2);
            ObjectMapper third = new ObjectMapper(clazz3);
            ObjectMapper fourth = new ObjectMapper(clazz4);
            ObjectMapper fifth = new ObjectMapper(clazz5);
            ObjectMapper sixth = new ObjectMapper(clazz6);
            ObjectMapper seventh = new ObjectMapper(clazz7);
            ObjectMapper eighth = new ObjectMapper(clazz8);
            ObjectMapper ninth = new ObjectMapper(clazz9);

            ResultSet resultSet = execute();

            List<T> firstTable = first.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<U> secondTable = second.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<V> thirdTable = third.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<W> fourthTable = fourth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<X> fifthTable = fifth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<Y> sixthTable = sixth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<Z> seventhTable = seventh.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<A> eighthTable = eighth.map(resultSet);
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new FishSQLException(e);
            }
            List<B> ninthTable = ninth.map(resultSet);

            if (firstTable.size() != secondTable.size()) {
                throw new RuntimeException(STR."Length of first and second table are not the same: \{firstTable.size()} and \{secondTable.size()}");
            }
            if (firstTable.size() != thirdTable.size()) {
                throw new RuntimeException(STR."Length of first and third table are not the same: \{firstTable.size()} and \{thirdTable.size()}");
            }
            if (firstTable.size() != fourthTable.size()) {
                throw new RuntimeException(STR."Length of first and fourth table are not the same: \{firstTable.size()} and \{fourthTable.size()}");
            }
            if (firstTable.size() != fifthTable.size()) {
                throw new RuntimeException(STR."Length of first and fifth table are not the same: \{firstTable.size()} and \{fifthTable.size()}");
            }
            if (firstTable.size() != sixthTable.size()) {
                throw new RuntimeException(STR."Length of first and sixth table are not the same: \{firstTable.size()} and \{sixthTable.size()}");
            }
            if (firstTable.size() != seventhTable.size()) {
                throw new RuntimeException(STR."Length of first and seventh table are not the same: \{firstTable.size()} and \{seventhTable.size()}");
            }
            if (firstTable.size() != eighthTable.size()) {
                throw new RuntimeException(STR."Length of first and eighth table are not the same: \{firstTable.size()} and \{eighthTable.size()}");
            }
            if (firstTable.size() != ninthTable.size()) {
                throw new RuntimeException(STR."Length of first and ninth table are not the same: \{firstTable.size()} and \{ninthTable.size()}");
            }

            List<JointClasses.JointNon<T, U, V, W, X, Y, Z, A, B>> jointNons = new ArrayList<>();
            for (int i = 0; i < firstTable.size(); i++) {
                jointNons.add(new JointClasses.JointNon<>(firstTable.get(i), secondTable.get(i), thirdTable.get(i), fourthTable.get(i), fifthTable.get(i), sixthTable.get(i), seventhTable.get(i), eighthTable.get(i), ninthTable.get(i)));
            }

            return jointNons;
        }
    }
}
