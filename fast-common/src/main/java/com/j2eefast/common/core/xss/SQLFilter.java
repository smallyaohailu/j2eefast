/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.xss;

import org.apache.commons.lang3.StringUtils;
import com.j2eefast.common.core.exception.RxcException;

import java.util.regex.Pattern;


/**
 * SQL过滤
 * @author huanzhou
 */
public class SQLFilter {

	/**
	 * 仅支持字母、数字、下划线、空格、逗号（支持多个字段排序）
	 */
	public static String 				  SQL_PATTERN = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|((extractvalue|updatexml|if|mid|database)([\\s]*?)\\()|"
			+ "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute|case when|sleep|union|load_file)\\b)";


	/**
	 * SQL注入过滤
	 *
	 * @param str 待验证的字符串
	 */
	public static String sqlInject(String str) {

		if (StringUtils.isBlank(str)) {
			return null;
		}

		// 去掉'|"|;|\字符
		str = StringUtils.replace(str, "'", "");
		str = StringUtils.replace(str, "\"", "");
		str = StringUtils.replace(str, ";", "");
		str = StringUtils.replace(str, "\\", "");

		return escapeOrderBySql(str);
	}

	/**
	 * 验证 语法是否符合规范
	 */
	public static boolean isValidOrderBySql(String value) {
		Pattern sqlPattern = Pattern.compile(SQL_PATTERN, Pattern.CASE_INSENSITIVE);
		if (sqlPattern.matcher(value.toLowerCase()).find()) {
			return true;
		}
		return false;
	}

	/**
	 * 检查字符，防止注入绕过
	 */
	public static String escapeOrderBySql(String value) {
		if (StringUtils.isNotEmpty(value) && isValidOrderBySql(value)) {
			throw new RxcException("包含非法字符");
		}
		return value;
	}
}
