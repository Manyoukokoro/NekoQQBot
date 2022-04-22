package org.nekotori.common;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.utils.MiraiLoggerPlatformBase;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Slf4j
public class SpringStyleBotLogger extends MiraiLoggerPlatformBase {

    @Override
    protected void debug0(@Nullable String s, @Nullable Throwable throwable) {
        log.debug(s, Optional.ofNullable(throwable).map(Throwable::getMessage).orElse(null));
    }

    @Override
    protected void error0(@Nullable String s, @Nullable Throwable throwable) {
        log.error(s, Optional.ofNullable(throwable).map(Throwable::getMessage).orElse(null));
    }

    @Override
    protected void info0(@Nullable String s, @Nullable Throwable throwable) {
        log.info(s, Optional.ofNullable(throwable).map(Throwable::getMessage).orElse(null));
    }

    @Override
    protected void verbose0(@Nullable String s, @Nullable Throwable throwable) {
        log.info(s, Optional.ofNullable(throwable).map(Throwable::getMessage).orElse(null));
    }

    @Override
    protected void warning0(@Nullable String s, @Nullable Throwable throwable) {
        log.warn(s, Optional.ofNullable(throwable).map(Throwable::getMessage).orElse(null));
    }

    @Override
    protected void verbose0(@Nullable String message) {
        log.info(message);
    }

    @Override
    protected void debug0(@Nullable String message) {
        log.debug(message);
    }

    @Override
    protected void error0(@Nullable String message) {
        log.error(message);
    }

    @Override
    protected void info0(@Nullable String message) {
        log.info(message);
    }

    @Override
    protected void warning0(@Nullable String message) {
        log.warn(message);
    }


    @Nullable
    @Override
    public String getIdentity() {
        return "NekoBot";
    }


}