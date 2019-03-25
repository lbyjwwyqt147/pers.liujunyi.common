package pers.liujunyi.cloud.common.query.jpa.annotation;

/***
 *  定义查询连接条件
 * @author ljy
 */
public enum MatchType {
    //filed = value
    equal,
    // termsQuery("key", obj1, obj2..)   一次匹配多个值
    equals,
    // 前缀查询
    prefix,
    // 通配符查询
    wildcard,
    // or
    or,
    //下面四个用于Number类型的比较
    // filed > value
    gt,
    // field >= value
    ge,
    // field < value
    lt,
    // field <= value
    le,
    // field != value
    notEqual,
    // field like value
    like,
    // field not like value
    notLike,
    // 下面四个用于可比较类型(Comparable)的比较
    // field > value
    greaterThan,
    // field >= value
    greaterThanOrEqualTo,
    // field < value
    lessThan,
    // field <= value
    lessThanOrEqualTo


}