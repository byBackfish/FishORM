package de.bybackfish.sql.util;

public class JointPair<K, V> {
    public K key;
    public V value;

    public JointPair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
