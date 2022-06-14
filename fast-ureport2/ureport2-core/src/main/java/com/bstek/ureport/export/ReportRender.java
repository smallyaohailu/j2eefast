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
package com.bstek.ureport.export;

import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.cache.CacheUtils;
import com.bstek.ureport.definition.CellDefinition;
import com.bstek.ureport.definition.Expand;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.exception.ReportParseException;
import com.bstek.ureport.export.builder.down.DownCellbuilder;
import com.bstek.ureport.export.builder.right.RightCellbuilder;
import com.bstek.ureport.model.Report;
import com.bstek.ureport.parser.ReportParser;
import com.bstek.ureport.provider.report.ReportProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Jacky.gao
 * @since 2016年12月4日
 */
public class ReportRender implements ApplicationContextAware{
	private ReportParser reportParser;
	private ReportBuilder reportBuilder;
	private Collection<ReportProvider> reportProviders;
	private DownCellbuilder downCellParentbuilder=new DownCellbuilder();
	private RightCellbuilder rightCellParentbuilder=new RightCellbuilder();

	public Report render(String file,Map<String,Object> parameters){
		ReportDefinition reportDefinition=getReportDefinition(file);
		return reportBuilder.buildReport(reportDefinition,parameters);
	}
	
	public Report render(ReportDefinition reportDefinition,Map<String,Object> parameters){
		return reportBuilder.buildReport(reportDefinition,parameters);
	}
	
	public ReportDefinition getReportDefinition(String file){
		ReportDefinition reportDefinition=CacheUtils.getReportDefinition(file);
		if(reportDefinition==null){
			reportDefinition=parseReport(file);
			rebuildReportDefinition(reportDefinition);
			CacheUtils.cacheReportDefinition(file, reportDefinition);
		}
		return reportDefinition;
	}
	
	public void rebuildReportDefinition(ReportDefinition reportDefinition){
		List<CellDefinition> cells=reportDefinition.getCells();
		for(CellDefinition cell:cells){
			addRowChildCell(cell,cells);
			addColumnChildCell(cell,cells);
		}
		for(CellDefinition cell:cells){
			Expand expand=cell.getExpand();
			if(expand.equals(Expand.Down)){
				downCellParentbuilder.buildParentCell(cell,cells);
			}else if(expand.equals(Expand.Right)){
				rightCellParentbuilder.buildParentCell(cell,cells);
			}
		}
	}

	public ReportDefinition parseReport(String file){
		InputStream inputStream=null;
		try {
			inputStream=buildReportFile(file);
			ReportDefinition reportDefinition=reportParser.parse(inputStream,file);
			return reportDefinition;
		}finally{
			try {
				if(inputStream!=null){
					inputStream.close();					
				}
			} catch (IOException e) {
				throw new ReportParseException(e);
			}
		}
	}
	
	private InputStream buildReportFile(String file){
		InputStream inputStream=null;
		for(ReportProvider provider:reportProviders){
			if(file.startsWith(provider.getPrefix())){
				inputStream=provider.loadReport(file);
			}
		}
		if(inputStream==null){
			throw new ReportException("Report ["+file+"] not support.");
		}
		return inputStream;
	}

	/**
	 * 获取同行右侧单元格
	 * @param cell
	 * @param cells
	 */
	private void addRowChildCell(CellDefinition cell,List<CellDefinition> cells){

		List<CellDefinition> rowCells = new ArrayList<>();
		int row = cell.getRowNumber();
		for(CellDefinition c:cells){
			if(c.getRowNumber() == row){
				rowCells.add(c);
			}
		}

		for(CellDefinition c: rowCells){
			if(c.getColumnNumber() > cell.getColumnNumber()){
				cell.getRowChildrenCells().add(c);
			}
		}

		for(CellDefinition c: rowCells){
			if(c.getColumnNumber() < cell.getColumnNumber()){
				cell.getLeftParentCells().add(c);
			}
		}
	}

	private void addColumnChildCell(CellDefinition cell,List<CellDefinition> cells){
//		CellDefinition topCell=cell.getTopParentCell();
//		if(topCell==null){
//			return;
//		}
//		List<CellDefinition> childrenCells=topCell.getColumnChildrenCells();
//		childrenCells.add(childCell);
//		addColumnChildCell(topCell,childCell);
		List<CellDefinition> columnCells = new ArrayList<>();
		int col = cell.getColumnNumber(); //所属列
		for(CellDefinition c:cells){
			if(c.getColumnNumber() == col){
				columnCells.add(c);
			}
		}
		for(CellDefinition c: columnCells){
			if(c.getRowNumber() > cell.getRowNumber()){
				cell.getColumnChildrenCells().add(c);
			}
		}
	}
//	private void addRowChildCell(CellDefinition cell,CellDefinition childCell){
//
//		//		//解决横向合并问题
////		if((cell.getRowNumber() + (cell.getRowSpan() > 0? (cell.getRowSpan() - 1): 0)) >= childCell.getRowNumber()){
////
////			CellDefinition leftCell = cell.getLeftParentCell();
////			CellDefinition rightCell = cell.getRightParentCell();
////			if(leftCell==null || rightCell == null){
////				return;
////			}
////			List<CellDefinition> childrenCells=leftCell.getRowChildrenCells();
////			childrenCells.add(childCell);
////			addRowChildCell(leftCell,childCell);
////
////		}
////		//解决横向合并问题
////		if(!((cell.getRowNumber() + cell.getRowSpan()) >= childCell.getRowNumber())){
////			return;
////		}
//	}

	public void setReportParser(ReportParser reportParser) {
		this.reportParser = reportParser;
	}
	public void setReportBuilder(ReportBuilder reportBuilder) {
		this.reportBuilder = reportBuilder;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		reportProviders=applicationContext.getBeansOfType(ReportProvider.class).values();
	}
}
