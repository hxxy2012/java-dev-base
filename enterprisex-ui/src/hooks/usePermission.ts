import { useAuthStore } from '@/store/authStore';

/**
 * 权限Hook
 */
export const usePermission = () => {
  const { hasPermission, hasAnyPermission, hasAllPermissions } = useAuthStore();

  return {
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
  };
};

export default usePermission;
