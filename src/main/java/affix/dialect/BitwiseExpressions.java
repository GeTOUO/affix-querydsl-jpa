package affix.dialect;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;

public abstract class BitwiseExpressions {

	public static NumberTemplate<Long> bitAnd(Object arg1, Object arg2) {
		return Expressions.numberTemplate(Long.class, "function('"+ BitSupportMysql5Dialect.BIT_AND +"', {0}, {1})", arg1, arg2);
	}

	public static NumberTemplate<Long> bitOr(Object arg1, Object arg2) {
		return Expressions.numberTemplate(Long.class, "function('"+ BitSupportMysql5Dialect.BIT_OR +"', {0}, {1})", arg1, arg2);
	}

	public static NumberTemplate<Long> bitXor(Object arg1, Object arg2) {
		return Expressions.numberTemplate(Long.class, "function('"+ BitSupportMysql5Dialect.BIT_XOR +"', {0}, {1})", arg1, arg2);
	}

	public static BooleanExpression bitHas(Object arg1, Long arg2) {
		return bitAnd(arg1, arg2).eq(arg2);
	}

	public static BooleanExpression bitNotHas(Object arg1, Long arg2) {
		return bitAnd(arg1, arg2).eq(arg2).not();
	}
}