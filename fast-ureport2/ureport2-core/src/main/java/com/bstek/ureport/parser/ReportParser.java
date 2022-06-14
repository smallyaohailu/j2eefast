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
package com.bstek.ureport.parser;

import cn.hutool.core.util.IdUtil;
import com.bstek.ureport.definition.*;
import com.bstek.ureport.definition.datasource.DatasourceDefinition;
import com.bstek.ureport.definition.searchform.SearchForm;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.exception.ReportParseException;
import com.bstek.ureport.parser.impl.*;
import com.bstek.ureport.parser.impl.searchform.SearchFormParser;
import com.bstek.ureport.utils.ToolUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.*;

/**
 * @author Jacky.gao
 * @since 2016年12月2日
 */
public class ReportParser {
	private Map<String,Parser<?>> parsers=new HashMap<String,Parser<?>>();
	public ReportParser() {
		parsers.put("row",new RowParser());
		parsers.put("config",new ConfigParser());
		parsers.put("column",new ColumnParser());
		parsers.put("cell",new CellParser());
		parsers.put("datasource",new DatasourceParser());
		parsers.put("paper",new PaperParser());
		parsers.put("header",new HeaderFooterParser());
		parsers.put("footer",new HeaderFooterParser());
		parsers.put("search-form",new SearchFormParser());
	}
	public ReportDefinition parse(InputStream inputStream,String file) {
		ReportDefinition report=new ReportDefinition();
		report.setReportFullName(file);
		SAXReader saxReader = new SAXReader();
		try{
			Document document = saxReader.read(inputStream);
			Element element = document.getRootElement();
			if(!element.getName().equals("ureport")){
				throw new ReportParseException("Unknow report file.");
			}
			List<RowDefinition> rows=new ArrayList<RowDefinition>();
			List<ColumnDefinition> columns=new ArrayList<ColumnDefinition>();
			List<CellDefinition> cells=new ArrayList<CellDefinition>();
			List<DatasourceDefinition> datasources=new ArrayList<DatasourceDefinition>();
			//行信息
			report.setRows(rows);
			//列信息
			report.setColumns(columns);
			//单元格信息
			report.setCells(cells);
			report.setDatasources(datasources); 
			for(Object obj:element.elements()){
				if(obj==null || !(obj instanceof Element)){
					continue;
				}
				Element ele=(Element)obj;
				Parser<?> parser=parsers.get(ele.getName());
				if(parser!=null){
					Object target=parser.parse(ele);
					if(target instanceof RowDefinition){
						rows.add((RowDefinition)target);
					}else if(target instanceof ColumnDefinition){
						columns.add((ColumnDefinition)target);
					}else if(target instanceof CellDefinition){
						cells.add((CellDefinition)target);
					}else if(target instanceof DatasourceDefinition){
						datasources.add((DatasourceDefinition)target);
					}else if(target instanceof Paper){
						Paper paper=(Paper)target;
						report.setPaper(paper);
					}else if(target instanceof Config){
						Config config = (Config) target;
						if("classpath:template/template.ureport.xml".equals(report.getReportFullName())){
							if((ToolUtils.isNotEmpty(config.getName())
									&& "template.ureport.xml".equals(config.getName())) ||
									ToolUtils.isEmpty(config.getName())){
								//初始文件赋值名称
								config.setName(IdUtil.nanoId());
							}
						}
						report.setConfig(config);
					}
					else if(target instanceof HeaderFooterDefinition){
						HeaderFooterDefinition hf=(HeaderFooterDefinition)target;
						if(ele.getName().equals("header")){
							report.setHeader(hf);
						}else{
							report.setFooter(hf);
						}
					}else if(target instanceof SearchForm){
						SearchForm form=(SearchForm)target;
						report.setSearchForm(form);
						report.setSearchFormXml(ele.asXML());
					}
				}else{
					throw new ReportParseException("Unknow element :"+ele.getName());
				}
			}
			Collections.sort(rows);
			Collections.sort(columns);
		}catch(Exception ex){
			throw new ReportParseException(ex);
		}
		rebuild(report);
		return report;
	}

	//转换赋值 单元格 相邻左侧 上面 单元格
	private void rebuild(ReportDefinition report) {
		List<CellDefinition> cells=report.getCells();
		Map<String,CellDefinition> cellsMap=new HashMap<String,CellDefinition>();
		Map<String,CellDefinition> cellsRowColMap=new HashMap<String,CellDefinition>();
		for(CellDefinition cell:cells){
			cellsMap.put(cell.getName(), cell);
			int rowNum=cell.getRowNumber(),colNum=cell.getColumnNumber(),rowSpan=cell.getRowSpan(),colSpan=cell.getColSpan();
			rowSpan = rowSpan>0 ? rowSpan-- : 1;
			colSpan = colSpan>0 ? colSpan-- : 1; // 1 - 2
			int rowStart=rowNum,rowEnd=rowNum+rowSpan,colStart=colNum,colEnd=colNum+colSpan;
			for(int i=rowStart;i<rowEnd;i++){
				//1,2 2,2
				cellsRowColMap.put(i+","+colNum, cell);
			}
			for(int i=colStart;i<colEnd;i++){
				//1,2 1,3
				cellsRowColMap.put(rowNum+","+i, cell);								
			}
		}
		for(CellDefinition cell:cells){
			int rowNumber=cell.getRowNumber();
			int colNumber=cell.getColumnNumber();
			String leftParentCellName=cell.getLeftParentCellName();
			if(StringUtils.isNotBlank(leftParentCellName)){
				if(!leftParentCellName.equals("root")){
					CellDefinition targetCell=cellsMap.get(leftParentCellName);
					if(targetCell==null){
						throw new ReportException("Cell ["+cell.getName()+"] 's left parent cell ["+leftParentCellName+"] not exist.");
					}
					cell.setLeftParentCell(targetCell);					
				}
			}else{
				//列大于一 就一定会有左边列
				if(colNumber>1){
					CellDefinition targetCell = cellsRowColMap.get(rowNumber+","+(colNumber-1));
					if(targetCell != null){
						//cell.setLeftParentCellName(targetCell.getName());
						cell.setLeftParentCell(targetCell);
					}
				}
			}

			String topParentCellName=cell.getTopParentCellName();
			if(StringUtils.isNotBlank(topParentCellName)){
				if(!topParentCellName.equals("root")){
					CellDefinition targetCell=cellsMap.get(topParentCellName);
					if(targetCell==null){
						throw new ReportException("Cell ["+cell.getName()+"] 's top parent cell ["+topParentCellName+"] not exist.");
					}
					cell.setTopParentCell(targetCell);					
				}
			}else{
				if(rowNumber>1){
					CellDefinition targetCell=cellsRowColMap.get((rowNumber-1)+","+colNumber);
					if(targetCell != null){
						//cell.setTopParentCellName(targetCell.getName());
						cell.setTopParentCell(targetCell);
					}
				}
			}

			//设置下方
			String downParentCellName = cell.getDownParentCellName();
			if(StringUtils.isNotBlank(downParentCellName)){
				if(!topParentCellName.equals("root")){
					CellDefinition targetCell=cellsMap.get(topParentCellName);
					if(targetCell==null){
						throw new ReportException("Cell ["+cell.getName()+"] 's down parent cell ["+topParentCellName+"] not exist.");
					}
					cell.setDownParentCell(targetCell);
				}
			}else{
				int rowN = rowNumber + (cell.getRowSpan() > 0?(cell.getRowSpan() -1):0);
				//当前行小于总行数才有 相邻下单元格
				if(rowN < report.getRows().size()){
					CellDefinition targetCell=cellsRowColMap.get((rowN+1)+","+colNumber);
					cell.setDownParentCellName(targetCell.getName());
					cell.setDownParentCell(targetCell);
				}
			}

			//设置右侧相邻单元格
			String rightParentCellName = cell.getRightParentCellName();
			if(StringUtils.isNotBlank(rightParentCellName)){
				if(!topParentCellName.equals("root")){
					CellDefinition targetCell=cellsMap.get(rightParentCellName);
					if(targetCell==null){
						throw new ReportException("Cell ["+cell.getName()+"] 's down parent cell ["+topParentCellName+"] not exist.");
					}
					cell.setRightParentCell(targetCell);
				}
			}else{
				//当前所在列小于总列 才有相邻右单元格
				int colN  = colNumber + (cell.getColSpan() > 0?(cell.getColSpan() -1):0);
				if( colN < report.getColumns().size()){
					CellDefinition targetCell=cellsRowColMap.get(rowNumber+","+(colN+1));
					cell.setRightParentCellName(targetCell.getName());
					cell.setRightParentCell(targetCell);
				}
			}
		}
	}
}
