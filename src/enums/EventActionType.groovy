package enums

enum EventActionType {
  PUSH("PUSH"),
  MERGE("MERGE"),
  TAG_PUSH("TAG_PUSH");
  
  private String prefix
  
  EventActionType(String prefix) {
    this.prefix = prefix
  }
  
  String getPrefix() {
    return prefix
  }
}

