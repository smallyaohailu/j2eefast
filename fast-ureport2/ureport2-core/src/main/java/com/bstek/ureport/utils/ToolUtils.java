package com.bstek.ureport.utils;

import java.security.MessageDigest;
import java.util.*;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huanzhou
 */
public class ToolUtils {

    private final static Logger LOG                        = LoggerFactory.getLogger(ToolUtils.class);

    private static final String QUESTION_MARK = "?";
    private static final String EMPTY = "";
    private static final String DOT = ".";


    protected static final List<SelectItem> COUNT_SELECT_ITEM = Collections.singletonList(defaultCountSelectItem());

    /**
     * 获取jsqlparser中count的SelectItem
     */
    private static SelectItem defaultCountSelectItem() {
        Function function = new Function();
        function.setName("COUNT");
        function.setAllColumns(true);
        return new SelectExpressionItem(function).withAlias(new Alias("total"));
    }

    public static String hash(String s){
        try{
            return new String(toHex(md5(s)).getBytes("UTF-8"), "UTF-8");
        }
        catch (Exception e){
            LOG.error("not supported charset...{}", e);
            return s;
        }
    }

    private static byte[] md5(String s){
        MessageDigest algorithm;
        try{
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes("UTF-8"));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        }
        catch (Exception e){
            LOG.error("MD5 Error...", e);
        }
        return null;
    }

    private static final String toHex(byte hash[]){
        if (hash == null){
            return null;
        }
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++){
            if ((hash[i] & 0xff) < 0x10){
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * 判断对象是否为空  true 不为空
     * @author zhouzhou
     * @date 2020-03-11 15:07
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     *
     * 对象是否为空 true 为空
     * @author zhouzhou
     * @date 2020-03-11 15:09
     */
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof String) {
            if ("".equals(o.toString().trim())) {
                return true;
            }
        } else if (o instanceof List) {
            if (((List<?>) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Map) {
            if (((Map<?, ?>) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Set) {
            if (((Set<?>) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Object[]) {
            if (((Object[]) o).length == 0) {
                return true;
            }
        } else if (o instanceof int[]) {
            if (((int[]) o).length == 0) {
                return true;
            }
        } else if (o instanceof long[]) {
            if (((long[]) o).length == 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * 无法进行count优化时,降级使用此方法
     *
     * @param originalSql 原始sql
     * @return countSql
     */
    public static String lowLevelCountSql(String originalSql) {
        return getOriginalCountSql(originalSql);
    }

    public static String getOriginalCountSql(String originalSql) {
        return String.format("SELECT COUNT(*) FROM (%s) TOTAL", originalSql);
    }

    /**
     * 自动获取查询语句组成COUNT语句自动优化
     * @param sql  sql
     * @return countSql
     * copy:com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
     * zhouzhou 本地化优化
     */
    public static String autoCountSql(String sql) {
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            SelectBody selectBody = select.getSelectBody();
            // https://github.com/baomidou/mybatis-plus/issues/3920  分页增加union语法支持
            if(selectBody instanceof SetOperationList) {
                return lowLevelCountSql(sql);
            }
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            Distinct distinct = plainSelect.getDistinct();
            GroupByElement groupBy = plainSelect.getGroupBy();
            List<OrderByElement> orderBy = plainSelect.getOrderByElements();

            if (isNotEmpty(orderBy)) {
                boolean canClean = true;
                if (groupBy != null) {
                    // 包含groupBy 不去除orderBy
                    canClean = false;
                }
                if (canClean) {
                    for (OrderByElement order : orderBy) {
                        // order by 里带参数,不去除order by
                        Expression expression = order.getExpression();
                        if (!(expression instanceof Column) && expression.toString().contains(QUESTION_MARK)) {
                            canClean = false;
                            break;
                        }
                    }
                }
                if (canClean) {
                    plainSelect.setOrderByElements(null);
                }
            }
            //#95 Github, selectItems contains #{} ${}, which will be translated to ?, and it may be in a function: power(#{myInt},2)
            for (SelectItem item : plainSelect.getSelectItems()) {
                if (item.toString().contains(QUESTION_MARK)) {
                    return lowLevelCountSql(select.toString());
                }
            }
            // 包含 distinct、groupBy不优化
            if (distinct != null || null != groupBy) {
                return lowLevelCountSql(select.toString());
            }
            // 包含 join 连表,进行判断是否移除 join 连表
            List<Join> joins = plainSelect.getJoins();
            if (isNotEmpty(joins)) {
                boolean canRemoveJoin = true;
                String whereS = Optional.ofNullable(plainSelect.getWhere()).map(Expression::toString).orElse(EMPTY);
                // 不区分大小写
                whereS = whereS.toLowerCase();
                for (Join join : joins) {
                    if (!join.isLeft()) {
                        canRemoveJoin = false;
                        break;
                    }
                    FromItem rightItem = join.getRightItem();
                    String str = "";
                    if (rightItem instanceof Table) {
                        Table table = (Table) rightItem;
                        str = Optional.ofNullable(table.getAlias()).map(Alias::getName).orElse(table.getName()) + DOT;
                    } else if (rightItem instanceof SubSelect) {
                        SubSelect subSelect = (SubSelect) rightItem;
                        /* 如果 left join 是子查询，并且子查询里包含 ?(代表有入参) 或者 where 条件里包含使用 join 的表的字段作条件,就不移除 join */
                        if (subSelect.toString().contains(QUESTION_MARK)) {
                            canRemoveJoin = false;
                            break;
                        }
                        str = subSelect.getAlias().getName() + DOT;
                    }
                    // 不区分大小写
                    str = str.toLowerCase();
                    String onExpressionS = join.getOnExpression().toString();
                    /* 如果 join 里包含 ?(代表有入参) 或者 where 条件里包含使用 join 的表的字段作条件,就不移除 join */
                    if (onExpressionS.contains(QUESTION_MARK) || whereS.contains(str)) {
                        canRemoveJoin = false;
                        break;
                    }
                }
                if (canRemoveJoin) {
                    plainSelect.setJoins(null);
                }
            }
            // 优化 SQL
            plainSelect.setSelectItems(COUNT_SELECT_ITEM);
            return select.toString();
        } catch (JSQLParserException e) {
            // 无法优化使用原 SQL
            LOG.warn("optimize this sql to a count sql has exception, sql:\"" + sql + "\", exception:\n" + e.getCause());
        } catch (Exception e) {
            LOG.warn("optimize this sql to a count sql has error, sql:\"" + sql + "\", exception:\n" + e);
        }
        return lowLevelCountSql(sql);
    }
}
