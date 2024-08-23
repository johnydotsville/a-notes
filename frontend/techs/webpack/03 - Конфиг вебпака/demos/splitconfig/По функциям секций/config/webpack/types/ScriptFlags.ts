export interface ScriptFlags {
  mode: BuildMode;  // TODO: а если не задан? Могу я это отразить как-то тут?
}

export type BuildMode = "development" | "production";