package com.mmall.beans;

import lombok.*;

import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Mail {

    private String subject;

    private String message;

    private Set<String> receivers;
}
