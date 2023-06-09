package com.j2eefast.common.core.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.OshiUtil;
import com.bstek.ureport.export.ExportManager;
import com.bstek.ureport.export.html.HtmlReport;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.crypto.EnctryptTools;
import com.j2eefast.common.core.exception.RxcException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.request.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import oshi.hardware.NetworkIF;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 高频使用工具类
 * @author zhouzhou
 * @date 2020-03-11 15:02
 */
@Slf4j
public class ToolUtil{

	private static int 							counter 							= 0;

    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 视频格式
            "mp4", "avi", "rmvb",
            // pdf
            "pdf" };

    private static ResourceLoader resourceLoader;
    private static ResourcePatternResolver resourceResolver;
    static{
        resourceLoader = new DefaultResourceLoader();
        resourceResolver = new PathMatchingResourcePatternResolver(resourceLoader);
    }

    /**
     * 获取资源加载器（可读取jar内的文件）
     */
    public static Resource getResource(String location) {
        return resourceLoader.getResource(location);
    }

    /**
     * 检查文件是否可下载
     *
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource)
    {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, ".."))
        {
            return false;
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(DEFAULT_ALLOWED_EXTENSION, FileUtil.extName(resource)))
        {
            return true;
        }

        // 不在允许下载的文件规则
        return false;
    }
	
	/**
     * 获取随机字符,自定义长度
     *
     * @author zhouzhou
     * 2020-03-11 15:07
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    public static void isBlank(String str, String message) {
		if (ToolUtil.isEmpty(str)) {
			throw new RxcException(message);
		}
	}

	public static void isNull(Object object, String message) {
		if (object == null) {
			throw new RxcException(message);
		}
	}
	
	public static void isNull(Object object, String message, String code) {
		if (object == null) {
			throw new RxcException(message,code);
		}
	}

    /**
     * 字符串大写字母个数
     * @param str
     * @return
     */
	public static int charUpperCaseLen(String str){
        if (isEmpty(str)) {
            return 0;
        }
        int len = 0;
        char[] buffer = str.toCharArray();
        for (int i = 0; i < buffer.length; i++) {
             char ch = buffer[i];
            if (Character.isUpperCase(ch)) {
                len ++;
            }
        }
        return len;
    }


	
	/**
	 * 判断对象是否为空  true 不为空
	 * @author zhouzhou
	 * @date 2020-03-11 15:07
	 */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * 
     * 对象是否为空 true 为空
     * @author zhouzhou
     * @date 2020-03-11 15:09
     */
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof String) {
            if ("".equals(o.toString().trim())) {
                return true;
            }
        } else if (o instanceof List) {
            if (((List<?>) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Map) {
            if (((Map<?, ?>) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Set) {
            if (((Set<?>) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Object[]) {
            if (((Object[]) o).length == 0) {
                return true;
            }
        } else if (o instanceof int[]) {
            if (((int[]) o).length == 0) {
                return true;
            }
        } else if (o instanceof long[]) {
            if (((long[]) o).length == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对象组中是否存在空对象
     *
     * @author zhouzhou
     * @Date 2020-03-11 15:09
     */
    public static boolean isOneEmpty(Object... os) {
        for (Object o : os) {
            if (isEmpty(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * String[] 转换 Long[]
     * @param strs
     * @return
     */
    public  static Long[] StrToLong(String[] strs){
        Long[] lon = new Long[strs.length];
        for(int i=0; i< strs.length; i++){
            lon[i] = Long.valueOf(strs[i]);
        }
        return lon;
    }

    /**
     * 对象组中是否全是空对象
     *
     * @author zhouzhou
     * @date 2020-03-11 15:09
     */
    public static boolean isAllEmpty(Object... os) {
        for (Object o : os) {
            if (!isEmpty(o)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 根据消息键和参数 获取消息 委托给Spring messageSource
     * 
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args){
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        try{
			return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
		}catch (Exception e){
        	return code;
		}
    }
    
    /**
	 * 
	 * 字节计算转换
	 * 
	 * <pre>
	 * StrUtil.convertFileSize(1024)   			= 1kB
	 * </pre>
	 * @author zhouzhou loveingowp@163.com
	 * @time 2019-04-03 12:29
	 * @param size 字节大小
	 * @return 转换后大小字符串
	 *
	 */
	public static String convertFileSize(long size) {
		long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb)
        {
            return StrUtil.format("{} GB", NumberUtil.round((float) size / gb,2));
        }
        else if (size >= mb)
        {
            float f = NumberUtil.round((float) size / mb,2).floatValue();
            return StrUtil.format(f > 100 ? "{} MB" : "{} MB", f);
        }
        else if (size >= kb)
        {
            float f = NumberUtil.round((float) size / kb,2).floatValue();
            return StrUtil.format(f > 100 ? "{}  KB" : "{}  KB", f);
        }
        else
        {
            return StrUtil.format("{} B", size);
        }
	}
	
	public static String getMessage(Exception e){
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
		} finally {
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
		return sw.toString();
	}

	public static boolean isBoolIp(String ipAddress) {
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}


	public static String createFolder(String folder){
		folder += File.separator + DateUtil.format(new Date(),"yyyy") + File.separator + DateUtil.format(new Date(),"MM") +
				File.separator + DateUtil.format(new Date(),"dd")+ File.separator;
		FileUtil.mkdir(folder);
		return folder;
	}


	/**
	 * 编码文件名
	 */
	public static String encodingFilename(String fileName) {
		fileName = fileName.replace("_", " ");
		fileName = Md5Util.hash(fileName + System.nanoTime() + counter++);
		return fileName;
	}

	public static String encodingExcelFilename(String filename){
		filename = UUID.randomUUID().toString() + "_" + filename + ".xlsx";
		return filename;
	}

    /**
     * 配合JU在线报表模板、发送邮件
     * @param tos 收件人列表
     * @param ccs 抄送人列表，可以为null或空
     * @param bccs 密送人列表，可以为null或空
     * @param subject 标题
     * @param fileNo 报表名 (注意不含前缀fast- 、 不含后缀.xml)
     * @param parameters 报表接收参数
     * @param files 附件列表 可以为 null
     */
	public static void sendMailJu(Collection<String> tos,
                                  Collection<String> ccs,
                                  Collection<String> bccs,
                                  String subject,
                                  String fileNo,
                                  Map<String,Object> parameters,
                                  File... files){
	    try{
	        log.info("开始发送准备>>>>>>>>>");
            parameters.put(ConfigConstant.SQLFILTER, "");
            HtmlReport htmlReport = ((ExportManager)SpringUtil.getBean("fastExportManager")).exportHtml("fast-"+fileNo+".xml","",parameters);
            log.info("成功获取报表转换>>>>>>>>>");
            FreeMarkerConfigurer freeMarkerConfigurer = SpringUtil.getBean(FreeMarkerConfigurer.class);
            //生成静态文件
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            // 加载模板对象
            Template template = configuration.getTemplate("common/template/mail.html");
            // 创建一个数据集
            Map<String, Object>  data = new HashMap<>();
            data.put("style",htmlReport.getStyle());
            data.put("table",htmlReport.getContent());
            File file = FileUtil.touch(Global.getRootPath() + "mail" + File.separator +
                    DatePattern.PURE_DATE_FORMAT.format(new Date())+"_"+fileNo + ".html" );
            // 指定文件输出路径以及文件名
            Writer out = new FileWriter(file);
            // 输出文件
            template.process(data, out);
            // 关闭流
            out.close();
            MailUtil.send(tos,ccs,bccs,  subject,FileUtil.readUtf8String(file),true,files);
            log.info("开始发送成功>>>>>>>>>");
        }catch (Exception e){
	        log.error("发送异常:",e);
        }
    }

	/**
	 * 通过Hutool工具类获取系统硬件信息
	 * @throws Exception
	 */
	public static void  getFastServerInfos(){
        if(ToolUtil.isEmpty(ConfigConstant.FAST_OS_SN)){
            //NetworkIF[] netwoeks = OshiUtil.getHardware().getNetworkIFs();
            List<NetworkIF> netwoeks = OshiUtil.getHardware().getNetworkIFs();
			String macAddress = "";
			List<String> IpList = new ArrayList<>();
			for(NetworkIF net: netwoeks){
				macAddress+=net.getMacaddr();
				String temp = StrUtil.join(",",net.getIPv4addr());
				if(ToolUtil.isNotEmpty(temp)){
					IpList.add(temp);
				}
			}
			//序列号
			String serialNumber = OshiUtil.getSystem().getSerialNumber();
			//处理器ID
			String processorID = OshiUtil.getProcessor().getProcessorIdentifier().getProcessorID();
			//组装 系统机器码 mac串+序列号+处理器ID+程序系统路径+系统名称+主机名+系统架构+环境版本号  -->机器码  可以自行增加硬件信息确保唯一性
			String temp = macAddress + serialNumber + processorID
					+ SystemUtil.getUserInfo().getCurrentDir() + SystemUtil.getOsInfo().getName() + SystemUtil.getHostInfo().getName() +
					SystemUtil.getOsInfo().getArch() + SystemUtil.getJavaInfo().getVersion();
			//再将机器码加密成16位字符串
			ConfigConstant.FAST_OS_SN = EnctryptTools.SM4Mac(ConfigConstant.FAST_MAC_KEY,temp.getBytes());
			ConfigConstant.KEY = EnctryptTools.SM4Mac(HexUtil.decodeHex(ConfigConstant.KEY),HexUtil.decodeHex(ConfigConstant.FAST_OS_SN));
			ConfigConstant.FAST_IPS = IpList;
        }
    }

    /**
     * 获取工程源文件所在路径
     * @return
     */
    public static String getProjectPath(){
        String projectPath = "";
        try {
            File file = getResource("").getFile();
            if (file != null){
                while(true){
                    File f = new File(FileUtil.normalize(file.getPath() + "/src/main"));
                    if (f.exists()){
                        break;
                    }
                    f = new File(FileUtil.normalize(file.getPath() + "/target/classes"));
                    if (f.exists()){
                        break;
                    }
                    if (file.getParentFile() != null){
                        file = file.getParentFile();
                    }else{
                        break;
                    }
                }
                projectPath = file.toString();
            }
        } catch (FileNotFoundException e) {
            // 忽略异常
        } catch (IOException e) {
            // 忽略异常
        }
        // 取不到，取当前工作路径
        if (StrUtil.isBlank(projectPath)){
            projectPath = System.getProperty("user.dir");
        }
        return projectPath;
    }


    /**
     * 结果值转换
     * @param resultSet 执行返回结果值
     * @return Map 返回信息
     */
    public static Map<String, Object> resultSetToMap(ResultSet resultSet) {
        try {
            HashMap<String, Object> result = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                result.put(columnName, columnValue);
            }
            return result;
        } catch (SQLException e) {
            log.error("转化结果集异常", e);
            return new HashMap<>(1);
        }
    }

    /**
     * 判断字符是否是Base64
     * @param str
     * @return
     */
    public static boolean isBase64(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        } else {
            if (str.length() % 4 != 0) {
                return false;
            }
            char[] strChars = str.toCharArray();
            for (char c:strChars) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                        || c == '+' || c == '/' || c == '=') {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }
    }
    /**
     * 获取工程源文件所在路径
     * @return
     */
    public static String getWebappPath(){
        String webappPath = "";
        try {
            File file = getResource("").getFile();
            if (file != null){
                while(true){
                    File f = new File(FileUtil.normalize(file.getPath() + "/WEB-INF/classes"));
                    if (f.exists()){
                        break;
                    }
                    f = new File(FileUtil.normalize(file.getPath() + "/src/main/webapp"));
                    if (f.exists()){
                        return f.getPath();
                    }
                    if (file.getParentFile() != null){
                        file = file.getParentFile();
                    }else{
                        break;
                    }
                }
                webappPath = file.toString();
            }
        } catch (FileNotFoundException e) {
            // 忽略异常
        } catch (IOException e) {
            // 忽略异常
        }
        // 取不到，取当前工作路径
        if (StrUtil.isBlank(webappPath)){
            webappPath = System.getProperty("user.dir");
        }
        return webappPath;
    }

	/**
	 * 数组以某种分隔符拼装
	 * @param value Long数值
	 * @param s 分隔符
	 * @return 拼装之后的字符串
	 */
    public static String conversion(Object value, String s){
    	String src = "";
    	if(value instanceof  Long[]){
    	    Long[] a = (Long[]) value;
            for(Long l: a){
                src += l+s;
            }
        }
    	if(value instanceof List){
            @SuppressWarnings({ "unchecked", "rawtypes" })
			List<String> a = (List) value;
            for(int i=0; i< a.size(); i++){
                src += a.get(i)+s;
            }
        }
		return  src.substring(0,src.length()-s.length());
	}

	/**
	 * 判断ResponseData 是否成功
	 * @param responseData 返回页面数据
	 * @return true 成功
	 */
	public static boolean isSuccess(ResponseData responseData){
    	if(responseData.get("code").equals(ResponseData.DEFAULT_SUCCESS_CODE)){
			return true;
		}else{
    		return false;
		}
	}

	@SuppressWarnings("deprecation")
	public static AuthRequest getAuthRequest(String source,String clientId,String clientSecret,String redirectUri, AuthStateCache authStateCache) {
        AuthRequest authRequest = null;
        switch (source.toLowerCase()) {
            case "dingtalk":
                authRequest = new AuthDingTalkRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "baidu":
                authRequest = new AuthBaiduRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "github":
                authRequest = new AuthGithubRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(), authStateCache);
                break;
            case "gitee":
                authRequest = new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(), authStateCache);
                break;
            case "weibo":
                authRequest = new AuthWeiboRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "coding":
                authRequest = new AuthCodingRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .codingGroupName("")
                        .build(),authStateCache);
                break;
            case "oschina":
                authRequest = new AuthOschinaRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "alipay":
                // 支付宝在创建回调地址时，不允许使用localhost或者127.0.0.1，所以这儿的回调地址使用的局域网内的ip
                authRequest = new AuthAlipayRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .alipayPublicKey("")
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "qq":
                authRequest = new AuthQqRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "wechat_open":
                authRequest = new AuthWeChatOpenRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "csdn":
                authRequest = new AuthCsdnRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "taobao":
                authRequest = new AuthTaobaoRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
//            case "google":
//                authRequest = new AuthGoogleRequest(AuthConfig.builder()
//                        .clientId(clientId)
//                        .clientSecret(clientSecret)
//                        .redirectUri(redirectUri)
//                        // 针对国外平台配置代理
//                        .httpConfig(HttpConfig.builder()
//                                .timeout(15000)
//                                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10080)))
//                                .build())
//                        .build(),authStateCache);
//                break;
//            case "facebook":
//                authRequest = new AuthFacebookRequest(AuthConfig.builder()
//                        .clientId(clientId)
//                        .clientSecret(clientSecret)
//                        .redirectUri(redirectUri)
//                        // 针对国外平台配置代理
//                        .httpConfig(HttpConfig.builder()
//                                .timeout(15000)
//                                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10080)))
//                                .build())
//                        .build(),authStateCache);
//                break;
            case "douyin":
                authRequest = new AuthDouyinRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "linkedin":
                authRequest = new AuthLinkedinRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "microsoft":
                authRequest = new AuthMicrosoftRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "mi":
                authRequest = new AuthMiRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "toutiao":
                authRequest = new AuthToutiaoRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "teambition":
                authRequest = new AuthTeambitionRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "pinterest":
                authRequest = new AuthPinterestRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "renren":
                authRequest = new AuthRenrenRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "stack_overflow":
                authRequest = new AuthStackOverflowRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .stackOverflowKey("")
                        .build(),authStateCache);
                break;
            case "huawei":
                authRequest = new AuthHuaweiRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "wechat_enterprise":
                authRequest = new AuthWeChatEnterpriseRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .agentId("")
                        .build(),authStateCache);
                break;
            case "kujiale":
                authRequest = new AuthKujialeRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "gitlab":
                authRequest = new AuthGitlabRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "meituan":
                authRequest = new AuthMeituanRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "eleme":
                authRequest = new AuthElemeRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build());
                break;
//            case "twitter":
//                authRequest = new AuthTwitterRequest(AuthConfig.builder()
//                        .clientId(clientId)
//                        .clientSecret(clientSecret)
//                        .redirectUri(redirectUri)
//                        // 针对国外平台配置代理
//                        .httpConfig(HttpConfig.builder()
//                                .timeout(15000)
//                                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10080)))
//                                .build())
//                        .build(),authStateCache);
//                break;
            case "wechat_mp":
                authRequest = new AuthWeChatMpRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            case "aliyun":
                authRequest = new AuthAliyunRequest(AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build(),authStateCache);
                break;
            default:
                break;
        }
        if (null == authRequest) {
            throw new AuthException("未获取到有效的Auth配置");
        }
        return authRequest;
    }


    /**
     * 下载文件
     * @param request
     * @param response
     * @param fileName
     * @param in
     */
    public static void fileDownload(HttpServletRequest request,
                             HttpServletResponse response,
                             String fileName,
                             InputStream in) throws Exception {
        //浏览器设置
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            //IE浏览器处理
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } else {
            // 非IE浏览器的处理：
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        //下载的文件携带这个名称
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        //文件下载类型--二进制文件
        response.setContentType("application/octet-stream");
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream(in.available());
        int len = 0;
        while (-1 != (len = in.read(buffer, 0, buffer.length))) {
            bos.write(buffer,0,len);
        }
        log.info("==============================下载包长度:!" + bos.size() +"   ========================");
        response.setHeader("Content-Length",bos.size()+ "");
        in.close();
        ServletOutputStream sos = response.getOutputStream();
        sos.write(bos.toByteArray());
        sos.flush();
        sos.close();
        log.info("==============================下载完成![" + fileName +"]   ========================");
        return;
    }

    /**
     * 文件视图下载
     * @param response
     * @param fileName
     * @param in
     * @throws IOException
     */
    public static void fileView(HttpServletResponse response,
                                String fileName,
                                InputStream in) throws IOException {

        response.setContentType(HttpUtil.getMimeType(fileName));
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream(in.available());
        int len = 0;
        while (-1 != (len = in.read(buffer, 0, buffer.length))) {
            bos.write(buffer,0,len);
        }
        log.info("==============================下载包长度:!" + bos.size() +"   ========================");
        response.setHeader("Content-Length",bos.size()+ "");
        in.close();
        ServletOutputStream sos = response.getOutputStream();
        try{
            sos.write(bos.toByteArray());
            sos.flush();
            sos.close();
        }catch (Exception e){}
        log.info("==============================下载完成![" + fileName +"]   ========================");
    }


    /**
     * 随机生成秘钥，参考org.apache.shiro.crypto.AbstractSymmetricCipherService#generateNewKey(int)
     * @return
     */
    public static byte[] generateNewKey() {
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException var5) {
            String msg = "Unable to acquire AES algorithm.  This is required to function.";
            throw new IllegalStateException(msg, var5);
        }
        kg.init(128);
        SecretKey key = kg.generateKey();
        byte[] encoded = key.getEncoded();
        return encoded;
    }

    public static String getLocalBpmDbPath(String dbName,String fileName) {
        String realPath = ToolUtil.class.getClassLoader().getResource("")
                .getFile();
        java.io.File file = new java.io.File(realPath);
        realPath = file.getAbsolutePath();
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        realPath = realPath.substring(0,realPath.indexOf("fast-admin")) + "db"+File.separator+dbName+File.separator+"bpm"+File.separator+fileName+".sql";
        realPath = FileUtil.normalize(realPath);
        return realPath;
    }

    public static String getLocalCoreDbPath(String dbName,String fileName) {
        String realPath = ToolUtil.class.getClassLoader().getResource("")
                .getFile();
        java.io.File file = new java.io.File(realPath);
        realPath = file.getAbsolutePath();
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        realPath = realPath.substring(0,realPath.indexOf("fast-admin")) + "db"+File.separator+dbName+File.separator+fileName+".sql";
        realPath = FileUtil.normalize(realPath);
        return realPath;
    }

//    public static void main(String[] args) {
//        Ftp ftp = new Ftp("192.168.20.110",21,"ftp","zhouhuan");
//        ftp.download("attach/file/2021/02/09/","ca296385a464b91bdbff567015519efa.jpg",FileUtil.touch(""));
//    }
}
