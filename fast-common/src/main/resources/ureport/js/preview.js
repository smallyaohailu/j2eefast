$(function () {

    // 表单初始化加载
    if (typeof window.__INITFORM__ == "function"){
        window.__INITFORM__.call(this);
    }

    if ($.fn.select2 !== undefined) {
        $.fn.select2.defaults.set("theme", "bootstrap");
        $("select.form-control:not(.noselect2)").each(function () {
            if (typeof ($(this).attr("multiple")) == "undefined") {
                $(this).select2();
            } else {
                $(this).select2({allowClear: true, placeholder: ""});
            }
        });
    }

    if ($.fn.iCheck !== undefined) {
        $(".check-box:not(.noicheck),.radio-box:not(.noicheck)").each(function() {
            $(this).iCheck({
                checkboxClass: (typeof($(this).data("style")) == "undefined")?'icheckbox-blue':("icheckbox_" +($(this).data("style") || "square-blue")),
                radioClass:(typeof($(this).data("style")) == "undefined")? 'iradio-blue':("iradio_" +($(this).data("style") || "square-blue"))
            })
        })
    }

    if ($(".time-input").length > 0) {
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
                            if(Object.prototype.toString.apply(callback) == '[object String]'){
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
});