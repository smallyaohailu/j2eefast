/*!
 * 树封装
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 * @author ZhouZhou
 */
(function ($) {
    $.extend({
        _tree: {},
        tree: {
            _option: {},
            _lastValue: {},
            // 初始化树结构
            init: function(options) {
                var defaults = {
                    ajxType: "GET",
                    ajaxParams:{},
                    id: "tree",                    // 属性ID
                    expandLevel: 0,                // 展开等级节点 0-默认 1-展开根节点(不包含子节点) 2-展开所有节点
                    view: {
                        selectedMulti: false,      // 设置是否允许同时选中多个节点
                        nameIsHTML: true           // 设置 name 属性是否支持 HTML 脚本
                    },
                    check: {
                        enable: true,             // 置 zTree 的节点上是否显示 checkbox / radio
                        radioType: "level",
                        chkStyle: "radio",
                        nocheckInherit: true      // 设置子节点是否自动继承
                    },
                    data: {
                        key: {
                            title: "name",         // 节点数据保存节点提示信息的属性名称
                            name: "title"
                        },
                        simpleData: {
                            enable: true           // true / false 分别表示 使用 / 不使用 简单数据模式
                        }
                    },
                    displayLen: 0
                };
                var options = $.extend(defaults, options);
                $.tree._option = options;
                // 树结构初始化加载
                var setting = {
                    callback: {
                        onClick: options.onClick,                      // 用于捕获节点被点击的事件回调函数
                        onCheck: options.onCheck,                      // 用于捕获 checkbox / radio 被勾选 或 取消勾选的事件回调函数
                        onDblClick: options.onDblClick,                // 用于捕获鼠标双击之后的事件回调函数
                        onCollapse: options.OnCollapse,                 // 用于捕获节点被折叠的事件回调函数
                        onExpand: options.OnExpand,                      // 用于捕获节点被展开的事件回调函数
                        beforeClick: options.beforeClick
                    },
                    check: options.check,
                    view: options.view,
                    data: options.data,

                };


                var config = {
                    url: options.url,
                    type: options.ajxType,
                    dataType: "JSON",
                    data: options.ajaxParams,
                    success: function(data) {

                        //兼容返回数据
                        var list;

                        if(opt.common.isArray(data)){
                            list = data;
                        }else{
                            if(data.code == opt.variable.web_status.SUCCESS){
                                for(var key  in data){
                                    if(opt.common.isArray(data[key])){
                                        list = data[key];
                                    }
                                }
                                for(var i=0; i<list.length; i++){
                                    for(var key  in list[i]){
                                        if(key == "url"){
                                            delete list[i][key];
                                        }
                                    }
                                }
                            }else{
                                opt.modal.error(data.msg);
                                return;
                            }
                        }

                        if(options.displayLen != 0){
                            for(var i=0; i<list.length; i++){
                                for(var key  in list[i]){
                                    if(key == "title"){
                                        list[i][key] = opt.common.subString(list[i][key],options.displayLen,true);
                                    }
                                }
                            }
                        }

                        var treeId = $("#treeId").val();
                        tree = $.fn.zTree.init($("#" + options.id), setting, list);
                        $._tree = tree;
                        // var nodes = tree.getNodesByParam("level", options.expandLevel - 1);
                        //
                        // for (var i = 0; i < nodes.length; i++) {
                        //     tree.expandNode(nodes[i], true, false, false);
                        // }
                        //展开根节点 不包含子节点
                        if(options.expandLevel == 1){
                            var node = tree.getNodesByFilter(function (node) { return node.level == 0 }, true);
                            tree.expandNode(node, true, null, null);
                        }
                        //展开所有节点
                        if(options.expandLevel == 2){
                            tree.expandAll(true);
                        }
                        //
                        // if(options.check.enable){
                        if(!opt.common.isEmpty(options._list)){
                            var _l = options._list.split(",");
                            for(var i=0; i<_l.length; i++){
                                var node = tree.getNodeByParam(opt.common.isEmpty(options.data.simpleData.idKey)?
                                    "id":options.data.simpleData.idKey, _l[i]);
                                if(node !=null){
                                    tree.checkNode(node,true);
                                    tree.selectNode(node);
                                }
                            }
                        }
                        if(!opt.common.isEmpty(treeId)){
                            var _l = treeId.split(",");
                            for(var i=0; i<_l.length; i++){
                                var node = tree.getNodeByParam(opt.common.isEmpty(options.data.simpleData.idKey)?
                                    "id":options.data.simpleData.idKey, _l[i]);
                                if(node !=null){
                                    tree.checkNode(node,true);
                                    tree.selectNode(node);
                                }
                            }
                            // var node = tree.getNodeByParam(opt.common.isEmpty(options.data.simpleData.idKey)?
                            //     "id":options.data.simpleData.idKey, treeId);
                            // if(!opt.common.isEmpty(node)){
                            //     tree.checkNode(node,true);
                            //     if($.tree._option.check.chkStyle == 'radio') tree.selectNode(node);
                            // }
                        }
                        // }

                        // if(!opt.common.isEmpty(treeId) && !options.check.enable){
                        //     var node = tree.getNodeByParam(opt.common.isEmpty(options.data.simpleData.idKey)?
                        //         "id":options.data.simpleData.idKey, treeId);
                        //     if(!opt.common.isEmpty(node)){
                        //         tree.selectNode(node);
                        //     }
                        // }
                        //回调方法
                        if(typeof(options.callBack) === "function"){
                            options.callBack($._tree);
                        }
                    }
                };
                $.ajax(config);
            },
            expandAll: function(){
                $._tree.expandAll(true);
            },
            // 搜索节点
            searchNode: function() {
                // 取得输入的关键字的值
                var value = opt.common.trim($("#keyword").val());
                if ($.tree._lastValue == value) {
                    return;
                }
                // 保存最后一次搜索名称
                $.tree._lastValue = value;
                var nodes = $._tree.getNodes();
                // 如果要查空字串，就退出不查了。
                if (value == "") {
                    $.tree.showAllNode(nodes);
                    return;
                }
                $.tree.hideAllNode(nodes);

                //console.log($._tree.getNodesByParamFuzzy("name", value))

                // 根据搜索值模糊匹配
                $.tree.updateNodes($._tree.getNodesByParamFuzzy("name", value));
            },
            // 根据Id和Name选中指定节点
            selectByIdName: function(treeId, node) {
                if (opt.common.isNotEmpty(treeId) && treeId == opt.common.getJsonValue(node,$.tree._option.data.simpleData.idKey)) {
                    $._tree.selectNode(node, true);
                }
            },
            // 显示所有节点
            showAllNode: function(nodes) {
                nodes = $._tree.transformToArray(nodes);
                for (var i = nodes.length - 1; i >= 0; i--) {
                    if (nodes[i].getParentNode() != null) {
                        $._tree.expandNode(nodes[i], true, false, false, false);
                    } else {
                        $._tree.expandNode(nodes[i], true, true, false, false);
                    }
                    $._tree.showNode(nodes[i]);
                    $.tree.showAllNode(nodes[i].children);
                }
            },
            // 隐藏所有节点
            hideAllNode: function(nodes) {
                var tree = $.fn.zTree.getZTreeObj("tree");
                var nodes = $._tree.transformToArray(nodes);
                for (var i = nodes.length - 1; i >= 0; i--) {
                    $._tree.hideNode(nodes[i]);
                }
            },
            // 显示所有父节点
            showParent: function(treeNode) {
                var parentNode;
                while ((parentNode = treeNode.getParentNode()) != null) {
                    $._tree.showNode(parentNode);
                    $._tree.expandNode(parentNode, true, false, false);
                    treeNode = parentNode;
                }
            },
            // 获取所有 节点所有的父节点
            getParentIds: function(treeNode){
                if(opt.common.isEmpty(treeNode)) return "";
                var ids = treeNode.id;
                var node = treeNode.getParentNode();
                if(node != null){
                    ids = ids + "," + $.tree.getParentIds(node);
                }
                return ids;
            },
            // 显示所有孩子节点
            showChildren: function(treeNode) {
                if (treeNode.isParent) {
                    for (var idx in treeNode.children) {
                        var node = treeNode.children[idx];
                        $._tree.showNode(node);
                        $.tree.showChildren(node);
                    }
                }
            },
            // 更新节点状态
            updateNodes: function(nodeList) {
                $._tree.showNodes(nodeList);
                for (var i = 0, l = nodeList.length; i < l; i++) {
                    var treeNode = nodeList[i];
                    $.tree.showChildren(treeNode);
                    $.tree.showParent(treeNode)
                }
            },
            //重新加载
            refreshNode:function(){
                $._tree.reAsyncChildNodes(null, "refresh");
            },
            // 获取当前被勾选集合
            getCheckedNodes: function(column) {
                var _column = opt.common.isEmpty(column) ? (opt.common.isEmpty($.tree._option.data.simpleData.idKey)?
                    "id":$.tree._option.data.simpleData.idKey) : column;
                var nodes = $._tree.getCheckedNodes(true);
                var _t = $.map(nodes, function (row) {
                    return row[_column];
                }).join()
                return _t.split(",");
            },
            // 不允许根父节点选择
            notAllowParents: function(_tree) {
                // var nodes = _tree.getSelectedNodes();
                // if(nodes.length == 0){
                //     $.modal.msgError("请选择节点后提交");
                //     return false;
                // }
                // for (var i = 0; i < nodes.length; i++) {
                //     if (nodes[i].level == 0) {
                //         $.modal.msgError("不能选择根节点（" + nodes[i].name + "）");
                //         return false;
                //     }
                //     if (nodes[i].isParent) {
                //         $.modal.msgError("不能选择父节点（" + nodes[i].name + "）");
                //         return false;
                //     }
                // }
                return true;
            },
            // 不允许最后层级节点选择
            notAllowLastLevel: function(_tree) {
                var nodes = _tree.getSelectedNodes();
                for (var i = 0; i < nodes.length; i++) {
                    if (!nodes[i].isParent) {
                        $.modal.msgError("不能选择最后层级节点（" + nodes[i].name + "）");
                        return false;
                    }
                }
                return true;
            },
            // 隐藏/显示搜索栏
            toggleSearch: function() {
                $('#search').slideToggle(200);
                $('#btnShow').toggle();
                $('#btnHide').toggle();
                $('#keyword').focus();
            },
            //勾选 或 取消勾选 全部节点
            checkAllNodes: function(checked){
                $._tree.checkAllNodes(checked);
            },
            // 折叠
            collapse: function() {
                $._tree.expandAll(false);
            },
            // 展开
            expand: function() {
                $._tree.expandAll(true);
            },
            //处理回调之后获取id 与name 选取的所有父节点
            callBackTree:function (_p,_m) {
                var tree = _p.find("iframe")[0].contentWindow.$._tree;
                if ($.tree.notAllowParents(tree)) {
                    var body = $(_p.find('iframe')[0].contentWindow.document);
                    var treeId = body.find('#treeId').val();
                    var treeName = body.find('#treeName').val();
                    var parentIds = body.find('#parentIds').val();
                    var treeTitle = body.find('#treeTitle').val();
                    var type = body.find('#type').val();
                    // if(opt.common.isEmpty(treeName)){
                    //     if(tree.setting.check.enable){
                    //         var _l = treeId.split(",");
                    //         for(var i=0; i<_l.length; i++){
                    //             var node = tree.getNodeByParam(tree.setting.data.simpleData.idKey,_l[i]);
                    //             if(node!=null){
                    //                 treeName += node.name + ",";
                    //             }
                    //         }
                    //         if(opt.common.isEmpty(treeName)) treeName = treeName.substr(0,treeName.length-1);
                    //     }else{
                    //         treeName = tree.getNodesByParam(tree.setting.data.simpleData.idKey, treeId, null)[0].name;
                    //     }
                    // }
                    var _n = "{\"id\":\""+treeId+"\",\"name\":\""+treeName+"\",\"title\":\""+treeTitle+"\",\"parentIds\":\""+parentIds+"\",\"type\":\""+type+"\"}";
                    return $.parseJSON(_n);
                }else{
                    return false;
                }
            }
        }
    })
})(jQuery);
