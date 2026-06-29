package tech.meliora.mulika.support;

import org.springframework.stereotype.Service;
import tech.meliora.mulika.annotations.Monitor;

@Service
public class TestService {
    // success
    @Monitor(service = "test")
    public String success(){
        return "OK";
    }

    // failure
    @Monitor(service = "test")
    public void failure(){
        throw new RuntimeException("failure");
    }
}
