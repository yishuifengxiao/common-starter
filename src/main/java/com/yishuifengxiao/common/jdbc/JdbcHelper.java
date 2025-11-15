/**
 *
 */
package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.entity.Slice;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * JdbcTemplate操作器
 * </p>
 * <p>
 * 【注意】在没有特意指出的前提下，所有筛选条件的笔记方式为完全匹配
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface JdbcHelper {

    /**
     * 根据主键查询一条数据
     *
     * @param clazz      POJO类型
     * @param primaryKey 主键值
     * @param <T>        数据类型
     * @return 查询出来的数据
     */
    <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey);


    /**
     * 根据pojo实例中的非空属性值查询出所有符合条件的数据的数量
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param <T>      数据类型
     * @return 所有符合条件的数据的数量
     */
    <T> Long countAll(T t, boolean likeMode);

    /**
     * 根据pojo实例中的非空属性值查询出一条符合条件的数据
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders   排序条件
     * @param <T>      数据类型
     * @return 查询出来的数据
     */
    <T> T findOne(T t, boolean likeMode, Order... orders);


    /**
     * 根据pojo实例中的非空属性值查询出所有符合条件的数据
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders   排序条件
     * @param <T>      数据类型
     * @return 查询出来的数据
     */
    <T> List<T> findAll(T t, boolean likeMode, Order... orders);


    /**
     * 根据pojo实例中的非空属性值分页查询出所有符合条件的数据
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param slice    分页参数
     * @param orders   排序条件
     * @param <T>      数据类型
     * @return 查询出来的数据
     */
    <T> Page<T> findPage(T t, boolean likeMode, Slice slice, Order... orders);

    /**
     * 根据pojo实例中的非空属性值分页查询出所有符合条件的数据
     *
     * @param pageQuery pojo实例查询条件
     * @param likeMode  是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders    排序条件
     * @param <T>       数据类型
     * @return 查询出来的数据
     */
    <T> Page<T> findPage(PageQuery<T> pageQuery, boolean likeMode, Order... orders);


    /**
     * 根据主键全属性全量更新方式更新一条数据
     *
     * @param <T> POJO类
     * @param t   待更新的数据
     * @return 受影响的记录的数量
     */
    <T> int updateByPrimaryKey(T t);

    /**
     * 根据主键可选属性增量更新方式更新一条数据
     *
     * @param <T> POJO类
     * @param t   待更新的数据
     * @return 受影响的记录的数量
     */
    <T> int updateByPrimaryKeySelective(T t);


    /**
     * 根据主键删除一条数据
     *
     * @param <T>         POJO类
     * @param clazz       操作的对象
     * @param primaryKeys 主键值
     * @return 受影响的记录的数量
     */
    <T> int deleteByPrimaryKey(Class<T> clazz, Object... primaryKeys);

    /**
     * 以全属性方式新增一条数据
     *
     * @param <T> POJO类
     * @param t   待新增的数据
     * @return 保存数据的主键
     */
    <T> KeyHolder insert(T t);


    /**
     * 根据主键id判断数据是否存在，若存在则先删除存在的数据，然后再插入新的数据
     *
     * @param t   待操作的数据
     * @param <T> POJO类
     * @return 保存数据的主键;只有数据库为自增时才有用，其他情况下无效
     */
    <T> KeyHolder saveOrUpdate(T t);

    /**
     * 批量保存数据
     *
     * @param list 待批量保存的数据
     * @param <T>  POJO数据类型
     */
    <T> void saveAll(Collection<T> list);


    /**
     * 根据sql查询出所有的数据
     *
     * @param clazz  数据类型
     * @param sql    sql语句
     * @param params 参数
     * @param <T>    POJO类
     * @return 查询出来的数据
     */
    <T> List<T> findAll(Class<T> clazz, String sql, Object... params);

    /**
     * 根据SQL查询语句查找指定类型的对象列表
     * <p>命名参数不区分大小写，但建议与传入的 Map 或 JavaBean 属性保持一致</p>
     * <p>SQL 语句中的命名参数必须以冒号（:）开头，例如 :name。</p>
     * <p>
     * String sql = "UPDATE users SET name = :name, age = :age WHERE id = :id";
     * 此方法使用命名参数（如 :name）执行查询，参数值从 params 中获取。</p>
     *
     * @param <T>   查询结果的对象类型
     * @param clazz 返回结果的类型Class对象
     * @param sql   查询SQL语句
     * @param param SQL查询参数
     * @return 指定类型的对象列表
     */
    <T> List<T> find(Class<T> clazz, String sql, Object param);

    /**
     * 根据SQL查询语句和参数查找指定类型的对象列表
     * <p>命名参数不区分大小写，但建议与传入的 Map 或 JavaBean 属性保持一致</p>
     * <p>SQL 语句中的命名参数必须以冒号（:）开头，例如 :name。</p>
     * <p>
     * 此方法使用命名参数（如 :name）执行查询，参数值从 params 中获取。</p>
     * <pre>
     *     示例1：
     *     String sql = "UPDATE users SET name = :name, age = :age WHERE id = :id";
     *     Map<String, Object> params = new HashMap<>();
     *     params.put("name", user.getName());
     *     params.put("age", user.getAge());
     *     params.put("id", user.getId());
     * </pre>
     * <p>特别注意：在命名参数中，Like 查询需要特别注意，因为需要将百分号（%）包含在参数值中。</p>
     * <pre>
     *     示例2：  String sql = "SELECT * FROM users WHERE name LIKE :name";
     *             Map<String, Object> params = new HashMap<>();
     *             params.put("name", "%" + user.getName() + "%");
     * </pre>
     * <p>NamedParameterJdbcTemplate 支持 IN 子句，可以使用具名参数传入一个集合。</p>
     * <pre>
     *     示例3：  String sql = "SELECT * FROM users WHERE id IN (:ids)";
     *             Map<String, Object> params = new HashMap<>();
     *             params.put("ids", List.of(1, 2, 3));
     * </pre>
     *
     * @param <T>    查询结果的对象类型
     * @param clazz  返回结果的类型Class对象
     * @param sql    查询SQL语句
     * @param params SQL查询参数Map，键为参数名，值为参数值
     * @return 符合查询条件的对象列表，当前实现返回空列表
     */
    <T> List<T> find(Class<T> clazz, String sql, Map<String, Object> params);

    /**
     * 根据SQL查询语句和参数查找指定类型的对象列表
     * <p>命名参数不区分大小写，但建议与传入的 Map 或 JavaBean 属性保持一致</p>
     * <p>SQL 语句中的命名参数必须以冒号（:）开头，例如 :name。</p>
     * <p>
     * 此方法使用命名参数（如 :name）执行查询，参数值从 params 中获取。</p>
     *
     * <pre>
     *     示例1：  String sql = "UPDATE users SET name = :name, age = :age WHERE id = :id";
     *              SqlParameterSource params = new MapSqlParameterSource()
     *             .addValue("name", user.getName())
     *             .addValue("age", user.getAge())
     *             .addValue("id", user.getId());
     * </pre>
     * <p>特别注意：在命名参数中，Like 查询需要特别注意，因为需要将百分号（%）包含在参数值中。</p>
     * <pre>
     *     示例2：  String sql = "SELECT * FROM users WHERE name LIKE :name";
     *             SqlParameterSource params = new MapSqlParameterSource()
     *             .addValue("name", "%" + user.getName() + "%");
     * </pre>
     * <p>NamedParameterJdbcTemplate 支持 IN 子句，可以使用具名参数传入一个集合。</p>
     * <pre>
     *     示例3：  String sql = "SELECT * FROM users WHERE id IN (:ids)";
     *             SqlParameterSource params = new MapSqlParameterSource()
     *             .addValue("ids", List.of(1, 2, 3));
     * </pre>
     *
     * @param <T>    查询结果的对象类型
     * @param clazz  返回结果的类型Class对象
     * @param sql    执行的SQL查询语句
     * @param params SQL查询参数
     * @return 指定类型的对象列表
     */
    <T> List<T> find(Class<T> clazz, String sql, SqlParameterSource params);

    /**
     * 根据SQL查询语句和参数查找指定类型的对象列表
     * <p>命名参数不区分大小写，但建议与传入的 Map 或 JavaBean 属性保持一致</p>
     * <p>SQL 语句中的命名参数必须以冒号（:）开头，例如 :name。</p>
     * <p>
     * 此方法使用命名参数（如 :name）执行查询，参数值从 params 中获取。</p>
     * <pre>
     *     示例1：
     *     String sql = "UPDATE users SET name = :name, age = :age WHERE id = :id";
     *     Map<String, Object> params = new HashMap<>();
     *     params.put("name", user.getName());
     *     params.put("age", user.getAge());
     *     params.put("id", user.getId());
     * </pre>
     * <p>特别注意：在命名参数中，Like 查询需要特别注意，因为需要将百分号（%）包含在参数值中。</p>
     * <pre>
     *     示例2：  String sql = "SELECT * FROM users WHERE name LIKE :name";
     *             Map<String, Object> params = new HashMap<>();
     *             params.put("name", "%" + user.getName() + "%");
     * </pre>
     * <p>NamedParameterJdbcTemplate 支持 IN 子句，可以使用具名参数传入一个集合。</p>
     * <pre>
     *     示例3：  String sql = "SELECT * FROM users WHERE id IN (:ids)";
     *             Map<String, Object> params = new HashMap<>();
     *             params.put("ids", List.of(1, 2, 3));
     * </pre>
     *
     * @param <T>    查询结果的对象类型
     * @param clazz  返回结果的类型Class对象
     * @param slice  分页参数
     * @param sql    查询SQL语句
     * @param params SQL查询参数Map，键为参数名，值为参数值
     * @return 符合查询条件的对象列表，当前实现返回空列表
     */
    <T> Page<T> find(Class<T> clazz, Slice slice, String sql, Map<String, Object> params);

    /**
     * 获取操作的JdbcTemplate实例
     *
     * @return JdbcTemplate实例
     */
    JdbcTemplate jdbcTemplate();

    /**
     * 根据指定的类类型和命名处理器查找数据
     *
     * @param clazz   要查找的数据类型类对象
     * @param handler 命名处理器，用于处理SQL语句和参数映射
     * @return 返回指定类型的数据列表
     */
    <T> List<T> find(Class<T> clazz, NamedHandler handler);

    /**
     * 根据指定的处理逻辑查找分页数据
     *
     * @param clazz   要查询的实体类类型
     * @param slice   分页信息，包含页码和每页大小
     * @param handler 命名处理程序，用于生成SQL语句和参数
     * @param <T>     实体类泛型参数
     * @return 返回指定类型的分页结果
     */
    <T> Page<T> find(Class<T> clazz, Slice slice, NamedHandler handler);


}
