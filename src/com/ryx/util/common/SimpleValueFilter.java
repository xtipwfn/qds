package com.ryx.util.common;

import com.alibaba.fastjson.serializer.ValueFilter;

public class SimpleValueFilter implements ValueFilter {
	public Object process(Object object, String name, Object value) {
		// 只要字段为空则返回""
		if (value == null)
			return "";
		return value;
	}

}
