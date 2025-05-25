package model

class Version {
  int major
  int minor
  int patch
  
  Version(int major, int minor, int patch) {
    this.major = major
    this.minor = minor
    this.patch = patch
  }
  
  String toString() {
    "${major}.${minor}.${patch}"
  }
}
