package com.enterprisex.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.auth.domain.UserAuthInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户认证信息 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuthInfo> {

    /**
     * 根据用户ID查询角色标识列表
     *
     * @param userId 用户ID
     * @return 角色标识列表
     */
    @Select("SELECT r.role_key FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.del_flag = 0")
    List<String> selectRoleKeysByUserId(Long userId);

    /**
     * 根据用户ID查询权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    @Select("SELECT DISTINCT m.perms FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 AND m.perms IS NOT NULL AND m.perms != ''")
    List<String> selectPermissionsByUserId(Long userId);
}
