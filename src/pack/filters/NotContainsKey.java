/*
 * Decompiled with CFR 0.139.
 */
package pack.filters;

import pack.filters.Filter;

public class NotContainsKey
extends Filter {
    String key;

    public NotContainsKey(String key) {
        this.key = key;
    }

    @Override
    public boolean evaluate(String[] disparo) {
        for (String s : disparo) {
            if (!s.split("=")[0].equals(this.key)) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        return "Does NOT contains key \"" + this.key + "\"";
    }
}

