package com.judas.katachi.feature.api.model

import com.google.gson.annotations.SerializedName

data class RecentProGames(
    val games: List<ProGame> = listOf()
)

data class ProGame(
    @SerializedName("gid") val gameId: String = "",
    @SerializedName("sgf_header") val sgfHeader: String = ""
) {
    val enhancedHeader: String
        get() {
            var enhanced = sgfHeader
            if (!enhanced.contains("SZ[")) enhanced += "SZ[19]"
            if (!enhanced.contains("GM[")) enhanced += "GM[1]"
            return enhanced
        }
}

data class ProGameDetail(
    val sgf: String = ""
)
