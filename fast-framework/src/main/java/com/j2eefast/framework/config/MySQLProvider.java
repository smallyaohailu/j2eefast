package com.j2eefast.framework.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.RedisUtil;
import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.bussiness.aop.DataFilterAspect;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.sys.entity.SysUreportFileEntity;
import com.j2eefast.framework.sys.service.SysRoleService;
import com.j2eefast.framework.sys.service.SysUreportFileService;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.ServerUtil;
import com.j2eefast.framework.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定制化设置权限
 */
@Component
@ConfigurationProperties(prefix = "fast.ureport.mysql.provider")
public class MySQLProvider implements ReportProvider {

    private static final String NAME = "Msyql数据库存储";

    // 特定前缀
    private String prefix = "fast-";

    // Redis 缓存
    private String redisPrefix = "fast:ureport:";

    // 是否禁用
    private boolean enabled;


    @Autowired
    private SysUreportFileService sysUreportFileService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 根据文件名称 获取默认下载文件
     * @param _u
     * @return
     */
    public  String getFileName(String _u){
        String name = StrUtil.replace(_u,prefix,"");
        SysUreportFileEntity sysUreportFileEntity = sysUreportFileService.getOne(new
                QueryWrapper<SysUreportFileEntity>().eq("name",name).select("file_name"));
        return StrUtil.blankToDefault(sysUreportFileEntity.getFileName(),name);
    }

    /**
     * 获取报表数据过滤SQL
     * @param _u
     * @return
     */
    public String getSQLFilter(String _u){
        LoginUserEntity user = UserUtils.getUserInfo();

        // 如果不是超级管理员，则进行数据过滤
        if (!user.getId().equals(Constant.SUPER_ADMIN)) {
            String name = StrUtil.replace(_u,prefix,"");
            SysUreportFileEntity sysUreportFileEntity = sysUreportFileService.getOne(new
                    QueryWrapper<SysUreportFileEntity>().eq("name",name).select("table_alias"));
            String tableAlias = sysUreportFileEntity.getTableAlias();
            if(ToolUtil.isEmpty(tableAlias)){
                return "1 = 0";
            }
            List<String> tas = StrUtil.split(tableAlias,StrUtil.C_COMMA);
            // 获取表的别名
            String compAlias = tas.get(0);
            String deptAlias = tas.get(1);
            String userAlias = tas.get(2);
            return  DataFilterAspect.getSQLFilter(user,compAlias,deptAlias,userAlias,null, false);
        }else{
            return StrUtil.EMPTY;
        }
    }

    public List<SysRoleEntity> getSysRoles(){
        return sysRoleService.getRolesAll();
    }

    /**
     * 判断是否有权限
     * @return true 有 false 无
     */
    public boolean ishasRole(String _u){
        //超级管理员直接跳过检验
        if(UserUtils.isSupAdmin() ||
                UserUtils.getUserId().equals(1L)){
            return true;
        }

        String name = StrUtil.replace(_u,prefix,"");

        String roles = redisUtil.get(redisPrefix + "role:"+name);

        if(ToolUtil.isEmpty(roles)){
            SysUreportFileEntity sysUreportFileEntity = sysUreportFileService.getOne(new
                    QueryWrapper<SysUreportFileEntity>().eq("name",name).select("role_keys"));
            roles = sysUreportFileEntity.getRoleKeys();
        }

        //禁用
        if(ToolUtil.isEmpty(roles) ||
                roles.equals("_DISABLE")){
            return false;
        }

        //全部放开
        if(roles.equals("_ALL")){
            return true;
        }

        //根据权限放开
        List<String> rolesList =  StrUtil.split(roles,",");
        String[] rks = rolesList.toArray(new String[rolesList.size()]);
        return UserUtils.hasAnyRoleKeys(rks);
    }

    /**
     * 判断开放功能
     * @param _u
     * @param func
     * @return
     */
    public boolean isFuncs(String _u,String func){
        //超级管理员直接跳过检验
        if(UserUtils.isSupAdmin() ||
                UserUtils.getUserId().equals(1L)){
            return true;
        }
        String name = StrUtil.replace(_u,prefix,"");

        String funcs = redisUtil.get(redisPrefix + "func:"+name);
        if(ToolUtil.isEmpty(funcs)){
            SysUreportFileEntity sysUreportFileEntity = sysUreportFileService.getOne(new
                    QueryWrapper<SysUreportFileEntity>().eq("name",name).select("func"));
            //判断是否开放功能
            if(ToolUtil.isEmpty(sysUreportFileEntity.getFunc())){
                return false;
            }
            funcs = sysUreportFileEntity.getFunc();
            redisUtil.set(redisPrefix + "func:"+name,funcs);
        }
        if(funcs.equals("_ALL")){
            return true;
        }
        //根据权限放开
        List<String> funcLsit =  StrUtil.split(funcs,",");
        return funcLsit.contains(func);
    }

    /**
     * 加载
     * @param file
     * @return
     */
    @Override
    public InputStream loadReport(String file) {
        SysUreportFileEntity ureportFile = sysUreportFileService
                .getUreportFileByFileName(getCorrectName(file));
        byte[] content = ureportFile.getContent();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        return inputStream;
    }

    /**
     * 删除
     * @param file
     */
    @Override
    public void deleteReport(String file) {
        redisUtil.del(redisPrefix + "func:"+getCorrectName(file));
        redisUtil.del(redisPrefix + "role:"+getCorrectName(file));
        sysUreportFileService.delUreportFileByFileName(getCorrectName(file));
    }

    /**
     * 获取列表
     * @return
     */
    @Override
    public List<ReportFile> getReportFiles() {
        List<SysUreportFileEntity> list = sysUreportFileService.list();
        List<ReportFile> reportList = new ArrayList<>();
        for (SysUreportFileEntity ureportFileEntity : list) {
            reportList.add(new ReportFile(ureportFileEntity.getName(), ureportFileEntity.getUpdateTime()));
        }
        return reportList;
    }

    /**
     * 保存
     * @param file
     * @param content
     */
    @Override
    public void saveReport(String file, String content) {
    	System.out.println("content:" + content);
        String status = ServletUtil.getParameter("_status");
        String _downFileName = ServletUtil.getParameter("_downFileName");
        String _roleKeys = ServletUtil.getParameter("_roleKeys");
        String func = ServletUtil.getParameter("_func");
        file = getCorrectName(file);
        SysUreportFileEntity ureportFileEntity = sysUreportFileService
                .getUreportFileByFileName(file);
        if(ureportFileEntity == null && status.equals("save")){
            ureportFileEntity = new SysUreportFileEntity();
            ureportFileEntity.setName(file);
            ureportFileEntity.setFileName(_downFileName);
            ureportFileEntity.setRoleKeys(_roleKeys);
            ureportFileEntity.setFunc(func);
            ureportFileEntity.setContent(content.getBytes());
            ureportFileEntity.setUpdateTime(new Date());
            redisUtil.set(redisPrefix + "func:"+file,func);
            redisUtil.set(redisPrefix + "role:"+file,_roleKeys);
        }else if(ToolUtil.isNotEmpty(ureportFileEntity) && status.equals("update")){
            ureportFileEntity.setContent(content.getBytes());
        }else{
            throw new RxcException("报表名称已经存在请确保唯一性!","A00002");
        }
        sysUreportFileService.saveOrUpdate(ureportFileEntity);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean disabled() {
        return enabled;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * 获取没有前缀的文件名
     * @param name
     * @return
     */
    private String getCorrectName(String name){
        if(name.startsWith(prefix)){
            name = name.substring(prefix.length(), name.length());
        }
        return name;
    }
}
