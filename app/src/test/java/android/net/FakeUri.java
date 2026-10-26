package android.net;

import java.util.Collections;
import java.util.List;

/** A Uri for JVM tests: android.net.Uri cannot be created there, and its constructor is only visible inside android.net. */
public class FakeUri extends Uri {
    @Override public Builder buildUpon() { return null; }
    @Override public String getAuthority() { return null; }
    @Override public String getEncodedAuthority() { return null; }
    @Override public String getEncodedFragment() { return null; }
    @Override public String getEncodedPath() { return null; }
    @Override public String getEncodedQuery() { return null; }
    @Override public String getEncodedSchemeSpecificPart() { return null; }
    @Override public String getEncodedUserInfo() { return null; }
    @Override public String getFragment() { return null; }
    @Override public String getHost() { return null; }
    @Override public String getLastPathSegment() { return null; }
    @Override public String getPath() { return null; }
    @Override public List<String> getPathSegments() { return Collections.emptyList(); }
    @Override public int getPort() { return -1; }
    @Override public String getQuery() { return null; }
    @Override public String getScheme() { return null; }
    @Override public String getSchemeSpecificPart() { return null; }
    @Override public String getUserInfo() { return null; }
    @Override public boolean isHierarchical() { return false; }
    @Override public boolean isRelative() { return false; }
    @Override public void writeToParcel(android.os.Parcel dest, int flags) { }
    @Override public int describeContents() { return 0; }
    @Override public String toString() { return "fake://uri"; }
}
