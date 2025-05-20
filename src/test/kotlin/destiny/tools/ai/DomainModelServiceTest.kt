/**
 * Created by smallufo on 2024-08-19.
 */
package destiny.tools.ai

import destiny.tools.ai.model.Domain
import destiny.tools.config.Holder
import org.junit.jupiter.api.Nested
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
        ProviderModel(Provider.CLAUDE, "claude-3-haiku-20240307", Temperature(0.9))
      ),
      DomainLanguage(Domain.Bdnp.EW, null) to listOf(
        ProviderModel(Provider.OPENAI, "gpt-4", Temperature(0.8)),
        ProviderModel(Provider.GEMINI, "gemini-pro", Temperature(0.8))
      ),
      DomainLanguage(Domain.Bdnp.ZIWEI, "en") to listOf(
        ProviderModel(Provider.OPENAI, "gpt-4", Temperature(0.7)),
        ProviderModel(Provider.CLAUDE, "claude-3-haiku-20240307", Temperature(0.7))
      )
    )
    val holder = Holder(map)
    service = DomainModelService(holder, emptySet())
  }

  @Nested
  inner class Multiple {

    @Test
    fun `matching domain and language`() {
      service.getProviderModels(Domain.Bdnp.ZIWEI, "en").also { pms ->
        assertTrue { pms.map { pm -> pm.provider }.toSet() == setOf(Provider.OPENAI, Provider.CLAUDE) }
        assertTrue { pms.map { pm -> pm.model }.toSet() == setOf("gpt-4", "claude-3-haiku-20240307") }
      }
    }

    @Test
    fun `matching domain but no language`() {
      service.getProviderModels(Domain.Bdnp.ZIWEI, null).also { pms ->
        assertTrue { pms.map { pm -> pm.provider }.toSet() == setOf(Provider.OPENAI, Provider.CLAUDE) }
        assertTrue { pms.map { pm -> pm.model }.toSet() == setOf("gpt-4", "claude-3-haiku-20240307") }
      }

      service.getProviderModels(Domain.Bdnp.EW, null).also { pms ->
        assertTrue { pms.map { pm -> pm.provider }.toSet() == setOf(Provider.OPENAI, Provider.GEMINI) }
        assertTrue { pms.map { pm -> pm.model }.toSet() == setOf("gpt-4", "gemini-pro") }
      }
    }

    @Test
    fun `matching domain but different language`() {
      service.getProviderModels(Domain.Bdnp.ZIWEI, "fr").also { pms ->
        assertTrue { pms.map { pm -> pm.provider }.toSet() == setOf(Provider.OPENAI, Provider.CLAUDE) }
        assertTrue { pms.map { pm -> pm.model }.toSet() == setOf("gpt-4", "claude-3-haiku-20240307") }
      }
    }

    @Test
    fun otherDomain() {
      service.getProviderModels(Domain.TAROT).also { pms ->
        assertTrue { pms.map { pm -> pm.provider }.toSet() == setOf(Provider.OPENAI, Provider.CLAUDE, Provider.GEMINI) }
        assertTrue { pms.map { pm -> pm.model }.toSet() == setOf("gpt-4", "claude-3-haiku-20240307", "gemini-pro") }
      }
    }
  }


  @Nested
  inner class Single {
    @Test
    fun `matching domain and language`() {
      val result = service.getProviderModel(Domain.Bdnp.HOROSCOPE, "zh")
      assertEquals(Provider.CLAUDE, result.provider)
      assertEquals("claude-3-haiku-20240307", result.model)
      assertEquals(Temperature(0.9), result.temperature)
    }

    @Test
    fun `matching domain but no language`() {
      val result = service.getProviderModel(Domain.Bdnp.EW)
      assertTrue(result.provider in listOf(Provider.OPENAI, Provider.GEMINI))
      assertTrue(result.model in listOf("gpt-4", "gemini-pro"))
      assertEquals(Temperature(0.8), result.temperature)
    }

    @Test
    fun `matching domain but different language`() {
      val result = service.getProviderModel(Domain.Bdnp.ZIWEI, "fr")
      assertTrue(result.provider in listOf(Provider.OPENAI, Provider.CLAUDE))
      assertTrue(result.model in listOf("gpt-4", "claude-3-haiku-20240307"))
      assertEquals(Temperature(0.7), result.temperature)
    }

    @Test
    fun otherDomain() {
      val result = service.getProviderModel(Domain.TAROT)
      assertTrue(result.provider in listOf(Provider.OPENAI, Provider.CLAUDE, Provider.GEMINI))
      assertTrue(result.model in listOf("claude-3-haiku-20240307", "gpt-4", "gemini-pro"))
    }

  }

  @Test
  fun `all unique models`() {
    assertEquals(5, service.allProviderModels.size)
    assertTrue(service.allProviderModels.any { it.provider == Provider.CLAUDE })
    assertTrue(service.allProviderModels.any { it.provider == Provider.OPENAI })
    assertTrue(service.allProviderModels.any { it.provider == Provider.GEMINI })
  }
}
