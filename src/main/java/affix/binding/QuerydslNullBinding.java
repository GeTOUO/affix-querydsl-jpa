package affix.binding;

import affix.Prefix;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CollectionPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Optional;

public class QuerydslNullBinding extends QuerydslExpandBinding<Path<? extends Object>, Object> {

    public QuerydslNullBinding() {
    }

    public QuerydslNullBinding(Prefix prefix) {
        super(prefix);
    }

    @Override
    public Optional<Predicate> bind(Path<?> path, Collection<?> value) {
        Assert.notNull(path, "Path must not be null!");
        Assert.notNull(value, "Value must not be null!");

        if (value.isEmpty()) {
            return Optional.empty();
        }

        try {
            // 对于null条件 只取一个值, 多个值没有意义
            final Boolean aBoolean = Boolean.valueOf(value.iterator().next().toString());
            if (path instanceof CollectionPathBase) {

                final CollectionPathBase cBase = (CollectionPathBase) path;
                return Optional.of(aBoolean ? cBase.isEmpty() : cBase.isNotEmpty());
            } else {
                final SimpleExpression simpleExpression = (SimpleExpression) path;
                return Optional.of(aBoolean ? simpleExpression.isNull(): simpleExpression.isNotNull());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
