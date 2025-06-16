package com.wb.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 与 AiQADTO的不同的地方在于 要应用的http请求的请求体不同（主要是prompt和suffix的变化)
 */
@Data //提供getter和setter函数
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "（FIM补全）用户询问AI相关的传递的数据模型")
public class AiQAByFimDTO implements Serializable {
    private String justTest;
    @ApiModelProperty("选择的模型")
    private String model;//Possible values: [deepseek-chat, deepseek-reasoner]
    @ApiModelProperty("询问用的提示词")
    private String prompt;
    @ApiModelProperty("在输出中是否把prompt的内容也输出出来")
    private boolean echo=true;
    @ApiModelProperty("惩罚频率，一般为0")
    private int frequency_penalty;
    @ApiModelProperty("最大token数（输入token+输出token）")
    private int max_tokens;
    private int presence_penalty;

    @ApiModelProperty("是否选择流运输")
    private boolean stream;
    @ApiModelProperty("FIM补全中的后缀（可选）")
    private String suffix="";

    private double temperature;
    private double top_p;

    public String toJson() {
        // Convert this object to JSON string using your preferred library
        // You can use Jackson, Gson etc.
        // Example using Jackson:
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
