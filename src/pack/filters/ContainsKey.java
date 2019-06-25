/*
 * Decompiled with CFR 0.139.
 */
package pack.filters;

import pack.filters.Filter;

public class ContainsKey
extends Filter {
    String key;

    public ContainsKey(String key) {
        this.key = key;
    }

    @Override
    public boolean evaluate(String[] disparo) {
        for (String s : disparo) {
            if (!s.split("=")[0].equals(this.key)) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return "Contains \"" + this.key + "\"";
    }
}

