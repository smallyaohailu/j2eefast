/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.build;

import java.util.List;

/**
 * 添加查询数据库总数分页使用
 * @author Jacky.gao ZhouZHou
 * @since 2016年11月1日
 */
public class Dataset {
	//数据集名称
	private String name;
	//数据
	private List<?> data;
	//总数
	private int count;

	//一共多少页
	private long totalPage;

	//sql 一页大小
	private int pageSize;

	public Dataset(String name, List<?> data) {
		this.name = name;
		this.data = data;
	}

	public Dataset(String name, List<?> data, int count) {
		this.name = name;
		this.data = data;
		this.count = count;
	}

	public Dataset(String name, List<?> data, int count, long totalPage) {
		this.name = name;
		this.data = data;
		this.count = count;
		this.totalPage = totalPage;
	}

	public Dataset(String name, List<?> data, int count, long totalPage, int pageSize) {
		this.name = name;
		this.data = data;
		this.count = count;
		this.totalPage = totalPage;
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public int getCount() {
		return count;
	}
	public String getName() {
		return name;
	}
	public List<?> getData() {
		return data;
	}
}
