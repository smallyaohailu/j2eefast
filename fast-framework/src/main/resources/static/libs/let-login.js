/*!
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 * @author zhouzhou
 * @data 2021-09-20
 * @version 1.0.0
 */
var vm = new Vue({
    el:'#__app',
    data:{
        username: '',
        password: '',
        captcha: '',
        /*errorMsg: '',*/
        rememberme : false,
        loadcode : false,
        phoneValid: false,
        src: 'captcha.gif',
        tenantId: '',
        tenantData: [],
        phoneNo:'',
        sendValid: true,
        loginValidCode: '',
        tenantLabel: TENANTLABEL,
        login_toggle: MOBILE_TOGGLE,
    },
    beforeCreate: function(){
        if(self != top){
            top.location.href = self.location.href;
        }
    },
    methods: {
        refreshCode: function(){ /*刷新验证码*/
            this.src = baseURL+"captcha.gif?t=" + $.now();
        },
        __clearLang: function(){/*清空本地多语言*/
            window.localStorage.removeItem("i18nLang");
        },
        _onkeydown : function(e){
            var that = this;
            if(e && e.keyCode==13) { /* enter 键 */
                that.login();
            }
        },
        /***手机验证码登录*/
        _phoneValid: function(){
            if(!vm.phoneValid){
                vm.phoneValid = true;
                vm.login_toggle = USERNAME_TOGGLE;
            }else{
                vm.phoneValid = false;
                vm.login_toggle = MOBILE_TOGGLE;
            }
        },
        setValidCodeTime:function(time){
            var that = this;
            --time;
            if(time == 0){
                $('#butValieCode').val(RM_AUTH_CODE);
                $('#butValieCode').removeAttr("disabled");
                return;
            }
            $('#butValieCode').attr("disabled","disabled");
            $('#butValieCode').val(time +MM_AUTH_CODE);
            window.setTimeout(function(){
                that.setValidCodeTime(time);
            },1000);
        },
        getValidCode: function(){
            var that = this;
            if(TENANTMODEL){
	            if(vm.tenantId.length == 0){
	                return that.errrInfo(TENANTLABEL);
	            }
            }
            var phone = vm.phoneNo;
            if(!(/^1[3456789]\d{9}$/.test(phone))){
                return that.errrInfo(PHONETIP);
            }
			
			if(ISVALIDATIONCODE){
            	$("#verfyImg").find(".mask").css("display","block");
            }else{
				var data = {"mobile":vm.phoneNo,
                	"tenantId":vm.tenantId};
				that.sendValidCode(data);
				return;
			}
			
        },
		sendValidCode: function(data){
			
			var that = this;
			/*获取验证码*/
            $.ajax({
                type: "POST",
                url: baseURL+"getLoginValideCode",
                data: data,
                dataType: "json",
                beforeSend: function () {
                    $(window).block({ message: '<div class="loaderbox"><div class="loading-circle"></div> '
                            + WAITING + '</div>' });
                },
                success: function(result){
                    $(window).unblock();
                    if(result.code == 0){
						$('#butValieCode').attr("disabled","disabled");
                        $('#loginValidCode').attr("placeholder",SENDOW);
                        vm.sendValid = false;
                        that.setValidCodeTime(60);
                        //演示
                        //vm.errrInfo("模拟测试验证码:"+result.valideCode,10000);
                    }
                    else{
                        vm.errrInfo(result.msg);
                    }
                },
                error: function(xhr, textStatus) {
                    vm.errrInfo(JSON.parse(xhr.responseText).msg || xhr.responseText );
                    $(window).unblock();
                }
            });
		},
        login: function (event) {

            var that = this;
			
            if(this.getCookie("_secretKey") == ""){
                location.reload();
                return;
            }
            if(!vm.phoneValid){
                if(vm.username.length < 3 ||  vm.password.length < 3){
                    return that.errrInfo(USERNAMEERR);
                }
            }

            if(vm.phoneValid){
                var phone = vm.phoneNo;
                if(!(/^1[3456789]\d{9}$/.test(phone))){
                    return that.errrInfo(PHONETIP);;
                }
				if(vm.loginValidCode.length != 6){
					return that.errrInfo(INCORRECT_VERIFICATION_CODE);
				}
                var data = {"mobile":phone,
                    "valideCode":vm.loginValidCode,
                    "tenantId":vm.tenantId,
                    "rememberMe":vm.rememberme};
		        $.ajax({
		            type: "POST",
		            url: baseURL+"valideCodeLogin",
		            data: data,
		            dataType: "json",
		            success: function(result){
		                if(result.code == 0){
		                    setTimeout(
		                        function () {
		                            location.href = ctx + result.index;
		                        }
		                        , 100);
		                }
		                else{
		                    $(window).unblock();
		                    vm.errrInfo(result.msg);
		                }
		            },
		            error: function(xhr, textStatus) {
		                vm.errrInfo(JSON.parse(xhr.responseText).msg || xhr.responseText );
		                $(window).unblock();
		            }
		        });
		        return;
            }else{
            	if(TENANTMODEL){
	                if(vm.tenantId.length == 0){
	                    return that.errrInfo(TENANTLABEL);
	                }
                }
                if(VERIFICATION){
                	$("#verfyImg").find(".mask").css("display","block");
                }else{
	                $(window).block({ message: '<div class="loaderbox"><div class="loading-circle"></div> '
	                        + WAITING + '</div>' });
	                var s4 = SoftEncryption.loginSM2Encry(vm.username,vm.password,that.getCookie("_secretKey"));
	                var data = {"username":s4.username,
	                    "password":s4.password,
	                    "kg4":s4.kg4,
	                    "sign":s4.sign,
	                    "captcha":vm.captcha,
	                    "tenantId":vm.tenantId,
	                    "rememberMe":vm.rememberme};
	                that.postLogin(data,true);
                }
            	return;
            }
        },
        selecTenant: function(id,event){
            var el = event.currentTarget;
            vm.tenantLabel = el.innerHTML;
            vm.tenantId = id;
        },
        errrInfo : function(text){
            $.toast({
                heading: WARNING,
                text: text,
                hideAfter:6000,
                position: {
                    right: 8,
                    bottom: 4
                },
                textAlign: 'left',
                showHideTransition: 'slide',
                allowToastClose: true,
                loader: true,
                /*stack: 5,*/
                icon: 'error'
            })
        },
        getCookie:function(c_name){
            if (document.cookie.length>0){
                c_start=document.cookie.indexOf(c_name + "=");
                if (c_start!=-1){
                    c_start=c_start + c_name.length+1;
                    c_end=document.cookie.indexOf(";",c_start);
                    if (c_end==-1) {
                        c_end=document.cookie.length
                    };
                    return unescape(document.cookie.substring(c_start,c_end))
                }
            };
            return __SECRETKEY__;
        },
        postLogin: function(data,flag){
            $.ajax({
                type: "POST",
                url: baseURL+"login",
                data: data,
                dataType: "json",
                success: function(result){
                    if(result.code == 0){
                        if($("input[name='rememberUserCode']").is(':checked')){
                            window.localStorage.setItem('rememberUserCode',vm.username);
                        }else{
                            window.localStorage.setItem('rememberUserCode',"");
                        };
                        setTimeout(
                            function () {
                            	location.href = ctx + result.index;
                            }
                            , 100);
                    }else if(result.code == '50004' || result.code == '50006'){
                        if(flag){$(window).unblock()};
                        vm.loadcode = true;
                        vm.errrInfo(result.msg);
                        vm.captcha = '';
                        vm.password = '';
                        vm.refreshCode();
                    }else if(result.code == '99998'){
                        location.href = baseURL +'authInsu';
                    }
                    else{
                        if(flag){$(window).unblock()};
                        vm.errrInfo(result.msg);
                        vm.captcha = '';
                        vm.password = '';
                        vm.refreshCode();
                    }
                },
                error: function(xhr, textStatus) {
                    vm.errrInfo(JSON.parse(xhr.responseText).msg || xhr.responseText );
                    vm.captcha = '';
                    vm.password = '';
                    if(flag){$(window).unblock()};
                    vm.refreshCode();
                }
            });
        }
    },
    mounted :function(){
    	
        var that = this;
        if(window.localStorage.getItem('rememberUserCode') && window.localStorage.getItem('rememberUserCode') != ""){
            $("input[name='rememberUserCode']").iCheck('check');
            this.username = window.localStorage.getItem('rememberUserCode');
        }else{
            $("input[name='rememberUserCode']").iCheck('uncheck');
        }

        if(TENANTMODEL){
	        if(window.localStorage.getItem('tenantId') && window.localStorage.getItem('tenantId') != ""){
	            this.tenantId = window.localStorage.getItem('tenantId');
	            $("#tenantId").val(this.tenantId).select2();
	        }
			/* 设置租户选择**/
	        if ($.fn.select2 !== undefined) {
	            $.fn.select2.defaults.set( "theme", "bootstrap" );
	            $("select.form-control:not(.noselect2)").each(function () {
	                $(this).select2({allowClear: false,placeholder:TENANTLABEL});
	            })
	        }
        }
        /*绑定记住我样式*/
        $("input[type=checkbox]").each(function () {
            $(this).iCheck({
                checkboxClass: "icheckbox_" + ($(this).data("style") || "square-blue")
            }).on('ifChanged', function(event){
                if($(this).attr("name") == "rememberme"){
                    if($(this).is(':checked')){
                        vm.rememberme = true;
                    }else{
                        vm.rememberme = false;
                    }
                }
            });
        });

        /* 设置页面模式 */
        if(typeof (Storage) !== 'undefined'){
            var css = window.localStorage.getItem('mode');
            if(typeof  css !== 'undefined' && css && css.length > 0){
                $('html').addClass(css)
            }
        };

        /* 设置tooltip */
        $('[data-toggle="tooltip"]').each(function () {
            $(this).tooltip();
        });

        /* 粒子线条背景*/
        /*$('#canvas').particleground({
            dotColor:'#7ec7fd',
            lineColor:'#7ec7fd'
        });*/

        if(VERIFICATION || ISVALIDATIONCODE){
	        /** 初始化验证码  弹出式*/
	        $('#verfyImg').slideVerify({
	            baseUrl: ctx,
	            mode:'pop',
	            success : function(params) {
					if(!vm.phoneValid){
						/**账号 密码登录 */
						var s4 = SoftEncryption.loginSM2Encry(vm.username,vm.password,
	                    that.getCookie("_secretKey"));
		                var data = {"username":s4.username,
		                    "password":s4.password,
		                    "kg4":s4.kg4,
		                    "sign":s4.sign,
		                    "captcha":vm.captcha,
		                    "tenantId":vm.tenantId,
		                    "rememberMe":vm.rememberme};
			                data = $.extend(data, params);
			                that.postLogin(data,false);
					}else{
						/**获取手机验证码 */
						var data = {"mobile":vm.phoneNo,
		                	"tenantId":vm.tenantId};
			                data = $.extend(data, params);
			                that.sendValidCode(data);
					}
	                
	            },
	            error : function() {}
	        });
        }
		
        /*获取页面键盘事件*/
        window.document.onkeydown = that._onkeydown;

        var href= location.href;
        if(href.indexOf("kickout=2")>0){
            that.errrInfo("您被提下线,请重新登陆!");
        };
        if(href.indexOf("kickout=1")>0){
            that.errrInfo("您的账号在另一台设备上登录,如非本人操作，请立即修改密码！");
        };
    }
});

/**
 * 选择回调
 */
function selectCallback(id, event, data) {
    if(id == 'tenantId'){
        vm.tenantId = data[0].id;
        window.localStorage.setItem('tenantId',data[0].id);
    }
}
