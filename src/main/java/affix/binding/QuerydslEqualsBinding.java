package affix.binding;

import affix.Prefix;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CollectionPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class QuerydslEqualsBinding extends QuerydslExpandBinding<Path<? extends Object>, Object>  {

    public QuerydslEqualsBinding() {
    }

    public QuerydslEqualsBinding(Prefix prefix) {
        super(prefix);
    }

    public Optional<Predicate> bind(Path<?> path, Collection<? extends Object> value) {
        Assert.notNull(path, "Path must not be null!");
        Assert.notNull(value, "Value must not be null!");
        if (value.isEmpty()) {
            return Optional.empty();
        } else if (!(path instanceof CollectionPathBase)) {
            if (path instanceof SimpleExpression) {

                return value.size() > 1 ? Optional.of(((SimpleExpression)path).in(value)) : Optional.of(((SimpleExpression)path).eq(value.iterator().next()));
            } else {
                throw new IllegalArgumentException(String.format("Cannot create predicate for path '%s' with type '%s'.", path, path.getMetadata().getPathType()));
            }
        } else {
            BooleanBuilder builder = new BooleanBuilder();
            Iterator var4 = value.iterator();

            while(var4.hasNext()) {
                Object element = var4.next();
                builder.and(((CollectionPathBase)path).contains(element));
            }

            return Optional.of(builder.getValue());
        }
    }
}
