package enums

import utils.Constant


enum VersionType {
  MAJOR("major"),
  MINOR("minor"),
  PATCH("patch");
  
  private String prefix
  
  VersionType(String prefix) {
    this.prefix = prefix
  }
  
  String getPrefix() {
    return prefix
  }
  
  static boolean isVersionTypeValid(String prefixToCheck) {
    values().any {VersionType name -> name.getPrefix().equalsIgnoreCase(prefixToCheck)
    }
  }
  
  static VersionType getVersionType(String mergeRequestTitle) {
    if (MAJOR.getPrefix().equalsIgnoreCase(mergeRequestTitle)) {
      return MAJOR
    } else if (MINOR.getPrefix().equalsIgnoreCase(mergeRequestTitle)) {
      return MINOR
    } else if (PATCH.getPrefix().equalsIgnoreCase(mergeRequestTitle)) {
      return PATCH
    } else {
      throw new IllegalArgumentException(Constant.errorInvalidMergeRequestTitle)
    }
  }
}
