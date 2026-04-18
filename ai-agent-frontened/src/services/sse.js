import { buildApiUrl } from './http'

const DONE_SIGNALS = new Set(['[DONE]', 'DONE', 'done', '__DONE__'])

function normalizeChunk(rawData) {
  if (!rawData) {
    return ''
  }

  const data = rawData.trimEnd()

  try {
    const parsed = JSON.parse(data)
    return (
      parsed.content ??
      parsed.text ??
      parsed.message ??
      parsed.result ??
      parsed.data ??
      data
    )
  } catch {
    return data
  }
}

export function createSseConnection({
  path,
  params,
  onOpen,
  onChunk,
  onDone,
  onError,
}) {
  const url = buildApiUrl(path, params)
  const source = new EventSource(url)
  let closed = false
  let receivedAnyChunk = false

  function close() {
    if (closed) {
      return
    }

    closed = true
    source.close()
  }

  source.onopen = () => {
    onOpen?.()
  }

  source.onmessage = (event) => {
    const chunk = normalizeChunk(event.data)

    if (DONE_SIGNALS.has(chunk)) {
      close()
      onDone?.()
      return
    }

    if (chunk) {
      receivedAnyChunk = true
      onChunk?.(chunk)
    }
  }

  source.addEventListener('done', () => {
    close()
    onDone?.()
  })

  source.addEventListener('end', () => {
    close()
    onDone?.()
  })

  source.onerror = (event) => {
    close()

    if (receivedAnyChunk) {
      onDone?.()
      return
    }

    onError?.(event)
  }

  return {
    url,
    close,
  }
}
