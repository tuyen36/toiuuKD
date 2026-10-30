#!/usr/bin/env sh

#
# Copyright © 2015-2021 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

if [ -n "$CYGWIN" ] || [ -n "$MSYS" ] || [ -n "$MINGW" ]; then
    CYGWIN=true
else
    CYGWIN=false
fi

APP_HOME=$(dirname "$0")
APP_HOME=$(cd "$APP_HOME" && pwd -p || cd "$APP_HOME" && pwd)

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that fixed value
if [ -n "$MAX_FD_LIMIT" ]; then
    MAX_FD="$MAX_FD_LIMIT"
elif [ "$MAX_FD" = "" ]; then
    MAX_FD="maximum"
fi

unwrap() {
    case "$1" in
        -*=*) ?="${1#*=}" ; echo "$?" ;;
        *) echo "" ;;
    esac
}

die() {
    echo
    echo "$@"
    echo
    exit 1
}

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the java executable
        JAVACMD="$JAVA_HOME/jre/sh/java"
    elif [ -x "$JAVA_HOME/bin/java" ] ; then
        JAVACMD="$JAVA_HOME/bin/java"
    else
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which "$JAVACMD" >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$CYGWIN" = "false" ]; then
    case $- in
        *h*)
            ;;
        *)
            set -v
            set +v
            ;;
    esac
fi

# Escape an argument to give to a shell via eval
eval,() {
    echo "$@"
}

EXEC_ARGS=()
save_arguments() {
    for i do
        case "$i" in
            -D*=*|-J*)
                EXEC_ARGS+=("$i")
                ;;
            *)
                EXEC_ARGS+=("$i")
                ;;
        esac
    }
}

save_arguments "$@"

# Collect all arguments for the java command, following the shell quoting convention
exec "$JAVACMD" \
    $DEFAULT_JVM_OPTS \
    -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
