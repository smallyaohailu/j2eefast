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
package com.bstek.ureport.definition.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.definition.dataset.DatasetDefinition;
import com.bstek.ureport.definition.dataset.SqlDatasetDefinition;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.utils.ToolUtils;

/**
 * 优化分页
 * @author Jacky.gao ZhouZhou
 * @since 2017年2月9日
 */
public class BuildinDatasourceDefinition implements DatasourceDefinition {
	//数据源
	private String name;
	private List<DatasetDefinition> datasets;
	private Map<String, List<?>> dbs = new HashMap<>();
	
	public List<Dataset> buildDatasets(Connection conn,Map<String,Object> parameters){
		dbs.clear();
		if(datasets==null || datasets.size()==0){
			return null;
		}
		List<Dataset> list = new ArrayList<Dataset>();
		try{
			for(DatasetDefinition dsDef:datasets){
				SqlDatasetDefinition sqlDataset=(SqlDatasetDefinition)dsDef;

				String mpKey = sqlDataset.getSqlKey(parameters);
				if(ToolUtils.isNotEmpty(dbs.get(mpKey))){
					list.add(new Dataset(sqlDataset.getName(),dbs.get(mpKey)));
				}else{
//					//分页查询
//					if(ToolUtils.isNotEmpty(parameters.get("__paging")) &&
//							(boolean)parameters.get("__paging") &&
//							((List<String>)parameters.get("__objList")).contains(sqlDataset.getName())){
//						//总数
//						Dataset sum = sqlDataset.buildDatasetCount(parameters, conn);
//						list.add(sum);
//
//						Dataset ds=sqlDataset.buildDataset(parameters, conn);
//						list.add(ds);
//						dbs.put(mpKey,ds.getData());
//					}else{
//					}
					Dataset ds=sqlDataset.buildDataset(parameters, conn);
					list.add(ds);
					dbs.put(mpKey,ds.getData());
				}
//				Dataset ds=sqlDataset.buildDataset(parameters, conn);
//				list.add(ds);
			}			
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				throw new ReportComputeException(e);
			}
		}
		return list;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}


	public Map<String, List<?>> getDbs() {
		return dbs;
	}

	public void setDbs(Map<String, List<?>> dbs) {
		this.dbs = dbs;
	}

	@Override
	public List<DatasetDefinition> getDatasets() {
		return datasets;
	}
	
	public void setDatasets(List<DatasetDefinition> datasets) {
		this.datasets = datasets;
	}

	@Override
	public DatasourceType getType() {
		return DatasourceType.buildin;
	}
}
