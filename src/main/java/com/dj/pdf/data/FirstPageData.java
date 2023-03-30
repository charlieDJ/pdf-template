package com.dj.pdf.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FirstPageData extends Basic {

    private String depart;
    private String processId;
    private String secretLevel;
    private String version;
    private String imageVersion;
    private String processName;
    private String remark;
    private String year;
    private String month;
    private String day;


}
