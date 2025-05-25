package enums

enum BranchType {
  FEATURE("feature"),
  BUGFIX("bugfix"),
  HOTFIX("hotfix"),
  MAIN("main"),
  
  private String prefix
  
  BranchType(String prefix) {
    this.prefix = prefix
  }
  
  String getPrefix() {
    return prefix
  }
  
  static boolean isBranchTypeValid(String prefixToCheck) {
    values().any {BranchType name -> name.getPrefix().startsWith(prefixToCheck)
    }
  }
}

