package shareMe.ShareMe.Health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class health {

//    @GetMapping("/")
    public String home() {
        return "working properly";
    }
}
