package enums

enum MergeStatus {
  
  OPENED("opened"),
  MERGED("merged"),
  CLOSED("closed");
  
  private String prefix
  
  MergeStatus(String prefix) {
    this.prefix = prefix
  }
  
  String getPrefix() {
    return prefix
  }
  
  static boolean isMergeStatusValid(String prefixToCheck) {
    values().any {MergeStatus name -> name.getPrefix().equalsIgnoreCase(prefixToCheck)
    }
  }
}
