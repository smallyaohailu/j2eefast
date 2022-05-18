package com.j2eefast.framework.sys.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.config.LicenseCheckListener;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.crypto.SM4;
import com.j2eefast.common.core.crypto.SoftEncryption;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.license.LicenseVerify;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.annotation.RepeatSubmit;
import com.j2eefast.framework.sys.entity.SysUserEntity;
import com.j2eefast.framework.sys.mapper.SysUserMapper;
import com.j2eefast.framework.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

@Slf4j
@Controller
@RequestMapping("/authInsu")
public class SysAuthLicenseController extends BaseController {

    private String                  urlPrefix                   = "modules/sys/license";

    /**
     * 证书生成路径
     */
    @Value("#{ @environment['license.licensePath'] ?: 'license.lic' }")
    private String licensePath;

    @Autowired
    private LicenseCheckListener listener;
    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 产品名称
     */
    @Value("${productName: FastOs Demo}")
    private String productName;

    @GetMapping()
    public String authorization(ModelMap mmap) {
        mmap.put("fastSN", HexUtil.encodeHexStr(SM4.encryptData_ECB(HexUtil.decodeHex
                (ConfigConstant.FAST_OS_SN),ConfigConstant.FAST_KEY)));
        mmap.put("productName",productName);
        mmap.put("authorizationTime",ConfigConstant.AUTHORIZATION_TIME);
        LicenseVerify licenseVerify = new LicenseVerify();
        int online = licenseVerify.onlineNumVerify(listener.getVerifyParam());
        mmap.put("onlineNum",online == -1 ? "无限制" : online);
        return urlPrefix + "/authorization";
    }


    /**
     * 上传许可文件
     */
    @BussinessLog(title = "上传许可文件", businessType = BusinessType.UPDATE)
    @PostMapping("/uploadLic")
    @RepeatSubmit
    @ResponseBody
    public ResponseData uploadLic(@RequestParam("licfile") MultipartFile file,
                                  @RequestParam("name") String name,
                                  @RequestParam("password") String password,
                                  @RequestParam("kg4") String kg4,
                                  @RequestParam("sign") String sign){
        if(ToolUtil.isEmpty(name) || ToolUtil.isEmpty(password)) {
            throw new RxcException("账号密码不能为空!", "50001");
        }

        if(ToolUtil.isEmpty(kg4) || ToolUtil.isEmpty(sign)){
            throw new RxcException("登录确实必要参数kg4/sing","50002");
        }

        //校验签名
        String _sign = SoftEncryption.genSM3Keys((kg4 + name + password).getBytes()).getStr("b64") ;
        if(!_sign.equals(sign)){
            //校验签名失败
            throw new RxcException("加密校验失败","50001");
        }
        //获得明文kg4
        kg4 = SoftEncryption.decryptBySM2(Base64.decode(kg4),
                ConfigConstant.PRIVKEY).getStr("hex");

        //前端账号密码解密
        name =new String(SoftEncryption.decryptBySM4(Base64.decode(name),
                cn.hutool.core.util.HexUtil.decodeHex(kg4)).get("bytes",byte[].class)).trim();
        password =new String(SoftEncryption.decryptBySM4(Base64.decode(password),
                cn.hutool.core.util.HexUtil.decodeHex(kg4)).get("bytes",byte[].class)).trim();

        SysUserEntity user = this.sysUserMapper.findUserByUserName(name,
                StrUtil.EMPTY);

        if(ToolUtil.isEmpty(user)){
            throw new RxcException(ToolUtil.message("sys.login.failure"),"50001");
        }

        //判断密码是否正确
        if(!UserUtils.sha256(password, user.getSalt()).equals(user.getPassword())) {
            throw new RxcException(ToolUtil.message("sys.login.failure"),"50001");
        }
        try{
            if (!file.isEmpty() && "license.lic".equals(file.getOriginalFilename())){
                String pathName = FileUploadUtil.uploadFile(Global.getTempPath(), file);
                //先拷贝之前的备份防止出故障 重命名
                FileUtil.rename(FileUtil.file(licensePath),"licenseReName",true,true);
                FileUtil.move(FileUtil.file(pathName),FileUtil.file(licensePath),true);
                //验证证书
                LicenseVerify licenseVerify = new LicenseVerify();
                //校验证书是否有效
                licenseVerify.install(listener.getVerifyParam());

                String path = FileUtil.getAbsolutePath(licensePath);
                FileUtil.del(path.substring(0,FileUtil.lastIndexOfSeparator(FileUtil.getAbsolutePath(path)))   +
                        File.separator+ "licenseReName.lic");

                return success();
            }

            return error(ToolUtil.message("sys.file.null"));
        } catch (Exception e) {
            String path = FileUtil.getAbsolutePath(licensePath);
            if(FileUtil.exist(path.substring(0,FileUtil.lastIndexOfSeparator(FileUtil.getAbsolutePath(path)))   +
                    File.separator+ "licenseReName.lic")){
                FileUtil.move(FileUtil.file(path.substring(0,FileUtil.lastIndexOfSeparator(FileUtil.getAbsolutePath(path)))   +
                        File.separator+ "licenseReName.lic"),FileUtil.file(licensePath),true);
            }
            try{
                //再装载老许可证书 -- 否则影响系统 也可以不处理 默认违规操作 必须重启服务器
                //验证证书
                LicenseVerify licenseVerify = new LicenseVerify();
                //校验证书是否有效
                licenseVerify.install(listener.getVerifyParam());
            }catch (Exception ex){
            }
            return error("许可证书无效,请确认无误在上传!");
        }
    }

}
