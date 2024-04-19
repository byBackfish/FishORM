package de.bybackfish.sql.util;

public class JointClasses {

    public record JointPair<K, V>(K first, V second) {
    }

    public record JointTriple<K, V, W>(K first, V second, W third) {
    }

    public record JointQuad<K, V, W, X>(K first, V second, W third, X fourth) {
    }

    public record JointQuint<K, V, W, X, Y>(K first, V second, W third, X fourth, Y fifth) {
    }

    public record JointSext<K, V, W, X, Y, Z>(K first, V second, W third, X fourth, Y fifth, Z sixth) {
    }

    public record JointSept<K, V, W, X, Y, Z, A>(K first, V second, W third, X fourth, Y fifth, Z sixth, A seventh) {
    }

    public record JointOct<K, V, W, X, Y, Z, A, B>(K first, V second, W third, X fourth, Y fifth, Z sixth, A seventh,
                                                   B eighth) {
    }

    public record JointNon<K, V, W, X, Y, Z, A, B, C>(K first, V second, W third, X fourth, Y fifth, Z sixth, A seventh,
                                                      B eighth, C ninth) {
    }

}

