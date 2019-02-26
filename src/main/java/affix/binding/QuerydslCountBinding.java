package affix.binding;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CollectionPathBase;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Optional;

public class QuerydslCountBinding extends QuerydslExpandBinding<Path<? extends Object>, Object> {
    @Override
    public Optional<Predicate> bind(Path<?> path, Collection<?> value) {

        Assert.notNull(path, "Path must not be null!");
        Assert.notNull(value, "Value must not be null!");

        if (value.isEmpty()) {
            return Optional.empty();
        }

        if (path instanceof CollectionPathBase) {
            final Integer integer = Integer.valueOf(value.iterator().next().toString());
            return Optional.of(((CollectionPathBase) path).size().eq(integer));
        } else {
            return Optional.empty();
        }
    }
}
