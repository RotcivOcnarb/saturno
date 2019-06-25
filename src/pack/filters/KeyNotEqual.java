/*
 * Decompiled with CFR 0.139.
 */
package pack.filters;

import pack.filters.Filter;

public class KeyNotEqual
extends Filter {
    String key;
    String value;

    public KeyNotEqual(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean evaluate(String[] disparo) {
        for (String s : disparo) {
            if (!s.split("=")[0].equals(this.key)) continue;
            return !s.split("=")[1].equals(this.value);
        }
        return false;
    }

    public String toString() {
        return String.valueOf(this.key) + " NOT equals \"" + this.value + "\"";
    }
}

