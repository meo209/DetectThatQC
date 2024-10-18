package com.meo209.detectthatqc

import dev.isxander.yacl3.config.v2.api.SerialEntry
import java.awt.Color

class Config {

    companion object {
        @SerialEntry
        var highlightBlockColor: Color = Color(250, 0, 250, 100)

        @SerialEntry
        var scanRange: Int = 10
    }

}