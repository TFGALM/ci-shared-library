package service

import enums.VersionType
import utils.Constant
import model.Version


class VersionService implements Serializable {
  
  
  VersionService() {}
  
  Version getNextVersion(Version version, String mergeType) {
    VersionType versionType = VersionType.getVersionType(mergeType)
    switch (versionType) {
      case VersionType.MAJOR:
        return new Version(version.major + 1, 0, 0)
      case VersionType.MINOR:
        return new Version(version.major, version.minor + 1, 0)
      case VersionType.PATCH:
        return new Version(version.major, version.minor, version.patch + 1)
      default:
        throw new IllegalArgumentException(Constant.errorVersionNotSupported)
    }
  }
  
  Version getVersionFromString(String version) {
    def versionMatcher = version=~/(\d+)\.(\d+)\.(\d+)/
    if (versionMatcher) {
      int major = versionMatcher.group(1) as int
      int minor = versionMatcher.group(2) as int
      int patch = versionMatcher.group(3) as int
      return new Version(major, minor, patch)
    } else {
      throw new IllegalStateException(Constant.errorWrongVersionTag)
    }
  }
}
