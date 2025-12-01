// src/env.d.ts
declare const process: {
  env: {
    [key: string]: string | undefined;
  };
};