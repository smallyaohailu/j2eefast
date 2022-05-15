/*!
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 * @author ZhouZhou
 */
(function ($) {
    $.extend({
        bttTable: {},
        // 表格树封装处理
        treeTable: {
            // 初始化表格
            init: function(options) {
                var defaults = {
                    id: "bootstrap-tree-table",
                    type: 1, // 0 代表bootstrapTable 1代表bootstrapTreeTable
                    height: 0,
                    rootIdValue: '0',       //树根节点 id
                    ajaxParams: {},
                    sortName: "",           //排序字段
                    sortOrder: "asc",       //默认升序
                    async: false,
                    toolbar: "toolbar",
                    striped: false,
                    pageSize: 12,              // 一页条数
                    pageList: [12, 24, 32],    // 分页数据库
                    expandColumn: 1,
                    showSearch: true,
                    showRefresh: true,
                    showColumns: true,
                    expandAll: true,
                    expandFirst: true,
                    bordered: false,
                    asynUrl: null,
                };
                var options = $.extend(defaults, options);

                //
                var flag = false;
                if(!opt.common.isEmpty(options.columns.length)){
                    for(var i=0; i<options.columns.length; i++ ){
                        if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"align"))){
                            options.columns[i].align = 'center';
                        }
                        if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"halign"))){
                            options.columns[i].halign = 'center';
                        }
                        if(opt.common.getJsonValue(options.columns[i],"field") === 'selectItem'){
                            flag = true;
                        }
                    }
                }

                if(!flag){
                    options.expandColumn = 0;
                }

                opt.table.options = options;
                opt.table.config[options.id] = options;
                $.bttTable = $('#' + options.id).bootstrapTreeTable({
                    id:  options.id,
                    code: options.code,                                 // 用于设置父子关系
                    parentCode: options.parentCode,                     // 用于设置父子关系
                    type: 'post',                                       // 请求方式（*）
                    url: options.url,                                   // 请求后台的URL（*）
                    async: options.async,                               // 是否异步加载数据
                    asynUrl: options.asynUrl,                           // 异步加载子数据请求URL
                    data: options.data,                                 // 无url时用于渲染的数据
                    ajaxParams: options.ajaxParams,                     // 请求数据的ajax的data属性
                    sortName: options.sortName,                         // 排序字段
                    sortOrder: options.sortOrder,                       // 默认升序
                    rootIdValue: options.rootIdValue,                   // 设置指定根节点id值
                    height: options.height,                             // 表格树的高度
                    pageSize: options.pageSize,                         // 一页条数
                    pageList: options.pageList,                         // 页面列表
                    expandColumn: options.expandColumn,                 // 在哪一列上面显示展开按钮
                    striped: options.striped,                           // 是否显示行间隔色
                    bordered: options.bordered,                         // 是否显示边框
                    toolbar: '#' + options.toolbar + '-' + options.id,  // 指定工作栏
                    showSearch: options.showSearch,                     // 是否显示检索信息
                    showRefresh: options.showRefresh,                   // 是否显示刷新按钮
                    showColumns: options.showColumns,                   // 是否显示隐藏某列下拉框
                    expandAll: options.expandAll,                       // 是否全部展开
                    expandFirst: options.expandFirst,                   // 是否默认第一级展开--expandAll为false时生效
                    columns: options.columns,                           // 显示列信息（*）
                    onClickRow: $.treeTable.onClickRow,                 // 单击某行事件
                    responseHandler: $.treeTable.responseHandler        // 当所有数据被加载时触发处理函数
                });
            },
            // 条件查询
            search: function(formId) {
                var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                var params = opt.common.formToJSON(currentId);
                params['__refre'] = true;
                //如果表单为空添加异步请求数据
                for(var o in params){
                    if(opt.common.isNotEmpty(params[o])
                        && o !== '__refre'){
                        params['__refre'] = false;
                        break;
                    }
                }
                $.bttTable.bootstrapTreeTable('refresh', params);
            },
            // 刷新
            refresh: function() {
                $.bttTable.bootstrapTreeTable('refresh', {'__refre':true});
            },
            // 查询表格树指定列值
            selectColumns: function(column) {
                var rows = $.map($.bttTable.bootstrapTreeTable('getSelections'), function (row) {
                    return row[column];
                });
                return opt.common.uniqueFn(rows);
            },
            //单击某行回调事件
            onClickRow: function(data){
                var toolbar = 'toolbar-'+ opt.table.get(this.id).id ;
                $('#' + toolbar + ' .multiple').toggleClass('disabled', false);
                $('#' + toolbar + ' .single').toggleClass('disabled', false);
                if (typeof opt.table.get(this.id).onClickRow == "function") {
                    opt.table.get(this.id).onClickRow(data);
                }
            },
            // 请求获取数据后处理回调函数，校验异常状态提醒
            responseHandler: function(data) {

                if (data.code != undefined && data.code != 0) {
                    opt.modal.error(data.msg);
                    return [];
                } else {
                    if (typeof opt.table.get(this.id).responseHandler == "function") {
                        opt.table.get(this.id).responseHandler(data);
                    }
                    return data;
                }
            },
        }
    })
})(jQuery);