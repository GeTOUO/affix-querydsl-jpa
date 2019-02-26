package affix.util;


import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import java.util.List;

public class PageUtils {
    public static <E> Page<E> page(List<E> totalElements, Pageable pageable) {

        Assert.notNull(totalElements, "totalElements must not be null!");
        Assert.notNull(pageable, "pageable must not be null!");

        final long offset = pageable.getOffset();
        final int size = totalElements.size();
        PageImpl<E> page;
        if (offset > size) {
            page = new PageImpl<>(Lists.newArrayList(), pageable, 0L);
        } else if (offset <= size && offset + pageable.getPageSize() > size) {
            page = new PageImpl<>(totalElements.subList((int) offset, size), pageable, size);
        } else {
            final List<E> eList = totalElements.subList((int) offset, (int) offset + pageable.getPageSize());
            page = new PageImpl<>(eList, pageable, size);
        }
        return page;
    }
}
