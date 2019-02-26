package affix.binding;

import affix.Prefix;
import com.querydsl.core.types.Path;
import org.springframework.data.querydsl.binding.MultiValueBinding;

import java.util.Optional;

public abstract class QuerydslExpandBinding<T extends Path<? extends S>, S>  implements MultiValueBinding<T, S> {
    protected Optional<Prefix> predicateConnect = Optional.of(Prefix.DEFAULT_AND);

    public QuerydslExpandBinding() {
        this(Prefix.DEFAULT_AND);
    }

    public QuerydslExpandBinding(Prefix prefix) {
        this.predicateConnect = Optional.ofNullable(prefix);
    }

    protected boolean isAnd() {
        return !(predicateConnect.isPresent() && predicateConnect.get().equals(Prefix.OR));
    }
}
