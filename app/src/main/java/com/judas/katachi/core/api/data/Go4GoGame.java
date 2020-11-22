package com.judas.katachi.core.api.data;

import com.google.gson.annotations.SerializedName;

public class Go4GoGame {
    @SerializedName("gid")
    public String id;

    @SerializedName("sgf_header")
    public String sgfHeader;
}
