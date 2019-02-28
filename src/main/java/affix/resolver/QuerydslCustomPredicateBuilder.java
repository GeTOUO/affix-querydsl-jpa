package affix.resolver;

import affix.binding.QuerydslExpandBinding;
import affix.binding.QuerydslLikeBinding;
import com.querydsl.core.types.Path;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.binding.MultiValueBinding;
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuerydslCustomPredicateBuilder extends QuerydslPredicateBuilder {
    private MultiValueBinding<Path<? extends Object>, Object> defaultBindings;
    /**
     * Creates a new {@link QuerydslCustomPredicateBuilder} for the given {@link ConversionService} and
     * {@link EntityPathResolver}.
     *
     * @param conversionService must not be {@literal null}.
     * @param resolver can be {@literal null}.
     */
    public QuerydslCustomPredicateBuilder(ConversionService conversionService, EntityPathResolver resolver, Optional<QuerydslExpandBinding> customBinding) {
        super(conversionService, resolver);
        customBinding.ifPresent(binding -> {
            try {
                final Field defaultBindingField = QuerydslPredicateBuilder.class.getDeclaredField("defaultBinding");
//                Arrays.asList(QuerydslPredicateBuilder.class.getDeclaredFields()).stream().filter(field -> MultiValueBinding.class.isAssignableFrom(field.getType()))
                defaultBindingField.setAccessible(true);
                defaultBindingField.set(this, binding);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    // key :: PathInformation
    public Map<Object, Path<?>> getPaths() {
        Map<Object, Path<?>> res = new HashMap<>();
        try {
            final Field pathsField = QuerydslPredicateBuilder.class.getDeclaredField("paths");
            pathsField.setAccessible(true);
            res = (Map<Object, Path<?>> ) pathsField.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return res;
    }
}
