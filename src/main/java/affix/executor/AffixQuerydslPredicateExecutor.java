package affix.executor;

import affix.model.Domain;
import affix.resolver.QueryParamAccessor;
import affix.util.GenericClassUtil;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.util.MultiValueMap;


public interface AffixQuerydslPredicateExecutor<T extends Domain> extends QuerydslPredicateExecutor<T> {


    default Class<T> getParameterizedTypeOfEntity() {
        final Class<?> aClass = GenericTypeResolver.resolveTypeArgument(getClass(), AffixQuerydslPredicateExecutor.class);
        if (aClass != null && Domain.class.isAssignableFrom(aClass)) {
            return (Class<T>) aClass;
        }
        final Class<?> genericClass = GenericClassUtil.getGenericClass(getClass(), Domain.class);
        return genericClass == null ? (Class<T>) aClass : (Class<T>) genericClass;
    }

    default Page<T> findAllByAnyWhere(Predicate equalsPredicate, Pageable pageable, MultiValueMap<String, String> params) {
        final Predicate allPredicate = ExpressionUtils.allOf(equalsPredicate, QueryParamAccessor.resolverPredicate(getParameterizedTypeOfEntity(), params, false));
        return findAll(allPredicate, pageable);
    }

    default Page<T> findAllByAnyWhere(Pageable pageable, MultiValueMap<String, String> params) {
        return findAllByAnyWhere(pageable, params, true);
    }

    default Page<T> findAllByAnyWhere(Pageable pageable, MultiValueMap<String, String> params, boolean parseEquals) {
        return findAll(QueryParamAccessor.resolverPredicate(getParameterizedTypeOfEntity(), params, parseEquals), pageable);
    }
}
