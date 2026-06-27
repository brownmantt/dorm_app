/**
 * @param {string | null | undefined} contactInfo
 * @returns {{ mobilePhone: string, email: string }}
 */
export function parseContactInfo(contactInfo) {
  if (!contactInfo) return { mobilePhone: '', email: '' }
  try {
    const parsed = JSON.parse(contactInfo)
    return {
      mobilePhone: parsed.mobilePhone || '',
      email: parsed.email || ''
    }
  } catch {
    return { mobilePhone: '', email: '' }
  }
}
