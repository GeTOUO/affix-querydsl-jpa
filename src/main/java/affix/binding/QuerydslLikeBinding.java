package affix.binding;

import affix.Prefix;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CollectionPathBase;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuerydslLikeBinding extends QuerydslExpandBinding<Path<? extends Object>, Object> {
    public QuerydslLikeBinding() {
    }

    public QuerydslLikeBinding(Prefix prefix) {
        super(prefix);
    }
    //    public QuerydslLikeBinding() {
//        this.predicateConnect = false;
//    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.web.querydsl.QueryDslPredicateBuilder#buildPredicate(org.springframework.data.mapping.PropertyPath, java.lang.Object)
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Optional<Predicate> bind(Path<?> path, Collection<? extends Object> value) {

        Assert.notNull(path, "Path must not be null!");
        Assert.notNull(value, "Value must not be null!");

        if (value.isEmpty()) {
            return Optional.empty();
        }

        if (path instanceof CollectionPathBase) {
            BooleanBuilder builder = new BooleanBuilder();

            for (Object element : value) {
                if (isAnd()) builder.and(((CollectionPathBase) path).contains(element));
                else builder.or(((CollectionPathBase) path).contains(element));
            }

            return Optional.of(builder.getValue());
        }

        final List<String> stringValues = value.stream().map(v -> v.toString()).collect(Collectors.toList());
        if (!(path instanceof StringExpression)) {

            return Optional.empty();
        }

        try {

            if (stringValues.size() > 1) {
                BooleanBuilder builder = new BooleanBuilder();
                for (String str: stringValues) {
                    if (isAnd()) builder.and(((StringExpression) path).contains(str).or(((StringExpression) path).like(str)));
                    else builder.or(((StringExpression) path).contains(str).or(((StringExpression) path).like(str)));
                }
                return Optional.of(builder.getValue());
//                return Optional.of(((StringExpression) path).in(stringValues));
            }
            final String next = stringValues.iterator().next();
            return Optional.of(((StringExpression) path).contains(next).or(((StringExpression) path).like(next)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException(
                String.format("Cannot create predicate for path '%s' with type '%s'.", path, path.getMetadata().getPathType()));
    }
}
