package com.leqiwl.novel.util;

/**
 * @author: CaoBin
 * @Date: 2022/2/18 15:28
 * @Description:
 */
@FunctionalInterface
public interface LockFunctionInterface<T> {

    void process(T t);

}
