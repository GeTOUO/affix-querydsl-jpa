package affix.dialect;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.LongType;

public class BitSupportMysql5Dialect extends MySQL5Dialect {
    public static final String BIT_AND      = "bit_and";
    public static final String BIT_OR      = "bit_or";
    public static final String BIT_XOR      = "bit_xor";

    public static final String BIT_HAS      = "bit_has";
    public static final String BIT_NOTHAS      = "bit_not_has";
    public BitSupportMysql5Dialect() {
        super();
        registerFunction(BIT_AND, new SQLFunctionTemplate(LongType.INSTANCE, "(?1 & ?2)"));
        registerFunction(BIT_OR, new SQLFunctionTemplate(LongType.INSTANCE, "(?1 | ?2)"));
        registerFunction(BIT_XOR, new SQLFunctionTemplate(LongType.INSTANCE, "(?1 ^ ?2)"));

//        registerFunction(BIT_HAS, new SQLFunctionTemplate(BooleanType.INSTANCE, "(?1 & ?2) = ?2"));
    }

}
