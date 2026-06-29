package tech.meliora.mulika.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MulikaHTTPResponse {
    private int responseCode;
    private String body;
}
 