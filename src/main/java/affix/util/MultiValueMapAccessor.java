package affix.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 键值存取工具类, 避免null异常和支持类型转换
 */
public final class MultiValueMapAccessor {
    private final MultiValueMap<String, String> vm;

    public MultiValueMapAccessor(MultiValueMap vm) {
        this.vm = vm;
    }

    public <T> List<T> values(String keyName, Function<String, T> valueConverter) {
        return vm == null ? new ArrayList<>() : Optional.ofNullable(vm.get(keyName))
                .map(strList -> strList.stream().map(valueConverter).collect(Collectors.toList()))
                .orElseGet(() -> new ArrayList<>());
    }

    public static MultiValueMapAccessor of(MultiValueMap<String, String> vm) {
        return new MultiValueMapAccessor(vm);
    }

    public MultiValueMap<String, String> multiValueMap() {
        return Optional.ofNullable(vm).orElseGet(() -> new LinkedMultiValueMap<>());
    }
}
