package br.com.engepro.api.config

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.stereotype.Component

@CompileStatic
@Component
class EngeproProperties {

    @Memoized
    static Dotenv getDotenv() {
        Dotenv.configure().ignoreIfMissing().load()
    }
}
