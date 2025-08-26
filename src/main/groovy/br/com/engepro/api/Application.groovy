package br.com.engepro.api

import io.github.cdimascio.dotenv.Dotenv
import jakarta.annotation.PostConstruct
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application {

    static void main(String[] args) {
        Dotenv.configure()
                .ignoreIfMissing()
                .systemProperties()
                .load()

        SpringApplication.run(Application.class, args)
    }

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
}
