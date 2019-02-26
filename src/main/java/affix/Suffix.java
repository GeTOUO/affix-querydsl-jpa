package affix;

import java.util.Arrays;
import java.util.Optional;

public enum Suffix {

        DEFAULT_EQ("(EQ)"),

        LIKE("(LIKE)"),
        NULL("(NULL)"),
        BETWEEN("(BETWEEN)"),
        BIT_HAS("(BIT.HAS)"),
        BIT_NOTHAS("(BIT.NOTHAS)"),
        ;
        public static final String INVALID_VALUE = "\\\\INVALID_VALUE";
        public final String affix;

        Suffix(String type) {
            this.affix = type;
        }

        public static Optional<Suffix> byTypeFirst(String type) {
            return Arrays.asList(values()).stream().filter(t -> t.affix.equalsIgnoreCase(type)).findFirst();
        }
    }