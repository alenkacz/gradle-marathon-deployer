package cz.alenkacz.gradle.marathon.deploy

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.slf4j.Marker

class NoOpLogger implements Logger {
    @Override
    boolean isLifecycleEnabled() {
        return false
    }

    @Override
    String getName() {
        return null
    }

    @Override
    boolean isTraceEnabled() {
        return false
    }

    @Override
    void trace(String msg) {

    }

    @Override
    void trace(String format, Object arg) {

    }

    @Override
    void trace(String format, Object arg1, Object arg2) {

    }

    @Override
    void trace(String format, Object... arguments) {

    }

    @Override
    void trace(String msg, Throwable t) {

    }

    @Override
    boolean isTraceEnabled(Marker marker) {
        return false
    }

    @Override
    void trace(Marker marker, String msg) {

    }

    @Override
    void trace(Marker marker, String format, Object arg) {

    }

    @Override
    void trace(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    void trace(Marker marker, String format, Object... argArray) {

    }

    @Override
    void trace(Marker marker, String msg, Throwable t) {

    }

    @Override
    boolean isDebugEnabled() {
        return false
    }

    @Override
    void debug(String msg) {

    }

    @Override
    void debug(String format, Object arg) {

    }

    @Override
    void debug(String format, Object arg1, Object arg2) {

    }

    @Override
    void debug(String s, Object... objects) {

    }

    @Override
    void debug(String msg, Throwable t) {

    }

    @Override
    boolean isDebugEnabled(Marker marker) {
        return false
    }

    @Override
    void debug(Marker marker, String msg) {

    }

    @Override
    void debug(Marker marker, String format, Object arg) {

    }

    @Override
    void debug(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    void debug(Marker marker, String format, Object... arguments) {

    }

    @Override
    void debug(Marker marker, String msg, Throwable t) {

    }

    @Override
    boolean isInfoEnabled() {
        return false
    }

    @Override
    void info(String msg) {

    }

    @Override
    void info(String format, Object arg) {

    }

    @Override
    void info(String format, Object arg1, Object arg2) {

    }

    @Override
    void lifecycle(String s) {

    }

    @Override
    void lifecycle(String s, Object... objects) {

    }

    @Override
    void lifecycle(String s, Throwable throwable) {

    }

    @Override
    boolean isQuietEnabled() {
        return false
    }

    @Override
    void quiet(String s) {

    }

    @Override
    void quiet(String s, Object... objects) {

    }

    @Override
    void info(String s, Object... objects) {

    }

    @Override
    void info(String msg, Throwable t) {

    }

    @Override
    boolean isInfoEnabled(Marker marker) {
        return false
    }

    @Override
    void info(Marker marker, String msg) {

    }

    @Override
    void info(Marker marker, String format, Object arg) {

    }

    @Override
    void info(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    void info(Marker marker, String format, Object... arguments) {

    }

    @Override
    void info(Marker marker, String msg, Throwable t) {

    }

    @Override
    boolean isWarnEnabled() {
        return false
    }

    @Override
    void warn(String msg) {

    }

    @Override
    void warn(String format, Object arg) {

    }

    @Override
    void warn(String format, Object... arguments) {

    }

    @Override
    void warn(String format, Object arg1, Object arg2) {

    }

    @Override
    void warn(String msg, Throwable t) {

    }

    @Override
    boolean isWarnEnabled(Marker marker) {
        return false
    }

    @Override
    void warn(Marker marker, String msg) {

    }

    @Override
    void warn(Marker marker, String format, Object arg) {

    }

    @Override
    void warn(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    void warn(Marker marker, String format, Object... arguments) {

    }

    @Override
    void warn(Marker marker, String msg, Throwable t) {

    }

    @Override
    boolean isErrorEnabled() {
        return false
    }

    @Override
    void error(String msg) {

    }

    @Override
    void error(String format, Object arg) {

    }

    @Override
    void error(String format, Object arg1, Object arg2) {

    }

    @Override
    void error(String format, Object... arguments) {

    }

    @Override
    void error(String msg, Throwable t) {

    }

    @Override
    boolean isErrorEnabled(Marker marker) {
        return false
    }

    @Override
    void error(Marker marker, String msg) {

    }

    @Override
    void error(Marker marker, String format, Object arg) {

    }

    @Override
    void error(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    void error(Marker marker, String format, Object... arguments) {

    }

    @Override
    void error(Marker marker, String msg, Throwable t) {

    }

    @Override
    void quiet(String s, Throwable throwable) {

    }

    @Override
    boolean isEnabled(LogLevel logLevel) {
        return false
    }

    @Override
    void log(LogLevel logLevel, String s) {

    }

    @Override
    void log(LogLevel logLevel, String s, Object... objects) {

    }

    @Override
    void log(LogLevel logLevel, String s, Throwable throwable) {

    }
}
