(()=>{var __webpack_modules__={8650:(t,e,n)=>{"use strict";function i(t){o("消息提示",t).modal("show")}function a(t,e){o("确认提示",t,[{name:"确认",click:function(){e.call(this)}}]).modal("show")}function o(t,e,n,i){const a="modal-dialog"+(i?" modal-lg":"");let o=$('<div class="modal fade" tabindex="-1" role="dialog" aria-hidden="true"></div>'),r=$(`<div class="${a}"></div>`);o.append(r);let s=$(`<div class="modal-content">\n         <div class="modal-header">\n            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">\n               &times;\n            </button>\n            <h4 class="modal-title">\n               ${t}\n            </h4>\n         </div>\n         <div class="modal-body">\n            ${"string"==typeof e?e:""}\n         </div>`);"object"==typeof e&&s.find(".modal-body").append(e),r.append(s);let d=$('<div class="modal-footer"></div>');if(s.append(d),n)n.forEach(((t,e)=>{let n=$(`<button type="button" class="btn btn-default">${t.name}</button>`);n.click(function(e){t.click.call(this),t.holdDialog||o.modal("hide")}.bind(this)),d.append(n)}));else{let t=$('<button type="button" class="btn btn-default" data-dismiss="modal">确定</button>');d.append(t)}return o.on("show.bs.modal",(function(){var t=1050;$(document).find(".modal").each((function(e,n){var i=$(n).css("z-index");i&&""!==i&&!isNaN(i)&&(i=parseInt(i))>t&&(t=i)})),t++,o.css({"z-index":t})})),o}n.d(e,{Z9:()=>i,iG:()=>a})},4131:(t,e,n)=>{"use strict";n.d(e,{LW:()=>l,QP:()=>r,Tx:()=>p,XX:()=>d,Zk:()=>s,kQ:()=>o,kW:()=>a,qp:()=>_});var i=n(9307);function a(t){$(window).block({message:'<div class="loaderbox"><div class="loading-circle"></div>'+t+"</div>"})}function o(){$(window).unblock()}function r(){const t=window._server+"/res/ureport-asserts/icons/loading.gif",e=$(window).height()/2,n=$(window).width()/2,i=$(`<div class="ureport-loading-cover" style="position: absolute;left: 0px;top: 0px;width:${2*n}px;height:${2*e}px;z-index: 1199;background:rgba(222,222,222,.5)"></div>`);$(document.body).append(i);const a=$(`<div class="ureport-loading" style="text-align: center;position: absolute;z-index: 1120;left: ${n-35}px;top: ${e-35}px;"><img src="${t}">\n    <div style="margin-top: 5px">打印数据加载中...</div></div>`);$(document.body).append(a)}function s(){$(".ureport-loading-cover").remove(),$(".ureport-loading").remove()}function d(t){var e=new RegExp("(^|&)"+t+"=([^&]*)(&|$)"),n=window.location.search.substr(1).match(e);return null!=n?n[2]:null}function l(t){let e=2.834646*t;return Math.round(e)}function p(t){let e=.352778*t;return Math.round(e)}function _(){return{A0:{width:841,height:1189},A1:{width:594,height:841},A2:{width:420,height:594},A3:{width:297,height:420},A4:{width:210,height:297},A5:{width:148,height:210},A6:{width:105,height:148},A7:{width:74,height:105},A8:{width:52,height:74},A9:{width:37,height:52},A10:{width:26,height:37},B0:{width:1e3,height:1414},B1:{width:707,height:1e3},B2:{width:500,height:707},B3:{width:353,height:500},B4:{width:250,height:353},B5:{width:176,height:250},B6:{width:125,height:176},B7:{width:88,height:125},B8:{width:62,height:88},B9:{width:44,height:62},B10:{width:31,height:44}}}new(n.n(i)())},5037:(t,e,n)=>{"use strict";n.d(e,{Z:()=>o});var i=n(4131),a=n(8650);class o{constructor(){$(window).width(),$(window).height(),this.paperSizeList=(0,i.qp)(),this.dialog=$(`<div class="modal fade" role="dialog" aria-hidden="true" style="z-index: 1110">\n            <div class="modal-dialog modal-lg" style="width: 1250px;">\n                <div class="modal-content">\n                    <div class="modal-header">\n                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">\n                            &times;\n                        </button>\n                        <h4 class="modal-title">\n                            ${window.i18n.pdfPrint.title}\n                        </h4>\n                    </div>\n                    <div class="modal-body" style="padding-top:5px"></div>\n                    <div class="modal-footer">\n                    </div>\n                </div>\n            </div>\n        </div>`),this.body=this.dialog.find(".modal-body"),this.dialog.find(".modal-footer"),this.initBody()}initBody(){const t=$(`<fieldset style="width: 100%;height: 60px;font-size: 12px;border: solid 1px #ddd;border-radius: 5px;padding: 1px 8px;">\n        <legend style="font-size: 12px;width: 60px;border-bottom: none;margin-bottom: 0;">${window.i18n.pdfPrint.setup}</legend>\n        </fieldset>`);this.body.append(t);const e=$(`<div class="form-group" style="display: inline-block"><label>${window.i18n.pdfPrint.paper}</label></div>`);t.append(e),this.pageSelect=$(`<select class="form-control" style="display: inline-block;width: 68px;font-size: 12px;padding: 1px;height: 28px;">\n            <option>A0</option>\n            <option>A1</option>\n            <option>A2</option>\n            <option>A3</option>\n            <option>A4</option>\n            <option>A5</option>\n            <option>A6</option>\n            <option>A7</option>\n            <option>A8</option>\n            <option>A9</option>\n            <option>A10</option>\n            <option>B0</option>\n            <option>B1</option>\n            <option>B2</option>\n            <option>B3</option>\n            <option>B4</option>\n            <option>B5</option>\n            <option>B6</option>\n            <option>B7</option>\n            <option>B8</option>\n            <option>B9</option>\n            <option>B10</option>\n            <option value="CUSTOM">${window.i18n.pdfPrint.custom}</option>\n        </select>`),e.append(this.pageSelect);const n=this;this.pageSelect.change((function(){let t=$(this).val();if("CUSTOM"===t)n.pageWidthEditor.prop("readonly",!1),n.pageHeightEditor.prop("readonly",!1);else{n.pageWidthEditor.prop("readonly",!0),n.pageHeightEditor.prop("readonly",!0);let e=n.paperSizeList[t];n.pageWidthEditor.val(e.width),n.pageHeightEditor.val(e.height),n.paper.width=(0,i.LW)(e.width),n.paper.height=(0,i.LW)(e.height)}n.paper.paperType=t}));const o=$(`<div class="form-group" style="display: inline-block;margin-left: 6px"><span>${window.i18n.pdfPrint.width}</span></div>`);t.append(o),this.pageWidthEditor=$('<input type="number" class="form-control" readonly style="display: inline-block;width: 40px;font-size: 12px;padding: 1px;height: 28px">'),o.append(this.pageWidthEditor),this.pageWidthEditor.change((function(){let t=$(this).val();t&&!isNaN(t)?(n.paper.width=(0,i.LW)(t),n.context.printLine.refresh()):(0,a.Z9)(`${window.i18n.pdfPrint.numberTip}`)}));const r=$(`<div class="form-group" style="display: inline-block;margin-left: 6px"><span>${window.i18n.pdfPrint.height}</span></div>`);t.append(r),this.pageHeightEditor=$('<input type="number" class="form-control" readonly style="display: inline-block;width: 40px;font-size: 12px;padding: 1px;height: 28px">'),r.append(this.pageHeightEditor),this.pageHeightEditor.change((function(){let t=$(this).val();t&&!isNaN(t)?n.paper.height=(0,i.LW)(t):(0,a.Z9)(`${window.i18n.pdfPrint.numberTip}`)}));const s=$(`<div class="form-group" style="display: inline-block;margin-left: 6px"><label>${window.i18n.pdfPrint.orientation}</label></div>`);t.append(s),this.orientationSelect=$(`<select class="form-control" style="display:inline-block;width: 60px;font-size: 12px;padding: 1px;height: 28px">\n            <option value="portrait">${window.i18n.pdfPrint.portrait}</option>\n            <option value="landscape">${window.i18n.pdfPrint.landscape}</option>\n        </select>`),s.append(this.orientationSelect),this.orientationSelect.change((function(){let t=$(this).val();n.paper.orientation=t}));const d=$('<div style="display: inline-block"></div>');t.append(d);const l=$(`<div class="form-group" style="display: inline-block;margin-left:6px"><label>${window.i18n.pdfPrint.leftMargin}</label></div>`);d.append(l),this.leftMarginEditor=$('<input type="number" class="form-control" style="display: inline-block;width: 40px;font-size: 12px;padding: 1px;height: 28px">'),l.append(this.leftMarginEditor),this.leftMarginEditor.change((function(){let t=$(this).val();t&&!isNaN(t)?(n.paper.leftMargin=(0,i.LW)(t),n.context.printLine.refresh()):(0,a.Z9)(`${window.i18n.pdfPrint.numberTip}`)}));const p=$(`<div class="form-group" style="display: inline-block;margin-top: 5px;margin-left: 6px""><label>${window.i18n.pdfPrint.rightMargin}</label></div>`);d.append(p),this.rightMarginEditor=$('<input type="number" class="form-control" style="display: inline-block;width: 40px;font-size: 12px;padding: 1px;height: 28px">'),p.append(this.rightMarginEditor),this.rightMarginEditor.change((function(){let t=$(this).val();t&&!isNaN(t)?(n.paper.rightMargin=(0,i.LW)(t),n.context.printLine.refresh()):(0,a.Z9)(`${window.i18n.pdfPrint.numberTip}`)}));const _=$(`<div class="form-group" style="display: inline-block;margin-left: 6px;"><label>${window.i18n.pdfPrint.topMargin}</label></div>`);d.append(_),this.topMarginEditor=$('<input type="number" class="form-control" style="display: inline-block;width: 40px;font-size: 12px;padding: 1px;height: 28px">'),_.append(this.topMarginEditor),this.topMarginEditor.change((function(){let t=$(this).val();t&&!isNaN(t)?n.paper.topMargin=(0,i.LW)(t):(0,a.Z9)(`${window.i18n.pdfPrint.numberTip}`)}));const c=$(`<div class="form-group" style="display: inline-block;margin-left: 6px""><label>${window.i18n.pdfPrint.bottomMargin}</label></div>`);d.append(c),this.bottomMarginEditor=$('<input type="number" class="form-control" style="display: inline-block;width: 40px;font-size: 12px;padding: 1px;height: 28px">'),c.append(this.bottomMarginEditor),this.bottomMarginEditor.change((function(){let t=$(this).val();t&&!isNaN(t)?n.paper.bottomMargin=(0,i.LW)(t):(0,a.Z9)(`${window.i18n.pdfPrint.numberTip}`)})),(0,i.XX)("_u");const w=window.location.search,h=$(`<button class="btn btn-primary" style="padding-top:5px;height: 30px;margin-left: 10px;">${window.i18n.pdfPrint.apply}</button>`);t.append(h);let u=0;h.click((function(){(0,i.QP)();const t=JSON.stringify(n.paper);$.ajax({type:"POST",data:{_paper:t},url:window._server+"/pdf/newPaging"+w,success:function(){const t=window._server+"/pdf/show"+w+"&_r="+u++;n.iFrame.prop("src",t)},error:function(){(0,i.Zk)(),(0,a.Z9)(`${window.i18n.pdfPrint.fail}`)}})}));const m=$(`<button class="btn btn-danger" style="padding-top:5px;height: 30px;margin-left: 10px;">${window.i18n.pdfPrint.print}</button>`);t.append(m),m.click((function(){window.frames._iframe_for_pdf_print.window.print()}))}initIFrame(){if(this.iFrame)return;const t=buildLocationSearchParameters(),e=$(window).height(),n=window._server+"/pdf/show"+t+"&_p=1";this.iFrame=$(`<iframe name="_iframe_for_pdf_print" style="width: 100%;height:${e}px;margin-top: 5px;border:solid 1px #c2c2c2" frameborder="0" src="${n}"></iframe>`),this.body.append(this.iFrame),this.iFrame.get(0);const a=window.navigator.appName.indexOf("Internet Explorer"),o=!!window.MSInputMethodContext&&!!document.documentMode;-1!==a||o||(0,i.QP)(),this.iFrame.on("load",(function(){(0,i.Zk)()}))}show(t){this.paper=t,this.pageSelect.val(this.paper.paperType),this.pageWidthEditor.val((0,i.Tx)(this.paper.width)),this.pageHeightEditor.val((0,i.Tx)(this.paper.height)),this.pageSelect.trigger("change"),this.leftMarginEditor.val((0,i.Tx)(this.paper.leftMargin)),this.rightMarginEditor.val((0,i.Tx)(this.paper.rightMargin)),this.topMarginEditor.val((0,i.Tx)(this.paper.topMargin)),this.bottomMarginEditor.val((0,i.Tx)(this.paper.bottomMargin)),this.orientationSelect.val(this.paper.orientation),this.dialog.modal("show"),this.initIFrame()}}},9307:(t,e,n)=>{var i;!function(){"use strict";var a=function(){var t,e,n=[],i=-1,a=0,o=!1;return e=function(t,e){return t&&"function"==typeof t[e]?(o=!0,t[e](),o=!1,this):this},{add:function(e){return o||(n.splice(i+1,n.length-i),n.push(e),a&&n.length>a&&(0,s=-(a+1),(r=n).splice(0,!s||1+s-0+(!(s<0^!0)&&(s<0||-1)*r.length)),r.length),i=n.length-1,t&&t()),this;var r,s},setCallback:function(e){t=e},undo:function(){var a=n[i];return a?(e(a,"undo"),i-=1,t&&t(),this):this},redo:function(){var a=n[i+1];return a?(e(a,"redo"),i+=1,t&&t(),this):this},clear:function(){var e=n.length;n=[],i=-1,t&&e>0&&t()},hasUndo:function(){return-1!==i},hasRedo:function(){return i<n.length-1},getCommands:function(){return n},getIndex:function(){return i},setLimit:function(t){a=t}}};void 0===(i=function(){return a}.call(e,n,e,t))||(t.exports=i)}()},6551:t=>{"use strict";t.exports=JSON.parse('{"pdfPrint":{"title":"PDF在线打印","setup":"打印配置","paper":"纸张:","custom":"自定义","width":"宽(毫米):","numberTip":"请输入数字！","height":"高(毫米):","orientation":"方向:","portrait":"纵向","landscape":"横向","leftMargin":"左边距(毫米):","rightMargin":"右边距(毫米):","topMargin":"上边距(毫米):","bottomMargin":"下边距(毫米):","apply":"应用","fail":"操作失败！","print":"打印"}}')},2731:t=>{"use strict";t.exports=JSON.parse('{"pdfPrint":{"title":"pdf online print","setup":"Print Setup","paper":"Paper:","custom":"Custom","width":"Width(mm):","numberTip":"Please input a number","height":"Height(mm):","orientation":"Orientation:","portrait":"Portrait","landscape":"Landscape","leftMargin":"Left Margin(mm):","rightMargin":"Right Margin(mm):","topMargin":"Top Margin(mm):","bottomMargin":"Bottom Margin(mm):","apply":"Apply","fail":"Apply fail!","print":"Print"}}')}},__webpack_module_cache__={};function __webpack_require__(t){var e=__webpack_module_cache__[t];if(void 0!==e)return e.exports;var n=__webpack_module_cache__[t]={exports:{}};return __webpack_modules__[t](n,n.exports,__webpack_require__),n.exports}__webpack_require__.n=t=>{var e=t&&t.__esModule?()=>t.default:()=>t;return __webpack_require__.d(e,{a:e}),e},__webpack_require__.d=(t,e)=>{for(var n in e)__webpack_require__.o(e,n)&&!__webpack_require__.o(t,n)&&Object.defineProperty(t,n,{enumerable:!0,get:e[n]})},__webpack_require__.o=(t,e)=>Object.prototype.hasOwnProperty.call(t,e);var __webpack_exports__={};(()=>{"use strict";var _Utils_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(4131),_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(8650),_dialog_PDFPrintDialog_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(5037),_i18n_preview_json__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(6551),_i18n_preview_en_json__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(2731);function buildPrintStyle(t){const e=(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Tx)(t.leftMargin),n=(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Tx)(t.topMargin),i=(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Tx)(t.rightMargin),a=(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Tx)(t.bottomMargin),o=t.paperType;let r=o;return"CUSTOM"===o&&(r=(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Tx)(t.width)+"mm "+(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Tx)(t.height)+"mm"),`\n        @media print {\n            .page-break{\n                display: block;\n                page-break-before: always;\n            }\n        }\n        @page {\n          size: ${r} ${t.orientation};\n          margin-left: ${e}mm;\n          margin-top: ${n}mm;\n          margin-right:${i}mm;\n          margin-bottom:${a}mm;\n        }\n    `}function _refreshData(t){const e=buildLocationSearchParameters("_i");let n=window._server+`/preview/loadData${e}`;const i=window._totalPage;i>0&&(window._currentPageIndex&&(window._currentPageIndex>i&&(window._currentPageIndex=1),n+="&_i="+window._currentPageIndex),$("#pageSelector").val(window._currentPageIndex)),$.ajax({url:n,type:"GET",success:function(e){const n=$("#_ureport_table");n.empty(),window._totalPage=e.totalPageWithCol,n.append(e.content),_buildChartDatas(e.chartDatas),buildPaging(window._currentPageIndex,window._totalPage),window._currentPageIndex&&window._currentPageIndex++,setTimeout((function(){_refreshData(t)}),t)},error:function(e){const n=$("#_ureport_table");n.empty(),e&&e.responseText?n.append("<h3 style='color: #d30e00;'>服务端错误："+e.responseText+"</h3>"):n.append("<h3 style='color: #d30e00;'>加载数据失败</h3>"),setTimeout((function(){_refreshData(t)}),t)}})}!function(t,e){var n=e.ajax;e.ajax=function(i){var a={beforeSend:function(t){},error:function(t,e,n){},success:function(t,e){},complete:function(t,e){}};i.beforeSend&&(a.beforeSend=i.beforeSend),i.error?a.error=i.error:a.error=function(t){try{opt.error(JSON.parse(t.responseText).msg||t.responseText)}catch(e){console.error(t.responseText)}opt.modal.closeLoading()},i.success&&(a.success=i.success),i.complete&&(a.complete=i.complete),t.__ISCSRF__&&String(i.type).toUpperCase()===String("POST").toUpperCase()&&(i.headers&&i.headers["X-CSRF-Token"]||(i=e.extend(i,{headers:{"X-CSRF-Token":e('meta[name="csrf-token"]').attr("content")}})));var o=e.extend(i,{xhrFields:{withCredentials:!0},error:function(t,e,n){a.error(t,e,n)},success:function(t,e){a.success(t,e)},beforeSend:function(t){a.beforeSend(t)},complete:function(t,e){a.complete(t,e)}});return i.xhrFields&&(o.xhrFields=i.xhrFields),n(o)}}(window,jQuery),$(document).ready((function(){let language=window.__LANG__||window.navigator.language;language||(language="zh_cn"),language=language.toLowerCase(),window.i18n=_i18n_preview_json__WEBPACK_IMPORTED_MODULE_2__,"zh_cn"!==language&&(window.i18n=_i18n_preview_en_json__WEBPACK_IMPORTED_MODULE_3__),$(".ureport-print").click((function(){let urlParameters=buildLocationSearchParameters();urlParameters+="&__a=0";const url=window._server+"/preview/loadPrintPages"+urlParameters;$.ajax({url,type:"POST",beforeSend:function(){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.kW)("正在拼命加载中，请稍后...")},success:function(result){$.get(window._server+"/preview/loadPagePaper"+urlParameters,(function(paper){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Zk)();const html=result.html,watermark=result.watermark,_echarts=result.echarts,iFrame=window.frames._print_frame;let styles='<style type="text/css">';styles+=buildPrintStyle(paper),styles+=$("#_ureport_table_style").html(),styles+="</style>",$(iFrame.document.body).html(styles+html),watermark&&watermark.length>0&&(iFrame.window.onload=function(){_watermark(iFrame,{watermark_txt:watermark})},iFrame.window.onload()),_echarts&&_echarts.length>0?__loadScript(iFrame.document,window.basePath+"/static/plugins/echarts/echarts.min.js",(function(){console.log(_echarts);const es=eval("("+_echarts+")");console.log(es),__printInitECharts(iFrame.document,es,0,(function(){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.kQ)(),iFrame.window.focus(),iFrame.window.print()}))})):((0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.kQ)(),iFrame.window.focus(),iFrame.window.print())}))},error:function(t){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Zk)(),t&&t.responseText?(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.Z9)("服务端错误："+t.responseText):(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.Z9)("服务端出错！")}})})),$(".ureport-printAll").click((function(){var t=$(".ureport-printAll").data("pg");if(t>30)(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.iG)('当前总页数过大,一共[<span style="font-weight: bold;font-size: 14pt;color: #f44336;">'+t+"</span>]页,请确认是否打印？数据加载有点慢请耐心等待...",(function(){let t=buildLocationSearchParameters();t+="&__a=1";const e=window._server+"/preview/loadPrintPages"+t;(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.QP)(),$.ajax({url:e,type:"POST",success:function(e){$.get(window._server+"/preview/loadPagePaper"+t,(function(t){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Zk)();const n=e.html,i=e.watermark,a=window.frames._print_frame;let o='<style type="text/css">';o+=buildPrintStyle(t),o+=$("#_ureport_table_style").html(),o+="</style>",$(a.document.body).html(o+n),i&&i.length>0&&(a.window.onload=function(){_watermark(a,{watermark_txt:i})},a.window.onload()),a.window.focus(),a.window.print()}))},error:function(t){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Zk)(),t&&t.responseText?(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.Z9)("服务端错误："+t.responseText):(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.Z9)("服务端出错！")}})}));else{let t=buildLocationSearchParameters();t+="&__a=1";const e=window._server+"/preview/loadPrintPages"+t;(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.QP)(),$.ajax({url:e,type:"POST",success:function(e){$.get(window._server+"/preview/loadPagePaper"+t,(function(t){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Zk)();const n=e.html,i=e.watermark,a=window.frames._print_frame;let o='<style type="text/css">';o+=buildPrintStyle(t),o+=$("#_ureport_table_style").html(),o+="</style>",$(a.document.body).html(o+n),i&&i.length>0&&(a.window.onload=function(){_watermark(a,{watermark_txt:i})},a.window.onload()),a.window.focus(),a.window.print()}))},error:function(t){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Zk)(),t&&t.responseText?(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.Z9)("服务端错误："+t.responseText):(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.Z9)("服务端出错！")}})}}));let directPrintPdf=!1,index=0;const pdfPrintDialog=new _dialog_PDFPrintDialog_js__WEBPACK_IMPORTED_MODULE_1__.Z;$(".ureport-pdf-print").click((function(){const t=buildLocationSearchParameters();$.get(window._server+"/preview/loadPagePaper"+t,(function(t){pdfPrintDialog.show(t)}))})),$(".ureport-pdf-direct-print").click((function(){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.QP)();const t=buildLocationSearchParameters(),e=window._server+"/pdf/show"+t+"&_i="+index++,n=window.frames._print_pdf_frame;directPrintPdf||(directPrintPdf=!0,$("iframe[name='_print_pdf_frame']").on("load",(function(){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.Zk)(),n.window.focus(),n.window.print()}))),n.window.focus(),n.location.href=e})),$(".ureport-export-pdf").click((function(){const t=buildLocationSearchParameters(),e=window._server+"/pdf"+t;window.open(e,"_blank")})),$(".ureport-export-word").click((function(){const t=buildLocationSearchParameters(),e=window._server+"/word"+t;window.open(e,"_blank")})),$(".ureport-export-excel").click((function(){const t=buildLocationSearchParameters(),e=window._server+"/excel"+t;window.open(e,"_blank")})),$(".ureport-export-excel-paging").click((function(){const t=buildLocationSearchParameters(),e=window._server+"/excel/paging"+t;window.open(e,"_blank")})),$(".ureport-export-excel-paging-sheet").click((function(){const t=buildLocationSearchParameters(),e=window._server+"/excel/sheet"+t;window.open(e,"_blank")}))})),window._currentPageIndex=null,window._totalPage=null,window._watermark=function(t,e){var n={watermark_txt:"演示样例",watermark_x:20,watermark_y:20,watermark_rows:100,watermark_cols:20,watermark_x_space:10,watermark_y_space:10,watermark_color:"#aaa",watermark_alpha:.8,watermark_fontsize:"15px",watermark_font:"微软雅黑",watermark_width:150,watermark_height:80,watermark_angle:15};if(2===arguments.length&&"object"==typeof arguments[1]){var i=arguments[1]||{};for(let t in i)i[t]&&n[t]&&i[t]===n[t]||i[t]&&(n[t]=i[t])}let a=t.document.createDocumentFragment(),o=Math.max(t.document.body.scrollWidth,t.document.body.clientWidth),r=.015*o;o-=r;let s,d,l=Math.max(t.document.body.scrollHeight-80,t.document.body.clientHeight-40);(0==n.watermark_cols||parseInt(n.watermark_x+n.watermark_width*n.watermark_cols+n.watermark_x_space*(n.watermark_cols-1))>o)&&(n.watermark_cols=parseInt((o-n.watermark_x+n.watermark_x_space)/(n.watermark_width+n.watermark_x_space)),n.watermark_x_space=parseInt((o-n.watermark_x-n.watermark_width*n.watermark_cols)/(n.watermark_cols-1))),(0==n.watermark_rows||parseInt(n.watermark_y+n.watermark_height*n.watermark_rows+n.watermark_y_space*(n.watermark_rows-1))>l)&&(n.watermark_rows=parseInt((n.watermark_y_space+l-n.watermark_y)/(n.watermark_height+n.watermark_y_space)),n.watermark_y_space=parseInt((l-n.watermark_y-n.watermark_height*n.watermark_rows)/(n.watermark_rows-1)));for(let e=0;e<n.watermark_rows;e++){d=n.watermark_y+(n.watermark_y_space+n.watermark_height)*e;for(let i=0;i<n.watermark_cols;i++){s=n.watermark_x+(n.watermark_width+n.watermark_x_space)*i;var p=t.document.createElement("div");p.id="mask_div"+e+i,p.className="mask_div",p.appendChild(t.document.createTextNode(n.watermark_txt)),p.style.webkitTransform="rotate(-"+n.watermark_angle+"deg)",p.style.MozTransform="rotate(-"+n.watermark_angle+"deg)",p.style.msTransform="rotate(-"+n.watermark_angle+"deg)",p.style.OTransform="rotate(-"+n.watermark_angle+"deg)",p.style.transform="rotate(-"+n.watermark_angle+"deg)",p.style.visibility="",p.style.position="fixed",p.style.left=s+"px",p.style.top=d+"px",p.style.overflow="hidden",p.style.zIndex="19920219",p.style.pointerEvents="none",p.style.opacity=n.watermark_alpha,p.style.fontSize=n.watermark_fontsize,p.style.fontFamily=n.watermark_font,p.style.color=n.watermark_color,p.style.textAlign="center",p.style.width=n.watermark_width+"px",p.style.height=n.watermark_height+"px",p.style.display="block",e%2==0&&i%2==0&&a.appendChild(p),e%2==1&&i%2==1&&a.appendChild(p)}}t.document.body.appendChild(a)},window.__loadScript=function(t,e,n){const i=t.createElement("script");i.type="text/javascript",i.readyState?i.onreadystatechange=function(){"loaded"!==i.readyState&&"complete"!==i.readyState||(i.onreadystatechange=null,n())}:i.onload=function(){n()},i.src=e,t.body.append(i)},window.buildPaging=function(t,e){if(0===e)return;if(!t)return;window._currentPageIndex||(window._currentPageIndex=t),t=window._currentPageIndex,window._totalPage||(window._totalPage=e);const n=$("#pageSelector");if(n.change((function(){const t=window.buildLocationSearchParameters("_i");let e=window._server+`/preview${t}&_i=${$(this).val()}`;window.open(e,"_self")})),n.val(t),1===e)return;const i=window.buildLocationSearchParameters("_i"),a=$("#pageLinkContainer");if(a.empty(),t>1){let e=window._server+`/preview${i}&_i=${t-1}`;const n=$('<button type="button" class="btn btn-link btn-sm">上一页</button>');a.append(n),n.click((function(){window.open(e,"_self")}))}if(t<e){let e=window._server+`/preview${i}&_i=${t+1}`;const n=$('<button type="button" class="btn btn-link btn-sm">下一页</button>');a.append(n),n.click((function(){window.open(e,"_self")}))}},window._intervalRefresh=function(t,e){if(!t)return;window._totalPage=e;const n=1e3*t;setTimeout((function(){_refreshData(n)}),n)},window._buildChartDatas=function(chartData){if(chartData)for(let d of chartData){let json=d.json;json=JSON.parse(json,(function(k,v){return v.indexOf&&v.indexOf("function")>-1?eval("(function(){return "+v+" })()"):v})),_buildChart(d.id,json)}},window._buildEchartDatas=function(chartData){if(chartData)for(let d of chartData){let json=d.json;console.log(json),json=eval("("+json+")"),___buildECharts(document,d.id,json)}},window.__printInitECharts=function(dom,es,i,fun){if(i>=es.length)return void window.setTimeout((function(){fun()}),500);let json=eval("("+es[i].json+")"),id=es[i].id;___buildECharts(dom,id,json,(function(){++i<es.length?__printInitECharts(dom,es,i,fun):window.setTimeout((function(){fun()}),500)}))},window.___buildECharts=function(t,e,n,i){const a=t.getElementById(e),o=window.echarts.init(a);if("sql"==n.type)o.setOption(n.data),"function"==typeof i&&o.on("finished",(function(){let t='<img src="'+o.getDataURL()+'" style="width:'+a.clientWidth+"px; height:"+a.clientHeight+'px"/>';a.innerHTML=t,i()}));else if("api"==n.type){let t=n.apiUrl;t.indexOf("http")<0&&(t=window.baseURL+t);let e={url:t,type:"POST",dataType:"json",success:function(t){if(0==t.code){if("line_stack"==n.chartType||"Bar_Label_Rotation"==n.chartType||"bar_stack"==n.chartType||"line_step"==n.chartType){const e=t.data;let i=[],a=[],r=[];e.forEach((function(t){i.push(t.name),a.push(t.type)})),i=Array.from(new Set(i)),a=Array.from(new Set(a)),a.forEach((function(t){let a=JSON.parse(JSON.stringify(n.data.series[0]));a.name=t,a.data=[],a.emphasis={focus:"series"},i.forEach((function(n){for(let i of e)if(i.name==n&&i.type==t){a.data.push(i.value);break}})),r.push(a)})),n.data.series=r,"bar_stack"==n.chartType?n.data.yAxis.data=i:n.data.xAxis.data=i,n.data.legend=n.data.legend||{},n.data.legend.data=a,console.log(n.data),o.setOption(n.data)}else if("bar_Background"==n.chartType||"line_basic"==n.chartType||"line_area"==n.chartType){const e=t.data;let i=[],a=[];e.forEach((function(t){i.push(t.name),a.push(t.value)})),n.data.series[0].data=a,n.data.xAxis.data=i,o.setOption(n.data)}else if("pie_simple"==n.chartType||"pie_doughnut"==n.chartType||"pie_roseType"==n.chartType){const e=t.data;n.data.series[0].data=e,o.setOption(n.data)}"function"==typeof i&&o.on("finished",(function(){let t='<img src="'+o.getDataURL()+'" style="width:'+a.clientWidth+"px; height:"+a.clientHeight+'px"/>';a.innerHTML=t,i()}))}}};$.ajax(e)}},window._buildChart=function(t,e){const n=document.getElementById(t);if(!n)return;let i=e.options;i||(i={},e.options=i);let a=i.animation;a||(a={},i.animation=a),a.onComplete=function(e){const n=e.chart.toBase64Image(),i=window.location.search,a=window._server+"/chart/storeData"+i,o=$("#"+t),r=parseInt(o.css("width")),s=parseInt(o.css("height"));$.ajax({type:"POST",data:{_base64Data:n,_chartId:t,_width:r,_height:s},url:a})},new Chart(n,e)},window.__clearLockhost=function(){if(window.localStorage)for(var t in localStorage)"__JU"==t.substring(0,4)&&window.localStorage.removeItem(t)},window.buildLocationSearchParameters=function(t){let e=window.location.search;e.length>0&&(e=e.substring(1,e.length));let n={};const i=e.split("&");for(let e=0;e<i.length;e++){const a=i[e];if(""===a)continue;const o=a.split("=");let r=o[0];if(t&&r===t)continue;let s=o[1];n[r]=s}if(window.searchFormParameters)for(let e in window.searchFormParameters){if(e===t)continue;const i=window.searchFormParameters[e];i&&(n[e]=i)}let a="?";for(let t in n)a+="?"===a?t+"="+n[t]:"&"+t+"="+n[t];return a},window.submitSearchForm=function(t,e){window.searchFormParameters={};for(let t of window.formElements){const e=t.call(this);for(let t in e){let n=e[t];n=encodeURI(n),n=encodeURI(n),window.searchFormParameters[t]=n}}const n=window.buildLocationSearchParameters("_i");let i=window._server+"/preview/loadData"+n;const a=$("#pageSelector");a.length>0&&(i+="&_i=1"),$.ajax({url:i,type:"POST",beforeSend:function(){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.kW)("数据处理中，请稍后...")},success:function(t){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.kQ)(),window._currentPageIndex=1;const e=$("#_ureport_table");e.empty(),e.append(t.content),_buildChartDatas(t.chartDatas),_buildEchartDatas(t.echartDatas);const n=t.totalPage;if(window._totalPage=n,a.length>0){a.empty();for(let t=1;t<=n;t++)a.append(`<option>${t}</option>`);const e=t.pageIndex||1;a.val(e),$("#totalPageLabel").html(n),buildPaging(e,n)}},error:function(t){(0,_Utils_js__WEBPACK_IMPORTED_MODULE_0__.kQ)(),t&&t.responseText?(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.Z9)("服务端错误："+t.responseText):(0,_MsgBox_js__WEBPACK_IMPORTED_MODULE_4__.Z9)("查询操作失败！")}})}})()})();