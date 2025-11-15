import { get, post, put, del } from '@/utils/request';

/**
 * 菜单类型定义
 */
export interface Menu {
  menuId?: number;
  parentId?: number;
  menuName: string;
  menuType: string;  // M=目录 C=菜单 F=按钮
  path?: string;
  component?: string;
  perms?: string;
  icon?: string;
  orderNum?: number;
  visible?: number;  // 0=隐藏 1=显示
  status?: number;   // 0=禁用 1=正常
  createTime?: string;
  updateTime?: string;
  remark?: string;
  children?: Menu[];
}

/**
 * 菜单查询参数
 */
export interface MenuQuery {
  menuName?: string;
  menuType?: string;
  status?: number;
  parentId?: number;
}

/**
 * 查询菜单列表
 */
export const getMenuList = (params: MenuQuery) => {
  return get('/system/menu/list', params);
};

/**
 * 查询菜单树
 */
export const getMenuTree = (params: MenuQuery) => {
  return get('/system/menu/tree', params);
};

/**
 * 获取菜单详情
 */
export const getMenu = (menuId: number) => {
  return get(`/system/menu/${menuId}`);
};

/**
 * 新增菜单
 */
export const addMenu = (data: Menu) => {
  return post('/system/menu', data);
};

/**
 * 修改菜单
 */
export const updateMenu = (data: Menu) => {
  return put('/system/menu', data);
};

/**
 * 删除菜单
 */
export const deleteMenu = (menuId: number) => {
  return del(`/system/menu/${menuId}`);
};
