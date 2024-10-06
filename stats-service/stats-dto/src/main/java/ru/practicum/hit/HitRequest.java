package ru.practicum.hit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HitRequest {

    @NotEmpty(message = "app is not be empty")
    private String app;

    @NotEmpty(message = "uri is not be empty")
    private String uri;

    @NotEmpty(message = "ip is not be empty")
    private String ip;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;

}
