package affix.resolver;


import affix.Prefix;
import affix.Suffix;
import affix.model.Domain;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 查询参数解析工具
 *
 * 主要作用是对前端调用时传递的查询字符串转换为符合格式要求的结构
 */
public class QueryParamAccessor {

    public static boolean hasSuffix(String affixPath, String suffix) {
        return Option.of(affixPath).map(notNullPath ->
                StringUtils.endsWithIgnoreCase(notNullPath, suffix))
                .getOrElse(false);
    }

    public static boolean hasPrefix(String affixPath, String prefix) {
        return Option.of(affixPath).map(notNullPath ->
                StringUtils.startsWithIgnoreCase(notNullPath, prefix))
                .getOrElse(false);
    }

    public static String removeSuffix(String affixPath, String suffix) {
        return StringUtils.removeEndIgnoreCase(affixPath, suffix);
    }

    public static String removePrefix(String affixPath, String prefix) {
        return StringUtils.removeStartIgnoreCase(affixPath, prefix);
    }

    public static Tuple2<Prefix, String> findPrefix(String affixPath) {
        if (hasPrefix(affixPath, Prefix.OR.affix)) return new Tuple2<>(Prefix.OR, removePrefix(affixPath, Prefix.OR.affix));
        else return new Tuple2<>(Prefix.DEFAULT_AND, removePrefix(affixPath, Prefix.DEFAULT_AND.affix));
    }

    public static String findOnlySuffix(String affixPath, Set<String> suffixes, boolean parseEquals) {
        return suffixes.stream().filter(suffix -> StringUtils.isNotBlank(suffix) &&
                hasSuffix(affixPath, suffix)).findFirst()
                .orElseGet(() -> parseEquals ? Suffix.DEFAULT_EQ.affix : Suffix.INVALID_VALUE);
    }

    /**
     * 筛选出以指定后缀的kv条件对, 并在去掉指定后缀后返回新的 MultiValueMap
     *
     * @param suffix 指定后缀
     * @param values 值组
     * @return 符合条件的去掉后缀的kv集合
     */
    public static MultiValueMap<String, String> accessorOfSuffix(String suffix, MultiValueMap<String, String> values) {
        final LinkedMultiValueMap<String, String> multiValueMapOfSuffix = new LinkedMultiValueMap<>();
        nullable(values).ifPresent(v -> v.entrySet().stream()
                .filter(e -> hasSuffix(e.getKey(), suffix)).forEach(e ->
                    multiValueMapOfSuffix.put(
                            removeSuffix(e.getKey(), suffix),
                            e.getValue())));
        return multiValueMapOfSuffix;
    }

    /**
     * 筛选出以指定前缀的kv条件对, 并在去掉指定前缀后返回新的 MultiValueMap
     *
     * @param prefix 指定前缀
     * @param values 值组
     * @return 符合条件的去掉前缀的kv集合
     */
    public static MultiValueMap<String, String> accessorOfPrefix(String prefix, MultiValueMap<String, String> values) {
        final LinkedMultiValueMap<String, String> multiValueMapOfPrefix = new LinkedMultiValueMap<>();
        nullable(values).ifPresent(v -> v.entrySet().stream()
                .filter(e -> hasPrefix(e.getKey(), prefix)).forEach(e ->
                    multiValueMapOfPrefix.put(
                            removePrefix(e.getKey(), prefix),
                            e.getValue())));
        return multiValueMapOfPrefix;
    }

    /**
     * 按条件的连接方式分组, And一组, Or 一组
     * @param values
     * @return
     */
    public static Map<Prefix, MultiValueMap<String, String>> groupByPrefix(MultiValueMap<String, String> values) {
        final Map<Prefix, MultiValueMap<String, String>> collect = new HashMap<>();

        values.entrySet().stream().collect(Collectors
                .groupingBy(entry -> findPrefix(entry.getKey())._1))
                .forEach((key, valueList) -> valueList.forEach(valueOf ->
                        collect.computeIfAbsent(key, createMapping -> new LinkedMultiValueMap<>())
                                .addAll(removePrefix(valueOf.getKey(), key.affix) , valueOf.getValue())));
        return collect;
    }

    /**
     * 按查询类型分组
     * @param values
     * @return
     */
    public static Map<String, MultiValueMap<String, String>> groupBySuffix(MultiValueMap<String, String> values, boolean parseEquals) {
        final Map<String, MultiValueMap<String, String>> collect = new HashMap<>();

        values.entrySet().stream().collect(Collectors
                .groupingBy(entry -> findOnlySuffix(entry.getKey(), AffixParseFactory.suffixes(parseEquals), parseEquals)))
                .forEach(((key, valueList) -> valueList.forEach(valueOf ->
                        collect.computeIfAbsent(key, createMapping -> new LinkedMultiValueMap<>())
                                .addAll(removeSuffix(valueOf.getKey(), key) , valueOf.getValue()))));
        return collect;
    }

    public static Predicate resolverPredicate(Class<? extends Domain> root, Map<Prefix, MultiValueMap<String, String>> params, boolean parseEquals) {
        final Map<Prefix, Map<String, MultiValueMap<String, String>>> prefixMapMap = params.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, byPrefix -> groupBySuffix(byPrefix.getValue(), parseEquals)));

        return ExpressionUtils.allOf(prefixMapMap.entrySet().stream().map(entry ->
                AffixParseFactory.executorOfPrefix(entry.getKey()).map(executor ->
                        executor.apply(root, entry.getValue()))
                        .getOrElse(()-> null)).collect(Collectors.toSet()));

    }

    public static Predicate resolverPredicate(Class<? extends Domain> root, MultiValueMap<String, String> params) {
        return resolverPredicate(root, groupByPrefix(params), true);
    }

    public static Predicate resolverPredicate(Class<? extends Domain> root, MultiValueMap<String, String> params, boolean parseEquals) {
        return resolverPredicate(root, groupByPrefix(params), parseEquals);
    }

    /**
     * 对象包装为Optional
     *
     * @param element
     * @param <T>
     * @return
     */
    private static <T> Optional<T> nullable(T element) {
        return Optional.ofNullable(element);
    }
}

