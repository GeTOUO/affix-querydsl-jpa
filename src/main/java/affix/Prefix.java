package affix;

import java.util.Arrays;
import java.util.Optional;

public enum Prefix {

        DEFAULT_AND("[AND]"),

        OR("[OR]"),
        ;
        public final String affix;

        Prefix(String type) {
            this.affix = type;
        }

        public static Optional<Prefix> byLogicFirst(String type) {
            return Arrays.asList(values()).stream().filter(t -> t.affix.equalsIgnoreCase(type)).findFirst();
        }
    }