package affix.binding;

import affix.Prefix;
import affix.dialect.BitwiseExpressions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Optional;

public class QuerydslBitwiseBinding extends QuerydslExpandBinding<Path<? extends Object>, Object> {
    protected final boolean has;

    public QuerydslBitwiseBinding(boolean has) {
        this.has = has;
    }

    public QuerydslBitwiseBinding(Prefix prefix, boolean has) {
        super(prefix);
        this.has = has;
    }

    @Override
    public Optional<Predicate> bind(Path<?> path, Collection<?> value) {

        Assert.notNull(path, "Path must not be null!");
        Assert.notNull(value, "Value must not be null!");

        if (value.isEmpty()) { return Optional.empty(); }
        try {
            final Class<?> type = path.getType();
            if (path instanceof NumberExpression && (Integer.class.equals(type) || Long.class.equals(type))) {
                final BooleanBuilder builder = new BooleanBuilder();
                value.stream().map(ns -> {
                    Optional<Long> rs = Optional.empty();
                    try {
                        rs = Optional.ofNullable(Long.valueOf(ns.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return rs;
                }).filter(Optional::isPresent).map(Optional::get)
                    .forEach(l -> {
                        if(isAnd()) builder.and(has ? BitwiseExpressions.bitHas(path, l): BitwiseExpressions.bitNotHas(path, l));
                        else builder.or(has ? BitwiseExpressions.bitHas(path, l): BitwiseExpressions.bitNotHas(path, l));
                    });
                return Optional.ofNullable(builder.getValue());
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
