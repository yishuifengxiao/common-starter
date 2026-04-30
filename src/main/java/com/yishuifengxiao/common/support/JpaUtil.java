package com.yishuifengxiao.common.support;

import com.yishuifengxiao.common.tool.entity.Page;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

/**
 * JPA操作扩展支持工具
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class JpaUtil {

    /**
     * 生成根据条件查询的忽略大小和空字段的模糊查询jpa查询条件
     *
     * @param <T> 查询数据对应的数据类型
     * @param t   查询数据(POJO类)
     * @return 忽略大小和空字段的模糊查询jpa查询条件
     */
    public static <T> Example<T> fuzzy(T t) {
        if (t == null) {
            return Example.of(t);
        }
        
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withIgnoreCase(true);
        
        return Example.of(t, matcher);
    }

    /**
     * 生成根据条件查询的忽略大小和空字段的精确查询jpa查询条件
     *
     * @param <T> 查询数据对应的数据类型
     * @param t   查询数据(POJO类)
     * @return 忽略大小和空字段的精确查询jpa查询条件
     */
    public static <T> Example<T> exact(T t) {
        if (t == null) {
            return Example.of(t);
        }
        
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT)
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withIgnoreCase(false);
        
        return Example.of(t, matcher);
    }

    /**
     * 根据查询条件查询出全部数据
     *
     * @param <T>        查询条件对应的POJO类的类型
     * @param repository JpaRepository实例
     * @param example    查询条件
     * @return 查询出的数据
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> all(JpaRepositoryImplementation repository, Example<T> example) {
        if (repository == null || example == null) {
            return List.of();
        }
        return repository.findAll(example);
    }

    /**
     * 根据查询条件查询出全部数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param example    查询条件
     * @param orderName  排序字段名字，默认升序
     * @return 查询出的数据
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> all(JpaRepositoryImplementation repository, Example<T> example, String orderName) {
        if (repository == null || example == null || orderName == null || orderName.isEmpty()) {
            return all(repository, example);
        }
        return repository.findAll(example, Sort.by(Direction.ASC, orderName));
    }

    /**
     * 根据查询条件查询出全部数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param example    查询条件
     * @param sort       排序条件
     * @return 查询出的数据
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> all(JpaRepositoryImplementation repository, Example<T> example, Sort sort) {
        if (repository == null || example == null) {
            return List.of();
        }
        if (sort == null) {
            return repository.findAll(example);
        }
        return repository.findAll(example, sort);
    }

    /**
     * 根据条件查询<strong>模糊查询</strong>出所有的数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @return 查询出的数据
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> allFuzzy(JpaRepositoryImplementation repository, T query) {
        if (repository == null || query == null) {
            return List.of();
        }
        Example<T> example = JpaUtil.fuzzy(query);
        return repository.findAll(example);
    }

    /**
     * 根据条件查询<strong>模糊查询</strong>出所有的数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @param orderName  排序字段名字，默认升序
     * @return 查询出的数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> List<T> allFuzzy(JpaRepositoryImplementation repository, T query, String orderName) {
        if (repository == null || query == null || orderName == null || orderName.isEmpty()) {
            return allFuzzy(repository, query);
        }
        Example<T> example = JpaUtil.fuzzy(query);
        return repository.findAll(example, Sort.by(Direction.ASC, orderName));
    }

    /**
     * 根据条件查询<strong>模糊查询</strong>出所有的数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @param sort       排序条件
     * @return 查询出的数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> List<T> allFuzzy(JpaRepositoryImplementation repository, T query, Sort sort) {
        if (repository == null || query == null) {
            return List.of();
        }
        Example<T> example = JpaUtil.fuzzy(query);
        if (sort == null) {
            return repository.findAll(example);
        }
        return repository.findAll(example, sort);
    }

    /**
     * 根据条件查询<strong>精确查询</strong>出所有的数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @return 查询出的数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query) {
        if (repository == null || query == null) {
            return List.of();
        }
        Example<T> example = exact(query);
        return repository.findAll(example);
    }

    /**
     * 根据条件查询<strong>精确查询</strong>出所有的数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @param orderName  排序字段名字，默认升序
     * @return 查询出的数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query, String orderName) {
        if (repository == null || query == null || orderName == null || orderName.isEmpty()) {
            return allExact(repository, query);
        }
        Example<T> example = exact(query);
        return repository.findAll(example, Sort.by(Direction.ASC, orderName));
    }

    /**
     * 根据条件查询<strong>精确查询</strong>出所有的数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @param sort       排序条件
     * @return 查询出的数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query, Sort sort) {
        if (repository == null || query == null) {
            return List.of();
        }
        Example<T> example = exact(query);
        if (sort == null) {
            return repository.findAll(example);
        }
        return repository.findAll(example, sort);
    }

    /**
     * 根据条件<strong>分页模糊查询</strong>出数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @param pageSize   分页大小
     * @param pageNum    当前页页码,从1开始
     * @return 查询出来的数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum) {
        if (repository == null || query == null || pageSize <= 0) {
            return Page.of(List.of(), 0L, pageSize, pageNum);
        }
        
        int pageIndex = normalizePageIndex(pageNum);
        Example<T> example = JpaUtil.fuzzy(query);
        final org.springframework.data.domain.Page page = repository.findAll(example, PageRequest.of(pageIndex, pageSize));
        return Page.of(page.getContent(), page.getTotalElements(), pageSize, pageNum);
    }

    /**
     * 根据条件<strong>分页模糊查询</strong>出数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @param pageSize   分页大小
     * @param pageNum    当前页页码,从1开始
     * @param orderName  排序字段名字，默认升序
     * @return 查询出来的数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum, String orderName) {
        if (repository == null || query == null || pageSize <= 0 || orderName == null || orderName.isEmpty()) {
            return pageFuzzy(repository, query, pageSize, pageNum);
        }
        
        int pageIndex = normalizePageIndex(pageNum);
        Example<T> example = JpaUtil.fuzzy(query);
        final org.springframework.data.domain.Page page = repository.findAll(example, PageRequest.of(pageIndex, pageSize, Direction.ASC, orderName));
        return Page.of(page.getContent(), page.getTotalElements(), pageSize, pageNum);
    }

    /**
     * 根据条件<strong>分页模糊查询</strong>出数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param query      查询条件(POJO类实例)
     * @param pageSize   分页大小
     * @param pageNum    当前页页码,从1开始
     * @param sort       排序条件
     * @return 查询出来的数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum, Sort sort) {
        if (repository == null || query == null || pageSize <= 0) {
            return Page.of(List.of(), 0L, pageSize, pageNum);
        }
        
        int pageIndex = normalizePageIndex(pageNum);
        Example<T> example = JpaUtil.fuzzy(query);
        PageRequest pageRequest = sort == null 
                ? PageRequest.of(pageIndex, pageSize) 
                : PageRequest.of(pageIndex, pageSize, sort);
        final org.springframework.data.domain.Page page = repository.findAll(example, pageRequest);
        return Page.of(page.getContent(), page.getTotalElements(), pageSize, pageNum);
    }

    /**
     * 根据条件<strong>分页精确查询</strong>出数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param example    查询条件
     * @param pageSize   分页大小
     * @param pageNum    当前页页码,从1开始
     * @return 查询出来的数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum) {
        if (repository == null || example == null || pageSize <= 0) {
            return Page.of(List.of(), 0L, pageSize, pageNum);
        }
        
        int pageIndex = normalizePageIndex(pageNum);
        final org.springframework.data.domain.Page page = repository.findAll(example, PageRequest.of(pageIndex, pageSize));
        return Page.of(page.getContent(), page.getTotalElements(), pageSize, pageNum);
    }

    /**
     * 根据条件<strong>分页精确查询</strong>出数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param example    查询条件
     * @param pageSize   分页大小
     * @param pageNum    当前页页码,从1开始
     * @param orderName  排序字段名字，默认升序
     * @return 查询出来的数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum, String orderName) {
        if (repository == null || example == null || pageSize <= 0 || orderName == null || orderName.isEmpty()) {
            return page(repository, example, pageSize, pageNum);
        }
        
        int pageIndex = normalizePageIndex(pageNum);
        final org.springframework.data.domain.Page page = repository.findAll(example, PageRequest.of(pageIndex, pageSize, Direction.ASC, orderName));
        return Page.of(page.getContent(), page.getTotalElements(), pageSize, pageNum);
    }

    /**
     * 根据条件<strong>分页精确查询</strong>出数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param example    查询条件
     * @param pageSize   分页大小
     * @param pageNum    当前页页码,从1开始
     * @param sort       排序条件
     * @return 查询出来的数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum, Sort sort) {
        if (repository == null || example == null || pageSize <= 0) {
            return Page.of(List.of(), 0L, pageSize, pageNum);
        }
        
        int pageIndex = normalizePageIndex(pageNum);
        PageRequest pageRequest = sort == null 
                ? PageRequest.of(pageIndex, pageSize) 
                : PageRequest.of(pageIndex, pageSize, sort);
        final org.springframework.data.domain.Page page = repository.findAll(example, pageRequest);
        return Page.of(page.getContent(), page.getTotalElements(), pageSize, pageNum);
    }

    /**
     * 根据条件<strong>分页精确查询</strong>出数据
     *
     * @param <T>        查询数据对应的数据类型
     * @param repository JpaRepository实例
     * @param example    查询条件
     * @param pageSize   分页大小
     * @param pageNum    当前页页码,从1开始
     * @param direction  排序方向
     * @param properties 排序属性
     * @return 查询出来的数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum, Direction direction, String... properties) {
        if (repository == null || example == null || pageSize <= 0 || direction == null || properties == null || properties.length == 0) {
            return page(repository, example, pageSize, pageNum);
        }
        
        int pageIndex = normalizePageIndex(pageNum);
        final org.springframework.data.domain.Page page = repository.findAll(example, PageRequest.of(pageIndex, pageSize, direction, properties));
        return Page.of(page.getContent(), page.getTotalElements(), pageSize, pageNum);
    }

    /**
     * 规范化页码索引，将页码转换为从0开始的索引
     *
     * @param pageNum 当前页页码，从1开始
     * @return 从0开始的页码索引
     */
    private static int normalizePageIndex(int pageNum) {
        return pageNum > 1 ? pageNum - 1 : 0;
    }

}
