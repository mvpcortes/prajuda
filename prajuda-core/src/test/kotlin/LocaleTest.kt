import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocaleTest{

    val locale = Locale.getDefault()

    @Test
    fun `verify locale is count us`(){
        System.getenv().forEach{println(it)}
        assertThat(locale.country).isEqualTo("US")
    }

    @Test
    fun `verify locale is language is en`(){
        assertThat(locale.language).isEqualTo("en")
    }
}