/*!
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 * @author ZhouHuan
 * @date 2020-12-21
 * @version 2.6.0
 */
if (typeof jQuery === "undefined") {
    throw new Error("fastJS JavaScript requires jQuery")
}
(function ($, window, undefined) {

    var opt = {
        //变量
        variable:{
            navtab:null,
            _tabIndex:-999,
            tindex:0,
            pushMenu:null,
            version:'2.6.0',
            debug:true,
            mode: 'storage',
            // 默认加载提示名人名言 如果不用 false
            loadTip: true,
            // 是否开启水印
            isWatermark: true,
            // 表格类型
            table_type : {
                bootstrapTable: 0,
                bootstrapTreeTable: 1
            },
            // 弹窗状态码
            modal_status : {
                SUCCESS: "success",
                FAIL: "error",
                WARNING: "warning"
            },
            // 消息状态码
            web_status : {
                SUCCESS: "00000",
                FAIL: 500,
                WARNING: "50001"
            },
            // 皮肤
            skins:[
                'skin-blue',
                'skin-black',
                'skin-red',
                'skin-yellow',
                'skin-purple',
                'skin-green',
                'skin-blue-light',
                'skin-black-light',
                'skin-red-light',
                'skin-yellow-light',
                'skin-purple-light',
                'skin-green-light',
                'skin-blue-dark'
            ]
        },

        /**
         * 在dom树ready之后执行给定的回调函数
         * @method domReady
         * @remind 如果在执行该方法的时候， dom树已经ready， 那么回调函数将立刻执行
         * @param { Function } fn dom树ready之后的回调函数
         * @example
         * ```javascript
         *
         * opt.domReady( function () {
         *
         *     console.log('123');
         *
         * } );
         *
         * ```
         */
        domReady:function () {

            var fnArr = [];

            function doReady(doc) {
                //确保onready只执行一次
                doc.isReady = true;
                for (var ci; ci = fnArr.pop(); ci()) {
                }
            }

            return function (onready, win) {
                win = win || window;
                var doc = win.document;
                onready && fnArr.push(onready);
                if (doc.readyState === "complete") {
                    doReady(doc);
                } else {
                    doc.isReady && doReady(doc);
                    var agent = navigator.userAgent.toLowerCase();
                    var ie = /(msie\s|trident.*rv:)([\w.]+)/.test(agent);
                    var version;
                    var v1 =  agent.match(/(?:msie\s([\w.]+))/);
                    var v2 = agent.match(/(?:trident.*rv:([\w.]+))/);
                    if(v1 && v2 && v1[1] && v2[1]){
                        version = Math.max(v1[1]*1,v2[1]*1);
                    }else if(v1 && v1[1]){
                        version = v1[1]*1;
                    }else if(v2 && v2[1]){
                        version = v2[1]*1;
                    }else{
                        version = 0;
                    }
                    if (ie && version != 11) {
                        (function () {
                            if (doc.isReady) return;
                            try {
                                doc.documentElement.doScroll("left");
                            } catch (error) {
                                setTimeout(arguments.callee, 0);
                                return;
                            }
                            doReady(doc);
                        })();
                        win.attachEvent('onload', function () {
                            doReady(doc)
                        });
                    } else {
                        doc.addEventListener("DOMContentLoaded", function () {
                            doc.removeEventListener("DOMContentLoaded", arguments.callee, false);
                            doReady(doc);
                        }, false);
                        win.addEventListener('load', function () {
                            doReady(doc)
                        }, false);
                    }
                }

            }
        }(),

        // 设置菜单栏收缩
        sidebarCollapse: function(){
            opt.variable.pushMenu.expandOnHover();
            if (!$('body').hasClass('sidebar-collapse')){
                $('[data-layout="sidebar-collapse"]').click();
            }
            $('[data-toggle="push-menu"]').click();
        },
        pushMenuInit: function(){
            $('.main-sidebar').unbind('mouseenter').unbind('mouseleave');
        },
        // --
        wclearInterval: function(){
            window.clearInterval(opt.variable.tindex);
        },
        getMessage:function(){
            $.getJSON("sys/user/info?_" + $.now(), function (r) {
            });
        },
        /*
        debug: function (message) {
            if (window.console && opt.variable.debug) {
                console.log(message)
            }
        },
		*/

        /**
         * 系统提示通知
         */
        toast: function(){
            if ($.toast) {
                return $.toast
            }
            try {
                if (parent.$.toast) {
                    return parent.$.toast
                }
                if (parent.parent.$.toast) {
                    return parent.parent.$.toast
                }
                if (top.$.toast) {
                    return top.$.toast
                }
            } catch (e) {}
            return null
        }(),

        /**
         * 第一个参数必输 提示内容
         * 第二个参数 第三个参数 非必输循序随意 代表 显示时间、回调函数
         * 支持动态参数 3个
         * @param msg
         */
        error: function (msg) {
            var __arguments = arguments;
            var callback = '';
            var hideTime = 8000;
            var __callback = __arguments.length > 1;
            if(__callback){
                for(var i = 1; i<__arguments.length; i++){
                    if(typeof __arguments[i] == 'number'){
                        hideTime = __arguments[i];
                    }
                    if(typeof __arguments[i] == 'function'){
                        callback = __arguments[i];
                    }
                }
            }
            opt.modal.enable(); //显示提交按钮=
            if(opt.toast){
                var position = {
                    right: 7,
                    bottom: 32
                }
                if(top.location == self.location){
                    position = {
                        right: 7,
                        bottom: 5
                    }
                }
                opt.toast({
                    heading: $.i18n.prop('警告'),
                    text: msg,
                    hideAfter: hideTime,
                    position: position,
                    showHideTransition: 'slide',
                    afterHidden: function () {
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                    },
                    //stack: false,
                    icon: 'error'
                })
            }else{
                opt.modal.alertError(msg);
            }
        },

        info: function(msg){
            var __arguments = arguments;
            var callback = '';
            var hideTime = 4000;
            var __callback = __arguments.length > 1;
            if(__callback){
                for(var i = 1; i<__arguments.length; i++){
                    if(typeof __arguments[i] == 'number'){
                        hideTime = __arguments[i];
                    }
                    if(typeof __arguments[i] == 'function'){
                        callback = __arguments[i];
                    }
                }
            }
            if(opt.toast){
                var position = {
                    right: 7,
                    bottom: 32
                }
                if(top.location == self.location){
                    position = {
                        right: 7,
                        bottom: 5
                    }
                }
                opt.toast({
                    heading: $.i18n.prop('提示'),
                    text: msg,
                    hideAfter:hideTime,
                    position: position,
                    showHideTransition: 'slide',
                    afterHidden: function () {
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                    },
                    icon: 'info'
                })
            }else{
                opt.modal.alertInfo(msg);
                if(typeof(callback) === "function"){
                    callback("ok");
                }
            }
        },

        success: function (msg) {
            var __arguments = arguments;
            var callback = '';
            var hideTime = 4000;
            var __callback = __arguments.length > 1;
            if(__callback){
                for(var i = 1; i<__arguments.length; i++){
                    if(typeof __arguments[i] == 'number'){
                        hideTime = __arguments[i];
                    }
                    if(typeof __arguments[i] == 'function'){
                        callback = __arguments[i];
                    }
                }
            }
            debugger;
            if(opt.toast){
                var position = {
                    right: 7,
                    bottom: 32
                }
                if(top.location == self.location){
                    position = {
                        right: 7,
                        bottom: 5
                    }
                }
                opt.toast({
                    heading: $.i18n.prop('成功'),
                    text: msg,
                    hideAfter: hideTime,
                    position: position,
                    showHideTransition: 'slide',
                    afterHidden: function () {
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                    },
                    //stack: false,
                    icon: 'success'
                })
            }else{
                // opt.modal.success(msg,callback);
                // layui.use('layer', function(){
                //     var layer = layui.layer;
                //
                // });
                opt.selfLayer.msg(msg);
                if(typeof(callback) === "function"){
                    callback("ok");
                }
            }

        },

        warning:function(text){
            var __arguments = arguments;
            var callback = '';
            var hideTime = 4000;
            var __callback = __arguments.length > 1;
            if(__callback){
                for(var i = 1; i<__arguments.length; i++){
                    if(typeof __arguments[i] == 'number'){
                        hideTime = __arguments[i];
                    }
                    if(typeof __arguments[i] == 'function'){
                        callback = __arguments[i];
                    }
                }
            }
            if(opt.toast){
                var position = {
                    right: 7,
                    bottom: 32
                }
                if(top.location == self.location){
                    position = {
                        right: 7,
                        bottom: 5
                    }
                }
                opt.toast({
                    heading: $.i18n.prop('警告'),
                    text: text,
                    hideAfter: hideTime,
                    position: position,
                    showHideTransition: 'slide',
                    afterHidden: function () {
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                    },
                    //stack: false,
                    icon: 'warning'
                })
            }else{
                opt.modal.alertWarning(text);
            }
        },
        // 设置皮肤
        changeSkin: function(cls) {
            $.each(opt.variable.skins, function (i) {
                $('body').removeClass(opt.variable.skins[i])
            })
            $('body').addClass(cls);
            opt.storage.set('skin', cls)
            return false
        },
        // 设置页面模式
        setPageMode: function(src){
            if(top.location == self.location){
                var mode = opt.storage.get('mode',0);
                if(typeof mode !== 'undefined' && mode
                    && mode.length >0){
                    $('html').removeClass(mode);
                }
                if(typeof src !== 'undefined' && src
                    && src.length >0){
                    if (!$('html').hasClass(src)){
                        $('html').addClass(src);
                    }
                }
            }
        },
        //get menu config data for layer open  opt.getLeftMeunDataConfig('core','sys/comp')
        getLeftMeunDataConfig:function(module,href){
            var dataId = $("#leftMenu li a[data-url='"+href+"'][data-module='"+module+"']").attr("data-id") || -1;
            var dataIcon = $("#leftMenu li a[data-url='"+href+"'][data-module='"+module+"'] i").attr("data-icon") || "";
            var title = $("#leftMenu li a[data-url='"+href+"'][data-module='"+module+"'] span").html() || ""
            var data = {
                href: href,
                icon:  dataIcon,  //'fa fa-address-card',
                title: $.i18n.prop(title),
                id:   dataId, // $("#leftMenu .treeview-menu [data-url='sys/comp'],[data-module='core']").attr("data-id"), //'Y29yZTQx',
                module:module
            };
            return data
        },
        //get the frist menu config for layer open
        getLeftFirstMenuConig:function(){
            var href =  $("#leftMenu li a[data-url]:first-child").attr("data-url") || "main";
            var dataId = $("#leftMenu li a[data-url]:first-child").attr("data-id") || -1;
            var module = $("#leftMenu li a[data-url]:first-child").attr("data-module") ||"core";
            var dataIcon = $("#leftMenu li a[data-url]:first-child i").attr("data-icon") || "";
            var title = $("#leftMenu li a[data-url]:first-child span").html() ;
            var data = {
                href: href,
                icon:  dataIcon,
                title: $.i18n.prop(title),
                id:   dataId,
                module:module
            };
            return data
        },

        createMenuItem: function(dataUrl, menuName, icon) {
            if(top.location !== self.location) {
                var dataIndex = opt.common.randomString(16);
                var panelId,module;
                if (dataUrl == undefined || $.trim(dataUrl).length == 0) return false;
                if (opt.common.isEmpty(parent.opt.variable.navtab)) {
                    parent.opt.createMenuItem(dataUrl, menuName,icon);
                    return;
                }
                var topWindow = $(window.parent.document);
                if ($(".layui-tab-title li", topWindow).length > 0) {
                    $(".layui-tab-title li", topWindow).each(function () {
                        if ($(this).hasClass("layui-this")) {
                            panelId = $(this).attr("lay-id");
                            module = $(this).children('em').data('module');
                        }
                    })
                }
                if(opt.common.isEmpty(icon)){
                    icon = "";
                }
                var data = {
                    href: dataUrl,
                    icon: icon,
                    panel: panelId,
                    title: menuName,
                    module:module,
                    id: dataIndex
                };
                parent.opt.navTabAdd(data);
                return;
            }else {
                opt.selfLayer.open({
                    type: 2,
                    maxmin: true,
                    shadeClose: true,
                    title: menuName,
                    area: [($(window).width() - 100)+'px',
                        ($(window).height() - 100) + 'px'],
                    content:dataUrl
                });
            }
        },

        closeItem: function(dataId){
            if(top.location!=self.location){
                var topWindow = $(window.parent.document);
                if($(".layui-tab-title li",topWindow).length > 0){
                    if(opt.common.isNotEmpty(dataId)){
                        $('.layui-tab-title li[lay-id="'+dataId+'"]',topWindow).children('i.layui-tab-close[data-id="' + data.id + '"]').trigger("click");
                    }else{
                        $(".layui-tab-title li",topWindow).each(function(){
                            if($(this).hasClass("layui-this")){
                                $(this).children('i.layui-tab-close[data-id="' + $(this).attr("lay-id") + '"]').trigger("click");
                                return false;
                            }
                        })
                    }
                }else{
                    try{
                        if(parent.opt.table.options.type == opt.variable.table_type.bootstrapTable){
                            parent.$.table.refresh();
                        }else if (parent.opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                            parent.$.treeTable.refresh();
                        }
                    }catch (e) {
                    }
                    parent.opt.selfLayer.closeAll();
                }
            }else {
                window.opener=null;
                window.open('','_self');
                window.close();
            }
        },
        //数字自增到某一值动画参数（目标元素,自定义配置）
        animateNum: function (targetEle, options) {
            /*可以自己改造下传入的参数，按照自己的需求和喜好封装该函数*/
            //不传配置就把它绑定在相应html元素的data-xxxx属性上吧
            options = options || {};
            var $this = document.getElementById(targetEle),
                time = options.time || $this.data('time') || 2500, //总时间--毫秒为单位
                finalNum = options.num || $this.data('value'), //要显示的真实数值
                regulator = options.regulator || 100, //调速器，改变regulator的数值可以调节数字改变的速度
                step = finalNum / (time / regulator),/*每30ms增加的数值--*/
                count = 0, //计数器
                initial = 0;
            var timer = setInterval(function() {
                count = count + step;
                if(count >= finalNum) {
                    clearInterval(timer);
                    count = finalNum;
                }
                //t未发生改变的话就直接返回
                //避免调用text函数，提高DOM性能
                var t = Math.floor(count);
                if(t == initial) return;
                initial = t;
                $this.innerHTML = initial;
            }, 30);
        },

        //页面遮罩
        block: function(value,element){
            if(opt.common.isEmpty(value)){
                if(opt.common.isNotEmpty(element)){
                    $(element).block();
                }else{
                    $.blockUI();
                }
            }else{
                if(opt.common.isNotEmpty(element)){
                    $(element).block({ message: '<div class="loaderbox"><div class="loading-activity"></div> ' + $.i18n.prop(value) + '</div>' });
                }else{
                    $.blockUI({ message: '<div class="loaderbox"><div class="loading-activity"></div> ' + $.i18n.prop(value) + '</div>' });
                }
            }

        },

        unblock: function(element){
            if(opt.common.isNotEmpty(element)){
                $(element).unblock();
            }else{
                $.unblockUI();
            }
        },
        /*****************************************************/
        //借鉴 jeesite
        //模板引擎调用
        template: function (id, data, callback) {
            var tpl = String($("#" + id).html()).replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g, ""),
                data = data || [];
            if (typeof callback == "function") {
                laytpl(tpl).render(data || [], function (render) {
                    callback(render)
                });
                return null
            }
            return laytpl(tpl).render(data || [])
        },

        layer: function () {
            try {
                if (top.layer) {
                    return top.layer
                }
                if (parent.parent.layer) {
                    return parent.parent.layer
                }
                if (parent.layer) {
                    return parent.layer
                }
            } catch (e) {}
            if (window.layer) {
                return layer
            }
            return null
        }(),
        /******************************************************/
        //获取系统cookie
        getCookie: function(name){
            if (document.cookie.length>0){
                var c_start=document.cookie.indexOf(name + "=")
                if (c_start!=-1){
                    c_start=c_start + name.length+1
                    var c_end=document.cookie.indexOf(";",c_start)
                    if (c_end==-1) c_end=document.cookie.length
                    return unescape(document.cookie.substring(c_start,c_end))
                }
            }
            return ""
        },

        selfLayer:function(){
            if (window.layer) {
                return layer
            }
            try {
                if (top.layer && top.layer.window) {
                    return top.layer
                }
                if (parent.parent.layer && parent.parent.layer.window) {
                    return parent.parent.layer
                }
                if (parent.layer && parent.layer.window) {
                    return parent.layer
                }
            } catch (e) {}
            return null
        }(),
        //========================TAB================================//
        navDelTab: function (dataId) {
            //layui.element.tabDelete("main-tab",dataId).init();
            opt.variable.navtab.tabDelete(dataId);
        },

        navTabAdd: function(data){
            opt.variable.navtab.tabAdd(data);
        },

        navToTab: function(dataId){
            //layui.element.tabChange("main-tab",dataId);
            opt.variable.navtab.tabChange(dataId);
        },

        navDelTabNow: function(){
            if($(".layui-tab-title li").length > 1){
                $(".layui-tab-title li").each(function(){
                    if($(this).attr("lay-id") != 0){
                        //opt.navDelTab($(this).attr("lay-id"));
                        $(this).children('i.layui-tab-close[data-id="' + $(this).attr("lay-id") + '"]').trigger("click");
                    }
                })
            }
        },
        //========================TAB================================//

        //本地缓存处理
        storage:{
            set: function(key, value,tag) {
                if(typeof tag !== 'undefined'&& tag == 0){
                    window.localStorage.setItem(key, value);
                }else{
                    window.localStorage.setItem(__USERNAME__+key, value);
                }
            },
            get: function(key,tag) {
                if(typeof tag !== 'undefined'&& tag == 0){
                    return window.localStorage.getItem(key);
                }else {
                    return window.localStorage.getItem(__USERNAME__+key);
                }
            },
            remove: function(key,tag) {
                if(typeof tag !== 'undefined'&& tag == 0){
                    window.localStorage.removeItem(key);
                }else{
                    window.localStorage.removeItem(__USERNAME__+key);
                }
            },
            clear: function() {
                window.localStorage.clear();
            }
        },
        // 当前table相关信息
        table : {
            config: {},
            // 当前实例配置
            options: {},
            // 设置实例配置
            set: function(id) {
                if(opt.common.getLength(opt.table.config) > 1) {
                    var tableId = opt.common.isEmpty(id) ? $(event.currentTarget).parents(".bootstrap-table").find(".table").attr("id") : id;
                    if (opt.common.isNotEmpty(tableId)) {
                        opt.table.options = opt.table.get(tableId);
                    }
                }
            },
            //字段缓存
            __dict__:{},
            // 获取实例配置
            get: function(id) {
                return opt.table.config[id];
            },
            // 记住选择实例组
            rememberSelecteds: {},
            // 记住选择ID组
            rememberSelectedIds: {},

        },
        // 通用方法封装处理
        common: {
            /**
             * 判断对象是否为空
             * @param obj 需要判断的对象
             * @remind 如果判断的对象是NULL， 将直接返回true， 如果是数组且为空， 返回true， 如果是字符串， 且字符串为空，
             *          返回true， 如果是普通对象， 且该对象没有任何实例属性， 返回true
             * @returns {boolean} 对象是否为空
             * @example
             * ```javascript
             *
             * //output: true
             * console.log( opt.common.isEmpty( {} ) );
             *
             * //output: true
             * console.log( opt.common.isEmpty( [] ) );
             *
             * //output: true
             * console.log( opt.common.isEmpty( "" ) );
             *
             * //output: false
             * console.log( opt.common.isEmpty( { key: 1 } ) );
             *
             * //output: false
             * console.log( opt.common.isEmpty( [1] ) );
             *
             * //output: false
             * console.log( opt.common.isEmpty( "1" ) );
             *
             * //output: false
             * console.log( opt.common.isEmpty( 1 ) );
             *
             * ```
             */
            isEmpty: function (obj) {
                try{
                    if (obj == null || obj == undefined) return true;
                    if (this.isArray(obj) || this.isString(obj)) return obj.length === 0;
                    if (this.isNumber(obj)) return false;
                    if (this.isFunction(obj)) return false;
                    if (typeof obj === 'boolean') return false;
                    for (var key in obj) if (obj.hasOwnProperty(key)) return false;
                    return true;
                }catch (e) {
                    if(window.console)console.error(e);
                    return true;
                }
            },
            /**
             * 为网页添加文字水印方法
             * @param [Array] str 要添加的字符串数组,分别为姓名，手机号，日期
             * [name,mobileNumber,datetime]
             */
            addWaterMarker: function(str){
                var cpyName = str;
                if (str.length > 26) {
                    cpyName = str.substring(0, 26);
                }
                var can = document.createElement('canvas');
                var report = $('.wrapper')[0];
                report.appendChild(can);
                can.width = 280;
                can.height = 110;
                can.style.display = 'none';
                can.style.zIndex = '999999'

                var cans = can.getContext('2d');
                cans.rotate(-25 * Math.PI / 180);
                cans.font = "800 30px Microsoft JhengHei";
                cans.fillStyle = "#000";
                cans.textAlign = 'center';
                cans.textBaseline = 'Middle';
                if(cans.measureText(cpyName).width > 180) {
                    var size = 180 / cpyName.length
                    cans.font = '800 ' + size +'px '+ ' Microsoft JhengHei'
                }
                cans.fillText(cpyName, 60, 100);
                report.style.backgroundImage = "url(" + can.toDataURL("image/png") + ")";
            },

            watermark: function (settings) {
                //默认设置
                var defaultSettings={
                    watermark_txt:"text",
                    watermark_x:20,//水印起始位置x轴坐标
                    watermark_y:20,//水印起始位置Y轴坐标
                    watermark_rows:100,//水印行数
                    watermark_cols:20,//水印列数
                    watermark_x_space:10,//水印x轴间隔
                    watermark_y_space:10,//水印y轴间隔
                    watermark_color:'#aaa',//水印字体颜色
                    watermark_alpha:0.3,//水印透明度
                    watermark_fontsize:'15px',//水印字体大小
                    watermark_font:'微软雅黑',//水印字体
                    watermark_width:150,//水印宽度
                    watermark_height:80,//水印长度
                    watermark_angle:15//水印倾斜度数
                };
                //采用配置项替换默认值，作用类似jquery.extend
                if(arguments.length===1&&typeof arguments[0] ==="object" ) {
                    var src=arguments[0]||{};
                    for(key in src)
                    {
                        if(src[key]&&defaultSettings[key]&&src[key]===defaultSettings[key])
                            continue;
                        else if(src[key])
                            defaultSettings[key]=src[key];
                    }
                }

                var oTemp = document.createDocumentFragment();

                //获取页面最大宽度
                var page_width = Math.max(document.body.scrollWidth,document.body.clientWidth);
                var cutWidth = page_width*0.0150;
                page_width=page_width-cutWidth;
                //获取页面最大高度
                var page_height = Math.max(document.body.scrollHeight-80,document.body.clientHeight-40);
                // var page_height = document.body.scrollHeight+document.body.scrollTop;
                //如果将水印列数设置为0，或水印列数设置过大，超过页面最大宽度，则重新计算水印列数和水印x轴间隔
                if (defaultSettings.watermark_cols == 0 || (parseInt(defaultSettings.watermark_x + defaultSettings.watermark_width *defaultSettings.watermark_cols + defaultSettings.watermark_x_space * (defaultSettings.watermark_cols - 1)) > page_width)) {
                    defaultSettings.watermark_cols = parseInt((page_width-defaultSettings.watermark_x+defaultSettings.watermark_x_space) / (defaultSettings.watermark_width + defaultSettings.watermark_x_space));
                    defaultSettings.watermark_x_space = parseInt((page_width - defaultSettings.watermark_x - defaultSettings.watermark_width * defaultSettings.watermark_cols) / (defaultSettings.watermark_cols - 1));
                }
                //如果将水印行数设置为0，或水印行数设置过大，超过页面最大长度，则重新计算水印行数和水印y轴间隔
                if (defaultSettings.watermark_rows == 0 || (parseInt(defaultSettings.watermark_y + defaultSettings.watermark_height * defaultSettings.watermark_rows + defaultSettings.watermark_y_space * (defaultSettings.watermark_rows - 1)) > page_height)) {
                    defaultSettings.watermark_rows = parseInt((defaultSettings.watermark_y_space + page_height - defaultSettings.watermark_y) / (defaultSettings.watermark_height + defaultSettings.watermark_y_space));
                    defaultSettings.watermark_y_space = parseInt(((page_height - defaultSettings.watermark_y) - defaultSettings.watermark_height * defaultSettings.watermark_rows) / (defaultSettings.watermark_rows - 1));
                }
                var x;
                var y;
                for (var i = 0; i < defaultSettings.watermark_rows; i++) {
                    y = defaultSettings.watermark_y + (defaultSettings.watermark_y_space + defaultSettings.watermark_height) * i;
                    for (var j = 0; j < defaultSettings.watermark_cols; j++) {
                        x = defaultSettings.watermark_x + (defaultSettings.watermark_width + defaultSettings.watermark_x_space) * j ;
                        var mask_div = document.createElement('div');
                        mask_div.id = 'mask_div' + i + j;
                        mask_div.className = 'mask_div';
                        mask_div.appendChild(document.createTextNode(defaultSettings.watermark_txt));
                        //设置水印div倾斜显示
                        mask_div.style.webkitTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.MozTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.msTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.OTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.transform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.visibility = "";
                        mask_div.style.position = "fixed";
                        mask_div.style.left = x + 'px';
                        mask_div.style.top = y + 'px';
                        mask_div.style.overflow = "hidden";
                        mask_div.style.zIndex = "19920219";
                        mask_div.style.pointerEvents='none';//pointer-events:none  让水印不遮挡页面的点击事件
                        //mask_div.style.border="solid #eee 1px";
                        mask_div.style.opacity = defaultSettings.watermark_alpha;
                        mask_div.style.fontSize = defaultSettings.watermark_fontsize;
                        mask_div.style.fontFamily = defaultSettings.watermark_font;
                        mask_div.style.color = defaultSettings.watermark_color;
                        mask_div.style.textAlign = "center";
                        mask_div.style.width = defaultSettings.watermark_width + 'px';
                        mask_div.style.height = defaultSettings.watermark_height + 'px';
                        mask_div.style.display = "block";
                        //交叉网格显示
                        if((i%2==0)&&(j%2==0)){
                            oTemp.appendChild(mask_div);
                        }
                        if((i%2==1)&&(j%2==1)){
                            oTemp.appendChild(mask_div);
                        }
                    };
                };
                document.body.appendChild(oTemp);
            },
            /**
             * 检测值是否为 基本类型
             */
            isPrimitive: function (value) {
                return (
                    typeof value === 'string' ||
                    typeof value === 'number' ||
                    // $flow-disable-line
                    typeof value === 'symbol' ||
                    typeof value === 'boolean'
                )
            },
            // 判断一个字符串是否为非空串
            isNotEmpty: function (value) {
                return !opt.common.isEmpty(value);
            },
            // 空对象转字符串
            nullToStr: function(value,_default) {
                if (opt.common.isEmpty(value)) {
                    return (opt.common.isEmpty(_default))?"":_default;
                }
                return value;
            },
            // 是否显示数据 为空默认为显示
            visible: function (value) {
                if (opt.common.isEmpty(value) || value == true) {
                    return true;
                }
                return false;
            },

            // 回显数据字典
            selectDictLabel: function(datas, value,tag) {
                //如果是字符串直接从缓存中获取
                if(opt.common.isString(datas)){
                    if(opt.table.__dict__ == undefined){
                        opt.table.__dict__ = {};
                    }
                    if(opt.table.__dict__[datas] == undefined ||
                        opt.table.__dict__[datas] == 'undefined'){
                        var data = window.localStorage.getItem('_dict_'+datas);
                        if(opt.common.isEmpty(data) ||
                            data == 'undefined'){
                            var conifg = {
                                url: baseURL + 'sys/dict/data/type/'+datas,
                                type: "GET",
                                dataType: "JSON",
                                async: false,
                                success: function(ref) {
                                    if(ref.code == opt.variable.web_status.SUCCESS &&
                                        typeof ref.data == 'object'){
                                        window.localStorage.setItem('_dict_'+datas,JSON.stringify(ref.data));
                                        datas = ref.data;
                                        opt.table.__dict__[datas] = ref.data;
                                    }else{
                                        opt.error("获取字典错误！请检查字典字段:"+datas);
                                        return;
                                    }
                                }
                            }
                            //请求后台
                            $.ajax(conifg);
                        }
                        else{
                            var d = JSON.parse(data);
                            opt.table.__dict__[datas] = d;
                            datas = d;
                        }
                    }else{
                        datas = opt.table.__dict__[datas];
                    }
                    if(typeof  datas  != "object"){
                        console.error("请检查字典设置字段是否正确.....",datas);
                    }
                }
                var actions = [];
                $.each(datas, function(index, dict) {
                    if (dict.dictValue == ('' + value)) {
                        var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                        if(!opt.common.isEmpty(dict.cssClass)){
                            listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                        }
                        if(!opt.common.isEmpty(dict.cssStyle)){
                            listClass = dict.cssStyle;
                        }
                        if(tag != undefined && tag == 0){
                            listClass = '';
                        }
                        if(!opt.common.isEmpty(dict.cssStyle)){
                            actions.push(opt.common.sprintf("<span style='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                        }else{
                            actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                        }
                        return false;
                    }
                    //兼容客户端数据为空 -- 则匹配字典默认值
                    if (opt.common.isEmpty(value) && dict.isDefault === "Y") {
                        var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                        if(!opt.common.isEmpty(dict.cssClass)){
                            listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                        }
                        if(!opt.common.isEmpty(dict.cssStyle)){
                            listClass = dict.cssStyle;
                        }
                        if(tag != undefined && tag == 0){
                            listClass = '';
                        }
                        if(!opt.common.isEmpty(dict.cssStyle)){
                            actions.push(opt.common.sprintf("<span style='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                        }else{
                            actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                        }
                        return false;
                    }
                });
                return actions.join('');
            },

            // 回显数据字典 多个, 约定以逗号分割---用于复选框翻译回显 , values 值 字符串 如： 1,2,3,4
            selectDictLabels: function(datas, valueStr) {
                var actions = [];
                valueStr = valueStr + "";
                valueStr = valueStr || "";
                var values = valueStr.split(",");
                $.each(values ,function(index, value){
                    $.each(datas, function(index, dict) {
                        if (dict.dictValue == ('' + value)) {
                            // var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                            // if(!opt.common.isEmpty(dict.cssClass)){
                            //     listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                            // }
                            // actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                            // return false;
                            var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                            if(!opt.common.isEmpty(dict.cssClass)){
                                listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                            }
                            if(!opt.common.isEmpty(dict.cssStyle)){
                                listClass = dict.cssStyle;
                            }
                            if(!opt.common.isEmpty(dict.cssStyle)){
                                actions.push(opt.common.sprintf("<span style='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                            }else{
                                actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                            }
                            return false;
                        }
                        //兼容客户端数据为空 -- 则匹配字典默认值
                        if (opt.common.isEmpty(value) && dict.isDefault === "Y") {
                            var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                            if(!opt.common.isEmpty(dict.cssClass)){
                                listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                            }
                            actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                            return false;
                        }
                    });
                });

                return actions.join(',');
            },

            /**
             *  列超出指定长度浮动提示
             * @param value 需要控制的文本
             * @param length 超出多长显示
             * @param align 截取value文本显示缩略
             * @param target copy 双击弹出复制文本 open弹窗打开文本
             * @returns 返回处理过后DIV
             */
            tooltip: function (value, length,align, target) {
                var _length = opt.common.isEmpty(length) ? 20 : length;
                var _align = opt.common.isEmpty(align) ? false : align;
                var _text = "";
                var _value = opt.common.nullToStr(value);
                var _target = opt.common.isEmpty(target) ? 'copy' : target;
                if (_value.length > _length) {
                    if(_align){
                        _text = "..." + _value.substr(_value.length-_length, _length);
                    }else{
                        _text = _value.substr(0, _length) + "...";
                    }
                    _value = _value.replace(/\'/g,"&apos;");
                    _value = _value.replace(/\"/g,"&quot;");
                    var actions = [];
                    actions.push(opt.common.sprintf('<input style="opacity: 0;position: absolute;z-index:-1" type="text" value="%s"/>', _value));
                    actions.push(opt.common.sprintf('<a href="###" class="tooltip-show" data-toggle="tooltip" data-target="%s" title="%s">%s</a>', _target, _value, _text));
                    return actions.join('');
                } else {
                    _text = _value;
                    //return _text;
                    var actions = [];
                    actions.push(opt.common.sprintf('<input style="opacity: 0;position: absolute;z-index:-1" type="text" value="%s"/>', _value));
                    // actions.push(opt.common.sprintf('<a href="###" class="tooltip-show" data-toggle="tooltip" data-target="%s" title="%s">%s</a>', _target, _value, _text));
                    actions.push(opt.common.sprintf('<span class="tooltip-show" data-target="%s" title="%s">%s</span>', _target, _value, _text));
                    return actions.join('');
                }
            },
            /**
             * 删除字符串str的首尾空格
             * @param value 必须是字符串
             * @returns {string} 删除首尾空格字符
             * @example
             * var str = ' j2eefast ';
             *
             * //output: 10
             * console.log( str.length );
             *
             * //output: 8
             * console.log( opt.common.trim( ' j2eefast ').length );
             *
             * //output: 10
             * console.log( str.length );
             */
            trim: function (value) {
                if (value == null) {
                    return "";
                }
                if(typeof value === 'string'){
                    return value.toString().replace(/(^[ \t\n\r]+)|([ \t\n\r]+$)/g, '');
                }else{
                    return value;
                }
            },
            hideStr:function(value,len, tag){
                if (opt.common.isEmpty(value)) {
                    return "-";
                }else{
                    if(tag == 0){
                        if(value.length > len){
                            return value.substr(0,len) + "...";
                        }else{
                            return value;
                        }
                    }else{
                        return "..."+value.substr(value.length-len,len);
                    }
                }
            },
            // 比较两个字符串（大小写敏感）
            equals: function (str, that) {
                return str == that;
            },
            // 比较两个字符串（大小写不敏感）
            equalsIgnoreCase: function (str, that) {
                return String(str).toUpperCase() === String(that).toUpperCase();
            },
            // 将字符串按指定字符分割
            split: function (str, sep, maxLen) {
                if (opt.common.isEmpty(str)) {
                    return null;
                }
                var value = String(str).split(sep);
                return maxLen ? value.slice(0, maxLen - 1) : value;
            },
            // 字符串格式化(%s )
            sprintf: function (str) {
                var args = arguments, flag = true, i = 1;
                str = str.replace(/%s/g, function () {
                    var arg = args[i++];
                    if (typeof arg === 'undefined') {
                        flag = false;
                        return '';
                    }
                    return arg;
                });

                return flag ? str : '';
            },
            // 指定随机数返回
            random: function (min, max) {
                return Math.floor((Math.random() * max) + min);
            },
            // 指定随机生成字符串
            randomString:function (len) {
                len = len || 32;
                var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';    /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
                var maxPos = $chars.length;
                var pwd = '';
                for (var i = 0; i < len; i++) {
                    pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
                }
                return pwd;
            },
            // 判断字符串是否是以start开头
            startWith: function(value, start) {
                var reg = new RegExp("^" + start);
                return reg.test(value)
            },
            // 判断字符串是否是以end结尾
            endWith: function(value, end) {
                var reg = new RegExp(end + "$");
                return reg.test(value)
            },
            // 数组去重
            uniqueFn: function(array) {
                var result = [];
                var hashObj = {};
                for (var i = 0; i < array.length; i++) {
                    if (!hashObj[array[i]]) {
                        hashObj[array[i]] = true;
                        result.push(array[i]);
                    }
                }
                return result;
            },
            // 数组中的所有元素放入一个字符串
            join: function(array, separator) {
                if (opt.common.isEmpty(array)) {
                    return null;
                }
                return array.join(separator);
            },
            // 获取form下所有的字段并转换为json对象
            formToJSON: function(formId) {
                var json = {};
                $.each($("#" + formId).serializeArray(), function(i, field) {
                    if(json[field.name]) {
                        json[field.name] += ("," + field.value);
                    } else {
                        json[field.name] = field.value;
                    }
                });
                return json;
            },
            getJsonValue:function(obj,k){
                // var _r;
                // for(var key  in obj){
                //     if(key == k){
                //         _r = obj[key];
                //         break;
                //     }
                // }
                // return _r;

                var rn = obj;
                var props = k.split('.');
                for (var p in props) {
                    if(rn[props[p]] && typeof rn[props[p]] != 'undefined'){
                        rn = rn[props[p]];
                    }else {
                        rn = '';
                    }
                }
                return rn;
            },
            objToEmpty:function(obj){
                // for(var i=0; i<obj.length; i++){
                //     if(Array.prototype==obj[i].__proto__){
                //         obj[key] = [];
                //     }else if(typeof obj[key] == "string"){
                //         obj[key] = '';
                //     }else if(typeof obj[key] == "number"){
                //         obj[key] = 0;
                //     }
                // }

                for(var key  in obj){
                    if(Array.prototype==obj[key].__proto__){
                        obj[key] = [];
                    }else if(typeof obj[key] == "string"){
                        obj[key] = '';
                    }else if(typeof obj[key] == "number"){
                        obj[key] = 0;
                    }
                }
                return obj;
            },
            /**
             * 获取对象长度
             * @param obj
             * @returns {number}
             */
            getLength: function(obj) {
                var count = 0;
                for (var i in obj) {
                    if (obj.hasOwnProperty(i)) {
                        count++;
                    }
                }
                return count;
            },
            /**
             * 页面复制粘贴板
             * @param text
             */
            copy:function(text){
                var oInput = document.createElement('textarea');
                oInput.value = text;
                $(oInput).css({opacity:'0'})
                $(oInput).attr({name:"__copy_secukey"})
                document.body.appendChild(oInput);
                oInput.select(); // 选择对象
                document.execCommand("Copy"); // 执行浏览器复制命令
                oInput.className = 'oInput';
                $("textarea[name='__copy_secukey']").remove()
            },
            /**
             * 获取 Checkbox 值 已,隔开
             */
            getCheckboxValue:function (name) {
                var txt = "";
                $('input[name="'+name+'"]').each(function () {
                    if($(this).is(':checked')){
                        txt+=$(this).val()+","
                    }
                });
                if(txt.length > 0){
                    txt = txt.substring(0,txt.length-1);
                }
                return txt;
            },
            /**
             * 将字符串字符 转数字,如果转换失败，则返回原始字符串。
             */
            toNumber: function (val) {
                var n = parseFloat(val);
                return isNaN(n) ? val : n
            },
            /**
             *  从数组中删除
             *  例如 var a = [1,2], var b= 2
             *      opt.common.remove(a,b);
             *      中a 值为 [1]
             */
            remove: function (arr, item) {
                if (arr.length) {
                    var index = arr.indexOf(item);
                    if (index > -1) {
                        return arr.splice(index, 1)
                    }
                }
            },
            /**
             * 截取数组
             * 例如 var a = [1,2,3,4,5,6], var b = 3;
             *     返回 [4, 5, 6]
             */
            toArray: function (list, start) {
                start = start || 0;
                var i = list.length - start;
                var ret = new Array(i);
                while (i--) {
                    ret[i] = list[i + start];
                }
                return ret
            },
            /**
             * 将source对象中的属性扩展到target对象上
             * @method extend
             * @remind 该方法将强制把source对象上的属性复制到target对象上
             * @see opt.common.extend(Object,Object,Boolean)
             * @param { Object } target 目标对象， 新的属性将附加到该对象上
             * @param { Object } source 源对象， 该对象的属性会被附加到target对象上
             * @return { Object } 返回target对象
             * @example
             * ```javascript
             *
             * var target = { name: 'target', sex: 1 },
             *      source = { name: 'source', age: 17 };
             *
             * opt.common.extend( target, source );
             *
             * //output: { name: 'source', sex: 1, age: 17 }
             * console.log( target );
             *
             * ```
             */

            /**
             * 将source对象中的属性扩展到target对象上， 根据指定的isKeepTarget值决定是否保留目标对象中与
             * 源对象属性名相同的属性值。
             * @method extend
             * @param { Object } target 目标对象， 新的属性将附加到该对象上
             * @param { Object } source 源对象， 该对象的属性会被附加到target对象上
             * @param { Boolean } isKeepTarget 是否保留目标对象中与源对象中属性名相同的属性
             * @return { Object } 返回target对象
             * @example
             * ```javascript
             *
             * var target = { name: 'target', sex: 1 },
             *      source = { name: 'source', age: 17 };
             *
             * opt.common.extend( target, source, true );
             *
             * //output: { name: 'target', sex: 1, age: 17 }
             * console.log( target );
             *
             * ```
             */
            extend:function (target, source, isKeepTarget) {
                if (source) {
                    for (var k in source) {
                        if (!isKeepTarget || !target.hasOwnProperty(k)) {
                            target[k] = source[k];
                        }
                    }
                }
                return target;
            },

            /**
             * 用给定的迭代器遍历数组或类数组对象
             * @method each
             * @param { Array } array 需要遍历的数组或者类数组
             * @param { Function } iterator 迭代器， 该方法接受两个参数， 第一个参数是当前所处理的value， 第二个参数是当前遍历对象的key
             * @example
             * ```javascript
             * var divs = document.getElmentByTagNames( "div" );
             *
             * //output: 0: DIV, 1: DIV ...
             * opt.common.each( divs, funciton ( value, key ) {
             *
             *     console.log( key + ":" + value.tagName );
             *
             * } );
             * ```
             */
            each : function(obj, iterator, context) {
                if (obj == null) return;
                if (obj.length === +obj.length) {
                    for (var i = 0, l = obj.length; i < l; i++) {
                        if(iterator.call(context, obj[i], i, obj) === false)
                            return false;
                    }
                } else {
                    for (var key in obj) {
                        if (obj.hasOwnProperty(key)) {
                            if(iterator.call(context, obj[key], key, obj) === false)
                                return false;
                        }
                    }
                }
            },

            /**
             * 将一个对象数组合并到一个对象中。
             */
            toObject: function (arr) {
                var that = this;
                var res = {};
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i]) {
                        that.extend(res, arr[i]);
                    }
                }
                return res
            },

            /**
             * 金额转换中文汉字
             * @param money
             * @returns {string}
             * @example
             * ```javascript
             *
             * var money = opt.common.moneyToChinese(12390.97);
             *
             * //output: 壹万贰仟叁佰玖拾元玖角柒分
             * console.log( money );
             *
             * ```
             */
            moneyToChinese : function (money) {
                var cnNums = new Array("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"); //汉字的数字
                var cnIntRadice = new Array("", "拾", "佰", "仟"); //基本单位
                var cnIntUnits = new Array("", "万", "亿", "兆"); //对应整数部分扩展单位
                var cnDecUnits = new Array("角", "分", "毫", "厘"); //对应小数部分单位
                var cnInteger = "整"; //整数金额时后面跟的字符
                var cnIntLast = "元"; //整型完以后的单位
                var maxNum = 999999999999999.9999; //最大处理的数字
                var IntegerNum; //金额整数部分
                var DecimalNum; //金额小数部分
                var ChineseStr = ""; //输出的中文金额字符串
                var parts; //分离金额后用的数组，预定义
                var Symbol = "";//正负值标记
                if (opt.common.isEmpty(money)) {
                    return "零";
                }
                money = parseFloat(money);
                if (money >= maxNum) {
                    throw new Error('超出最大金额!');
                }
                if (money == 0) {
                    ChineseStr = cnNums[0] + cnIntLast + cnInteger;
                    return ChineseStr;
                }
                if (money < 0) {
                    money = -money;
                    Symbol = "负 ";
                }
                money = money.toString(); //转换为字符串
                if (money.indexOf(".") == -1) {
                    IntegerNum = money;
                    DecimalNum = '';
                } else {
                    parts = money.split(".");
                    IntegerNum = parts[0];
                    DecimalNum = parts[1].substr(0, 4);
                }
                if (parseInt(IntegerNum, 10) > 0) { //获取整型部分转换
                    var zeroCount = 0;
                    var IntLen = IntegerNum.length;
                    for (var i = 0; i < IntLen; i++) {
                        var n = IntegerNum.substr(i, 1);
                        var p = IntLen - i - 1;
                        var q = p / 4;
                        var m = p % 4;
                        if (n == "0") {
                            zeroCount++;
                        }
                        else {
                            if (zeroCount > 0) {
                                ChineseStr += cnNums[0];
                            }
                            zeroCount = 0; //归零
                            ChineseStr += cnNums[parseInt(n)] + cnIntRadice[m];
                        }
                        if (m == 0 && zeroCount < 4) {
                            ChineseStr += cnIntUnits[q];
                        }
                    }
                    ChineseStr += cnIntLast;
                    //整型部分处理完毕
                }
                if (DecimalNum != '') { //小数部分
                    var decLen = DecimalNum.length;
                    for (var i = 0; i < decLen; i++) {
                        var n = DecimalNum.substr(i, 1);
                        if (n != '0') {
                            ChineseStr += cnNums[Number(n)] + cnDecUnits[i];
                        }
                    }
                }
                if (ChineseStr == '') {
                    ChineseStr += cnNums[0] + cnIntLast + cnInteger;
                } else if (DecimalNum == '') {
                    ChineseStr += cnInteger;
                }
                ChineseStr = Symbol + ChineseStr;
                return ChineseStr;
            },
            /**
             * 版本比较函数
             * @param version1
             * @param version2
             * @returns {number}
             */
            compareVersion: function(version1, version2) {
                var arr1 = version1.split('.');
                var arr2 = version2.split('.');
                var length1 = arr1.length;
                var length2 = arr2.length;
                var minlength = Math.min(length1, length2);
                var i = 0;
                for (i ; i < minlength; i++) {
                    var a = parseInt(arr1[i]);
                    var b = parseInt(arr2[i]);
                    if (a > b) {
                        return 1;
                    } else if (a < b) {
                        return -1;
                    }
                }
                if (length1 > length2) {
                    for(var j = i; j < length1; j++) {
                        if (parseInt(arr1[j]) != 0) {
                            return 1;
                        }
                    }
                    return 0;
                } else if (length1 < length2) {
                    for(var j = i; j < length2; j++) {
                        if (parseInt(arr2[j]) != 0) {
                            return -1;
                        }
                    }
                    return 0;
                }
                return 0;
            },
            /**
             * 数字 转金额格式
             * 12345678,2,'$',',','.'
             * --> $12,345,678.00
             * @param number 数字
             * @param places 保留多少位
             * @param symbol 货币符号
             * @param thousand 整数部分千位分隔符
             * @param decimal 小数部分分隔符
             * @returns {string}
             */
            formatMoney: function(number, places, symbol, thousand, decimal) {
                number = number || 0;
                places = !isNaN(places = Math.abs(places)) ? places : 2;
                symbol = symbol !== undefined ? symbol : "$";
                thousand = thousand || ",";
                decimal = decimal || ".";
                var negative = number < 0 ? "-" : "",
                    i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
                    j = (j = i.length) > 3 ? j % 3 : 0;
                return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
            },

            /**
             * 播放音频
             * @param src 可以传网络url 也可传项目audio文件夹下面音频文件名称
             */
            playSound: function (src) {
                if (!(src.indexOf('http') == 0)) {
                    src = baseURL + 'static/audio/' + src + '.wav';
                }
                if (!!window.ActiveXObject || "ActiveXObject" in window) {  // IE
                    var embed = document.noticePlay;
                    if (embed) {
                        embed.remove();
                    }
                    embed = document.createElement('embed');
                    embed.setAttribute('name', 'noticePlay');
                    embed.setAttribute('src', src);
                    embed.setAttribute('autostart', true);
                    embed.setAttribute('loop', false);
                    embed.setAttribute('hidden', true);
                    document.body.appendChild(embed);
                    embed = document.noticePlay;
                    embed.volume = 100;
                } else {   // 非IE
                    var audio = document.createElement('audio');
                    audio.setAttribute('hidden', true);
                    audio.setAttribute('src', src);
                    document.body.appendChild(audio);
                    audio.addEventListener('ended', function () {
                        audio.parentNode.removeChild(audio);
                    }, false);
                    audio.play();
                }
            },

            /**
             * 发送AJax数据 Jq Ajax 原生方法添加防CSRF攻击
             * @param request
             */
            //@Deprecated
            sendAjax :function(config){
            	 console.warn('opt.common.sendAjax 方法即将弃用,请直接使用$.ajax(). 系统已经重载自动判断添加X-CSRF-Token');
                 //防CSRF攻击
                 if(opt.common.equalsIgnoreCase(config.type,'POST') && opt.common.isNotEmpty($('meta[name="csrf-token"]').attr("content"))){
                     config = opt.common.extend(config,{headers: {
                             "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content")
                         }});
                 }
                 if(opt.common.isEmpty(config.error) || typeof config.error != "function"){
                     config = opt.common.extend(config,{error:
                             function(xhr, textStatus) {
                                 try{
                                     opt.error(JSON.parse(xhr.responseText).msg || xhr.responseText );
                                 }catch (e) {
                                     console.error(xhr.responseText);
                                 }
                                 opt.modal.closeLoading();
                                 return;
                             }
                     });
                }
                $.ajax(config);
            },

            selectDate: function(value, startId,endId){
                var date;
                switch(value) {
                    case "0":
                        date = dateRangeUtil.getToDay();
                        break;
                    case "1":
                        var oneDayTime = 24 * 60 * 60 * 1000;
                        var dateNow = new Date();
                        var lastDay = new Date(dateNow.getTime() - 1 * oneDayTime);
                        date = new Array();
                        date.push(opt.common.formatDat(Date.parse(lastDay)));
                        date.push(opt.common.formatDat(Date.parse(lastDay)));
                        break;
                    case "2":
                        var date0 = dateRangeUtil.getCurrentWeek();
                        date = new Array();
                        date.push(opt.common.formatDat(Date.parse(date0[0])));
                        date.push(opt.common.formatDat(Date.parse(date0[1])));
                        break;
                    case "3":
                        var date0 = dateRangeUtil.getPreviousWeek();
                        date = new Array();
                        date.push(opt.common.formatDat(Date.parse(date0[0])));
                        date.push(opt.common.formatDat(Date.parse(date0[1])));
                        break;
                    case "4":
                        var date0 = dateRangeUtil.getPreviousMonth();
                        date = new Array();
                        date.push(opt.common.formatDat(Date.parse(date0[0])));
                        date.push(opt.common.formatDat(Date.parse(date0[1])));
                        break;
                    case "5":
                        var oneDayTime = 24 * 60 * 60 * 1000;
                        var dateNow = new Date();
                        date = new Array();
                        var lastDay = new Date(dateNow.getTime() - 30 * oneDayTime);
                        date.push(opt.common.formatDat(Date.parse(lastDay)));
                        date.push(opt.common.formatDat(Date.parse(dateNow)));
                        break;
                    case "6":
                        var oneDayTime = 24 * 60 * 60 * 1000;
                        var dateNow = new Date();
                        date = new Array();
                        var lastDay = new Date(dateNow.getTime() - 60 * oneDayTime);
                        date.push(opt.common.formatDat(Date.parse(lastDay)));
                        date.push(opt.common.formatDat(Date.parse(dateNow)));
                        break;
                    case "7":
                        var oneDayTime = 24 * 60 * 60 * 1000;
                        var dateNow = new Date();
                        date = new Array();
                        var lastDay = new Date(dateNow.getTime() - 90 * oneDayTime);
                        date.push(opt.common.formatDat(Date.parse(lastDay)));
                        date.push(opt.common.formatDat(Date.parse(dateNow)));
                        break;
                    case "8":
                        var date0 = dateRangeUtil.getCurrentSeason();
                        date = new Array();
                        date.push(opt.common.formatDat(Date.parse(date0[0])));
                        date.push(opt.common.formatDat(Date.parse(date0[1])));
                        break;
                    case "9":
                        var date0 = dateRangeUtil.getCurrentYear();
                        date = new Array();
                        date.push(opt.common.formatDat(Date.parse(date0[0])));
                        date.push(opt.common.formatDat(Date.parse(date0[1])));
                        break;
                    case "10":
                        var date0 = dateRangeUtil.getPreviousYear();
                        date = new Array();
                        date.push(opt.common.formatDat(Date.parse(date0[0])));
                        date.push(opt.common.formatDat(Date.parse(date0[1])));
                        break;
                    default:
                        date = new Array();
                        date.push("");
                        date.push("");
                        break;
                }
                $('#'+startId).val(date[0]);
                $('#'+endId).val(date[1]);
            },

            /**
             * 通过下载地址 静默下载 支持批量下载
             * @param files 为一个下载链接字符串为单个下载，若为下载数组链接字符串则批量循环下载
             */
            downLoadFile :function(files){
                if(opt.common.isString(files)){
                    var id = opt.common.randomString(10);
                    var judgeDiv = document.getElementById("dwDiv_"+id);
                    if(judgeDiv!=null){
                        document.body.removeChild(judgeDiv);
                    }
                    var divObj = document.createElement("div");
                    divObj.style.display = "none";
                    divObj.id="dwDiv_"+id;
                    var aObj  = document.createElement('a'); // 创建a标签
                    divObj.appendChild(aObj);
                    document.body.appendChild(divObj);
                    var e = document.createEvent('MouseEvents') // 创建鼠标事件对象
                    e.initEvent('click', false, false) // 初始化事件对象
                    aObj.href=encodeURI(files);
                    aObj.id = "hrefFile_"+id;
                    aObj.dispatchEvent(e);
                    judgeDiv = document.getElementById("dwDiv_"+id);
                    if(judgeDiv!=null){
                        document.body.removeChild(judgeDiv);
                    }
                }
                if(opt.common.isArray(files)){
                    files.forEach(function(url){
                        if (!!window.ActiveXObject || "ActiveXObject" in window) {  // IE
                            window.open(url, '_blank')
                        } else {
                            var a = document.createElement('a') // 创建a标签
                            var e = document.createEvent('MouseEvents') // 创建鼠标事件对象
                            e.initEvent('click', false, false) // 初始化事件对象
                            a.href = url // 设置下载地址
                            a.download = '' // 设置下载文件名
                            a.dispatchEvent(e)
                        }
                    })
                }
            },
            /**
             * POST请求下载文件
             *
             * @param datas = [{name: 'test1', value: '1'},{name: 'test2', value: '2'}]
             * @param url 下载链接
             */
            postDownLoadFile: function(datas, url){
                var form = document.createElement("form");
                form.style.display = "none";
                form.action = url;
                form.method = "post";
                document.body.appendChild(form);
                // 动态创建input并给value赋值
                for (var key in datas) {
                    var input = document.createElement("input");
                    input.type = "hidden";
                    input.name = datas[key].name;
                    input.value = datas[key].value;
                    form.appendChild(input);
                }
                form.submit();
                form.remove();
            },
            //
            digit: function(e, t) {
                var i = "";
                e = String(e),
                    t = t || 2;
                for (var n = e.length; n < t; n++)
                    i += "0";
                return e < Math.pow(10, t) ? i + (0 | e) : e
            },
            /**
             * 日期格式转换
             * @param data 日期
             * @param t 转换的格式
             */
            toDateString: function(data, t) {
                var that = this
                    , n = new Date(data || new Date)
                    , a = [that.digit(n.getFullYear(), 4), that.digit(n.getMonth() + 1), that.digit(n.getDate())]
                    , o = [that.digit(n.getHours()), that.digit(n.getMinutes()), that.digit(n.getSeconds())];
                return t = t || "yyyy-MM-dd HH:mm:ss",
                    t.replace(/yyyy/g, a[0]).replace(/MM/g, a[1]).replace(/dd/g, a[2]).replace(/HH/g, o[0]).replace(/mm/g, o[1]).replace(/ss/g, o[2])
            },
            formatDat: function(date, format) {
                var that = this;
                if(that.isEmpty(date)) return "";
                format = format || "yyyy-MM-dd";
                switch(typeof date) {
                    case "string":
                        date = new Date(date.replace(/-/g, "/"));
                        break;
                    case "number":
                        date = new Date(date);
                        break;
                }
                if (!date instanceof Date) return;
                var dict = {
                    "yyyy": date.getFullYear(),
                    "M": date.getMonth() + 1,
                    "d": date.getDate(),
                    "H": date.getHours(),
                    "m": date.getMinutes(),
                    "s": date.getSeconds(),
                    "MM": ("" + (date.getMonth() + 101)).substr(1),
                    "dd": ("" + (date.getDate() + 100)).substr(1),
                    "HH": ("" + (date.getHours() + 100)).substr(1),
                    "mm": ("" + (date.getMinutes() + 100)).substr(1),
                    "ss": ("" + (date.getSeconds() + 100)).substr(1)
                };
                return format.replace(/(yyyy|MM?|dd?|HH?|ss?|mm?)/g, function() {
                    return dict[arguments[0]];
                });
            },
            //HTML转义
            escape: function(html){
                return String(html||'').replace(/&(?!#?[a-zA-Z0-9]+;)/g, '&amp;')
                    .replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&#39;').replace(/"/g, '&quot;');
            },
            //反转义
            escapeHtml: function(value){
                return value.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&amp;/g, "&").replace(/&quot;/g, '"').replace(/&apos;/g, "'");
            },
            //url编码
            encodeUrl: function(a) {
                return encodeURIComponent(a)
            },
            //url解码
            decodeUrl: function(a) {
                return decodeURIComponent(a)
            },
            /**
             * 截取字符串考虑中文字符
             * @param str 源字符
             * @param len 长度
             * @param hasDot 超过长度是否用...代替
             * @returns 截取之后字符串
             */
            subString: function(str, len, hasDot) {
                var newLength = 0;
                var newStr = "";
                var chineseRegex = /[^\x00-\xff]/g;
                var singleChar = "";
                var strLength = str.replace(chineseRegex,"**").length;
                for(var i = 0;i < strLength;i++){
                    singleChar = str.charAt(i).toString();
                    if(singleChar.match(chineseRegex) != null)
                    {
                        newLength += 2;
                    }
                    else
                    {
                        newLength++;
                    }
                    if(newLength > len)
                    {
                        break;
                    }
                    newStr += singleChar;
                }
                if(hasDot && strLength > len){
                    newStr += "...";
                }
                return newStr;
            },
            /**
             * 字节转换
             */
            bytesToSize : function (bytes) {
                if (bytes === 0) return '0 B';
                var k = 1024;
                sizes = ['B','KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
                i = Math.floor(Math.log(bytes) / Math.log(k));
                var num = bytes / Math.pow(k, i);
                return num.toPrecision(3) + ' ' + sizes[i];
                //return (bytes / Math.pow(k, i)) + ' ' + sizes[i];
                //toPrecision(3) 后面保留一位小数，如1.0GB //return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
            },
            /**
             * Get file name from path(获取文件路径获取文件名)
             * @param {String} file path to file
             * @return filename
             */
            fileFromPath: function(file){
                return file.replace(/.*(\/|\\)/, "");
            },

            /**
             * Get file extension lowercase(获取文件后缀名称)
             * @param {String} file name
             * @return file extenstion
             */
            getExt: function(file){
                return (-1 !== file.indexOf('.')) ? file.replace(/.*[.]/, '') : '';
            },

            /**
             * Repair the path(http修复路径)
             * @param path
             * @returns {string}
             */
            optimizationPath: function(path) {
                var protocol = /^[a-z]+:\/\//.exec(path)[0],
                    tmp = null,
                    res = [];
                path = path.replace(protocol, "").split("?")[0].split("#")[0];
                path = path.replace(/\\/g, '/').split(/\//);
                path[path.length - 1] = "";
                while (path.length) {
                    if (( tmp = path.shift() ) === "..") {
                        res.pop();
                    } else if (tmp !== ".") {
                        res.push(tmp);
                    }
                }
                return protocol + res.join("/");
            }
        },
        // 弹出层封装处理
        modal: {
            // 显示图标
            icon: function(type) {
                var icon = "";
                if (type == opt.variable.modal_status.WARNING) {
                    icon = 0;
                } else if (type == opt.variable.modal_status.SUCCESS) {
                    icon = 1;
                } else if (type == opt.variable.modal_status.FAIL) {
                    icon = 2;
                } else {
                    icon = 3;
                }
                return icon;
            },
            // 消息提示
            msg: function(content, type) {
                if (type != undefined) {
                    // layui.use('layer', function(){
                    //     var layer = layui.layer;
                    //     layer.msg(content, { icon: opt.modal.icon(type), time: 1000, shift: 5 });
                    // });
                    opt.selfLayer.msg(content, { icon: opt.modal.icon(type), time: 1000, shift: 5 });
                } else {
                    opt.selfLayer.msg(content);
                }
            },
            // 错误消息
            msgError: function(content) {
                opt.modal.msg(content,  opt.variable.modal_status.FAIL);
            },
            // 成功消息
            msgSuccess: function(content) {
                opt.modal.msg(content,  opt.variable.modal_status.SUCCESS);
            },
            // 警告消息
            msgWarning: function(content) {
                opt.modal.msg(content,  opt.variable.modal_status.WARNING);
            },
            // 弹出提示
            alert: function(content, type,shadeClose) {
                opt.selfLayer.alert(content, {
                    icon: opt.modal.icon(type),
                    title: $.i18n.prop("系统提示"),
                    shadeClose: opt.common.isEmpty(shadeClose) ? true : shadeClose,
                    btn: ['<i class="fa fa-check"></i> '+$.i18n.prop("确定")],
                    btnclass: ['btn btn-primary'],
                });
            },
            // 消息提示并刷新父窗体
            msgReload: function(msg, type) {
                opt.selfLayer.msg(msg, {
                        icon: opt.modal.icon(type),
                        time: 500
                        //,shade: [0.4, '#8F8F8F']
                    },
                    function() {
                        opt.modal.reload();
                    });
            },
            // 错误提示
            alertError: function(content) {
                opt.modal.alert(content,  opt.variable.modal_status.FAIL);
            },
            // 成功提示
            alertSuccess: function(content) {
                opt.modal.alert(content,  opt.variable.modal_status.SUCCESS);
            },
            // 成功提示
            alertInfo: function(content) {
                opt.modal.alert(content, "");
            },
            // 成功提示
            success: function(msg, callback) {
                opt.selfLayer.alert(msg,{
                    icon: 1,
                    closeBtn: 0,
                    title: $.i18n.prop("系统提示"),
                    anim: 1,
                    // skin: 'layui-layer-molv',
                    yes:function(index){
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                        opt.selfLayer.close(index);
                    }
                });
            },
            error : function(msg,callback){
                opt.error(msg,callback);
            },
            warning :function(msg,callback){
                opt.warning(msg,callback);
            },
            // 警告提示
            alertWarning: function(content) {
                opt.modal.alert(content, opt.variable.modal_status.WARNING,true);
            },
            // 关闭窗体
            close: function () {
                var index = opt.selfLayer.getFrameIndex(window.name);
                if(opt.common.isNotEmpty(index)){
                    opt.selfLayer.close(index);
                }else{
                    opt.layer.closeAll();
                }

            },
            // 关闭全部窗体
            closeAll: function () {
                opt.selfLayer.closeAll();
                opt.layer.closeAll();
            },
            // 确认窗体
            confirm: function (content, callback, shadeClose) {
                opt.selfLayer.confirm(content, {
                    icon: 3,
                    shadeClose: opt.common.isEmpty(shadeClose) ? true : shadeClose,
                    title: '<i class="fa fa-exclamation-circle"></i> '+$.i18n.prop("系统提示"),
                    btn: ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")]
                }, function (index) {
                    if(typeof(callback) === "function"){
                        callback("ok");
                    }
                    opt.selfLayer.close(index);
                });
            },
            /**
             * 右侧弹出窗口 一般用于信息查看用
             * @param title 标题
             * @param url 展示地址
             * @param isEdit 是否显示修改按钮
             * @param editFun 修改方法
             */
            popupRight: function(title, url, isEdit,editFun,id){
                var width = 150;
                var topp = 50;
                if(top.location !== self.location) {
                    if($(top.window).outerWidth() < 400){
                        width = 50;
                    }
                }else{
                    topp = 0;
                    if($(window).outerWidth() < 400){
                        width = 50;
                    }
                }
                opt.layer.open({
                    type: 2,
                    //shade: false,
                    scrollbar:false,
                    anim:-1,
                    closeBtn: 0,
                    //shade: 0.3,
                    move: false,
                    title: title,
                    shadeClose: true,
                    outLeft: true,
                    skin:'layui-anim layui-anim-rl',
                    offset: [topp +'px', ($(top.window?top.window:window).outerWidth() -($(window).outerWidth()-width))  + 'px'],
                    area: [($(window).outerWidth()-width) + 'px', '100%'],
                    success: function(layero, index){
                        if(opt.common.isNotEmpty(isEdit) && isEdit){
                            var update = $('<div id="'+index+'" class="layui-right-update"><i class="fa fa-mail-reply-all"></i> '+$.i18n.text("修改")+'</div>').on('click',function(){
                                opt.layer.close($(this).attr('id'));
                                if( typeof  editFun == "function"){
                                    window.setTimeout(function(){
                                        editFun(id);
                                    },200);
                                }
                            });
                            if(!$(layero).find('.layui-right-update').html()){
                                $(layero).find('.layui-layer-title').attr("style","font-size:13px;").before(update);
                            }
                        }else{
                            var update = $('<div id="'+index+'" class="layui-right-update"><i class="fa fa fa-times-circle-o"></i> '+$.i18n.text("关闭")+'</div>').on('click',function(){
                                opt.layer.close($(this).attr('id'));
                            });
                            if(!$(layero).find('.layui-right-update').html()){
                                $(layero).find('.layui-layer-title').attr("style","font-size:13px;").before(update);
                            }
                        }
                    },
                    content: url
                });
            },
            /**
             * 弹出层指定宽度 此方法弹出窗口会在宽度高度做自动适配
             * 如果 width height 你设置了数值,当你设置的数值大于当前窗口的最大值则会全屏展示
             *
             * @param title 弹出窗口Title *必输
             * @param url 弹出窗口URL  *必输
             * @param width 指定弹出窗口宽度 [非必输]
             * @param height 指定弹出窗口高度 [非必输]
             * @param callback 弹出窗口点击确定按钮回调 弹出本页函数 [非必输] 如果不输入 则回调弹出的页面submitHandler 方法
             * @param yes [非必输] 只有在传 true 则先回调弹出层submitHandler 方法如果此submitHandler方法返回true,则再回调 callback 方法
             */
            open: function (title, url,width, height,callback,type) {
                var full = false;
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    width = 'auto';
                    height = 'auto';
                    full = true;
                }
                // if (opt.common.isEmpty(title)) {
                //     title = false;
                // }
                title = title || false;
                // if (opt.common.isEmpty(url)) {
                //     url = "/404.html";
                // }
                url = url || "/404.html";
                // if (opt.common.isEmpty(width)) {
                //     width = 800;
                // }
                width = width || 800;
                if (opt.common.isEmpty(height)) {
                    height = ($(window).height() - 50);
                }
                // if (opt.common.isEmpty(type)) {
                //     type = 2;
                // }
                type = type || 2;
                //自动适配窗口大小 如果传的大小比所在窗口大 则最大化
                if(width !== 'auto' || height !== 'auto'){
                    if(width > $(window).width() || height > $(window).height() ){
                        full = true;
                    }
                }

                var submit;

                // 查看是否传回调函数
                if(!opt.common.isEmpty(callback) && typeof callback == 'function'){
                    submit = function(index, layero) {
                        var iframeWin = layero.find('iframe')[0];
                        if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                            if(iframeWin.contentWindow.submitHandler(index, layero)){
                                callback(index,layero,opt.selfLayer);
                                return;
                            }
                        }else{
                            callback(index,layero,opt.selfLayer);
                            return;
                        }
                    }
                }else{
                    submit = function(index, layero) {
                        var iframeWin = layero.find('iframe')[0];
                        if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                            iframeWin.contentWindow.submitHandler(index, layero)
                            return;
                        }else{
                            opt.selfLayer.close(index);
                            return;
                        }
                    }
                }

                //opt.modal.loading($.i18n.prop("数据加载中，请稍后..."));

                var index = opt.selfLayer.open({
                    type: type,
                    area: [width + 'px', height + 'px'],
                    fix: false,
                    //不固定
                    maxmin: true,
                    //shade: 0.3,
                    title: title,
                    content: url,
                    btn: ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")],
                    // 弹层外区域关闭
                    shadeClose: true,
                    yes:submit,
                    success: function(layero, index){
                        // var ifWin = window[layero.find('iframe')[0]['name']];
                        // $("#_addTab",ifWin.document).addClass("hide");
                        // if (!opt.common.isEmpty(obj)) {
                        var iframeWin = layero.find('iframe')[0];
                        if(!opt.common.isEmpty(iframeWin.contentWindow.onLoadSuccess)){
                            iframeWin.contentWindow.onLoadSuccess(index,layero,opt.selfLayer);
                        }
                        // }
                        //opt.modal.closeLoading();
                    },
                    cancel: function(index) {
                        return true;
                    },
                    end: function(){
                        //opt.modal.closeLoading();
                    }
                });

                if(full){
                    opt.selfLayer.full(index);
                }

            },
            /**
             * btn : 按钮
             * url : 访问连接
             * title : 窗口标题
             * width : 窗口宽
             * height : 窗口高
             * fromData : POST 提交的参数
             * type : 基本层类型
             * @param options
             */
            // 弹出层指定参数选项
            openOptions: function (options) {
                var _btn = opt.common.isEmpty(options.btn) ? [] : options.btn;
                var _url = opt.common.isEmpty(options.url) ? "/404.html" : options.url;
                var _title = opt.common.isEmpty(options.title) ? $.i18n.prop("系统窗口") : $.i18n.prop(options.title);
                var _width = opt.common.isEmpty(options.width) ? "800" : options.width;
                var _height = opt.common.isEmpty(options.height) ? ($(window).height() - 50) : options.height;
                var _framData = opt.common.isEmpty(options.fromData) ? {} : options.fromData;
                var _type = opt.common.isEmpty(options.type) ? 2 : options.type;
                var _auto = opt.common.isEmpty(options.auto) ? true : options.auto;
                var _closeBtn = opt.common.isEmpty(options.closeBtn) ? 1: options.closeBtn;
                var _maxmin = opt.common.isEmpty(options.maxmin) ? true: options.maxmin;
                var full = false;

                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    _width = 'auto';
                    _height = 'auto';
                    full = true;
                }

                if(_auto){
                    //自动适配窗口大小 如果传的大小比所在窗口大 则最大化
                    if(_width !== 'auto' || _height !== 'auto'){
                        if(_width > $(window).width() || _height > $(window).height() ){
                            full = true;
                        }
                    }
                }

                var index;
                if(options.clear){
                    if(_btn.length == 0){
                        _btn = ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-trash-o"></i> '+$.i18n.prop("清除"),'<i class="fa fa-close"></i> '+$.i18n.prop("取消")];
                    }
                }else{
                    if(_btn.length == 0){
                        _btn = ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")];
                    }
                }
                var _sf;
                if(opt.common.isNotEmpty(options.main) && options.main){
                    _sf = opt.layer;
                    _height = opt.common.isEmpty(options.height) ? ($(top.window).height() - 220) : options.height;
                }else{
                    _sf = opt.selfLayer;
                }

                if (opt.common.isEmpty(options.yes)) {
                    options.yes = function(index, layero) {
                        var iframeWin = layero.find('iframe')[0];
                        if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                            if(iframeWin.contentWindow.submitHandler(index, layero,_sf)){
                                if(typeof  options.callBack == 'function'){
                                    options.callBack(index, layero,_sf);
                                }
                            }
                            return;
                        }else{
                            if(typeof  options.callBack == 'function'){
                                options.callBack(index, layero,_sf);
                                return;
                            }
                        }
                    }
                }

                index = _sf.open({
                    type: _type,
                    maxmin: _maxmin,
                    //shade: 0.3,
                    title: _title,
                    closeBtn: _closeBtn,
                    fix: false,
                    area: [opt.common.endWith(_width,'%')?_width: ( _width+ 'px'), opt.common.endWith(_height,'%')?_height: ( _height+ 'px')],
                    content: _url,
                    fromData: _framData,
                    shadeClose: opt.common.isEmpty(options.shadeClose) ? true : options.shadeClose,
                    skin: options.skin,
                    btn: _btn,
                    yes: options.yes,
                    //取消或者清除
                    btn2: function(index, layero){
                        if(options.clear){
                            options.clear(index, layero,_sf);
                        }else{
                            if (!opt.common.isEmpty(options.cancel)) {
                                options.cancel(index,layero);
                            }
                            _sf.close(index);
                        }
                    },
                    btn3: function(index, layero){
                        if (!opt.common.isEmpty(options.cancel)) {
                            options.cancel(index,layero);
                        }
                        _sf.close(index);
                    },
                    success: function(layero, index){
                        // if (!opt.common.isEmpty(options.obj)) {
                        var iframeWin = layero.find('iframe')[0];
                        //判断页面是否有初始方法有就执行
                        if(typeof(iframeWin.contentWindow.onLoadSuccess) === "function"){
                            iframeWin.contentWindow.onLoadSuccess(options.obj,layero, index,_sf);
                        }
                        // }
                    },
                    cancel: function(index, layero){
                        if (!opt.common.isEmpty(options.cancel)) {
                            options.cancel(index,layero);
                        }
                        return true;
                    },
                    end: function(){
                        if (!opt.common.isEmpty(options.end)) {
                            options.end(index,opt.layer);
                        }
                        return true;
                    }
                });

                if(full){
                    _sf.full(index);
                }
            },
            /**
             * 窗口有确定 取消按钮
             * 在主窗口弹出窗口  url *必输 title *必输 callBack 非必输  but  非必输
             * callBack 如果没传 在弹出主窗口 的确定 事件没有 则会直接关掉窗口
             * but 弹出窗口是否自带确定取消按钮
             * 当弹出窗口会检测弹出窗口页面里面是否有onLoadSuccess 方法 如果有会执行此方法
             * options = { url: "",title:"", callBack:"" , but: false}
             * @param options
             */
            openMainWin :function(options){
                var _url = opt.common.isEmpty(options.url) ? "/404.html" : options.url;
                var _title = opt.common.isEmpty(options.title) ? $.i18n.prop("系统窗口") : $.i18n.prop(options.title);
                var _width = opt.common.isEmpty(options.width) ? $(top.window).width() - 100 : options.width;
                var _height = opt.common.isEmpty(options.height) ? $(top.window).height() - 100 : options.height;
                var _closeBtn = opt.common.isEmpty(options.closeBtn) ? 1: options.closeBtn;
                var _framData = opt.common.isEmpty(options.fromData) ? {} : options.fromData;
                var _maxmin = opt.common.isEmpty(options.maxmin) ? true: options.maxmin;
                var full = false;
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    _width = 'auto';
                    _height = 'auto';
                    full = true;
                }
                var index;
                //自动适配窗口大小 如果传的大小比所在窗口大 则最大化
                if(_width !== 'auto' || _height !== 'auto'){
                    if(_width > $(top.window).width() || _height > $(top.window).height() ){
                        full = true;
                    }
                }

                if(opt.common.isEmpty(options.but)){
                    options.but = true;
                }

                if(options.but){
                    if(options.clear){
                        _btn = ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-trash-o"></i> '+$.i18n.prop("清除"),'<i class="fa fa-close"></i> '+$.i18n.prop("取消")];
                    }else{
                        _btn = ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")];
                    }

                    if (!opt.common.isEmpty(options.callBack)) {
                        options.yes = function(index, layero) {
                            var iframeWin = layero.find('iframe')[0];
                            if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                                if(iframeWin.contentWindow.submitHandler(index, layero)){
                                    window.setTimeout(function(){
                                        options.callBack(index,layero,opt.layer);
                                    },100);
                                }
                            }else{
                                /**
                                 * 注意返回入参
                                 */
                                options.callBack(index,layero,opt.layer);
                            }
                        }
                    }else{
                        options.yes = function(index, layero) {
                            var iframeWin = layero.find('iframe')[0];
                            if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                                if(iframeWin.contentWindow.submitHandler(index, layero)){
                                    window.setTimeout(function(){
                                        opt.layer.close(index);
                                    },100);
                                }
                            }else{
                                opt.layer.close(index);
                            }
                        }
                    }

                    index =  opt.layer.open({
                        type: 2,
                        maxmin: _maxmin,
                        closeBtn: _closeBtn,
                        shadeClose: opt.common.isEmpty(options.shadeClose) ? true : options.shadeClose,
                        title: options.title === false? false : _title,
                        area: [_width+'px',
                            _height + 'px'],
                        scrollbar: false,
                        content:_url,
                        fromData: _framData,
                        success: function(layero, index){
                            var iframeWin = layero.find('iframe')[0];
                            //判断页面是否有
                            if(typeof(iframeWin.contentWindow.onLoadSuccess) === "function"){
                                if(opt.common.isEmpty(options.obj)){
                                    iframeWin.contentWindow.onLoadSuccess(index,layero,opt.layer);
                                }else{
                                    iframeWin.contentWindow.onLoadSuccess(options.obj,index,layero,opt.layer);
                                }
                            }
                        },
                        btn: opt.common.isEmpty(options.btn) ? _btn : options.btn,
                        yes: options.yes,
                        //取消或者清除
                        btn2: function(index, layero){
                            if(options.clear){
                                options.clear(index, layero,opt.layer);
                            }else{
                                opt.layer.close(index);
                            }
                        },
                        btn3: function(index, layero){
                            opt.layer.close(index);
                        },
                        cancel: function(index, layero){
                            if (!opt.common.isEmpty(options.cancel)) {
                                options.cancel(index,layero);
                            }
                            //opt.layer.close(index);
                            return true;
                        },
                        end: function(){
                            if (!opt.common.isEmpty(options.end)) {
                                options.end(index,opt.layer);
                            }
                            return true;
                        }
                    });
                }else{
                    index =  opt.layer.open({
                        type: 2,
                        maxmin: _maxmin,
                        closeBtn: _closeBtn,
                        shadeClose: opt.common.isEmpty(options.shadeClose) ? true : options.shadeClose,
                        title: options.title === false? false : _title,
                        area: [_width+'px',
                            _height + 'px'],
                        content:_url,
                        success: function(layero, index){
                            opt.selfLayer = opt.layer;
                            var iframeWin = layero.find('iframe')[0];
                            //判断页面是否有
                            if(typeof(iframeWin.contentWindow.onLoadSuccess) === "function"){
                                if(opt.common.isEmpty(options.obj)){
                                    iframeWin.contentWindow.onLoadSuccess(index,layero,opt.layer);
                                }else{
                                    iframeWin.contentWindow.onLoadSuccess(options.obj,index,layero,opt.layer);
                                }
                            }
                        },
                        cancel: function(index, layero){
                            if (!opt.common.isEmpty(options.cancel)) {
                                options.cancel(index,layero);
                            }
                            return true;
                        },
                        end: function(){
                            if (!opt.common.isEmpty(options.end)) {
                                options.end(index,opt.layer);
                            }
                            return true;
                        }
                    });
                }

                if(full){
                    opt.layer.full(index);
                }
            },
            // 弹出层全屏 本窗口
            openFull: function (title, url, width, height) {
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    width = 'auto';
                    height = 'auto';
                }
                if (opt.common.isEmpty(title)) {
                    title = false;
                }
                if (opt.common.isEmpty(url)) {
                    url = "/404.html";
                }
                if (opt.common.isEmpty(width)) {
                    width = 800;
                }
                if (opt.common.isEmpty(height)) {
                    height = ($(window).height() - 50);
                }
                var index = opt.selfLayer.open({
                    type: 2,
                    area: [width + 'px', height + 'px'],
                    fix: false,
                    //不固定
                    maxmin: true,
                    //shade: 0.3,
                    title: title,
                    content: url,
                    btn: ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")],
                    // 弹层外区域关闭
                    shadeClose: true,
                    yes: function(index, layero) {
                        var iframeWin = layero.find('iframe')[0];
                        iframeWin.contentWindow.submitHandler(index, layero);
                    },
                    cancel: function(index) {
                        return true;
                    }
                });
                opt.selfLayer.full(index);
            },
            //预览文件
            openView:function(name,url,width,height){
                var url = baseURL + "sys/component/fileViwe?fileName="+ opt.common.encodeUrl(name) +
                    "&fileUrl=" + opt.common.encodeUrl(url);
                width = opt.common.isEmpty(width)? $(top.window).width() - 200:width;
                height = opt.common.isEmpty(height)? $(top.window).height() - 50:height;
                opt.layer? opt.layer.open({
                    type: 2,
                    maxmin: false,
                    shadeClose: true,
                    title: false,
                    area: [width + "px", height + "px"],
                    content: url
                }):opt.modal.windowOpen(url);
            },
            windowOpen: function(url, title, width, height) {
                width && height || (width = window.screen.width - 200,
                    height = window.screen.height - 150);
                var top = parseInt((window.screen.height - height) / 2 - 20, 10)
                    , left = parseInt((window.screen.width - width) / 2, 10);
                window.open(url, title, "location=no,menubar=no,toolbar=no," +
                    "dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,resizable=yes," +
                    "scrollbars=yes,width=" + width + ",height=" + height + ",top=" + top + ",left=" + left)
            },
            // 选卡页方式打开
            openTab: function (title, url, icon) {
                opt.createMenuItem(url, title, icon);
            },
            // 选卡页同一页签打开
            parentTab: function (title, url) {
                var dataId = window.frameElement.getAttribute('data-id');
                opt.createMenuItem(url, title);
                opt.closeItem(dataId);
            },
            // 关闭选项卡
            closeTab: function (dataId) {
                opt.closeItem(dataId);
            },
            // 禁用按钮
            disable: function() {
                var doc = window.top == window.parent ? window.document : window.parent.document;
                try {
                    $("a[class*=layui-layer-btn]", doc).addClass("layer-disabled");
                    $('button[class="btn btn-sm btn-primary"]',doc).attr("disabled",true);
                    // $('button[class="btn btn-sm btn-primary"]',doc).addClass("layer-disabled");
                }catch (e) {}
            },
            // 启用按钮
            enable: function() {
                var doc = window.top == window.parent ? window.document : window.parent.document;
                try{
                    $("a[class*=layui-layer-btn]", doc).removeClass("layer-disabled");
                    $('button[class="btn btn-sm btn-primary"]',doc).attr("disabled",false);
                }catch (e) {}
            },
            // 打开遮罩层
            loading: function (message) {
                if(opt.common.isEmpty(message)){
                    $.blockUI();
                }else{
                    $.blockUI({ message: '<div class="loaderbox"><div class="loading-activity"></div> ' + message + '</div>' });
                }
            },
            // 关闭遮罩层
            closeLoading: function () {
                // if(top.location != self.location){
                //     setTimeout(function(){
                //         $('.content-wrapper', window.parent.document).unblock();
                //     }, 50);
                // }else{
                setTimeout(function(){
                    $.unblockUI();
                }, 50);
                // }
            },
            // 立即关闭遮罩层
            closeNowLoading: function () {
                if(top.location != self.location){
                    $('.content-wrapper', window.parent.document).unblock();
                }else{
                    $.unblockUI();
                }
            },
            // 重新加载
            reload: function () {
                parent.location.reload();
            }
        },
        // 操作封装处理
        operate: {
            // 提交数据
            submit: function(url, type, dataType, data, callback,del) {
                var config = {
                    url: url,
                    type: type,
                    dataType: dataType,
                    data: data,
                    beforeSend: function () {
                        opt.modal.loading("正在处理中，请稍后...");
                    },
                    success: function(result) {
                        //判断如果时删除提交且表格有记住我 需要删除记住我里面删除的数据
                        // if (result.code == opt.variable.web_status.SUCCESS && opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                        //     if(!opt.common.isEmpty(del) && del){
                        //         if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                        //             var s = opt.common.getJsonValue(data,"ids").split(',');
                        //             var column = opt.common.isEmpty(opt.table.options.uniqueId) ? opt.table.options.columns[1].field : opt.table.options.uniqueId;
                        //             var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                        //             var p = [];
                        //             if(opt.common.isNotEmpty(selectedRows)) {
                        //                 for (var j = 0; j < selectedRows.length; j++) {
                        //                     for(var i=0; i< s.length; i++){
                        //                         if(opt.common.getJsonValue(selectedRows[j],column) === s[i]){
                        //                             p[p.length] = selectedRows[j];
                        //                             selectedRows.splice(j, 1);
                        //                             j = j - 1;
                        //                         }
                        //                     }
                        //                 }
                        //             }
                        //             var selectedIds = opt.table.rememberSelectedIds[opt.table.options.id];
                        //             if(opt.common.isNotEmpty(selectedIds)) {
                        //                 for (var j = 0; j < selectedIds.length; j++) {
                        //                     for(var i=0; i< p.length; i++){
                        //                         if(opt.common.getJsonValue(p[i],column) === selectedIds[j]){
                        //                             selectedIds.splice(j, 1);
                        //                             j = j - 1;
                        //                         }
                        //                     }
                        //                 }
                        //             }
                        //         }
                        //     }
                        // }

                        if (typeof callback == "function") {
                            callback(result);
                        }
                        opt.operate.ajaxSuccess(result);
                    }
                };
                $.ajax(config);
            },
            //删除单独调用post删除
            delPost:function(url, data, callback) {
                //重置清空页面记住我数据
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    opt.table.rememberSelecteds = {};
                    opt.table.rememberSelectedIds = {};
                }
                opt.operate.submit(url, "post", "json", data, callback);
            },
            // post请求传输
            post: function(url, data, callback) {
                opt.operate.submit(url, "post", "json", data, callback);
            },
            // get请求传输
            get: function(url, callback) {
                opt.operate.submit(url, "get", "json", "", callback);
            },
            /**
             *  弹出详细信息
             * @param id url带参数
             * @param title 弹出title 默认为表格参数 table.options.modalName + "详细"
             * @param width 弹出宽度
             * @param height 弹出高度
             */
            detail: function(id,title, width, height) {
                opt.table.set();
                var _url = opt.operate.detailUrl(id);
                var _width = opt.common.isEmpty(width) ? "800" : width;
                var _height = opt.common.isEmpty(height) ? ($(window).height() - 50) : height;
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    _width = 'auto';
                    _height = 'auto';
                }
                var options = {
                    title: opt.common.isEmpty(title)? $.i18n.text("{0}详细",opt.table.options.modalName):title,
                    width: _width,
                    height: _height,
                    url: _url,
                    //skin: 'layui-layer-gray',
                    btn: [$.i18n.text("关闭")],
                    yes: function (index,layero) {
                        opt.layer.close(index) || opt.selfLayer.close(index);
                    }
                };
                opt.modal.openOptions(options);
            },
            // 详细访问地址
            detailUrl: function(id) {
                var url = "/404.html";
                if (opt.common.isNotEmpty(id)) {
                    url = opt.table.options.detailUrl.replace("{id}", id);
                } else {
                    var id = opt.common.isEmpty(opt.table.options.uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(opt.table.options.uniqueId);
                    if (id.length == 0) {
                        opt.modal.alertWarning("请至少选择一条记录");
                        return;
                    }
                    url = opt.table.options.detailUrl.replace("{id}", id);
                }
                return url;
            },
            // 删除信息
            del: function(id,tableId) {

                if(opt.common.isNotEmpty(tableId)){
                    opt.table.set(tableId);
                }else{
                    opt.table.set();
                }
                if (opt.common.isEmpty( opt.table.options.delUrl)){
                    opt.modal.msgError("delUrl 未传!");
                    return;
                }
                //表格编辑回调
                if(typeof bootTableCallback === 'function'){
                    //获取当前编辑数据
                    var rows = $.table.selectAllColumnRows(tableId);
                    rows = rows.length ==0?[$.table.getRowByUniqueId(id)]:rows;
                    //当回调函数返回false 代码终止
                    if(!bootTableCallback(opt.table.options.id,'del',rows)){
                        return;
                    }
                }
                opt.modal.confirm($.i18n.text("确定删除该条{0}信息吗?",opt.table.options.modalName), function() {
                    if(opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                        var row = $("#" + opt.table.options.id).bootstrapTreeTable('getSelections')[0];
                        var url;
                        if (opt.common.isEmpty(row)) {
                            url = opt.table.options.delUrl;
                        }else{
                            url = opt.table.options.delUrl.replace("{id}",  row[opt.table.options.uniqueId]);
                        }
                        opt.operate.get(url);
                    } else {
                        var url = opt.common.isEmpty(id) ? opt.table.options.delUrl : opt.table.options.delUrl.replace("{id}", id);
                        var data = { "ids": id };
                        opt.operate.submit(url, "POST", "json", data,"",true);
                    }
                });
            },
            /**
             * 处理信息
             * @param id
             */
            exe:function(id){
                opt.table.set();
                opt.modal.confirm("确定处理该条" + opt.table.options.modalName + "信息吗？", function() {
                    var url = opt.common.isEmpty(id) ? opt.table.options.exeUrl : opt.table.options.exeUrl.replace("{id}", id);
                    if(opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                        opt.operate.get(url);
                    } else {
                        var data = { "ids": id };
                        opt.operate.submit(url, "POST", "json", data);
                    }
                });
            },
            // 批量删除信息
            delAll: function(tableId,tip) {
                if(opt.common.isEmpty(tableId)){
                    opt.table.set();
                }else{
                    opt.table.set(tableId);
                }
                if (opt.common.isEmpty( opt.table.options.delUrl)){
                    opt.modal.msgError("delUrl 未传!");
                    return;
                }

                //表格编辑回调
                if(typeof bootTableCallback === 'function'){
                    //获取当前编辑数据
                    var rows = $.table.selectAllColumnRows(tableId);
                    //当回调函数返回false 代码终止
                    if(!bootTableCallback(opt.table.options.id,'del',rows)){
                        return;
                    }
                }
                var rows  = opt.common.isEmpty(opt.table.options.uniqueId) ?
                    $.table.selectFirstColumns() : $.table.selectColumns(opt.table.options.uniqueId);
                if (opt.common.isEmpty(rows)) {
                    opt.modal.error("请至少选择一条记录");
                    return;
                }
                opt.modal.confirm($.i18n.text("确认要{0}选中的{1}条数据吗?",
                    '<span style="color:red;font-size: 18px;font-weight: 600">'+$.i18n.text("删除")+'</span>',
                    '<span style="font-weight: 600;color: #000;font-size: 18px;">'+rows.length+'</span>') +
                    (opt.common.isNotEmpty(tip)?tip:""), function() {
                    var url = opt.table.options.delUrl;
                    var data = { "ids": rows.join() };
                    opt.operate.submit(url, "POST", "json", data,"",true);
                });
            },
            // 清空信息
            clean: function() {
                opt.table.set();
                opt.modal.confirm("确定清空所有" + opt.table.options.modalName + "吗？", function() {
                    var url = opt.table.options.cleanUrl;
                    opt.operate.submit(url, "post", "json", "");
                });
            },
            // 添加信息
            add: function(id) {
                opt.table.set();
                opt.modal.open($.i18n.text("新增{0}",opt.table.options.modalName), opt.operate.addUrl(id));
            },
            //处理信息
            exeDis: function(id,title) {
                opt.table.set();
                opt.modal.open($.i18n.text("处理[{0}]",title), opt.operate.exeUrl(id));
            },
            // 添加信息，以tab页展现
            addTab: function (id) {
                opt.table.set();
                opt.modal.openTab($.i18n.text("新增{0}",opt.table.options.modalName), opt.operate.addUrl(id));
            },
            // 添加信息 全屏
            addFull: function(id) {
                opt.table.set();
                var url = opt.common.isEmpty(id) ? opt.table.options.addUrl : opt.table.options.addUrl.replace("{id}", id);
                opt.modal.openFull($.i18n.text("新增{0}" , opt.table.options.modalName), url);
            },
            // 添加访问地址
            addUrl: function(id) {
                opt.table.set();
                var url = opt.common.isEmpty(id) ?  opt.table.options.addUrl.replace("{id}", "") :  opt.table.options.addUrl.replace("{id}", id);
                return url;
            },
            exeUrl: function(id) {
                opt.table.set();
                var url = opt.common.isEmpty(id) ?  opt.table.options.exeUrl.replace("{id}", "") :  opt.table.options.exeUrl.replace("{id}", id);
                return url;
            },
            /**
             * 修改信息
             * @param id 修改数据ID
             * @param tableId 表格对象ID
             */
            edit: function(id,tableId) {
                opt.table.set(tableId);
                if (opt.common.isEmpty( opt.table.options.editUrl)){
                    opt.modal.msgError("editUrl 未传!");
                    return;
                }
                //表格编辑回调
                if(typeof bootTableCallback === 'function'){
                    //获取当前编辑数据
                    var rows;
                    if(opt.common.isEmpty(id)){
                        //获取当前编辑数据
                        rows = $.table.selectAllColumnRows(tableId);
                        rows = rows.length ==0?[$.table.getRowByUniqueId(id)]:rows;
                    }else{
                        rows = [$.table.getRowByUniqueId(id)];
                    }
                    //当回调函数返回false 代码终止
                    /**
                     * 回调参数
                     * ID 表格对象ID
                     * event 方法名称
                     * data 当前数据
                     */
                    if(!bootTableCallback(opt.table.options.id,'edit',rows)){
                        return;
                    }
                }
                if(opt.common.isEmpty(id) &&  opt.table.options.type ==  opt.variable.table_type.bootstrapTreeTable) {
                    var row = $("#" + opt.table.options.id).bootstrapTreeTable('getSelections')[0];
                    if (opt.common.isEmpty(row)) {
                        opt.modal.error("请至少选择一条记录");
                        return;
                    }
                    var url = opt.table.options.editUrl.replace("{id}", row[opt.table.options.uniqueId]);
                    opt.modal.open($.i18n.text("修改{0}",opt.table.options.modalName), url);
                } else {
                    opt.modal.open($.i18n.text("修改{0}",opt.table.options.modalName), opt.operate.editUrl(id));
                }
            },
            // 查看表详情
            view: function(id,edit,fun){
                var _self = this;
                opt.table.set();
                if (opt.common.isEmpty( opt.table.options.viewUrl)){
                    opt.modal.msgError("viewUrl 未传!");
                    return;
                }
                if(opt.common.isEmpty(id)){
                    opt.modal.error("id必须传!");
                    return;
                }
                if(opt.common.isEmpty(edit)){
                    edit = false;
                }else{
                    edit = true;
                }
                var url = opt.table.options.viewUrl.replace("{id}", id);
                opt.modal.popupRight($.i18n.text("{0}信息详情",opt.table.options.modalName),url,
                    edit,fun,id);
            },
            // 修改信息，以tab页展现
            editTab: function(id,prefix,tableId) {
                opt.table.set();
                if (opt.common.isEmpty( opt.table.options.editUrl)){
                    opt.modal.msgError("editUrl 未传!");
                    return;
                }
                //表格编辑回调
                if(typeof bootTableCallback === 'function'){
                    //获取当前编辑数据
                    var rows;
                    if(opt.common.isEmpty(id)){
                        //获取当前编辑数据
                        rows = $.table.selectAllColumnRows(tableId);
                        rows = rows.length ==0?[$.table.getRowByUniqueId(id)]:rows;
                    }else{
                        rows = [$.table.getRowByUniqueId(id)];
                    }
                    //当回调函数返回false 代码终止
                    /**
                     * 回调参数
                     * ID 表格对象ID
                     * event 方法名称
                     * data 当前数据
                     */
                    if(!bootTableCallback(opt.table.options.id,'editTab',rows)){
                        return;
                    }
                }
                if(opt.common.isEmpty(id) &&  opt.table.options.type ==  opt.variable.table_type.bootstrapTreeTable) {
                    var row = $("#" + opt.table.options.id).bootstrapTreeTable('getSelections')[0];
                    if (opt.common.isEmpty(row)) {
                        opt.modal.error("请至少选择一条记录");
                        return;
                    }
                    var url = opt.table.options.editUrl.replace("{id}", row[opt.table.options.uniqueId]);
                    opt.modal.openTab((opt.common.isEmpty(prefix)?$.i18n.text("修改{0}",opt.table.options.modalName):(prefix+opt.table.options.modalName)) , opt.operate.editUrl(id));
                } else {
                    opt.modal.openTab((opt.common.isEmpty(prefix)?$.i18n.text("修改{0}",opt.table.options.modalName):(prefix+opt.table.options.modalName)), opt.operate.editUrl(id));
                }
            },
            // 修改信息 全屏
            editFull: function(id) {
                opt.table.set();
                var url = "/404.html";
                if (opt.common.isNotEmpty(id)) {
                    url = opt.table.options.editUrl.replace("{id}", id);
                } else {
                    var row = opt.common.isEmpty(opt.table.options.uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(opt.table.options.uniqueId);
                    url = opt.table.options.editUrl.replace("{id}", row);
                }
                opt.modal.openFull($.i18n.text("修改{0}", opt.table.options.modalName), url);
            },
            // 修改访问地址
            editUrl: function(id) {
                opt.table.set();
                var url = "/404.html";
                if (opt.common.isNotEmpty(id)) {
                    url = opt.table.options.editUrl.replace("{id}", id);
                } else {
                    var id = opt.common.isEmpty(opt.table.options.uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(opt.table.options.uniqueId);
                    if (id.length == 0) {
                        opt.modal.error("请至少选择一条记录");
                        return;
                    }
                    url = opt.table.options.editUrl.replace("{id}", id);
                }
                return url;
            },
            /****
             *  保存信息并且刷新表格 若有传callback 回调函数 则自行处理 保存信息服务端返回的信息 后续工作
             * @param url 保存信息URL
             * @param data 保存数据
             * @param callback 返回信息 回调函数
             */
            save: function(url, data, callback) {
                var config = {
                    url: url,
                    type: "post",
                    dataType: "json",
                    data: data,
                    beforeSend: function () {
                        opt.modal.disable();
                        opt.modal.loading($.i18n.prop("数据加载中，请稍后..."));
                    },
                    success: function(result) {
                        if (typeof callback == "function") {
                            opt.modal.closeLoading();
                            callback(result);
                        }else{
                            opt.operate.successCallback(result);
                        }
                    }
                };
                $.ajax(config);
            },
            // 保存信息 弹出提示框
            saveModal: function(url, data, callback) {
                var config = {
                    url: url,
                    type: "post",
                    dataType: "json",
                    data: data,
                    beforeSend: function () {
                        opt.modal.loading($.i18n.prop("数据加载中，请稍后..."));
                    },
                    success: function(result) {
                        opt.modal.closeLoading();
                        if (typeof callback == "function") {
                            callback(result);
                            return;
                        }
                        if (result.code == opt.variable.web_status.SUCCESS) {
                            opt.modal.success($.i18n.prop("操作成功!"))
                        } else if (result.code == opt.variable.web_status.WARNING) {
                            opt.modal.warning(result.msg)
                        } else {
                            opt.modal.error(result.msg);
                        }
                    }
                };
                $.ajax(config);
            },
            // 保存选项卡信息
            saveTab: function(url, data, callback) {
                var config = {
                    url: url,
                    type: "POST",
                    dataType: "JSON",
                    data: data,
                    beforeSend: function () {
                        opt.modal.loading($.i18n.prop("数据加载中，请稍后..."));
                    },
                    success: function(result) {
                        if (typeof callback == "function") {
                            opt.modal.closeLoading();
                            callback(result);
                        }else{
                            // opt.modal.closeLoading();
                            //opt.modal.success($.i18n.prop("操作成功!"),function () {
                            opt.operate.successTabCallback(result);
                            //});
                        }
                    }
                };
                $.ajax(config);
            },
            // 保存结果弹出msg刷新table表格
            ajaxSuccess: function (result) {
                if (result.code == opt.variable.web_status.SUCCESS && opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                    opt.success($.i18n.prop("操作成功!"))
                    //重置清空页面记住我数据
                    if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                        opt.table.rememberSelecteds = {};
                        opt.table.rememberSelectedIds = {};
                    }
                    $.table.refresh();
                } else if (result.code == opt.variable.web_status.SUCCESS && opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                    opt.success($.i18n.prop("操作成功!"))
                    $.treeTable.refresh();
                }else if (result.code == opt.variable.web_status.SUCCESS){
                    opt.success($.i18n.prop("操作成功!"))
                }else if (result.code == opt.variable.web_status.WARNING) {
                    opt.modal.warning(result.msg)
                }  else {
                    opt.modal.error(result.msg);
                }
                opt.modal.closeLoading();
            },
            // 成功结果提示msg（父窗体全局更新）
            saveSuccess: function (result) {
                if (result.code == opt.variable.web_status.SUCCESS) {
                    opt.modal.msgReload("保存成功,正在刷新数据请稍后……", opt.variable.modal_status.SUCCESS);
                } else if (result.code == opt.variable.web_status.WARNING) {
                    opt.modal.alertWarning(result.msg)
                }  else {
                    opt.modal.alertError(result.msg);
                }
                opt.modal.closeLoading();
            },
            // 成功回调执行事件（父窗体静默更新）
            successCallback: function(result) {
                opt.modal.closeLoading();
                if (result.code == opt.variable.web_status.SUCCESS) {
                    var parent = window.parent;
                    if (parent.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                        //重置清空页面记住我数据
                        if (parent.opt.common.isNotEmpty(parent.opt.table.options.rememberSelected) && parent.opt.table.options.rememberSelected) {
                            parent.opt.table.rememberSelecteds = {};
                            parent.opt.table.rememberSelectedIds = {};
                        }
                        parent.$.table.refresh();
                        parent.opt.success("操作成功", parent.opt.modal.closeAll());
                        // parent.opt.modal.closeAll();
                    } else if (parent.opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                        parent.$.treeTable.refresh();
                        parent.opt.success("操作成功",parent.opt.modal.closeAll());
                    } else {
                        if (window.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                            window.$.table.refresh();
                            window.opt.success("操作成功", parent.opt.modal.closeAll());
                        }else{
                            opt.modal.msgReload("保存成功,正在刷新数据请稍后……", opt.variable.modal_status.SUCCESS);
                        }
                    }
                } else if (result.code == opt.variable.web_status.WARNING) {
                    opt.modal.warning(result.msg);
                }  else {
                    opt.modal.error(result.msg);
                }
                opt.modal.enable();
            },
            // 选项卡成功回调执行事件（父窗体静默更新）
            successTabCallback: function(result) {
                opt.modal.closeLoading();
                if(top.location!=self.location){
                    if (result.code == opt.variable.web_status.SUCCESS) {
                        var topWindow = $(window.parent.document);
                        var currentId = $('.layui-tab-title', topWindow).find('.layui-this').children('em').attr('panel-id');
                        if(opt.common.isNotEmpty(currentId)){
                            var $contentWindow = $('iframe[data-id="' + currentId + '"]', topWindow)[0].contentWindow;
                            $contentWindow.opt.success($.i18n.prop("操作成功!"));
                            if ($contentWindow.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                                //重置清空页面记住我数据
                                if ($contentWindow.opt.common.isNotEmpty($contentWindow.opt.table.options.rememberSelected)
                                    && $contentWindow.opt.table.options.rememberSelected) {
                                    $contentWindow.opt.table.rememberSelecteds = {};
                                    $contentWindow.opt.table.rememberSelectedIds = {};
                                }
                                $contentWindow.$.table.refresh();
                            } else if ($contentWindow.opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                                $contentWindow.$.treeTable.refresh();
                            }
                        }else{
                            parent.opt.success($.i18n.prop("操作成功!"));
                        }
                        opt.modal.closeTab();
                    } else if (result.code == opt.variable.web_status.WARNING) {
                        opt.modal.warning(result.msg);
                    } else {
                        opt.modal.error(result.msg);
                    }
                }else {
                    opt.modal.closeTab();
                }
            },
            successTab:function () {
                if(top.location!=self.location){
                    var topWindow = $(window.parent.document);
                    var currentId = $('.layui-tab-title', topWindow).find('.layui-this').children('em').attr('panel-id');
                    if(opt.common.isNotEmpty(currentId)){
                        var $contentWindow = $('iframe[data-id="' + currentId + '"]', topWindow)[0].contentWindow;
                        $contentWindow.opt.modal.msg($.i18n.prop("操作成功!"));
                        if ($contentWindow.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                            //重置清空页面记住我数据
                            if ($contentWindow.opt.common.isNotEmpty($contentWindow.opt.table.options.rememberSelected) && $contentWindow.opt.table.options.rememberSelected) {
                                $contentWindow.opt.table.rememberSelecteds = {};
                                $contentWindow.opt.table.rememberSelectedIds = {};
                            }
                            $contentWindow.$.table.refresh();
                        } else if ($contentWindow.opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                            $contentWindow.$.treeTable.refresh();
                        }
                    }
                    opt.modal.closeTab();
                }else{
                    opt.modal.closeTab();
                }

            }
        },
        // 校验封装处理
        validate: {
            // 判断返回标识是否唯一 true 存在 其他返回则显示错误提示
            unique: function (o) {
                var obj = $.parseJSON(o);
                if (obj.code === opt.variable.web_status.SUCCESS) {
                    return true;
                }
                return "<i class='fa fa-times-circle'></i>  " + obj.msg;
            },
            // 表单验证
            /**
             * 表单验证
             * @param formId 参数不传则获取页面第一个from表单值
             * @returns {jQuery}
             */
            form: function (formId) {
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
                return $("#" + currentId).validate().form();
            },
            // 重置表单验证（清除提示信息）
            reset: function (formId) {
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
                return $("#" + currentId).validate().resetForm();
            },
            fromData: function (formId) {
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
                return $("#" + currentId).serialize();
            }
        },
        // 表单封装处理
        form: {
            // 表单重置
            reset: function(tableId,formId,notName) {
                var notNames = opt.common.isEmpty(notName)?[]:notName.split(",");
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
                //$("#" + currentId)[0].reset();
                var inpt = $("#" + currentId).find('input, select');
                $.each(inpt, function() {
                    if(this.tagName == "SELECT"){
                        if(opt.common.isEmpty(notName)){
                            if(opt.table.options.__fromData__ &&
                                opt.table.options.__fromData__[this.name] != undefined){
                                $(this).val(opt.table.options.__fromData__[this.name]).trigger("change");
                            }else{
                                $(this).val(null).trigger("change");
                            }
                        }else{
                            var temp = false;
                            for(var i=0; i<notNames.length;i++){
                                if($(this).attr("name") == notNames[i]){
                                    temp = true;
                                }
                            }
                            if(!temp){
                                $(this).val(null).trigger("change");
                            }
                        }
                    }
                    else if(this.tagName == "INPUT"){
                        //表单域输入框
                        if(!(this.type == "hidden" && opt.common.isNotEmpty($(this).data("refresh"))
                            && !$(this).data("refresh"))){
                            if(opt.table.options.__fromData__ &&
                                opt.table.options.__fromData__[this.name] != undefined){
                                this.value = opt.table.options.__fromData__[this.name];
                            }else{
                                this.value = "";
                            }
                        }
                    }
                });
                //重置表单select2
                $("#" + currentId +" select").each(function(i) {
                    if(!opt.common.isEmpty($(this).attr("data-select2-id"))){
                        if(opt.common.isEmpty(notName)){
                            if(opt.table.options.__fromData__ &&
                                opt.table.options.__fromData__[this.name] != undefined){
                                $(this).val(opt.table.options.__fromData__[this.name]).trigger("change");
                            }else{
                                $(this).val(null).trigger("change");
                            }
                        }else{
                            var temp = false;
                            for(var i=0; i<notNames.length;i++){
                                if($(this).attr("name") == notNames[i]){
                                    temp = true;
                                }
                            }
                            if(!temp){
                                $(this).val(null).trigger("change");
                            }
                        }
                    }
                });
                if (opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                    //重置清空页面记住我数据
                    if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                        opt.table.rememberSelecteds = {};
                        opt.table.rememberSelectedIds = {};
                    }
                    if(opt.common.isEmpty(tableId)){
                        $("#" + opt.table.options.id).bootstrapTable('refresh');
                    } else{
                        $("#" + tableId).bootstrapTable('refresh');
                    }
                } else if (opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                    if(opt.common.isEmpty(tableId)){
                        $("#" + opt.table.options.id).bootstrapTreeTable('refresh', {'__refre':true});
                    } else{
                        $("#" + tableId).bootstrapTreeTable('refresh', {'__refre':true});
                    }
                }
            },
            submit: function(){
                if(typeof submitHandler == "function"){
                    opt.modal.disable();
                    submitHandler();
                    opt.modal.enable();
                }else{
                    opt.modal.error($.i18n.prop('页面必须存在[submitHandler]方法'));
                }
            },
            // 获取选中复选框项
            selectCheckeds: function(name) {
                var checkeds = "";
                $('input:checkbox[name="' + name + '"]:checked').each(function(i) {
                    if (0 == i) {
                        checkeds = $(this).val();
                    } else {
                        checkeds += ("," + $(this).val());
                    }
                });
                return checkeds;
            },
            // 获取选中下拉框项
            selectSelects: function(name) {
                var selects = "";
                $('#' + name + ' option:selected').each(function (i) {
                    if (0 == i) {
                        selects = $(this).val();
                    } else {
                        selects += ("," + $(this).val());
                    }
                });
                return selects.split(",");
            },
            getInputValue:function(name){
                return $('input[name="'+name+'"]').val();
            },
            dataUp:function (options) {
                var defaults = {
                    id :"form-user-saveUpdate",
                    del:[],
                    conversion:[]
                }
                var options = $.extend(defaults, options);
                var data =$("#" + options.id).serializeArray();
                for(var i=0; i<data.length; i++){
                    if(opt.common.isEmpty(options.del)){
                        break;
                    }
                    for (var k=0; k<options.del.length; k++) {
                        if(data[i].name == options.del[k]){
                            data.splice(i,1);
                            if(i==0){
                                i=0;
                            }else{
                                i--;
                            }
                        }
                    }
                }
                for(var i=0; i<data.length; i++){
                    if(opt.common.isEmpty(options.conversion)){
                        break;
                    }
                    for(var k=0; k<options.conversion.length; k++){
                        if(data[i].name == options.conversion[k].name){
                            if(options.conversion[k].value == 'arr'){
                                data[i].value = data[i].value.split(",");
                            }
                            if(options.conversion[k].value == 'num'){
                                data[i].value = Number(data[i].value);
                            }
                        }
                    }
                }
                return data;
            }
        },
    };

    /**
     * 判断给定的对象是否是字符串
     * @method opt.common.isString
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是字符串
     */

    /**
     * 判断给定的对象是否是数组
     * @method opt.common.isArray
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是数组
     */

    /**
     * 判断给定的对象是否是一个Function
     * @method opt.common.isFunction
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是Function
     */

    /**
     * 判断给定的对象是否是Number
     * @method opt.common.isNumber
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是Number
     */

    /**
     * 判断给定的对象是否是一个正则表达式
     * @method opt.common.isRegExp
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是正则表达式
     */

    /**
     * 判断给定的对象是否是一个普通对象
     * @method opt.common.isObject
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是普通对象
     */
    opt.common.each(['String', 'Function', 'Array', 'Number', 'RegExp', 'Object', 'Date'], function (v) {
        opt.common['is' + v] = function (obj) {
            return Object.prototype.toString.apply(obj) == '[object ' + v + ']';
        }
    });

    $(function () {

        // 全局添加水印
        if(!(self.frameElement && self.frameElement.tagName == "IFRAME")
            && opt.variable.isWatermark) {
            opt.common.watermark({"watermark_txt":"J2eeFAST 开源版本 v"+window.__VERSION__});
        }

        //全局设置tooltip
        $('[data-toggle="tooltip"]').each(function () {
            $(this).tooltip();
        });

        //分析所有新标签 添加跳动样式使其跳动
        setTimeout(function(){
            $('[data-index="_new"]').each(function () {
                $(this).addClass('jump5');
            });
        },2000);

        // select2复选框事件绑定
        if ($.fn.select2 !== undefined) {
            $.fn.select2.defaults.set( "theme", "bootstrap" );
            $("select.form-control:not(.noselect2)").each(function () {
                //     .on("change", function () {
                //     $(this).valid();
                // })
                if(typeof($(this).attr("multiple"))=="undefined"){
                    $(this).select2({allowClear: false, placeholder: $.i18n.text("请点击选择")});
                }else{
                    $(this).select2({allowClear: true, placeholder: ""});
                }
            })
        }

        // iCheck单选框及复选框事件绑定
        if ($.fn.iCheck !== undefined) {
            $(".check-box:not(.noicheck),.radio-box:not(.noicheck)").each(function() {
                $(this).iCheck({
                    checkboxClass: (typeof($(this).data("style")) == "undefined")?'icheckbox-blue':("icheckbox_" +($(this).data("style") || "square-blue")),
                    radioClass:(typeof($(this).data("style")) == "undefined")? 'iradio-blue':("iradio_" +($(this).data("style") || "square-blue"))
                })
            })
        }

        $('[data-toggle="popover"]').each(function () {
            $(this).popover();
        });


        // laydate 时间控件绑定
        if ($(".select-time").length > 0) {
            var startDate = laydate.render({
                elem: '#startTime',
                max: $('#endTime').val(),
                theme: 'default',
                type: $('#startTime').attr("data-type") || 'date',
                trigger: 'click',
                done: function(value, date) {
                    // 结束时间大于开始时间
                    if (value !== '') {
                        endDate.config.min.year = date.year;
                        endDate.config.min.month = date.month - 1;
                        endDate.config.min.date = date.date;
                    } else {
                        endDate.config.min.year = '';
                        endDate.config.min.month = '';
                        endDate.config.min.date = '';
                    }
                    $('#endTime').trigger('click');
                }
            });
            var endDate = laydate.render({
                elem: '#endTime',
                min: $('#startTime').val(),
                theme: 'default',
                type: $('#endTime').attr("data-type") || 'date',
                trigger: 'click',
                done: function(value, date) {
                    // 开始时间小于结束时间
                    if (value !== '') {
                        startDate.config.max.year = date.year;
                        startDate.config.max.month = date.month - 1;
                        startDate.config.max.date = date.date;
                    } else {
                        startDate.config.max.year = '2099';
                        startDate.config.max.month = '12';
                        startDate.config.max.date = '31';
                    }
                }
            });
        }
        // laydate time-input 时间控件绑定
        if ($(".time-input").length > 0) {
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
                    trigger: 'click',
                    type: type,
                    format: format,
                    btns: newBtnArr,
                    done: function (value, data) {
                        time.val(value);
                        if (typeof window[callback] != 'undefined'
                            && window[callback] instanceof Function) {
                            window[callback](value, data);
                        }else{
                            try{
                                if(opt.common.isString(callback)){
                                    console.log(callback);
                                    eval(callback) || new Function(callback);
                                }
                            }catch (e) {
                                console.error(e);
                            }
                        }
                    }
                });
            });
        }

        // tree表格树 展开/折叠
        var expandFlag;
        $("#expandAllBtn").click(function() {
            var dataExpand = opt.common.isEmpty(opt.table.options.expandAll) ? true : opt.table.options.expandAll;
            expandFlag = opt.common.isEmpty(expandFlag) ? dataExpand : expandFlag;
            if (!expandFlag) {
                $.bttTable.bootstrapTreeTable('expandAll');
            } else {
                $.bttTable.bootstrapTreeTable('collapseAll');
            }
            expandFlag = expandFlag ? false: true;
        });

        // tree 关键字搜索绑定
        if ($("#keyword").length > 0) {
            $("#keyword").bind("focus", function focusKey(e) {
                if ($("#keyword").hasClass("empty")) {
                    $("#keyword").removeClass("empty");
                }
            }).bind("blur", function blurKey(e) {
                if ($("#keyword").val() === "") {
                    $("#keyword").addClass("empty");
                }
                $.tree.searchNode(e);
            }).bind("input propertychange", $.tree.searchNode);
        };

        // 按下ESC按钮关闭弹层
        $('body', document).on('keyup',this, function(e) {
            if (e.which === 27) {
                opt.modal.closeAll();
            }
        });

        /* 回到顶部绑定 */
        if ($.fn.toTop !== undefined) {
            //判断是否
            if($.fn.layout !== undefined && $('.ui-layout-center')){
                var _opt = {
                    win:$('.ui-layout-center'),
                    doc:$('.ui-layout-center')
                };
                $('#scroll-up').toTop(_opt);
            }else{
                $('#scroll-up').toTop();
            }
        }
        /* 页面菜单更多事件*/
        $('a[data-toggle="formMore"]').each(function(){
            $(this).off("click").on("click", function () {
                var html = [];
                var formMore = $(this).parents('form').find('.select-list-more');
                var show = formMore.css('display');
                if(show == 'block'){
                    formMore.toggle('normal');
                    html.push('<i class="fa fa-angle-double-down"></i>&nbsp;',$.i18n.prop("更多"));
                }else{
                    formMore.fadeIn(500).css('display','block');
                    html.push('<i class="fa fa-angle-double-up"></i>&nbsp;',$.i18n.prop("隐藏"));
                }
                $(this).empty().append(html.join(''));
            })
        });
    });

    window.opt = opt;

})(window.jQuery, window);
// 回到顶部
(function( $ ){
    'use strict';
    //TOP方法
    $.fn.toTop = function(opt){
        //variables
        var elem = this;
        //修复插件冲突导致失效问题
        var win = (opt && opt.hasOwnProperty('win')) ? opt.win : $(window);
        var doc = (opt && opt.hasOwnProperty('doc')) ? opt.doc : $('html, body');
        //Extended Options
        var options = $.extend({
            autohide: true,
            offset: 50,
            speed: 500,
            position: true,
            right: 15,
            bottom: 5
        }, opt);

        elem.css({
            'cursor': 'pointer'
        });

        if(options.autohide){
            elem.css('display', 'none');
        }

        if(options.position){
            elem.css({
                'position': 'fixed',
                'right': options.right,
                'bottom': options.bottom,
            });
        }
        elem.click(function(){
            doc.animate({scrollTop: 0}, options.speed);
        });

        win.scroll(function(){
            var scrolling = win.scrollTop();
            if(options.autohide){
                if(scrolling > options.offset){
                    elem.fadeIn(options.speed);
                }
                else elem.fadeOut(options.speed);
            }
        });

    };

}( jQuery ));

//设置主页皮肤
+function () {

    //var _lang = opt.getCookie("_lang");

    //初始化i18n插件
    $.i18n.properties({
        path: baseURL + 'i18n/',//这里表示访问路径
        name: 'i18n',//文件名开头
        language: __LANG__,//文件名语言 例如en_US
        tag: __I18NTAG__,
        cache: true,
        mode: 'map'//默认值
    });


    var dict = opt.storage.get('dict',0);
    // console.log(dict);
    // console.log(dict == undefined);
    //清空字典缓存
    if( dict == undefined || dict != __DICTTAG__){
        for (var i=0; i< window.localStorage.length; i++){
            var key = window.localStorage.key(i);
            if(opt.common.startWith(key,"_dict_")){
                window.localStorage.removeItem(key);
            }
        }
        window.localStorage.setItem('dict', __DICTTAG__);
    }

    var tmp = opt.storage.get('skin');
    if (tmp && ($.inArray(tmp, opt.variable.skins)>=0)){
        opt.changeSkin(tmp);
    }

    //设置所有弹出框样式
    if(window.layer !== undefined){
        layer.config({
            extend: 'moon/style.css',
            skin: 'layer-ext-moon'
        });
    };

    /*$(document).ajaxError(function(e,xhr,opt){
        if(xhr.statusText == "parsererror"){//被踢下线,或者被挤下线
            window.opt.wclearInterval();
            $.getJSON("sys/user/info/login/msg/"+__USERNAME__+"?V=" + $.now(), function (r) {
                if(r.code == "00000"){
                    var m = r.msg.split("#");
                    window.opt.outLogin(m[0]+"</br>" + m[1],$.i18n.prop('sys.login.out.info'));
                    return;
                }else{
                    window.opt.outLogin("",r.msg);
                    return;
                }
            });
        }
        if(xhr.statusText == "error"){//与服务器断开连接
            window.opt.wclearInterval();
            window.opt.outLogin("",$.i18n.prop('sys.login.out.error'));
            return;
        }
    });*/
    // $.ajaxSetup({
    //     complete: function(XMLHttpRequest, textStatus) {
    //         if (textStatus == 'timeout') {
    //             //window.opt.wclearInterval();
    //             opt.outLogin("",$.i18n.prop('sys.login.out.error'));
    //             return;
    //         } else if (textStatus == "parsererror" || textStatus == "error") {
    //             opt.outLogin("",$.i18n.prop('sys.login.out.error'));
    //             return;
    //         }
    //     }
    // });
}();
/**
 * 页面模板引擎
 * 声明: 引用layui.laytpl 作者:贤心
 * j2eefast.com zhouzhou 二次封装
 */
!function () {
    var config = {
        open: '{{',
        close: '}}'
    };

    var tool = {
        exp: function(str){
            return new RegExp(str, 'g');
        },
        //匹配满足规则内容
        query: function(type, _, __){
            var types = [
                '#([\\s\\S])+?',   //js语句
                '([^{#}])*?' //普通字段
            ][type || 0];
            return exp((_||'') + config.open + types + config.close + (__||''));
        },
        escape: function(html){
            return String(html||'').replace(/&(?!#?[a-zA-Z0-9]+;)/g, '&amp;')
                .replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&#39;').replace(/"/g, '&quot;');
        },
        error: function(e, tplog){
            var error = 'Laytpl Error：';
            typeof console === 'object' && console.error(error + e + '\n'+ (tplog || ''));
            return error + e;
        }
    };

    var exp = tool.exp, Tpl = function(tpl){
        this.tpl = tpl;
    };

    Tpl.pt = Tpl.prototype;

    window.errors = 0;

    //编译模版
    Tpl.pt.parse = function(tpl, data){
        var that = this, tplog = tpl;
        var jss = exp('^'+config.open+'#', ''), jsse = exp(config.close+'$', '');

        tpl = tpl.replace(/\s+|\r|\t|\n/g, ' ')
            .replace(exp(config.open+'#'), config.open+'# ')
            .replace(exp(config.close+'}'), '} '+config.close).replace(/\\/g, '\\\\')

            //不匹配指定区域的内容
            .replace(exp(config.open + '!(.+?)!' + config.close), function(str){
                str = str.replace(exp('^'+ config.open + '!'), '')
                    .replace(exp('!'+ config.close), '')
                    .replace(exp(config.open + '|' + config.close), function(tag){
                        return tag.replace(/(.)/g, '\\$1')
                    });
                return str
            })

            //匹配JS规则内容
            .replace(/(?="|')/g, '\\').replace(tool.query(), function(str){
                str = str.replace(jss, '').replace(jsse, '');
                return '";' + str.replace(/\\/g, '') + ';view+="';
            })

            //匹配普通字段
            .replace(tool.query(1), function(str){
                var start = '"+(';
                if(str.replace(/\s/g, '') === config.open+config.close){
                    return '';
                }
                str = str.replace(exp(config.open+'|'+config.close), '');
                if(/^=/.test(str)){
                    str = str.replace(/^=/, '');
                    start = '"+_escape_(';
                }
                return start + str.replace(/\\/g, '') + ')+"';
            });

        tpl = '"use strict";var view = "' + tpl + '";return view;';

        try{
            that.cache = tpl = new Function('d, _escape_', tpl);
            return tpl(data, tool.escape);
        } catch(e){
            delete that.cache;
            return tool.error(e, tplog);
        }
    };

    Tpl.pt.render = function(data, callback){
        var that = this, tpl;
        if(!data) return tool.error('no data');
        tpl = that.cache ? that.cache(data, tool.escape) : that.parse(that.tpl, data);
        if(!callback) return tpl;
        callback(tpl);
    };

    var laytpl = function(tpl){
        if(typeof tpl !== 'string') return tool.error('Template not found');
        return new Tpl(tpl);
    };

    laytpl.config = function(options){
        options = options || {};
        for(var i in options){
            config[i] = options[i];
        }
    };

    laytpl.v = '1.2.0';

    "function" == typeof define ? define(function () {
        return laytpl
    }) : "undefined" != typeof exports ? module.exports = laytpl : window.laytpl = laytpl
}();


/* BoxWidget()
 * ======
 * Adds box widget functions to boxes.
 *
 * @Usage: $('.my-box').boxWidget(options)
 *         This plugin auto activates on any element using the `.box` class
 *         Pass any option as data-option="value"
 */
+function ($) {
    'use strict';

    var DataKey = 'lte.boxwidget';

    var Default = {
        animationSpeed : 500,
        collapseTrigger: '[data-widget="collapse"]',
        removeTrigger  : '[data-widget="remove"]',
        collapseIcon   : 'fa-minus',
        expandIcon     : 'fa-plus',
        removeIcon     : 'fa-times'
    };

    var Selector = {
        data     : '.box',
        collapsed: '.collapsed-box',
        header   : '.box-header',
        body     : '.box-body',
        footer   : '.box-footer',
        tools    : '.box-tools'
    };

    var ClassName = {
        collapsed: 'collapsed-box'
    };

    var Event = {
        collapsing: 'collapsing.boxwidget',
        collapsed: 'collapsed.boxwidget',
        expanding: 'expanding.boxwidget',
        expanded: 'expanded.boxwidget',
        removing: 'removing.boxwidget',
        removed: 'removed.boxwidget'
    };

    // BoxWidget Class Definition
    // =====================
    var BoxWidget = function (element, options) {
        this.element = element;
        this.options = options;

        this._setUpListeners();
    };

    BoxWidget.prototype.toggle = function () {
        var isOpen = !$(this.element).is(Selector.collapsed);

        if (isOpen) {
            //折叠
            this.collapse();
        } else {
            //展开
            this.expand();
        }
    };

    BoxWidget.prototype.expand = function () {
        var expandedEvent = $.Event(Event.expanded);
        var expandingEvent = $.Event(Event.expanding);
        var collapseIcon  = this.options.collapseIcon;
        var expandIcon    = this.options.expandIcon;

        $(this.element).removeClass(ClassName.collapsed);

        $(this.element)
            .children(Selector.header + ', ' + Selector.body + ', ' + Selector.footer)
            .children(Selector.tools)
            .find('.' + expandIcon)
            .removeClass(expandIcon)
            .addClass(collapseIcon);

        $(this.element).children(Selector.body + ', ' + Selector.footer)
            .slideDown(this.options.animationSpeed, function () {
                $(this.element).trigger(expandedEvent);
            }.bind(this))
            .trigger(expandingEvent);
    };

    BoxWidget.prototype.collapse = function () {
        var collapsedEvent = $.Event(Event.collapsed);
        var expandingEvent = $.Event(Event.collapsing);
        var collapseIcon   = this.options.collapseIcon;
        var expandIcon     = this.options.expandIcon;

        $(this.element)
            .children(Selector.header + ', ' + Selector.body + ', ' + Selector.footer)
            .children(Selector.tools)
            .find('.' + collapseIcon)
            .removeClass(collapseIcon)
            .addClass(expandIcon);

        $(this.element).children(Selector.body + ', ' + Selector.footer)
            .slideUp(this.options.animationSpeed, function () {
                $(this.element).addClass(ClassName.collapsed);
                $(this.element).trigger(collapsedEvent);
            }.bind(this))
            .trigger(expandingEvent);
    };

    BoxWidget.prototype.remove = function () {
        var removedEvent = $.Event(Event.removed);
        var removingEvent = $.Event(Event.removing);

        $(this.element).slideUp(this.options.animationSpeed, function () {
            $(this.element).trigger(removedEvent);
            $(this.element).remove();
        }.bind(this))
            .trigger(removingEvent);
    };

    // Private

    BoxWidget.prototype._setUpListeners = function () {
        var that = this;

        $(this.element).on('click', this.options.collapseTrigger, function (event) {
            if (event) event.preventDefault();
            that.toggle($(this));
            return false;
        });

        $(this.element).on('click', this.options.removeTrigger, function (event) {
            if (event) event.preventDefault();
            that.remove($(this));
            return false;
        });
    };

    // Plugin Definition
    // =================
    function Plugin(option) {
        return this.each(function () {
            var $this = $(this);
            var data  = $this.data(DataKey);

            if (!data) {
                var options = $.extend({}, Default, $this.data(), typeof option == 'object' && option);
                $this.data(DataKey, (data = new BoxWidget($this, options)));
            }

            if (typeof option == 'string') {
                if (typeof data[option] == 'undefined') {
                    throw new Error('No method named ' + option);
                }
                data[option]();
            }
        });
    }

    var old = $.fn.boxWidget;

    $.fn.boxWidget             = Plugin;
    $.fn.boxWidget.Constructor = BoxWidget;

    // No Conflict Mode
    // ================
    $.fn.boxWidget.noConflict = function () {
        $.fn.boxWidget = old;
        return this;
    };

    // BoxWidget Data API
    // ==================
    $(window).on('load', function () {
        $(Selector.data).each(function () {
            Plugin.call($(this));
        });
    });
}(jQuery);

/**
 * 表单序列号成对象 使用--> $('').serializeJson()
 * 表单序列号成 字符串 使用-->$('').serialize()
 */
(function(window, $) {
    $.fn.serializeJson = function() {
        var serializeObj = {};
        var array = this.serializeArray();
        //var str = this.serialize();
        $(array).each(
            function() {
                if (serializeObj[this.name]) {
                    if ($.isArray(serializeObj[this.name])) {
                        serializeObj[this.name].push(this.value);
                    } else {
                        serializeObj[this.name] = [
                            serializeObj[this.name], this.value ];
                    }
                } else {
                    serializeObj[this.name] = this.value;
                }
            });
        return serializeObj;
    };

})(window, jQuery);


/*重写Jquery中的ajax 封装壳*/
(function (window,$) {
    //首先备份下jquery的ajax方法
    var _ajax = $.ajax;
    //重写jquery的ajax方法
    $.ajax = function ($opt) {

        //备份opt中error和success方法
        var fn = {
            beforeSend: function (XHR) { },
            error: function (XMLHttpRequest, textStatus, errorThrown) { },
            success: function (data, textStatus) { },
            complete: function (XHR, TS) { }
        }

        if ($opt.beforeSend) {
            fn.beforeSend = $opt.beforeSend;
        }

        if ($opt.error) {
            fn.error = $opt.error;
        }else{
            fn.error = function(xhr){
                try{
                    opt.error(JSON.parse(xhr.responseText).msg || xhr.responseText );
                }catch (e) {
                    console.error(xhr.responseText);
                }
                opt.modal.closeLoading();
                return;
            }
        }

        if ($opt.success) {
            fn.success = $opt.success;
        }
        if ($opt.complete) {
            fn.complete = $opt.complete;
        }

        //防CSRF攻击
        if(__ISCSRF__ &&
                opt.common.equalsIgnoreCase($opt.type,'POST')){

            if(!($opt.headers && $opt.headers["X-CSRF-Token"])){
                $opt = opt.common.extend($opt,{headers: {
                  "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content")
               }});
            }
        }

        //扩展增强处理
        var _opt = $.extend($opt, {
            //全局允许跨域
            xhrFields: {
                withCredentials: true
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                //错误方法增强处理
                fn.error(XMLHttpRequest, textStatus, errorThrown);
            },
            success: function (data, textStatus) {
                //成功回调方法增强处理
                fn.success(data, textStatus);
            },
            beforeSend: function (XHR) {
                //提交前回调方法
                fn.beforeSend(XHR);
            },
            complete: function (XHR, TS) {
                //请求完成后回调函数 (请求成功或失败之后均调用)。
                fn.complete(XHR, TS);
            }
        });

        if ($opt.xhrFields) {
            _opt.xhrFields = $opt.xhrFields;
        }

        //调用native ajax 方法
        return _ajax(_opt);
    };
})(window,jQuery);
/**
 * 日期范围工具类
 */
var dateRangeUtil = (function () {
    /***
     * 获得当前时间
     */
    this.getCurrentDate = function () {
        return new Date();
    };

    //今天
    this.getToDay = function(){
        //起止日期数组
        var startStop = new Array();
        var date = opt.common.formatDat(Date.parse(new Date()));
        startStop.push(date);
        startStop.push(date);
        return startStop;
    };

    /***
     * 获得本周起止时间
     */
    this.getCurrentWeek = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //返回date是一周中的某一天
        var week = currentDate.getDay();
        //返回date是一个月中的某一天
        var month = currentDate.getDate();

        //一天的毫秒数
        var millisecond = 1000 * 60 * 60 * 24;
        //减去的天数
        var minusDay = week != 0 ? week - 1 : 6;
        //alert(minusDay);
        //本周 周一
        var monday = new Date(currentDate.getTime() - (minusDay * millisecond));
        //本周 周日
        var sunday = new Date(monday.getTime() + (6 * millisecond));
        //添加本周时间
        startStop.push(monday); //本周起始时间
        //添加本周最后一天时间
        startStop.push(sunday); //本周终止时间
        //返回
        return startStop;
    };

    /***
     * 获得本月的起止时间
     */
    this.getCurrentMonth = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前月份0-11
        var currentMonth = currentDate.getMonth();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        //求出本月第一天
        var firstDay = new Date(currentYear, currentMonth, 1);


        //当为12月的时候年份需要加1
        //月份需要更新为0 也就是下一年的第一个月
        if (currentMonth == 11) {
            currentYear++;
            currentMonth = 0; //就为
        } else {
            //否则只是月份增加,以便求的下一月的第一天
            currentMonth++;
        }


        //一天的毫秒数
        var millisecond = 1000 * 60 * 60 * 24;
        //下月的第一天
        var nextMonthDayOne = new Date(currentYear, currentMonth, 1);
        //求出上月的最后一天
        var lastDay = new Date(nextMonthDayOne.getTime() - millisecond);

        //添加至数组中返回
        startStop.push(firstDay);
        startStop.push(lastDay);
        //返回
        return startStop;
    };

    /**
     * 得到本季度开始的月份
     * @param month 需要计算的月份
     ***/
    this.getQuarterSeasonStartMonth = function (month) {
        var quarterMonthStart = 0;
        var spring = 0; //春
        var summer = 3; //夏
        var fall = 6;   //秋
        var winter = 9; //冬
        //月份从0-11
        if (month < 3) {
            return spring;
        }

        if (month < 6) {
            return summer;
        }

        if (month < 9) {
            return fall;
        }

        return winter;
    };

    /**
     * 获得该月的天数
     * @param year年份
     * @param month月份
     * */
    this.getMonthDays = function (year, month) {
        //本月第一天 1-31
        var relativeDate = new Date(year, month, 1);
        //获得当前月份0-11
        var relativeMonth = relativeDate.getMonth();
        //获得当前年份4位年
        var relativeYear = relativeDate.getFullYear();

        //当为12月的时候年份需要加1
        //月份需要更新为0 也就是下一年的第一个月
        if (relativeMonth == 11) {
            relativeYear++;
            relativeMonth = 0;
        } else {
            //否则只是月份增加,以便求的下一月的第一天
            relativeMonth++;
        }
        //一天的毫秒数
        var millisecond = 1000 * 60 * 60 * 24;
        //下月的第一天
        var nextMonthDayOne = new Date(relativeYear, relativeMonth, 1);
        //返回得到上月的最后一天,也就是本月总天数
        return new Date(nextMonthDayOne.getTime() - millisecond).getDate();
    };

    /**
     * 获得本季度的起止日期
     */
    this.getCurrentSeason = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前月份0-11
        var currentMonth = currentDate.getMonth();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        //获得本季度开始月份
        var quarterSeasonStartMonth = this.getQuarterSeasonStartMonth(currentMonth);
        //获得本季度结束月份
        var quarterSeasonEndMonth = quarterSeasonStartMonth + 2;

        //获得本季度开始的日期
        var quarterSeasonStartDate = new Date(currentYear, quarterSeasonStartMonth, 1);
        //获得本季度结束的日期
        var quarterSeasonEndDate = new Date(currentYear, quarterSeasonEndMonth, this.getMonthDays(currentYear, quarterSeasonEndMonth));
        //加入数组返回
        startStop.push(quarterSeasonStartDate);
        startStop.push(quarterSeasonEndDate);
        //返回
        return startStop;
    };

    /***
     * 得到本年的起止日期
     *
     */
    this.getCurrentYear = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();

        //本年第一天
        var currentYearFirstDate = new Date(currentYear, 0, 1);
        //本年最后一天
        var currentYearLastDate = new Date(currentYear, 11, 31);
        //添加至数组
        startStop.push(currentYearFirstDate);
        startStop.push(currentYearLastDate);
        //返回
        return startStop;
    };

    /**
     * 返回上一个月的第一天Date类型
     * @param year 年
     * @param month 月
     **/
    this.getPriorMonthFirstDay = function (year, month) {
        //年份为0代表,是本年的第一月,所以不能减
        if (month == 0) {
            month = 11; //月份为上年的最后月份
            year--; //年份减1
            return new Date(year, month, 1);
        }
        //否则,只减去月份
        month--;
        return new Date(year, month, 1); ;
    };

    /**
     * 获得上一月的起止日期
     * ***/
    this.getPreviousMonth = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前月份0-11
        var currentMonth = currentDate.getMonth();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        //获得上一个月的第一天
        var priorMonthFirstDay = this.getPriorMonthFirstDay(currentYear, currentMonth);
        //获得上一月的最后一天
        var priorMonthLastDay = new Date(priorMonthFirstDay.getFullYear(), priorMonthFirstDay.getMonth(), this.getMonthDays(priorMonthFirstDay.getFullYear(), priorMonthFirstDay.getMonth()));
        //添加至数组
        startStop.push(priorMonthFirstDay);
        startStop.push(priorMonthLastDay);
        //返回
        return startStop;
    };


    /**
     * 获得上一周的起止日期
     * **/
    this.getPreviousWeek = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //返回date是一周中的某一天
        var week = currentDate.getDay();
        //返回date是一个月中的某一天
        var month = currentDate.getDate();
        //一天的毫秒数
        var millisecond = 1000 * 60 * 60 * 24;
        //减去的天数
        var minusDay = week != 0 ? week - 1 : 6;
        //获得当前周的第一天
        var currentWeekDayOne = new Date(currentDate.getTime() - (millisecond * minusDay));
        //上周最后一天即本周开始的前一天
        var priorWeekLastDay = new Date(currentWeekDayOne.getTime() - millisecond);
        //上周的第一天
        var priorWeekFirstDay = new Date(priorWeekLastDay.getTime() - (millisecond * 6));

        //添加至数组
        startStop.push(priorWeekFirstDay);
        startStop.push(priorWeekLastDay);

        return startStop;
    };

    /**
     * 得到上季度的起始日期
     * year 这个年应该是运算后得到的当前本季度的年份
     * month 这个应该是运算后得到的当前季度的开始月份
     * */
    this.getPriorSeasonFirstDay = function (year, month) {
        var quarterMonthStart = 0;
        var spring = 0; //春
        var summer = 3; //夏
        var fall = 6;   //秋
        var winter = 9; //冬
        //月份从0-11
        switch (month) {//季度的其实月份
            case spring:
                //如果是第一季度则应该到去年的冬季
                year--;
                month = winter;
                break;
            case summer:
                month = spring;
                break;
            case fall:
                month = summer;
                break;
            case winter:
                month = fall;
                break;

        };

        return new Date(year, month, 1);
    };

    /**
     * 得到上季度的起止日期
     * **/
    this.getPreviousSeason = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前月份0-11
        var currentMonth = currentDate.getMonth();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        //上季度的第一天
        var priorSeasonFirstDay = this.getPriorSeasonFirstDay(currentYear, currentMonth);
        //上季度的最后一天
        var priorSeasonLastDay = new Date(priorSeasonFirstDay.getFullYear(), priorSeasonFirstDay.getMonth() + 2, this.getMonthDays(priorSeasonFirstDay.getFullYear(), priorSeasonFirstDay.getMonth() + 2));
        //添加至数组
        startStop.push(priorSeasonFirstDay);
        startStop.push(priorSeasonLastDay);
        return startStop;
    };

    /**
     * 得到去年的起止日期
     * **/
    this.getPreviousYear = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        currentYear--;
        var priorYearFirstDay = new Date(currentYear, 0, 1);
        var priorYearLastDay = new Date(currentYear, 11, 1);
        //添加至数组
        startStop.push(priorYearFirstDay);
        startStop.push(priorYearLastDay);
        return startStop;
    };

    this.getBetweenAllDay = function (starDay, endDay) {

        var arr = [];
        var dates = [];

        // 设置两个日期UTC时间
        var db = new Date(starDay);
        var de = new Date(endDay);

        // 获取两个日期GTM时间
        var s = db.getTime() - 24 * 60 * 60 * 1000;
        var d = de.getTime() - 24 * 60 * 60 * 1000;

        // 获取到两个日期之间的每一天的毫秒数
        for (var i = s; i <= d;) {
            i = i + 24 * 60 * 60 * 1000;
            arr.push(parseInt(i))
        }

        // 获取每一天的时间  YY-MM-DD
        for( var j in arr ){
            var time = new Date(arr[j]);
            var year = time.getFullYear(time);
            var mouth = (time.getMonth() + 1)>=10?(time.getMonth() + 1):('0'+(time.getMonth() + 1));
            var day = time.getDate()>=10?time.getDate():('0'+time.getDate());
            var YYMMDD = year + '-' + mouth + '-' + day;
            dates.push(YYMMDD)
        }

        return dates
    }

    return this;
})();