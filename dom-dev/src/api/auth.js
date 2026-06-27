import request from '@/utils/request'

/** @param {{ username: string, password: string }} data */
export function login(data) {
  return request.post('/auth/login', data)
}
