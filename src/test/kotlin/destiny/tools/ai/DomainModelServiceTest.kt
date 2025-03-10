/**
 * Created by smallufo on 2024-08-19.
 */
package destiny.tools.ai

import destiny.tools.ai.model.Domain
import destiny.tools.config.Holder
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DomainModelServiceTest {

  private lateinit var service: DomainModelService

  @BeforeTest
  fun setup() {
    val map = mapOf(
      DomainLanguage(Domain.Bdnp.HOROSCOPE, "zh") to listOf(
        ProviderModel(Provider.CLAUDE, "claude-3-haiku-20240307", 0.9)
      ),
      DomainLanguage(Domain.Bdnp.EW, null) to listOf(
        ProviderModel(Provider.OPENAI, "gpt-4", 0.8),
        ProviderModel(Provider.GEMINI, "gemini-pro", 0.8)
      ),
      DomainLanguage(Domain.Bdnp.ZIWEI, "en") to listOf(
        ProviderModel(Provider.OPENAI, "gpt-4", 0.7),
        ProviderModel(Provider.CLAUDE, "claude-3-haiku-20240307", 0.7)
      )
    )
    val holder = Holder(map)
    service = DomainModelService(holder, emptySet())
  }

  @Test
  fun `matching domain and language`() {
    val result = service.getProviderModel(Domain.Bdnp.HOROSCOPE, "zh")
    assertEquals(Provider.CLAUDE, result.provider)
    assertEquals("claude-3-haiku-20240307", result.model)
    assertEquals(0.9, result.temperature)
  }

  @Test
  fun `matching domain but no language`() {
    val result = service.getProviderModel(Domain.Bdnp.EW)
    assertTrue(result.provider in listOf(Provider.OPENAI, Provider.GEMINI))
    assertTrue(result.model in listOf("gpt-4", "gemini-pro"))
    assertEquals(0.8, result.temperature)
  }


  @Test
  fun `matching domain but different language`() {
    val result = service.getProviderModel(Domain.Bdnp.ZIWEI, "fr")
    assertTrue(result.provider in listOf(Provider.OPENAI, Provider.CLAUDE))
    assertTrue(result.model in listOf("gpt-4", "claude-3-haiku-20240307"))
    assertEquals(0.7, result.temperature)
  }

  @Test
  fun otherDomain() {
    val result = service.getProviderModel(Domain.TAROT)
    assertTrue(result.provider in listOf(Provider.OPENAI, Provider.CLAUDE, Provider.GEMINI))
    assertTrue(result.model in listOf("claude-3-haiku-20240307", "gpt-4", "gemini-pro"))
  }

  @Test
  fun `all unique models`() {
    assertEquals(5, service.allProviderModels.size)
    assertTrue(service.allProviderModels.any { it.provider == Provider.CLAUDE })
    assertTrue(service.allProviderModels.any { it.provider == Provider.OPENAI })
    assertTrue(service.allProviderModels.any { it.provider == Provider.GEMINI })
  }
}
