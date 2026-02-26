package tech.mystox.framework.proxy;

import java.util.Comparator;
import java.util.List;

public class OperaFilterChain implements OperaInvoker {

    private final List<OperaFilter> filters;
    private final OperaInvoker target;

    public OperaFilterChain(List<OperaFilter> filters,
                            OperaInvoker target) {
        this.filters = filters.stream()
                .sorted(Comparator.comparingInt(OperaFilter::getOrder))
                .toList();
        this.target = target;
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        return buildInvokerChain(0).invoke(invocation);
    }

    private OperaInvoker buildInvokerChain(int index) {

        if (index == filters.size()) {
            return target;
        }

        OperaFilter filter = filters.get(index);

        return invocation ->
                filter.invoke(
                        buildInvokerChain(index + 1),
                        invocation
                );
    }
}