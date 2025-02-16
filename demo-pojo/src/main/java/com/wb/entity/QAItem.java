package com.wb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ai问答
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QAItem {
    private long id;
    private long userId;
    private String question;
    private String answer;
    private int prompt_tokens;//输入token
    private int completion_tokens;//输出token
}
