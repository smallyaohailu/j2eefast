/*!
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 * @author ZhouZhou
 */
(function ($) {
    $.extend({
        table: {
            // 初始化表格参数
            init: function(options) {
                var defaults = {
                    formId:"",
                    id: "bootstrap-table",
                    type: 0, // 0 代表bootstrapTable 1代表bootstrapTreeTable
                    height: undefined,
                    sidePagination: "server",
                    sortName: "",
                    sortOrder: "desc",
                    pagination: true,
                    pageSize: 50,
                    pageList: [50, 100, 150],
                    showCheckNumber: false, //勾选显示总数
                    toolbar: "toolbar",
                    showToolbar: true,
                    striped: false,
                    escape: true,
                    outcheckbox:true, //是否开启检测toolbar有删除按钮 就默认使表格 checkbox:true 支持选中
                    firstLoad: true,
                    subtotalShowFooter: false,
                    rowTotal: undefined,
                    undefinedText:'-',
                    emptyText:'-',
                    search: false,
                    showSearch: false,
                    showPageGo: true,
                    showRefresh: false,
                    showColumns: true,
                    resizable: false,
                    showToggle: false,
                    cardView: false,
                    //是否开启自定义表格
                    showCustomView: false,
                    //是否开启表格导出 切exportData 导出函数没设置则以导出链接exportUrl 为导出链接，请求参数为表格请求参数
                    //如果设置了导出函数则直接回调函数自行处理下载文件
                    showExport: false,
                    exportData: undefined,
                    clickToSelect: true,
                    singleSelect: false,
                    paginationLoop: true,
                    /* 表格选中回调事件*/
                    onSelectData:undefined,
                    showPage: true,
                    mobileResponsive: true,
                    rememberSelected: true, /**默认记住我*/
                    _total:false, //是否需要合计表格
                    showPaginationSwitch: false,
                    fixedColumns: false,
                    onlyInfoPagination: false,
                    fixedNumber: 0,
                    rightFixedColumns: false,
                    totalFooterHeight: undefined,
                    subtotalRowStyle: undefined,
                    totalFooterRowStyle: undefined,
                    rightFixedNumber: 0,
                    queryParams: $.table.queryParams,
                    totalData:undefined, //服务返回合计对象
                    _totalColumns:[],
                    rowStyle: {},
                    showBorder: false,
                    isFixedColumn: false
                };

                if(options.resizable){
                    try{
                        if(0 !== _ColResizable){
                            opt.error("模板引擎缺少引入表格拖动Libs['resizable']")
                            return;
                        }
                    }catch (e) {
                        opt.error("模板引擎缺少引入表格拖动Libs['resizable']")
                        return;
                    }
                }
                var options = $.extend(defaults, options);
                //兼容自动识别有删除按钮表格有checkbox 选项
                if(options.outcheckbox && (!opt.common.isEmpty($('#' + options.toolbar+'-'+options.id + ' .multiple').html())
                    || !opt.common.isEmpty($('#' + options.toolbar +'-'+options.id+ ' .single').html()))){
                    var _flag = false;
                    if(!opt.common.isEmpty(options.columns.length)){
                        for(var i=0; i<options.columns.length; i++ ){
                            if(options.columns[i] instanceof Array){
                                for(var j=0; j<options.columns[i].length; j++) {
                                    if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i][j],"checkbox"))){
                                        _flag = true;
                                        break;
                                    }
                                }
                                if(_flag){
                                    break;
                                }
                            }else{
                                if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"checkbox"))){
                                    _flag = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!_flag){
                        //无表头信息
                        if(options.columns[0] instanceof Array){
                            options.columns[options.columns.length -1].splice(0,0,{checkbox: true, field: '_state'});
                        }else{
                            options.columns.splice(0,0,{checkbox: true, field: '_state'});
                        }
                    }
                }

                //
                if(!opt.common.isEmpty(options.columns.length)){
                    for(var i=0; i<options.columns.length; i++ ){
                        if(options.columns[0] instanceof Array){
                            for(var j=0; j<options.columns[i].length; j++) {
                                if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i][j],"align"))){
                                    options.columns[i][j].align = 'center';
                                }
                                if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i][j],"halign"))){
                                    options.columns[i][j].halign = 'center';
                                }
                                // 表格首列有checkbox 勾选字段名称必须state - 记住我必须是字段state
                                if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i][j],"checkbox"))){
                                    options.columns[i][j].field = '_state';
                                }
                                // 表格首列有radio 勾选字段名称必须state - 记住我必须是字段state
                                if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"radio"))){
                                    options.columns[i].field = '_state';
                                }
                            }
                        }else {
                            if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"align"))){
                                options.columns[i].align = 'center';
                            }
                            if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"halign"))){
                                options.columns[i].halign = 'center';
                            }
                            // 表格首列有checkbox 勾选字段名称必须state - 记住我必须是字段state
                            if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"checkbox"))){
                                options.columns[i].field = '_state';
                            }
                            // 表格首列有radio 勾选字段名称必须state - 记住我必须是字段state
                            if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"radio"))){
                                options.columns[i].field = '_state';
                            }
                        }
                    }
                }
                opt.table.options = options;
                opt.table.config[options.id] = options;
                $.table.initEvent();
                $('#' + options.id).bootstrapTable({
                    id: options.id,                                     // 对象ID
                    url: options.url,                                   // 请求后台的URL（*）
                    contentType: "application/x-www-form-urlencoded",   // 发送到服务器的数据编码类型
                    method: 'post',                                     // 请求方式（*）
                    cache: false,                                       // 是否使用缓存
                    height: options.height,                             // 表格的高度
                    striped: options.striped,                           // 是否显示行间隔色
                    sortable: true,                                     // 是否启用排序
                    sortStable: true,                                   // 设置为 true 将获得稳定的排序
                    sortName: options.sortName,                         // 排序列名称
                    sortOrder: options.sortOrder,                       // 排序方式  asc 或者 desc
                    pagination: options.pagination,                     // 设置为 true 会在表格底部显示分页条
                    paginationLoop: options.paginationLoop,             // 设置为 true 启用分页条无限循环的功能。
                    undefinedText:options.undefinedText,                // 当数据为 undefined 时显示的字符
                    emptyText:options.emptyText,                        // 当数据为 "" 空 时显示的字符  [修改源码 扩展字段]
                    showPaginationSwitch:options.showPaginationSwitch,  // 是否显示 数据条数选择框
                    pageNumber: 1,                                      // 初始化加载第一页，默认第一页
                    pageSize: options.pageSize,                         // 每页的记录行数（*）
                    showCustomView: options.showCustomView,             // 是否开启自定义表格
                    showCustomTalbe: options.showCustomTalbe,           // 初始化显示表格 默认false 显示自定义
                    customVIcon: options.customVIcon,                   // 右上角自定义图标
                    customTxt: options.customTxt,                       // 自定义图标按钮文字
                    customViewFormatter: options.customViewFormatter,   //
                    pageList: options.pageList,                         // 可供选择的每页的行数（*）
                    showCheckNumber: options.showCheckNumber,           // 勾选显示勾选的总数
                    firstLoad: options.firstLoad,                       // 是否首次请求加载数据，对于数据较大可以配置false
                    isFixedColumn: options.isFixedColumn,               // 是否固定列宽，当列比较多时，开启水平滚动，可设置为 true
                    escape: options.escape,                             // 转义HTML字符串
                    subtotalShowFooter: options.subtotalShowFooter,     // 是否统计显示表底部尾行 配合 footerFormatter 方法使用
                    rowTotal: options.rowTotal,                         // 小计多少个
                    subtotalRowStyle: options.subtotalRowStyle,         // 小计行样式
                    subtotalFooterStyle: options.subtotalFooterStyle,   // 小计单元格样式
                    totalShowFooter: options.totalShowFooter,           // 合计显示
                    totalFooterRowStyle: options.totalFooterRowStyle,   // 合计行样式
                    totalFooterHeight: options.totalFooterHeight,       // 底部合计高度
                    totalFooterStyle: options.totalFooterStyle,         // 合计函数
                    iconSize: 'outline',                                // 图标大小：undefined默认的按钮尺寸 xs超小按钮sm小按钮lg大按钮
                    toolbar: options.showToolbar ? ('#' +
                        options.toolbar + '-'+options.id) : null,        // 指定工作栏
                    sidePagination: options.sidePagination,             // server启用服务端分页client客户端分页
                    onlyInfoPagination: options.onlyInfoPagination,     //
                    search: options.search,                             // 是否显示搜索框功能
                    showBorder: options.showBorder,                     // 是否显示表格边框
                    showToolbar: options.showToolbar,                   // 是否显示工具类
                    //showExport: options.showExport,                     // 是否显示导出报表
                    searchText: options.searchText,                     // 搜索框初始显示的内容，默认为空
                    showSearch: options.showSearch,                     // 是否显示检索信息
                    showPageGo: options.showPageGo,               		// 是否显示跳转页
                    showPage: options.showPage,               		    // 是否显示左下角页码
                    showRefresh: options.showRefresh,                   // 是否显示刷新按钮
                    showColumns: options.showColumns,                   // 是否显示隐藏某列下拉框
                    showToggle: options.showToggle,                     // 是否显示详细视图和列表视图的切换按钮
                    showExport: options.showExport,                     // 是否支持导出文件
                    exportTxt: options.exportTxt,                       // 导出图标按钮文字
                    onCustom: options.onCustom,                         // 点击视图切换图标事件
                    cardView: options.cardView,                         // 卡片视图
                    exportData: $.table.exportData,                     // 导出函数
                    uniqueId: options.uniqueId,                         // 唯 一的标识符
                    clickToSelect: options.clickToSelect,				// 是否启用点击选中行
                    singleSelect: options.singleSelect,                 // 是否单选checkbox
                    mobileResponsive: options.mobileResponsive,         // 是否支持移动端适配
                    onCheck: options.onCheck,                           // 点击选择（check）此行时触发
                    onUncheck: options.onUncheck,                       // 当取消选择（uncheck）此行时触发
                    onCheckAll: options.onCheckAll,                     // 当全选行时触发
                    onUncheckAll: options.onUncheckAll,                 // 当取消全选行时触发
                    onClickRow: options.onClickRow,                     // 点击某行触发的事件
                    onDblClickRow: options.onDblClickRow,               // 双击某行触发的事件
                    onClickCell: options.onClickCell,                   // 单击某格触发的事件
                    onDblClickCell: options.onDblClickCell,             // 双击某格触发的事件
                    onEditableSave: options.onEditableSave,             // 行内编辑保存的事件
                    onExpandRow: options.onExpandRow,                   // 点击详细视图的事件
                    rememberSelected: options.rememberSelected,         // 启用翻页记住前面的选择
                    fixedColumns: options.fixedColumns,                 // 是否启用冻结列（左侧）
                    fixedNumber: options.fixedNumber,                   // 列冻结的个数（左侧）
                    resizable: options.resizable,                       // 是否允许拉伸列宽及增加表格线
                    rightFixedColumns: options.rightFixedColumns,       // 是否启用冻结列（右侧）
                    rightFixedNumber: options.rightFixedNumber,         // 列冻结的个数（右侧）
                    onReorderRow: options.onReorderRow,                 // 当拖拽结束后处理函数
                    queryParams: options.queryParams,                   // 传递参数（*）
                    rowStyle: options.rowStyle,                         // 通过自定义函数设置行样式
                    columns: options.columns,                           // 显示列信息（*）
                    responseHandler: $.table.responseHandler,           // 在加载服务器发送来的数据之前处理函数
                    onLoadSuccess: $.table.onLoadSuccess,               // 当所有数据被加载时触发处理函数
                    // exportOptions: options.exportOptions,               // 前端导出忽略列索引
                    detailView: options.detailView,                     // 是否启用显示细节视图
                    detailFormatter: options.detailFormatter,           // 在行下面展示其他数据列表
                });
            },
            // 获取实例ID，如存在多个返回#id1,#id2 delimeter分隔符
            getOptionsIds: function(separator) {
                var _separator = opt.common.isEmpty(separator) ? "," : separator;
                var optionsIds = "";
                $.each(opt.table.config, function(key, value){
                    optionsIds += "#" + key + _separator;
                });
                return optionsIds.substring(0, optionsIds.length - 1);
            },
            // 查询条件
            queryParams: function(params) {
                /*
                pageSize: 10
                pageNum: 1
                orderByColumn: createTime
                isAsc: desc
                dictName:
                dictType:
                status:
                params[beginTime]:
                params[endTime]:

                _search: false
                nd: 1576836627600
                limit: 50
                page: 1
                sidx: roleId
                order: asc
                * */
                var curParams = {
                    // 传递参数查询参数
                    __limit:       params.limit,
                    __page:        params.offset / params.limit + 1,
                    searchValue:    params.search,
                    __sidx:           params.sort,
                    __order:          params.order
                };
                opt.table.set(this.id);
                var currentId = opt.common.isEmpty(opt.table.options.formId) ? $('form').attr('id') : opt.table.options.formId;
                var json = {};
                $.each($("#" + currentId).serializeArray(), function(i, field) {
                    if(json[field.name]) {
                        json[field.name] += ("," + field.value);
                    } else {
                        json[field.name] = field.value;
                    }
                });
                //获取初始表单值保存缓存
                if(opt.table.options.__fromData__ == undefined){
                    opt.table.options.__fromData__ = json;
                }
                return $.extend(curParams, json);
            },
            //表格合计汇总
            tableTotalHtml:function(id){
                opt.table.set();
                var _v = "";
                if(!opt.common.isEmpty(opt.table.options.totalData)){
                    return "<span>"+opt.common.getJsonValue(opt.table.options.totalData,id)+"</span>";
                }else{
                    return "";
                }
            },
            // 请求获取数据后处理回调函数
            responseHandler: function(res) {
                if (typeof opt.table.get(this.id).responseHandler == "function") {
                    opt.table.get(this.id).responseHandler(res);
                }
                if (res.code == 0) {
                    if (opt.common.isNotEmpty(opt.table.options.sidePagination) && opt.table.options.sidePagination == 'client') {
                        return res.rows;
                    } else {
                        if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                            var column = opt.common.isEmpty(opt.table.options.uniqueId) ? opt.table.options.columns[1].field : opt.table.options.uniqueId;
                            $.each(res.data.list, function(i, row) {
                                if(opt.table.rememberSelectedIds[opt.table.options.id]){
                                    var _flag = false;
                                    for(var i=0; i<opt.table.rememberSelectedIds[opt.table.options.id].length; i++){
                                        if(opt.common.getJsonValue(row,column) == opt.table.rememberSelectedIds[opt.table.options.id][i]){
                                            _flag = true;
                                        }
                                    }
                                    if(_flag){
                                        //同步记住我的数据
                                        if(opt.common.isEmpty(opt.table.rememberSelecteds[opt.table.options.id])){
                                            opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], row,column);
                                        }else{
                                            var _f = true;
                                            for(var k=0; k< opt.table.rememberSelecteds[opt.table.options.id].length; k++){
                                                if(opt.common.getJsonValue(row,column) == opt.common.getJsonValue(opt.table.rememberSelecteds[opt.table.options.id][k],column)){
                                                    _f = false;
                                                    break;
                                                }
                                            }
                                            if(_f){
                                                var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                                                opt.table.rememberSelecteds[opt.table.options.id] = _['union'](selectedRows, row,column);
                                            }
                                        }
                                    }
                                    row._state = _flag;
                                }else{
                                    row._state = false;
                                }
                            })
                        }
                        if(opt.common.isNotEmpty(opt.table.options._total) && opt.table.options._total){
                            opt.table.options.totalData = res.pageTotal; //服务返回合计对象
                        }
                        return { rows: res.data.list, total: res.data.totalCount };
                    }
                } else {
                    opt.modal.error(res.msg,function () {
                        try {
                            opt.closeItem();
                        }catch (e) {
                        }
                    });
                    return { rows: [], total: 0 };
                }
            },
            // 初始化事件
            initEvent: function() {
                // 实例ID信息
                var optionsIds = $.table.getOptionsIds();
                // 监听事件处理
                $(optionsIds).on(TABLE_EVENTS, function () {
                    opt.table.set(this.id);
                });

                // 选中、取消、全部选中、全部取消（事件）
                $(optionsIds).on("check.bs.table check-all.bs.table uncheck.bs.table uncheck-all.bs.table", function (e, rows) {
                    // 复选框分页保留保存选中数组
                    var rowIds = $.table.affectedRowIds(rows);
                    if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                        //获取到当前页面数据
                        var _trows = $.map($("#" + opt.table.options.id).bootstrapTable('getAllSelections'), function (row) {
                            return row;
                        });
                        var selectedIds = opt.table.rememberSelectedIds[opt.table.options.id];
                        var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                        if(opt.common.isEmpty(selectedIds) && opt.common.isEmpty(selectedRows) && _trows.length >= 1){
                            opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], _trows,opt.table.options.uniqueId);
                            opt.table.rememberSelectedIds[opt.table.options.id] = _['union']([],  $.table.affectedRowIds(_trows),opt.table.options.uniqueId);
                        }
                        if(!opt.table.options.singleSelect){
                            var column = opt.common.isEmpty(opt.table.options.uniqueId) ? opt.table.options.columns[1].field : opt.table.options.uniqueId,
                                //判断勾选使用合并函数-反选使用剔除元素
                                func = $.inArray(e.type, ['check', 'check-all']) > -1 ? 'union' : 'difference';
                            selectedIds = opt.table.rememberSelectedIds[opt.table.options.id];
                            if(opt.common.isNotEmpty(selectedIds)) {
                                opt.table.rememberSelectedIds[opt.table.options.id] = _[func](selectedIds, rowIds,column);
                            } else {
                                opt.table.rememberSelectedIds[opt.table.options.id] = _[func]([], rowIds,column);
                            }
                            selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                            if(opt.common.isNotEmpty(selectedRows)) {
                                opt.table.rememberSelecteds[opt.table.options.id] = _[func](selectedRows, rows,column);
                            } else {
                                opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], _trows,column);
                            }
                            if (opt.table.options.showCheckNumber) {
                                $("#" + opt.table.options.id).bootstrapTable('setCheckNumer',opt.table.rememberSelectedIds[opt.table.options.id].length);
                            }
                        }else{
                            opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], _trows,opt.table.options.uniqueId);
                            opt.table.rememberSelectedIds[opt.table.options.id] = _['union']([],  $.table.affectedRowIds(_trows),opt.table.options.uniqueId);
                        }
                    }

                    if (typeof opt.table.get(opt.table.options.id).onSelectData == "function") {
                        if(opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected){
                            opt.table.get(opt.table.options.id).onSelectData(opt.table.rememberSelecteds[opt.table.options.id]);
                        }else{
                            opt.table.get(opt.table.options.id).onSelectData(rows);
                        }
                    }
                });
                // 加载成功、选中、取消、全部选中、全部取消（事件）
                $(optionsIds).on("check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table load-success.bs.table", function (e) {
                    var toolbar = opt.table.options.toolbar + '-'+ opt.table.options.id ,
                        uniqueId = opt.table.options.uniqueId,
                        // 工具栏按钮控制
                        rows = opt.common.isEmpty(uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(uniqueId);
                    // 非多个禁用
                    $('#' + toolbar + ' .multiple').toggleClass('disabled', !rows.length);
                    // 非单个禁用
                    $('#' + toolbar + ' .single').toggleClass('disabled', rows.length!=1);

                });
                // 图片预览事件
                $(optionsIds).off("click").on("click", '.img-circle', function() {
                    var src = $(this).attr('src');
                    var target = $(this).data('target');
                    var height = $(this).data('height') == 'auto'?'':$(this).data('height');
                    var name = $(this).data('name');
                    var width = $(this).data('width') == 'auto'?'':$(this).data('width');
                    if(opt.common.equals("self", target)) {
                        opt.modal.openView(name,src,width,height);
                    } else if (opt.common.equals("blank", target)) {
                        window.open(src);
                    }
                });
                // 单击tooltip事件
                $(optionsIds).on("click", '.tooltip-show', function() {
                    var target = $(this).data('target');
                    var input = $(this).prev();
                    if (opt.common.equals("open", target)) {
                        opt.selfLayer.alert(input.val(), {
                            title: "信息内容",
                            shadeClose: true,
                            btn: ['确认'],
                            btnclass: ['btn btn-primary'],
                        });
                    }
                });
            },
            // 当所有数据被加载时触发
            onLoadSuccess: function(data) {

                if (typeof opt.table.get(this.id).onLoadSuccess == "function") {
                    opt.table.get(this.id).onLoadSuccess(data);
                }

                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) &&
                    opt.table.options.rememberSelected && opt.table.options.showCheckNumber) {
                    $("#" + this.id).bootstrapTable('setCheckNumer', opt.table.rememberSelectedIds[opt.table.options.id] ?
                        opt.table.rememberSelectedIds[opt.table.options.id].length : 0);
                }

                // 浮动提示框特效
                $(".table [data-toggle='tooltip']").tooltip();

                //加载合计统计
                if(opt.common.isNotEmpty(opt.table.options._total) && opt.table.options._total){
                    var _p = "汇总:&nbsp;&nbsp;"
                    for(var i=0; i<opt.table.options._totalColumns.length;i++){
                        _p += opt.table.options._totalColumns[i].title;
                        _p += ":"
                        _p += opt.common.isEmpty(opt.common.getJsonValue(opt.table.options.totalData,opt.table.options._totalColumns[i].field))?"0.00":
                            opt.common.getJsonValue(opt.table.options.totalData,opt.table.options._totalColumns[i].field) + "&nbsp;&nbsp;"
                    }
                    $("#_pageTotal").html(_p);
                }

                $('.table [data-toggle="popover"]').popover();

                if ($.fn.iCheck !== undefined) {
                    $(".check-box:not(.noicheck),.radio-box:not(.noicheck)").each(function() {
                        $(this).iCheck({
                            checkboxClass: (typeof($(this).data("style")) == "undefined")?'icheckbox-blue':("icheckbox_" +($(this).data("style") || "square-blue")),
                            radioClass:(typeof($(this).data("style")) == "undefined")? 'iradio-blue':("iradio_" +($(this).data("style") || "square-blue"))
                        })
                    })
                }

                // laydate time-input 时间控件绑定
                if ($(".table .time-input").length > 0) {
                    // layui.use('laydate', function () {
                    //     var com = layui.laydate;
                    //
                    // });
                    $(".time-input").each(function (index, item) {
                        var time = $(item);
                        // 控制控件外观
                        var type = time.attr("data-type") || 'date';
                        // 控制回显格式
                        var format = time.attr("data-format") || 'yyyy-MM-dd';
                        // 控制日期控件按钮
                        var buttons = time.attr("data-btn") || 'clear|now|confirm', newBtnArr = [];
                        // 日期控件选择完成后回调处理
                        var callback = time.attr("data-callback") || {};
                        if (buttons) {
                            if (buttons.indexOf("|") > 0) {
                                var btnArr = buttons.split("|"), btnLen = btnArr.length;
                                for (var j = 0; j < btnLen; j++) {
                                    if ("clear" === btnArr[j] || "now" === btnArr[j] || "confirm" === btnArr[j]) {
                                        newBtnArr.push(btnArr[j]);
                                    }
                                }
                            } else {
                                if ("clear" === buttons || "now" === buttons || "confirm" === buttons) {
                                    newBtnArr.push(buttons);
                                }
                            }
                        } else {
                            newBtnArr = ['clear', 'now', 'confirm'];
                        }
                        laydate.render({
                            elem: item,
                            theme: 'molv',
                            trigger: 'click',
                            type: type,
                            format: format,
                            btns: newBtnArr,
                            done: function (value, data) {
                                if (typeof window[callback] != 'undefined'
                                    && window[callback] instanceof Function) {
                                    window[callback](value, data);
                                }
                            }
                        });
                    });
                }

                //select2复选框事件绑定
                if ($.fn.select2 !== undefined) {
                    $.fn.select2.defaults.set( "theme", "bootstrap" );
                    $("select.form-control:not(.noselect2)").each(function () {
                        if(typeof($(this).attr("multiple"))=="undefined"){
                            $(this).select2();
                        }else{
                            $(this).select2({allowClear: true, placeholder: ""});
                        }
                    })
                }

                //设置溢出文本复制
                $.contextMenu({
                    selector: ".table a[data-target='copy'],.table span[data-target='copy']",
                    trigger: 'right',
                    autoHide: true,
                    items: {
                        "copy": {
                            name: $.i18n.prop("复制文本"),
                            icon: "fa-copy",
                            callback: function(key, opt) {
                                var input = $(this).prev();
                                input.select();
                                if(document.execCommand("copy")){
                                    window.opt.modal.msg($.i18n.prop('复制成功')+'!');
                                }
                            }
                        }
                    }
                });

                // $(window).on('resize', function () {
                //     // 浮动提示框特效
                //     //$(".table [data-toggle='tooltip']").tooltip();
                //
                //     $('[data-toggle="popover"]').popover();
                //
                //     if ($.fn.iCheck !== undefined) {
                //         $(".check-box:not(.noicheck),.radio-box:not(.noicheck)").each(function() {
                //             $(this).iCheck({
                //                 checkboxClass: (typeof($(this).data("style")) == "undefined")?'icheckbox-blue':("icheckbox_" +($(this).data("style") || "square-blue")),
                //                 radioClass:(typeof($(this).data("style")) == "undefined")? 'iradio-blue':("iradio_" +($(this).data("style") || "square-blue"))
                //             })
                //         })
                //     }
                //
                //     //select2复选框事件绑定
                //     if ($.fn.select2 !== undefined) {
                //         $.fn.select2.defaults.set( "theme", "bootstrap" );
                //         $("select.form-control:not(.noselect2)").each(function () {
                //             if(typeof($(this).attr("multiple"))=="undefined"){
                //                 $(this).select2();
                //             }else{
                //                 $(this).select2({allowClear: true, placeholder: ""});
                //             }
                //         })
                //     }
                //
                // }).resize();

            },
            // 表格销毁
            destroy: function (tableId) {
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('destroy');
                delete opt.table.rememberSelectedIds[tableId];
                delete opt.table.rememberSelecteds[tableId];
            },
            // 序列号生成
            serialNumber: function (index, tableId) {
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                var tableParams = $("#" + currentId).bootstrapTable('getOptions');
                var pageSize = tableParams.pageSize;
                var pageNumber = tableParams.pageNumber;
                return pageSize * (pageNumber - 1) + index + 1;
            },
            // 下拉按钮切换
            dropdownToggle: function (value) {
                var actions = [];
                actions.push('<div class="btn-group">');
                actions.push('<button type="button" class="btn btn-xs dropdown-toggle" data-toggle="dropdown" aria-expanded="false">');
                actions.push('<i class="fa fa-cog"></i>&nbsp;<span class="fa fa-chevron-down"></span></button>');
                actions.push('<ul class="dropdown-menu">');
                actions.push(value.replace(/<a/g,"<li><a").replace(/<\/a>/g,"</a></li>"));
                actions.push('</ul>');
                actions.push('</div>');
                return actions.join('');
            },
            // 图片预览
            imageView: function (value, height, width, target) {
                if (opt.common.isEmpty(width)) {
                    width = 'auto';
                }
                if (opt.common.isEmpty(height)) {
                    height = 'auto';
                }
                // blank or self
                var _target = opt.common.isEmpty(target) ? 'self' : target;
                if (opt.common.isNotEmpty(value)) {
                    //value = opt.common.optimizationPath(value);
                    var name = opt.common.fileFromPath(value);
                    ///fast/sys/comm/fileAvatarView?filePath=/avatar/2020/12/25/7325bc151faf46538733237d97bf9270.png
                    return opt.common.sprintf("<img class='img-circle img-xs' data-height='%s' data-width='%s' data-target='%s' src='%s' data-name='%s'/>", height, width, _target, value,name);
                } else {
                    return opt.common.nullToStr(value);
                }
            },
            // 搜索-默认第一个form
            /**
             * 优先搜索传入表单from 如果没有传入则会查找当前表格是否指定表单from 如果有指定则搜索指定from表单,否则搜索第一个表单
             * @param formId 搜索表单ID
             * @param tableId 表格ID
             * @param data 附加数据
             */
            search: function(tableId, formId,data) {
                opt.table.set(tableId);
                // var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                var currentId;
                if(opt.common.isEmpty(formId)){
                    if(opt.common.isEmpty(opt.table.options.formId)){
                        currentId = $('form').attr('id');
                    }else{
                        currentId = opt.table.options.formId;
                    }
                }else{
                    currentId = formId;
                }

                //查询是否有更多按钮
                var butform = $('#'+currentId).find('a[data-toggle="formMore"]');
                var formMore = $('#'+currentId).find('div.select-list-more');
                if(opt.common.isNotEmpty(butform) &&
                    opt.common.isNotEmpty(formMore) &&
                    formMore.css('display')){
                    var show = formMore.css('display');
                    var html = [];
                    if(show == 'block'){
                        formMore.toggle('normal');
                        html.push('<i class="fa fa-angle-double-down"></i>&nbsp;',$.i18n.prop("更多"));
                        butform.empty().append(html.join(''));
                    }
                }

                var params = opt.common.isEmpty(tableId) ? $("#" + opt.table.options.id).bootstrapTable('getOptions') : $("#" + tableId).bootstrapTable('getOptions');
                params.queryParams = function(params) {
                    var search = opt.common.formToJSON(currentId);
                    if(opt.common.isNotEmpty(data)){
                        $.each(data, function(key) {
                            search[key] = data[key];
                        });
                    }
                    search.__limit = params.limit;
                    search.__page = params.offset / params.limit + 1;
                    search.searchValue = params.search;
                    search.__sidx = params.sort;
                    search.__order = params.order;
                    return search;
                }

                if(opt.common.isNotEmpty(tableId)){
                    $("#" + tableId).bootstrapTable('refresh', params);
                } else{
                    $("#" + opt.table.options.id).bootstrapTable('refresh', params);
                }
            },

            //导出表格
            exportData: function(){
                var currentId;
                if(opt.common.isEmpty(opt.table.options.formId)){
                    currentId = $('form').attr('id');
                }else{
                    currentId = opt.table.options.formId;
                }
                if (typeof opt.table.get(this.id).exportData == "function") {
                    opt.modal.loading("正在导出数据，请稍后...");
                    opt.table.get(this.id).exportData(this.id,$("#" + currentId).serializeArray());
                    window.setTimeout(function(){
                        opt.modal.closeLoading();
                    },500);
                }else{
                    opt.table.set(this.id);
                    if(opt.common.isEmpty(opt.table.options.exportUrl)){
                        opt.error('导出地址exportUrl为空,请检查配置');
                        return;
                    }
                    opt.modal.confirm("根据上方查询导出数据,确定导出" + opt.table.options.modalName + "数据吗？", function() {
                        opt.modal.loading("正在导出数据，请稍后...");
                        var currentId;
                        if(opt.common.isEmpty(opt.table.options.formId)){
                            currentId = $('form').attr('id');
                        }else{
                            currentId = opt.table.options.formId;
                        }
                        var datas = $("#" + currentId).serializeArray();
                        opt.common.postDownLoadFile(datas,opt.table.options.exportUrl);
                        window.setTimeout(function(){
                            opt.modal.closeLoading();
                        },500);
                    });
                }
            },
            /**
             * 导出Excel 表格
             * @param formId 导出表单ID 如果不传则获取默认页面第一个
             */
            exportExcel: function(formId) {
                opt.table.set();
                opt.modal.confirm("确定导出所有" + opt.table.options.modalName + "吗？", function() {
                    var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                    opt.modal.loading("正在导出数据，请稍后...");
                    var config = {
                        type: "POST",
                        url: opt.table.options.exportUrl,
                        data: $("#" + currentId).serializeArray(),
                        success: function(result) {
                            if (result.code ==opt.variable.web_status.SUCCESS) {
                                window.location.href = baseURL + "excel/download?fileName=" + encodeURI(result.msg);
                            } else if (result.code == opt.variable.web_status.WARNING) {
                                opt.modal.alertWarning(result.msg)
                            } else {
                                opt.modal.alertError(result.msg);
                            }
                            opt.modal.closeLoading();
                        }
                    }
                    $.ajax(config);
                });
            },
            // 下载模板
            importTemplate: function(fileName) {
                fileName = window.encodeURI(fileName);
                opt.common.downLoadFile(baseURL + "excel/download?fileName=" +fileName);
            },
            // 导入数据
            importExcel: function(tplId,title,url) {
                opt.table.set();
                var currentId = opt.common.isEmpty(tplId) ? 'importTpl' : tplId;
                opt.layer.open({
                    type: 1,
                    area: ['580px', '200px'],
                    resize: false,
                    scrollbar: true,
                    //shade: 0.3,
                    title: '<i class="fa fa-upload"></i> 导入' +(opt.common.isEmpty(title) ? opt.table.options.modalName : (title + '数据')),
                    content: opt.template(currentId),
                    btn: ['<i class="fa fa-check"></i> 导入', '<i class="fa fa-remove"></i> 取消'],
                    // 弹层外区域关闭
                    shadeClose: true,
                    success: function(layero, index){
                        //解决同一个文件二次上传无效的问题
                        layero.find('#file').off('click').on('click',function(e){
                            e.target.value = '';
                        })
                    },
                    btn1: function(index, layero){
                        var file = layero.find('#file').val();
                        if (file == '' || (!opt.common.endWith(file, '.xls') && !opt.common.endWith(file, '.xlsx'))){
                            opt.warning("请选择后缀为 “xls”或“xlsx”的文件。");
                            return false;
                        }
                        parent.opt.modal.loading();
                        var formData = new FormData(layero.find('form')[0]);
                        var config = {
                            url: opt.common.isEmpty(url)?opt.table.options.importUrl:url,
                            data: formData,
                            cache: false,
                            contentType: false,
                            processData: false,
                            type: 'POST',
                            success: function (result) {
                                if (result.code == opt.variable.web_status.SUCCESS) {
                                    parent.opt.modal.closeLoading();
                                    opt.success(result.msg);
                                    $.table.refresh();
                                    opt.layer.close(index);
                                } else if (result.code == opt.variable.web_status.WARNING) {
                                    opt.error(result.msg);
                                    parent.opt.modal.closeLoading();
                                    //opt.layer.close(index);
                                } else {
                                    opt.error(result.msg);
                                    parent.opt.modal.closeLoading();
                                    //opt.layer.close(index);
                                }
                            },
                            error: function(xhr, textStatus) {
                                opt.error(JSON.parse(xhr.responseText).msg || xhr.responseText );
                                parent.opt.modal.closeLoading();
                                return;
                            }
                        };
                        $.ajax(config);
                    }
                });
            },
            // 刷新表格
            refresh: function(tableId) {
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('refresh', {
                    silent: true
                });
            },
            //根据行uniqueId值 查询整行数据
            getRowByUniqueId: function(uniqueId,tableId){
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                return $("#" + currentId).bootstrapTable('getRowByUniqueId',uniqueId);
            },
            // 查询表格指定列值
            selectColumns: function(column) {
                var rows = $.map($("#" + opt.table.options.id).bootstrapTable('getSelections'), function (row) {
                    var value = row;
                    var props = column.split('.');
                    for (var p in props) {
                        value = value && value[props[p]];
                    }
                    return value;
                });
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                    if(opt.common.isNotEmpty(selectedRows)) {
                        rows = $.map(opt.table.rememberSelecteds[opt.table.options.id], function (row) {
                            var value = row;
                            var props = column.split('.');
                            for (var p in props) {
                                value = value && value[props[p]];
                            }
                            return value;
                        });
                    }
                }
                return opt.common.uniqueFn(rows);
            },
            // 获取当前页选中或者取消的行ID
            affectedRowIds: function(rows) {
                var field = opt.common.isEmpty(opt.table.options.uniqueId) ? opt.table.options.columns[1].field : opt.table.options.uniqueId;
                var rowIds;
                if (rows instanceof Array) {
                    rowIds = $.map(rows, function(row) {
                        var value = row;
                        var props = field.split('.');
                        for (var p in props) {
                            value = value && value[props[p]];
                        }
                        return value;
                    });
                } else {
                    rowIds = rows;
                    var props = field.split('.');
                    for (var p in props) {
                        rowIds = rowIds && rowIds[props[p]];
                    }
                    rowIds = [rowIds];
                }
                return rowIds;
            },
            // 查询表格首列值 -- 实际就是第二列 去掉了前面的 勾选 与序列
            selectFirstColumns: function(tableId) {
                if(opt.common.isNotEmpty(tableId)){
                    opt.table.set(tableId);
                }
                var rows = $.map($("#" + opt.table.options.id).bootstrapTable('getSelections'), function (row) {
                    return row[opt.table.options.columns[1].field];
                });
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                    if(opt.common.isNotEmpty(selectedRows)) {
                        rows = $.map(selectedRows, function (row) {
                            return row[opt.table.options.columns[1].field];
                        });
                    }
                }
                return opt.common.uniqueFn(rows);
            },
            //获取选中行 id 集合
            selectAllColumns :function(tableId){
                if(opt.common.isNotEmpty(tableId)){
                    opt.table.set(tableId);
                }
                var rows;
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                    if(opt.common.isNotEmpty(selectedRows)) {
                        rows = $.map(selectedRows, function (row) {
                            return opt.common.getJsonValue(row,opt.table.options.uniqueId);
                        });
                    }else{
                        rows = [];
                    }
                }
                //若没有记住我或者记住我内部无值 则获取页面，如果有值则获取记住我,因为记住我里面的值为所有,不单单是当前页还有其他页记住的数据
                if(opt.common.isEmpty(rows) || rows.length == 0){
                    rows = $.map($("#" + opt.table.options.id).bootstrapTable('getAllSelections'), function (row) {
                        return row;
                    });
                    // if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    //     opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], rows,opt.table.options.uniqueId);
                    //     opt.table.rememberSelectedIds[opt.table.options.id] = _['union']([], $.table.affectedRowIds(rows),opt.table.options.uniqueId);
                    // }
                    rows = $.table.affectedRowIds(rows);
                }
                return opt.common.uniqueFn(rows);
            },
            //清除勾选记住数据
            clearRememberSelected:function(tableId){
                opt.table.set(tableId);
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    delete opt.table.rememberSelectedIds[opt.table.options.id];
                    delete opt.table.rememberSelecteds[opt.table.options.id];
                }
            },
            //获取选中行对象集合
            selectAllColumnRows: function(tableId){
                if(opt.common.isNotEmpty(tableId)){
                    opt.table.set(tableId);
                }
                var rows;
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                    if(opt.common.isNotEmpty(selectedRows)) {
                        return selectedRows;
                    }else{
                        rows = [];
                    }
                }
                //若没有记住我或者记住我内部无值 则获取页面，如果有值则获取记住我,因为记住我里面的值为所有,不单单是当前页还有其他页记住的数据
                if(opt.common.isEmpty(rows) || rows.length == 0){
                    rows = $.map($("#" + opt.table.options.id).bootstrapTable('getAllSelections'), function (row) {
                        return row;
                    });
                    // if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    //     opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], rows,opt.table.options.uniqueId);
                    //     opt.table.rememberSelectedIds[opt.table.options.id] = _['union']([], $.table.affectedRowIds(rows),opt.table.options.uniqueId);
                    // }
                    return rows;
                }
            },
            // 显示表格指定列
            showColumn: function(column, tableId) {
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('showColumn', column);
            },
            // 隐藏表格指定列
            hideColumn: function(column, tableId) {
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('hideColumn', column);
            },
            //取消选择某一行，索引（index）从0开始
            uncheck: function(index,tableId){
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('uncheck', index);
            },
            // 更新表格数据
            updataData: function(count,tableId){
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                // 创建一个数组,用来存放表格中所有的数据
                var params = [];
                for(var dataIndex=0;dataIndex<=count;dataIndex++) {
                    // 使用JQuery选择器找到td标签(也就是包含input标签的标签)对象,这种选法选中了很多td标签,因此需要遍历
                    var columns = $('#'+currentId+' tr[data-index="'+dataIndex+'"] td');
                    // 创建一个对象.用来存放表格中一行的数据
                    var obj = {};
                    for (var i=0; i < columns.length; i++) {
                        var inputValue = $(columns[i]).find('input');
                        if(inputValue.length == 1){
                            var selectValue = $(columns[i]).find('select');
                            var key = opt.table.options.columns[i].field;
                            if (opt.common.isNotEmpty(inputValue.val())) {
                                obj[key] = inputValue.val();
                            } else if (opt.common.isNotEmpty(selectValue.val())) {
                                obj[key] = selectValue.val();
                            } else {
                                obj[key] = "";
                            }
                        }else{
                            var selectValue = $(columns[i]).find('select');
                            var key = opt.table.options.columns[i].field;//use.id
                            if (opt.common.isNotEmpty($(inputValue[0]).val())) {
                                var names = $(inputValue[1]).attr('name').split('.');
                                if (names.length > 1) {
                                    obj[key] = $(inputValue[0]).val();
                                    var str = names;
                                    var obj0 = {},idx = str.length -1;
                                    while(idx>-1){
                                        var temp = {};
                                        temp[str[idx]] = obj0;
                                        obj0 = temp;
                                        if(idx == (str.length -1)){
                                            obj0[str[idx]] = $(inputValue[1]).val();
                                        }
                                        idx--;
                                    }
                                    obj = opt.common.extend(obj0,obj);
                                }else{
                                    obj[key] = $(inputValue[0]).val();
                                    obj[$(inputValue[1]).attr('name')] = $(inputValue[1]).val();
                                }
                            } else if (opt.common.isNotEmpty(selectValue.val())) {
                                obj[key] = selectValue.val();
                            } else {
                                obj[key] = "";
                            }
                        }

                    }
                    /*
                    * {index:索引值, row:每行索引对应的数据(是一个对象)}
                    * 表格中每一行的数据
                    * {index:0, row:{id: "1", name: "XXX", age: "18", address: "XX"}}
                    * {index:1, row:{id: "1", name: "XXX", age: "20", address: "XX"}}
                    * */
                    params.push({index:dataIndex, row:obj});
                }
                // 对表格中的数据进行更新
                $('#' + currentId).bootstrapTable("updateRow",params);
            },
            //删除选中行
            removeData: function(index,tableId) {
                var that = this;
                opt.table.set();
                index = opt.common.isEmpty(index) ? 'index' : index;
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                var rows = $('#' + currentId).bootstrapTable('getSelections');//获取选中行
                if (rows.length == 0) {
                    opt.modal.error($.i18n.prop("请选择要删除的数据"));
                    return;
                }
                var count = $("#" + currentId).bootstrapTable('getData').length;
                that.updataData(count);
                var indexs = [];
                for (var i = 0; i < rows.length; i++) {
                    indexs[i] = rows[i][index];
                }
                $('#' + opt.table.options.id).bootstrapTable('remove', {
                    field: index,
                    values: indexs
                });
            },
            //动态插入数据
            addColumn: function(row,callback,tableId){
                var that = this;
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                var count = $("#" + currentId).bootstrapTable('getData').length;
                that.updataData(count);
                $("#" + currentId).bootstrapTable('insertRow', {
                    index: count + 1,
                    row: row
                });

                // laydate time-input 时间控件绑定
                if ($(".time-input").length > 0) {
                    // layui.use('laydate', function () {
                    //     var com = layui.laydate;
                    // });
                    $(".time-input").each(function (index, item) {
                        var time = $(item);
                        // 控制控件外观
                        var type = time.attr("data-type") || 'date';
                        // 控制回显格式
                        var format = time.attr("data-format") || 'yyyy-MM-dd';
                        // 控制日期控件按钮
                        var buttons = time.attr("data-btn") || 'clear|now|confirm', newBtnArr = [];
                        // 日期控件选择完成后回调处理
                        var callback = time.attr("data-callback") || {};
                        if (buttons) {
                            if (buttons.indexOf("|") > 0) {
                                var btnArr = buttons.split("|"), btnLen = btnArr.length;
                                for (var j = 0; j < btnLen; j++) {
                                    if ("clear" === btnArr[j] || "now" === btnArr[j] || "confirm" === btnArr[j]) {
                                        newBtnArr.push(btnArr[j]);
                                    }
                                }
                            } else {
                                if ("clear" === buttons || "now" === buttons || "confirm" === buttons) {
                                    newBtnArr.push(buttons);
                                }
                            }
                        } else {
                            newBtnArr = ['clear', 'now', 'confirm'];
                        }
                        laydate.render({
                            elem: item,
                            theme: 'molv',
                            trigger: 'click',
                            type: type,
                            format: format,
                            btns: newBtnArr,
                            done: function (value, data) {
                                if (typeof window[callback] != 'undefined'
                                    && window[callback] instanceof Function) {
                                    window[callback](value, data);
                                }
                            }
                        });
                    });
                }

                // select2复选框事件绑定
                if ($.fn.select2 !== undefined) {
                    $.fn.select2.defaults.set( "theme", "bootstrap" );
                    $("select.form-control:not(.noselect2)").each(function () {
                        //     .on("change", function () {
                        //     $(this).valid();
                        // })
                        if(typeof($(this).attr("multiple"))=="undefined"){
                            $(this).select2({allowClear: false, placeholder: "请点击选择"});
                        }else{
                            $(this).select2({allowClear: true, placeholder: ""});
                        }
                    })
                }

                if(typeof callback == "function"){
                    callback(row);
                }
            }
        }
    })
})(jQuery);