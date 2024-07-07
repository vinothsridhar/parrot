package ai.sridhar.faq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@SpringBootApplication
@EnableAsync
class FaqApplication

fun main(args: Array<String>) {
    runApplication<FaqApplication>(*args)
}

@Controller
class AppController {

    @RequestMapping(value = ["/app/**"])
    fun appIndex() : String {
        return "forward:/index.html"
    }

    @RequestMapping("/")
    fun redirect() = "redirect:/app"

}