package com.github.fernthedev.lightchat.core.codecs;

import com.github.fernthedev.lightchat.core.codecs.json.FastJSONHandler;
import com.github.fernthedev.lightchat.core.codecs.json.GSONHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Shortcut for json handlers
 */
@RequiredArgsConstructor
@Getter
public enum CodecEnum {

    GSON(new GSONHandler()),
    ALIBABA_FASTJSON(new FastJSONHandler());

    private final JSONHandler jsonHandler;

}