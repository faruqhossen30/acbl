package com.shopnolive.shopnolive.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportModel {
    @SerializedName("reported_user")
    @Expose
    private String reportedId;
    @SerializedName("reporter")
    @Expose
    private String reporterId;
}
