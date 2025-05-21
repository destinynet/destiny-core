package destiny.tools.ai

import destiny.tools.KotlinLogging
import destiny.tools.ai.model.FormatSpec
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class Reply {

  abstract val provider: Provider

  data class Normal(val content: String,
                    val think: String?,
                    override val provider: Provider,
                    val model: String,
                    val invokedFunCalls: List<FunCall> = emptyList(),
                    val inputTokens: Int? = null,
                    val outputTokens: Int? = null,
                    val duration: Duration? = null,
  ) : Reply()

  sealed class Error : Reply() {

    data class TooLong(val message: String, override val provider: Provider) : Error()

    sealed class Unrecoverable : Error() {
      data class InvalidApiKey(override val provider: Provider) : Unrecoverable()
      data class Busy(override val provider: Provider) : Unrecoverable()
      data class Unknown(val message: String, override val provider: Provider) : Unrecoverable()
    }
  }
}


interface IChatCompletion {

  val provider: Provider

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCalls: Set<IFunctionDeclaration> = emptySet(), timeout: Duration = 90.seconds, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec? = null) : Reply

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCall: IFunctionDeclaration, timeout: Duration = 90.seconds, chatOptions: ChatOptions) : Reply {
    return chatComplete(model, messages, user, setOf(funCall), timeout, chatOptions)
  }

  suspend fun <T : Any> typedChatComplete(model: String, messages: List<Msg>, user: String? = null, funCalls: Set<IFunctionDeclaration> = emptySet(), timeout: Duration = 90.seconds, chatOptions: ChatOptions, postProcessors : List<IPostProcessor>, formatSpec: FormatSpec<T>, locale: Locale, json: Json) : ResultDto<T>?
}

abstract class AbstractChatCompletion : IChatCompletion {

  abstract suspend fun doChatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec? = null): Reply

  override suspend fun chatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec?): Reply {
    val filteredFunCalls = funCalls.filter { it.applied(messages) }.toSet()

    val finalMsgs = messages.fold(mutableListOf<Msg>()) { acc, msg ->
      if (acc.isNotEmpty()) {
        val lastMsg = acc.last()
        if (lastMsg.role == msg.role) {
          if (lastMsg.stringContents == msg.stringContents) {
            // Drop the duplicate message
            logger.warn { "DROP_DUPLICATED  : ${msg.stringContents}" }
          } else {
            // Append the content
            acc[acc.size - 1] = lastMsg.copy(contents = buildList {
              addAll(lastMsg.contents)
              addAll(msg.contents)
            })
          }
        } else {
          acc.add(msg)
        }
      } else {
        acc.add(msg)
      }
      acc
    }

    val funCallPrompts = (filteredFunCalls.joinToString(",") { it.name }).let {
      if (it.isEmpty()) it
      else buildString {
        // TODO : add meta data , to enhance LLM memory
        append("With function calls if applicable : ")
        append(it)
      }
    }

    if (filteredFunCalls.isNotEmpty() && finalMsgs.isNotEmpty()) {
      val lastMsg = finalMsgs.last()
      finalMsgs[finalMsgs.lastIndex] = lastMsg.copy(
        contents = buildList {
          addAll(lastMsg.contents)
          add(Content.StringContent("\n$funCallPrompts"))
        }
      )
    }

    return doChatComplete(model, finalMsgs, user, filteredFunCalls, timeout, chatOptions, jsonSchema)
  }

  override suspend fun <T : Any> typedChatComplete(
    model: String,
    messages: List<Msg>,
    user: String?,
    funCalls: Set<IFunctionDeclaration>,
    timeout: Duration,
    chatOptions: ChatOptions,
    postProcessors: List<IPostProcessor>,
    formatSpec: FormatSpec<T>,
    locale: Locale,
    json: Json
  ): ResultDto<T>? {
    return when (val reply = chatComplete(model, messages, user, funCalls, timeout, chatOptions, formatSpec.jsonSchema)) {
      is Reply.Normal -> {

        val processedString = postProcessors.fold(reply.content) { currentContent, postProcessor ->
          val (nextContent, _) = postProcessor.process(currentContent, locale)
          nextContent
        }
        val serializer = formatSpec.serializer

        if (serializer.descriptor.kind == PrimitiveKind.STRING && serializer == String.serializer()) {
          @Suppress("UNCHECKED_CAST")
          processedString as T
        } else {
          try {
            json.decodeFromString(serializer, processedString)
          } catch (e: SerializationException) {
            logger.warn(e) { "Failed to deserialize content from $model (serializer: ${serializer.descriptor.serialName}). Content: $processedString" }
            null
          }
        }?.let { t ->
          ResultDto(t, reply.think, reply.provider, reply.model, reply.invokedFunCalls, reply.inputTokens, reply.outputTokens, reply.duration)
        }
      }
      else -> null
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
