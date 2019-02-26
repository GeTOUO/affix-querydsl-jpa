package affix.resolver;

import affix.Prefix;
import affix.Suffix;
import affix.binding.*;
import affix.model.Domain;
import affix.resolver.QuerydslCustomPredicateResolver;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.control.Option;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AffixParseFactory {
    static final Function1<Prefix, QuerydslExpandBinding> defaultBitHas = (prefix) -> new QuerydslBitwiseBinding(prefix, true);
    static final Function1<Prefix, QuerydslExpandBinding> defaultBitNotHas = (prefix) -> new QuerydslBitwiseBinding(prefix, false);

    static final Function1<Prefix, QuerydslExpandBinding> defaultEq = (prefix) -> new QuerydslEqualsBinding(prefix);

    static final Function1<Prefix, QuerydslExpandBinding> defaultLike = (prefix) -> new QuerydslLikeBinding(prefix);
    static final Function1<Prefix, QuerydslExpandBinding> defaultNull = (prefix) -> new QuerydslNullBinding(prefix);
    static final Function1<Prefix, QuerydslExpandBinding> defaultBetween = (prefix) -> new QuerydslBetweenBinding(prefix);


    static final Function3<Class<? extends Domain>, String, MultiValueMap<String, String>,
            Predicate> andPredicateFuc = (clz, suffix, map) ->
            getBinding(suffix).map(bindingFuc -> bindingFuc.apply(Prefix.DEFAULT_AND))
                    .map(QuerydslCustomPredicateResolver::new).map(resolver -> { try {
                return resolver.resolve(clz, map);
            } catch (Exception e) {
                return null;
            }}).getOrElse(() -> null);

    static final Function3<Class<? extends Domain>, String, MultiValueMap<String, String>,
            Predicate> orPredicateFuc = (clz, suffix, map) ->
            getBinding(suffix).map(bindingFuc -> bindingFuc.apply(Prefix.OR))
                    .map(QuerydslCustomPredicateResolver::new).map(resolver ->
                    ExpressionUtils.anyOf(
                            map.entrySet().stream().map(entry -> new LinkedMultiValueMap<String, String>(){{put(entry.getKey(), entry.getValue());}})
                                    .map(singleValueMap -> {
                                        try {
                                            return resolver.resolve(clz, singleValueMap);
                                        } catch (Exception e) {
                                            return null;
                                        }
                                    }).collect(Collectors.toSet()))
            ).getOrElse(() -> null);

    static final Function2<Class<? extends Domain>, Map<String, MultiValueMap<String, String>>,
            Predicate> allAndPredicateFuc = (clz, map) ->
            ExpressionUtils.allOf(map.entrySet().stream().map(entry ->
                    andPredicateFuc.apply(clz, entry.getKey(), entry.getValue()))
                    .collect(Collectors.toSet()));

    static final Function2<Class<? extends Domain>, Map<String, MultiValueMap<String, String>>,
            Predicate> allOrPredicateFuc = (clz, map) ->
            ExpressionUtils.anyOf(map.entrySet().stream().map(entry ->
                    orPredicateFuc.apply(clz, entry.getKey(), entry.getValue()))
                    .collect(Collectors.toSet()));

    private final static Map<Prefix, Function2<Class<? extends Domain>, Map<String, MultiValueMap<String, String>>, Predicate>> prefixExecutor = new ConcurrentHashMap<Prefix, Function2<Class<? extends Domain>, Map<String, MultiValueMap<String, String>>, Predicate>>() {{
        put(Prefix.DEFAULT_AND, allAndPredicateFuc);
        put(Prefix.OR, allOrPredicateFuc);
    }};


    private final static Map<String, Function1<Prefix, QuerydslExpandBinding>> suffixParsers = new ConcurrentHashMap<String, Function1<Prefix, QuerydslExpandBinding>>() {{
        put(Suffix.DEFAULT_EQ.affix, defaultEq);
        put(Suffix.LIKE.affix, defaultLike);
        put(Suffix.NULL.affix, defaultNull);
        put(Suffix.BETWEEN.affix, defaultBetween);
        put(Suffix.BIT_HAS.affix, defaultBitHas);
        put(Suffix.BIT_NOTHAS.affix, defaultBitNotHas);
    }};

    public static Set<String> suffixes(boolean needEquals) {
        return needEquals ? suffixParsers.keySet() : suffixParsers.keySet().stream()
                .filter(key -> !Suffix.DEFAULT_EQ.affix.equalsIgnoreCase(key))
                .collect(Collectors.toSet());
    }
    public static Option<Function2<Class<? extends Domain>, Map<String, MultiValueMap<String, String>>, Predicate>> executorOfPrefix(Prefix prefix) {
        return Option.of(prefixExecutor.get(prefix));
    }

    public static Option<Function1<Prefix, QuerydslExpandBinding>> getBinding(String suffix) {
        return Option.of(suffixParsers.get(suffix));
    }


    public static void bindingExpand(String suffix, Function1<Prefix, QuerydslExpandBinding> customBinding) {
        if (customBinding != null) suffixParsers.put(suffix, customBinding);
        else suffixParsers.remove(suffix);
    }
//    private static final String defaultPrefixWrapper = "[*]";
//    private static final String defaultSuffixWrapper = "(*)";

}
