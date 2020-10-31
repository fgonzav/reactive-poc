package com.globant.reactivepoc.controller;

import com.globant.reactivepoc.domain.Thing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThingResponse implements Serializable {

    private Thing thing;
    private String message;

}
