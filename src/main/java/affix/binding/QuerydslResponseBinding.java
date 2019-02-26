package affix.binding;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CollectionPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class QuerydslResponseBinding extends QuerydslExpandBinding<Path<? extends Object>, Object> {
    @Override
    public Optional<Predicate> bind(Path<?> path, Collection<?> value) {
        Assert.notNull(path, "Path must not be null!");
        Assert.notNull(value, "Value must not be null!");

        if (value.isEmpty()) {
            value = Arrays.asList("1");
            //return Optional.empty();
        }

        if (path instanceof CollectionPathBase) {

            BooleanBuilder builder = new BooleanBuilder();

            for (Object element : value) {
                builder.and(((CollectionPathBase) path).contains(element));
            }

            return Optional.of(builder.getValue());
        }

        if (path instanceof SimpleExpression) {

            if (value.size() > 1) {
                return Optional.of(((SimpleExpression) path).in(value));
            }

            return Optional.of(((SimpleExpression) path).eq(value.iterator().next()));
        }

        throw new IllegalArgumentException(
                String.format("Cannot create predicate for path '%s' with type '%s'.", path, path.getMetadata().getPathType()));
    }
}
