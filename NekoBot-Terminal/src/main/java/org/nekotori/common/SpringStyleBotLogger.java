package org.nekotori.common;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.utils.MiraiLoggerPlatformBase;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class SpringStyleBotLogger extends MiraiLoggerPlatformBase {

    @Override
    protected void debug0(@Nullable String s, @Nullable Throwable throwable) {
        log.debug(s, throwable);
    }

    @Override
    protected void error0(@Nullable String s, @Nullable Throwable throwable) {
        log.error(s, throwable);
    }

    @Override
    protected void info0(@Nullable String s, @Nullable Throwable throwable) {
        log.info(s, throwable);
    }

    @Override
    protected void verbose0(@Nullable String s, @Nullable Throwable throwable) {
        log.info(s, throwable);
    }

    @Override
    protected void warning0(@Nullable String s, @Nullable Throwable throwable) {
        log.warn(s, throwable);
    }

    @Override
    protected void verbose0(@Nullable String message) {log.info(message);}

    @Override
    protected void debug0(@Nullable String message) {log.debug(message);}

    @Override
    protected void error0(@Nullable String message) {log.error(message);}

    @Override
    protected void info0(@Nullable String message) {log.info(message);}

    @Override
    protected void warning0(@Nullable String message) {log.warn(message);}



    @Nullable
    @Override
    public String getIdentity() {
        return "NekoBot";
    }


}