package destiny.tools.openAi


interface IOpenAi {

  suspend fun complete(prompt: String, user: String? = null): OpenAiReply
}
