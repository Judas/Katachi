package com.judas.katachi.core.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GameListResponse {
    @SerializedName("games")
    public List<Go4GoGame> games = new ArrayList<>();
}
