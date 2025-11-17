package com.enterprisex.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.auth.domain.RouterVo;
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

    /**
     * 根据用户ID查询菜单列表（用于构建路由）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Select("SELECT DISTINCT m.menu_id, m.parent_id, m.menu_name, m.path, m.component, " +
            "m.menu_type, m.perms, m.icon, m.order_num, m.visible, m.status " +
            "FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 AND m.menu_type IN ('M', 'C') " +
            "ORDER BY m.order_num ASC")
    List<RouterVo> selectMenusByUserId(Long userId);

    /**
     * 查询所有菜单列表（用于管理员）
     *
     * @return 菜单列表
     */
    @Select("SELECT menu_id, parent_id, menu_name, path, component, menu_type, perms, icon, " +
            "order_num, visible, status " +
            "FROM sys_menu " +
            "WHERE status = 1 AND menu_type IN ('M', 'C') " +
            "ORDER BY order_num ASC")
    List<RouterVo> selectAllMenus();
}
