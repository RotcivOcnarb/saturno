/*
 * Decompiled with CFR 0.139.
 */
package pack;

import java.util.HashMap;

public class HitNameInfo {
    String raw_name;
    public static HashMap<String, String> param_names;
    public static HashMap<String, String> compose_names;
    public static HashMap<String, String> enhanced_names;
    String fullName;
    boolean enhanced_ecommerce;
    String type_of_enhance = "";
    String value;
    int[] ids = new int[0];

    public HitNameInfo(String raw_name) {
        this.raw_name = raw_name;
        if (raw_name.endsWith("=")) {
            this.value = "";
        } else {
            try {
                this.value = raw_name.split("=")[1];
            }
            catch (Exception e) {
                this.fullName = "erro";
                return;
            }
        }
        this.fullName = this.calculate();
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getRawName() {
        return this.raw_name;
    }

    public String getCode() {
        return this.raw_name.split("=")[0];
    }

    public int[] getIds() {
        return this.ids;
    }

    public boolean isEnhancedEcommerce() {
        return this.enhanced_ecommerce;
    }

    public String getTypeOfEnhance() {
        return this.type_of_enhance;
    }

    public String toString() {
        String texto = "(" + this.getCode() + ")" + "\n\t" + this.fullName + "\n\t" + "Enhanced Ecommerce: " + this.enhanced_ecommerce;
        for (int i : this.ids) {
            texto = String.valueOf(texto) + "\n\t" + i;
        }
        return texto;
    }

    public String calculate() {
        String codename = this.getCode();
        if (param_names.get(codename) == null) {
            if (!codename.split("\\d+")[0].equals(codename)) {
                String result = "";
                String[] ltr = codename.split("\\d+");
                String[] nms = this.removeEmpties(codename.split("[a-z]+"));
                this.ids = new int[nms.length];
                for (int i = 0; i < nms.length; ++i) {
                    this.ids[i] = Integer.parseInt(nms[i]);
                }
                int nums = 0;
                this.enhanced_ecommerce = false;
                for (String s : ltr) {
                    if (nums < nms.length) {
                        if (nums == 0 && enhanced_names.containsKey(s)) {
                            result = String.valueOf(result) + enhanced_names.get(s) + " " + nms[nums] + " ";
                            this.type_of_enhance = enhanced_names.get(s);
                            this.enhanced_ecommerce = true;
                        } else {
                            result = String.valueOf(result) + compose_names.get(s) + " " + nms[nums] + " ";
                        }
                    } else {
                        result = String.valueOf(result) + compose_names.get(s);
                    }
                    ++nums;
                }
                return result;
            }
            return codename;
        }
        return "(" + codename + ") " + param_names.get(codename);
    }

    public String[] removeEmpties(String[] arr) {
        int cont = 0;
        for (String s : arr) {
            if (s.equals("")) continue;
            ++cont;
        }
        String[] result = new String[cont];
        int i = 0;
        for (String s : arr) {
            if (s.equals("")) continue;
            result[i] = s;
            ++i;
        }
        return result;
    }

    public String getValue() {
        return this.value;
    }
}

