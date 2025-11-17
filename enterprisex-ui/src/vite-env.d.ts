/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_APP_TITLE: string;
  readonly VITE_APP_VERSION: string;
  readonly VITE_USE_MOCK: string;
  readonly VITE_USE_CDN: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
